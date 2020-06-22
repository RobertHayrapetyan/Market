package com.marketapp.rob.markettus.Buyers;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.marketapp.rob.markettus.Model.SellerOrders;
import com.marketapp.rob.markettus.Prevalent.Prevalent;
import com.marketapp.rob.markettus.R;
import com.marketapp.rob.markettus.Sellers.SellerUserProductActivity;

public class OrdersActivity extends AppCompatActivity {

    private RecyclerView userOrderList;
    private DatabaseReference userOrdersRef;
    private SwipeRefreshLayout refreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        userOrdersRef = FirebaseDatabase.getInstance().getReference().child("Orders").child("User View").child(Prevalent.currentOnlineUser.getPhone());

        userOrderList = (RecyclerView)findViewById(R.id.user_order_list);
        refreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_to_refresh_order);
        userOrderList.setLayoutManager(new LinearLayoutManager(this));

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                onStart();
                refreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<SellerOrders> options = new FirebaseRecyclerOptions.Builder<SellerOrders>()
                .setQuery(userOrdersRef.orderByChild("state").equalTo("not shipped"), SellerOrders.class)
                .build();

        FirebaseRecyclerAdapter<SellerOrders, UserOrdersViewHolder> adapter = new FirebaseRecyclerAdapter<SellerOrders, UserOrdersViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull UserOrdersViewHolder holder, final int position, @NonNull final SellerOrders model) {
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

                        Intent intent = new Intent(OrdersActivity.this, SellerUserProductActivity.class);
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

                        AlertDialog.Builder builder = new AlertDialog.Builder(OrdersActivity.this);
                        builder.setTitle("Have you arrived this order?");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (i == 0){
                                    final String uID = getRef(position).getKey();
                                    userOrdersRef.child(uID).child("state").setValue("shipped").addOnCompleteListener(new OnCompleteListener<Void>() {
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
                                                                    Toast.makeText(OrdersActivity.this, "Products shipped successfully", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });
                                            }else {
                                                Toast.makeText(OrdersActivity.this, "Network error. Please try again", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                    finish();
                                }else {
                                    finish();
                                }
                            }
                        });
                        builder.show();
                    }
                });
            }

            @NonNull
            @Override
            public UserOrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.orders_layout, parent, false);
                return new UserOrdersViewHolder(view);
            }
        };

        userOrderList.setAdapter(adapter);
        adapter.startListening();
    }

    private void removeOrder(String uID) {
        userOrdersRef.child(uID).removeValue();
    }

    private class UserOrdersViewHolder extends RecyclerView.ViewHolder {

        public TextView userName, userphoneNumber, userTotalPrice, userDateTime, userShipingAddress, orderSellerName;
        public Button showAllProductsBtn;

        public UserOrdersViewHolder(@NonNull View itemView) {
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
