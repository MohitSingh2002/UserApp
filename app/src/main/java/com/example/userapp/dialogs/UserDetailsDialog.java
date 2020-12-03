package com.example.userapp.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;

import com.example.userapp.databinding.UserDetailsLayoutBinding;

public class UserDetailsDialog {

    Context context;
    UserDetailsLayoutBinding binding;

    public UserDetailsDialog(Context context) {
        this.context = context;
        binding = UserDetailsLayoutBinding.inflate(LayoutInflater.from(context));
    }

    public void showDialog(OnUserDetailsPickedListener listener) {
        new AlertDialog.Builder(context)
                .setTitle("Enter Details")
                .setView(binding.getRoot())
                .setPositiveButton("DONE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.onPicked(
                                    binding.userPhoneNo.getText().toString(),
                                    binding.userAddress.getText().toString()
                                );
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.onCancel();
                    }
                })
                .show();
    }

    public interface OnUserDetailsPickedListener {
        void onPicked(String phone_no, String address);
        void onCancel();
    }

}
