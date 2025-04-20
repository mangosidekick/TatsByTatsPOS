package com.example.tatsbytatspos.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tatsbytatspos.R;
import com.example.tatsbytatspos.model.Product;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private List<Product> productList;
    private boolean showStar;

    public ProductAdapter(List<Product> productList, boolean showStar) {
        this.productList = productList;
        this.showStar = showStar;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.product_cards, parent, false); // 'item_product' is the layout you provided
        return new ProductViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product currentProduct = productList.get(position);


        if (showStar) {
            holder.star.setVisibility(View.VISIBLE);
            holder.btnPlus.setVisibility(View.INVISIBLE);
            holder.btnMinus.setVisibility(View.INVISIBLE);
        } else {
            holder.star.setVisibility(View.GONE);
            holder.btnPlus.setVisibility(View.VISIBLE);
            holder.btnMinus.setVisibility(View.VISIBLE);
        }


        holder.productName.setText(currentProduct.getName());
        holder.productPrice.setText("₱" + currentProduct.getPrice());
        holder.productImage.setImageResource(currentProduct.getImageResource());



        // For Quantity (just an example, you can handle this accordingly)
        holder.btnMinus.setOnClickListener(v -> {
            // Implement logic for decrementing quantity
        });
        holder.btnPlus.setOnClickListener(v -> {
            // Implement logic for incrementing quantity
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productName, productPrice;
        ImageButton btnMinus, btnPlus, star;

        public ProductViewHolder(View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.product_image);
            productName = itemView.findViewById(R.id.product_name);
            productPrice = itemView.findViewById(R.id.product_price);
            btnMinus = itemView.findViewById(R.id.btn_minus);
            btnPlus = itemView.findViewById(R.id.btn_plus);
            star = itemView.findViewById(R.id.star);
        }
    }

}