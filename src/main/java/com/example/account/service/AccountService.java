package com.example.account.service;

import com.example.account.domain.Account;
import com.example.account.domain.AccountUser;
import com.example.account.dto.AccountDto;
import com.example.account.exception.AccountException;
import com.example.account.repository.AccountRepository;
import com.example.account.repository.AccountUserRepository;
import com.example.account.type.AccountStatus;
import com.example.account.type.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static com.example.account.type.AccountStatus.IN_USE;
import static com.example.account.type.ErrorCode.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final AccountUserRepository accountUserRepository;
    /**
     *  사용자가 있는지 조회
     *  계좌의 번호를 생성하고
     *  계좌를 저장하고, 그 정보를 넘김
     */
    @Transactional
    public AccountDto createAccount(Long userId, Long initialBalance) {
        AccountUser accountUser = accountUserRepository.findById(userId)
                .orElseThrow(() -> new AccountException(USER_NOT_FOUND));

        if(accountRepository.countAccountUser(accountUser) == 10){
            throw new AccountException(ErrorCode.MAX_ACCOUNT_PER_USER_10);
        }

        String newAccountNumber = accountRepository.findFirstByOrderByIdDesc()
                .map(account -> (Integer.parseInt(account.getAccountNumber())) + 1 + "")
                .orElse("1000000000");

        Account account =  accountRepository.save(
                Account.builder()
                        .accountUser(accountUser)
                        .accountStatus(IN_USE)
                        .accountNumber(newAccountNumber)
                        .balance(initialBalance)
                        .registeredAt(LocalDateTime.now())
                        .build()
        );

        return AccountDto.fromEntity(account);
    }


    @Transactional
    public Account getAccount(Long id) {
        if(id < 0){
            throw new RuntimeException("Minus");
        }
        return accountRepository.findById(id).get();
    }


    @Transactional
    public AccountDto deleteAccount(Long userId, String accountNumber) {
        AccountUser accountUser = accountUserRepository.findById(userId)
                .orElseThrow(() -> new AccountException(USER_NOT_FOUND));

        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountException(ErrorCode.ACCOUNT_NOT_FOUND));

        validateDeleteAccount(accountUser, account);

        // testcase를 위해 저장
        accountRepository.save(account);

        account.setAccountStatus(AccountStatus.UNREGISTERED);
        account.setUnregisteredAt(LocalDateTime.now());

        return AccountDto.fromEntity(account);
    }

    private void validateDeleteAccount(AccountUser accountUser, Account account) {
        if(!Objects.equals(accountUser.getId() , account.getAccountUser().getId())){
            throw new AccountException(ErrorCode.USER_ACCOUNT_UNMATCHED);
        }

        if(account.getAccountStatus() == AccountStatus.UNREGISTERED){
            throw new AccountException(ErrorCode.ACCOUNT_ALREADY_UNREGISTERED);
        }

        if(account.getBalance() > 0){
            throw new AccountException(ErrorCode.BALANCE_NOT_EMPTY);
        }
    }

    @Transactional
    public List<AccountDto> getAccountByUserId(Long userId) {
        AccountUser accountUser = accountUserRepository.findById(userId)
                .orElseThrow(()-> new AccountException(USER_NOT_FOUND));

       List<Account> accounts = accountRepository
               .findByAccountUser(accountUser);

       return accounts.stream()
               .map(account -> AccountDto.fromEntity(account))
               .collect(Collectors.toList());

    }
}
