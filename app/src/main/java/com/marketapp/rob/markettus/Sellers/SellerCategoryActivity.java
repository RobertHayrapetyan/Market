package com.marketapp.rob.markettus.Sellers;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.marketapp.rob.markettus.R;

public class SellerCategoryActivity extends AppCompatActivity {

    private ImageView categoryWine, categoryBeer, categoryAlcohol;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_category);

        categoryWine = (ImageView)findViewById(R.id.category_wine);
        categoryBeer = (ImageView)findViewById(R.id.category_beer);
        categoryAlcohol = (ImageView)findViewById(R.id.category_alcohol);


        categoryWine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SellerCategoryActivity.this, SellerAddNewProductActivity.class);
                intent.putExtra("category", "categoryWine");
                startActivity(intent);
            }
        });

        categoryBeer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SellerCategoryActivity.this, SellerAddNewProductActivity.class);
                intent.putExtra("category", "categoryBeer");
                startActivity(intent);
            }
        });

        categoryAlcohol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SellerCategoryActivity.this, SellerAddNewProductActivity.class);
                intent.putExtra("category", "categoryAlcohol");
                startActivity(intent);
            }
        });
    }
}
