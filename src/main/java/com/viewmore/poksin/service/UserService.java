package com.viewmore.poksin.service;

import com.viewmore.poksin.dto.user.*;
import com.viewmore.poksin.entity.ChatRoomEntity;
import com.viewmore.poksin.entity.CounselorEntity;
import com.viewmore.poksin.entity.UserEntity;
import com.viewmore.poksin.entity.ChatMessageEntity;
import com.viewmore.poksin.exception.ChatRoomNotFoundException;
import com.viewmore.poksin.exception.DuplicateUsernameException;
import com.viewmore.poksin.repository.ChatMessageRepository;
import com.viewmore.poksin.repository.ChatRoomRepository;
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
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final CounselorRepository counselorRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public void register(RegisterDTO registerDTO) {
        String username = registerDTO.getUsername();
        String password = registerDTO.getPassword();

        // 상담사, 일반 유저 아이디 중복 검사
        if (userRepository.existsByUsername(username) || counselorRepository.existsByUsername(username)) {
            throw new DuplicateUsernameException("중복된 아이디가 존재합니다.");
        }

        UserEntity user = UserEntity.userEntityBuilder()
                .username(username)
                .password(bCryptPasswordEncoder.encode(password))
                .phoneNum(registerDTO.getPhoneNum())
                .emergencyNum(registerDTO.getEmergencyNum())
                .address(registerDTO.getAddress())
                .phoneOpen(registerDTO.getphoneOpen())
                .emergencyOpen(registerDTO.getEmergencyOpen())
                .addressOpen(registerDTO.getAddressOpen())
                .role("ROLE_USER")
                .build();

        userRepository.save(user);
    }

    public UserResponseDTO mypage(String username) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("해당 사용자 이름을 가진 사용자를 찾을 수 없습니다: " + username));

        //마지막 채팅 시간 조회


        Optional<ChatMessageEntity> lastChatMessage = chatMessageRepository.findLatestBySender(username);
        LocalDateTime lastChated = lastChatMessage.map(ChatMessageEntity::getTimestamp).orElse(null);

        return UserResponseDTO.toDto(user, lastChated);
    }

    @Transactional
    public UserResponseDTO updateUser(String username, UpdateUserDTO updateUserDTO) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("해당 사용자 이름을 가진 사용자를 찾을 수 없습니다: " + username));

        user.updateUser(updateUserDTO);

        return UserResponseDTO.toDto(user, null);
    }

    public void deleteUser(String username) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("해당 사용자 이름을 가진 사용자를 찾을 수 없습니다: " + username));

        userRepository.delete(user);
    }

    public List<UserResponseDTO> findAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> UserResponseDTO.toDto(user, null))
                .collect(Collectors.toList());
    }

    public CounselorResponseDTO getAdminMypage(String adminUsername, String userUsername) {
        CounselorEntity counselor = counselorRepository.findByUsername(adminUsername)
                .orElseThrow(() -> new UsernameNotFoundException("해당 상담사를 찾을 수 없습니다: " + adminUsername));

        // 로그인한 사용자와 동일한 이름의 채팅방 찾기
        ChatRoomEntity chatRoom = chatRoomRepository.findByName(userUsername)
                .orElseThrow(() -> new ChatRoomNotFoundException("해당 채팅방을 찾을 수 없습니다: " + userUsername));

        // 채팅방에서 첫 번째 메시지 조회
        List<ChatMessageEntity> messages = chatMessageRepository.findAllByRoomIdOrderByTimestampAsc(chatRoom.getRoomId());
        LocalDateTime firstChatDate = messages.isEmpty() ? null : messages.get(0).getTimestamp();

        int chatCount = counselor.getChatRoomCount() != null ? counselor.getChatRoomCount().getTotalCount() : 0;

        counselor.setStart(firstChatDate);

        return CounselorResponseDTO.toDto(counselor);
    }
}
