package com.viewmore.poksin.service;

import com.viewmore.poksin.dto.user.CounselorRegisterDTO;
import com.viewmore.poksin.dto.user.CounselorResponseDTO;
import com.viewmore.poksin.entity.ChatMessageEntity;
import com.viewmore.poksin.repository.ChatMessageRepository;
import com.viewmore.poksin.entity.CounselorEntity;
import com.viewmore.poksin.exception.DuplicateUsernameException;
import com.viewmore.poksin.repository.CounselorRepository;
import com.viewmore.poksin.repository.UserRepository;
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

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final CounselorRepository counselorRepository;

    @Autowired
    private final ChatMessageRepository chatMessageRepository;

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

        CounselorEntity user = CounselorEntity.counselorEntityBuilder()
                .username(username)
                .password(bCryptPasswordEncoder.encode(password))
                .phoneNum(counselorRegisterDTO.getPhoneNum())
                .specialty(counselorRegisterDTO.getSpecialty())
                .count(0)
                .career(counselorRegisterDTO.getCareer())
                .role("ROLE_ADMIN")
                .build();

        counselorRepository.save(user);
    }

    public CounselorResponseDTO counselorMypage(String username) {
        CounselorEntity user = counselorRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("해당 사용자 이름을 가진 사용자를 찾을 수 없습니다: " + username));

        // 채팅 메시지 조회
        List<ChatMessageEntity> messages = chatMessageRepository.findBySender(username);

        int count = messages.size();
        LocalDateTime start = messages.stream()
                .map(ChatMessageEntity::getTimestamp)
                .min(LocalDateTime::compareTo)
                .orElse(null);

        user.setCount(count);
        user.setStart(start);

        return CounselorResponseDTO.toDto(user);
    }
}
