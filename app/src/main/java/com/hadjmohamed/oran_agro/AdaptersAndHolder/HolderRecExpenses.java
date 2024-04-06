package com.hadjmohamed.oran_agro.AdaptersAndHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hadjmohamed.oran_agro.R;
import com.hadjmohamed.oran_agro.RecViewInterface;

public class HolderRecExpenses extends RecyclerView.ViewHolder {

    TextView name, price, nameUser;

    public HolderRecExpenses(@NonNull View itemView, RecViewInterface recViewInterface) {
        super(itemView);
        nameUser = itemView.findViewById(R.id.c1);
        name = itemView.findViewById(R.id.c2);
        price = itemView.findViewById(R.id.c3);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (recViewInterface != null) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION)
                        recViewInterface.onItemClick("category", pos);
                }
            }
        });
    }
}
