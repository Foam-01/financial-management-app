package com.example.moneymanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {

    private ArrayList<Transaction> transactions;
    private Context context;

    public TransactionAdapter(ArrayList<Transaction> transactions, Context context) {
        this.transactions = transactions;
        this.context = context;
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        Transaction transaction = transactions.get(position);

        // ตั้งค่าชื่อประเภท
        holder.categoryTextView.setText(transaction.getCategory());

        // ตั้งค่าจำนวนเงิน และเปลี่ยนสีตามประเภท (รายรับ = สีเขียว, รายจ่าย = สีแดง)
        holder.amountTextView.setText(String.valueOf(transaction.getAmount()));
        if (transaction.isIncome()) {
            holder.amountTextView.setTextColor(ContextCompat.getColor(context, R.color.green));
        } else {
            holder.amountTextView.setTextColor(ContextCompat.getColor(context, R.color.red));
        }

        // แสดงไอคอนตามประเภทที่เลือก
        switch (transaction.getCategory()) {
            case "เงินเดือน":
                holder.iconImageView.setImageResource(R.drawable.ic_salary);
                break;
            case "การลงทุน":
                holder.iconImageView.setImageResource(R.drawable.ic_investment);
                break;
            case "รางวัล":
                holder.iconImageView.setImageResource(R.drawable.ic_prize);
                break;
            case "รายรับอื่นๆ":
                holder.iconImageView.setImageResource(R.drawable.ic_other_income);
                break;
            case "aum":
                holder.iconImageView.setImageResource(R.drawable.ic_aum);
                break;
            case "ช้อปปิ้ง":
                holder.iconImageView.setImageResource(R.drawable.ic_shopping);
                break;
            case "อาหาร":
                holder.iconImageView.setImageResource(R.drawable.ic_food);
                break;
            case "โทรศัพท์":
                holder.iconImageView.setImageResource(R.drawable.ic_phone);
                break;
            case "เสื้อผ้า":
                holder.iconImageView.setImageResource(R.drawable.ic_clothing);
                break;
            case "การศึกษา":
                holder.iconImageView.setImageResource(R.drawable.ic_education);
                break;
            case "บ้านหรือหอ":
                holder.iconImageView.setImageResource(R.drawable.ic_house);
                break;
            case "ความบันเทิง":
                holder.iconImageView.setImageResource(R.drawable.ic_entertainment);
                break;
            case "สุขภาพ":
                holder.iconImageView.setImageResource(R.drawable.ic_health);
                break;
            default:
                holder.iconImageView.setImageResource(R.drawable.ic_default); // ไอคอนเริ่มต้น
                break;
        }
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    // ฟังก์ชันสำหรับอัปเดตรายการธุรกรรม
    public void setTransactions(ArrayList<Transaction> newTransactions) {
        this.transactions = newTransactions;
        notifyDataSetChanged(); // แจ้งให้ RecyclerView อัปเดตข้อมูลใหม่
    }

    public static class TransactionViewHolder extends RecyclerView.ViewHolder {
        ImageView iconImageView;
        TextView categoryTextView, amountTextView;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            iconImageView = itemView.findViewById(R.id.transaction_icon);
            categoryTextView = itemView.findViewById(R.id.transaction_category);
            amountTextView = itemView.findViewById(R.id.transaction_amount);
        }
    }
}
