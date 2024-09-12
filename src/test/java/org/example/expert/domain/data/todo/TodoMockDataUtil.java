package org.example.expert.domain.data.todo;

import org.example.expert.domain.data.user.UserMockDataUtil;
import org.example.expert.domain.todo.dto.request.TodoSaveRequest;
import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.dto.response.TodoSaveResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TodoMockDataUtil {
    public static Todo todo() {
        User user = UserMockDataUtil.userFromAuthUser();
        Todo todo = new Todo("title", "title", "weather", user);
        ReflectionTestUtils.setField(todo, "id", 1L);
        return todo;
    }

    public static Todo todo(long todoId) {
        User user = UserMockDataUtil.userFromAuthUser();
        Todo todo = new Todo("title", "title", "weather", user);
        ReflectionTestUtils.setField(todo, "id", todoId);
        return todo;
    }

    public static Page<Todo> todoPage() {
        User user = UserMockDataUtil.userFromAuthUser();
        List<Todo> todoList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            todoList.add(new Todo("title", "title", "weather", user));
        }

        return new PageImpl<>(todoList);
    }

    public static TodoSaveRequest todoSaveRequest() {
        return new TodoSaveRequest("title", "contents");
    }

    public static TodoSaveRequest todoSaveRequest(String title, String contents) {
        return new TodoSaveRequest(title, contents);
    }

    public static TodoSaveResponse todoSaveResponse() {
        UserResponse userResponse = UserMockDataUtil.userResponse();
        return new TodoSaveResponse(1L, "title", "contents", "weather", userResponse);
    }

    public static Page<TodoResponse> todoResponsePage() {
        UserResponse userResponse = UserMockDataUtil.userResponse();
        List<TodoResponse> todoList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            todoList.add(new TodoResponse((long) i, "title", "title", "weather", userResponse, LocalDateTime.now(), LocalDateTime.now()));
        }

        Pageable pageable = PageRequest.of(1, 10);
        Page<TodoResponse> pageResponse = new PageImpl<>(todoList, pageable, todoList.size());
        return pageResponse;
    }

    public static TodoResponse todoResponse() {
        UserResponse userResponse = UserMockDataUtil.userResponse();
        return new TodoResponse(1L, "title", "title", "weather", userResponse, LocalDateTime.now(), LocalDateTime.now());
    }

}
