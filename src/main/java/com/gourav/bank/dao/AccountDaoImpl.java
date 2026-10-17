package com.gourav.bank.dao;

import com.gourav.bank.model.Account;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.Optional;

@Repository
public class AccountDaoImpl implements AccountDao {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Account save(Account account) {
        if (account.getId() == null) {
            em.persist(account);
            return account;
        }
        return em.merge(account);
    }

    @Override
    public boolean existsByNumber(String accountNumber) {
        Long count = em.createQuery(
                "select count(a) from Account a where a.accountNumber = :n", Long.class)
                .setParameter("n", accountNumber)
                .getSingleResult();
        return count > 0;
    }

    @Override
    public Optional<Account> findByNumber(String accountNumber) {
        return query(accountNumber).getResultList().stream().findFirst();
    }

    @Override
    public Optional<Account> findByNumberForUpdate(String accountNumber) {
        TypedQuery<Account> q = query(accountNumber);
        q.setLockMode(LockModeType.PESSIMISTIC_WRITE);
        return q.getResultList().stream().findFirst();
    }

    private TypedQuery<Account> query(String accountNumber) {
        return em.createQuery("select a from Account a where a.accountNumber = :n", Account.class)
                .setParameter("n", accountNumber);
    }
}
