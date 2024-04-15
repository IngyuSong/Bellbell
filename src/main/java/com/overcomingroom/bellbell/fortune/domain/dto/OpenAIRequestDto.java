package com.overcomingroom.bellbell.fortune.domain.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class OpenAIRequestDto {
  private String model;
  private List<MessageDto> messages;
  private Double temperature;

  public OpenAIRequestDto(String model, String prompt, String temperature) {
    this.model = model;
    this.messages =  new ArrayList<>();
    this.messages.add(new MessageDto("user", prompt));
    this.temperature = Double.valueOf(temperature);
  }
}
