package com.example.userapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.userapp.constants.Constants;
import com.example.userapp.databinding.ActivityCartBinding;
import com.example.userapp.databinding.CartItemViewBinding;
import com.example.userapp.dialogs.UserDetailsDialog;
import com.example.userapp.fcm.FCMSender;
import com.example.userapp.fcm.MessageFormatter;
import com.example.userapp.model.Cart;
import com.example.userapp.model.CartItem;
import com.example.userapp.model.Order;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firestore.v1.DocumentTransform;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class CartActivity extends AppCompatActivity {

    ActivityCartBinding binding;
    Cart cart;
    MyApp myApp;

    SharedPreferences preferences;
    String sharedPreferencesFile = "com.example.android.userapp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferences = getSharedPreferences(sharedPreferencesFile, MODE_PRIVATE);

        myApp = (MyApp) getApplicationContext();

        Intent intent = getIntent();
        cart = (Cart) intent.getSerializableExtra("data");

        showCartItems();

        showItemsAndPrice();

    }


    private void showCartItems() {
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

            setupDeleteButton(b, map.getKey(), map.getValue());


            binding.cartItems.addView(b.getRoot());
        }
    }

    private void setupDeleteButton(CartItemViewBinding b, String key, CartItem value) {
        b.deleteCartItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                cart.removeItemWithKey(key, value);

                binding.cartItems.removeView(b.getRoot());

                showItemsAndPrice();
            }
        });
    }

    private void showItemsAndPrice() {
        binding.items.setText("Items : " +  cart.noOfItems);
        binding.price.setText("Price : Rs. " + cart.totalPrice);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent latestCartIntent = new Intent();
            latestCartIntent.putExtra("new", cart);
            setResult(RESULT_OK, latestCartIntent);
            finish();
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.place_order_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.place_order) {
            placeOrder();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void placeOrder() {
//        Toast.makeText(this, "Place Order!", Toast.LENGTH_SHORT).show();

        List<CartItem> orderItems = new ArrayList<>();

        for (Map.Entry<String, CartItem> map : cart.map.entrySet()) {
            orderItems.add(map.getValue());
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        String orderID = user.getUid() + "" + cart.noOfItems + "" + cart.totalPrice;
        Order newOrder = new Order(
                orderID,
                Timestamp.now(),
                preferences.getString(Constants.USER_NAME, ""),
                preferences.getString(Constants.USER_PHONE_NO, ""),
                preferences.getString(Constants.USER_ADDRESS, ""),
                orderItems,
                Order.OrderStatus.PLACED,
                cart.totalPrice,
                cart.noOfItems
        );

        myApp.db.collection(Constants.ORDERS).document(orderID)
                .set(newOrder)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(CartActivity.this, "Order Successfully Placed!", Toast.LENGTH_SHORT).show();
                        new FCMSender().send(MessageFormatter.getSampleMessage("admin", "New Order!", "new order from " + preferences.getString(Constants.USER_NAME, "")), new Callback() {
                            @Override
                            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                                Log.e("bxj", "Failure");
                            }

                            @Override
                            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                Log.e("cjsc", "Success");
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CartActivity.this, "Failed to place order!", Toast.LENGTH_SHORT).show();
                    }
                });

    }

}
