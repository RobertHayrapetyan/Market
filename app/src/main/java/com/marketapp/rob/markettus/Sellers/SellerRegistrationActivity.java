package com.marketapp.rob.markettus.Sellers;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.marketapp.rob.markettus.Buyers.MainActivity;
import com.marketapp.rob.markettus.R;

import java.util.HashMap;

public class SellerRegistrationActivity extends AppCompatActivity {

    private Button registerBtn;
    private EditText sellerName, sellerPhone, sellerEmail, sellerShopAddress, sellerPassword;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sellers_registration);

        mAuth = FirebaseAuth.getInstance();

        loadingBar = new ProgressDialog(this);

        sellerName = (EditText)findViewById(R.id.seller_name);
        sellerPhone = (EditText)findViewById(R.id.seller_phone_number);
        sellerEmail = (EditText)findViewById(R.id.seller_email);
        sellerShopAddress = (EditText)findViewById(R.id.seller_shop_address);
        sellerPassword = (EditText)findViewById(R.id.seller_password);

        registerBtn = (Button)findViewById(R.id.seller_register_btn);

        
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerSeller();
            }
        });
    }

    private void registerSeller() {

        final String name = sellerName.getText().toString();
        final String phone = sellerPhone.getText().toString();
        final String email = sellerEmail.getText().toString();
        final String shopAddress = sellerShopAddress.getText().toString();
        final String password = sellerPassword.getText().toString();

        if (!name.equals("") && !phone.equals("") && !email.equals("") && !shopAddress.equals("") && !password.equals("")){

            loadingBar.setTitle("Creating Seller Account");
            loadingBar.setMessage("Please wait, while we checking credentials...");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                final DatabaseReference rootRef;
                                rootRef = FirebaseDatabase.getInstance().getReference();

                                String sid = mAuth.getCurrentUser().getUid();

                                HashMap<String, Object> sellerMap = new HashMap<>();
                                sellerMap.put("sid", sid);
                                sellerMap.put("phone", phone);
                                sellerMap.put("email", email);
                                sellerMap.put("address", shopAddress);
                                sellerMap.put("name", name);

                                rootRef.child("Sellers").child(sid).updateChildren(sellerMap)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                loadingBar.dismiss();
                                                Toast.makeText(SellerRegistrationActivity.this, "You are registered successfully", Toast.LENGTH_SHORT).show();

                                                Intent intent = new Intent(SellerRegistrationActivity.this, SellerHomeActivity.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(intent);
                                                finish();
                                            }
                                        });
                            } else {
                                loadingBar.dismiss();
                                Toast.makeText(SellerRegistrationActivity.this, "Error" + task.getException().toString(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });

        }else {
            loadingBar.dismiss();
            Toast.makeText(SellerRegistrationActivity.this, "Please, compile the register form", Toast.LENGTH_SHORT).show();
        }
    }
}
