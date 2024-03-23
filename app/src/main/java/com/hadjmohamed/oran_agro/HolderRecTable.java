package com.hadjmohamed.oran_agro;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class HolderRecTable extends RecyclerView.ViewHolder {

    TextView productName, priceProduct, qnt;

    public HolderRecTable(@NonNull View itemView, RecViewInterface recViewInterface) {
        super(itemView);
        productName = itemView.findViewById(R.id.c1);
        priceProduct = itemView.findViewById(R.id.c2);
        qnt = itemView.findViewById(R.id.c3);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (recViewInterface != null) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION)
                        recViewInterface.onItemClick("Table", pos);
                }
            }
        });
    }
}
