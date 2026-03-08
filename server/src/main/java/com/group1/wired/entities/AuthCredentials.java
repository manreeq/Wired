package com.group1.wired.entities;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "auth_credentials")
public class AuthCredentials {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "credential_id", nullable = false)
    private Long credentialId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "userID", nullable = false)
    private User user; 

    @Column(name = "access_token", nullable = false, length = 2048)
    private String accessToken = "None";

    // This will store your AES-256 encrypted string, not the raw token
    @Column(name = "refresh_token", nullable = false, length = 2048)
    private String refreshToken = "None";

    @Column(name = "token_expires_at", nullable = false)
    private LocalDateTime tokenExpiresAt;

    
    public AuthCredentials() {}

    public AuthCredentials(User user, String accessToken, String refreshToken, LocalDateTime tokenExpiresAt) {
        this.user = user;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.tokenExpiresAt = tokenExpiresAt;
    }


    public Long getCredentialId() {
        return credentialId;
    }

    public void setCredentialId(Long credentialId) {
        this.credentialId = credentialId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public LocalDateTime getTokenExpiresAt() {
        return tokenExpiresAt;
    }

    public void setTokenExpiresAt(LocalDateTime tokenExpiresAt) {
        this.tokenExpiresAt = tokenExpiresAt;
    }
}