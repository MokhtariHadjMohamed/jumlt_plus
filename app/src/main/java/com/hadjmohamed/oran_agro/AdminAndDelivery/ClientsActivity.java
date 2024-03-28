package com.hadjmohamed.oran_agro.AdminAndDelivery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.hadjmohamed.oran_agro.AdapterRecTableClient;
import com.hadjmohamed.oran_agro.R;
import com.hadjmohamed.oran_agro.RecViewInterface;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.List;

public class ClientsActivity extends AppCompatActivity implements RecViewInterface, View.OnClickListener {

    // TODO Button var
    private CardView add, home, category;
    // TODO RecyclerView view Client var
    private RecyclerView recyclerView;
    private AdapterRecTableClient adapterRecTableClient;
    private List<Client> clientList;
    private Client client;

    // TODO Firebase var
    private FirebaseFirestore firestore;

    // TODO Dialog
    private Dialog dialog;
    private EditText name, fName, email, address, phone;
    // TODO Dialog Button
    private Button cancel, submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clients);

        // TODO Firebase
        firestore = FirebaseFirestore.getInstance();

        // TODO RecyclerView
        recyclerView = findViewById(R.id.recyclerViewClient);
        clientList = new ArrayList<>();
        adapterRecTableClient = new AdapterRecTableClient(getApplicationContext(),
                clientList, this);
        getClient();

        // TODO Button
        add = findViewById(R.id.addClientActivity);
        home = findViewById(R.id.homeClientActivity);
        category = findViewById(R.id.allClientActivity);
        add.setOnClickListener(this);
        home.setOnClickListener(this);
        category.setOnClickListener(this);

        // TODO Dialog
        dialog = new Dialog(this);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.user_dialog);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        name = dialog.findViewById(R.id.nameUserDialog);
        fName = dialog.findViewById(R.id.familyNameUserDialog);
        email = dialog.findViewById(R.id.emailUserDialog);
        address = dialog.findViewById(R.id.addressUserDialog);
        phone = dialog.findViewById(R.id.phoneUserDialog);
        submit = dialog.findViewById(R.id.submitUserDialog);
        cancel = dialog.findViewById(R.id.cancelBtnUserDialog);

        submit.setOnClickListener(this);
        cancel.setOnClickListener(this);
    }

    private void getClient() {
        firestore.collection("Users")
                .whereEqualTo("type", "user")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (!task.isSuccessful())
                            Log.e("Field", "Get Client");
                        for (QueryDocumentSnapshot d : task.getResult())
                            clientList.add(d.toObject(Client.class));
                        adapterRecTableClient.notifyDataSetChanged();
                    }
                });
        recyclerView.setLayoutManager(new
                LinearLayoutManager(ClientsActivity.this,
                LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapterRecTableClient);
    }
    private void getClientWithDebt() {
        clientList.clear();
        firestore.collection("Users")
                .whereEqualTo("type", "user")
                .whereGreaterThan("totalDebt", 0)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (!task.isSuccessful())
                            Log.e("Field", "Get Client");
                        for (QueryDocumentSnapshot d : task.getResult())
                            clientList.add(d.toObject(Client.class));
                        adapterRecTableClient.notifyDataSetChanged();
                    }
                });
        recyclerView.setLayoutManager(new
                LinearLayoutManager(ClientsActivity.this,
                LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapterRecTableClient);
    }

    @Override
    public void onItemClick(String view, int position) {
        client = clientList.get(position);
        name.setText(client.getName());
        fName.setText(client.getFamilyName());
        email.setText(client.getEmail());
        address.setText(client.getAddress());
        phone.setText(String.valueOf(client.getPhone()));
        dialog.show();
    }

    @Override
    public void onClick(View view) {
        if (view == add) {
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.fragmentClients, AddClientFragment.class, new Bundle())
                    .commit();
        } else if (view == home) {
            startActivity(new Intent(ClientsActivity.this, HomePageAdminActivity.class));
            finish();
        } else if (view == category) {
            TextView textCategory = findViewById(R.id.textCategory);
            if (textCategory.getText().toString().equals("كل")){
                getClientWithDebt();
                textCategory.setText(">0");
            }else{
                textCategory.setText("كل");
                getClient();
            }
        } else if (view == cancel) {
            dialog.dismiss();
        } else if (view == submit) {
            client.setName(name.getText().toString());
            client.setFamilyName(fName.getText().toString());
            client.setEmail(email.getText().toString());
            client.setAddress(address.getText().toString());
            client.setPhone(Integer.parseInt(phone.getText().toString()));
            firestore.collection("Users")
                    .document(client.getIdUser())
                    .update(client.toHashMap())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(ClientsActivity.this, "Update done", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            finish();
                            startActivity(getIntent());
                        }
                    });
        }
    }
}