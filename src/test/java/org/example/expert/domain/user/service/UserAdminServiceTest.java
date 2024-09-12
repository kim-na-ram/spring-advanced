package org.example.expert.domain.user.service;

import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.data.user.UserMockDataUtil;
import org.example.expert.domain.user.dto.request.UserRoleChangeRequest;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class UserAdminServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserAdminService userAdminService;

    @Nested
    @DisplayName("user 권한 변경 테스트 케이스")
    class ChangeUserRole {
        @Test
        @DisplayName("user가 존재하지 않아 권한 변경에 실패한다.")
        void changeUserRole_notFoundUser_failure() {
            // given
            long userId = 1L;
            UserRoleChangeRequest userRoleChangeRequest = UserMockDataUtil.userRoleChangeRequest_toUser();

            given(userRepository.findById(anyLong())).willReturn(Optional.empty());

            // when
            InvalidRequestException exception =
                    assertThrows(InvalidRequestException.class,
                            () -> userAdminService.changeUserRole(userId, userRoleChangeRequest)
                    );

            // then
            assertEquals("User not found", exception.getMessage());
        }

        @Test
        @DisplayName("유효하지 않은 권한으로 인해 권한 변경에 실패한다.")
        void changeUserRole_invalidUserRole_failure() {
            // given
            long userId = 1L;
            UserRoleChangeRequest userRoleChangeRequest = UserMockDataUtil.userRoleChangeRequest_toInvalidRole();

            User user = UserMockDataUtil.user();

            given(userRepository.findById(anyLong())).willReturn(Optional.of(user));

            // when
            InvalidRequestException exception =
                    assertThrows(InvalidRequestException.class,
                            () -> userAdminService.changeUserRole(userId, userRoleChangeRequest)
                    );

            // then
            assertEquals("유효하지 않은 UerRole", exception.getMessage());
        }

        @Test
        @DisplayName("user 권한 변경에 성공한다.")
        void changeUserRole_success() {
            // given
            long userId = 1L;
            UserRoleChangeRequest userRoleChangeRequest = UserMockDataUtil.userRoleChangeRequest_toAdmin();

            User user = UserMockDataUtil.user();

            given(userRepository.findById(anyLong())).willReturn(Optional.of(user));

            // when
            userAdminService.changeUserRole(userId, userRoleChangeRequest);

            // then
            assertEquals(UserRole.ADMIN, user.getUserRole());
        }
    }
}