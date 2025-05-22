package com.walletservice.repository;


import org.springframework.data.repository.CrudRepository;

import com.walletservice.entity.Wallet;

public interface WalletRepository  extends CrudRepository<Wallet, Long>{
     
}
