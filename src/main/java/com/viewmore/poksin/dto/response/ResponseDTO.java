package com.viewmore.poksin.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.viewmore.poksin.code.SuccessCode;
import lombok.Data;
import lombok.Setter;

@Data
public class ResponseDTO<T> {
    private Integer status;
    private String code;
    private String message;
    @Setter
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer totalCount;
    @Setter
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Float totalDuration;
    private T data;

    public ResponseDTO(SuccessCode successCode, T data) {
        this.status = successCode.getStatus().value();
        this.code = successCode.name();
        this.message = successCode.getMessage();
        this.data = data;
    }

}