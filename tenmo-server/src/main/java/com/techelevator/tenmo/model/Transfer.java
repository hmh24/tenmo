package com.techelevator.tenmo.model;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Objects;

public class Transfer {
    private int transferId;
    private int transferTypeId;
    private int transferStatusId;

    @NotNull
    private int accountFrom;

    @NotNull
    private int accountTo;

    @NotNull
    @DecimalMin(message="transfer can't be zero or negative", value="0.01", inclusive = true)
    private BigDecimal amount;

    public Transfer() {}

    public Transfer(BigDecimal amount, int accountFrom, int accountTo) {
        this.amount = amount;
        this.accountFrom = accountFrom;
        this.accountTo = accountTo;
    }

    public int getTransferId() {
        return transferId;
    }

    public void setTransferId(int transferId) {
        this.transferId = transferId;
    }

    public int getTransferTypeId() {
        return transferTypeId;
    }

    public void setTransferTypeId(int transferTypeId) {
        this.transferTypeId = transferTypeId;
    }

    public int getTransferStatusId() {
        return transferStatusId;
    }

    public void setTransferStatusId(int tranferStatusId) {
        this.transferStatusId = tranferStatusId;
    }

    public int getAccountFrom() {
        return accountFrom;
    }

    public void setAccountFrom(int accountFrom) {
        this.accountFrom = accountFrom;
    }

    public int getAccountTo() {
        return accountTo;
    }

    public void setAccountTo(int accountTo) {
        this.accountTo = accountTo;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transfer transfer = (Transfer) o;
        return Objects.equals(amount, transfer.amount) &&
                Objects.equals(accountFrom, transfer.accountFrom) &&
                Objects.equals(accountTo, transfer.accountTo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, accountFrom, accountTo);
    }
}
