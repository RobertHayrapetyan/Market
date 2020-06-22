package com.marketapp.rob.markettus.Buyers;

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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.marketapp.rob.markettus.Prevalent.Prevalent;
import com.marketapp.rob.markettus.R;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private TextView settingsCloseBtn, settingsUpdateBtn, profileChangeBtn;
    private EditText userPhoneNumberInput, userFullNameInput, userAddressInput, userCityInput;
    private CircleImageView profileImageView;
    private Button secQustBtn;

    private Uri imageUri;
    private String myUrl = "";
    private StorageReference storageProfilePictureRef;
    private StorageTask uploadTask;
    private String checker = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        storageProfilePictureRef = FirebaseStorage.getInstance().getReference().child("Profile pictures");

        settingsCloseBtn = (TextView)findViewById(R.id.close_settings_btn);
        settingsUpdateBtn = (TextView)findViewById(R.id.update_settings_btn);
        profileChangeBtn = (TextView)findViewById(R.id.profile_image_change_btn);
        userPhoneNumberInput = (EditText)findViewById(R.id.settings_phone_number);
        userFullNameInput = (EditText)findViewById(R.id.settings_full_name);
        userAddressInput = (EditText)findViewById(R.id.settings_address);
        userCityInput = (EditText)findViewById(R.id.settings_city);
        profileImageView = (CircleImageView)findViewById(R.id.settings_profile_image);
        secQustBtn = (Button) findViewById(R.id.seciurity_question_btn);



        userInfoDisplay(profileImageView, userPhoneNumberInput, userFullNameInput, userAddressInput);

        secQustBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingsActivity.this, ResetPasswordActivity.class);
                intent.putExtra("check", "settings");
                startActivity(intent);
                finish();
            }
        });

        settingsCloseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        settingsUpdateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checker.equals("clicked")){
                    userInfoSaved();
                }else{
                    updateOnlyUserInfo();
                }
            }
        });

        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checker = "clicked";

                CropImage.activity(imageUri)
                        .setAspectRatio(1, 1)
                        .start(SettingsActivity.this);
            }
        });
    }

    private void updateOnlyUserInfo() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");
        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put("name", userFullNameInput.getText().toString().trim());
        userMap.put("phone", userPhoneNumberInput.getText().toString().trim());
        userMap.put("address", userAddressInput.getText().toString().trim());
        userMap.put("city", userCityInput.getText().toString().trim());
        ref.child(Prevalent.currentOnlineUser.getPhone()).updateChildren(userMap);

        startActivity(new Intent(SettingsActivity.this, HomeActivity.class));
        Toast.makeText(SettingsActivity.this, "Profile information updated successfully...", Toast.LENGTH_SHORT);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && data != null){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();

            profileImageView.setImageURI(imageUri);
        }else {
            Toast.makeText(SettingsActivity.this, "Error: Try Again", Toast.LENGTH_SHORT);
            startActivity(new Intent(SettingsActivity.this, SettingsActivity.class));
            finish();
        }
    }

    private void userInfoSaved() {

        if (TextUtils.isEmpty(userFullNameInput.getText().toString()))
        {
            Toast.makeText(SettingsActivity.this, "Please enter your fullname...", Toast.LENGTH_SHORT);
        }
        else if (TextUtils.isEmpty(userPhoneNumberInput.getText().toString()))
        {
            Toast.makeText(SettingsActivity.this, "Please enter your phone number...", Toast.LENGTH_SHORT);
        }
        else if (TextUtils.isEmpty(userAddressInput.getText().toString()))
        {
            Toast.makeText(SettingsActivity.this, "Please enter your address...", Toast.LENGTH_SHORT);
        }
        else if (TextUtils.isEmpty(userCityInput.getText().toString()))
        {
            Toast.makeText(SettingsActivity.this, "Please enter your city...", Toast.LENGTH_SHORT);
        }
        else if (checker.equals("clicked"))
        {
            uploadeImage();
        }
    }

    private void uploadeImage() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Update Profile");
        progressDialog.setMessage("Please wait, while we are updating your account information...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        if (imageUri != null){
            final StorageReference fileRef = storageProfilePictureRef.child(Prevalent.currentOnlineUser.getPhone() + ".jpg");

            uploadTask = fileRef.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()){
                        throw task.getException();
                    }

                    return fileRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){
                        Uri downloadUri = task.getResult();
                        myUrl = downloadUri.toString();

                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");
                        HashMap<String, Object> userMap = new HashMap<>();
                        userMap.put("name", userFullNameInput.getText().toString());
                        userMap.put("phone", userPhoneNumberInput.getText().toString());
                        userMap.put("address", userAddressInput.getText().toString());
                        userMap.put("city", userCityInput.getText().toString());
                        userMap.put("image", myUrl);
                        ref.child(Prevalent.currentOnlineUser.getPhone()).updateChildren(userMap);


                        progressDialog.dismiss();
                        startActivity(new Intent(SettingsActivity.this, HomeActivity.class));
                        Toast.makeText(SettingsActivity.this, "Profile information updated successfully...", Toast.LENGTH_SHORT);
                        finish();
                    }else {
                        progressDialog.dismiss();
                        Toast.makeText(SettingsActivity.this, "Error: Please try again...", Toast.LENGTH_SHORT);

                    }
                }
            });
        }else {
            Toast.makeText(SettingsActivity.this, "Image is not selected...", Toast.LENGTH_SHORT);

        }
    }

    private void userInfoDisplay(final CircleImageView profileImageView, final EditText userPhoneNumberInput, final EditText userFullNameInput, final EditText userAddressInput) {

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(Prevalent.currentOnlineUser.getPhone());
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()){
                    if (dataSnapshot.child("image").exists()) {
                        String image = dataSnapshot.child("image").getValue().toString();
                        Picasso.get().load(image).into(profileImageView);
                    }
                        String name = dataSnapshot.child("name").getValue().toString();
                        String phone = dataSnapshot.child("phone").getValue().toString();
                        String address = dataSnapshot.child("address").getValue().toString();
                        String city = dataSnapshot.child("city").getValue().toString();

                        userFullNameInput.setText(name);
                        userPhoneNumberInput.setText(phone);
                        userAddressInput.setText(address);
                        userCityInput.setText(city);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
