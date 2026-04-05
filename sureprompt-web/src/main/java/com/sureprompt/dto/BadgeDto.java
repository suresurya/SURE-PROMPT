package com.sureprompt.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BadgeDto {
    private String name;    // "Contributor"
    private String icon;    // "fa-trophy"
    private String color;   // "#f59e0b"
}
