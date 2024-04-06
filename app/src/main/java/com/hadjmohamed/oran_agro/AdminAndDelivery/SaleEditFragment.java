package com.hadjmohamed.oran_agro.AdminAndDelivery;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hadjmohamed.oran_agro.AdaptersAndHolder.AdapterRecTableProduct;
import com.hadjmohamed.oran_agro.Product;
import com.hadjmohamed.oran_agro.R;
import com.hadjmohamed.oran_agro.RecViewInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class SaleEditFragment extends Fragment implements RecViewInterface, View.OnClickListener {

    // TODO view var
    private View view;
    // TODO firebase var
    private FirebaseFirestore firestore;
    private String collection;

    // TODO RecycleView var
    private RecyclerView recyclerView;
    private List<Product> productList;
    private AdapterRecTableProduct adapterRecTableProduct;
    private ProgressDialog progressDialog;
    // TODO UID Sale var
    private String uid;
    // TODO Element var
    private TextView nameClient, nameDeliveryBoy,
            total, totalPaid, remaining;
    private Button submit, delete, paid;
    private ImageView back;
    private Sale sale;

    // TODO Dialog Sale Var
    private Dialog dialogSale;
    private Float totalNum = 0.0f;
    private Spinner spinner;
    private TextView totalSubmitSale;
    private EditText payment;
    private Button submitPayment, cancelPayment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_sale_edit, container, false);

        // TODO UID Declaration
        uid = getArguments().getString("uid");
        collection = getArguments().getString("collection");

        // TODO Element Declaration
        nameClient = view.findViewById(R.id.nameClientFragmentSale);
        nameDeliveryBoy = view.findViewById(R.id.nameDeliveryBoyFragmentSale);
        total = view.findViewById(R.id.totalSaleFragment);
        totalPaid = view.findViewById(R.id.totalPaidSaleFragment);
        remaining = view.findViewById(R.id.remainingSaleFragment);
        submit = view.findViewById(R.id.submitFragmentSale);
        delete = view.findViewById(R.id.deleteFragmentSale);
        back = view.findViewById(R.id.backFragmentSale);
        paid = view.findViewById(R.id.paidFragmentSale);

        if (collection == "DeletedSales"){
            submit.setText("أسترداد");
            paid.setEnabled(false);
        }

        submit.setOnClickListener(this);
        delete.setOnClickListener(this);
        back.setOnClickListener(this);
        paid.setOnClickListener(this);

        // TODO Firebase Declaration
        firestore = FirebaseFirestore.getInstance();

        // TODO Progress Declaration
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Fetching data...");
        progressDialog.show();

        // TODO dialog submit sale
        dialogSale = new Dialog(getContext());
        dialogSale.setCancelable(false);
        dialogSale.setContentView(R.layout.submit_order_sale);
        dialogSale.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        dialogSale.findViewById(R.id.productNameSale).setVisibility(View.GONE);

        spinner = dialogSale.findViewById(R.id.buyerNeme);
        spinner.setVisibility(View.GONE);
        totalSubmitSale = dialogSale.findViewById(R.id.prudectPriceTotalPay);
        payment = dialogSale.findViewById(R.id.productPaymentSale);
        submitPayment = dialogSale.findViewById(R.id.submitPay);
        cancelPayment = dialogSale.findViewById(R.id.CancelPay);

        // TODO RecyclerView Product Declaration
        recyclerView = view.findViewById(R.id.recyclerViewProductSale);
        productList = new ArrayList<>();
        adapterRecTableProduct = new AdapterRecTableProduct(getContext(), productList, this);
        getSale(uid).thenAccept(
                this::putSale
        ).exceptionally(exception -> {
            // Handle exception here
            return null;
        });

        return view;
    }

    // TODO putSale function
    private void putSale(Sale sale2) {
        sale = sale2;
        totalNum = sale.getTotal();
    }

    // TODO getSale function
    private CompletableFuture<Sale> getSale(String uid) {
        CompletableFuture<Sale> futureCombiner = new CompletableFuture<>();
        firestore.collection(collection)
                .document(uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (!task.isSuccessful()) {
                            Log.e("Error", "get sale sale edit fragment");
                            if (progressDialog.isShowing())
                                progressDialog.dismiss();
                        }
                        Sale sale = task.getResult().toObject(Sale.class);
                        futureCombiner.complete(sale);
                        for (Product p : sale.getProducts())
                            productList.add(p);
                        total.setText(String.valueOf(sale.getTotal()));
                        totalPaid.setText(String.valueOf(sale.getTotalPayed()));
                        remaining.setText(String.valueOf(sale.getTotal() - sale.getTotalPayed()));
                        if (sale.getTotal() - sale.getTotalPayed() == 0) {
                            paid.setEnabled(false);
                        }
                        getUser(sale.getUidClient(), nameClient);
                        getUser(sale.getUidEmployee(), nameDeliveryBoy);
                        adapterRecTableProduct.notifyDataSetChanged();
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();
                    }
                });
        recyclerView.setLayoutManager(new
                LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapterRecTableProduct);
        return futureCombiner;
    }

    // TODO GET USER
    private void getUser(String uid, TextView name) {
        firestore.collection("Users")
                .document(uid)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Client user = documentSnapshot.toObject(Client.class);
                        name.setText(user.getName());
                    }
                });
    }

    // TODO Item Click
    @Override
    public void onItemClick(String view, int position) {
//        productList.remove(position);
//        adapterRecTableProduct.notifyDataSetChanged();
    }

    // TODO On Click
    @Override
    public void onClick(View view) {
        if (submit == view) {
            if (collection.equals("Sales")) {
                sale.setProducts(productList);
                firestore.collection(collection)
                        .document(uid)
                        .update(sale.toHashMap());
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().remove(SaleEditFragment.this).commit();
            } else {
                DialogInterface.OnClickListener diOnClickListener = (dialogInterface, i) -> {
                    FragmentManager fragmentManager;
                    switch (i) {
                        case DialogInterface.BUTTON_NEGATIVE:
                            fragmentManager = requireActivity().getSupportFragmentManager();
                            fragmentManager.beginTransaction().remove(SaleEditFragment.this).commit();
                            break;
                        case DialogInterface.BUTTON_POSITIVE:
                            firestore.collection("Sales").document(uid).set(sale);
                            firestore.collection("DeletedSales").document(uid).delete();
                            fragmentManager = requireActivity().getSupportFragmentManager();
                            fragmentManager.beginTransaction().remove(SaleEditFragment.this).commit();
                            getActivity().finish();
                            startActivity(getActivity().getIntent());
                            break;
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("هل تريد استرداد هذا العنصر؟")
                        .setPositiveButton("نعم", diOnClickListener)
                        .setNegativeButton("لا", diOnClickListener).show();
            }
        } else if (delete == view) {
            // TODO Delete sale btn
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    switch (i) {
                        case DialogInterface.BUTTON_NEGATIVE:
                            break;
                        case DialogInterface.BUTTON_POSITIVE:
                            if (collection.equals("Sales")) {
                                firestore.collection("DeletedSales").document(uid).set(sale);
                                firestore.collection("Sales").document(uid).delete();
                            } else {
                                firestore.collection("DeletedSales").document(uid).delete();
                            }
                            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                            fragmentManager.beginTransaction().remove(SaleEditFragment.this).commit();
                            getActivity().finish();
                            startActivity(getActivity().getIntent());
                            break;
                    }
                }
            };
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage("هل أنت متأكد؟").setPositiveButton("نعم", dialogClickListener)
                    .setNegativeButton("لا", dialogClickListener).show();

        } else if (back == view) {
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction().remove(SaleEditFragment.this).commit();
        } else if (paid == view) {
            payment.setText(String.valueOf(sale.getTotal() - sale.getTotalPayed()));
            totalSubmitSale.setText(String.valueOf(totalNum));
            submitPayment.setOnClickListener(this);
            cancelPayment.setOnClickListener(this);
            dialogSale.show();
        } else if (cancelPayment == view) {
            dialogSale.dismiss();
        } else if (submitPayment == view) {
            if ((sale.getTotal() - sale.getTotalPayed()) < Float.parseFloat(payment.getText().toString())) {
                Toast.makeText(getContext(), "مبلغ مدخل اكير من مبلغ مطلوب، وشكرا.", Toast.LENGTH_SHORT).show();
            } else {
                sale.setTotalPayed(sale.getTotalPayed() + Float.parseFloat(payment.getText().toString()));
                total.setText(String.valueOf(sale.getTotal()));
                totalPaid.setText(String.valueOf(sale.getTotalPayed()));
                remaining.setText(String.valueOf(sale.getTotal() - sale.getTotalPayed()));
                dialogSale.dismiss();
                if (sale.getTotal() - sale.getTotalPayed() == 0) {
                    paid.setEnabled(false);
                }
            }
        }
    }
}