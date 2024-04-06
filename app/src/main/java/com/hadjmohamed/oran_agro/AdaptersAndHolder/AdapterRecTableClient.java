package com.hadjmohamed.oran_agro.AdaptersAndHolder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hadjmohamed.oran_agro.AdminAndDelivery.Client;
import com.hadjmohamed.oran_agro.R;
import com.hadjmohamed.oran_agro.RecViewInterface;

import java.util.List;

public class AdapterRecTableClient extends RecyclerView.Adapter<HolderRecTable> {

    private final RecViewInterface recViewInterface;
    Context context;
    List<Client> clientList;

    public AdapterRecTableClient(Context context, List<Client> clientList, RecViewInterface recViewInterface) {
        this.recViewInterface = recViewInterface;
        this.context = context;
        this.clientList = clientList;
    }

    @NonNull
    @Override
    public HolderRecTable onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HolderRecTable(LayoutInflater.from(context).
                inflate(R.layout.row_rec_item, parent, false), recViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderRecTable holder, int position) {
        holder.productName.setText(clientList.get(position).getName());
        holder.priceProduct.setText(clientList.get(position).getTotalDebt() + "");
        holder.qnt.setText(clientList.get(position).getPaidDebt() + "");
    }

    @Override
    public int getItemCount() {
        return clientList.size();
    }

}
