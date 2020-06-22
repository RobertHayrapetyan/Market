package com.marketapp.rob.markettus.Sellers;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.marketapp.rob.markettus.Model.SellerOrders;
import com.marketapp.rob.markettus.R;

public class SellerNewOrderActivity extends AppCompatActivity {

    private RecyclerView orderList;
    private DatabaseReference ordersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_new_order);

        ordersRef = FirebaseDatabase.getInstance().getReference().child("Orders").child("Seller View").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        orderList = (RecyclerView)findViewById(R.id.order_list);
        orderList.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<SellerOrders> options = new FirebaseRecyclerOptions.Builder<SellerOrders>()
                .setQuery(ordersRef.orderByChild("state").equalTo("not shipped"), SellerOrders.class)
                .build();

        FirebaseRecyclerAdapter<SellerOrders, SellerOrdersViewHolder> adapter = new FirebaseRecyclerAdapter<SellerOrders, SellerOrdersViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull SellerOrdersViewHolder holder, final int position, @NonNull final SellerOrders model) {
                holder.userName.setText("Name: " + model.getName());
                holder.userShipingAddress.setText("Shiping address: \n" + model.getAddress() + ", " + model.getCity());
                holder.userDateTime.setText("Ordered at: " + model.getDate() + ", " + model.getTime());
                holder.userphoneNumber.setText("Phone: " + model.getPhone());
                holder.userTotalPrice.setText("Total amount: " + model.getTotalAmount());
                holder.orderSellerName.setText(model.getOrderSellerName());

                holder.showAllProductsBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String uID = getRef(position).getKey();

                        Intent intent = new Intent(SellerNewOrderActivity.this, SellerUserProductActivity.class);
                        intent.putExtra("uid", uID);
                        startActivity(intent);
                    }
                });

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CharSequence options[] = new CharSequence[]{
                                "Yes",
                                "No"
                        };

                        AlertDialog.Builder builder = new AlertDialog.Builder(SellerNewOrderActivity.this);
                        builder.setTitle("Have you shipped this order?");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (i == 0){
                                    final String uID = getRef(position).getKey();
                                    ordersRef.child(uID).child("state").setValue("shipped").addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                FirebaseDatabase.getInstance().getReference()
                                                        .child("Orders")
                                                        .child("User View")
                                                        .child(uID).child("state")
                                                        .setValue("shipped")
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()){
                                                            Toast.makeText(SellerNewOrderActivity.this, "Products shipped successfully", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                            }else {
                                                Toast.makeText(SellerNewOrderActivity.this, "Network error. Please try again", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            }
                        });
                        builder.show();
                    }
                });
            }

            @NonNull
            @Override
            public SellerOrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.orders_layout, parent, false);
                return new SellerOrdersViewHolder(view);
            }
        };

        orderList.setAdapter(adapter);
        adapter.startListening();
    }

    private void removeOrder(String uID) {
        ordersRef.child(uID).removeValue();
    }

    private class SellerOrdersViewHolder extends RecyclerView.ViewHolder {

        public TextView userName, userphoneNumber, userTotalPrice, userDateTime, userShipingAddress, orderSellerName;
        public Button showAllProductsBtn;

        public SellerOrdersViewHolder(@NonNull View itemView) {
            super(itemView);

            orderSellerName = itemView.findViewById(R.id.order_seller_name);
            userName = itemView.findViewById(R.id.user_name);
            userphoneNumber = itemView.findViewById(R.id.order_phone_number);
            userShipingAddress = itemView.findViewById(R.id.order_address);
            userTotalPrice = itemView.findViewById(R.id.order_total_price);
            userDateTime = itemView.findViewById(R.id.order_date_time);
            showAllProductsBtn = itemView.findViewById(R.id.show_all_products_btn);
        }
    }
}
