package com.example.moneymanager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("MainActivity", "onCreate called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // โค้ดอื่น ๆ ของ MainActivity

        // ตรวจสอบว่าผู้ใช้ล็อกอินอยู่หรือไม่
        if (!isUserLoggedIn()) {
            Log.d("MainActivity", "User is not logged in. Redirecting to LoginActivity.");
            startActivity(new Intent(this, LoginActivity.class));
            finish(); // ปิด MainActivity ถ้าไม่ได้ล็อกอิน
            return;
        }

        // รับค่า userId จาก LoginActivity
        userId = getIntent().getIntExtra("USER_ID", -1);
        Log.d("MainActivity", "Received User ID: " + userId);

        // ตรวจสอบว่ามี userId หรือไม่
        if (userId == -1) {
            Log.d("MainActivity", "No User ID found. Redirecting to LoginActivity.");
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // กำหนด BottomNavigationView
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            if (item.getItemId() == R.id.nav_record) {
                selectedFragment = new RecordFragment();
            } else if (item.getItemId() == R.id.nav_graph) {
                selectedFragment = new GraphFragment();
            } else if (item.getItemId() == R.id.nav_profile) {
                selectedFragment = new ProfileFragment();
            }

            if (selectedFragment != null) {
                // ส่ง userId ไปยัง Fragment
                Bundle bundle = new Bundle();
                bundle.putInt("USER_ID", userId);
                selectedFragment.setArguments(bundle);

                // เปลี่ยน Fragment
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
            }
            return true;
        });

        // เริ่มต้นที่หน้า RecordFragment
        if (savedInstanceState == null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_record);
        }
    }

    // ฟังก์ชันตรวจสอบว่าผู้ใช้ล็อกอินอยู่หรือไม่
    private boolean isUserLoggedIn() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        return sharedPreferences.getBoolean("isLoggedIn", false);
    }
}