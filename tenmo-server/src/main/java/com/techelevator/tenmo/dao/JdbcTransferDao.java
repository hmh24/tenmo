package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcTransferDao implements TransferDao {

    @Autowired

    private JdbcTemplate jdbcTemplate;

    public JdbcTransferDao() {}

    public JdbcTransferDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public JdbcTransferDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public int getAccountIdByUserId(int userId) {
        String getAccountFromUserID = "SELECT account_id FROM account WHERE user_id = ?";
        Integer accountId = jdbcTemplate.queryForObject(getAccountFromUserID, Integer.class,
                userId);
        return (int)accountId;
    }

    @Override
    public List<Transfer> getTransfersByUserId(int userId) {
        int accountId = getAccountIdByUserId(userId);

        String query = "SELECT * FROM transfer WHERE account_from = ? " +
                "OR account_to = ?;";
        List<Transfer> transferList = new ArrayList<>();
        SqlRowSet result = jdbcTemplate.queryForRowSet(query, accountId, accountId);
        while (result.next()) {
            transferList.add(mapRowToTransfer(result));
        }
        return transferList;
    }

    @Override
    public Transfer getTransferById(int transferId) {
        Transfer transfer = null;
        String query = "SELECT * FROM transfer WHERE transfer_id = ?;";
        SqlRowSet result = jdbcTemplate.queryForRowSet(query, transferId);
        if (result.next()) {
            transfer = mapRowToTransfer(result);
        }
        return transfer;
    }

    @Override
    public void sendTransfer(Transfer transferToSend) {
        BigDecimal amountToTransfer = transferToSend.getAmount();

        String query = "UPDATE account SET balance = balance - ? WHERE account_id = ?;";
        jdbcTemplate.update(query, amountToTransfer, transferToSend.getAccountFrom());

        query = "UPDATE account SET balance = balance + ? WHERE account_id = ?;";
        jdbcTemplate.update(query, amountToTransfer, transferToSend.getAccountTo());
    }

    @Override
    public Transfer createTransfer(Transfer newTransfer) {
        int accountFromID = getAccountIdByUserId(newTransfer.getAccountFrom());
        int accountToID = getAccountIdByUserId(newTransfer.getAccountTo());


        String query = "INSERT INTO transfer (transfer_type_id, transfer_status_id, " +
                "account_from, account_to, amount) VALUES (2, ?, ?, ?, ?) RETURNING transfer_id;";
        Integer newId = jdbcTemplate.queryForObject(query, Integer.class, newTransfer.getTransferStatusId(),
                accountFromID, accountToID, newTransfer.getAmount());
        return getTransferById(newId);
    }

    public Transfer mapRowToTransfer(SqlRowSet result) {
        Transfer transfer = new Transfer();
        transfer.setTransferId(result.getInt("transfer_id"));
        transfer.setTransferTypeId(result.getInt("transfer_type_id"));
        transfer.setTransferStatusId(result.getInt("transfer_status_id"));
        transfer.setAccountFrom(result.getInt("account_from"));
        transfer.setAccountTo(result.getInt("account_to"));
        transfer.setAmount(result.getBigDecimal("amount"));
        return transfer;
    }
}
