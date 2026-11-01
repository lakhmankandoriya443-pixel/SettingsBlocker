package com.example.settingsblocker;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class OtpVerificationActivity extends AppCompatActivity {

    private EditText etOtp;
    private Button btnSubmit;
    
    // Abhi ke liye ek test OTP ("1234") set kar rahe hain. 
    // Baad me ise aap apne brother ke online system ya API se connect kar sakte hain.
    private static final String CORRECT_OTP = "1234";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        etOtp = findViewById(R.id.etOtp);
        btnSubmit = findViewById(R.id.btnSubmit);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String enteredOtp = etOtp.getText().toString().trim();

                if (enteredOtp.equals(CORRECT_OTP)) {
                    // Sahi OTP hone par 5 minute ka temporary access de do
                    SettingsBlockerService.grantTemporaryAccess();
                    Toast.makeText(OtpVerificationActivity.this, "Access Granted for 5 Minutes!", Toast.LENGTH_SHORT).show();
                    finish(); // Screen band karke wapas jaane do
                } else {
                    Toast.makeText(OtpVerificationActivity.this, "Incorrect OTP! Try Again.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
