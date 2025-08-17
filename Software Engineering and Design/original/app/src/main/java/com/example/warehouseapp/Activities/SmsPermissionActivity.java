package com.example.warehouseapp.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.warehouseapp.R;

public class SmsPermissionActivity extends AppCompatActivity {

    private static final int SMS_PERMISSION_CODE = 1;
    private Button btnGrant, btnDeny;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sms_permission);

        // get the buttons
        btnDeny = findViewById(R.id.btnDenySmsPerm);
        btnGrant = findViewById(R.id.btnGrantSmsPerm);

        // double check if perm is granted, return to login if so
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
            navigateToLogin();
            return;
        }

        // request permission
        btnGrant.setOnClickListener(v -> {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, SMS_PERMISSION_CODE);
        });

        // deny permission - user doesn't get notified
        btnDeny.setOnClickListener(v -> {
            Toast.makeText(this, "SMS permission denied. You can enable it later in settings.", Toast.LENGTH_SHORT).show();
            navigateToLogin();
        });
    }
    
    // handle the result of the permission request
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == SMS_PERMISSION_CODE) {

            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "SMS Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "SMS Permission Denied", Toast.LENGTH_SHORT).show();
            }
            navigateToLogin();
        }
        
    }

    /**
     * Method to navigate from this context to the LoginActivity
     */
    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
