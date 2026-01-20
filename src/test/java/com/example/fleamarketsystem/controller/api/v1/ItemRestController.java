package com.example.fleamarketsystem.controller.api.v1;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class ItemRestControllerTest {

    @Autowired
    MockMvc mockMvc;

    private static final String BASE_URL = "/api/v1/items";

    private static final String EMAIL = "adminC@example.com";
    private static final String PASSWORD = "adminpass";

    /* ======================
     * 一覧取得（認証不要）
     * ====================== */
    @Test
    void get_items_list_ok() throws Exception {
        mockMvc.perform(get(BASE_URL))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.content").isArray());
    }

    /* ======================
     * 詳細取得（認証不要）
     * ====================== */
    @Test
    void get_item_detail_ok() throws Exception {
        mockMvc.perform(get(BASE_URL + "/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.name").exists());
    }

    /* ======================
     * 新規作成（認証必須）
     * ====================== */
    @Test
    void create_item_ok() throws Exception {
        String json = """
            {
              "name": "テスト商品",
              "description": "JUnit から作成",
              "price": 1500,
              "categoryId": 1
            }
            """;

        mockMvc.perform(
                post(BASE_URL)
                    .with(httpBasic(EMAIL, PASSWORD))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.name").value("テスト商品"))
            .andExpect(jsonPath("$.price").value(1500));
    }

    /* ======================
     * 更新（本人のみ）
     * ====================== */
    @Test
    void update_item_ok() throws Exception {
        String json = """
            {
              "name": "更新後商品",
              "description": "説明更新",
              "price": 2000,
              "categoryId": 1
            }
            """;

        mockMvc.perform(
                put(BASE_URL + "/1")
                    .with(httpBasic(EMAIL, PASSWORD))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("更新後商品"))
            .andExpect(jsonPath("$.price").value(2000));
    }

    /* ======================
     * 削除（本人のみ）
     * ====================== */
    @Test
    void delete_item_ok() throws Exception {
        mockMvc.perform(
                delete(BASE_URL + "/1")
                    .with(httpBasic(EMAIL, PASSWORD))
            )
            .andExpect(status().isOk());
    }

    /* ======================
     * 未認証は 401
     * ====================== */
    @Test
    void create_item_unauthorized() throws Exception {
        String json = """
            {
              "name": "失敗商品",
              "description": "未認証",
              "price": 1000,
              "categoryId": 1
            }
            """;

        mockMvc.perform(
                post(BASE_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json)
            )
            .andExpect(status().isUnauthorized());
    }
}

