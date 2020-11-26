package com.example.userapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.userapp.adapter.ProductesAdapter;
import com.example.userapp.constants.Constants;
import com.example.userapp.databinding.ActivityMainBinding;
import com.example.userapp.model.Cart;
import com.example.userapp.model.Inventory;
import com.example.userapp.model.Product;
import com.example.userapp.model.Varient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    Cart cart = new Cart();
    private ProductesAdapter adapter;
    private List<Product> list;
    private MyApp app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

//        setupList();

        setup();
        fetchProductsListFromCloudFirestore();

        binding.checkoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CartActivity.class);
                intent.putExtra("data", cart);
                startActivityForResult(intent, 1);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Cart newCart = (Cart) data.getSerializableExtra("new");

                cart.changeCart(newCart);

                adapter.notifyDataSetChanged();

                updateCheckOutSummary();
            }
        }

    }

    private void setup() {
        app = (MyApp) getApplicationContext();
    }

    private void fetchProductsListFromCloudFirestore() {

        if (app.isOffline()) {
            app.showToast(MainActivity.this, "No Internet!");
            return;
        }

        app.showLoadingDialog(this);

        app.db.collection(Constants.INVENTORY).document(Constants.PRODUCTS)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            Inventory inventory = documentSnapshot.toObject(Inventory.class);
                            list = inventory.productList;
                        } else {
                            list = new ArrayList<>();
                        }
                        setupList();
                        app.hideLoadingDialog();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        app.hideLoadingDialog();
                    }
                });

    }

    private void setupList() {

//        list = new ArrayList<>();
//        Product apple = new Product("Apple", 10, 1);
//        Product banana = new Product("Banana");
//        Varient banana1 = new Varient("1kg", 20);
//        Varient banana2 = new Varient("2kg", 30);
//        Varient banana3 = new Varient("3kg", 40);
//        banana.varientsList.add(banana1);
//        banana.varientsList.add(banana2);
//        banana.varientsList.add(banana3);
//        Product mango = new Product("Mango");
//        Varient mango1 = new Varient("5kg", 50);
//        mango.varientsList.add(mango1);
//
//        Product kiwi = new Product("Kiwi");
//        Varient kiwi1 = new Varient("2kg", 25);
//        kiwi.varientsList.add(kiwi1);
//
//        list.add(apple);
//        list.add(banana);
//        list.add(mango);
//        list.add(kiwi);

        adapter = new ProductesAdapter(this, list, cart);

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
