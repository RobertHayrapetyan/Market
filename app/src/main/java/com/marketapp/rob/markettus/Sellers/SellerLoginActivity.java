package com.marketapp.rob.markettus.Sellers;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.marketapp.rob.markettus.R;

public class SellerLoginActivity extends AppCompatActivity {

    private Button loginSellerBtn, registerBtn;
    private EditText sellerLoginEmail, sellerLoginPassword;
    private ProgressDialog loadingBar;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_login);

        loadingBar = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();

        sellerLoginEmail = (EditText)findViewById(R.id.seller_login_email);
        sellerLoginPassword = (EditText)findViewById(R.id.seller_login_password);
        loginSellerBtn = (Button) findViewById(R.id.seller_login_btn);
        registerBtn = (Button)findViewById(R.id.seller_go_to_register_btn);


        loginSellerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sellerLogin();
            }
        });
        sellerLoginPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if ((keyEvent!=null && (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (i == EditorInfo.IME_ACTION_DONE)){
                    loginSellerBtn.performClick();
                }
                return false;
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SellerLoginActivity.this, SellerRegistrationActivity.class);
                startActivity(intent);
            }
        });
    }

    private void sellerLogin() {

        final String email = sellerLoginEmail.getText().toString().replace(" ", "");
        final String password = sellerLoginPassword.getText().toString().replace(" ", "");

        if (!email.equals("") && !password.equals("")) {

            loadingBar.setTitle("Seller Account Login");
            loadingBar.setMessage("Please wait, while we checking credentials...");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        loadingBar.dismiss();
                        Toast.makeText(SellerLoginActivity.this, "You are registered successfully", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(SellerLoginActivity.this, SellerHomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }else{
                        loadingBar.dismiss();
                        Toast.makeText(SellerLoginActivity.this, "Error, try again.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else {
            Toast.makeText(SellerLoginActivity.this, "Please, compile the login form", Toast.LENGTH_SHORT).show();

        }

    }
}
