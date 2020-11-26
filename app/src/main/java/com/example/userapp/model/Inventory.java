package com.example.userapp.model;

import java.util.List;

public class Inventory {

    public List<Product> productList;

    public Inventory() {
    }

    public Inventory(List<Product> productList) {
        this.productList = productList;
    }

}
