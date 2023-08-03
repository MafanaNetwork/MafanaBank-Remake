package me.tahacheji.mafananetwork.data;

public enum TransactionType {

    LOAN("Loan"),
    DEPOSIT("Deposit"),
    RESET("Reset"),
    PAYLOAN("PayedLoan"),
    WITHDRAW("Withdraw");


    private final String lore;

    TransactionType(String lore) {
        this.lore = lore;
    }

    public String getLore() {
        return lore;
    }

}
