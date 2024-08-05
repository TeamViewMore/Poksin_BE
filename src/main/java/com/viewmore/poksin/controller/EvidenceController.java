package com.viewmore.poksin.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.viewmore.poksin.code.SuccessCode;
import com.viewmore.poksin.dto.evidence.CreateEvidenceDTO;
import com.viewmore.poksin.dto.evidence.EvidenceDetailResponseDTO;
import com.viewmore.poksin.dto.evidence.MonthEvidenceResponseDTO;
import com.viewmore.poksin.dto.response.ResponseDTO;
import com.viewmore.poksin.entity.CategoryTypeEnum;
import com.viewmore.poksin.repository.ViolenceSegmentRepository;
import com.viewmore.poksin.service.EvidenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/evidence")
@RequiredArgsConstructor
@Slf4j
public class EvidenceController {
    private final EvidenceService evidenceService;
    private final ViolenceSegmentRepository violenceSegmentRepository;
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @PostMapping("/upload")
    public ResponseEntity<ResponseDTO> createListFile(
            @ModelAttribute CreateEvidenceDTO createEvidenceDTO,
            @RequestParam("fileUrls") List<MultipartFile> fileUrls) throws IOException {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("controller"+username);
        EvidenceDetailResponseDTO response = evidenceService.updateFile(username, createEvidenceDTO, fileUrls);
        System.out.println(response);
        return ResponseEntity
                .status(SuccessCode.SUCCESS_CREATE_EVIDENCE.getStatus().value())
                .body(new ResponseDTO<>(SuccessCode.SUCCESS_CREATE_EVIDENCE, response));
    }

    // year, month로 증거 기록 조회
    @GetMapping("/get-month-evidence")
    public ResponseEntity<ResponseDTO> findAllEvidenceByMonth(
            @RequestParam("year") String year,
            @RequestParam("month") String month
             ) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        List<MonthEvidenceResponseDTO> response = evidenceService.findAllByMonth(username, year, month);
        return ResponseEntity
                .status(SuccessCode.SUCCESS_RETRIEVE_MONTH_EVIDENCE.getStatus().value())
                .body(new ResponseDTO<>(SuccessCode.SUCCESS_RETRIEVE_MONTH_EVIDENCE, response));
    }

    @GetMapping("/get-day-evidence")
    public ResponseEntity<ResponseDTO> findAllEvidenceByDay(
            @RequestParam("year") String year,
            @RequestParam("month") String month,
            @RequestParam("day") String day,
            @RequestParam("category") CategoryTypeEnum category
    ) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println(username);
        List<EvidenceDetailResponseDTO> response = evidenceService.findAllByDay(username, year, month, day, category);
        return ResponseEntity
                .status(SuccessCode.SUCCESS_RETRIEVE_DAY_EVIDENCE.getStatus().value())
                .body(new ResponseDTO<>(SuccessCode.SUCCESS_RETRIEVE_DAY_EVIDENCE, response));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ResponseDTO> deleteEvidence(
            @PathVariable("id") Integer id
    ) throws JsonProcessingException {
        evidenceService.deleteEvidence(id);
        return ResponseEntity
                .status(SuccessCode.SUCCESS_DELETE_EVIDENCE.getStatus().value())
                .body(new ResponseDTO<>(SuccessCode.SUCCESS_DELETE_EVIDENCE, null));

    }


    // 이거는 비디오 디테일을 보여주는 controller 부분임
    @GetMapping("/detail/{id}")
    public ResponseEntity<ResponseDTO> detailVideoEvidence(
            @PathVariable("id") Integer id
    ) throws JsonProcessingException {
        List<EvidenceDetailResponseDTO.EvidenceVideoResponseDTO> videoResponseDTOS = evidenceService.detailVideoEvidence(id);
        ResponseDTO responseDTO = new ResponseDTO<>(SuccessCode.SUCCESS_DETAIL_VIDEO_EVIDENCE, videoResponseDTOS);
        Integer times = violenceSegmentRepository.countAllByEvidence_Id(id);
        Float duration = violenceSegmentRepository.sumDurationByEvidence_Id(id);

            // null 값을 0으로 변환
        times = (times == null) ? 0 : times;
        duration = (duration == null) ? 0.0f : duration;

        String message = String.format("폭력 발생 횟수는 %d회, 폭력 지속 시간 %.2f초.", times, duration);
        responseDTO.setInfo(message);
        return ResponseEntity
                .status(SuccessCode.SUCCESS_DETAIL_VIDEO_EVIDENCE.getStatus().value())
                .body(new ResponseDTO<>(SuccessCode.SUCCESS_DETAIL_VIDEO_EVIDENCE, videoResponseDTOS));
    }
}
