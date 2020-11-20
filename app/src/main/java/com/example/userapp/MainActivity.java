package com.example.userapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.userapp.adapter.ProductesAdapter;
import com.example.userapp.databinding.ActivityMainBinding;
import com.example.userapp.model.Cart;
import com.example.userapp.model.Product;
import com.example.userapp.model.Varient;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    Cart cart = new Cart();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupList();

        binding.checkoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CartActivity.class);
                intent.putExtra("data", cart);
                startActivity(intent);
            }
        });

    }

    private void setupList() {
        List<Product> list = new ArrayList<>();
        Product apple = new Product("Apple", 10, 1);
        Product banana = new Product("Banana");
        Varient banana1 = new Varient("1kg", 20);
        Varient banana2 = new Varient("2kg", 30);
        Varient banana3 = new Varient("3kg", 40);
        banana.varientsList.add(banana1);
        banana.varientsList.add(banana2);
        banana.varientsList.add(banana3);
        Product mango = new Product("Mango");
        Varient mango1 = new Varient("5kg", 50);
        mango.varientsList.add(mango1);

        Product kiwi = new Product("Kiwi");
        Varient kiwi1 = new Varient("2kg", 25);
        kiwi.varientsList.add(kiwi1);

        list.add(apple);
        list.add(banana);
        list.add(mango);
        list.add(kiwi);

        ProductesAdapter adapter = new ProductesAdapter(this, list, cart);

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        DividerItemDecoration itemDecor = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        binding.recyclerView.addItemDecoration(itemDecor);

        binding.recyclerView.setAdapter(adapter);
    }

    public void updateCheckOutSummary() {
        if (cart.noOfItems == 0) {
            binding.checkout.setVisibility(View.GONE);
        } else {
            binding.checkout.setVisibility(View.VISIBLE);
            binding.cartSummary.setText("Total: Rs. " + cart.totalPrice + "\n" + cart.noOfItems + " items");
        }
    }

}
