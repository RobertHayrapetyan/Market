package com.marketapp.rob.markettus.Sellers;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.marketapp.rob.markettus.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class SellerAddNewProductActivity extends AppCompatActivity {

    private String categoryName, productName, productDescription, productPrice, saveCurrentDate, saveCurrentTime;
    private Button productAddBtn;
    private EditText productNameInput, productDescriptionInput, productPriceInput;
    private ImageView productImageSelect;
    private static final int GALLERY_PICK = 1;
    private Uri imageUri;
    private String productRandomKey, downloadImageUri;

    private StorageReference productImageRef;
    private DatabaseReference productRef, sellersRef;
    private ProgressDialog loadingBar;

    private String sellerName, sellerPhone, sellerEmail, sellerAddress, sellerID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_add_new_product);

        categoryName = getIntent().getExtras().get("category").toString();
        productImageRef = FirebaseStorage.getInstance().getReference().child("Product images");
        productRef = FirebaseDatabase.getInstance().getReference().child("Products");
        sellersRef = FirebaseDatabase.getInstance().getReference().child("Sellers");

        Toast.makeText(SellerAddNewProductActivity.this, categoryName, Toast.LENGTH_SHORT).show();

        productAddBtn = (Button)findViewById(R.id.product_add_btn);
        productNameInput = (EditText) findViewById(R.id.product_name);
        productDescriptionInput = (EditText) findViewById(R.id.product_description);
        productPriceInput = (EditText) findViewById(R.id.product_price);
        productImageSelect = (ImageView)findViewById(R.id.select_product_image);

        loadingBar = new ProgressDialog(this);


        productImageSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });

        productAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateProductData();
            }
        });

        sellersRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            sellerName = dataSnapshot.child("name").getValue().toString();
                            sellerAddress = dataSnapshot.child("address").getValue().toString();
                            sellerEmail = dataSnapshot.child("email").getValue().toString();
                            sellerPhone = dataSnapshot.child("phone").getValue().toString();
                            sellerID = dataSnapshot.child("sid").getValue().toString();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_PICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==GALLERY_PICK && resultCode==RESULT_OK && data!=null){
            imageUri = data.getData();
            productImageSelect.setImageURI(imageUri);
        }
    }

    private void validateProductData()
    {
        productName = productNameInput.getText().toString();
        productDescription = productDescriptionInput.getText().toString();
        productPrice = productPriceInput.getText().toString();

        if (imageUri == null){
            Toast.makeText(SellerAddNewProductActivity.this, "Please choose product image...", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(productName)){
            Toast.makeText(SellerAddNewProductActivity.this, "Please enter product name...", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(productDescription)){
            Toast.makeText(SellerAddNewProductActivity.this, "Please enter product description...", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(productPrice)){
            Toast.makeText(SellerAddNewProductActivity.this, "Please enter product price...", Toast.LENGTH_SHORT).show();
        }else {
            storageProductInformation();
        }
    }

    private void storageProductInformation() {

        loadingBar.setTitle("Add new product");
        loadingBar.setMessage("Please wait, adding new product...");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        Date date = new Date();

        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MM-yyyy");
        saveCurrentDate = currentDate.format(date);

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss");
        saveCurrentTime = currentTime.format(date);

        productRandomKey = saveCurrentDate + "_" + saveCurrentTime;

        final StorageReference filePath = productImageRef.child(imageUri.getLastPathSegment() + productRandomKey + ".jpg");

        final UploadTask uploadTask = filePath.putFile(imageUri);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                loadingBar.dismiss();
                String message = e.toString();
                Toast.makeText(SellerAddNewProductActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();

            }
        }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                Toast.makeText(SellerAddNewProductActivity.this, "Product image uploaded successfully...", Toast.LENGTH_SHORT).show();

                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>()
                {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()){
                            loadingBar.dismiss();
                            throw task.getException();
                        }

                        downloadImageUri = filePath.getDownloadUrl().toString();
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()){
                            downloadImageUri = task.getResult().toString();
                            Toast.makeText(SellerAddNewProductActivity.this, "Got the product image successfully...", Toast.LENGTH_SHORT).show();
                            saveProductInfoToDatabase();
                        }
                    }
                });
            }
        });
    }

    private void saveProductInfoToDatabase() {
        HashMap<String, Object> productMap = new HashMap<>();
        productMap.put("pid", productRandomKey);
        productMap.put("date", saveCurrentDate);
        productMap.put("time", saveCurrentTime);
        productMap.put("image", downloadImageUri);
        productMap.put("category", categoryName);
        productMap.put("price", productPrice);
        productMap.put("name", productName);
        productMap.put("description", productDescription);

        productMap.put("sellerName", sellerName);
        productMap.put("sellerAddress", sellerAddress);
        productMap.put("sellerEmail", sellerEmail);
        productMap.put("sellerPhone", sellerPhone);
        productMap.put("sid", sellerID);
        productMap.put("state", "not approved");

        productRef.child(productRandomKey).updateChildren(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){

                    Intent intent = new Intent(SellerAddNewProductActivity.this, SellerHomeActivity.class);
                    startActivity(intent);

                    loadingBar.dismiss();
                    Toast.makeText(SellerAddNewProductActivity.this, "Product is adding successfully...", Toast.LENGTH_SHORT).show();
                }else {
                    loadingBar.dismiss();
                    String message = task.getException().toString();
                    Toast.makeText(SellerAddNewProductActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    private void sellerInformation(){

    }
}
