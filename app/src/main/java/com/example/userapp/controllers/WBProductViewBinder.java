package com.example.userapp.controllers;

import com.example.userapp.databinding.WbOrMultiVbItemBinding;
import com.example.userapp.model.Cart;
import com.example.userapp.model.Product;

public class WBProductViewBinder {

    WbOrMultiVbItemBinding binding;
    Product product;
    Cart cart;

    public WBProductViewBinder(WbOrMultiVbItemBinding binding, Product product, Cart cart) {
        this.binding = binding;
        this.product = product;
        this.cart = cart;
    }

    public void bindData() {}

}
