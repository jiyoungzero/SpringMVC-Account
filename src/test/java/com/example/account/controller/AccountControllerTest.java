package com.example.account.controller;

import com.example.account.domain.Account;
import com.example.account.domain.AccountUser;
import com.example.account.dto.AccountDto;
import com.example.account.dto.CreateAccount;
import com.example.account.dto.DeleteAccount;
import com.example.account.exception.AccountException;
import com.example.account.repository.TransactionRepository;
import com.example.account.type.AccountStatus;
import com.example.account.service.AccountService;

import com.example.account.type.ErrorCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.mock.http.server.reactive.MockServerHttpRequest.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccountController.class)
class AccountControllerTest {
    @MockBean
    private AccountService accountService;


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

//    @Test
//    void successCreateAccount() throws Exception {
//        //given
//        given(accountService.createAccount(anyLong(), anyLong()))
//                .willReturn(AccountDto.builder()
//                        .userId(1L)
//                        .accountNumber("1234567890")
//                        .registedAt(LocalDateTime.now())
//                        .unregistedAt(LocalDateTime.now())
//                        .build());
//
//        //when
//        //then
//        mockMvc.perform(post("/account")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(
//                        new CreateAccount.Request(1L, 100L)
//                )))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.userId").value(1))
//                .andExpect(jsonPath("$.accountNumber").value("1234567890 "))
//                .andDo(print());
//
//
//    }

    @Test
    void successGetAccount() throws Exception {
        //given
        given(accountService.getAccount(anyLong()))
                .willReturn(Account.builder()
                        .accountNumber("3456")
                        .accountStatus(AccountStatus.IN_USE)
                        .build());

        //when
        //then
        mockMvc.perform(get("/account/876"))
                .andDo(print())
                .andExpect(jsonPath("$.accountNumber").value("3456"))
                .andExpect(jsonPath("$.accountStatus").value("IN_USE"))
                .andExpect(status().isOk());
    }


    @Test
    void successDeleteAccount() throws Exception {
        //given
        given(accountService.deleteAccount(anyLong(), anyString()))
                .willReturn(AccountDto.builder()
                        .userId(1L)
                        .accountNumber("1234567890")
                        .registedAt(LocalDateTime.now())
                        .unregistedAt(LocalDateTime.now())
                        .build());

        //when
        //then
        mockMvc.perform(delete("/account")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                        new DeleteAccount.Request(1L, "1111111111")
                )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.accountNumber").value("1234567890"))
                .andDo(print());


    }

    @Test
    void successGetAccountsByUserId() throws Exception{
        //given
        List<AccountDto> accountDtos =
                Arrays.asList(
                        AccountDto.builder()
                                .accountNumber("1234567890")
                                .balance(1000L).build(),
                        AccountDto.builder()
                                .accountNumber("111111111")
                                .balance(2000L).build(),
                        AccountDto.builder()
                                .accountNumber("222222222")
                                .balance(3000L).build()
                );

        given(accountService.getAccountByUserId(anyLong()))
                .willReturn(accountDtos);

        //when
        //then
        mockMvc.perform(get("/account?user_id=1"))
                .andDo(print())
                .andExpect(jsonPath("$[0].accountNumber").value("1234567890"))
                .andExpect(jsonPath("$[0].balance").value(1000));

    }


    @Test
    void failGetAccount() throws Exception {
        //given
        given(accountService.getAccount(anyLong()))
                .willThrow(new AccountException(ErrorCode.ACCOUNT_NOT_FOUND));

        //when
        //then
        mockMvc.perform(get("/account/876"))
                .andDo(print())
                .andExpect(jsonPath("$.errorCode").value("ACCOUNT_NOT_FOUND"))
                .andExpect(jsonPath("$.errorMessage").value("계좌가 없습니다."))
                .andExpect(status().isOk());
    }


}