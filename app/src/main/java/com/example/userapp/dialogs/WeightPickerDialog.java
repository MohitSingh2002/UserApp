package com.example.userapp.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

import com.example.userapp.databinding.WeightPickerDialogBinding;
import com.example.userapp.model.Cart;
import com.example.userapp.model.Product;

public class WeightPickerDialog {

    WeightPickerDialogBinding binding;
    Context context;
    Product product;
    Cart cart;

    public WeightPickerDialog(Context context, Product product, Cart cart) {
        this.context = context;
        this.product = product;
        this.cart = cart;
        binding = WeightPickerDialogBinding.inflate(LayoutInflater.from(context));
    }

    public void showDialog(OnWeightPickedListener listener) {
        new AlertDialog.Builder(context)
                .setTitle(product.name)
                .setView(binding.getRoot())
                .setPositiveButton("SELECT", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int kg = binding.numberPickerKg.getValue();
                        int g = binding.numberPickerG.getValue() * 50;

                        if (kg == 0 && g == 0) {
                            return;
                        }

                        cart.updateWeightBasedProductInCart(product, kg + (g/1000f));
                        listener.onWeightPicked(kg, g);
                    }
                })
                .setNegativeButton("REMOVE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        cart.removeWeightBasedProductFromCart(product);
                        listener.onRemove();
                    }
                })
                .show();

        setupNumberPickers();
        showPreviousData();

    }

    private void showPreviousData() {
        if (cart.map.containsKey(product.name)) {
            float quantity = cart.map.get(product.name).quantity;

            binding.numberPickerKg.setValue((int) quantity);
            binding.numberPickerG.setValue((int) ((quantity - (int) quantity) * 1000) / 50);
        }
    }

    private void setupNumberPickers() {
        float quantity = product.minQty;
        binding.numberPickerKg.setMinValue((int) quantity);
        binding.numberPickerKg.setMaxValue(10);
        binding.numberPickerG.setMinValue(((int) (quantity % 1000)) / 50);
        binding.numberPickerG.setMaxValue(19);
        binding.numberPickerKg.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int value) {
                return value + " kg";
            }
        });
        binding.numberPickerG.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int value) {
                return (value * 50) + " g";
            }
        });
        View firstItemKG = binding.numberPickerKg.getChildAt(0);
        if (firstItemKG != null) {
            firstItemKG.setVisibility(View.INVISIBLE);
        }
        View firstItemG = binding.numberPickerG.getChildAt(0);
        if (firstItemG != null) {
            firstItemG.setVisibility(View.INVISIBLE);
        }
    }

    public interface OnWeightPickedListener {
        void onWeightPicked(int kg, int g);
        void onRemove();
    }

}
