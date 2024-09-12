package org.example.expert.domain.user.service;

import org.example.expert.config.PasswordEncoder;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.data.user.UserMockDataUtil;
import org.example.expert.domain.user.dto.request.UserChangePasswordRequest;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Spy
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Nested
    @DisplayName("user 조회 테스트 케이스")
    class GetUser {
        @Test
        @DisplayName("user가 존재하지 않아 조회에 실패한다.")
        public void getUser_notFoundUser_failure() {
            // given
            long userId = 1L;

            given(userRepository.findById(anyLong())).willReturn(Optional.empty());

            // when
            InvalidRequestException exception =
                    assertThrows(InvalidRequestException.class, () -> userService.getUser(userId));

            // then
            assertEquals("User not found", exception.getMessage());
        }

        @Test
        @DisplayName("user 조회에 성공한다.")
        public void getUser_success() {
            // given
            long userId = 1L;

            User user = UserMockDataUtil.user();

            given(userRepository.findById(anyLong())).willReturn(Optional.of(user));

            // when
            UserResponse result = userService.getUser(userId);

            // then
            assertNotNull(result);
        }
    }

    @Nested
    @DisplayName("비밀번호 변경 테스트 케이스")
    class ChangePassword {
        @Test
        @DisplayName("비밀번호 형식(길이)이 맞지 않아 비밀번호 변경에 실패한다.")
        public void changePassword_invalidPassword_length_failure() {
            // given
            long userId = 1L;
            UserChangePasswordRequest userChangePasswordRequest = new UserChangePasswordRequest("password", "ppp");

            // when
            InvalidRequestException exception =
                    assertThrows(InvalidRequestException.class,
                            () -> userService.changePassword(userId, userChangePasswordRequest)
                    );

            // then
            assertEquals("새 비밀번호는 8자 이상이어야 하고, 숫자와 대문자를 포함해야 합니다.", exception.getMessage());
        }

        @Test
        @DisplayName("비밀번호 형식(대문자)이 맞지 않아 비밀번호 변경에 실패한다.")
        public void changePassword_invalidPassword_upperCase_failure() {
            // given
            long userId = 1L;
            UserChangePasswordRequest userChangePasswordRequest = new UserChangePasswordRequest("password", "password1");

            // when
            InvalidRequestException exception =
                    assertThrows(InvalidRequestException.class,
                            () -> userService.changePassword(userId, userChangePasswordRequest)
                    );

            // then
            assertEquals("새 비밀번호는 8자 이상이어야 하고, 숫자와 대문자를 포함해야 합니다.", exception.getMessage());
        }

        @Test
        @DisplayName("비밀번호 형식(숫자)이 맞지 않아 비밀번호 변경에 실패한다.")
        public void changePassword_invalidPassword_number_failure() {
            // given
            long userId = 1L;
            UserChangePasswordRequest userChangePasswordRequest = new UserChangePasswordRequest("password", "Password");

            // when
            InvalidRequestException exception =
                    assertThrows(InvalidRequestException.class,
                            () -> userService.changePassword(userId, userChangePasswordRequest)
                    );

            // then
            assertEquals("새 비밀번호는 8자 이상이어야 하고, 숫자와 대문자를 포함해야 합니다.", exception.getMessage());
        }

        @Test
        @DisplayName("user가 존재하지 않아 비밀번호 변경에 실패한다.")
        public void changePassword_notFoundUser_failure() {
            // given
            long userId = 1L;
            UserChangePasswordRequest userChangePasswordRequest = new UserChangePasswordRequest("password", "Password1");

            given(userRepository.findById(anyLong())).willReturn(Optional.empty());

            // when
            InvalidRequestException exception =
                    assertThrows(InvalidRequestException.class,
                            () -> userService.changePassword(userId, userChangePasswordRequest)
                    );

            // then
            assertEquals("User not found", exception.getMessage());
        }

        @Test
        @DisplayName("새 비밀번호와 기존 비밀번호가 같아 비밀번호 변경에 실패한다.")
        public void changePassword_newPasswordSameOldPassword_failure() {
            // given
            long userId = 1L;
            UserChangePasswordRequest userChangePasswordRequest = new UserChangePasswordRequest("Password1", "Password1");

            User user = UserMockDataUtil.userWithPassword(passwordEncoder.encode("Password1"));

            given(userRepository.findById(anyLong())).willReturn(Optional.of(user));

            // when
            InvalidRequestException exception =
                    assertThrows(InvalidRequestException.class,
                            () -> userService.changePassword(userId, userChangePasswordRequest)
                    );

            // then
            assertEquals("새 비밀번호는 기존 비밀번호와 같을 수 없습니다.", exception.getMessage());
        }

        @Test
        @DisplayName("잘못된 비밀번호로 인해 비밀번호 변경에 실패한다.")
        public void changePassword_wrongPassword_failure() {
            // given
            long userId = 1L;
            UserChangePasswordRequest userChangePasswordRequest = new UserChangePasswordRequest("wrongPassword", "Password1");

            User user = UserMockDataUtil.userWithPassword(passwordEncoder.encode("password11"));

            given(userRepository.findById(anyLong())).willReturn(Optional.of(user));

            // when
            InvalidRequestException exception =
                    assertThrows(InvalidRequestException.class,
                            () -> userService.changePassword(userId, userChangePasswordRequest)
                    );

            // then
            assertEquals("잘못된 비밀번호입니다.", exception.getMessage());
        }

        @Test
        @DisplayName("비밀번호 변경에 성공한다.")
        public void changePassword_success() {
            // given
            long userId = 1L;
            UserChangePasswordRequest userChangePasswordRequest = new UserChangePasswordRequest("password", "Password1");

            User user = UserMockDataUtil.userWithPassword(passwordEncoder.encode("password"));

            given(userRepository.findById(anyLong())).willReturn(Optional.of(user));

            // when
            userService.changePassword(userId, userChangePasswordRequest);

            // then
            assertTrue(passwordEncoder.matches(userChangePasswordRequest.getNewPassword(), user.getPassword()));
        }
    }
}