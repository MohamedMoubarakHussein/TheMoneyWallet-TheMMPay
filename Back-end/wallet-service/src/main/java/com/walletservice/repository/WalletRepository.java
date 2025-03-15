package com.walletservice.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.walletservice.entity.Wallet;

public interface WalletRepository  extends CrudRepository<Wallet, Long>{
     List<Wallet> findAllByUserId(long userId);
}
