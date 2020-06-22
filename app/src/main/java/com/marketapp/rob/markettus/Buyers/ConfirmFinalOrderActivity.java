package com.marketapp.rob.markettus.Buyers;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.marketapp.rob.markettus.Prevalent.Prevalent;
import com.marketapp.rob.markettus.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class ConfirmFinalOrderActivity extends AppCompatActivity {


    private EditText nameET, phoneET, addressET, cityET;
    private Button confirmFinalOerderBtn;

    private String totalPrice = "", sellerName = "", sid = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_final_order);

        totalPrice = getIntent().getStringExtra("Total price");
        sellerName = getIntent().getStringExtra("sellerName");
        sid = getIntent().getStringExtra("sid");

        confirmFinalOerderBtn = (Button)findViewById(R.id.confirm_final_order_btn);
        nameET = (EditText)findViewById(R.id.shipment_name);
        phoneET = (EditText)findViewById(R.id.shipment_phone);
        addressET = (EditText)findViewById(R.id.shipment_address);
        cityET = (EditText)findViewById(R.id.shipment_city);

        displayInfo();

        confirmFinalOerderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                check();
            }
        });
    }

    private void check() {
        if (TextUtils.isEmpty(nameET.getText().toString())){
            Toast.makeText(ConfirmFinalOrderActivity.this, "Please enter your name", Toast.LENGTH_SHORT).show();
        } else  if (TextUtils.isEmpty(phoneET.getText().toString())){
            Toast.makeText(ConfirmFinalOrderActivity.this, "Please enter your phone number", Toast.LENGTH_SHORT).show();
        } else  if (TextUtils.isEmpty(addressET.getText().toString())){
            Toast.makeText(ConfirmFinalOrderActivity.this, "Please enter your home address", Toast.LENGTH_SHORT).show();
        } else  if (TextUtils.isEmpty(cityET.getText().toString())){
            Toast.makeText(ConfirmFinalOrderActivity.this, "Please enter your city", Toast.LENGTH_SHORT).show();
        } else {
            confirmOrder();
        }
    }

    private void confirmOrder() {

        final String saveCurrentDate, saveCurrentTime;

        Date date = new Date();

        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MM-yyyy");
        saveCurrentDate = currentDate.format(date);

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss");
        saveCurrentTime = currentTime.format(date);

        final DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference().child("Orders").child("Seller View").child(sid).child(Prevalent.currentOnlineUser.getPhone());
        final DatabaseReference adminOrderRef = FirebaseDatabase.getInstance().getReference().child("Orders").child("Admin View").child(sid);

        final HashMap<String, Object> orderMap = new HashMap<>();
        orderMap.put("totalAmount", totalPrice);
        orderMap.put("name", nameET.getText().toString());
        orderMap.put("phone", phoneET.getText().toString());
        orderMap.put("date", saveCurrentDate);
        orderMap.put("time", saveCurrentTime);
        orderMap.put("address", addressET.getText().toString());
        orderMap.put("city", cityET.getText().toString());
        orderMap.put("state", "not shipped");
        orderMap.put("sellerName", sellerName);
        orderMap.put("sid", sid);

        orderRef.updateChildren(orderMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    adminOrderRef.updateChildren(orderMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            FirebaseDatabase.getInstance().getReference()
                                    .child("Orders")
                                    .child("User View")
                                    .child(Prevalent.currentOnlineUser.getPhone())
                                    .updateChildren(orderMap)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                FirebaseDatabase.getInstance().getReference()
                                                        .child("Cart List")
                                                        .child("User View")
                                                        .child(Prevalent.currentOnlineUser.getPhone())
                                                        .removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    Toast.makeText(ConfirmFinalOrderActivity.this, "Your order confirmed successfully", Toast.LENGTH_SHORT).show();

                                                                    Intent intent = new Intent(ConfirmFinalOrderActivity.this, HomeActivity.class);
                                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                                    startActivity(intent);
                                                                    finish();
                                                                }
                                                            }
                                                        });
                                            }
                                        }
                                    });
                        }
                    });
                }
            }
        });
    }

    private void displayInfo(){
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference()
                .child("Users")
                .child(Prevalent.currentOnlineUser.getPhone());

        ref.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()){
                    String name = dataSnapshot.child("name").getValue().toString();
                    String phone = dataSnapshot.child("phone").getValue().toString();
                    String address = dataSnapshot.child("address").getValue().toString();
                    String city = dataSnapshot.child("city").getValue().toString();

                    nameET.setText(name);
                    phoneET.setText(phone);
                    addressET.setText(address);
                    cityET.setText(city);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
