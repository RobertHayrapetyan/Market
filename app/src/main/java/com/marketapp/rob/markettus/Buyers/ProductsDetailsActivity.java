package com.marketapp.rob.markettus.Buyers;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.marketapp.rob.markettus.Model.Products;
import com.marketapp.rob.markettus.Prevalent.Prevalent;
import com.marketapp.rob.markettus.R;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class ProductsDetailsActivity extends AppCompatActivity {

    private Button addToCartBtn;
    private ImageView productImage;
    private TextView productName, productDescription, productPrice, productsTotalPrice, productSellerName;
    private ElegantNumberButton numberButton;
    private String productId = "", state = "Normal", sid = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products_detail);

        productId = getIntent().getStringExtra("pid");
        sid = getIntent().getStringExtra("sid");

        addToCartBtn = (Button)findViewById(R.id.add_product_to_cart_btn);
        numberButton = (ElegantNumberButton)findViewById(R.id.number_btn);
        productImage = (ImageView)findViewById(R.id.product_image);
        productName = (TextView)findViewById(R.id.product_name);
        productDescription = (TextView)findViewById(R.id.product_description);
        productPrice = (TextView)findViewById(R.id.product_price);
        productsTotalPrice = (TextView)findViewById(R.id.products_total_sum);
        productSellerName = (TextView)findViewById(R.id.seller_name);

        getProductDetails(productId);


        addToCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (state.equals("Order shipped") || state.equals("Order placed")){
                    Toast.makeText(ProductsDetailsActivity.this, "You can add purchase more products, once your order is shipped or confirmed.", Toast.LENGTH_SHORT).show();
                }else {
                    addingToCartList();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        checkOrderState();
    }

    private void addingToCartList() {

        String saveCurrentDate, saveCurrentTime;

        Date date = new Date();

        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MM-yyyy");
        saveCurrentDate = currentDate.format(date);

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss");
        saveCurrentTime = currentTime.format(date);

        final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");
        final HashMap<String, Object> cartMap = new HashMap<>();
        cartMap.put("pid", productId);
        cartMap.put("sid", sid);
        cartMap.put("name", productName.getText().toString());
        cartMap.put("price", productPrice.getText().toString());
        cartMap.put("date", saveCurrentDate);
        cartMap.put("time", saveCurrentTime);
        cartMap.put("quantity", numberButton.getNumber());
        cartMap.put("discount", "");
        cartMap.put("sellerName", productSellerName.getText().toString());

        cartListRef.child("Admin View").child(Prevalent.currentOnlineUser.getPhone())
                .child("Products").child(productId).updateChildren(cartMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    cartListRef.child("User View").child(Prevalent.currentOnlineUser.getPhone())
                            .child("Products").child(productId).updateChildren(cartMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(ProductsDetailsActivity.this, "Added to cart list", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(ProductsDetailsActivity.this, HomeActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            }
                        }
                    });
                }
            }
        });
    }

    private void getProductDetails(final String productId) {
        final DatabaseReference productdRef = FirebaseDatabase.getInstance().getReference().child("Products");
        productdRef.child(productId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    Products products = dataSnapshot.getValue(Products.class);
                    productName.setText(products.getName());
                    productDescription.setText(products.getDescription());
                    productPrice.setText(products.getPrice());
                    productSellerName.setText(products.getSellerName());

                    Picasso.get().load(products.getImage()).into(productImage);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void checkOrderState(){
        DatabaseReference ordersRef;

        ordersRef = FirebaseDatabase.getInstance().getReference().child("Orders").child(Prevalent.currentOnlineUser.getPhone());
        ordersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){

                    String shipmentState = dataSnapshot.child("state").getValue().toString();

                    if (shipmentState.equals("shipped")){
                        state = "Order shipped";
                    } else if (shipmentState.equals("not shipped")){
                        state = "Order placed";

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
