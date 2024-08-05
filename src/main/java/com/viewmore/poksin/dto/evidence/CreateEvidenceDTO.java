package com.viewmore.poksin.dto.evidence;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.viewmore.poksin.entity.CategoryTypeEnum;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateEvidenceDTO {
    // 자료 제목
    private String title;
    // 자료 상세 설명
    private String description;
    // 자료 타입
    private CategoryTypeEnum type;
    // 자료 등록
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate createdAt;
}
