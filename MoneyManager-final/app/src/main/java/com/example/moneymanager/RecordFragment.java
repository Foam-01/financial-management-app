package com.example.moneymanager;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;

public class RecordFragment extends Fragment {

    private TextView totalBalanceTextView;
    private TextView totalIncomeTextView;
    private TextView totalExpenseTextView;
    private RecyclerView transactionRecyclerView;
    private TransactionAdapter transactionAdapter;
    private ArrayList<Transaction> transactions;

    private double totalIncome = 0;
    private double totalExpenses = 0;
    private double totalBalance = 0;

    // Store selected category
    private String selectedCategory = "";

    // Store userId
    private int userId;

    // DBHelper สำหรับการดึงข้อมูลจากฐานข้อมูล
    private DBHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_record, container, false);


        // รับค่า userId จาก Arguments ที่ส่งมาจาก MainActivity
        if (getArguments() != null) {
            userId = getArguments().getInt("USER_ID", -1);
        }

        // เชื่อมต่อกับ DBHelper
        dbHelper = new DBHelper(getContext());

        // กำหนดค่าให้กับ transactions
        transactions = new ArrayList<>();  // สร้าง ArrayList สำหรับรายการธุรกรรม

        // Initialize views
        totalBalanceTextView = view.findViewById(R.id.total_balance_text);
        totalIncomeTextView = view.findViewById(R.id.total_income_text);
        totalExpenseTextView = view.findViewById(R.id.total_expense_text);
        transactionRecyclerView = view.findViewById(R.id.transaction_recycler_view);
        FloatingActionButton addTransactionButton = view.findViewById(R.id.add_transaction_button);
        Button resetButton = view.findViewById(R.id.reset_button);  // ปุ่มรีเซ็ต

        // Setup RecyclerView
        transactionRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        transactionAdapter = new TransactionAdapter(transactions, getContext());
        transactionRecyclerView.setAdapter(transactionAdapter);

        // ดึงข้อมูลจากฐานข้อมูล
        loadTransactionsFromDB();

        // เพิ่มการคลิกปุ่มรีเซ็ต
        resetButton.setOnClickListener(v -> resetData());

        // Add functionality to add transactions
        addTransactionButton.setOnClickListener(v -> openAddTransactionDialog());

        // อัปเดตการแสดงยอดเงิน
        updateBudgetDisplay();

        return view;
    }

    // ฟังก์ชันสำหรับดึงข้อมูลจากฐานข้อมูล
    private void loadTransactionsFromDB() {
        if (userId != -1) {
            transactions = dbHelper.getTransactionsForUser(userId);
            transactionAdapter.setTransactions(transactions);// อัปเดตข้อมูลใน RecyclerView

            HashMap<String, Double> userData = dbHelper.getUserData(userId);
            double balance = userData.get("balance");
            totalBalanceTextView.setText("ยอดเงินคงเหลือ: " + balance);
            Log.d("RecordFragment", "ยอดเงินคงเหลือจากฐานข้อมูล: " + balance);

            updateBudgetDisplay();  // อัปเดตการแสดงยอดเงินเมื่อดึงข้อมูล
        }
    }

    private void updateBudgetDisplay() {
        // คำนวณยอดรวมและแสดงผลยอดรวมของรายรับ-รายจ่าย
        totalIncome = 0;
        totalExpenses = 0;

        // คำนวณรายรับและรายจ่ายทั้งหมด
        for (Transaction transaction : transactions) {
            if (transaction.isIncome()) {
                totalIncome += transaction.getAmount();
            } else {
                totalExpenses += transaction.getAmount();
            }
        }
        // อัปเดตยอดเงินคงเหลือ
        double totalBalance = totalIncome - totalExpenses;

        // อัปเดตข้อมูลยอดคงเหลือในฐานข้อมูลผู้ใช้
        dbHelper.updateUserData(userId, totalBalance, totalIncome, totalExpenses);

        // อัปเดต TextView สำหรับรายรับ, รายจ่าย, และยอดเงินคงเหลือ
        totalIncomeTextView.setText("รายรับที่ได้: " + totalIncome);
        totalExpenseTextView.setText("รายจ่ายที่ได้: " + totalExpenses);
        totalBalanceTextView.setText("ยอดเงินคงเหลือ: " + totalBalance);
        // อัปเดตข้อมูลยอดคงเหลือในฐานข้อมูลผู้ใช้
        Log.d("RecordFragment", "Update balance: " + totalBalance + ", total income: " + totalIncome + ", total expenses: " + totalExpenses);

    }

    private void resetData() {
        // รีเซ็ตยอดรายรับ รายจ่าย และลบข้อมูลธุรกรรมของผู้ใช้ที่กำลังใช้งานอยู่
        dbHelper.resetTransactionsForUser(userId);  // สร้างฟังก์ชันนี้ใน DBHelper
        totalIncome = 0;  // รีเซ็ตยอดรายรับ
        totalExpenses = 0;  // รีเซ็ตยอดรายจ่าย

        dbHelper.updateUserData(userId, dbHelper.getUserData(userId).get("balance"), totalIncome, totalExpenses);


        // อัปเดตยอดเงินคงเหลือและข้อมูลใน RecyclerView
        transactions.clear();  // ล้างรายการธุรกรรม
        transactionAdapter.notifyDataSetChanged();  // แจ้ง Adapter ว่ามีการเปลี่ยนแปลงข้อมูล
        updateBudgetDisplay();  // อัปเดตการแสดงยอดเงิน
    }

    // Dialog สำหรับเพิ่มรายการรายรับ-รายจ่าย
    private void openAddTransactionDialog() {
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_add_transaction);

        EditText amountEditText = dialog.findViewById(R.id.transaction_amount);
        RadioButton incomeRadioButton = dialog.findViewById(R.id.radio_income);
        RadioButton expenseRadioButton = dialog.findViewById(R.id.radio_expense);
        Button saveButton = dialog.findViewById(R.id.save_transaction_button);

        // Category layout for income and expense buttons
        LinearLayout incomeCategoryLayout = dialog.findViewById(R.id.income_category_layout);
        LinearLayout expenseCategoryLayout = dialog.findViewById(R.id.expense_category_layout);

        incomeCategoryLayout.setVisibility(View.GONE);
        expenseCategoryLayout.setVisibility(View.GONE);

        // Handle radio button selection
        incomeRadioButton.setOnClickListener(v -> {
            incomeCategoryLayout.setVisibility(View.VISIBLE);
            expenseCategoryLayout.setVisibility(View.GONE);
        });

        expenseRadioButton.setOnClickListener(v -> {
            incomeCategoryLayout.setVisibility(View.GONE);
            expenseCategoryLayout.setVisibility(View.VISIBLE);
        });

        // Set up income category buttons
        dialog.findViewById(R.id.category_salary_button).setOnClickListener(v -> selectedCategory = "เงินเดือน");
        dialog.findViewById(R.id.category_investment_button).setOnClickListener(v -> selectedCategory = "การลงทุน");
        dialog.findViewById(R.id.category_prize_button).setOnClickListener(v -> selectedCategory = "รางวัล");
        dialog.findViewById(R.id.category_other_income_button).setOnClickListener(v -> selectedCategory = "รายรับอื่นๆ");
        dialog.findViewById(R.id.category_aum_button).setOnClickListener(v -> selectedCategory = "aum");

        // Set up expense category buttons
        dialog.findViewById(R.id.category_shopping_button).setOnClickListener(v -> selectedCategory = "ช้อปปิ้ง");
        dialog.findViewById(R.id.category_food_button).setOnClickListener(v -> selectedCategory = "อาหาร");
        dialog.findViewById(R.id.category_phone_button).setOnClickListener(v -> selectedCategory = "โทรศัพท์");
        dialog.findViewById(R.id.category_clothing_button).setOnClickListener(v -> selectedCategory = "เสื้อผ้า");
        dialog.findViewById(R.id.category_education_button).setOnClickListener(v -> selectedCategory = "การศึกษา");
        dialog.findViewById(R.id.category_house_button).setOnClickListener(v -> selectedCategory = "บ้านหรือหอ");
        dialog.findViewById(R.id.category_entertainment_button).setOnClickListener(v -> selectedCategory = "ความบันเทิง");
        dialog.findViewById(R.id.category_health_button).setOnClickListener(v -> selectedCategory = "สุขภาพ");

        // Save the transaction
        saveButton.setOnClickListener(v -> {
            String amountText = amountEditText.getText().toString();
            if (!amountText.isEmpty()) {
                double amount = Double.parseDouble(amountText);
                boolean isIncome = incomeRadioButton.isChecked();
                Transaction transaction = new Transaction(selectedCategory, amount, isIncome, userId);
                dbHelper.addTransaction(transaction);  // เพิ่มธุรกรรมลงในฐานข้อมูล

                transactions.add(transaction);  // เพิ่มธุรกรรมลงในรายการ
                transactionAdapter.notifyDataSetChanged();  // แจ้ง Adapter ว่ามีข้อมูลใหม่

                updateBudgetDisplay();  // อัปเดตยอดรวม
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}
