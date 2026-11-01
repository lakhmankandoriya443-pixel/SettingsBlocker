 package com.example.settingsblocker;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toast;

import java.util.HashSet;
import java.util.Set;

public class SettingsBlockerService extends AccessibilityService {

    // Whitelist: Agar kisi choti setting ko bina OTP ke kholna ho toh uska package yahan add kar sakte hain
    private static final Set<String> WHITELISTED_PACKAGES = new HashSet<>();
    
    // Session timer variables (5 minute ka temporary access)
    private static boolean isTemporarilyUnlocked = false;
    private static final long UNLOCK_DURATION_MS = 5 * 60 * 1000; // 5 Minutes

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            CharSequence packageName = event.getPackageName();
            
            if (packageName != null) {
                String pkg = packageName.toString();

                // Agar user Main Settings ya restricted area khol raha hai
                if (pkg.equals("com.android.settings")) {

                    // Agar temporarily unlocked hai (OTP daalne ke baad 5 minute tak)
                    if (isTemporarilyUnlocked) {
                        return; // Aage jaane do, block mat karo
                    }

                    // Agar whitelist me hai toh khulne do, warna block karo
                    if (WHITELISTED_PACKAGES.contains(pkg)) {
                        return; 
                    }

                    // --- BLOCK ACTION ---
                    // 1. User ko turant Home Screen par bhej do
                    Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                    homeIntent.addCategory(Intent.CATEGORY_HOME);
                    homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(homeIntent);

                    // 2. Warning message dikhao aur OTP Verification Activity kholo
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), 
                                "Access Blocked! OTP Required.", 
                                Toast.LENGTH_LONG).show();
                            
                            // OTP Activity launch karne ka code
                            Intent otpIntent = new Intent(getApplicationContext(), OtpVerificationActivity.class);
                            otpIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(otpIntent);
                        }
                    });
                }
            }
        }
    }

    // Jab sahi OTP dal jaye tab yeh function call hoga aur 5 minute ke liye access mil jayega
    public static void grantTemporaryAccess() {
        isTemporarilyUnlocked = true;
        
        // Timer jo 5 minute baad wapas lock laga dega
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                isTemporarilyUnlocked = false;
            }
        }, UNLOCK_DURATION_MS);
    }

    @Override
    public void onInterrupt() {
        // Required method for AccessibilityService
    }
}
