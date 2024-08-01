package com.viewmore.poksin.dto.evidence;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonthEvidenceResponseDTO {
    // 기록 개수
    private Integer evidenceCount;
}
