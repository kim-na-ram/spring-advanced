package org.example.expert.domain.todo.service;

import org.example.expert.client.WeatherClient;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.common.exception.ServerException;
import org.example.expert.domain.data.todo.TodoMockDataUtil;
import org.example.expert.domain.data.user.UserMockDataUtil;
import org.example.expert.domain.todo.dto.request.TodoSaveRequest;
import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.dto.response.TodoSaveResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class TodoServiceTest {
    @Mock
    private TodoRepository todoRepository;

    @Mock
    private WeatherClient weatherClient;

    @InjectMocks
    private TodoService todoService;

    @Nested
    @DisplayName("todo 등록 테스트 케이스")
    class SaveTodo {
        @Test
        @DisplayName("날씨 정보가 없어 todo 등록에 실패한다")
        void saveTodo_weather_failure() {
            // given
            AuthUser authUser = UserMockDataUtil.authUser();
            TodoSaveRequest todoSaveRequest = TodoMockDataUtil.todoSaveRequest();

            given(weatherClient.getTodayWeather()).willThrow(new ServerException("날씨 데이터가 없습니다."));

            // when
            ServerException exception =
                    assertThrows(ServerException.class, () -> todoService.saveTodo(authUser, todoSaveRequest));

            // then
            assertNotNull(exception);
        }

        @Test
        @DisplayName("todo 등록에 성공한다")
        void saveTodo_success() {
            // given
            AuthUser authUser = UserMockDataUtil.authUser();
            TodoSaveRequest todoSaveRequest = TodoMockDataUtil.todoSaveRequest();

            String weather = "Sunny";
            Todo todo = TodoMockDataUtil.todo();

            given(weatherClient.getTodayWeather()).willReturn(weather);
            given(todoRepository.save(any())).willReturn(todo);

            // when
            TodoSaveResponse todoSaveResponse = todoService.saveTodo(authUser, todoSaveRequest);

            // then
            assertNotNull(todoSaveResponse);
        }
    }

    @Nested
    @DisplayName("todo 목록 조회 테스트 케이스")
    class GetToDoList {
        @Test
        @DisplayName("todo 목록 조회에 성공한다.")
        void getTodos_success() {
            // given
            int page = 1;
            int size = 10;

            Pageable pageable = PageRequest.of(page - 1, size);
            Page<Todo> todo = TodoMockDataUtil.todoPage();

            given(todoRepository.findAllByOrderByModifiedAtDesc(pageable)).willReturn(todo);

            // when
            Page<TodoResponse> todoResponsePage = todoService.getTodos(page, size);

            // then
            assertEquals(10, todoResponsePage.getContent().size());
        }
    }

    @Nested
    @DisplayName("todo 조회 테스트 케이스")
    class GetTodo {
        @Test
        @DisplayName("todo가 존재하지 않아 조회에 실패한다.")
        void getTodo_notFoundTodo_failure() {
            // given
            long todoId = 1L;
            given(todoRepository.findByIdWithUser(anyLong())).willReturn(Optional.empty());

            // when
            InvalidRequestException exception =
                    assertThrows(InvalidRequestException.class, () -> todoService.getTodo(todoId));

            // then
            assertEquals("Todo not found", exception.getMessage());
        }

        @Test
        @DisplayName("todo 조회에 성공한다.")
        void getTodo_success() {
            // given
            long todoId = 1L;

            Todo todo = TodoMockDataUtil.todo();

            given(todoRepository.findByIdWithUser(anyLong())).willReturn(Optional.of(todo));

            // when
            TodoResponse todoResponse = todoService.getTodo(todoId);

            // then
            assertNotNull(todoResponse);
        }
    }
}