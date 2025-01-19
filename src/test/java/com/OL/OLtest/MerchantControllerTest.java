package com.OL.OLtest;

import com.OL.OLtest.model.Merchant;
import com.OL.OLtest.repository.MerchantRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class MerchantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MerchantRepository merchantRepository;

    @Test
    public void testCreateMerchant() throws Exception {
        String merchantJson = """
            {
                "businessName": "Test Merchant",
                "phone": "1234567890",
                "email": "test@merchant.com",
                "status": "Active"
            }
        """;

        mockMvc.perform(post("/api/merchants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(merchantJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.businessName").value("Test Merchant"))
                .andDo(print());
    }

    @Test
    public void testGetMerchantById() throws Exception {
        Merchant merchant = new Merchant();
        merchant.setBusinessName("Test Merchant");
        merchant.setPhone("1234567890");
        merchant.setEmail("test@merchant.com");
        merchant.setStatus("Active");
        merchant = merchantRepository.save(merchant);

        mockMvc.perform(get("/api/merchants/" + merchant.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.businessName").value("Test Merchant"))
                .andDo(print());
    }

    @Test
    public void testDeleteMerchant() throws Exception {
        Merchant merchant = new Merchant();
        merchant.setBusinessName("Test Merchant");
        merchant.setPhone("1234567890");
        merchant.setEmail("test@merchant.com");
        merchant.setStatus("Active");
        merchant = merchantRepository.save(merchant);

        mockMvc.perform(delete("/api/merchants/" + merchant.getId()))
                .andExpect(status().isNoContent())
                .andDo(print());
    }
}
