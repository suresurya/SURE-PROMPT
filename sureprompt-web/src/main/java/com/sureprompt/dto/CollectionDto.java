package com.sureprompt.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CollectionDto {
    private Long id;
    private String name;
    private boolean isPublic;
    private long promptCount;
}
