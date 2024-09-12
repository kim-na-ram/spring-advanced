package org.example.expert.domain.todo.controller;

import org.example.expert.domain.data.todo.TodoMockDataUtil;
import org.example.expert.domain.todo.dto.request.TodoSaveRequest;
import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.dto.response.TodoSaveResponse;
import org.example.expert.domain.todo.service.TodoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.example.expert.utils.TestUtils.toJsonString;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TodoController.class)
class TodoControllerTest {
    @MockBean
    private TodoService todoService;

    @Autowired
    private TodoController todoController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(todoController).build();
    }

    @Nested
    @DisplayName("todo 등록 테스트 케이스")
    public class SaveTodo {
        @Test
        @DisplayName("제목란이 비어있어 todo 등록에 실패한다.")
        void saveTodo_blankTitle_failure() throws Exception {
            // given
            TodoSaveRequest todoSaveRequest = TodoMockDataUtil.todoSaveRequest(null, "contents");

            // when, then
            mockMvc.perform(post("/todos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .content(toJsonString(todoSaveRequest)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("내용란이 비어있어 todo 등록에 실패한다.")
        void saveTodo_blankContents_failure() throws Exception {
            // given
            TodoSaveRequest todoSaveRequest = TodoMockDataUtil.todoSaveRequest("title", null);

            // when, then
            mockMvc.perform(post("/todos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .content(toJsonString(todoSaveRequest)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("todo 등록에 성공하다.")
        void saveTodo_success() throws Exception {
            // given
            TodoSaveRequest todoSaveRequest = TodoMockDataUtil.todoSaveRequest();

            TodoSaveResponse todoSaveResponse = TodoMockDataUtil.todoSaveResponse();

            given(todoService.saveTodo(any(), any())).willReturn(todoSaveResponse);

            // when, then
            mockMvc.perform(post("/todos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .content(toJsonString(todoSaveRequest)))
                    .andExpect(status().isOk())
                    .andExpect(content().string(toJsonString(todoSaveResponse)));
        }
    }

    @Nested
    @DisplayName("todo 목록 조회 테스트 케이스")
    public class GetTodoList {
        @Test
        @DisplayName("todo 목록 조회에 성공한다.")
        public void getTodoList_success() throws Exception {
            // given
            Page<TodoResponse> todoResponsePage = TodoMockDataUtil.todoResponsePage();

            given(todoService.getTodos(anyInt(), anyInt())).willReturn(todoResponsePage);

            // when, then
            mockMvc.perform(get("/todos")
                            .param("page", "2")
                            .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(content().string(toJsonString(todoResponsePage)));
        }
    }

    @Nested
    @DisplayName("todo 조회 테스트 케이스")
    public class GetTodo {
        @Test
        @DisplayName("todo 조회에 성공한다.")
        public void getTodo_success() throws Exception {
            // given
            long todoId = 1L;

            TodoResponse todoResponse = TodoMockDataUtil.todoResponse();

            given(todoService.getTodo(anyLong())).willReturn(todoResponse);

            // when, then
            mockMvc.perform(get("/todos/{todoId}", todoId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().string(toJsonString(todoResponse)));
        }
    }
}