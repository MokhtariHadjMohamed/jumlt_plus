package com.hadjmohamed.oran_agro;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class HolderRecProduct extends RecyclerView.ViewHolder {


    ImageView imageView;
    TextView productName, productPriceCarton, productPricePiece;

    public HolderRecProduct(@NonNull View itemView, RecViewInterface recViewInterface) {
        super(itemView);
        imageView = itemView.findViewById(R.id.productImage);
        productName = itemView.findViewById(R.id.productName);
        productPriceCarton = itemView.findViewById(R.id.productPriceCarton);
        productPricePiece = itemView.findViewById(R.id.productPricePiece);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (recViewInterface != null) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION)
                        recViewInterface.onItemClick("Product", pos);
                }
            }
        });
    }
}
