package com.overcomingroom.bellbell.fortune.controller;

import com.overcomingroom.bellbell.exception.CustomException;
import com.overcomingroom.bellbell.exception.ErrorCode;
import com.overcomingroom.bellbell.fortune.domain.dto.FortuneDto;
import com.overcomingroom.bellbell.fortune.service.FortuneService;
import com.overcomingroom.bellbell.interceptor.AuthorizationInterceptor;
import com.overcomingroom.bellbell.response.ResResult;
import com.overcomingroom.bellbell.response.ResponseCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/fortune")
public class FortuneController {

  private final FortuneService fortuneService;

  @PostMapping
  public ResponseEntity<ResResult> activateFortune(@RequestBody FortuneDto fortuneDto) {

    String accessToken = AuthorizationInterceptor.getAccessToken();

    // 토큰이 없는 경우 예외 처리
    if (accessToken == null) {
      throw new CustomException(ErrorCode.JWT_VALUE_IS_EMPTY);
    }

    fortuneService.activeFortune(accessToken.substring(7), fortuneDto);

    ResponseCode responseCode = ResponseCode.FORTUNE_ACTIVATE_SUCCESSFUL;

    return ResponseEntity.ok(
        ResResult.builder()
            .responseCode(responseCode)
            .code(responseCode.getCode())
            .message(responseCode.getMessage())
            .build());
  }

  @GetMapping
  public ResponseEntity<ResResult> fortuneInfo() {
    String accessToken = AuthorizationInterceptor.getAccessToken();
    // 토큰이 없는 경우 예외 처리
    if (accessToken == null) {
      throw new CustomException(ErrorCode.JWT_VALUE_IS_EMPTY);
    }

    ResponseCode responseCode = ResponseCode.FORTUNE_INFO_GET_SUCCESSFUL;

    return ResponseEntity.ok(
        ResResult.builder()
            .responseCode(responseCode)
            .code(responseCode.getCode())
            .message(responseCode.getMessage())
            .data(fortuneService.getFortuneInfo(accessToken.substring(7)))
            .build());
  }
}

