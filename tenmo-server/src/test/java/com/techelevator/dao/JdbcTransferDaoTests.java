package com.techelevator.dao;

import com.techelevator.tenmo.dao.JdbcAccountDao;
import com.techelevator.tenmo.dao.JdbcTransferDao;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.security.SecurityUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class JdbcTransferDaoTests extends BaseDaoTests {

    private static final Account ACCOUNT_1 = new Account(new BigDecimal("1000.00"), 1001, 2001);
    private static final Account ACCOUNT_2 = new Account(new BigDecimal("500.00"), 1002, 2002);
    private static final Account ACCOUNT_3 = new Account(new BigDecimal("100.00"), 1003, 2003);

    private static final Transfer TRANSFER_1 = new Transfer(new BigDecimal("100.00"), 2001, 2002);
    private static final Transfer TRANSFER_2 = new Transfer(new BigDecimal("500.00"), 2003, 2002);
    private static final Transfer TRANSFER_3 = new Transfer(new BigDecimal("200.00"), 2002, 2003);


    private JdbcTransferDao sut;
    private JdbcAccountDao sut2;

    @Before
    public void setup() {
        sut = new JdbcTransferDao(dataSource);
        sut2 = new JdbcAccountDao(dataSource);
    }

    @Test
    public void get_transfer_by_accountId_returns_all_transfer_for_accountId() {
        List<Transfer> listTransfers = new ArrayList<>();
        listTransfers.add(TRANSFER_1);
        listTransfers.add(TRANSFER_2);
        Assert.assertEquals("method should return all transfers associated with accountId 2002", listTransfers,
                sut.getTransfersByUserId(1002));
    }

    @Test
    public void get_transfer_byId_returns_transfer_for_that_id() {
        TRANSFER_1.setTransferId(3001);
        Assert.assertEquals("method should return transfer with transferid 3001", TRANSFER_1,
                sut.getTransferById(3002));
    }

    @Test
    public void send_transfer_updates_accounts_correctly() {
        sut.sendTransfer(TRANSFER_1);

        BigDecimal accountFromBalance = sut2.getBalance(1001);
        BigDecimal accountToBalance = sut2.getBalance(1002);
        Assert.assertEquals("sending account balance should be correctly decreased",
                new BigDecimal("900.00"), accountFromBalance);

        Assert.assertEquals("receiving account balance should be correctly increased",
                new BigDecimal("600.00"), accountToBalance);
    }

}
