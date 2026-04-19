package com.group1.wired.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FriendRequestDTO {
    private Long requesterUserId;
    private String targetFriendCode;
    private String status; // e.g. "Pending"
}
