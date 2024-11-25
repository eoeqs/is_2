package eoeqs.dto;


public record ResponseForUserProfileDto(
        String userId,
        String userName,
        String userEmail,
        String userProfilePicture
) {
}