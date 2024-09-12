package org.example.expert.domain.auth.service;

import org.example.expert.config.JwtUtil;
import org.example.expert.config.PasswordEncoder;
import org.example.expert.domain.auth.dto.request.SigninRequest;
import org.example.expert.domain.auth.dto.request.SignupRequest;
import org.example.expert.domain.auth.dto.response.SigninResponse;
import org.example.expert.domain.auth.dto.response.SignupResponse;
import org.example.expert.domain.auth.exception.AuthException;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.data.user.UserMockDataUtil;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @InjectMocks
    private AuthService authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Nested
    @DisplayName("회원가입 테스트 케이스")
    class SignUp {
        @Test
        @DisplayName("이미 존재하는 이메일이라 회원가입에 실패한다.")
        public void signup_existsEmail_failure() {
            // given
            SignupRequest signupRequest = UserMockDataUtil.signupRequest();

            given(userRepository.existsByEmail(anyString())).willReturn(true);

            // when
            InvalidRequestException exception =
                    assertThrows(InvalidRequestException.class, () -> authService.signup(signupRequest));

            // then
            assertEquals("이미 존재하는 이메일입니다.", exception.getMessage());
        }

        @Test
        @DisplayName("유효하지 않은 권한으로 인해 회원가입에 실패한다.")
        public void signup_invalidUserRole_failure() {
            // given
            SignupRequest signupRequest = UserMockDataUtil.signupRequest_invalidRole();

            given(userRepository.existsByEmail(anyString())).willReturn(false);

            // when
            InvalidRequestException exception = assertThrows(InvalidRequestException.class,
                    () -> authService.signup(signupRequest));

            // then
            assertEquals("유효하지 않은 UerRole", exception.getMessage());
        }

        @Test
        @DisplayName("회원가입에 성공한다.")
        public void signup_success() {
            // given
            SignupRequest signupRequest = UserMockDataUtil.signupRequest();

            User user = UserMockDataUtil.user();
            String bearerToken = "bearerToken";

            given(userRepository.existsByEmail(anyString())).willReturn(false);
            given(userRepository.save(any(User.class))).willReturn(user);
            given(jwtUtil.createToken(anyLong(), anyString(), any(UserRole.class))).willReturn(bearerToken);

            // when
            SignupResponse result = authService.signup(signupRequest);

            // then
            assertNotNull(result.getBearerToken());
        }
    }

    @Nested
    @DisplayName("로그인 테스트 케이스")
    class SignIn {
        @Test
        @DisplayName("가입되지 않은 user라 로그인에 실패한다.")
        public void singin_notExistEmail_failure() {
            // given
            SigninRequest signinRequest = UserMockDataUtil.signinRequest();

            given(userRepository.findByEmail(anyString())).willReturn(Optional.empty());

            // when
            InvalidRequestException exception =
                    assertThrows(InvalidRequestException.class, () -> authService.signin(signinRequest));

            // then
            assertEquals("가입되지 않은 유저입니다.", exception.getMessage());
        }

        @Test
        @DisplayName("이메일과 비밀번호가 일치하지 않아서 로그인에 실패한다.")
        public void singin_wrongPassword_failure() {
            // given
            SigninRequest signinRequest = UserMockDataUtil.signinRequest();

            User user = UserMockDataUtil.user();

            given(userRepository.findByEmail(anyString())).willReturn(Optional.of(user));
            given(passwordEncoder.matches(anyString(), anyString())).willReturn(false);

            // when
            AuthException exception =
                    assertThrows(AuthException.class, () -> authService.signin(signinRequest));

            // then
            assertEquals("잘못된 비밀번호입니다.", exception.getMessage());
        }

        @Test
        @DisplayName("로그인에 성공한다.")
        public void singin_success() {
            // given
            SigninRequest signinRequest = UserMockDataUtil.signinRequest();

            User user = UserMockDataUtil.user();
            String bearerToken = "bearerToken";

            given(userRepository.findByEmail(anyString())).willReturn(Optional.of(user));
            given(passwordEncoder.matches(anyString(), anyString())).willReturn(true);
            given(jwtUtil.createToken(anyLong(), anyString(), any(UserRole.class))).willReturn(bearerToken);

            // when
            SigninResponse result = authService.signin(signinRequest);

            // then
            assertNotNull(result);
        }
    }
}