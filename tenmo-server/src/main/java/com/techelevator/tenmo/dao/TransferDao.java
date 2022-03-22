package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;

import java.math.BigDecimal;
import java.util.List;

public interface TransferDao {
    List<Transfer> getTransfersByUserId(int userId);

    Transfer getTransferById(int transferId);

    void sendTransfer(Transfer transferToSent);

    Transfer createTransfer(Transfer newTransfer);

    int getAccountIdByUserId(int userId);
}
