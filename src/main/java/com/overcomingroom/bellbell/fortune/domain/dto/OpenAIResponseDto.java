package com.overcomingroom.bellbell.fortune.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OpenAIResponseDto {

  private List<Choice> choices;


  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Choice {
    private int index;
    private MessageDto message;

  }
}
