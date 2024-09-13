package com.viewmore.poksin.controller;

import com.viewmore.poksin.code.SuccessCode;
import com.viewmore.poksin.dto.response.ResponseDTO;
import com.viewmore.poksin.dto.sms.SMSResponseDTO;
import com.viewmore.poksin.dto.sms.SendSMSDTO;
import com.viewmore.poksin.dto.user.RegisterDTO;
import com.viewmore.poksin.service.SMSService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/send-sms")
@RequiredArgsConstructor
public class SMSController {
    private final SMSService smsService;

    @PostMapping
    public ResponseEntity<ResponseDTO> sendLocation(@RequestBody SendSMSDTO sendSMSDTO) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        SMSResponseDTO response = smsService.sendLocation(sendSMSDTO.getLocation(), username);
        return ResponseEntity
                .status(SuccessCode.SUCCESS_SEND_MESSAGE.getStatus().value())
                .body(new ResponseDTO<>(SuccessCode.SUCCESS_SEND_MESSAGE, response));
    }

}
