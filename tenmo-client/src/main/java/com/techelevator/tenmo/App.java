package com.techelevator.tenmo;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.UserCredentials;
import com.techelevator.tenmo.services.AuthenticationService;
import com.techelevator.tenmo.services.ConsoleService;
import com.techelevator.tenmo.services.TenmoService;

import java.math.BigDecimal;
import java.util.List;

public class App {

    private static final String API_BASE_URL = "http://localhost:8080/";

    private final ConsoleService consoleService = new ConsoleService();
    private final AuthenticationService authenticationService = new AuthenticationService(API_BASE_URL);
    private final TenmoService tenmoService = new TenmoService(API_BASE_URL);

    private AuthenticatedUser currentUser;

    public static void main(String[] args) {
        App app = new App();
        app.run();
    }

    private void run() {
        consoleService.printGreeting();
        loginMenu();
        if (currentUser != null) {
            mainMenu();
        }
    }
    private void loginMenu() {
        int menuSelection = -1;
        while (menuSelection != 0 && currentUser == null) {
            consoleService.printLoginMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                handleRegister();
            } else if (menuSelection == 2) {
                handleLogin();
            } else if (menuSelection != 0) {
                System.out.println("Invalid Selection");
                consoleService.pause();
            }
        }
    }

    private void handleRegister() {
        System.out.println("Please register a new user account");
        UserCredentials credentials = consoleService.promptForCredentials();
        if (authenticationService.register(credentials)) {
            System.out.println("Registration successful. You can now login.");
        } else {
            consoleService.printErrorMessage();
        }
    }

    private void handleLogin() {
        UserCredentials credentials = consoleService.promptForCredentials();
        currentUser = authenticationService.login(credentials);
        if (currentUser == null) {
            consoleService.printErrorMessage();
        }
    }

    private void mainMenu() {
        int menuSelection = -1;
        while (menuSelection != 0) {
            consoleService.printMainMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                viewCurrentBalance();
            } else if (menuSelection == 2) {
                viewTransferHistory();
            } else if (menuSelection == 3) {
                viewPendingRequests();
            } else if (menuSelection == 4) {
                sendBucks();
            } else if (menuSelection == 5) {
                requestBucks();
            } else if (menuSelection == 0) {
                continue;
            } else {
                System.out.println("Invalid Selection");
            }
            consoleService.pause();
        }
    }

	private void viewCurrentBalance() {
        BigDecimal balance = tenmoService.getBalance(currentUser);
        System.out.println("Current balance: " + balance);
	}

	private void viewTransferHistory() {
		Transfer[] transfers = tenmoService.allTransfers(currentUser);
        for (Transfer transfer : transfers) {
            System.out.println("Transfer ID: " + transfer.getTransferId());
        }

        if(transfers.length != 0) {
            String details = consoleService.promptForString("Would you like to see details for a transfer (Y/N)? ");
            if(details.trim().equals("Y") || details.trim().equals("y")) {
                int transferId = consoleService.promptForInt("Please enter a transfer id: ");
                boolean containsId = false;
                for (Transfer transfer : transfers) {
                    if(transfer.getTransferId() == transferId) {
                        int status = transfer.getTransferStatusId();
                        System.out.println("Sent from account: " + transfer.getAccountFrom());
                        System.out.println("Received at account: " +  transfer.getAccountTo());
                        System.out.println("Amount: " + transfer.getAmount());
                        System.out.println(status == 2 ? "Approved" : "Rejected");
                        containsId = true;
                    }
                }
                if (!containsId) {
                    System.out.println("Not a valid response");
                }
            } else if (details.trim().equals("N") || details.trim().equals("n")) {
                System.out.println("Exiting...");
            } else {
                System.out.println("Not a valid transfer");
            }
        } else {
            System.out.println("No transfers");
        }
	}

	private void viewPendingRequests() {
		// TODO Auto-generated method stub

	}

	private void sendBucks() {
        List<Integer> accounts = tenmoService.allAccounts(currentUser);
        for (Integer account : accounts) {
            System.out.println(account);
        }

        int userId = consoleService.promptForInt("Please pick a user to send to: ");
        if (userId != (int)(long)currentUser.getUser().getId() && accounts.contains(userId)) {
            BigDecimal amount = consoleService.promptForBigDecimal("How much do you want to send? ");
            if (amount.compareTo(new BigDecimal("0.00")) <= 0 ) {
                System.out.println("Can't send 0 or a negative amount of funds");
            } else {
                Transfer transfer = new Transfer();
                transfer.setAmount(amount);
                transfer.setAccountFrom((int)(long)currentUser.getUser().getId());
                transfer.setAccountTo(userId);

                Transfer newTransfer = tenmoService.createTransfer(transfer, currentUser);

                if (newTransfer.getTransferStatusId() == 2) {
                    if (tenmoService.executeTransfer(newTransfer, currentUser)) {
                        System.out.println("Transfer successful!");
                    } else {
                        System.out.println("Failed...");
                    }
                } else {
                    System.out.println("Sorry, you don't have enough funds");
                }
            }
        } else {
            System.out.println("Sorry, not a valid user");
        }
	}

	private void requestBucks() {
		// TODO Auto-generated method stub

	}

}
