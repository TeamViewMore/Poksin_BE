package com.viewmore.poksin.exception;

import com.viewmore.poksin.code.ErrorCode;
import com.viewmore.poksin.dto.response.ErrorResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalHandleException {
    @ExceptionHandler(DuplicateUsernameException.class)
    protected ResponseEntity<ErrorResponseDTO> handleDuplicateUsernameException(final DuplicateUsernameException e) {
        return ResponseEntity
                .status(ErrorCode.DUPLICATE_USERNAME.getStatus().value())
                .body(new ErrorResponseDTO(ErrorCode.DUPLICATE_USERNAME));
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    protected ResponseEntity<ErrorResponseDTO> handleUsernameNotFoundException(final UsernameNotFoundException e) {
        return ResponseEntity
                .status(ErrorCode.USER_NOT_FOUND.getStatus().value())
                .body(new ErrorResponseDTO(ErrorCode.USER_NOT_FOUND));
    }

    @ExceptionHandler(CategoryNotFoundException.class)
    protected ResponseEntity<ErrorResponseDTO> handleCategoryNotFoundException(final CategoryNotFoundException e) {
        return ResponseEntity
                .status(ErrorCode.CATEGORY_NOT_FOUND.getStatus().value())
                .body(new ErrorResponseDTO(ErrorCode.CATEGORY_NOT_FOUND));
    }

    @ExceptionHandler(EvidenceNotFoundException.class)
    protected ResponseEntity<ErrorResponseDTO> handleEvidenceNotFoundException(final EvidenceNotFoundException e) {
        return ResponseEntity
                .status(ErrorCode.EVIDENCE_NOT_FOUND.getStatus().value())
                .body(new ErrorResponseDTO(ErrorCode.EVIDENCE_NOT_FOUND));
    }

    @ExceptionHandler(ChatRoomNotFoundException.class)
    protected ResponseEntity<ErrorResponseDTO> handleChatRoomNotFoundException(final ChatRoomNotFoundException e) {
        return ResponseEntity
                .status(ErrorCode.CHATROOM_NOT_FOUND.getStatus().value())
                .body(new ErrorResponseDTO(ErrorCode.CHATROOM_NOT_FOUND));
    }


    /**
     * 입력값 검증
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity
                .status(ErrorCode.BAD_REQUEST.getStatus().value())
                .body(new ErrorResponseDTO(ErrorCode.BAD_REQUEST, errors));
    }
}
