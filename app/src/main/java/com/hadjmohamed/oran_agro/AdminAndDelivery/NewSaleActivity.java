package com.hadjmohamed.oran_agro.AdminAndDelivery;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.hadjmohamed.oran_agro.AdaptersAndHolder.AdapterRecProduct;
import com.hadjmohamed.oran_agro.AdaptersAndHolder.AdapterRecTableProduct;
import com.hadjmohamed.oran_agro.Category;
import com.hadjmohamed.oran_agro.Product;
import com.hadjmohamed.oran_agro.R;
import com.hadjmohamed.oran_agro.RecViewInterface;
import com.hadjmohamed.oran_agro.User;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class NewSaleActivity extends AppCompatActivity implements RecViewInterface, View.OnClickListener {

    // TODO Back Home Page Var
    private ImageView backArrowBtnNewSale;
    // TODO RecyclerView Product var
    private String typeRec = "Product";
    private RecyclerView recyclerView;
    private List<Product> productList;
    private Product product;
    private AdapterRecProduct adapterRecProduct;
    private ProgressDialog progressDialog;
    // TODO Total var
    private TextView totalNewSale;
    private Float total = 0.0f;
    private Button submitNewSale;

    // TODO Search Var
    private String searchVariable;
    private SearchView searchView;
    // TODO firebase var
    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;

    // TODO Table Var
    private RecyclerView recyclerViewTable;
    private List<Product> productListTable;
    private AdapterRecTableProduct adapterRecTableProduct;

    // TODO Dialog Product Var
    private Dialog dialogProduct, dialogSale;
    private TextView productName, productPricePiece, productPriceCarton, productPriceTotal;
    private Button submit, cancel, delete;
    private EditText amount;

    // TODO Dialog Sale Var
    private Spinner spinner;
    private TextView totalSubmitSale;
    private EditText payment;
    private Button submitPayment, cancelPayment;
    private List<String> namesList;
    private List<User> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_sale);

        // TODO Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // TODO Back Home Page
        backArrowBtnNewSale = findViewById(R.id.backArrowBtnNewSale);
        backArrowBtnNewSale.setOnClickListener(this);

        // TODO total
        totalNewSale = findViewById(R.id.totalNewSale);
        submitNewSale = findViewById(R.id.submitNewSale);
        submitNewSale.setOnClickListener(this);

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
        cancel = dialogProduct.findViewById(R.id.CancelSale);
        delete = dialogProduct.findViewById(R.id.deleteSale);
        delete.setVisibility(View.GONE);

        // TODO dialog submit sale
        dialogSale = new Dialog(this);
        dialogSale.setCancelable(false);
        dialogSale.setContentView(R.layout.submit_order_sale);
        dialogSale.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        spinner = dialogSale.findViewById(R.id.buyerNeme);
        totalSubmitSale = dialogSale.findViewById(R.id.prudectPriceTotalPay);
        payment = dialogSale.findViewById(R.id.productPaymentSale);
        submitPayment = dialogSale.findViewById(R.id.submitPay);
        cancelPayment = dialogSale.findViewById(R.id.CancelPay);

        // TODO Progress
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Fetching data...");
        progressDialog.show();

        // TODO RecyclerView Product
        recyclerView = findViewById(R.id.recyclerViewProduct);
        productList = new ArrayList<>();
        adapterRecProduct = new AdapterRecProduct(getApplicationContext(),
                productList, this);
        getProduct();

        // TODO Search
        searchView = findViewById(R.id.searchViewNewSale);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                searchVariable = s.substring(0, 1).toUpperCase() + s.substring(1);
                getSubCategory(searchVariable);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        // TODO Table
        recyclerViewTable = findViewById(R.id.recyclerViewProductSale);
        productListTable = new ArrayList<>();
        adapterRecTableProduct = new AdapterRecTableProduct(getApplicationContext(),
                productListTable, this);
    }

    private void getProduct() {
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
                            adapterRecProduct.notifyDataSetChanged();
                            if (progressDialog.isShowing())
                                progressDialog.dismiss();
                        }
                    }
                });
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(adapterRecProduct);
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
        // Progress
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Fetching data...");
        progressDialog.show();

        productList.clear();

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
                        adapterRecProduct.notifyDataSetChanged();
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();
                    }
                });

        if (progressDialog.isShowing())
            progressDialog.dismiss();

        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(adapterRecProduct);
    }

    private void addRow(Product product) {
        productListTable.add(product);
        adapterRecTableProduct.notifyDataSetChanged();
        recyclerViewTable.setLayoutManager(new
                LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        recyclerViewTable.setAdapter(adapterRecTableProduct);
        dialogProduct.dismiss();
    }

    private void getUsers() {
        firestore.collection("Users")
                .orderBy("name")
                .whereEqualTo("type", "user")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (!task.isSuccessful()){
                            Log.e("Error:", "Field get users");
                            return;
                        }
                        for (QueryDocumentSnapshot d : task.getResult()) {
                            namesList.add(d.toObject(User.class).getName());
                            userList.add(d.toObject(User.class));
                        }
                        String[] names = new String[namesList.size()];
                        namesList.toArray(names);
                        ArrayAdapter arrayAdapter = new ArrayAdapter(NewSaleActivity.this,
                                android.R.layout.simple_spinner_item, names);
                        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner.setAdapter(arrayAdapter);
                    }
                });
    }

    @Override
    public void onItemClick(String view, int position) {
        if (Objects.equals(view, "Product")) {
            typeRec = "Product";
            delete.setVisibility(View.GONE);
            product = productList.get(position);
            productName.setText(product.getNameProduct());
            productPricePiece.setText(product.getPrixUnitaire() + "");
            productPriceCarton.setText(product.getPrixCarton() + "");
            productPriceTotal.setText(String.valueOf(product.getPrixCarton() * Integer.parseInt(amount.getText().toString())));
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
            cancel.setOnClickListener(this);
            dialogProduct.show();
        } else if (Objects.equals(view, "Table")) {
            typeRec = "Table";
            product = productListTable.get(position);
            productName.setText(product.getNameProduct());
            productPricePiece.setText(product.getPrixUnitaire() + "");
            productPriceCarton.setText(product.getPrixCarton() + "");
            productPriceTotal.setText(String.valueOf(product.getPrixCarton() * Integer.parseInt(amount.getText().toString())));
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
            cancel.setOnClickListener(this);
            delete.setVisibility(View.VISIBLE);
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    productListTable.remove(position);
                    total -= product.getPrixCarton() * product.getQuantite();
                    totalNewSale.setText(String.valueOf(total));
                    adapterRecTableProduct.notifyDataSetChanged();
                    recyclerViewTable.setLayoutManager(new
                            LinearLayoutManager(NewSaleActivity.this,
                            LinearLayoutManager.VERTICAL, false));
                    recyclerViewTable.setAdapter(adapterRecTableProduct);
                    dialogProduct.dismiss();
                }
            });
            dialogProduct.show();
        }
    }

    @Override
    public void onClick(View view) {
        if (view == submit) {
            if (Objects.equals(typeRec, "Product")){
                product.setQuantite(Integer.parseInt(amount.getText().toString()));
                total += product.getPrixCarton() * Integer.parseInt(amount.getText().toString());
                totalNewSale.setText(String.valueOf(total));
                addRow(product);
            } else if (Objects.equals(typeRec, "Table")) {
                product.setQuantite(Integer.parseInt(amount.getText().toString()));
                total += product.getPrixCarton() * Integer.parseInt(amount.getText().toString());
                totalNewSale.setText(String.valueOf(total));
                adapterRecTableProduct.notifyDataSetChanged();
                dialogProduct.dismiss();
            }
        } else if (view == cancel) {
            dialogProduct.dismiss();
        } else if (view == submitNewSale) {
            namesList = new ArrayList<>();
            userList = new ArrayList<>();
            getUsers();
            totalNewSale.setText(String.valueOf(total));
            payment.setText(String.valueOf(total));
            totalSubmitSale.setText(String.valueOf(total));
            submitPayment.setOnClickListener(this);
            cancelPayment.setOnClickListener(this);
            dialogSale.show();
        } else if (view == submitPayment) {
            Sale sale = new Sale();
            sale.setUidEmployee(firebaseAuth.getCurrentUser().getUid());
            sale.setUidClient(userList.get(spinner.getSelectedItemPosition()).getIdUser());
            sale.setProducts(productListTable);
            sale.setTotal(total);
            sale.setTotalPayed(Float.parseFloat(payment.getText().toString()));
            String uid = firestore.collection("Sales").document().getId();
            sale.setUid(uid);
            firestore.collection("Sales")
                    .document(uid)
                    .set(sale)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            startActivity(new Intent(NewSaleActivity.this, HomePageAdminActivity.class));
                            finish();
                            dialogSale.dismiss();
                        }
                    });
        } else if (view == cancelPayment) {
            dialogSale.dismiss();
        } else if (view == backArrowBtnNewSale) {
            startActivity(new Intent(NewSaleActivity.this, HomePageAdminActivity.class));
            finish();
        }
    }
}