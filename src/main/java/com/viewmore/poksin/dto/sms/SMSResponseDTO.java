package com.viewmore.poksin.dto.sms;

import lombok.*;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SMSResponseDTO {
    private String groupId;
    private String to;
    private String from;
    private String type;
    private String statusMessage;
    private String country;
    private String messageId;
    private String statusCode;
    private String accountId;

    public static SMSResponseDTO toDTO(SingleMessageSentResponse response) {
        return SMSResponseDTO.builder()
                .groupId(response.getGroupId())
                .to(response.getTo())
                .from(response.getFrom())
                .type(response.getType().name())
                .statusCode(response.getStatusCode())
                .country(response.getCountry())
                .messageId(response.getMessageId())
                .statusCode(response.getStatusCode())
                .accountId(response.getAccountId())
                .build();
    }
}
