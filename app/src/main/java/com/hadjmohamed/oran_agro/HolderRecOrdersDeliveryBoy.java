package com.hadjmohamed.oran_agro;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class HolderRecOrdersDeliveryBoy extends RecyclerView.ViewHolder {

    TextView orderId, nameId;

    public HolderRecOrdersDeliveryBoy(@NonNull View itemView, RecViewInterface recViewInterface) {
        super(itemView);
        orderId = itemView.findViewById(R.id.orderId);
        nameId = itemView.findViewById(R.id.nameId);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (recViewInterface != null) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION)
                        recViewInterface.onItemClick("orderDeliveryBoy", pos);
                }
            }
        });
    }
}
