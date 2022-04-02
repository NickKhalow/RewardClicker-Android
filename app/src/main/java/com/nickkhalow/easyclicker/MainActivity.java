package com.nickkhalow.easyclicker;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.nickkhalow.easyclicker.databinding.ActivityFullscreenBinding;

public class MainActivity extends AppCompatActivity {
    public static String TAG = MainActivity.class.getName();

    private static final int RC_LEADERBOARD_UI = 9004;

    private static final String APP_PREF = "app_pref";
    private static final String KEY_RECORD = "key_record";

    private SharedPreferences sharedPreferences;
    private int record;

    private ActivityFullscreenBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityFullscreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sharedPreferences = getSharedPreferences(APP_PREF, Context.MODE_PRIVATE);
        record = sharedPreferences.getInt(KEY_RECORD, 0);

        binding.satisfyButton.setOnClickListener(view -> clickReward());
        binding.leaderButton.setOnClickListener(view -> showLeaderboard());
        binding.signIn.setOnClickListener(view -> startSignIn());

        updateRecordUI();


    }

    @Override
    protected void onPause() {
        super.onPause();
        sharedPreferences
                .edit()
                .putInt(KEY_RECORD, record).apply();
    }

    private void updateRecordUI() {
        binding.fullscreenContent.setText(String.valueOf(record));
    }

    private void clickReward() {
        record++;
        updateRecordUI();
        //code
    }

    private void showLeaderboard() {
        //code
    }

    private void startSignIn() {
        //code
    }
}