package com.revconnect.auth.dto;

public class TokenRefreshResponse {

    private String accessToken;
    private String refreshToken;
    private String tokenType;

    public TokenRefreshResponse() {
        this.tokenType = "Bearer";
    }

    private TokenRefreshResponse(Builder builder) {
        this.accessToken = builder.accessToken;
        this.refreshToken = builder.refreshToken;
        this.tokenType = builder.tokenType != null ? builder.tokenType : "Bearer";
    }

    public static Builder builder() {
        return new Builder();
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

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public static class Builder {
        private String accessToken;
        private String refreshToken;
        private String tokenType;

        public Builder accessToken(String accessToken) {
            this.accessToken = accessToken;
            return this;
        }

        public Builder refreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
            return this;
        }

        public Builder tokenType(String tokenType) {
            this.tokenType = tokenType;
            return this;
        }

        public TokenRefreshResponse build() {
            return new TokenRefreshResponse(this);
        }
    }
}
