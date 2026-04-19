package com.group1.wired.dto;

import com.group1.wired.entities.FriendConnection;

import com.group1.wired.entities.User;
import lombok.Getter;

@Getter
public class PendingRequestsDTO {
    private Long connectionId;
    private String requesterDisplayName;
    private String requesterFriendCode;
    private User requester;
    private String targetDisplayName;
    private String targetFriendCode;
    private String status;

    public PendingRequestsDTO(FriendConnection connection) {
        this.connectionId = connection.getConnectionId();
        this.requesterDisplayName = connection.getRequesterUser().getDisplayName();
        this.requesterFriendCode = connection.getRequesterUser().getFriendCode();
        this.requester = connection.getRequesterUser();
        this.targetDisplayName = connection.getTargetUser().getDisplayName();
        this.targetFriendCode = connection.getTargetUser().getFriendCode();
        this.status = connection.getStatus();
    }

}
