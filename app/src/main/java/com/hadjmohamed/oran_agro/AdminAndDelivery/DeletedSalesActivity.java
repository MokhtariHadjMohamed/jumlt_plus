package com.hadjmohamed.oran_agro.AdminAndDelivery;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.hadjmohamed.oran_agro.AdaptersAndHolder.AdapterRecTableSales;
import com.hadjmohamed.oran_agro.R;
import com.hadjmohamed.oran_agro.RecViewInterface;

import java.util.ArrayList;
import java.util.List;

public class DeletedSalesActivity extends AppCompatActivity implements View.OnClickListener,
        RecViewInterface {

    // TODO Firebase var
    private FirebaseFirestore firestore;
    // TODO Recycle View var
    private RecyclerView recyclerView;
    private List<Sale> saleList;
    private AdapterRecTableSales adapterRecTableSales;
    private ProgressDialog progressDialog;
    // TODO Element var
    private CardView home, category;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deleted_sales);
        // TODO Firebase Declaration
        firestore = FirebaseFirestore.getInstance();

        // TODO Progress Declaration
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Fetching data...");
        progressDialog.show();

        // TODO RecyclerView Product Declaration
        recyclerView = findViewById(R.id.recyclerViewDeletedSales);
        saleList = new ArrayList<>();
        adapterRecTableSales = new AdapterRecTableSales(this, saleList, this);
        getDeleteSales();

        // TODO Element Declaration
        home = findViewById(R.id.homeBtnDeletedSales);
        category = findViewById(R.id.categoryDeletedSales);
        home.setOnClickListener(this);
        category.setOnClickListener(this);
    }

    //  TODO get Delete Sales
    private void getDeleteSales() {
        saleList.clear();
        firestore.collection("DeletedSales")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (!task.isSuccessful()){
                            Log.e(TAG, "onComplete: getDeletedsales");
                            if (progressDialog.isShowing())
                                progressDialog.dismiss();
                        }
                        for (QueryDocumentSnapshot d: task.getResult()){
                            saleList.add(d.toObject(Sale.class));
                        }
                        adapterRecTableSales.notifyDataSetChanged();
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();
                    }
                });
        recyclerView.setLayoutManager(new
                LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapterRecTableSales);
    }
    private void getDeleteSalesALLPayed() {
        firestore.collection("DeletedSales")
                .whereGreaterThan("totalPayed", 0)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (!task.isSuccessful()){
                            Log.e(TAG, "onComplete: getDeletedsales");
                            if (progressDialog.isShowing())
                                progressDialog.dismiss();
                        }
                        for (QueryDocumentSnapshot d: task.getResult()){
                            saleList.add(d.toObject(Sale.class));
                        }
                        adapterRecTableSales.notifyDataSetChanged();
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();
                    }
                });
        recyclerView.setLayoutManager(new
                LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapterRecTableSales);
    }

    private void getDeleteSalesNotPayed() {
        firestore.collection("DeletedSales")
                .whereEqualTo("totalPayed", 0)

                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (!task.isSuccessful()){
                            Log.e(TAG, "onComplete: getDeletedsales");
                            if (progressDialog.isShowing())
                                progressDialog.dismiss();
                        }
                        for (QueryDocumentSnapshot d: task.getResult()){
                            saleList.add(d.toObject(Sale.class));
                        }
                        adapterRecTableSales.notifyDataSetChanged();
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();
                    }
                });
        recyclerView.setLayoutManager(new
                LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapterRecTableSales);
    }

    @Override
    public void onItemClick(String view, int position) {
        Bundle bundle = new Bundle();
        bundle.putString("uid", saleList.get(position).getUid());
        bundle.putString("collection", "DeletedSales");
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.salesFragment, SaleEditFragment.class, bundle)
                .commit();
    }

    @Override
    public void onClick(View view) {
        if (view == home){
            startActivity(new Intent(DeletedSalesActivity.this,
                    HomePageAdminActivity.class));
            finish();
        } else if (view == category) {
            TextView textView = findViewById(R.id.categoryTextDeletedSales);
            if (textView.getText().toString().equals("كل")) {
                progressDialog.show();
                textView.setText("غير مدفوع");
                getDeleteSalesNotPayed();
            } else if (textView.getText().toString().equals("غير مدفوع")) {
                progressDialog.show();
                textView.setText("مدفوع");
                getDeleteSalesALLPayed();
            } else if (textView.getText().toString().equals("مدفوع")) {
                progressDialog.show();
                textView.setText("كل");
                getDeleteSales();
            }
        }
    }
}