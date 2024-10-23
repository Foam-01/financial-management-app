package com.example.moneymanager;

public class Transaction {
    private String category;
    private double amount;
    private boolean isIncome;
    private int userId;  // เพิ่มฟิลด์สำหรับ userId

    // คอนสตรักเตอร์ที่รวม userId
    public Transaction(String category, double amount, boolean isIncome, int userId) {
        this.category = category;
        this.amount = amount;
        this.isIncome = isIncome;
        this.userId = userId;  // กำหนดค่า userId
    }

    // Getter และ Setter สำหรับ userId
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    // Getter และ Setter อื่นๆ สำหรับฟิลด์ category, amount, isIncome
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public boolean isIncome() {
        return isIncome;
    }

    public void setIncome(boolean income) {
        isIncome = income;
    }
}
