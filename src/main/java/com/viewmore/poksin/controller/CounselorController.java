package com.viewmore.poksin.controller;

import com.viewmore.poksin.code.SuccessCode;
import com.viewmore.poksin.dto.response.ResponseDTO;
import com.viewmore.poksin.dto.user.CounselorRegisterDTO;
import com.viewmore.poksin.dto.user.CounselorResponseDTO;
import com.viewmore.poksin.service.CounselorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/counselor")
@RequiredArgsConstructor
public class CounselorController implements CounselorAPI{
    private final CounselorService counselorService;

    @PostMapping("/register")
    public ResponseEntity<ResponseDTO> registerCounselor(@RequestBody CounselorRegisterDTO counselorRegisterDTO) {
        counselorService.registerCounselor(counselorRegisterDTO);
        return ResponseEntity
                .status(SuccessCode.SUCCESS_COUNSELOR_REGISTER.getStatus().value())
                .body(new ResponseDTO<>(SuccessCode.SUCCESS_COUNSELOR_REGISTER, null));
    }

    @GetMapping("/mypage")
    public ResponseEntity<ResponseDTO> counselorMypage() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        CounselorResponseDTO response = counselorService.counselorMypage(username);
        return ResponseEntity
                .status(SuccessCode.SUCCESS_RETRIEVE_USER.getStatus().value())
                .body(new ResponseDTO<>(SuccessCode.SUCCESS_RETRIEVE_USER, response));
    }

}
