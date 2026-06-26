package com.hadjmohamed.oran_agro;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.hadjmohamed.oran_agro.models.Product;
import com.hadjmohamed.oran_agro.models.ProductOrder;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;

public class ProductActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseFirestore firestore;
    private TextView productName, priceUnit, priceCarton, priceCarton02, quantity;
    private EditText numQuantity;
    private ImageView increase, decrease, productImage;
    private Button submit;
    private int qnt = 1;
    private ImageView btnGoBack;
    private Toolbar toolbar;
    private ProgressDialog progressDialog;
    private Product product;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_activity);

        productName = findViewById(R.id.nameProductActivity);
        priceCarton = findViewById(R.id.priceCartonProductActivity);
        priceCarton02 = findViewById(R.id.priceCarton02ProductActivity);
        priceUnit = findViewById(R.id.priceUnitProductActivity);
        quantity = findViewById(R.id.quantityProductActivity);
        numQuantity = findViewById(R.id.numQuantityProductActivity);
        increase = findViewById(R.id.increaseProductActivity);
        decrease = findViewById(R.id.decreaseProductActivity);
        productImage = findViewById(R.id.productImageProductActivity);

        submit = findViewById(R.id.addProductActivity);

        increase.setOnClickListener(this);
        decrease.setOnClickListener(this);
        submit.setOnClickListener(this);

        numQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String input = charSequence.toString();
                if (!input.isEmpty()) {
                    try {
                        int enteredQuantity = Integer.parseInt(input);
                        int availableQuantity = product.getQuantite();

                        if (enteredQuantity > availableQuantity) {
                            numQuantity.setError("Only " + availableQuantity + " items in stock");
                            numQuantity.setText(String.valueOf(availableQuantity));
                        } else {
                            qnt = enteredQuantity;
                        }

                    } catch (NumberFormatException e) {
                        numQuantity.setError("Invalid number");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        // ToolBar
        toolbar = findViewById(R.id.toolbarBack);
        setSupportActionBar(toolbar);
        btnGoBack = findViewById(R.id.btnGoBack);

        btnGoBack.setOnClickListener(this);
        // Progress
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Fetching data...");
        progressDialog.show();

        // Firestore
        firestore = FirebaseFirestore.getInstance();
        String idProduct = getIntent().getStringExtra("IdProduct");
        firestore.collection("Products")
                .document(idProduct)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (!task.isSuccessful())
                            Log.e("Failed", "Failed");

                        product = task.getResult().toObject(Product.class);
                        productName.setText(product.getNameProduct());
                        priceCarton.setText(product.getPrixCarton() + "");
                        priceCarton02.setText(product.getPrixCarton() + "");
                        priceUnit.setText(product.getPrixUnitaire() + "");
                        quantity.setText(product.getQuantite() + "");

                        if (product.getImageUrl().isEmpty())
                            productImage.setImageResource(R.drawable.baseline_image_not_supported_24);
                        else
                            Picasso.get()
                                    .load(product.getImageUrl())
                                    .placeholder(R.drawable.loading_image)
                                    .error(R.drawable.baseline_image_not_supported_24)
                                    .into(productImage);

                        // toolbarName
                        toolbar.setTitle(product.getNameProduct());
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();
                    }
                });
    }

    @Override
    public void onClick(View view) {
        int availableQuantity = product.getQuantite();

        if (view == increase) {
            qnt++;
            if (qnt > availableQuantity) {
                numQuantity.setError("Only " + availableQuantity + " items in stock");
                numQuantity.setText(String.valueOf(availableQuantity));
            } else {
                numQuantity.setText(String.valueOf(qnt));
            }
        } else if (view == decrease) {
            qnt--;
            if (qnt < 1){
                qnt = 1;
                numQuantity.setText(String.valueOf(qnt));
            }else{
                numQuantity.setText(String.valueOf(qnt));
            }
        } else if (view == submit) {
            DocumentReference order = firestore.collection("ProductsOrder").document();
            Toast.makeText(this, "added: " + product.getNameProduct(), Toast.LENGTH_SHORT).show();
            order.set(new ProductOrder(
                    order.getId(),
                    product.getIdProduct(),
                    FirebaseAuth.getInstance().getCurrentUser().getUid(),
                    qnt,
                    "في انتظار تاكيد",
                    product.getNameProduct(),
                    product.getPrixCarton()
            ));
        } else if (view == btnGoBack) {
            startActivity(new Intent(ProductActivity.this, HomePageActivity.class));
            finish();
        }
    }
}