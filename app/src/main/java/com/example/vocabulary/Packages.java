package com.example.vocabulary;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Packages extends AppCompatActivity {

    private LinearLayout layout6Months, layout1Year, layoutLifetime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_packages);

        Button btnContact = findViewById(R.id.btnContact);
        Button btnInstruct = findViewById(R.id.btnInstruct);

        // Ánh xạ layouts
        layout6Months = findViewById(R.id.layout6Months);
        layout1Year = findViewById(R.id.layout1Year);
        layoutLifetime = findViewById(R.id.layoutlifetime);

        btnContact.setOnClickListener(v -> showContactDialog());
        btnInstruct.setOnClickListener(v -> showInstructionDialog());
        

        layout6Months.setOnClickListener(v -> showPurchaseDialog("6 MONTHS", "$1.99", "$2.99", "Gói sử dụng 6 tháng, giảm giá đặc biệt."));
        layout1Year.setOnClickListener(v -> showPurchaseDialog("1 YEAR", "$2.99", "$4.99", "Gói sử dụng 1 năm, nhiều ưu đãi hơn."));
        layoutLifetime.setOnClickListener(v -> showPurchaseDialog("LIFETIME", "$3.99", "$7.99", "Mua một lần, sử dụng trọn đời."));

        // Nếu có yêu cầu restore (xóa gói mua)
        boolean restoreRequested = getIntent().getBooleanExtra("restore", false);
        if (restoreRequested) {
            clearPurchasedPackage();
            Toast.makeText(this, "Đã huỷ gói đã mua", Toast.LENGTH_SHORT).show();
        }

    }

    private void showContactDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Nhập số điện thoại");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_PHONE);

        LinearLayout container = new LinearLayout(this);
        container.setPadding(50, 20, 50, 10);
        container.addView(input);
        builder.setView(container);

        builder.setPositiveButton("Submit", (dialog, which) -> {
            String phone = input.getText().toString().trim();
            if (!phone.isEmpty()) {
                Toast.makeText(this, "Đã gửi: " + phone, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Vui lòng nhập số điện thoại!", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void showInstructionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Hướng dẫn");
        builder.setMessage("1. Mua gói Pro để mở khoá toàn bộ tính năng.\n2. Nhấn Contact để liên hệ hỗ trợ.\n3. Sử dụng mục Goals để thiết lập mục tiêu hằng ngày.\n4. Reminder sẽ nhắc bạn học từ mỗi ngày.");
        builder.setPositiveButton("Đóng", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void showPurchaseDialog(String title, String priceNow, String priceOld, String description) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Xác nhận mua: " + title);
        builder.setMessage(description + "\n\nGiá gốc: " + priceOld + "\nGiá ưu đãi: " + priceNow);

        builder.setPositiveButton("Mua ngay", (dialog, which) -> {
            savePurchasedPackage(title);
            highlightPurchasedPackage();
            Toast.makeText(this, "Mua " + title + " thành công!", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void savePurchasedPackage(String packageName) {
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("purchased_package", packageName);
        editor.apply();
    }

    private void clearPurchasedPackage() {
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove("purchased_package");
        editor.apply();

        resetLayoutColors();
    }

    private void highlightPurchasedPackage() {
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String purchased = prefs.getString("purchased_package", "");

        resetLayoutColors();

        if (purchased == null || purchased.isEmpty()) {
            // Chưa mua gói nào, giữ nguyên màu mặc định
            return;
        }

        switch (purchased) {
            case "6 MONTHS":
                layout6Months.setBackgroundColor(Color.parseColor("#FFC0CB")); // Màu hồng nhạt highlight
                break;
            case "1 YEAR":
                layout1Year.setBackgroundColor(Color.parseColor("#FFC0CB"));
                break;
            case "LIFETIME":
                layoutLifetime.setBackgroundColor(Color.parseColor("#FFC0CB"));
                break;
        }
    }

    private void resetLayoutColors() {
        // Đặt lại về drawable border_box hoặc màu nền mặc định
        layout6Months.setBackgroundResource(R.drawable.border_box);
        layout1Year.setBackgroundResource(R.drawable.border_box);
        layoutLifetime.setBackgroundResource(R.drawable.border_box);
    }
}
