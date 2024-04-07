package com.hadjmohamed.oran_agro.AdminAndDelivery;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.hadjmohamed.oran_agro.AdaptersAndHolder.AdapterRecCategory;
import com.hadjmohamed.oran_agro.AdaptersAndHolder.AdapterRecTableProduct;
import com.hadjmohamed.oran_agro.Category;
import com.hadjmohamed.oran_agro.Product;
import com.hadjmohamed.oran_agro.R;
import com.hadjmohamed.oran_agro.RecViewInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AboutProductsActivity extends AppCompatActivity implements View.OnClickListener, RecViewInterface {

    // TODO Element Declaration
    private CardView home, all;
    private TextView categoryTextWarehouse;

    // TODO Category Dialog Declaration
    private RecyclerView categoryRecView;
    private AdapterRecCategory adapterRecCategory;
    private List<Category> categoryList;
    private Dialog dialogCategory;
    private Button cancelBtnCategory;

    // TODO RecycleView Product Declaration
    private RecyclerView recyclerView;
    private AdapterRecTableProduct adapterRecTableProduct;
    private List<Product> productList;
    // TODO ProgressDialog Declaration
    private ProgressDialog progressDialog;

    // TODO Search Declaration
    private String searchVariable;
    private SearchView searchView;
    // TODO Firebase Declaration
    private FirebaseFirestore firestore;

    // TODO Dialog Product Var
    private Dialog dialogProduct;
    private TextView productName, productPricePiece, productPriceCarton, productPriceTotal;
    private Button submit;
    private EditText amount;
    private Product product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_products);

        home = findViewById(R.id.homeBtnAboutProduct);
        all = findViewById(R.id.categoryBtnAboutProduct);
        categoryTextWarehouse = findViewById(R.id.categoryTextAboutProduct);

        home.setOnClickListener(this);
        all.setOnClickListener(this);

        // TODO Firebase
        firestore = FirebaseFirestore.getInstance();

        // TODO Progress
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Fetching data...");
        progressDialog.show();

        // TODO Search
        searchView = findViewById(R.id.searchViewAboutProduct);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                searchVariable = s.substring(0,1).toUpperCase() + s.substring(1);
                getSubCategory(searchVariable);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        // TODO dialog add product
        dialogProduct = new Dialog(this);
        dialogProduct.setCancelable(false);
        dialogProduct.setContentView(R.layout.add_product_sale);
        dialogProduct.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        productName = dialogProduct.findViewById(R.id.productNameSale);
        productPricePiece = dialogProduct.findViewById(R.id.prudectPricePieceSale);
        productPriceCarton = dialogProduct.findViewById(R.id.prudectPriceCartonSale);
        productPriceTotal = dialogProduct.findViewById(R.id.prudectPriceTotalSale);
        amount = dialogProduct.findViewById(R.id.productAmountSale);
        submit = dialogProduct.findViewById(R.id.submitSale);

        amount.setEnabled(false);
        submit.setText("خروج");
        dialogProduct.findViewById(R.id.CancelSale).setVisibility(View.GONE);
        dialogProduct.findViewById(R.id.deleteSale).setVisibility(View.GONE);

        // TODO Category Dialog
        dialogCategory = new Dialog(this);
        dialogCategory.setCancelable(false);
        dialogCategory.setContentView(R.layout.category_dialog);
        dialogCategory.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        categoryList = new ArrayList<>();
        categoryRecView = dialogCategory.findViewById(R.id.categoryRecView);
        adapterRecCategory = new AdapterRecCategory(getApplicationContext()
                , categoryList, this);
        adapterRecCategory.setLayout(R.layout.grid_item_category);
        cancelBtnCategory = dialogCategory.findViewById(R.id.cancelBtnCategory);
        cancelBtnCategory.setOnClickListener(this);

        // TODO RecycleView Product
        recyclerView = findViewById(R.id.recyclerViewAboutProduct);
        productList = new ArrayList<>();
        adapterRecTableProduct = new AdapterRecTableProduct(getApplicationContext(),
                productList, this);
        getProduct();
    }

    private void getSubCategory() {
        categoryList.clear();
        categoryList.add(new Category("all", 0, "all"));
        firestore.collection("SubCategory")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (!task.isSuccessful()) {
                            if (progressDialog.isShowing())
                                progressDialog.dismiss();
                            Log.e("Firestore error", "Failed");
                            return;
                        }
                        for (DocumentSnapshot ds : task.getResult().getDocuments())
                            categoryList.add(ds.toObject(Category.class));
                        adapterRecCategory.notifyDataSetChanged();
                    }
                });
        categoryRecView.setLayoutManager(new GridLayoutManager(this, 2));
        categoryRecView.setAdapter(adapterRecCategory);
        progressDialog.dismiss();
    }

    private void getSubCategory(String searchVariable) {
        firestore.collection("SubCategory")
                .whereEqualTo("Name", searchVariable)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (!task.isSuccessful()) {
                            if (progressDialog.isShowing())
                                progressDialog.dismiss();
                            Log.e("Firestore error", "Failed");
                            return;
                        }
                        for (DocumentSnapshot ds : task.getResult().getDocuments())
                            getProductSearch(searchVariable, ds.toObject(Category.class).getIdSubCategory());
                    }
                });
    }

    private void getProductSearch(String s, int IDCategorie) {
        productList.clear();
        progressDialog.show();
        firestore.collection("Products")
                .where(Filter.or(
                        Filter.equalTo("IDCategorie", IDCategorie),
                        Filter.equalTo("NameProduct", s)
                ))
                .orderBy("idProduct", Query.Direction.ASCENDING)
                .limit(10)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (!task.isSuccessful() || s.isEmpty()) {
                            if (progressDialog.isShowing())
                                progressDialog.dismiss();
                            Log.e("Task Failed", String.valueOf(task.getException()));
                            return;
                        }
                        for (DocumentSnapshot dc :
                                task.getResult().getDocuments()) {
                            if (dc.toObject(Product.class).getNameProduct().contains(s.toUpperCase()))
                                productList.add(dc.toObject(Product.class));
                        }
                        adapterRecTableProduct.notifyDataSetChanged();
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();
                    }
                });

        recyclerView.setLayoutManager(new
                LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapterRecTableProduct);
    }

    private void getProduct() {
        productList.clear();
        firestore.collection("Products")
                .orderBy("idProduct", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            if (progressDialog.isShowing())
                                progressDialog.dismiss();
                            Log.e("Firestore error", error.getMessage());
                            return;
                        }
                        for (DocumentChange dc :
                                value.getDocumentChanges()) {
                            if (dc.getType() == DocumentChange.Type.ADDED) {
                                productList.add(dc.getDocument().toObject(Product.class));
                            }
                        }
                        adapterRecTableProduct.notifyDataSetChanged();
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();
                    }
                });
        recyclerView.setLayoutManager(new
                LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapterRecTableProduct);
    }

    @Override
    public void onClick(View view) {
        if (view == home){
            startActivity(new Intent(AboutProductsActivity.this, HomePageAdminActivity.class));
            finish();
        } else if (view == all){
            progressDialog.show();
            getSubCategory();
            dialogCategory.show();
        }else if (view == cancelBtnCategory){
            dialogCategory.dismiss();
        }else if (view == submit) {
            dialogProduct.dismiss();
        }
    }

    @Override
    public void onItemClick(String view, int position) {
        if (Objects.equals(view, "Table")){
            product = productList.get(position);
            productName.setText(product.getNameProduct());
            productPricePiece.setText(product.getPrixUnitaire() + "");
            productPriceCarton.setText(product.getPrixCarton() + "");
            amount.setText(String.valueOf(product.getQuantite()));
            productPriceTotal.setText(String.valueOf(product.getPrixCarton()));
            amount.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    try {
                        productPriceTotal.setText(String.valueOf(product.getPrixCarton() * Integer.parseInt(amount.getText().toString())));
                    } catch (Exception e) {
                        Log.e("Error amount:", e.getMessage());
                    }
                }
            });
            submit.setOnClickListener(this);
            dialogProduct.show();
        } else if (Objects.equals(view, "category")) {
            if (position == 0){
                getProduct();
                dialogCategory.dismiss();
            }else {
                getSubCategory(categoryList.get(position).getName());
                categoryTextWarehouse.setText(categoryList.get(position).getName());
                dialogCategory.dismiss();
            }
        }
    }
}