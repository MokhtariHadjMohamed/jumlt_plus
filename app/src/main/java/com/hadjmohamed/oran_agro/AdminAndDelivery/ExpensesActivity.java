package com.hadjmohamed.oran_agro.AdminAndDelivery;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.hadjmohamed.oran_agro.AdaptersAndHolder.AdapterRecExpenses;
import com.hadjmohamed.oran_agro.R;
import com.hadjmohamed.oran_agro.RecViewInterface;
import com.hadjmohamed.oran_agro.User;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ExpensesActivity extends AppCompatActivity implements RecViewInterface, View.OnClickListener {

    // TODO Firebase Declaration
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    private User user;
    // TODO ProgressDialog Declaration
    private ProgressDialog progressDialog;
    // TODO Recycle View Declaration variables
    private RecyclerView recyclerView;
    private AdapterRecExpenses adapterRecExpenses;
    private List<Expenses> expensesList;
    // TODO Elements Declaration
    private CardView home, add, category;
    private SearchView searchView;
    private TextView textCategory;
    // TODO Dialog Expense Declaration
    private Dialog dialogExpenses;
    private EditText expensesName, expensesPrice;
    private Spinner typeOfExpenses;
    private Button submit, cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expenses);
        // TODO Firebase
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // TODO Progress
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Fetching data...");
        progressDialog.show();

        // TODO Recycle View
        recyclerView = findViewById(R.id.recyclerViewExpensesActivity);
        expensesList = new ArrayList<>();
        adapterRecExpenses = new AdapterRecExpenses(this, expensesList, this);
        getUser().thenAccept(deliveryBoy -> {
            if (user.getType().equals("admin") || user.getType().equals("employee"))
                getExpenses();
            else{
                getExpensesDeliveryBoy();
                searchView.setVisibility(View.GONE);
            }
        }).exceptionally(exception -> {
            // Handle exception here
            return null;
        });

        // TODO Elements
        home = findViewById(R.id.homeExpensesActivity);
        add = findViewById(R.id.addExpensesActivity);
        category = findViewById(R.id.categoryExpensesActivity);
        textCategory = findViewById(R.id.textCategoryExpensesActivity);
        searchView = findViewById(R.id.searchViewExpenses);

        home.setOnClickListener(this);
        add.setOnClickListener(this);
        category.setOnClickListener(this);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                progressDialog.show();
                getExpensesByNameUser(s);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        // TODO dialog add expense
        dialogExpenses = new Dialog(this);
        dialogExpenses.setCancelable(false);
        dialogExpenses.setContentView(R.layout.add_expense_dialog);
        dialogExpenses.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        expensesName = dialogExpenses.findViewById(R.id.nameAddExpenses);
        expensesPrice = dialogExpenses.findViewById(R.id.priceAddExpenses);
        typeOfExpenses = dialogExpenses.findViewById(R.id.typeExpenses);
        submit = dialogExpenses.findViewById(R.id.submitAddExpenses);
        cancel = dialogExpenses.findViewById(R.id.cancelBtnAddExpenses);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.expenses_array,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeOfExpenses.setAdapter(adapter);

        submit.setOnClickListener(this);
        cancel.setOnClickListener(this);
    }

    private CompletableFuture<String> getUser() {
        CompletableFuture<String> future = new CompletableFuture<>();
        firestore.collection("Users")
                .document(auth.getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (!task.isSuccessful()) {
                            Log.e("Error", "get user logged in Sales activity");
                        }
                        user = task.getResult().toObject(User.class);
                        future.complete(user.getType());
                    }
                });
        return future;
    }

    private void getExpensesByNameUser(String s) {
        expensesList.clear();
        firestore.collection("Users")
                .where(Filter.and(
                        Filter.or(Filter.equalTo("name", s),
                                Filter.or(Filter.equalTo("type", "employee"),
                                        Filter.equalTo("type", "admin"),
                                        Filter.equalTo("type", "deliveryBoy"))
                        )))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (!task.isSuccessful()) {
                            Log.e(TAG, "Error on get on expenses");
                        }
                        for (QueryDocumentSnapshot d : task.getResult())
                            getExpensesSearch(d.toObject(User.class).getIdUser());
                    }
                });
    }

    private void getExpensesSearch(String uid) {
        expensesList.clear();
        firestore.collection("Expenses")
                .whereEqualTo("userUid", uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (!task.isSuccessful()) {
                            Log.e(TAG, "Error on get on expenses");
                        }
                        for (QueryDocumentSnapshot d : task.getResult())
                            expensesList.add(d.toObject(Expenses.class));
                        adapterRecExpenses.notifyDataSetChanged();
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();
                    }
                });
        recyclerView.setLayoutManager(new
                LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapterRecExpenses);
    }

    private void getExpenses() {
        expensesList.clear();
        firestore.collection("Expenses")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (!task.isSuccessful()) {
                            Log.e(TAG, "Error on get on expenses");
                        }
                        for (QueryDocumentSnapshot d : task.getResult())
                            expensesList.add(d.toObject(Expenses.class));
                        adapterRecExpenses.notifyDataSetChanged();
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();
                    }
                });
        recyclerView.setLayoutManager(new
                LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapterRecExpenses);
    }

    private void getExpensesDeliveryBoy() {
        expensesList.clear();
        firestore.collection("Expenses")
                .whereEqualTo("userUid", auth.getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (!task.isSuccessful()) {
                            Log.e(TAG, "Error on get on expenses");
                        }
                        for (QueryDocumentSnapshot d : task.getResult())
                            expensesList.add(d.toObject(Expenses.class));
                        adapterRecExpenses.notifyDataSetChanged();
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();
                    }
                });
        recyclerView.setLayoutManager(new
                LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapterRecExpenses);
    }

    private void getExpensesDeliveryBoy(String s) {
        expensesList.clear();
        firestore.collection("Expenses")
                .whereEqualTo("type", s)
                .whereEqualTo("userUid", auth.getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (!task.isSuccessful()) {
                            Log.e(TAG, "Error on get on expenses");
                        }
                        for (QueryDocumentSnapshot d : task.getResult())
                            expensesList.add(d.toObject(Expenses.class));
                        adapterRecExpenses.notifyDataSetChanged();
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();
                    }
                });
        recyclerView.setLayoutManager(new
                LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapterRecExpenses);
    }

    private void getExpenses(String s) {
        expensesList.clear();
        firestore.collection("Expenses")
                .whereEqualTo("type", s)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (!task.isSuccessful()) {
                            Log.e(TAG, "Error on get on expenses");
                        }
                        for (QueryDocumentSnapshot d : task.getResult())
                            expensesList.add(d.toObject(Expenses.class));
                        adapterRecExpenses.notifyDataSetChanged();
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();
                    }
                });
        recyclerView.setLayoutManager(new
                LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapterRecExpenses);
    }

    @Override
    public void onItemClick(String view, int position) {

    }

    @Override
    public void onClick(View view) {
        if (view == home) {
            startActivity(new Intent(ExpensesActivity.this, HomePageAdminActivity.class));
            finish();
        } else if (view == category) {
            switch (textCategory.getText().toString()) {
                case "كل":
                    if (user.getType().equals("admin") || user.getType().equals("employee")) {
                        getExpenses("food");
                    } else {
                        getExpensesDeliveryBoy("food");
                    }
                    textCategory.setText("food");
                    break;
                case "food":
                    if (user.getType().equals("admin") || user.getType().equals("employee")) {
                        getExpenses("gas");
                    } else {
                        getExpensesDeliveryBoy("gas");
                    }
                    textCategory.setText("gas");
                    break;
                case "gas":
                    if (user.getType().equals("admin") || user.getType().equals("employee"))
                        getExpenses();
                    else
                        getExpensesDeliveryBoy();
                    textCategory.setText("كل");
                    break;
            }
        } else if (view == add) {
            dialogExpenses.show();
        } else if (view == submit) {
            Expenses expenses = new Expenses();
            expenses.setName(expensesName.getText().toString());
            expenses.setPrice(Float.parseFloat(expensesPrice.getText().toString()));
            expenses.setUserUid(FirebaseAuth.getInstance().getUid());
            expenses.setType(typeOfExpenses.getSelectedItem().toString());
            String uid = firestore.collection("Expenses").document().getId();
            expenses.setUid(uid);
            firestore.collection("Expenses")
                    .document(uid)
                    .set(expenses)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            finish();
                            startActivity(getIntent());
                        }
                    });
        } else if (view == cancel) {
            dialogExpenses.dismiss();
        }
    }
}