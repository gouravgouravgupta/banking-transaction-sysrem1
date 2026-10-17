package com.gourav.bank.dao;

import com.gourav.bank.model.BankTransaction;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class TransactionDaoImpl implements TransactionDao {

    @PersistenceContext
    private EntityManager em;

    @Override
    public BankTransaction save(BankTransaction txn) {
        em.persist(txn);
        return txn;
    }

    @Override
    public List<BankTransaction> findByAccount(String accountNumber, int limit) {
        return em.createQuery(
                "select t from BankTransaction t " +
                "where t.fromAccount = :n or t.toAccount = :n order by t.id desc", BankTransaction.class)
                .setParameter("n", accountNumber)
                .setMaxResults(limit)
                .getResultList();
    }
}
