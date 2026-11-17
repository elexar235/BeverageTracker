package cz.vsfs.danilalisitskii.beveragetracker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.vsfs.danilalisitskii.beveragetracker.controller.dto.UserCreateDto;
import cz.vsfs.danilalisitskii.beveragetracker.controller.dto.UserDto;
import cz.vsfs.danilalisitskii.beveragetracker.controller.dto.mappers.UserMapper;
import cz.vsfs.danilalisitskii.beveragetracker.entity.User;
import cz.vsfs.danilalisitskii.beveragetracker.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc; 

    @MockBean
    private UserService userService; 

    @MockBean
    private UserMapper userMapper; 

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void findAll_ShouldReturnList() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setUsername("testuser");

        when(userService.findAll()).thenReturn(List.of(user));
        when(userMapper.toDto(user)).thenReturn(userDto);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("testuser"))
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void create_ShouldReturnCreatedUser() throws Exception {
        UserCreateDto createDto = new UserCreateDto();
        createDto.setUsername("newuser");
        createDto.setEmail("new@test.com");

        User user = new User();
        user.setUsername("newuser");
        
        User createdUser = new User();
        createdUser.setId(1L);
        createdUser.setUsername("newuser");

        UserDto responseDto = new UserDto();
        responseDto.setId(1L);
        responseDto.setUsername("newuser");

        when(userMapper.toEntity(any(UserCreateDto.class))).thenReturn(user);
        when(userService.create(user)).thenReturn(createdUser);
        when(userMapper.toDto(createdUser)).thenReturn(responseDto);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("newuser"));
    }
}
