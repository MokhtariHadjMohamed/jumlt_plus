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
import android.widget.SearchView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.hadjmohamed.oran_agro.AdaptersAndHolder.AdapterRecTableUser;
import com.hadjmohamed.oran_agro.R;
import com.hadjmohamed.oran_agro.RecViewInterface;
import com.hadjmohamed.oran_agro.User;

import java.util.ArrayList;
import java.util.List;

public class EmployeesActivity extends AppCompatActivity implements RecViewInterface, View.OnClickListener {

    // TODO Firebase Declaration variables
    private FirebaseFirestore firestore;
    // TODO Recycle View Declaration variables
    private RecyclerView recyclerView;
    private AdapterRecTableUser adapterRecTableUser;
    private List<User> userList;
    // TODO ProgressDialog Declaration
    private ProgressDialog progressDialog;

    // TODO Elements Declaration variables
    private CardView home, add, category;
    private TextView categoryTextEmployees;
    private SearchView searchView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employees);
        // TODO Firebase onCreate
        firestore = FirebaseFirestore.getInstance();

        // TODO Progress
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Fetching data...");
        progressDialog.show();

        // TODO Recycle View onCreate
        recyclerView = findViewById(R.id.userRecycleViewemployees);
        userList = new ArrayList<>();
        adapterRecTableUser = new AdapterRecTableUser(this, userList, this);
        getUser();

        // TODO Elements onCreate
        home = findViewById(R.id.homeEmployeesActivity);
        add = findViewById(R.id.addEmployeesActivity);
        category = findViewById(R.id.categoryEmployeesActivity);
        categoryTextEmployees = findViewById(R.id.categoryTextEmployees);
        searchView = findViewById(R.id.searchViewEmployeesActivity);
        home.setOnClickListener(this);
        add.setOnClickListener(this);
        category.setOnClickListener(this);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                if (!s.isEmpty()){
                    progressDialog.show();
                    getUserSearch(s);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
    }

    private void getUser() {
        progressDialog.show();
        userList.clear();
        firestore.collection("Users")
                .where(Filter.or(Filter.equalTo("type", "employee"),
                        Filter.equalTo("type", "admin"),
                        Filter.equalTo("type", "deliveryBoy")))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (!task.isSuccessful()) {
                            Log.e(TAG, "onComplete: get User");
                            if (progressDialog.isShowing())
                                progressDialog.dismiss();
                        }
                        for (QueryDocumentSnapshot d : task.getResult())
                            userList.add(d.toObject(User.class));
                        adapterRecTableUser.notifyDataSetChanged();
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();
                    }
                });
        recyclerView.setLayoutManager(new
                LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapterRecTableUser);
    }

    private void getUser(String type) {
        progressDialog.show();
        userList.clear();
        firestore.collection("Users")
                .whereEqualTo("type", type)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (!task.isSuccessful()) {
                            Log.e(TAG, "onComplete: get User");
                            if (progressDialog.isShowing())
                                progressDialog.dismiss();
                        }
                        for (QueryDocumentSnapshot d : task.getResult())
                            userList.add(d.toObject(User.class));
                        adapterRecTableUser.notifyDataSetChanged();
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();
                    }
                });
        recyclerView.setLayoutManager(new
                LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapterRecTableUser);
    }

    private void getUserSearch(String s) {
        Log.d("Hloooooooooooo", "world");
        progressDialog.show();
        userList.clear();
        firestore.collection("Users")
                .where(Filter.and(
                        Filter.or(Filter.equalTo("name", s),
                                Filter.equalTo("phone", s),
                                Filter.equalTo("email", s),
                                Filter.equalTo("familyName", s)),
                        Filter.or(Filter.equalTo("type", "employee"),
                                Filter.equalTo("type", "admin"),
                                Filter.equalTo("type", "deliveryBoy"))
                ))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (!task.isSuccessful()) {
                            Log.e(TAG, "onComplete: get User");
                            if (progressDialog.isShowing())
                                progressDialog.dismiss();
                        }
                        for (QueryDocumentSnapshot d : task.getResult())
                            userList.add(d.toObject(User.class));
                        adapterRecTableUser.notifyDataSetChanged();
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();
                    }
                });
        recyclerView.setLayoutManager(new
                LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapterRecTableUser);
    }

    @Override
    public void onItemClick(String view, int position) {

    }

    @Override
    public void onClick(View view) {
        if (view == home) {
            startActivity(new Intent(EmployeesActivity.this, HomePageAdminActivity.class));
            finish();
        } else if (view == category) {
            switch (categoryTextEmployees.getText().toString()) {
                case "كل":
                    getUser("admin");
                    categoryTextEmployees.setText("admin");
                    break;
                case "admin":
                    getUser("employee");
                    categoryTextEmployees.setText("employee");
                    break;
                case "employee":
                    getUser("deliveryBoy");
                    categoryTextEmployees.setText("deliveryBoy");
                    break;
                case "deliveryBoy":
                    getUser();
                    categoryTextEmployees.setText("كل");
                    break;
            }
        } else if (view == add) {
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.fragmentEmployees, AddUserFragment.class, new Bundle())
                    .commit();
        }
    }
}