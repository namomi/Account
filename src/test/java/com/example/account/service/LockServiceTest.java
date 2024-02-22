package com.example.account.service;

import com.example.account.domain.Account;
import com.example.account.exception.AccountException;
import com.example.account.type.ErrorCode;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.rmi.AccessException;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class LockServiceTest {
    @Mock
    private RedissonClient redissonClient;

    @Mock
    private RLock rLock;

    @InjectMocks
    private LockService lockService;

    @Test
    public void successGetLock() throws Exception{
        //given
        given(redissonClient.getLock(anyString()))
                .willReturn(rLock);
        given(rLock.tryLock(anyLong(), anyLong(), any()))
                .willReturn(true);

        //when
        //then
        assertDoesNotThrow(()-> lockService.lock("123"));
    }

    @Test
    public void failLock() throws InterruptedException{
        //given
        given(redissonClient.getLock(anyString()))
                .willReturn(rLock);
        given(rLock.tryLock(anyLong(), anyLong(), any()))
                .willReturn(false);

        //when
        AccountException exception = assertThrows(AccountException.class, () -> lockService.lock("123"));
        //then
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.ACCOUNT_TRANSACTION_LOCK);
    }
}