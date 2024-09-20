package com.viewmore.poksin.service;

import com.viewmore.poksin.dto.user.CounselorRegisterDTO;
import com.viewmore.poksin.dto.user.CounselorResponseDTO;
import com.viewmore.poksin.entity.ChatMessageEntity;
import com.viewmore.poksin.entity.ChatRoomCountEntity;
import com.viewmore.poksin.repository.*;
import com.viewmore.poksin.entity.CounselorEntity;
import com.viewmore.poksin.exception.DuplicateUsernameException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CounselorService {

    private final UserRepository userRepository;
    private final CounselorRepository counselorRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomCountRepository chatRoomCountRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Transactional
    public void registerCounselor(CounselorRegisterDTO counselorRegisterDTO) {
        String username = counselorRegisterDTO.getUsername();
        String password = counselorRegisterDTO.getPassword();

        // 상담사, 일반 유저 아이디 중복 검사
        Boolean isExist = counselorRepository.existsByUsername(username);

        if (isExist) {
            throw new DuplicateUsernameException("중복된 아이디가 존재합니다.");
        }

        isExist = userRepository.existsByUsername(username);

        if (isExist) {
            throw new DuplicateUsernameException("중복된 아이디가 존재합니다.");
        }

        ChatRoomCountEntity chatRoomCount = new ChatRoomCountEntity();
        chatRoomCount.setTotalCount(0); // 초기 상담 횟수

        CounselorEntity counselor = CounselorEntity.counselorEntityBuilder()
                .username(username)
                .password(bCryptPasswordEncoder.encode(password))
                .phoneNum(counselorRegisterDTO.getPhoneNum())
                .specialty(counselorRegisterDTO.getSpecialty())
                .chatRoomCount(chatRoomCount)
                .start(null)
                .career(counselorRegisterDTO.getCareer())
                .role("ROLE_ADMIN")
                .build();

        chatRoomCount = chatRoomCountRepository.save(chatRoomCount);
        counselorRepository.save(counselor);
    }

    public CounselorResponseDTO counselorMypage(String username) {
        CounselorEntity user = counselorRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("해당 사용자 이름을 가진 사용자를 찾을 수 없습니다: " + username));

        return CounselorResponseDTO.toDto(user);
    }
}
