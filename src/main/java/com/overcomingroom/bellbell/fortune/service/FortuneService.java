package com.overcomingroom.bellbell.fortune.service;

import com.overcomingroom.bellbell.basicNotification.domain.entity.BasicNotification;
import com.overcomingroom.bellbell.basicNotification.service.BasicNotificationService;
import com.overcomingroom.bellbell.exception.CustomException;
import com.overcomingroom.bellbell.exception.ErrorCode;
import com.overcomingroom.bellbell.fortune.domain.dto.FortuneDto;
import com.overcomingroom.bellbell.fortune.domain.dto.OpenAIRequestDto;
import com.overcomingroom.bellbell.fortune.domain.dto.OpenAIResponseDto;
import com.overcomingroom.bellbell.fortune.domain.entity.Fortune;
import com.overcomingroom.bellbell.fortune.repository.FortuneRepository;
import com.overcomingroom.bellbell.kakaoMessage.service.CustomMessageService;
import com.overcomingroom.bellbell.member.domain.entity.Member;
import com.overcomingroom.bellbell.member.domain.service.MemberService;
import com.overcomingroom.bellbell.schedule.CronExpression;
import com.overcomingroom.bellbell.schedule.ScheduleType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
@RequiredArgsConstructor
public class FortuneService {

  @Value("${openai.api-key}")
  private String apiKey;
  @Value("${openai.service-uri}")
  private String serviceUri;
  @Value("${openai.model}")
  private String model;
  @Value("${openai.temperature}")
  private String temperature;


  private final MemberService memberService;
  private final FortuneRepository fortuneRepository;
  private final BasicNotificationService basicNotificationService;
  private final TaskScheduler taskScheduler;
  private final CustomMessageService customMessageService;
  private final Map<String, ScheduledFuture<?>> scheduledFutureMap = new ConcurrentHashMap<>();

  /**
   * fortune service 를 활성 / 비활성 합니다.
   *
   * @param accessToken
   * @param fortuneDto
   */
  public void activeFortune(String accessToken, FortuneDto fortuneDto) {

    Member member = memberService.getMember(accessToken);

    // 알림 정보 생성
    Fortune fortune = fortuneRepository.findByMember(member).orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND_IN_FORTUNE));

    BasicNotification basicNotification = basicNotificationService.activeNotification(fortune.getBasicNotification().getId(), fortuneDto);
    fortune.setBasicNotification(basicNotification);
    fortune.setMember(member);
    fortune.setGender(fortuneDto.getGender());
    fortune.setBirthdate(fortuneDto.getBirthdate());
    fortune.setCalendarType(fortuneDto.getCalendarType());
    fortune.setBirthTime(fortuneDto.getBirthTime());
    fortuneRepository.save(fortune);

    String scheduleId = ScheduleType.FORTUNE.toString() + fortune.getId();
    ScheduledFuture<?> scheduledFuture = scheduledFutureMap.get(scheduleId);

    // 알림 스케줄 생성
    if (scheduledFuture != null) {
      scheduledFuture.cancel(true);
      log.info("기존 기본 알림 스케줄을 취소 합니다.");
      scheduledFutureMap.remove(scheduleId, scheduledFuture);
    } else {
      String cronExpression = CronExpression.getCronExpression(basicNotification.getDay(), basicNotification.getTime());
      log.info(" {} 상태로 스케줄이 활성화 되었습니다.", basicNotification.getIsActivated());
      scheduledFuture = taskScheduler.schedule(() -> {
            customMessageService.sendMessage(accessToken, getFortuneContent(fortuneDto));
            log.info("오늘의 운세 스케줄 실행 완료.");
          }
          , new CronTrigger(cronExpression, TimeZone.getTimeZone("Asia/Seoul")));
      scheduledFutureMap.put(scheduleId, scheduledFuture);
    }

    if (scheduledFutureMap.entrySet().isEmpty()) {
      log.info("스케줄 목록이 비어있습니다.");
    } else {
      log.info("Schedule List");
      for (Map.Entry<String, ScheduledFuture<?>> entry : scheduledFutureMap.entrySet()) {
        log.info("Schedule ID: " + entry.getKey());
      }
    }
  }

  /**
   * member 정보로 fortune 를 찾습니다.
   *
   * @param member 멤버 정보
   * @return Optional<fortune>
   */
  public Optional<Fortune> getfortuneByMember(Member member) {
    return fortuneRepository.findByMember(member);
  }

  /**
   * member의 초기 fortune 정보를 설정합니다.
   *
   * @param member 멤버 정보
   */
  public void setFortune(Member member) {
    fortuneRepository.save(
        Fortune.builder()
            .member(member)
            .basicNotification(basicNotificationService.setNotification())
            .build());
  }

  public FortuneDto getFortuneInfo(String accessToken) {
    Fortune fortune = getfortuneByMember(memberService.getMember(accessToken)).orElseThrow(() -> new CustomException(ErrorCode.NOT_EXISTS_FORTUNE_INFO));
    BasicNotification basicNotification = basicNotificationService.getNotification(fortune.getBasicNotification().getId()).orElseThrow(() -> new CustomException(ErrorCode.BASIC_NOTIFICATION_IS_EMPTY));

    return FortuneDto.builder()
        .isActivated(basicNotification.getIsActivated())
        .day(basicNotification.getDay())
        .time(basicNotification.getTime())
        .birthdate(fortune.getBirthdate())
        .birthTime(fortune.getBirthTime())
        .calendarType(fortune.getCalendarType())
        .gender(fortune.getGender())
        .build();
  }

  public String getFortuneContent(FortuneDto fortuneDto) {

    OpenAIRequestDto openAIRequestDto = new OpenAIRequestDto(model, fortuneDto.toString(), temperature);
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setBearerAuth(apiKey);
    RequestEntity<?> requestEntity = RequestEntity.post(serviceUri).headers(headers).body(openAIRequestDto);

    RestTemplate restTemplate = new RestTemplate();
    ResponseEntity<OpenAIResponseDto> responseEntity = restTemplate.exchange(requestEntity, OpenAIResponseDto.class);

    return responseEntity.getBody().getChoices().get(0).getMessage().getContent();
  }

}
