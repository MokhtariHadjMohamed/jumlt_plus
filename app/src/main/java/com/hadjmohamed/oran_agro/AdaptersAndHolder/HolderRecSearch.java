package com.hadjmohamed.oran_agro.AdaptersAndHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hadjmohamed.oran_agro.R;
import com.hadjmohamed.oran_agro.RecViewInterface;

public class HolderRecSearch extends RecyclerView.ViewHolder {


    ImageView imageView;
    TextView productName, priceProduct;

    public HolderRecSearch(@NonNull View itemView, RecViewInterface recViewInterface) {
        super(itemView);
        imageView = itemView.findViewById(R.id.productImageRec);
        productName = itemView.findViewById(R.id.productNameRec);
        priceProduct = itemView.findViewById(R.id.productPriceRec);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (recViewInterface != null) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION)
                        recViewInterface.onItemClick("search", pos);
                }
            }
        });
    }
}
