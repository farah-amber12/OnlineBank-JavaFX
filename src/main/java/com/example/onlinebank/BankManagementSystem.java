package com.example.onlinebank;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.io.*;
import java.util.ArrayList;
import java.io.FileOutputStream;

 class Accounts implements Serializable {
    private int accountNumber;
    private double balance;
    private int pin;

    public int getPin() {

        return pin;
    }
    public Accounts(int accountNumber, double balance, int pin) {
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.pin = pin;
    }

    public int getAccountNumber() {

        return accountNumber;
    }

    public double getBalance() {

        return balance;
    }

    @Override
    public String toString() {
        return "Accounts{" +
                "accountNumber=" + accountNumber +
                ", balance=" + balance +
                ", pin=" + pin +
                '}';
    }
    protected void setBalance(double balance) {
        this.balance = balance;
    }
}

 class AccountHolders extends Accounts implements Serializable {
    private String name;
    private String address;
    private String  mobileNumber;

    public AccountHolders(int accountNumber, double balance, int pin, String name, String address, String mobileNumber) {
        super(accountNumber, balance, pin);
        this.name = name;
        this.address = address;
        this.mobileNumber = mobileNumber;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {

        this.name = name;
    }

    public String getAddress() {

        return address;
    }

    public void setAddress(String address) {

        this.address = address;
    }

    public String getMobileNumber() {

        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {

        this.mobileNumber = mobileNumber;
    }
    public void setBalance(double balance) {
        super.setBalance(balance);
    }

    @Override
    public String toString() {
        return super.toString() + "Customers{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", mobileNumber=" + mobileNumber +
                '}';
    }
}
  class Bank implements Serializable {
    private static int counter = 1;
    BankManagementSystem bankManagementSystem = new BankManagementSystem();

    private ArrayList<AccountHolders> accountHolders = new ArrayList<>();
    public void addAccountHolder(AccountHolders account) {
        accountHolders.add(account);
    }
    public boolean login(int accountNo, int password) {
        readFromFile();
        for (AccountHolders accountHolder : accountHolders) {
            if (accountHolder != null && accountHolder.getAccountNumber() == accountNo && accountHolder.getPin() == password) {
                System.out.println("Login successful!");
                return true;
            }
        }
        System.out.println("Login failed!");
        return false;
    }
    public double deposit(int accountNo, int pin, double amount) {
        readFromFile();
            for (AccountHolders accountHolder : accountHolders) {
                if (accountHolder != null && accountHolder.getPin() == pin && accountHolder.getAccountNumber() == accountNo) {
                    double currentBalance = accountHolder.getBalance();
                    double newBalance = currentBalance + amount;
                    accountHolder.setBalance(newBalance);
                    writeToFile();
                    return newBalance;
                }
            }
        return -1;
    }
    public int getNextAccountNumber(){
        readFromFile();
        int maxAccountNo = 0;
        for (AccountHolders accountHolder: accountHolders){
            if(accountHolder != null && accountHolder.getAccountNumber() > 0){
                maxAccountNo = accountHolder.getAccountNumber();
            }
        }
        return maxAccountNo + 1;
    }

    public double withdraw(int accountNo,int pin, double amount) {
        readFromFile();
        for (AccountHolders accountHolder : accountHolders) {
            if (accountHolder != null && accountHolder.getPin() == pin && accountHolder.getAccountNumber()== accountNo) {
                if(amount < accountHolder.getBalance()){
                double currentBalance = accountHolder.getBalance();
                double newBalance = currentBalance - amount;
                accountHolder.setBalance(newBalance);
                writeToFile();
                return newBalance;}
            }
        }
        return -1;
    }
    public double checkBalance(int accountNo,int pin) {
        readFromFile();
        for (AccountHolders accountHolder : accountHolders) {
            if (accountHolder != null && accountHolder.getPin() == pin && accountHolder.getAccountNumber()== accountNo) {

                return accountHolder.getBalance();}
            }
        System.out.println("Account not found!");
        return -1;
    }
    public String searchAccount(int accountNo,int pin) {
        readFromFile();
        for (AccountHolders accountHolder : accountHolders) {
            if (accountHolder != null && accountHolder.getPin() == pin && accountHolder.getAccountNumber() == accountNo) {
                return "Name: " + accountHolder.getName() +"\nAccount Number: " + accountHolder.getAccountNumber() + "\nMobile Number: " + accountHolder.getMobileNumber() + "\nAddress:" + accountHolder.getAddress() + "\nCurrent Balance: " + checkBalance(accountNo,pin) +" Rs";
            }
        }
        return "Account not found!";
    }
    public void writeToFile() {
        try {
            FileOutputStream fileOut = new FileOutputStream("accounts.txt");
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(accountHolders);
            fileOut.close();
            objectOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void readFromFile(){
        try{
            FileInputStream fileIn = new FileInputStream("accounts.txt");
            ObjectInputStream objectIn = new ObjectInputStream(fileIn);
            if(accountHolders instanceof ArrayList<AccountHolders>){
                accountHolders = (ArrayList<AccountHolders>) objectIn.readObject();
            }
            System.out.println(accountHolders.toString());
            fileIn.close();
            objectIn.close();
        }
        catch(IOException | ClassNotFoundException | ClassCastException e){
            e.printStackTrace();
        }
    }
    public boolean deleteAccountFromFile(int accountNumberToRemove, int pin) {
        readFromFile();
        AccountHolders accountToRemove = null;
        for (AccountHolders account : accountHolders) {
            if (account.getAccountNumber() == accountNumberToRemove && account.getPin() == pin) {
                accountToRemove = account;
                break;
            }
        }
        if (accountToRemove != null) {
            accountHolders.remove(accountToRemove);
            System.out.println("Account removed successfully!");
            writeToFile();
            return true;
        }
        return false;
    }
  }
public class BankManagementSystem extends Application {
    public void BankOperationsScene(Stage primaryStage, Bank bank, int accountNo, int pin) {

        Button depositButton = new Button("Deposit");
        Button withdrawButton = new Button("Withdraw");
        Button accountDetailsButton = new Button("Get Account Details");
        Button checkBalanceButton = new Button("Check Balance");
        Button deleteAccountButton = new Button("Delete Account");
        Button logOutButton = new Button("Log Out");

        // Set actions for the buttons
        depositButton.setOnAction(e -> {
            DepositScene(primaryStage,bank, accountNo, pin);
        });

        withdrawButton.setOnAction(e -> {
           WithdrawScene(primaryStage,bank, accountNo, pin);
        });

        accountDetailsButton.setOnAction(e -> {
            accountInfoScene(primaryStage,bank, accountNo, pin);

        });

        checkBalanceButton.setOnAction(e -> {
            CheckBalanceScene(primaryStage,bank, accountNo, pin);
        });
        deleteAccountButton.setOnAction(e -> {
           deleteAccountScene(primaryStage,bank, accountNo, pin);
        });
        logOutButton.setOnAction(e -> {
            try {
                start(primaryStage);
            } catch (FileNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        });

        VBox vbox = new VBox(10);
        vbox.getChildren().addAll(depositButton, withdrawButton, accountDetailsButton, checkBalanceButton, deleteAccountButton, logOutButton);
        vbox.setAlignment(Pos.CENTER);
        vbox.setStyle("-fx-background-color: rgb(163, 213, 229);");
        Scene bankingOperationsScene = new Scene(vbox, 600, 500);
        bankingOperationsScene.setCursor(Cursor.HAND);

        primaryStage.setScene(bankingOperationsScene);
        primaryStage.show();
    }
    public void CheckBalanceScene(Stage primaryStage, Bank bank, int accountNo, int pin){

        double newBalance = bank.checkBalance(accountNo, pin);
        Label resultLabelText = new Label("your balance is:");
        resultLabelText.setFont(Font.font("Times new roman", FontWeight.BOLD, 16));
        resultLabelText.setTextFill(Color.rgb(40, 60, 92));
        Label resultLabelValue = new Label(String.valueOf(newBalance));
        resultLabelValue.setFont(Font.font("Times new roman", FontWeight.BOLD, 16));
        resultLabelText.setTextFill(Color.rgb(40, 60, 92));
        Button backButton = new Button("<- Back");
            backButton.setOnAction(e ->{
                BankOperationsScene(primaryStage,bank,accountNo,pin);
            });

        VBox resultLayout = new VBox(10);
        resultLayout.getChildren().addAll(resultLabelText, resultLabelValue, backButton);
        resultLayout.setStyle("-fx-background-color: rgb(163, 213, 229);");
        resultLayout.setAlignment(Pos.CENTER);

        Scene resultScene = new Scene(resultLayout, 550, 450);
        resultScene.setCursor(Cursor.HAND);
        primaryStage.setScene(resultScene);
        primaryStage.show();
    }
    public void WithdrawScene(Stage primaryStage, Bank bank, int accountNo, int pin) {

        Label withdrawLabel = new Label("Withdraw Amount:");
        withdrawLabel.setFont(Font.font("Times new roman", FontWeight.BOLD, 16));
        withdrawLabel.setTextFill(Color.rgb(40, 60, 92));
        TextField withdrawField = new TextField();
        Button withdrawButton = new Button("Withdraw");

        withdrawButton.setOnAction(e -> {
            double withdrawAmount = Double.parseDouble(withdrawField.getText());

            double newBalance = bank.withdraw(accountNo, pin, withdrawAmount);
            if (newBalance != -1) {
                Label newBalanceTextLabel = new Label("Your New Balance After withdrawing:");
                newBalanceTextLabel.setFont(Font.font("Times new roman", FontWeight.BOLD, 16));
                newBalanceTextLabel.setTextFill(Color.rgb(40, 60, 92));
                Label newBalanceValueLabel = new Label(String.valueOf(newBalance));
                newBalanceValueLabel.setFont(Font.font("Times new roman", FontWeight.BOLD, 16));
                newBalanceValueLabel.setTextFill(Color.rgb(40, 60, 92));
                Button backButton = new Button("<- Back");
                backButton.setOnAction(b -> {
                    BankOperationsScene(primaryStage, bank, accountNo, pin);
                });

                VBox resultLayout = new VBox(10);
                resultLayout.setStyle("-fx-background-color: rgb(163, 213, 229);");
                resultLayout.getChildren().addAll(newBalanceTextLabel, newBalanceValueLabel, backButton);
                resultLayout.setAlignment(Pos.CENTER);
                Scene resultScene = new Scene(resultLayout, 550, 450);

                primaryStage.setScene(resultScene);
                primaryStage.show();
            }
        else {
            incorrectDataEntered("Insufficient Balance!");
        }
        });
        VBox withdrawLayout = new VBox(10);
        withdrawLayout.getChildren().addAll( withdrawLabel, withdrawField, withdrawButton);
        withdrawLayout.setStyle("-fx-background-color: rgb(163, 213, 229);");
        withdrawLayout.setAlignment(Pos.CENTER);

        Scene withdrawScene = new Scene(withdrawLayout, 550, 450);
        withdrawScene.setCursor(Cursor.HAND);
        primaryStage.setScene(withdrawScene);
        primaryStage.show();
    }
    public void accountInfoScene(Stage primaryStage, Bank bank, int accountNo , int pin){
        String AccountInfo = bank.searchAccount(accountNo, pin);
        Label showInfoLabel = new Label("Your account details:");
        showInfoLabel.setFont(Font.font("Times new roman", FontWeight.BOLD, 16));
        showInfoLabel.setTextFill(Color.rgb(40, 60, 92));
        Text showInfoLabelValue = new Text(AccountInfo);
        showInfoLabelValue.setFont(Font.font("Times new roman", FontWeight.BOLD, 16));
        showInfoLabel.setTextFill(Color.rgb(40, 60, 92));
        Button backButton = new Button("<- Back");
        backButton.setOnAction(e -> {
            BankOperationsScene(primaryStage, bank, accountNo, pin);
        });

        VBox resultLayout = new VBox(10);
        resultLayout.getChildren().addAll(showInfoLabel, showInfoLabelValue, backButton);

        resultLayout.setStyle("-fx-background-color: rgb(163, 213, 229);");
        resultLayout.setAlignment(Pos.CENTER);


        Scene resultScene = new Scene(resultLayout, 550, 450);
        resultScene.setCursor(Cursor.HAND);
        primaryStage.setScene(resultScene);
        primaryStage.show();
    }
    public void DepositScene(Stage primaryStage, Bank bank, int accountNo, int pin) {
        Stage depositStage = new Stage();
        depositStage.setTitle("Deposit");
        Label depositLabel = new Label("Enter deposit amount:");
        depositLabel.setFont(Font.font("Times new roman", FontWeight.BOLD, 16));
        depositLabel.setTextFill(Color.rgb(40, 60, 92));
        TextField depositField = new TextField();
        Button confirmButton = new Button("Confirm");

        confirmButton.setOnAction(e -> {
            double depositAmount = Double.parseDouble(depositField.getText());
            if(depositAmount >= 0) {
                double newBalance = bank.deposit(accountNo, pin, depositAmount);
                //Stage depositStage = (Stage) confirmButton.getScene().getWindow();
                Label newBalanceTextLabel = new Label("Your New Balance After Depositing is:");
                newBalanceTextLabel.setFont(Font.font("Times new roman", FontWeight.BOLD, 16));
                newBalanceTextLabel.setTextFill(Color.rgb(40, 60, 92));
                Label newBalanceValueLabel = new Label(String.valueOf(newBalance));
                Button backButton = new Button("<- Back");
                backButton.setOnAction(back -> {
                    BankOperationsScene(primaryStage, bank, accountNo, pin);
                });

                VBox resultLayout = new VBox(10);
                resultLayout.setStyle("-fx-background-color: rgb(163, 213, 229);");
                resultLayout.getChildren().addAll(newBalanceTextLabel, newBalanceValueLabel, backButton);
                resultLayout.setAlignment(Pos.CENTER);

            Scene resultScene = new Scene(resultLayout, 550, 450);
            primaryStage.setScene(resultScene);
            primaryStage.show();}
            else {
                incorrectDataEntered("Incorrect Amount Entered!");
            }
        });
        VBox depositLayout = new VBox(10);
        depositLayout.setStyle("-fx-background-color: rgb(163, 213, 229);");
        depositLayout.getChildren().addAll( depositLabel, depositField, confirmButton);
        depositLayout.setAlignment(Pos.CENTER);

        Scene depositScene = new Scene(depositLayout, 550, 450);
        depositScene.setCursor(Cursor.HAND);
        primaryStage.setScene(depositScene);
        primaryStage.show();
    }
    public void incorrectDataEntered( String errorMessage){
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Error");
            errorAlert.setHeaderText(null);
            errorAlert.setContentText(errorMessage);
            errorAlert.showAndWait();
        }

        public void signUpScene(Stage primaryStage, Bank bank){
            Stage signUpStage = new Stage();
            signUpStage.setTitle("Sign Up");

            // Create UI components for the sign-up dialog
            Label nameLabel = new Label("Name:");
            nameLabel.setFont(Font.font("Times new roman", FontWeight.BOLD, 16));
            nameLabel.setTextFill(Color.rgb(40, 60, 92));
            TextField nameField = new TextField();
            Label addressLabel = new Label("Address:");
            addressLabel.setTextFill(Color.rgb(40, 60, 92));
            addressLabel.setFont(Font.font("Times new roman", FontWeight.BOLD, 16));
            TextField addressField = new TextField();
            Label mobileNumberLabel = new Label("Mobile Number:");
            mobileNumberLabel.setTextFill(Color.rgb(40, 60, 92));
            mobileNumberLabel.setFont(Font.font("Times new roman", FontWeight.BOLD, 16));
            TextField mobileNumberField = new TextField();
            Label initialBalanceLabel = new Label("Initial Balance:");
            initialBalanceLabel.setTextFill(Color.rgb(40, 60, 92));
            initialBalanceLabel.setFont(Font.font("Times new roman", FontWeight.BOLD, 16));
            TextField initialBalanceField = new TextField();
            Label pinLabel = new Label("PIN:");
            pinLabel.setTextFill(Color.rgb(40, 60, 92));
            pinLabel.setFont(Font.font("Times new roman", FontWeight.BOLD, 16));
            PasswordField pinField = new PasswordField();
            Button signUpButton = new Button("Sign Up");
            Button backButton = new Button("Close");
            backButton.setOnAction(back ->{
                Stage stage = (Stage) backButton.getScene().getWindow();
                stage.close();
            });
            // GridPane layout for positioning UI components
            GridPane gridPane = new GridPane();
            gridPane.setAlignment(Pos.CENTER);
            gridPane.setHgap(15);
            gridPane.setVgap(15);
            gridPane.setPadding(new Insets(35, 35, 35, 35));

            // Add components to the grid
            gridPane.add(nameLabel, 0, 0);
            gridPane.add(nameField, 1, 0);
            gridPane.add(addressLabel, 0, 1);
            gridPane.add(addressField, 1, 1);
            gridPane.add(mobileNumberLabel, 0, 2);
            gridPane.add(mobileNumberField, 1, 2);
            gridPane.add(initialBalanceLabel, 0, 3);
            gridPane.add(initialBalanceField, 1, 3);
            gridPane.add(pinLabel, 0, 4);
            gridPane.add(pinField, 1, 4);
            gridPane.add(signUpButton, 1, 5);
            gridPane.add(backButton,1,6);
             gridPane.setStyle("-fx-background-color: rgb(163, 213, 229);");
            signUpButton.setOnAction(e -> {
                if(nameField.getText().isEmpty() || mobileNumberField.getText().isEmpty() || pinField.getText().isEmpty()
                || addressField.getText().isEmpty() || initialBalanceField.getText().isEmpty()){
                    incorrectDataEntered("You cannot leave any field empty!\nPlease fill out all fields to complete sign up!");
                } else {
                    String name = nameField.getText();
                    String address = addressField.getText();
                    String mobileNumber = mobileNumberField.getText();
                    double initialBalance = Double.parseDouble(initialBalanceField.getText());
                    int pin = Integer.parseInt(pinField.getText());

                    AccountHolders newAccount = new AccountHolders(bank.getNextAccountNumber(), initialBalance, pin, name, address, mobileNumber);
                    bank.addAccountHolder(newAccount);
                    bank.writeToFile();
                    signUpStage.close();
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setContentText("YOUR ACCOUNT HAS BEEN CREATED!");
                    alert.showAndWait();
                    try {
                        start(primaryStage);
                    } catch (FileNotFoundException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            });

            Scene signUpScene = new Scene(gridPane, 600, 500);
            signUpScene.setCursor(Cursor.HAND);
            primaryStage.setScene(signUpScene);

            primaryStage.show();
        }
        public void deleteAccountScene(Stage primaryStage,Bank bank, int accountNo, int pin){

            Button deleteButton = new Button("DELETE ACCOUNT");
            Button backButton = new Button("<- Back");
            backButton.setOnAction(e ->{
                BankOperationsScene(primaryStage,bank,accountNo,pin);
            });
            deleteButton.setOnAction(e -> {
                Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
                confirmAlert.setTitle("Confirmation");
                confirmAlert.setHeaderText(null);
                confirmAlert.setContentText("Are you sure you want to delete your account?");

                confirmAlert.showAndWait().ifPresent(response -> {

                    if (response == ButtonType.OK) {
                        boolean deleted = bank.deleteAccountFromFile(accountNo, pin);
                        if (deleted) {
                            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                            successAlert.setTitle("Success");
                            successAlert.setHeaderText(null);
                            successAlert.setContentText("Account deleted successfully!");

                            successAlert.show();
                        }
                    }

                });
            });

            VBox deleteLayout = new VBox(10);
            deleteLayout.getChildren().addAll(deleteButton, backButton);
            deleteLayout.setStyle("-fx-background-color: rgb(163, 213, 229);");
            deleteLayout.setAlignment(Pos.CENTER);

            Scene deleteScene = new Scene(deleteLayout, 550, 450);
            deleteScene.setCursor(Cursor.HAND);
            primaryStage.setScene(deleteScene);
            primaryStage.show();

        }
    @Override
    public void start(Stage primaryStage) throws FileNotFoundException {
        Bank bank = new Bank();
        primaryStage.setTitle("Bank Management System");
        Label signUpLabel = new Label("Don't have an account?");
        Label accountLabel = new Label("Account Number:");
        Label passwordLabel = new Label("Password:");
        Button signUpButton = new Button("Sign up");

        TextField accountField = new TextField();
        PasswordField passwordField = new PasswordField();
        signUpLabel.setFont(Font.font("Times new roman", FontWeight.BOLD, 16));
        signUpLabel.setTextFill(Color.rgb(40, 60, 92));
        accountLabel.setFont(Font.font("Times new roman", FontWeight.BOLD, 16));
        accountLabel.setTextFill(Color.rgb(40, 60, 92));
        passwordLabel.setFont(Font.font("Times new roman", FontWeight.BOLD, 16));
        passwordLabel.setTextFill(Color.rgb(40, 60, 92));

        Button signInButton = new Button("Sign In");
        signInButton.setOnAction(e -> {
            int enteredAccountNumber = Integer.parseInt(accountField.getText());
            int enteredPIN = Integer.parseInt(passwordField.getText());
            boolean isValidAccount = bank.login(enteredAccountNumber,enteredPIN);
            if (isValidAccount) {
               BankOperationsScene(primaryStage, bank, enteredAccountNumber, enteredPIN);
            } else {
                incorrectDataEntered("Incorrect account number or password!\nPlease try again");
            }
        });
        signUpButton.setOnAction(e -> {
            signUpScene(primaryStage,bank);
        });

        Image image = new Image("C:\\\\Farah Amber\\\\image\\\\bank.jpg");
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(400);
        imageView.setFitWidth(350);

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20, 20, 20, 20));
        grid.setVgap(10);
        grid.setHgap(10);

        grid.add(accountLabel, 0, 0);
        grid.add(accountField, 1, 0);
        grid.add(passwordLabel, 0, 1);
        grid.add(passwordField, 1, 1);
        grid.add(signInButton, 1, 2);
        grid.add(signUpLabel,1,5) ;
        grid.add(signUpButton,2,5);

        HBox hbox = new HBox();
        hbox.getChildren().addAll(imageView, grid);
        hbox.setStyle("-fx-background-color: rgb(163, 213, 229);");
        hbox.setSpacing(20);

        Scene scene = new Scene(hbox, 800, 400);
        scene.setCursor(Cursor.HAND);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    public static void main(String[] args) {
        launch();
    }
}
