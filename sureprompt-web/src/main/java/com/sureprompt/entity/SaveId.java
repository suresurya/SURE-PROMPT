package com.sureprompt.entity;

import java.io.Serializable;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @EqualsAndHashCode
public class SaveId implements Serializable {
    private Long user;
    private Long prompt;
}
