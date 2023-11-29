package com.hadjmohamed.oran_agro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class AccountPageDeliveryBoyActivity extends AppCompatActivity implements View.OnClickListener {

    private Button accountInfo, language, singOut;
    private TextView name, phone, invitation;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_page_delivery_boy);

        name = findViewById(R.id.nameAccountDeliveryBoy);
        phone = findViewById(R.id.phoneNumberAccountDeliveryBoy);
        invitation = findViewById(R.id.invitationAccountDeliveryBoy);

        getUserInfo();

        accountInfo = findViewById(R.id.accountInfoAccountDeliveryBoy);
        language = findViewById(R.id.languageAccountDeliveryBoy);
        singOut = findViewById(R.id.singOutAccountDeliveryBoy);

        accountInfo.setOnClickListener(this);
        language.setOnClickListener(this);
        singOut.setOnClickListener(this);

        // Navigation bar Bottom
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationPageDeliveryBoy);
        bottomNavigationView.setSelectedItemId(R.id.accountNavigationPageDeliveryBoy);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.homeNavigationPageDeliveryBoy) {
                startActivity(new Intent(AccountPageDeliveryBoyActivity.this, HomePageDeliveryBoyActivity.class));
                return true;
            } else if (id == R.id.placeNavigationPageDeliveryBoy) {
                startActivity(new Intent(AccountPageDeliveryBoyActivity.this, OrdersPageDeliveryBoyActivity.class));
                return true;
            } else if (id == R.id.accountNavigationPageDeliveryBoy) {
                return true;
            } else {
                return true;
            }
        });
    }

    private void getUserInfo() {
        // Firestore
        firestore = FirebaseFirestore.getInstance();
        firestore.collection("Users")
                .document(FirebaseAuth.getInstance()
                        .getCurrentUser().getUid())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (!task.isSuccessful())
                            Log.e("Failed", "Failed");
                        User user = task.getResult().toObject(User.class);
                        name.setText(user.getName() + " " + user.getFamilyName());
                        phone.setText(String.valueOf(user.getPhone()));
                        invitation.setText(user.getInvitation());
                    }
                });
    }

    @Override
    public void onClick(View view) {
        if (view == accountInfo) {
            startActivity(new Intent(AccountPageDeliveryBoyActivity.this, AccountInfoDeliveryBoyActivity.class));
        } else if (view == language) {
            startActivity(new Intent(AccountPageDeliveryBoyActivity.this, LanguageDeliveryBoyAcitvity.class));
        } else if (view == singOut) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(AccountPageDeliveryBoyActivity.this, LogInActivity.class));
            finish();
        }
    }
}