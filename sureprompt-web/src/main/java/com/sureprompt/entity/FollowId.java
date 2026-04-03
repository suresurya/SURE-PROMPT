package com.sureprompt.entity;

import java.io.Serializable;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @EqualsAndHashCode
public class FollowId implements Serializable {
    private Long follower;
    private Long following;
}
