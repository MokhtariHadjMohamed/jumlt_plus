package com.hadjmohamed.oran_agro.AdaptersAndHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hadjmohamed.oran_agro.R;
import com.hadjmohamed.oran_agro.RecViewInterface;

public class HolderRecOrders extends RecyclerView.ViewHolder {

    TextView name, total, orderSituation;

    public HolderRecOrders(@NonNull View itemView, RecViewInterface recViewInterface) {
        super(itemView);
        name = itemView.findViewById(R.id.c1);
        total = itemView.findViewById(R.id.c2);
        orderSituation = itemView.findViewById(R.id.c3);

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
