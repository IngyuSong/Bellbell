package com.overcomingroom.bellbell.fortune.domain.dto;

import com.overcomingroom.bellbell.basicNotification.domain.dto.AbstractBasicNotificationDto;
import lombok.Builder;
import lombok.Getter;

@Getter
public class FortuneDto extends AbstractBasicNotificationDto {
  private final String gender;
  private final String birthdate;
  private final String birthTime;
  private final String calendarType;

  @Builder
  public FortuneDto(Boolean isActivated, String day, String time, String gender, String birthdate, String birthTime, String calendarType) {
    super(isActivated, day, time);
    this.gender = gender;
    this.birthdate = birthdate;
    this.birthTime = birthTime;
    this.calendarType = calendarType;
  }

  @Override
  public String toString() {
    return "아래의 정보로 오늘의 운세 알려줘\n" +
        "성별: " + gender + "\n" +
        "생년월일: " + calendarType + " " + birthdate + "\n" +
        "태어난 시: " + birthTime + "\n";
  }
}
