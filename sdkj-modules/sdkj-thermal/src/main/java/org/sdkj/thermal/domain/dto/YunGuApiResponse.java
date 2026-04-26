package org.sdkj.thermal.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

/**
 * 云谷 API 统一响应体
 * 对应旧系统 YGR<T>
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class YunGuApiResponse<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @JsonProperty("Code")
    private int code;

    @JsonProperty("Message")
    private String message;

    @JsonProperty("Data")
    private T data;

    public static <T> YunGuApiResponse<T> success(T data) {
        return YunGuApiResponse.<T>builder()
            .code(0)
            .message("success")
            .data(data)
            .build();
    }

    public static <T> YunGuApiResponse<T> fail(String message) {
        return YunGuApiResponse.<T>builder()
            .code(1)
            .message(message)
            .data(null)
            .build();
    }
}
