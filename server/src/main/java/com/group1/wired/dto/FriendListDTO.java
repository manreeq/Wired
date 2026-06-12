package com.group1.wired.dto;

import com.group1.wired.entities.FriendConnection;

import lombok.Getter;

@Getter
public class FriendListDTO {
    private Long connectionId;
    private String requesterDisplayName;
    private String requesterFriendCode;
    private Long requesterId;
    private String requesterProfilePicUrl;
    private String targetDisplayName;
    private String targetFriendCode;
    private Long targetId;
    private String targetProfilePicUrl;
    private String status;

    public FriendListDTO(FriendConnection connection) {
        this.connectionId = connection.getConnectionId();
        this.requesterDisplayName = connection.getRequesterUser().getDisplayName();
        this.requesterFriendCode = connection.getRequesterUser().getFriendCode();
        this.requesterId = connection.getRequesterUser().getUserID();
        this.requesterProfilePicUrl = connection.getRequesterUser().getProfilePictureURL();
        this.targetDisplayName = connection.getTargetUser().getDisplayName();
        this.targetFriendCode = connection.getTargetUser().getFriendCode();
        this.targetId = connection.getTargetUser().getUserID();
        this.targetProfilePicUrl = connection.getTargetUser().getProfilePictureURL();
        this.status = connection.getStatus();
    }

}
