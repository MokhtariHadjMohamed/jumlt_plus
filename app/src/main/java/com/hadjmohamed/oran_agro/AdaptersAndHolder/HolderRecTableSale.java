package com.hadjmohamed.oran_agro.AdaptersAndHolder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hadjmohamed.oran_agro.R;
import com.hadjmohamed.oran_agro.RecViewInterface;

public class HolderRecTableSale extends RecyclerView.ViewHolder {

    TextView name, totalPrice, paidPrice, remaining;

    public HolderRecTableSale(@NonNull View itemView, RecViewInterface recViewInterface) {
        super(itemView);
        name = itemView.findViewById(R.id.c1);
        totalPrice = itemView.findViewById(R.id.c2);
        paidPrice = itemView.findViewById(R.id.c3);
        remaining = itemView.findViewById(R.id.c4);


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
