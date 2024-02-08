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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.core.OrderBy;
import com.google.firestore.v1.StructuredQuery;
import com.hadjmohamed.oran_agro.AdapterRecProduct;
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
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class NewSaleActivity extends AppCompatActivity implements RecViewInterface, View.OnClickListener {

    private RecyclerView recyclerView;
    private List<Product> productList, products;
    private Product product;
    private AdapterRecProduct adapterRecProduct;
    private ProgressDialog progressDialog;
    private TextView totalNewSale;
    private int total;
    private Button submitNewSale;

    // Search
    private String searchVariable;
    private SearchView searchView;
    // firebase
    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;

    //Table
    private TableLayout tableView;

    //dialog Product
    private Dialog dialogProduct, dialogSale;
    private TextView productName, productPricePiece, productPriceCarton, productPriceTotal;
    private Button submit, cancel;
    private EditText amount;

    //dialog Sale
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

        //dialog add product
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

        //dialog submit sale
        dialogSale = new Dialog(this);
        dialogSale.setCancelable(false);
        dialogSale.setContentView(R.layout.submit_order_sale);
        dialogSale.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        spinner = dialogSale.findViewById(R.id.buyerNeme);
        totalSubmitSale = dialogSale.findViewById(R.id.prudectPriceTotalPay);
        payment = dialogSale.findViewById(R.id.productPaymentSale);
        submitPayment = dialogSale.findViewById(R.id.submitPay);
        cancelPayment = dialogSale.findViewById(R.id.CancelPay);

        // Progress
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Fetching data...");
        progressDialog.show();

        // RecyclerView
        recyclerView = findViewById(R.id.recyclerViewProduct);
        productList = new ArrayList<>();
        products = new ArrayList<>();
        adapterRecProduct = new AdapterRecProduct(getApplicationContext(),
                productList, this);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        getProduct();

        // Search
        searchView = findViewById(R.id.searchViewNewSale);
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
        
        // Table
        tableView = findViewById(R.id.tableView);
        totalNewSale = findViewById(R.id.totalNewSale);
        submitNewSale = findViewById(R.id.submitNewSale);
        submitNewSale.setOnClickListener(this);
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

    @Override
    public void onItemClick(String view, int position) {
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
                }catch (Exception e){
                    Log.e("Error amount:", e.getMessage());
                }
            }
        });
        submit.setOnClickListener(this);
        cancel.setOnClickListener(this);
        dialogProduct.show();
    }
    private void addRow(String[] data){
        TableRow tableRow = new TableRow(this);
        TextView textView1 = new TextView(this);
        textView1.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT,
                10f));

        TextView textView2 = new TextView(this);
        textView2.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT,
                1f));

        TextView textView3 = new TextView(this);
        textView3.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT,
                1f));


        textView1.setText(data[0]);
        textView2.setText(data[1]);
        textView3.setText(data[2]);

        tableRow.addView(textView1);
        tableRow.addView(textView2);
        tableRow.addView(textView3);

        tableView.addView(tableRow);
        dialogProduct.dismiss();
        amount.setText("1");
        total += Integer.parseInt(productPriceTotal.getText().toString());
        totalNewSale.setText(String.valueOf(total));
    }
    private void getUsers(){
        firestore.collection("Users")
                .orderBy("name")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (!task.isSuccessful())
                            Log.e("Error:", "Field get users");
                        for(QueryDocumentSnapshot d : task.getResult()) {
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
    public void onClick(View view) {
        if (view == submit){
            products.add(product);
            addRow(new String[]{product.getNameProduct(),
                    String.valueOf(product.getPrixCarton() * Integer.parseInt(amount.getText().toString())),
                    product.getQuantite() + "*" + Integer.parseInt(amount.getText().toString())});
        }else if (view == cancel){
            dialogProduct.dismiss();
        }else if (view == submitNewSale){
            namesList = new ArrayList<>();
            userList = new ArrayList<>();
            getUsers();
            totalNewSale.setText(String.valueOf(total));
            payment.setText(String.valueOf(total));
            totalSubmitSale.setText(String.valueOf(total));
            submitPayment.setOnClickListener(this);
            cancelPayment.setOnClickListener(this);
            dialogSale.show();
        }else if (view == submitPayment){
            Sale sale = new Sale();
            sale.setUidEmployee(firebaseAuth.getCurrentUser().getUid());
            sale.setUidClient(userList.get(spinner.getSelectedItemPosition()).getIdUser());
            sale.setProducts(products);
            sale.setTotal(total);
            sale.setTotalPayed(Integer.parseInt(payment.getText().toString()));
            firestore.collection("Sales")
                    .add(sale)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            startActivity(new Intent(NewSaleActivity.this, HomePageAdminActivity.class));
                            finish();
                            dialogSale.dismiss();
                        }
                    });
        } else if (view == cancelPayment) {
            dialogSale.dismiss();
        }
    }
}