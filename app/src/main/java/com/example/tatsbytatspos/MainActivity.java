package com.example.tatsbytatspos;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private List<Product> productList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Initialize RecyclerView and set up the LayoutManager
        recyclerView = findViewById(R.id.menuRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        int numberOfColumns = 2; // Set the number of columns you want
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, numberOfColumns);
        recyclerView.setLayoutManager(gridLayoutManager);

        // Initialize the product list
        productList = new ArrayList<>();
        productList.add(new Product(R.drawable.product_image, "Product 1", 29.99));
        productList.add(new Product(R.drawable.product_image, "Product 2", 19.99));
        productList.add(new Product(R.drawable.product_image, "Product 3", 39.99));

        // Set up the adapter
        productAdapter = new ProductAdapter(productList);
        recyclerView.setAdapter(productAdapter);


    }
}