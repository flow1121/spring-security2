package com.example.demo.controller;

import static org.hamcrest.CoreMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import javax.transaction.Transactional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.example.demo.model.SiteUser;
import com.example.demo.util.Role;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class SecurityControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    @DisplayName("登録エラーがある時、エラー表示することを期待します")
    void whenThereIsRegistrationError_expectToSeeErrors()
            throws Exception {

        mockMvc
            // リクエストを実行します
            .perform(
                // HTTPのPOSTリクエストを使用します
                post("/register")
                // 入力の属性を設定します
                .flashAttr("user", new SiteUser())
                // CSRFトークンを自動挿入します
                .with(csrf())
            )
            // エラーがあることを検証します
            .andExpect(model().hasErrors())
            // 指定したHTMLを表示しているか検証します
            .andExpect(view().name("register"));
    }

    @Test
    @DisplayName("管理者ユーザとして登録する時、成功することを期待します")
    void whenRegisteringAsAdminUser_expectToSucceed()
            throws Exception {

        SiteUser user = new SiteUser();
        user.setUsername("管理者ユーザ");
        user.setPassword("password");
        user.setEmail("admin@example.com");
        user.setGender(0);
        user.setAdmin(true);
        user.setRole(Role.ADMIN.name());

        mockMvc.perform(post("/register")
            .flashAttr("user", user).with(csrf()))
            // エラーがないことを検証します
            .andExpect(model().hasNoErrors())
            // 指定したURLに、リダイレクトすることを検証します
            .andExpect(redirectedUrl("/login?register"))
            // ステータスコードが、Found（302）であることを検証します
            .andExpect(status().isFound());
    }

    @Test
    @DisplayName("管理者ユーザでログイン時、ユーザ一覧を表示することを期待します")
    @WithMockUser(username="admin", roles="ADMIN")
    void whenLoggedInAsAdminUser_expectToSeeListOfUsers()
            throws Exception {

        mockMvc.perform(get("/admin/list"))
            // ステータスコードが、OK（200）であることを検証します
            .andExpect(status().isOk())
            // HTMLの表示内容に、指定した文字列を含んでいるか検証します
            .andExpect(content().string(containsString("ユーザ一覧")))
            .andExpect(view().name("list"));
    }
}
