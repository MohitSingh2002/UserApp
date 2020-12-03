package com.example.userapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.userapp.adapter.ProductesAdapter;
import com.example.userapp.constants.Constants;
import com.example.userapp.databinding.ActivityMainBinding;
import com.example.userapp.model.Cart;
import com.example.userapp.model.Inventory;
import com.example.userapp.model.Product;
import com.example.userapp.model.Varient;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

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

    SharedPreferences preferences;
    String sharedPreferencesFile = "com.example.android.userapp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferences = getSharedPreferences(sharedPreferencesFile, MODE_PRIVATE);

        FirebaseMessaging.getInstance().subscribeToTopic(Constants.USER_TOPIC);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.logout) {
            logout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        AuthUI.getInstance()
                .signOut(MainActivity.this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        updateUserLoggedInStateInSharedPrefs();
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    }
                });
    }

    private void updateUserLoggedInStateInSharedPrefs() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(Constants.LOGIN_STATE, false);
        editor.apply();
    }

}
