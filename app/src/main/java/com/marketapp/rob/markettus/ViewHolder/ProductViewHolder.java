package com.marketapp.rob.markettus.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.marketapp.rob.markettus.Interface.ItemClickListener;
import com.marketapp.rob.markettus.R;

public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{

    public TextView productName, productDescription, productPrice, sellerName, productState;
    public ImageView productImage;
    public ItemClickListener listener;
    public String sid = "";

    public ProductViewHolder(@NonNull View itemView) {
        super(itemView);

        productName = (TextView)itemView.findViewById(R.id.product_name);
        productDescription = (TextView)itemView.findViewById(R.id.product_description);
        productPrice = (TextView)itemView.findViewById(R.id.product_price);
        productImage = (ImageView) itemView.findViewById(R.id.product_image);
        sellerName = (TextView) itemView.findViewById(R.id.seller_name);
        productState = (TextView) itemView.findViewById(R.id.product_state);

    }

    public void setOnItemClickListener(ItemClickListener listener){
        this.listener = listener;
    }

    @Override
    public void onClick(View view) {
        listener.onClick(view, getAdapterPosition(), false);
    }
}
