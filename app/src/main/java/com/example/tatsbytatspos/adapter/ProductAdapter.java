package com.example.tatsbytatspos.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tatsbytatspos.R;
import com.example.tatsbytatspos.model.Product;

import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    public interface OnProductClickListener {
        void onProductClick(Product product);
    }

    private List<Product> productList;
    private boolean showStar;
    private boolean invQuantity;
    private Context context;
    private OnProductClickListener listener;

    public ProductAdapter(Context context, List<Product> productList, boolean showStar, boolean invQuantity, OnProductClickListener listener) {
        this.context = context;
        this.productList = (productList != null) ? productList : new ArrayList<>();
        this.showStar = showStar;
        this.invQuantity = invQuantity;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.product_cards, parent, false);
        return new ProductViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product currentProduct = productList.get(position);

        //the star visibility for the inventory page :)
        if (showStar) {
            holder.star.setVisibility(View.VISIBLE);
            holder.btnPlus.setVisibility(View.INVISIBLE);
            holder.btnMinus.setVisibility(View.INVISIBLE);
        } else {
            holder.star.setVisibility(View.GONE);
            holder.btnPlus.setVisibility(View.VISIBLE);
            holder.btnMinus.setVisibility(View.VISIBLE);
        }

        //the quantity visibility for the inventory page :)
        if (invQuantity) {
            holder.productInvQuantity.setVisibility(View.VISIBLE);
            holder.productQuantity.setVisibility(View.GONE);
        } else {
            holder.productInvQuantity.setVisibility(View.GONE);
            holder.productQuantity.setVisibility(View.VISIBLE);
        }

        //the actual products
        holder.productName.setText(currentProduct.getName());
        holder.productPrice.setText("₱" + currentProduct.getPrice());
        holder.productInvQuantity.setText(String.valueOf(currentProduct.getQuantity()));;
        Bitmap bitmap = BitmapFactory.decodeByteArray(currentProduct.getImage(), 0, currentProduct.getImage().length);
        holder.productImage.setImageBitmap(bitmap);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onProductClick(currentProduct);
            }
        });


        // add and subtracting quantity yippee
        holder.productQuantity.setText(String.valueOf(currentProduct.getOrderQuantity()));

        holder.btnPlus.setOnClickListener(v -> {
            int quantity = currentProduct.getOrderQuantity() + 1;
            currentProduct.setOrderQuantity(quantity);
            notifyItemChanged(position);
        });
        holder.btnMinus.setOnClickListener(v -> {
            int quantity = currentProduct.getOrderQuantity();
            if (quantity > 0) {
                currentProduct.setOrderQuantity(quantity - 1);
                notifyItemChanged(position);
            }
        });

        holder.productQuantity.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {

                String input = holder.productQuantity.getText().toString().trim();
                if (!input.isEmpty()) {
                    int enteredQty = Integer.parseInt(input);
                    currentProduct.setOrderQuantity(enteredQty);
                }
                return true;
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public void updateList(List<Product> newList) {
        if (newList != null) {
            productList = newList;
            notifyDataSetChanged();
        }
    }

    public List<Product> getProductList() {
        return productList;
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productName, productPrice, productQuantity, productInvQuantity;
        ImageButton btnMinus, btnPlus, star;

        public ProductViewHolder(View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.product_image);
            productQuantity = itemView.findViewById(R.id.quantity_text);
            productInvQuantity = itemView.findViewById(R.id.quantity_inventory_text);
            productName = itemView.findViewById(R.id.product_name);
            productPrice = itemView.findViewById(R.id.product_price);
            btnMinus = itemView.findViewById(R.id.btn_minus);
            btnPlus = itemView.findViewById(R.id.btn_plus);
            star = itemView.findViewById(R.id.star);
        }
    }
}