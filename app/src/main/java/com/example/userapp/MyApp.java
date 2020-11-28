package com.example.userapp;

import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.userapp.databinding.CircularLoadingLottieBinding;
import com.google.firebase.firestore.FirebaseFirestore;

public class MyApp extends Application {

    public FirebaseFirestore db;
    public Dialog dialog;
    public ConnectivityManager connectivityManager;

    @Override
    public void onCreate() {
        super.onCreate();
        setup();
    }

    private void setup() {
        db = FirebaseFirestore.getInstance();
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    public void showLoadingDialog(Context context) {

        CircularLoadingLottieBinding binding = CircularLoadingLottieBinding.inflate(LayoutInflater.from(context));

        if(dialog != null && dialog.isShowing())
            return;

        dialog = new Dialog(context, R.style.LoaderStyle);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(binding.getRoot());

        if(dialog.getWindow() == null)
            return;

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();

//        CircularLoadingLottieBinding binding = CircularLoadingLottieBinding.inflate(LayoutInflater.from(context));
//        dialog = new AlertDialog.Builder(context, R.style.CustomAlertDialog)
//                .setCancelable(false)
//                .setView(binding.getRoot())
//                .show();

    }

    public void hideLoadingDialog() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    public void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public boolean isOffline() {
        NetworkInfo wifiNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo dataNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        return !(wifiNetworkInfo.isConnected() || dataNetworkInfo.isConnected());
    }

}
