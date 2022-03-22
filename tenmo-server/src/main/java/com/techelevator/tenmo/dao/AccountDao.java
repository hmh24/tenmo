package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;

import java.math.BigDecimal;
import java.util.List;

public interface AccountDao {
    BigDecimal getBalance(int accountId);
    Account getAccountById(int accountId);
    List<Account> allAccounts();
    int getAccountIdByUserId(int userId);
}
