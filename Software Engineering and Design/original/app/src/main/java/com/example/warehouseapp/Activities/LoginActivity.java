package com.example.warehouseapp.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.warehouseapp.Database.UserHelper;
import com.example.warehouseapp.R;

public class LoginActivity extends AppCompatActivity {

    // all of the content variables we need
    private EditText usernameInput, passwordInput;
    private Button btnLogin, btnRegister;

    // our user data base helper
    private UserHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set the content view
        setContentView(R.layout.activity_login);

        // check sms permission
        handleSMSPermissions();
        // grab all of our elements from the view
        usernameInput = findViewById(R.id.Username);
        passwordInput = findViewById(R.id.Password);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);

        // establish our helper
        dbHelper = new UserHelper(this);

        // register an event listener for when the user clicks login
        btnLogin.setOnClickListener(v -> {

            // grab the username/password for the views
            String username = usernameInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            boolean isLoginValid = dbHelper.validateUser(username, password); // check if the login is valid

            // if the login is not valid, do not log the user in
            if(!isLoginValid) {
                Toast.makeText(this, "Invalid credentials were entered", Toast.LENGTH_SHORT).show();
                return;
            }


            // popup a short term message indicating success
            Toast.makeText(this, "Login was successful", Toast.LENGTH_SHORT).show();

            // move to InventoryActivity after login
            Intent intent = new Intent(this, InventoryActivity.class);

            // start the activity
            startActivity(intent);

            finish(); // finish


        });

        // event listener for when the user clicks register new user
        btnRegister.setOnClickListener(v -> {
           // get the username / pass from their views
            String username = usernameInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            // if there are empty fields
            if(username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill out all of the fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // if the register process was successful
            if(dbHelper.registerUser(username, password)) {
                Toast.makeText(this, "Account created successfully. You may now login", Toast.LENGTH_SHORT).show();
                return;
            }

            // if the void method wasn't escaped, there was an issue
            Toast.makeText(this, "There was an error with creating your account. Does the already exist?", Toast.LENGTH_SHORT).show();

        });
    }

    /**
     * Method to handle the SMS permission check
     */
    private void handleSMSPermissions() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {

            Intent intent = new Intent(this, SmsPermissionActivity.class);
            startActivity(intent);
            finish();

        }
    }


}
