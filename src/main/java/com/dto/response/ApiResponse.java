package com.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private boolean success;
    private T data;
    private ErrorDetail error;
    private MetaDetail meta;

    public static <T> ApiResponse<T> ok(T data) {
        ApiResponse<T> r = new ApiResponse<>();
        r.success = true;
        r.data = data;
        return r;
    }

    public static <T> ApiResponse<T> ok(T data, MetaDetail meta) {
        ApiResponse<T> r = new ApiResponse<>();
        r.success = true;
        r.data = data;
        r.meta = meta;
        return r;
    }

    public static <T> ApiResponse<T> fail(String code, String message) {
        ApiResponse<T> r = new ApiResponse<>();
        r.success = false;
        r.error = new ErrorDetail(code, message);
        return r;
    }

    @Data
    public static class ErrorDetail {
        private final String code;
        private final String message;
		public ErrorDetail(String code, String message) {
			this.code = code;
			this.message = message;
		}
        
        
    }

    @Data
    public static class MetaDetail {
        private int page;
        private long total;

        public MetaDetail(int page, long total) {
            this.page = page;
            this.total = total;
        }
    }
}
