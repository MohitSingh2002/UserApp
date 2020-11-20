package com.example.userapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.userapp.databinding.ActivityCartBinding;
import com.example.userapp.databinding.CartItemViewBinding;
import com.example.userapp.model.Cart;
import com.example.userapp.model.CartItem;

import java.util.Map;

public class CartActivity extends AppCompatActivity {

    ActivityCartBinding binding;
//    Cart cart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        Cart cart = (Cart) intent.getSerializableExtra("data");

        showCartItems(cart);

    }

    private void showCartItems(Cart cart) {
        for(Map.Entry<String, CartItem> map : cart.map.entrySet()) {
            CartItemViewBinding b = CartItemViewBinding.inflate(
                    getLayoutInflater()
            );

            b.cartItemName.setText("" + map.getKey());

            b.cartItemPrice.setText("Rs. " + map.getValue().price);

            if (map.getValue().name.contains("kg")) {
                b.cartItemWeight.setText((int) (map.getValue().quantity) + " x Rs. " + (map.getValue().price) / ((int) (map.getValue().quantity)));
            } else {
                b.cartItemWeight.setText((int) (map.getValue().quantity) + "kg x Rs. " + (map.getValue().price) / ((int) (map.getValue().quantity)) + "/kg");
            }


            binding.cartItems.addView(b.getRoot());
        }
    }

}
