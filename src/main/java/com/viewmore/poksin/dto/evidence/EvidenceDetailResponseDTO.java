package com.viewmore.poksin.dto.evidence;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.viewmore.poksin.entity.CategoryTypeEnum;
import com.viewmore.poksin.entity.EvidenceEntity;
import com.viewmore.poksin.entity.ViolenceSegmentEntity;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EvidenceDetailResponseDTO {
    // id
    private int id;
    // 카테고리
    private CategoryTypeEnum category;
    // 자료 제목
    private String title;
    // 영상 검출 부분
    private String detection;
    // 영상 검출 완료
    private boolean done;
    // 자료 상세 설명
    private String description;
    // 자료와 함께 첨부하는 파일 (url)
    private List<String> fileUrls;
    // 생성일
    private LocalDate created_at;

    public static EvidenceDetailResponseDTO toDto(EvidenceEntity entity) throws JsonProcessingException {
        return builder()
                .id(entity.getId())
                .category(entity.getCategory().getName())
                .done(entity.isDone())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .fileUrls(entity.getFileUrls())
                .created_at(entity.getEvidencdCreatedAt())
                .build();
    }

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    //검출 비디오
    public static class EvidenceVideoResponseDTO {
        private int id;
        // 자료 원본
        private int evidence_id;
        // 폭력 지속 시간
        private Float duration;
        // 자료와 함께 첨부하는 파일 (url)
        private String fileurl;
        // 증거 생성일
        private String createdAt;


        public static EvidenceVideoResponseDTO toDto(ViolenceSegmentEntity violenceSegmentEntity) {
            return builder()
                    .id(violenceSegmentEntity.getId())
                    .duration(violenceSegmentEntity.getDuration())
                    .evidence_id(violenceSegmentEntity.getEvidence().getId())
                    .fileurl(violenceSegmentEntity.getS3_url())
                    .build();
        }
    }


}
