package com.nickkhalow.easyclicker;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.LeaderboardsClient;
import com.nickkhalow.easyclicker.databinding.ActivityFullscreenBinding;

public class MainActivity extends AppCompatActivity {
    private static final int RC_LEADERBOARD_UI = 9004;
    private static final int RC_SIGN_IN = 9024;

    private int record;

    private ActivityFullscreenBinding binding;

    @Nullable
    private LeaderboardsClient leaderboardsClient() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account == null) return null;
        return Games.getLeaderboardsClient(this, account);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFullscreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.satisfyButton.setOnClickListener(view -> clickReward());
        binding.leaderButton.setOnClickListener(view -> showLeaderboard());
        binding.signIn.setOnClickListener(view -> startSignInIntent());
    }

    @Override
    protected void onResume() {
        super.onResume();
        signInSilently();
    }

    private void clickReward() {
        record++;
        binding.fullscreenContent.setText(String.valueOf(record));

        LeaderboardsClient client = leaderboardsClient();
        if (client != null) client.submitScore(getString(R.string.leaderboard_id), record);
    }

    private void showLeaderboard() {
        LeaderboardsClient client = leaderboardsClient();

        if (client != null)
            client
                    .getLeaderboardIntent(getString(R.string.leaderboard_id))
                    .addOnSuccessListener(intent -> startActivityForResult(intent, RC_LEADERBOARD_UI));
        else
            Toast.makeText(this, R.string.not_connected, Toast.LENGTH_LONG).show();
    }

    private void startSignInIntent() {
        GoogleSignInClient signInClient = GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN);
        Intent intent = signInClient.getSignInIntent();
        startActivityForResult(intent, RC_SIGN_IN);
    }

    private void signInSilently() {
        GoogleSignInOptions signInOptions = GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN;
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (GoogleSignIn.hasPermissions(account, signInOptions.getScopeArray())) {
            // Already signed in.
            // The signed in account is stored in the 'account' variable.
            GoogleSignInAccount signedInAccount = account;
        } else {
            // Haven't been signed-in before. Try the silent sign-in first.
            GoogleSignInClient signInClient = GoogleSignIn.getClient(this, signInOptions);
            signInClient
                    .silentSignIn()
                    .addOnCompleteListener(
                            this,
                            task -> {
                                if (task.isSuccessful()) {
                                    // The signed in account is stored in the task's result.
                                    GoogleSignInAccount signedInAccount = task.getResult();
                                } else {
                                    // Player will need to sign-in explicitly using via UI.
                                    // See [sign-in best practices](http://developers.google.com/games/services/checklist) for guidance on how and when to implement Interactive Sign-in,
                                    // and [Performing Interactive Sign-in](http://developers.google.com/games/services/android/signin#performing_interactive_sign-in) for details on how to implement
                                    // Interactive Sign-in.
                                }
                            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // The signed in account is stored in the result.
                GoogleSignInAccount signedInAccount = result.getSignInAccount();
            } else {
                String message = result.getStatus().getStatusMessage();
                if (message == null || message.isEmpty()) {
                    message = getString(R.string.signin_other_error);
                }
                new AlertDialog.Builder(this).setMessage(message)
                        .setNeutralButton(android.R.string.ok, null).show();
            }
        }
    }
}