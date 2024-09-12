package org.example.expert.domain.manager.service;

import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.data.manager.ManagerMockDataUtil;
import org.example.expert.domain.data.todo.TodoMockDataUtil;
import org.example.expert.domain.data.user.UserMockDataUtil;
import org.example.expert.domain.manager.dto.request.ManagerSaveRequest;
import org.example.expert.domain.manager.dto.response.ManagerResponse;
import org.example.expert.domain.manager.dto.response.ManagerSaveResponse;
import org.example.expert.domain.manager.entity.Manager;
import org.example.expert.domain.manager.repository.ManagerRepository;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ManagerServiceTest {

    @Mock
    private ManagerRepository managerRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private TodoRepository todoRepository;
    @InjectMocks
    private ManagerService managerService;

    @Nested
    @DisplayName("manager 등록 테스트 케이스")
    class SaveManager {
        @Test
        @DisplayName("todo가 존재하지 않아 manager 등록에 실패한다.")
        void saveManager_notFoundTodo_failure() {
            // given
            long todoId = 1L;
            AuthUser authUser = UserMockDataUtil.authUser();
            ManagerSaveRequest managerSaveRequest = ManagerMockDataUtil.managerSaveRequest();

            given(todoRepository.findById(anyLong())).willReturn(Optional.empty());

            // when
            InvalidRequestException exception =
                    assertThrows(InvalidRequestException.class,
                            () -> managerService.saveManager(authUser, todoId, managerSaveRequest)
                    );

            // then
            assertEquals("Todo not found", exception.getMessage());
        }

        @Test
        @DisplayName("todo를 만든 user가 존재하지 않아 manager 등록에 실패한다.")
        void saveManager_todoUserIsNull_failure() {
            // given
            long todoId = 1L;
            AuthUser authUser = UserMockDataUtil.authUser();
            ManagerSaveRequest managerSaveRequest = ManagerMockDataUtil.managerSaveRequest();

            Todo todo = TodoMockDataUtil.todo();
            ReflectionTestUtils.setField(todo, "user", null);

            given(todoRepository.findById(anyLong())).willReturn(Optional.of(todo));

            // when, then
            InvalidRequestException exception =
                    assertThrows(InvalidRequestException.class,
                            () -> managerService.saveManager(authUser, todoId, managerSaveRequest)
                    );

            assertEquals("담당자를 등록하려고 하는 유저가 일정을 만든 유저가 유효하지 않습니다.", exception.getMessage());
        }

        @Test
        @DisplayName("현재 user와 todo를 만든 user가 같지 않아 담당 manager 등록에 실패한다.")
        void saveManager_userIsNotEqualTodoUser_failure() {
            // given
            long todoId = 1L;
            AuthUser authUser = UserMockDataUtil.authUser(2L);
            ManagerSaveRequest managerSaveRequest = ManagerMockDataUtil.managerSaveRequest();

            Todo todo = TodoMockDataUtil.todo();

            given(todoRepository.findById(anyLong())).willReturn(Optional.of(todo));

            // when, then
            InvalidRequestException exception =
                    assertThrows(InvalidRequestException.class,
                            () -> managerService.saveManager(authUser, todoId, managerSaveRequest)
                    );

            assertEquals("담당자를 등록하려고 하는 유저가 일정을 만든 유저가 유효하지 않습니다.", exception.getMessage());
        }

        @Test
        @DisplayName("담당자 user가 존재하지 않아 manager 등록에 실패한다.")
        void saveManager_notFoundManager_failure() {
            // given
            long todoId = 1L;
            AuthUser authUser = UserMockDataUtil.authUser();
            ManagerSaveRequest managerSaveRequest = ManagerMockDataUtil.managerSaveRequest();

            Todo todo = TodoMockDataUtil.todo();

            given(todoRepository.findById(anyLong())).willReturn(Optional.of(todo));
            given(userRepository.findById(anyLong())).willReturn(Optional.empty());

            // when
            InvalidRequestException exception =
                    assertThrows(InvalidRequestException.class,
                            () -> managerService.saveManager(authUser, todoId, managerSaveRequest)
                    );

            // then
            assertEquals("등록하려고 하는 담당자 유저가 존재하지 않습니다.", exception.getMessage());
        }

        @Test
        @DisplayName("담당자 user와 todo를 만든 user가 같아 manager 등록에 실패한다.")
        void saveManager_todoUserEqualManagerUser_failure() {
            // given
            long todoId = 1L;
            AuthUser authUser = UserMockDataUtil.authUser();
            ManagerSaveRequest managerSaveRequest = ManagerMockDataUtil.managerSaveRequest();

            Todo todo = TodoMockDataUtil.todo();
            User user = UserMockDataUtil.userFromAuthUser();

            given(todoRepository.findById(anyLong())).willReturn(Optional.of(todo));
            given(userRepository.findById(anyLong())).willReturn(Optional.of(user));

            // when
            InvalidRequestException exception =
                    assertThrows(InvalidRequestException.class,
                            () -> managerService.saveManager(authUser, todoId, managerSaveRequest)
                    );

            // then
            assertEquals("일정 작성자는 본인을 담당자로 등록할 수 없습니다.", exception.getMessage());
        }

        @Test
        @DisplayName("manager 등록에 성공한다.")
        void saveManager_success() {
            // given
            long todoId = 1L;
            AuthUser authUser = UserMockDataUtil.authUser();
            ManagerSaveRequest managerSaveRequest = ManagerMockDataUtil.managerSaveRequest();

            long managerUserId = 2L;
            Todo todo = TodoMockDataUtil.todo();
            User manager = UserMockDataUtil.userFromAuthUser(managerUserId);
            Manager savedManager = ManagerMockDataUtil.manager();

            given(todoRepository.findById(anyLong())).willReturn(Optional.of(todo));
            given(userRepository.findById(anyLong())).willReturn(Optional.of(manager));
            given(managerRepository.save(any())).willReturn(savedManager);

            // when
            ManagerSaveResponse managerSaveResponse = managerService.saveManager(authUser, todoId, managerSaveRequest);

            // then
            assertNotNull(managerSaveResponse);
        }
    }

    @Nested
    @DisplayName("manager 목록 조회 테스트 케이스")
    class GetManagerList {
        @Test
        @DisplayName("manager 목록 조회 시 todo가 없어 실패한다.")
        public void getManagers_notFoundTodo_failure() {
            // given
            long todoId = 1L;

            given(todoRepository.findById(todoId)).willReturn(Optional.empty());

            // when, then
            InvalidRequestException exception =
                    assertThrows(InvalidRequestException.class, () -> managerService.getManagers(todoId));

            assertEquals("Todo not found", exception.getMessage());
        }

        @Test
        @DisplayName("manager 목록 조회에 성공한다.")
        public void getManagers_success() {
            // given
            long todoId = 1L;
            Todo todo = TodoMockDataUtil.todo();
            List<Manager> managerList = List.of(ManagerMockDataUtil.manager());

            given(todoRepository.findById(todoId)).willReturn(Optional.of(todo));
            given(managerRepository.findByTodoIdWithUser(anyLong())).willReturn(managerList);

            // when
            List<ManagerResponse> managerResponseList = managerService.getManagers(todoId);

            // then
            assertFalse(managerResponseList.isEmpty());
        }
    }

    @Nested
    @DisplayName("manager 삭제 테스트 케이스")
    public class DeleteManager {
        @Test
        @DisplayName("user가 존재하지 않아 manager 삭제에 실패한다.")
        public void deleteManager_notFoundUser_failure() {
            // given
            long userId = 1L;
            long todoId = 1L;
            long managerId = 1L;

            given(userRepository.findById(anyLong())).willReturn(Optional.empty());

            // when
            InvalidRequestException exception =
                    assertThrows(InvalidRequestException.class,
                            () -> managerService.deleteManager(userId, todoId, managerId)
                    );

            // then
            assertEquals("User not found", exception.getMessage());
        }

        @Test
        @DisplayName("todo가 존재하지 않아 manager 삭제에 실패한다.")
        void deleteManager_notFoundTodo_failure() {
            // given
            long userId = 1L;
            long todoId = 1L;
            long managerId = 1L;

            User user = UserMockDataUtil.user();

            given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
            given(todoRepository.findById(anyLong())).willReturn(Optional.empty());

            // when
            InvalidRequestException exception =
                    assertThrows(InvalidRequestException.class,
                            () -> managerService.deleteManager(userId, todoId, managerId)
                    );

            // then
            assertEquals("Todo not found", exception.getMessage());
        }

        @Test
        @DisplayName("todo를 만든 user가 존재하지 않아 manager 삭제에 실패한다.")
        void deleteManager_userIsNull_failure() {
            // given
            long userId = 1L;
            long todoId = 1L;
            long managerId = 1L;

            User user = UserMockDataUtil.user();
            Todo todo = TodoMockDataUtil.todo();
            ReflectionTestUtils.setField(todo, "user", null);

            given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
            given(todoRepository.findById(anyLong())).willReturn(Optional.of(todo));

            // when
            InvalidRequestException exception =
                    assertThrows(InvalidRequestException.class,
                        () -> managerService.deleteManager(userId, todoId, managerId)
                    );

            // then
            assertEquals("해당 일정을 만든 유저가 유효하지 않습니다.", exception.getMessage());
        }

        @Test
        @DisplayName("현재 user와 todo를 만든 user가 같지 않아 manager 삭제에 실패한다.")
        void deleteManager_userIsNotEqualTodoUser_failure() {
            // given
            long userId = 2L;
            long todoId = 1L;
            long managerId = 1L;

            User user = UserMockDataUtil.user(userId);
            Todo todo = TodoMockDataUtil.todo();

            given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
            given(todoRepository.findById(anyLong())).willReturn(Optional.of(todo));

            // when
            InvalidRequestException exception =
                    assertThrows(InvalidRequestException.class,
                            () -> managerService.deleteManager(userId, todoId, managerId)
                    );

            // then
            assertEquals("해당 일정을 만든 유저가 유효하지 않습니다.", exception.getMessage());
        }

        @Test
        @DisplayName("manager 가 존재하지 않아 manager 삭제에 실패한다.")
        void deleteManager_notFoundManager_failure() {
            // given
            long userId = 1L;
            long todoId = 1L;
            long managerId = 1L;

            User user = UserMockDataUtil.user();
            Todo todo = TodoMockDataUtil.todo();

            given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
            given(todoRepository.findById(anyLong())).willReturn(Optional.of(todo));
            given(managerRepository.findById(anyLong())).willReturn(Optional.empty());

            // when
            InvalidRequestException exception =
                    assertThrows(InvalidRequestException.class,
                            () -> managerService.deleteManager(userId, todoId, managerId)
                    );

            // then
            assertEquals("Manager not found", exception.getMessage());
        }

        @Test
        @DisplayName("todo에 등록된 manager 가 아니라 manager 삭제에 실패한다.")
        void deleteManager_invalidManager_failure() {
            // given
            long userId = 1L;
            long todoId = 2L;
            long managerId = 1L;

            User user = UserMockDataUtil.user();
            Todo todo = TodoMockDataUtil.todo(todoId);
            Manager manager = ManagerMockDataUtil.manager();

            given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
            given(todoRepository.findById(anyLong())).willReturn(Optional.of(todo));
            given(managerRepository.findById(anyLong())).willReturn(Optional.of(manager));

            // when
            InvalidRequestException exception =
                    assertThrows(InvalidRequestException.class,
                            () -> managerService.deleteManager(userId, todoId, managerId)
                    );

            // then
            assertEquals("해당 일정에 등록된 담당자가 아닙니다.", exception.getMessage());
        }

        @Test
        @DisplayName("manager 삭제에 성공한다.")
        void deleteManager_success() {
            // given
            long userId = 1L;
            long todoId = 2L;
            long managerId = 1L;

            User user = UserMockDataUtil.user();
            Todo todo = TodoMockDataUtil.todo();
            Manager manager = ManagerMockDataUtil.manager();

            given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
            given(todoRepository.findById(anyLong())).willReturn(Optional.of(todo));
            given(managerRepository.findById(anyLong())).willReturn(Optional.of(manager));

            // when
            managerService.deleteManager(userId, todoId, managerId);

            // then
            verify(managerRepository, times(1)).delete(manager);
        }
    }

}
