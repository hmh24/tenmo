package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.JdbcAccountDao;
import com.techelevator.tenmo.dao.JdbcTransferDao;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;

@RestController
@PreAuthorize("isAuthenticated()")
public class TenmoController {

    @Autowired
    private JdbcAccountDao accountDao;

    @Autowired
    private JdbcTransferDao transferDao;

    @RequestMapping(path="/account/{id}/balance", method=RequestMethod.GET)
    public BigDecimal getBalance(@PathVariable int id) {
        return accountDao.getBalance(id);
    }

    @RequestMapping(path="/accounts", method=RequestMethod.GET)
    public List<Account> getAllAccounts() {
        return accountDao.allAccounts();
    }

    @RequestMapping(path="/account/{id}", method=RequestMethod.GET)
    public Account getAccountById(@PathVariable int id) {
        return accountDao.getAccountById(id);
    }

    @RequestMapping(path="/account/{id}/transfers", method=RequestMethod.GET)
    public List<Transfer> getAllTransfersByUserId(@PathVariable int id) {
        return transferDao.getTransfersByUserId(id);
    }

    @RequestMapping(path="/transfer/{id}", method=RequestMethod.GET)
    public Transfer getTransferByTransferId(@PathVariable int id) {
        return transferDao.getTransferById(id);
    }

    @RequestMapping(path="/transfer/create", method=RequestMethod.POST)
    public Transfer createTransfer(@Valid @RequestBody Transfer newTransfer) {
        int accountId = accountDao.getAccountIdByUserId(newTransfer.getAccountFrom());
        BigDecimal balance = accountDao.getAccountById(accountId).getBalance();

            if(balance.compareTo(newTransfer.getAmount()) < 0) {
                newTransfer.setTransferStatusId(3);
            } else if (newTransfer.getAccountFrom() == newTransfer.getAccountTo()) {
                newTransfer.setTransferStatusId(3);
            } else {
                newTransfer.setTransferStatusId(2);
        }

        return transferDao.createTransfer(newTransfer);
    }

    @RequestMapping(path="/transfer/sent", method=RequestMethod.PUT)
    public boolean executeTransfer(@Valid @RequestBody Transfer newTransfer) {
        BigDecimal balanceFrom = accountDao.getAccountById(newTransfer.getAccountFrom()).getBalance();
        BigDecimal balanceTo = accountDao.getAccountById(newTransfer.getAccountTo()).getBalance();

        transferDao.sendTransfer(newTransfer);

        BigDecimal updatedBalanceFrom = accountDao.getAccountById(newTransfer.getAccountFrom()).getBalance();
        BigDecimal updatedBalanceTo = accountDao.getAccountById(newTransfer.getAccountTo()).getBalance();

        return balanceFrom.subtract(newTransfer.getAmount()).compareTo(updatedBalanceFrom) == 0 &&
        balanceTo.add(newTransfer.getAmount()).compareTo(updatedBalanceTo) == 0;
    }
}
