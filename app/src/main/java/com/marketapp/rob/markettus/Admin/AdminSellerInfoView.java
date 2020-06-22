package com.marketapp.rob.markettus.Admin;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.marketapp.rob.markettus.R;

public class AdminSellerInfoView extends AppCompatActivity {

    private TextView sName, sPhone, sAddress, sEmail, sSID;
    private String sid, sellerPhone;
    private Button adminCallToSellerBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_seller_info_view);

        sid = getIntent().getStringExtra("sid");

        sName = (TextView)findViewById(R.id.admin_seller_name);
        sPhone = (TextView)findViewById(R.id.admin_seller_phone);
        sAddress = (TextView)findViewById(R.id.admin_seller_address);
        sEmail = (TextView)findViewById(R.id.admin_seller_email);
        sSID = (TextView)findViewById(R.id.admin_seller_sid);
        adminCallToSellerBtn = (Button)findViewById(R.id.admin_call_to_seller_btn);

        displaySellerInfo();

        adminCallToSellerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + sellerPhone));
                startActivity(intent);
            }
        });
    }

    private void displaySellerInfo() {
        DatabaseReference adminSellerInfoRef = FirebaseDatabase.getInstance().getReference().child("Sellers").child(sid);

        adminSellerInfoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String name = dataSnapshot.child("name").getValue().toString();
                    String phone = dataSnapshot.child("phone").getValue().toString();
                    String address = dataSnapshot.child("address").getValue().toString();
                    String email = dataSnapshot.child("email").getValue().toString();
                    String sid = dataSnapshot.child("sid").getValue().toString();

                    sName.setText("Seller name: " + name);
                    sPhone.setText("Seller phone: " + phone);
                    sAddress.setText("Seller address: " + address);
                    sEmail.setText("Seller email: " + email);
                    sSID.setText("Seller ID: " + sid);
                    sellerPhone = phone;

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
