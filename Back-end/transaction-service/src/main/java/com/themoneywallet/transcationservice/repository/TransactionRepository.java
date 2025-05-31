package com.themoneywallet.transcationservice.repository;

import com.themoneywallet.transcationservice.entity.Transaction;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository
    extends CrudRepository<Transaction, String> {
    List<Transaction> findAllByFromWalletId(String id);

    @Query(
        "SELECT t FROM Transaction t WHERE t.id = :tansactionId AND t.fromWalletId = :fromWalletId"
    )
    Transaction findByUserIdAndId(
        @Param("fromWalletId") String fromWalletId,
        @Param("tansactionId") String tansactionId
    );
}
