package com.hadjmohamed.oran_agro;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class CategoryPageActivity extends AppCompatActivity implements RecViewInterface{

    private RecyclerView recyclerViewCategory;
    private FirebaseFirestore firestore;
    private AdapterRecCategory adapterRecCategory;
    private ProgressDialog progressDialog;
    List<Category> categoryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_page);

        // Progress
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Fetching data...");
        progressDialog.show();

        // Recycle view search
        recyclerViewCategory = findViewById(R.id.categoryRecCategoryPage);
        firestore = FirebaseFirestore.getInstance();
        categoryList = new ArrayList<>();
        adapterRecCategory = new AdapterRecCategory(getApplicationContext(),
                categoryList, this);

        getUser();

        // Navigation bar Bottom
        BottomNavigationView bottomNavigationView = findViewById(R.id.navigation_bar_home);
        bottomNavigationView.setSelectedItemId(R.id.categoryNavigation);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.homeNavigation) {
                startActivity(new Intent(CategoryPageActivity.this, HomePageActivity.class));
                return true;
            } else if (id == R.id.searchNavigation) {
                startActivity(new Intent(CategoryPageActivity.this, SearchPageActivity.class));
                return true;
            } else if (id == R.id.accountNavigation) {
                startActivity(new Intent(CategoryPageActivity.this, UserAccountActivity.class));
                return true;
            } else if (id == R.id.categoryNavigation) {
                return true;
            } else if (id == R.id.shoppingCartNavigation) {
                startActivity(new Intent(CategoryPageActivity.this, ShoppingCartActivity.class));
                return true;
            }else {
                return true;
            }
        });
    }

    private void categorySeparation(User user) {
        firestore.collection("Category")
                .whereEqualTo("code", user.getInvitation())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (DocumentSnapshot ds : task.getResult().getDocuments())
                            getSubCategory(ds.toObject(Category.class).getIdCategory());
                    }
                });
        recyclerViewCategory.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        recyclerViewCategory.setAdapter(adapterRecCategory);
    }

    private void getSubCategory(int idCategory){
        firestore.collection("SubCategory")
                .whereEqualTo("idCategory", idCategory)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (!task.isSuccessful()){
                            if (progressDialog.isShowing())
                                progressDialog.dismiss();
                            Log.e("Firestore error", "Failed");
                            return;
                        }

                        for (DocumentSnapshot ds : task.getResult().getDocuments())
                            categoryList.add(ds.toObject(Category.class));

                        adapterRecCategory.notifyDataSetChanged();
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();

                    }
                });
    }

    private void getUser() {
        firestore.collection("Users")
                .document(FirebaseAuth.getInstance()
                        .getCurrentUser().getUid())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (!task.isSuccessful())
                            Log.e("Failed", "Failed");
                        categorySeparation(task.getResult().toObject(User.class));
                    }
                });
    }

    @Override
    public void onItemClick(String view, int position) {
        Intent intent = new Intent(CategoryPageActivity.this, ProductInSameCategoryActivity.class);
        intent.putExtra("idSubCategory", categoryList.get(position).getIdSubCategory());
        intent.putExtra("subCategoryName", categoryList.get(position).getName());
        startActivity(intent);
    }
}