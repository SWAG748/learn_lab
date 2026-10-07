package com.learnlab;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthAndCourseTest {

  @Autowired
  private MockMvc mockMvc;

  @Test
  void registerLoginAndCreateCourse() throws Exception {
    String body = """
        {"username":"ada2","email":"ada2@learnlab.local","password":"secret12"}
        """;
    MvcResult registered = mockMvc.perform(post("/api/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(body))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.token").isNotEmpty())
        .andExpect(jsonPath("$.data.user.role").value("USER"))
        .andReturn();
    String token = JsonPath.read(registered.getResponse().getContentAsString(), "$.data.token");

    mockMvc.perform(get("/api/auth/me").header("Authorization", "Bearer " + token))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.username").value("ada2"));

    mockMvc.perform(post("/api/courses")
            .header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"title\":\"JPA 练习\",\"summary\":\"关系映射\"}"))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.data.title").value("JPA 练习"))
        .andExpect(jsonPath("$.data.ownerName").value("ada2"));
  }

  @Test
  void rejectInvalidRegister() throws Exception {
    mockMvc.perform(post("/api/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"username\":\"ab\",\"email\":\"bad\",\"password\":\"123\"}"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
        .andExpect(jsonPath("$.fields.username").exists())
        .andExpect(jsonPath("$.fields.email").exists());
  }

  @Test
  void anonymousCannotListCourses() throws Exception {
    mockMvc.perform(get("/api/courses"))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
  }
}
