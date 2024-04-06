package com.hadjmohamed.oran_agro.AdaptersAndHolder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hadjmohamed.oran_agro.R;
import com.hadjmohamed.oran_agro.RecViewInterface;
import com.hadjmohamed.oran_agro.User;

import java.util.List;

public class AdapterRecTableUser extends RecyclerView.Adapter<HolderRecTable> {

    private final RecViewInterface recViewInterface;
    Context context;
    List<User> userList;

    public AdapterRecTableUser(Context context, List<User> userList, RecViewInterface recViewInterface) {
        this.recViewInterface = recViewInterface;
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public HolderRecTable onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HolderRecTable(LayoutInflater.from(context).
                inflate(R.layout.row_rec_item, parent, false), recViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderRecTable holder, int position) {
        holder.productName.setText(userList.get(position).getName());
        holder.priceProduct.setText(String.valueOf(userList.get(position).getPhone()));
        holder.qnt.setText(userList.get(position).getType());
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

}
