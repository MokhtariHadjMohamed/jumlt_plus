package com.hadjmohamed.oran_agro;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AdapterRecTableProduct extends RecyclerView.Adapter<HolderRecTable> {

    private final RecViewInterface recViewInterface;
    Context context;
    List<Product> productList;

    public AdapterRecTableProduct(Context context, List<Product> productList, RecViewInterface recViewInterface) {
        this.recViewInterface = recViewInterface;
        this.context = context;
        this.productList = productList;
    }

    @NonNull
    @Override
    public HolderRecTable onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HolderRecTable(LayoutInflater.from(context).
                inflate(R.layout.row_rec_item, parent, false), recViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderRecTable holder, int position) {
        holder.productName.setText(productList.get(position).getNameProduct());
        holder.priceProduct.setText(productList.get(position).getPrixCarton() + "");
        holder.qnt.setText(productList.get(position).getQuantite() + "");
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

}
