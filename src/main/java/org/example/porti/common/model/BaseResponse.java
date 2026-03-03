package org.example.porti.common.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import static org.example.porti.common.model.BaseResponseStatus.SUCCESS;

@Getter
@Setter
@AllArgsConstructor
public class BaseResponse<T> {
    private Boolean isSuccess;
    private T data;

    public static <T> BaseResponse success(T data) {
        return new BaseResponse(
                SUCCESS.isSuccess(),
                data
        );
    }

    public static <T> BaseResponse fail(BaseResponseStatus status) {
        return new BaseResponse(
                status.isSuccess(),
                null
        );
    }

    public static <T> BaseResponse fail(BaseResponseStatus status, T data) {
        return new BaseResponse(
                status.isSuccess(),
                data
        );
    }
}