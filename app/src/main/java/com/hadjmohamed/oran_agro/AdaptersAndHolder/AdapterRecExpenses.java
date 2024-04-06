package com.hadjmohamed.oran_agro.AdaptersAndHolder;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.hadjmohamed.oran_agro.AdminAndDelivery.Expenses;
import com.hadjmohamed.oran_agro.Category;
import com.hadjmohamed.oran_agro.R;
import com.hadjmohamed.oran_agro.RecViewInterface;
import com.hadjmohamed.oran_agro.User;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class AdapterRecExpenses extends RecyclerView.Adapter<HolderRecExpenses> {

    private final RecViewInterface recViewInterface;
    Context context;
    List<Expenses> expensesList;

    private int layout = R.layout.row_rec_item;

    public AdapterRecExpenses(Context context, List<Expenses> expensesList, RecViewInterface recViewInterface) {
        this.recViewInterface = recViewInterface;
        this.context = context;
        this.expensesList = expensesList;
    }

    @NonNull
    @Override
    public HolderRecExpenses onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HolderRecExpenses(LayoutInflater.from(context).
                inflate(layout, parent, false), recViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderRecExpenses holder, int position) {
        holder.name.setText(expensesList.get(position).getName());
        holder.price.setText(String.valueOf(expensesList.get(position).getPrice()));
        getUser(expensesList.get(position).getUserUid(), holder.nameUser);
    }

    private void getUser(String userUid, TextView nameUser) {
        FirebaseFirestore.getInstance().collection("Users")
                .document(userUid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (!task.isSuccessful())
                            Log.e(TAG, "onComplete: error get user AdapterRecExpenses");
                        nameUser.setText(task.getResult().toObject(User.class).getName());
                    }
                });
    }

    @Override
    public int getItemCount() {
        return expensesList.size();
    }

    public void setLayout(int layout) {
        this.layout = layout;
    }
}
