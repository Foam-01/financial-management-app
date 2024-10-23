package com.example.moneymanager;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.HashMap;

public class GraphFragment extends Fragment {

    private PieChart mainPieChart, remainingBalanceChart; // สำหรับแสดงกราฟหลักและกราฟยอดคงเหลือ
    private PieChart[] pieCharts = new PieChart[8];  // สำหรับเก็บ PieChart เล็ก 8 กราฟ
    private int selectedMode = 1;  // โหมดที่ถูกเลือกเริ่มต้น

    private TextView tvMainRemaining, tvMainBudget, tvMainSpent, tvMainIncome; // TextView สำหรับแสดงข้อมูลยอดเงินของกราฟหลัก รวมถึงรายรับ
    private TextView tvRemainingBalance, tvTotalIncome, tvTotalExpenses; // TextView สำหรับแสดงข้อมูลรายรับ-รายจ่ายและยอดคงเหลือ
    private TextView[] tvRemaining = new TextView[8]; // TextView สำหรับแสดงยอดคงเหลือของแต่ละหมวดหมู่
    private TextView[] tvBudget = new TextView[8]; // TextView สำหรับแสดงงบประมาณของแต่ละหมวดหมู่
    private TextView[] tvSpent = new TextView[8]; // TextView สำหรับแสดงรายจ่ายของแต่ละหมวดหมู่

    private DBHelper dbHelper;
    private int userId;

    // ตัวแปรเพื่อเก็บข้อมูลรายรับ รายจ่ายจาก RecordFragment
    private double totalIncome = 0;
    private double totalExpenses = 0;

    // ประเภทหมวดหมู่
    private final String[] categories = {
            "ช้อปปิ้ง", "อาหาร", "โทรศัพท์", "เสื้อผ้า", "การศึกษา", "บ้านหรือหอ", "ความบันเทิง", "สุขภาพ"
    };

