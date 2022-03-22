package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.util.BasicLogger;
import org.springframework.http.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import javax.print.attribute.standard.Media;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class TenmoService {
    private final String baseUrl;
    private final RestTemplate restTemplate = new RestTemplate();
    private String authToken = null;

    public TenmoService(String url) {
        this.baseUrl = url;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public BigDecimal getBalance(AuthenticatedUser currentUser) {
        BigDecimal balance = new BigDecimal("0.00");

        Long id = currentUser.getUser().getId();

        try {
            ResponseEntity<BigDecimal> response = restTemplate.exchange(baseUrl + "account/" + id + "/balance",
                    HttpMethod.GET, makeAuthEntity(currentUser), BigDecimal.class);
            balance = response.getBody();
        } catch(RestClientResponseException e) {
            BasicLogger.log(e.getMessage());
        } catch(ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }

        return balance;
    }

    public List<Integer> allAccounts(AuthenticatedUser currentUser) {
        Account[] accounts = null;

        try {
            ResponseEntity<Account[]> response = restTemplate.exchange(baseUrl + "accounts",
                    HttpMethod.GET, makeAuthEntity(currentUser), Account[].class);
            accounts = response.getBody();
        } catch(RestClientResponseException e) {
            BasicLogger.log(e.getMessage());
        } catch(ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        List<Integer> allUserIds = new ArrayList<>();
        for (Account account : accounts) {
            allUserIds.add(account.getUserId());
        }
        return allUserIds;
    }

    public Transfer createTransfer(Transfer transfer, AuthenticatedUser currentUser) {
        Transfer newTransfer = null;
        try {
            newTransfer = restTemplate.postForObject(baseUrl + "transfer/create",
                    makeTransferEntity(currentUser, transfer), Transfer.class);
        } catch(RestClientResponseException e) {
            BasicLogger.log(e.getMessage());
        } catch(ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return newTransfer;
    }

    public boolean executeTransfer(Transfer transfer, AuthenticatedUser currentUser) {
        boolean success = false;
        try {
            restTemplate.put(baseUrl + "transfer/sent", makeTransferEntity(currentUser, transfer), Transfer.class);
            success = true;
        } catch(RestClientResponseException e) {
            BasicLogger.log(e.getMessage());
        } catch(ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return success;
    }

    public Transfer[] allTransfers(AuthenticatedUser currentUser) {
        Transfer[] transfers = null;
        int id = (int)(long)currentUser.getUser().getId();

        try {
            ResponseEntity<Transfer[]> response = restTemplate.exchange(baseUrl + "account/" + id + "/transfers",
                    HttpMethod.GET, makeAuthEntity(currentUser), Transfer[].class);
            transfers = response.getBody();
        } catch(RestClientResponseException e) {
            BasicLogger.log(e.getMessage());
        } catch(ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }

        return transfers;
    }


    private HttpEntity<Account> makeAccountEntity(Account account) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authToken);
        return new HttpEntity<>(account, headers);
    }

    private HttpEntity<Transfer> makeTransferEntity(AuthenticatedUser currentUser, Transfer transfer) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(currentUser.getToken());
        return new HttpEntity<>(transfer, headers);
    }

    private HttpEntity<Void> makeAuthEntity(AuthenticatedUser currentUser) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(currentUser.getToken());
        return new HttpEntity<>(headers);
    }
}
