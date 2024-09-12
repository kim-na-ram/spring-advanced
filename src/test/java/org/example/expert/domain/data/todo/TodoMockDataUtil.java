package org.example.expert.domain.data.todo;

import org.example.expert.domain.data.user.UserMockDataUtil;
import org.example.expert.domain.todo.dto.request.TodoSaveRequest;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.util.ReflectionTestUtils;

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
}
