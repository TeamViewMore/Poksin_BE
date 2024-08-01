package com.viewmore.poksin.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name="violence_segment")
public class ViolenceSegmentEntity {
    @Id
    private int id;
    private float duration; //폭력 지속 시간 (초)

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String s3_url;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "evidence_id")
    private EvidenceEntity evidence;
}
