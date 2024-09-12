package org.example.expert.domain.data.user;

import org.example.expert.domain.auth.dto.request.SigninRequest;
import org.example.expert.domain.auth.dto.request.SignupRequest;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.user.dto.request.UserChangePasswordRequest;
import org.example.expert.domain.user.dto.request.UserRoleChangeRequest;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.StringUtils;

public class UserMockDataUtil {
    public static AuthUser authUser() {
        return new AuthUser(1L, "email", UserRole.USER);
    }

    public static AuthUser authUser(long userId) {
        return new AuthUser(userId, "email", UserRole.USER);
    }

    public static User userFromAuthUser() {
        AuthUser authUser = authUser();
        return User.fromAuthUser(authUser);
    }

    public static User userFromAuthUser(long userId) {
        AuthUser authUser = authUser(userId);
        return User.fromAuthUser(authUser);
    }

    public static User user() {
        User user = new User("email", "password", UserRole.USER);
        ReflectionTestUtils.setField(user, "id", 1L);
        return user;
    }

    public static User user(long userId) {
        User user = new User("email", "password", UserRole.USER);
        ReflectionTestUtils.setField(user, "id", userId);
        return user;
    }

    public static User userWithPassword(String password) {
        User user = new User("email", password, UserRole.USER);
        ReflectionTestUtils.setField(user, "id", 1L);
        return user;
    }

    public static SignupRequest signupRequest() {
        return new SignupRequest("email", "password", "user");
    }

    public static SignupRequest signupRequest_invalidRole() {
        return new SignupRequest("email", "password", "invalidRole");
    }

    public static SigninRequest signinRequest() {
        return new SigninRequest("email", "password");
    }

    public static UserRoleChangeRequest userRoleChangeRequest_toInvalidRole() {
        return new UserRoleChangeRequest("invalidRole");
    }

    public static UserRoleChangeRequest userRoleChangeRequest_toUser() {
        return new UserRoleChangeRequest("USER");
    }

    public static UserRoleChangeRequest userRoleChangeRequest_toAdmin() {
        return new UserRoleChangeRequest("ADMIN");
    }

    public static UserResponse userResponse() {
        return new UserResponse(1L, "email");
    }

    public static UserChangePasswordRequest userChangePasswordRequest() {
        return new UserChangePasswordRequest("oldPassword", "newPassword");
    }
}
