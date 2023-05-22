package com.example.account.dto;

import com.example.account.domain.Account;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import lombok.*;

import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountDto {

        private Long userId;
        private String accountNumber;
        private Long balance;

        private LocalDateTime registedAt;
        private LocalDateTime unregistedAt;


        public static AccountDto fromEntity(Account account) {
            return AccountDto.builder()
                    .userId(account.getAccountUser().getId())
                    .accountNumber(account.getAccountNumber())
                    .balance(account.getBalance())
                    .registedAt(account.getRegisteredAt())
                    .unregistedAt(account.getUnregisteredAt())
                    .build();
        }
}

