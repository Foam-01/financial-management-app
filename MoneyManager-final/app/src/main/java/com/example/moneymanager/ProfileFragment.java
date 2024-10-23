package com.example.moneymanager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {
    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView profileImageView;
    private Uri imageUri;

    private TextView profileNameTextView;
    private DBHelper dbHelper;
    private int userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // กำหนดค่า view ต่างๆ เช่น รูปโปรไฟล์ ชื่อโปรไฟล์ และปุ่ม log out
        profileImageView = view.findViewById(R.id.profile_image);
        profileNameTextView = view.findViewById(R.id.profile_name);
        Button logOutButton = view.findViewById(R.id.button_log_out);

        // รับค่า userId จาก Arguments ที่ส่งมาจาก MainActivity
        if (getArguments() != null) {
            userId = getArguments().getInt("USER_ID", -1);
        }

        // เชื่อมต่อกับ DBHelper
        dbHelper = new DBHelper(getContext());

        // ดึงข้อมูลผู้ใช้จากฐานข้อมูลหรือ SharedPreferences
        if (userId != -1) {
            String userName = dbHelper.getUserNameById(userId);  // ดึงชื่อผู้ใช้จากฐานข้อมูล
            profileNameTextView.setText(userName);  // แสดงชื่อผู้ใช้ใน TextView
        } else {
            // ถ้าไม่พบ userId ดึงข้อมูลจาก SharedPreferences
            SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyAppPrefs", 0);
            String profileName = sharedPreferences.getString("username", "User");
            profileNameTextView.setText(profileName);
        }

        // การคลิกรูปโปรไฟล์เพื่อเปลี่ยนรูป
        profileImageView.setOnClickListener(v -> openFileChooser());

        // การ Log out
        logOutButton.setOnClickListener(v -> {
            // ลบข้อมูลล็อกอินใน SharedPreferences
            SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyAppPrefs", 0);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isLoggedIn", false);
            editor.apply();

            // กลับไปยังหน้า LoginActivity
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // เคลียร์ stack
            startActivity(intent);
        });

        return view;
    }

    // ฟังก์ชันสำหรับเปิดตัวเลือกไฟล์รูปภาพ
    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    // รับผลลัพธ์เมื่อผู้ใช้เลือกรูปภาพ
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            profileImageView.setImageURI(imageUri); // อัปเดตรูปโปรไฟล์ใน ImageView
        }
    }
}
