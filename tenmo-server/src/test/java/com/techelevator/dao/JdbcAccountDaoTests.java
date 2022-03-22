package com.techelevator.dao;

import com.techelevator.tenmo.dao.JdbcAccountDao;
import com.techelevator.tenmo.model.Account;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class JdbcAccountDaoTests extends BaseDaoTests {

    private static final Account ACCOUNT_1 = new Account(new BigDecimal("1000.00"), 1001, 2001);
    private static final Account ACCOUNT_2 = new Account(new BigDecimal("500.00"), 1002, 2002);
    private static final Account ACCOUNT_3 = new Account(new BigDecimal("100.00"), 1003, 2003);

    private JdbcAccountDao sut;

    @Before
    public void setup() {
        sut = new JdbcAccountDao(dataSource);
    }

    @Test
    public void get_balance_returns_correct_balance() {
        BigDecimal balance = sut.getBalance(1001);
        Assert.assertEquals("Should return same balance",
                new BigDecimal("1000.00"), balance);

        balance = sut.getBalance(1002);
        Assert.assertEquals("Should return same balance",
                new BigDecimal("500.00"), balance);

        balance = sut.getBalance(1003);
        Assert.assertEquals("Should return same balance",
                new BigDecimal("100.00"), balance);
    }

    @Test
    public void get_account_by_id_should_return_correct_account_id() {

        Assert.assertEquals("Should return the same account number",
                ACCOUNT_1, sut.getAccountById(2001));

        Assert.assertEquals("Should return the same account number",
                ACCOUNT_2, sut.getAccountById(2002));

        Assert.assertEquals("Should return the same account number",
                ACCOUNT_3, sut.getAccountById(2003));

    }

    @Test
    public void all_accounts_should_return_correct_account_list() {
        List<Account> testAccountList = new ArrayList<>();
        testAccountList.add(ACCOUNT_1);
        testAccountList.add(ACCOUNT_2);
        testAccountList.add(ACCOUNT_3);

        Assert.assertEquals("Should return matching arrayList of Accounts",
                testAccountList, sut.allAccounts());
    }

}