    // โหมดงบประมาณของกราฟย่อย
    private final double[][] budgetModes = {
            {1000, 2500, 500, 1500, 3000, 2500, 1500, 500}, // โหมด 1
            {1500, 3000, 400, 1000, 4000, 3000, 2000, 700}, // โหมด 2
            {1700, 3000, 400, 1000, 4000, 3000, 2000, 700}, // โหมด 3
            {1800, 3000, 400, 1000, 4000, 3000, 2000, 700}, // โหมด 4
            {1900, 3000, 400, 1000, 4000, 3000, 2000, 700}, // โหมด 5
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("GraphFragmentPrefs", Context.MODE_PRIVATE);
        selectedMode = sharedPreferences.getInt("SELECTED_MODE", 1); // ค่าเริ่มต้นเป็น 1

        View view = inflater.inflate(R.layout.fragment_graph, container, false);

        // รับค่า userId จาก Arguments ที่ส่งมาจาก MainActivity
        if (getArguments() != null) {
            userId = getArguments().getInt("USER_ID", -1);
        }

        // เชื่อมต่อกับ DBHelper
        dbHelper = new DBHelper(getContext());

        // กราฟแสดงรายรับ-รายจ่าย (กราฟใหญ่)
        mainPieChart = view.findViewById(R.id.mainPieChart);

        // กราฟยอดคงเหลือใหม่
        remainingBalanceChart = view.findViewById(R.id.remainingBalanceChart);

        // กราฟงบประมาณ 8 อย่าง (กราฟเล็ก)
        pieCharts[0] = view.findViewById(R.id.pieChart_shopping);
        pieCharts[1] = view.findViewById(R.id.pieChart_food);
        pieCharts[2] = view.findViewById(R.id.pieChart_phone);
        pieCharts[3] = view.findViewById(R.id.pieChart_clothing);
        pieCharts[4] = view.findViewById(R.id.pieChart_education);
        pieCharts[5] = view.findViewById(R.id.pieChart_house);
        pieCharts[6] = view.findViewById(R.id.pieChart_entertainment);
        pieCharts[7] = view.findViewById(R.id.pieChart_health);

        // TextView สำหรับแสดงยอดคงเหลือ งบประมาณ และรายจ่ายของแต่ละหมวดหมู่
        tvRemaining[0] = view.findViewById(R.id.tv_shopping_remaining);
        tvBudget[0] = view.findViewById(R.id.tv_shopping_budget);
        tvSpent[0] = view.findViewById(R.id.tv_shopping_spent);

        tvRemaining[1] = view.findViewById(R.id.tv_food_remaining);
        tvBudget[1] = view.findViewById(R.id.tv_food_budget);
        tvSpent[1] = view.findViewById(R.id.tv_food_spent);

        tvRemaining[2] = view.findViewById(R.id.tv_phone_remaining);
        tvBudget[2] = view.findViewById(R.id.tv_phone_budget);
        tvSpent[2] = view.findViewById(R.id.tv_phone_spent);

        tvRemaining[3] = view.findViewById(R.id.tv_clothing_remaining);
        tvBudget[3] = view.findViewById(R.id.tv_clothing_budget);
        tvSpent[3] = view.findViewById(R.id.tv_clothing_spent);

        tvRemaining[4] = view.findViewById(R.id.tv_education_remaining);
        tvBudget[4] = view.findViewById(R.id.tv_education_budget);
        tvSpent[4] = view.findViewById(R.id.tv_education_spent);

        tvRemaining[5] = view.findViewById(R.id.tv_house_remaining);
        tvBudget[5] = view.findViewById(R.id.tv_house_budget);
        tvSpent[5] = view.findViewById(R.id.tv_house_spent);

        tvRemaining[6] = view.findViewById(R.id.tv_entertainment_remaining);
        tvBudget[6] = view.findViewById(R.id.tv_entertainment_budget);
        tvSpent[6] = view.findViewById(R.id.tv_entertainment_spent);

        tvRemaining[7] = view.findViewById(R.id.tv_health_remaining);
        tvBudget[7] = view.findViewById(R.id.tv_health_budget);
        tvSpent[7] = view.findViewById(R.id.tv_health_spent);

        // TextView สำหรับแสดงยอดคงเหลือ งบประมาณ และรายจ่ายของกราฟหลัก
        tvMainRemaining = view.findViewById(R.id.tv_main_remaining);
        tvMainBudget = view.findViewById(R.id.tv_main_budget);
        tvMainSpent = view.findViewById(R.id.tv_main_spent);
        tvMainIncome = view.findViewById(R.id.tv_total_income);  // เพิ่ม TextView สำหรับรายรับ

        // TextView สำหรับกราฟใหม่ที่แสดงยอดคงเหลือ รายรับ และรายจ่าย
        tvRemainingBalance = view.findViewById(R.id.tv_remaining_balance);
        tvTotalIncome = view.findViewById(R.id.tv_total_income);
        tvTotalExpenses = view.findViewById(R.id.tv_total_expenses);

        // ดึงข้อมูลจากฐานข้อมูล
        loadTransactionsFromDB();

        // เริ่มต้นด้วยโหมดที่เลือกเริ่มต้น
        setupBudgetPieCharts(selectedMode);
        updateMainPieChart(selectedMode);  // อัปเดตกราฟหลักด้วยงบประมาณรวมของโหมดแรก
        updateRemainingBalanceChart();  // อัปเดตกราฟยอดคงเหลือ

        // ปุ่มเลือกโหมด
        Button selectModeButton = view.findViewById(R.id.button_select_mode);
        selectModeButton.setOnClickListener(v -> openSelectModeDialog());



        return view;
    }

    private void saveSelectedMode(int mode) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("GraphFragmentPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("SELECTED_MODE", mode);
        editor.apply();
    }

    // ฟังก์ชันดึงข้อมูลจากฐานข้อมูล (เหมือนกับใน RecordFragment)
    private void loadTransactionsFromDB() {
        if (userId != -1) {
            ArrayList<Transaction> transactions = dbHelper.getTransactionsForUser(userId);

            // คำนวณยอดรวมรายรับและรายจ่าย
            totalIncome = 0;
            totalExpenses = 0;
            for (Transaction transaction : transactions) {
                if (transaction.isIncome()) {
                    totalIncome += transaction.getAmount();
                } else {
                    totalExpenses += transaction.getAmount();
                }
            }

            // อัปเดตการแสดงกราฟและ ViewText
            updateRemainingBalanceChart();
            updateMainPieChart(selectedMode);
        }
    }

