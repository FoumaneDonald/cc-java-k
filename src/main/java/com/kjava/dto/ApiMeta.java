package com.kjava.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiMeta {
    private Integer page;
    private Integer total;
    private Integer size;
    private Long timestamp;

    public static ApiMeta of(Integer page, Integer total) {
        return ApiMeta.builder()
                .page(page)
                .total(total)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    public static ApiMeta of(Integer page, Integer total, Integer size) {
        return ApiMeta.builder()
                .page(page)
                .total(total)
                .size(size)
                .timestamp(System.currentTimeMillis())
                .build();
    }
}
