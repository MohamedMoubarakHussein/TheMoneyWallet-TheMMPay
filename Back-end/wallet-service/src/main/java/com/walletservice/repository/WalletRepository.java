package com.walletservice.repository;

import com.themoneywallet.sharedUtilities.enums.CurrencyType;
import com.themoneywallet.sharedUtilities.enums.WalletTypes;
import com.walletservice.entity.Wallet;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

public interface WalletRepository extends CrudRepository<Wallet, Long> {
    boolean existsByUserIdAndWalletTypeAndCurrency(UUID userId, WalletTypes walletType, CurrencyType currency);
    long countByUserId(UUID userId);
    Optional<Wallet> findByUserIdAndWalletId(UUID userId, UUID walletId);
    List<Wallet> findAllByUserId(UUID userId);



}
