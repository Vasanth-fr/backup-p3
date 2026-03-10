package com.revconnect.user.dto;

import com.revconnect.user.entity.UserProfile;

public class UserSummaryResponse {

    private Long id;
    private String email;
    private String username;
    private String fullName;
    private String firstName;
    private String lastName;
    private String bio;
    private String profileImageUrl;

    public UserSummaryResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public static UserSummaryResponse fromEntity(UserProfile profile) {
        String username = profile.getEmail() != null && profile.getEmail().contains("@")
                ? profile.getEmail().substring(0, profile.getEmail().indexOf('@'))
                : profile.getEmail();
        String fullName = String.format("%s %s",
                profile.getFirstName() == null ? "" : profile.getFirstName(),
                profile.getLastName() == null ? "" : profile.getLastName()).trim();

        return UserSummaryResponse.builder()
                .id(profile.getId())
                .email(profile.getEmail())
                .username(username)
                .fullName(fullName)
                .firstName(profile.getFirstName())
                .lastName(profile.getLastName())
                .bio(profile.getBio())
                .profileImageUrl(profile.getProfilePictureUrl())
                .build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String email;
        private String username;
        private String fullName;
        private String firstName;
        private String lastName;
        private String bio;
        private String profileImageUrl;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder fullName(String fullName) {
            this.fullName = fullName;
            return this;
        }

        public Builder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder bio(String bio) {
            this.bio = bio;
            return this;
        }

        public Builder profileImageUrl(String profileImageUrl) {
            this.profileImageUrl = profileImageUrl;
            return this;
        }

        public UserSummaryResponse build() {
            UserSummaryResponse response = new UserSummaryResponse();
            response.id = this.id;
            response.email = this.email;
            response.username = this.username;
            response.fullName = this.fullName;
            response.firstName = this.firstName;
            response.lastName = this.lastName;
            response.bio = this.bio;
            response.profileImageUrl = this.profileImageUrl;
            return response;
        }
    }
}
