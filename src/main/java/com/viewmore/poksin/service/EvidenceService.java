package com.viewmore.poksin.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.viewmore.poksin.dto.evidence.CreateEvidenceDTO;
import com.viewmore.poksin.dto.evidence.EvidenceDetailResponseDTO;
import com.viewmore.poksin.dto.evidence.MonthEvidenceResponseDTO;
import com.viewmore.poksin.entity.*;
import com.viewmore.poksin.exception.CategoryNotFoundException;
import com.viewmore.poksin.exception.EvidenceNotFoundException;
import com.viewmore.poksin.exception.ViolenceSegmentNotFoundException;
import com.viewmore.poksin.repository.CategoryRepository;
import com.viewmore.poksin.repository.EvidenceRepository;
import com.viewmore.poksin.repository.UserRepository;
import com.viewmore.poksin.repository.ViolenceSegmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EvidenceService {
    private final EvidenceRepository evidenceRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final S3Uploader s3Uploader;
    private final RestTemplate restTemplate;  // RestTemplate 주입
    private final ViolenceSegmentRepository violenceSegmentRepository;
    private final String FASTAPI_URL = "http://43.202.127.70/detect-violence/";

    public EvidenceDetailResponseDTO updateFile(String username, CreateEvidenceDTO createEvidenceDTO, List<MultipartFile> fileUrls) throws IOException {
        System.out.println(username);
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("해당 사용자 이름을 가진 사용자를 찾을 수 없습니다: " + username));

        System.out.println(user);
        List<String> getUrls = new ArrayList<>();

        CategoryTypeEnum type = createEvidenceDTO.getType();
        for(MultipartFile file : fileUrls) {
            getUrls.add(s3Uploader.upload(file, type.toString().toLowerCase()));
        }

        CategoryEntity category = categoryRepository.findByName(type)
                .orElseThrow(() -> new CategoryNotFoundException("해당 카테고리 이름을 찾을 수 없습니다."));

        EvidenceEntity evidenceEntity = EvidenceEntity.builder()
                .user(user)
                .title(createEvidenceDTO.getTitle())
                .description(createEvidenceDTO.getDescription())
                .category(category)
                .build();

        evidenceEntity.setFileUrls(getUrls);

        evidenceRepository.save(evidenceEntity);

        // FastAPI에 요청
        if (type.equals(CategoryTypeEnum.VIDEO)) {
            for (String url : getUrls) {
                String fileName = url.substring(url.lastIndexOf("/") + 1);
                sendRequestToFastApi(fileName, evidenceEntity.getId());
            }
        }

        return EvidenceDetailResponseDTO.toDto(evidenceEntity);
    }

    private void sendRequestToFastApi(String fileName, int evidenceId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> requestPayload = Map.of(
                "file_name", fileName,
                "evidence_id", evidenceId
        );

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestPayload, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(FASTAPI_URL, requestEntity, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                System.out.println("Request to FastAPI was successful: " + response.getBody());
            } else {
                System.err.println("Request to FastAPI failed with status code: " + response.getStatusCode());
            }
        } catch (Exception e) {
            System.err.println("Exception occurred while sending request to FastAPI: " + e.getMessage());
        }
    }
    public List<MonthEvidenceResponseDTO> findAllByMonth(String username, String year, String month) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("해당 사용자 이름을 가진 사용자를 찾을 수 없습니다: " + username));

        List<EvidenceEntity> evidenceEntityList = evidenceRepository.findByUserAndYearAndMonth(user, Integer.parseInt(year), Integer.parseInt(month));

        List<EvidenceDetailResponseDTO> evidenceResponseDTOS = new ArrayList<>();

        // 날짜 별로 그룹핑
        Map<LocalDate, Long> groupedByDay = evidenceEntityList.stream()
                .collect(Collectors.groupingBy(
                        evidence -> evidence.getCreatedAt().toLocalDate(),
                        Collectors.counting()
                ));

        List<MonthEvidenceResponseDTO> responseDTOs = new ArrayList<>();
        for (Map.Entry<LocalDate, Long> entry : groupedByDay.entrySet()) {
            responseDTOs.add(MonthEvidenceResponseDTO.builder()
                    .evidenceCount(entry.getValue().intValue())
                    .build());
        }

        return responseDTOs;
    }

    public List<EvidenceDetailResponseDTO> findAllByDay(String username, String year, String month, String day, CategoryTypeEnum category) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("해당 사용자 이름을 가진 사용자를 찾을 수 없습니다: " + username));

        CategoryEntity categoryEntity = categoryRepository.findByName(category)
                .orElseThrow(() -> new CategoryNotFoundException("해당 카테고리 이름을 찾을 수 없습니다."));

        List<EvidenceEntity> evidenceEntityList = evidenceRepository.findByUserAndYearAndMonthAndDay(user, Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day), categoryEntity);

        List<EvidenceDetailResponseDTO> evidenceResponseDTOS = new ArrayList<>();
        evidenceEntityList.forEach(entity -> {
            try {
                EvidenceDetailResponseDTO evidenceDetailResponseDTO = EvidenceDetailResponseDTO.toDto(entity);
                if (entity.getCategory().getName() == CategoryTypeEnum.VIDEO) {
                    evidenceDetailResponseDTO.setDetection("영상에서 폭력 발생 검출 중입니다. 잠시만 기다려주세요.");

                    if (entity.isDone()) {
                        Integer times = violenceSegmentRepository.countAllByEvidence_Id(entity.getId());
                        Float duration = violenceSegmentRepository.sumDurationByEvidence_Id(entity.getId());

                        // null 값을 0으로 변환
                        times = (times == null) ? 0 : times;
                        duration = (duration == null) ? 0.0f : duration;

                        String message = String.format("폭력 발생 횟수는 %d회, 폭력 지속 시간 %.2f초.", times, duration);

                        System.out.println(message);

                        evidenceDetailResponseDTO.setDetection(message);
                    }

                }
                evidenceResponseDTOS.add(evidenceDetailResponseDTO);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });

        return evidenceResponseDTOS;
    }

    public void deleteEvidence(Integer id) throws JsonProcessingException {
        EvidenceEntity evidence = evidenceRepository.findById(id)
                .orElseThrow(() -> new EvidenceNotFoundException("증거를 찾을 수 없습니다."));

        evidenceRepository.delete(evidence);

        String prefix = "https://poksin.s3.ap-northeast-2.amazonaws.com/";

        for (String fileUrl : evidence.getFileUrls()) {
            String fileName = removePrefix(fileUrl, prefix);
            System.out.println(fileName);
            s3Uploader.deleteFile(fileName);
        }
    }

    private String removePrefix(String url, String prefix) {
        if (url.startsWith(prefix)) {
            return url.replace(prefix, "");
        }
        return url;
    }


    public List<EvidenceDetailResponseDTO.EvidenceVideoResponseDTO> detailVideoEvidence(Integer id) {
        List<ViolenceSegmentEntity> violenceSegmentEntities = violenceSegmentRepository.findAllByEvidence_Id(id)
                .orElseThrow(() -> new ViolenceSegmentNotFoundException("폭행 장면이 검출되지 않았습니다."));
        return violenceSegmentEntities.stream().map(EvidenceDetailResponseDTO.EvidenceVideoResponseDTO::toDto).toList();
    }
}
