package com.example.moneymanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "MoneyManager.db";
    private static final int DATABASE_VERSION = 2;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USERS_TABLE = "CREATE TABLE users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "username TEXT NOT NULL," +
                "email TEXT NOT NULL," +
                "password TEXT NOT NULL," +
                "balance REAL DEFAULT 0," +  // เพิ่ม balance
                "total_income REAL DEFAULT 0," +  // เพิ่ม total_income
                "total_expenses REAL DEFAULT 0" +  // เพิ่ม total_expenses
                ")";
        db.execSQL(CREATE_USERS_TABLE);

        // สร้างตาราง transactions ที่มีฟิลด์ user_id สำหรับเชื่อมโยงกับผู้ใช้
        String CREATE_TRANSACTIONS_TABLE = "CREATE TABLE transactions (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "user_id INTEGER NOT NULL," +  // ฟิลด์ user_id
                "category TEXT NOT NULL," +
                "amount REAL NOT NULL," +
                "is_income INTEGER NOT NULL," +  // 1 = รายรับ, 0 = รายจ่าย
                "FOREIGN KEY(user_id) REFERENCES users(id))";  // เชื่อมโยงกับตาราง users
        db.execSQL(CREATE_TRANSACTIONS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // ลบฐานข้อมูลเก่าเพื่อให้แอปสร้างใหม่
    }

    // อัปเดตยอดคงเหลือ รายรับ และรายจ่ายของผู้ใช้
    public void updateUserData(int userId, double balance, double totalIncome, double totalExpenses) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("balance", balance);
        values.put("total_income", totalIncome);
        values.put("total_expenses", totalExpenses);

        db.update("users", values, "id = ?", new String[]{String.valueOf(userId)});
        db.close();
    }

    // ดึงข้อมูลยอดคงเหลือ รายรับ และรายจ่ายของผู้ใช้
    public HashMap<String, Double> getUserData(int userId) {
        HashMap<String, Double> userData = new HashMap<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT balance, total_income, total_expenses FROM users WHERE id = ?", new String[]{String.valueOf(userId)});
        if (cursor.moveToFirst()) {
            userData.put("balance", cursor.getDouble(cursor.getColumnIndexOrThrow("balance")));
            userData.put("total_income", cursor.getDouble(cursor.getColumnIndexOrThrow("total_income")));
            userData.put("total_expenses", cursor.getDouble(cursor.getColumnIndexOrThrow("total_expenses")));
        }
        cursor.close();
        db.close();
        return userData;
    }

    // ฟังก์ชันสำหรับเพิ่มข้อมูลผู้ใช้ใหม่ลงในฐานข้อมูล
    public void addUser(String username, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("email", email);
        values.put("password", password);

        db.insert("users", null, values);
        db.close();
    }

    // ฟังก์ชันสำหรับดึงข้อมูลผู้ใช้ตาม userId
    public String getUserNameById(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String userName = "";

        Cursor cursor = db.rawQuery("SELECT username FROM users WHERE id = ?", new String[]{String.valueOf(userId)});
        if (cursor.moveToFirst()) {
            userName = cursor.getString(cursor.getColumnIndexOrThrow("username"));
        }
        cursor.close();
        db.close();

        return userName;
    }

    // ฟังก์ชันสำหรับเพิ่มข้อมูลธุรกรรมใหม่ลงในฐานข้อมูล
    public void addTransaction(Transaction transaction) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_id", transaction.getUserId());  // ใส่ userId ลงในฟิลด์ user_id
        values.put("category", transaction.getCategory());
        values.put("amount", transaction.getAmount());
        values.put("is_income", transaction.isIncome() ? 1 : 0);

        db.insert("transactions", null, values);  // บันทึกข้อมูลลงในตาราง transactions
        db.close();
    }

    // ฟังก์ชันสำหรับดึงข้อมูลธุรกรรมตาม userId
    public ArrayList<Transaction> getTransactionsForUser(int userId) {
        ArrayList<Transaction> transactions = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // ดึงข้อมูลจากตาราง transactions ที่ user_id ตรงกับผู้ใช้ที่ล็อกอิน
        Cursor cursor = db.rawQuery("SELECT * FROM transactions WHERE user_id = ?", new String[]{String.valueOf(userId)});
        if (cursor.moveToFirst()) {
            do {
                // ดึงข้อมูลจาก cursor โดยตรง
                String category = cursor.getString(cursor.getColumnIndexOrThrow("category"));
                double amount = cursor.getDouble(cursor.getColumnIndexOrThrow("amount"));
                boolean isIncome = cursor.getInt(cursor.getColumnIndexOrThrow("is_income")) == 1;

                // เพิ่มข้อมูลธุรกรรมลงในรายการ
                transactions.add(new Transaction(category, amount, isIncome, userId));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return transactions;
    }

    // ฟังก์ชันสำหรับดึงข้อมูลรายจ่ายแยกตามหมวดหมู่
    public HashMap<String, Double> getExpenseByCategory(int userId) {
        HashMap<String, Double> expensesByCategory = new HashMap<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // ดึงข้อมูลรายจ่ายจากหมวดหมู่ต่างๆ เฉพาะที่เป็นรายจ่าย (is_income = 0)
        Cursor cursor = db.rawQuery("SELECT category, SUM(amount) as total_expense FROM transactions WHERE user_id = ? AND is_income = 0 GROUP BY category", new String[]{String.valueOf(userId)});
        if (cursor.moveToFirst()) {
            do {
                String category = cursor.getString(cursor.getColumnIndexOrThrow("category"));
                double totalExpense = cursor.getDouble(cursor.getColumnIndexOrThrow("total_expense"));

                // เก็บข้อมูลรายจ่ายแยกตามหมวดหมู่
                expensesByCategory.put(category, totalExpense);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return expensesByCategory;
    }

    // ฟังก์ชันสำหรับดึงข้อมูลรายจ่ายทั้งหมดของผู้ใช้
    public double getTotalExpenses(int userId) {
        double totalExpenses = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        // Query เพื่อดึงผลรวมของรายจ่าย (is_income = 0) จากฐานข้อมูลสำหรับผู้ใช้ที่ล็อกอิน
        Cursor cursor = db.rawQuery("SELECT SUM(amount) AS total FROM transactions WHERE user_id = ? AND is_income = 0", new String[]{String.valueOf(userId)});
        if (cursor.moveToFirst()) {
            totalExpenses = cursor.getDouble(cursor.getColumnIndexOrThrow("total"));
        }
        cursor.close();
        db.close();
        return totalExpenses;
    }

    public void resetTransactionsForUser(int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("transactions", "user_id = ?", new String[]{String.valueOf(userId)});  // ลบข้อมูลธุรกรรมทั้งหมดสำหรับ userId นี้

        // รีเซ็ตยอดรวมรายรับและรายจ่ายของผู้ใช้
        ContentValues values = new ContentValues();
        values.put("total_income", 0);
        values.put("total_expenses", 0);

        db.update("users", values, "id = ?", new String[]{String.valueOf(userId)});  // อัปเดตยอดรายรับและรายจ่ายเป็น 0
        db.close();
    }

    // ฟังก์ชันสำหรับตรวจสอบการเข้าสู่ระบบของผู้ใช้
    public int checkUserLogin(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id FROM users WHERE email = ? AND password = ?", new String[]{email, password});
        int userId = -1;
        if (cursor.moveToFirst()) {
            userId = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
        }
        cursor.close();
        db.close();
        return userId;
    }

}
