package com.example.userapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.userapp.controllers.SingleVBProductViewBinder;
import com.example.userapp.controllers.WBProductViewBinder;
import com.example.userapp.databinding.SingleVbItemBinding;
import com.example.userapp.databinding.WbOrMultiVbItemBinding;
import com.example.userapp.model.Cart;
import com.example.userapp.model.Product;

import java.util.List;

public class ProductesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int TYPE_SINGLE_VB = 0, TYPE_WB_OR_VB_MULTI = 1;

    Context context;
    List<Product> productList;
    Cart cart;

    public ProductesAdapter(Context context, List<Product> productList, Cart cart) {
        this.context = context;
        this.productList = productList;
        this.cart = cart;
    }

    @Override
    public int getItemViewType(int position) {
        Product product = productList.get(position);
        if (product.type == TYPE_SINGLE_VB || product.varientsList.size() > 1) {
            return TYPE_WB_OR_VB_MULTI;
        }
        return TYPE_SINGLE_VB;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == TYPE_SINGLE_VB) {
            SingleVbItemBinding b = SingleVbItemBinding.inflate(
                    LayoutInflater.from(context),
                    parent,
                    false
            );
            return new SingleVBViewHolder(b);
        } else {
            WbOrMultiVbItemBinding b = WbOrMultiVbItemBinding.inflate(
                    LayoutInflater.from(context),
                    parent,
                    false
            );
            return new WBViewHolder(b);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Product product = productList.get(position);
        if (getItemViewType(position) == TYPE_SINGLE_VB) {
            SingleVBViewHolder viewHolder = (SingleVBViewHolder) holder;

            viewHolder.binding.nameSingleVb.setText(product.name + " " + product.varientsList.get(0).name);
            viewHolder.binding.priceSingleVb.setText(product.priceString());

            new SingleVBProductViewBinder(viewHolder.binding, product, cart).bindData();

        } else {
            WBViewHolder viewHolder = (WBViewHolder) holder;

            ((WBViewHolder) holder).binding.nameWb.setText("" + product.name);
            viewHolder.binding.priceWb.setText(product.priceString());

            new WBProductViewBinder(viewHolder.binding, product, cart).bindData();
        }
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    class SingleVBViewHolder extends RecyclerView.ViewHolder {

        SingleVbItemBinding binding;

        public SingleVBViewHolder(@NonNull SingleVbItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    class WBViewHolder extends RecyclerView.ViewHolder {

        WbOrMultiVbItemBinding binding;

        public WBViewHolder(@NonNull WbOrMultiVbItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

}