    // ฟังก์ชันสำหรับอัปเดตกราฟหลักด้วยงบประมาณรวม
    private void updateMainPieChart(int mode) {
        double totalBudget = 0;
        double totalSpent = 0;

        // คำนวณงบประมาณรวมและรายจ่ายรวม
        for (int i = 0; i < budgetModes[mode - 1].length; i++) {
            totalBudget += budgetModes[mode - 1][i];
            totalSpent += dbHelper.getExpenseByCategory(userId).getOrDefault(categories[i], 0.0);
        }

        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry((float) totalSpent, ""));
        entries.add(new PieEntry((float) (totalBudget - totalSpent), ""));

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setValueTextSize(14f);
        dataSet.setColors(
                ContextCompat.getColor(getContext(), R.color.colorPrimary),  // สีส้ม
                ContextCompat.getColor(getContext(), R.color.grays)  // สีเทา
        );

        PieData pieData = new PieData(dataSet);
        mainPieChart.setData(pieData);
        mainPieChart.invalidate(); // รีเฟรชกราฟ

        // อัปเดต TextView แสดงยอดคงเหลือ งบประมาณ รายจ่าย และรายรับ
        updateMainBudgetDisplay(totalBudget, totalSpent, totalIncome);
    }

    // ฟังก์ชันสำหรับอัปเดตกราฟยอดคงเหลือ
    private void updateRemainingBalanceChart() {
        double remainingBalance = totalIncome - totalExpenses;

        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry((float) totalExpenses, ""));
        entries.add(new PieEntry((float) remainingBalance, ""));

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setValueTextSize(14f);
        dataSet.setColors(
                ContextCompat.getColor(getContext(), R.color.colorPrimary),  // สีส้ม
                ContextCompat.getColor(getContext(), R.color.grays)  // สีเทา
        );

        PieData pieData = new PieData(dataSet);
        remainingBalanceChart.setData(pieData);
        remainingBalanceChart.invalidate(); // รีเฟรชกราฟ

        // ตรวจสอบว่า TextView แต่ละตัวเป็น null หรือไม่ หากใช่ให้ตั้งค่าเป็น "0"
        if (tvRemainingBalance != null) {
            tvRemainingBalance.setText("คงเหลือ: " + remainingBalance);
        } else {
            Log.e("GraphFragment", "tvRemainingBalance เป็น null");
        }

        if (tvTotalIncome != null) {
            tvTotalIncome.setText("รายรับ: " + totalIncome);
        } else {
            Log.e("GraphFragment", "tvTotalIncome เป็น null");
        }

        if (tvTotalExpenses != null) {
            tvTotalExpenses.setText("รายจ่าย: " + totalExpenses);
        } else {
            Log.e("GraphFragment", "tvTotalExpenses เป็น null");
        }
    }

    // ฟังก์ชันสำหรับตั้งค่ากราฟงบประมาณ 8 อย่างตามโหมดที่เลือก
    private void setupBudgetPieCharts(int mode) {
        // ดึงข้อมูลรายจ่ายแยกตามหมวดหมู่
        HashMap<String, Double> expensesByCategory = dbHelper.getExpenseByCategory(userId);

        for (int i = 0; i < pieCharts.length; i++) {
            String category = categories[i];
            double expense = expensesByCategory.containsKey(category) ? expensesByCategory.get(category) : 0;
            double budget = budgetModes[mode - 1][i];
            double remaining = budget - expense;

            ArrayList<PieEntry> entries = new ArrayList<>();
            // ข้อมูลกราฟ: ส่วนที่ใช้ไปและส่วนที่เหลือ
            entries.add(new PieEntry((float) expense, ""));
            entries.add(new PieEntry((float) remaining, ""));

            PieDataSet dataSet = new PieDataSet(entries, "" + (i + 1));
            dataSet.setValueTextSize(12f);
            dataSet.setColors(
                    ContextCompat.getColor(getContext(), R.color.colorPrimary),  // สีส้ม
                    ContextCompat.getColor(getContext(), R.color.grays)  // สีเทา
            );

            PieData pieData = new PieData(dataSet);
            pieCharts[i].setData(pieData);
            pieCharts[i].invalidate(); // รีเฟรชกราฟ

            // อัปเดต TextView แสดงยอดคงเหลือ งบประมาณ และรายจ่ายของแต่ละกราฟเล็ก
            tvBudget[i].setText("งบประมาณ: " + budget);
            tvSpent[i].setText("รายจ่าย: " + expense);
            tvRemaining[i].setText("คงเหลือ: " + remaining);
        }
    }

    // ฟังก์ชันสำหรับอัปเดต TextView ที่แสดงงบประมาณและรายรับ
    private void updateMainBudgetDisplay(double budget, double spent, double income) {
        double remaining = budget - spent;

        // ตรวจสอบว่า TextView แต่ละตัวเป็น null หรือไม่ หากใช่ให้ตั้งค่าเป็น "0"
        if (tvMainBudget != null) {
            tvMainBudget.setText("งบประมาณ: " + budget);
        } else {
            Log.e("GraphFragment", "tvMainBudget เป็น null");
        }

        if (tvMainSpent != null) {
            tvMainSpent.setText("รายจ่าย: " + spent);
        } else {
            Log.e("GraphFragment", "tvMainSpent เป็น null");
        }

        if (tvMainRemaining != null) {
            tvMainRemaining.setText("คงเหลือ: " + remaining);
        } else {
            Log.e("GraphFragment", "tvMainRemaining เป็น null");
        }

        if (tvMainIncome != null) {
            tvMainIncome.setText("รายรับ: " + income);
        } else {
            Log.e("GraphFragment", "tvMainIncome เป็น null");
        }
    }

    // ฟังก์ชันเปิด dialog สำหรับเลือกโหมด
    private void openSelectModeDialog() {
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_select_mode);

        dialog.findViewById(R.id.mode1_button).setOnClickListener(v -> {
            selectedMode = 1;
            saveSelectedMode(selectedMode);
            setupBudgetPieCharts(selectedMode);  // อัปเดตกราฟย่อยตามโหมดที่เลือก
            updateMainPieChart(selectedMode);  // อัปเดตกราฟใหญ่ตามโหมดที่เลือก
            dialog.dismiss();
        });

        dialog.findViewById(R.id.mode2_button).setOnClickListener(v -> {
            selectedMode = 2;
            saveSelectedMode(selectedMode);
            setupBudgetPieCharts(selectedMode);  // อัปเดตกราฟย่อยตามโหมดที่เลือก
            updateMainPieChart(selectedMode);  // อัปเดตกราฟใหญ่ตามโหมดที่เลือก
            dialog.dismiss();
        });

        dialog.findViewById(R.id.mode3_button).setOnClickListener(v -> {
            selectedMode = 3;
            saveSelectedMode(selectedMode);
            setupBudgetPieCharts(selectedMode);  // อัปเดตกราฟย่อยตามโหมดที่เลือก
            updateMainPieChart(selectedMode);  // อัปเดตกราฟใหญ่ตามโหมดที่เลือก
            dialog.dismiss();
        });

        dialog.findViewById(R.id.mode4_button).setOnClickListener(v -> {
            selectedMode = 4;
            saveSelectedMode(selectedMode);
            setupBudgetPieCharts(selectedMode);  // อัปเดตกราฟย่อยตามโหมดที่เลือก
            updateMainPieChart(selectedMode);  // อัปเดตกราฟใหญ่ตามโหมดที่เลือก
            dialog.dismiss();
        });

        dialog.findViewById(R.id.mode5_button).setOnClickListener(v -> {
            selectedMode = 5;
            saveSelectedMode(selectedMode);
            setupBudgetPieCharts(selectedMode);  // อัปเดตกราฟย่อยตามโหมดที่เลือก
            updateMainPieChart(selectedMode);  // อัปเดตกราฟใหญ่ตามโหมดที่เลือก
            dialog.dismiss();
        });

        dialog.show();
    }
}
