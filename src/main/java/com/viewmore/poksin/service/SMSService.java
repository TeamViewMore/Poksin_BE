package com.viewmore.poksin.service;

import com.viewmore.poksin.dto.sms.SMSResponseDTO;
import com.viewmore.poksin.entity.UserEntity;
import com.viewmore.poksin.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.service.DefaultMessageService;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;

@Service
@RequiredArgsConstructor
public class SMSService {
    @Value("${coolsms.apiKey}")
    private String apiKey;

    @Value("${coolsms.secretKey}")
    private String secretKey;

    @Value("${coolsms.phoneNum}")
    private String phoneNum;

    private DefaultMessageService messageService;

    private final UserRepository userRepository;

    // DI 후 값 초기화
    @PostConstruct
    public void init() {
        messageService = NurigoApp.INSTANCE.initialize(apiKey, secretKey, "https://api.coolsms.co.kr");
    }

    public SMSResponseDTO sendLocation(String location, String username){
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("해당 사용자 이름을 가진 사용자를 찾을 수 없습니다: " + username));

        String messageText = String.format("[POKSIN] %s이 보낸 긴급 메세지입니다. %s님의 현재 위치는 %s입니다.", username, username, location);

        Message message = new Message();
        message.setFrom(phoneNum);
        message.setTo(user.getEmergencyNum()); // 비상 번호로 전송
        message.setText(messageText);

        SingleMessageSentResponse response = messageService.sendOne(new SingleMessageSendingRequest(message));
        return SMSResponseDTO.toDTO(response);
    }
}
