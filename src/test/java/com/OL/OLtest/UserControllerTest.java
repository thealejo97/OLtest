package com.OL.OLtest;

import com.OL.OLtest.model.User;
import com.OL.OLtest.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testCreateUser() throws Exception {
        String userJson = """
            {
                "username": "testuser",
                "password": "password123",
                "email": "testuser@test.com",
                "role": "ADMIN"
            }
        """;

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andDo(print());
    }

    @Test
    public void testGetUserById() throws Exception {
        User user = new User();
        user.setPassword("password123");
        user.setEmail("testuser@test.com");
        user.setRole("ADMIN");
        user = userRepository.save(user);

        mockMvc.perform(get("/api/users/" + user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andDo(print());
    }

    @Test
    public void testDeleteUser() throws Exception {
        User user = new User();
        user.setPassword("password123");
        user.setEmail("testuser@test.com");
        user.setRole("ADMIN");
        user = userRepository.save(user);

        mockMvc.perform(delete("/api/users/" + user.getId()))
                .andExpect(status().isNoContent())
                .andDo(print());
    }
}
