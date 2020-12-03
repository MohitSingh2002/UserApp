package com.example.userapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.userapp.constants.Constants;
import com.example.userapp.databinding.ActivityLoginBinding;
import com.example.userapp.dialogs.UserDetailsDialog;
import com.example.userapp.model.UserDetails;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding binding;

    private static final int RC_SIGN_IN = 123;

    SharedPreferences preferences;
    String sharedPreferencesFile = "com.example.android.userapp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferences = getSharedPreferences(sharedPreferencesFile, MODE_PRIVATE);

        if (preferences.getBoolean(Constants.LOGIN_STATE, false)) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
        } else {
            setupGoogleSignIn();
        }

    }

    private void setupGoogleSignIn() {
        binding.signInGoogleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<AuthUI.IdpConfig> providers = Arrays.asList(
                        new AuthUI.IdpConfig.GoogleBuilder().build()
                );

                startActivityForResult(
                        AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setAvailableProviders(providers)
                                .build(),
                        RC_SIGN_IN
                );
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK) {
                if (preferences.getString(Constants.USER_NAME, "").isEmpty()) {
                    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                    new UserDetailsDialog(LoginActivity.this).showDialog(new UserDetailsDialog.OnUserDetailsPickedListener() {
                        @Override
                        public void onPicked(String phone_no, String address) {
                            UserDetails userDetails = new UserDetails(
                                    firebaseUser.getDisplayName(),
                                    phone_no,
                                    address
                            );
                            saveUserDetailsToSharedPrefs(userDetails);
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        }

                        @Override
                        public void onCancel() {
                            Toast.makeText(LoginActivity.this, "Cancelled!", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                }
            }
        }

    }

    private void saveUserDetailsToSharedPrefs(UserDetails userDetails) {
        changeUserToLoggedInState();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(Constants.USER_NAME, userDetails.name);
        editor.putString(Constants.USER_PHONE_NO, userDetails.phoneNo);
        editor.putString(Constants.USER_ADDRESS, userDetails.address);
        editor.apply();
    }

    private void changeUserToLoggedInState() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(Constants.LOGIN_STATE, true);
        editor.apply();
    }

}
