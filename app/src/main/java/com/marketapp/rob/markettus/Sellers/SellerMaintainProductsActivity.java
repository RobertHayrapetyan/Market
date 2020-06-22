package com.marketapp.rob.markettus.Sellers;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.marketapp.rob.markettus.R;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class SellerMaintainProductsActivity extends AppCompatActivity {

    private Button applyChangesBtn, deleteBtn;
    private ImageView imageView;
    private EditText name, price, description;
    private String productId = "";
    private DatabaseReference productsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_maintain_products);

        productId = getIntent().getStringExtra("pid");
        productsRef = FirebaseDatabase.getInstance().getReference().child("Products").child(productId);

        name = (EditText)findViewById(R.id.product_name_maintain);
        price = (EditText)findViewById(R.id.product_price_maintain);
        description = (EditText)findViewById(R.id.product_description_maintain);

        imageView = (ImageView)findViewById(R.id.product_image_maintain);
        applyChangesBtn = (Button)findViewById(R.id.apply_changes_btn);
        deleteBtn = (Button)findViewById(R.id.delete_btn);

        displaySpecificProductInfo();

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteThisProduct();
            }
        });

        applyChangesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                applyChanges();
            }
        });
    }

    private void deleteThisProduct() {
        productsRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(SellerMaintainProductsActivity.this, "The product is deleted successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SellerMaintainProductsActivity.this, SellerHomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    private void applyChanges() {
        String pName = name.getText().toString();
        String pDescription = description.getText().toString();
        String pPrice = price.getText().toString();

        if (pName.equals("")){
            Toast.makeText(SellerMaintainProductsActivity.this, "Please enter product name", Toast.LENGTH_SHORT).show();
        }else if (pDescription.equals("")){
            Toast.makeText(SellerMaintainProductsActivity.this, "Please enter product description", Toast.LENGTH_SHORT).show();
        }else if (pPrice.equals("")){
            Toast.makeText(SellerMaintainProductsActivity.this, "Please enter product price", Toast.LENGTH_SHORT).show();
        }else {
            HashMap<String, Object> productMap = new HashMap<>();
            productMap.put("pid", productId);
            productMap.put("price", pPrice);
            productMap.put("name", pName);
            productMap.put("description", pDescription);

            productsRef.updateChildren(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(SellerMaintainProductsActivity.this, "Changes applied successfully", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(SellerMaintainProductsActivity.this, SellerHomeActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }
                }
            });
        }
    }

    private void displaySpecificProductInfo() {
        productsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String pName = dataSnapshot.child("name").getValue().toString();
                    String pDescription = dataSnapshot.child("description").getValue().toString();
                    String pPrice = dataSnapshot.child("price").getValue().toString();
                    String pImage = dataSnapshot.child("image").getValue().toString();

                    name.setText(pName);
                    description.setText(pDescription);
                    price.setText(pPrice);

                    Picasso.get().load(pImage).into(imageView);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
