package com.OL.OLtest;

import com.OL.OLtest.model.Establishment;
import com.OL.OLtest.model.Merchant;
import com.OL.OLtest.repository.EstablishmentRepository;
import com.OL.OLtest.repository.MerchantRepository;
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
public class EstablishmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MerchantRepository merchantRepository;

    @Autowired
    private EstablishmentRepository establishmentRepository;

    @Test
    public void testCreateEstablishment() throws Exception {
        Merchant merchant = new Merchant();
        merchant.setBusinessName("Merchant Test");
        merchant.setPhone("123456789");
        merchant.setEmail("merchant@test.com");
        merchant.setStatus("Active");
        merchant = merchantRepository.save(merchant);

        String establishmentJson = """
            {
                "name": "Establishment Test",
                "address": "123 Test Street",
                "merchant": {
                    "id": %d
                },
                "employeeCount": 10
            }
        """.formatted(merchant.getId());

        mockMvc.perform(post("/api/establishments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(establishmentJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Establishment Test"))
                .andDo(print());
    }

    @Test
    public void testGetEstablishmentById() throws Exception {
        Merchant merchant = new Merchant();
        merchant.setBusinessName("Merchant Test");
        merchant.setPhone("123456789");
        merchant.setEmail("merchant@test.com");
        merchant.setStatus("Active");
        merchant = merchantRepository.save(merchant);

        Establishment establishment = new Establishment();
        establishment.setName("Test Establishment");
        establishment.setAddress("123 Test Street");
        establishment.setMerchant(merchant);
        establishment.setEmployeeCount(10);
        establishment = establishmentRepository.save(establishment);

        mockMvc.perform(get("/api/establishments/" + establishment.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Establishment"))
                .andDo(print());
    }

    @Test
    public void testDeleteEstablishment() throws Exception {
        Merchant merchant = new Merchant();
        merchant.setBusinessName("Merchant Test");
        merchant.setPhone("123456789");
        merchant.setEmail("merchant@test.com");
        merchant.setStatus("Active");
        merchant = merchantRepository.save(merchant);

        Establishment establishment = new Establishment();
        establishment.setName("Test Establishment");
        establishment.setAddress("123 Test Street");
        establishment.setMerchant(merchant);
        establishment.setEmployeeCount(10);
        establishment = establishmentRepository.save(establishment);

        mockMvc.perform(delete("/api/establishments/" + establishment.getId()))
                .andExpect(status().isNoContent())
                .andDo(print());
    }
}
