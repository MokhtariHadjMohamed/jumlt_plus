package com.hadjmohamed.oran_agro;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserAccountActivity extends AppCompatActivity implements View.OnClickListener {

    private Button accountInfo, language, singOut, delete;
    private TextView name, phone;
    private FirebaseFirestore firestore;
    //dialog variable
    private MaterialAlertDialogBuilder dialogBuilder;
    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_account);

        name = findViewById(R.id.nameAccountUserAcitvity);
        phone = findViewById(R.id.phoneNumberAccountUserAcitvity);


        getUserInfo();

        accountInfo = findViewById(R.id.accountInfoUserAccount);
        language = findViewById(R.id.languageUserAccount);
        singOut = findViewById(R.id.singOutUserAccount);
        delete = findViewById(R.id.deleteUserAccount);

        accountInfo.setOnClickListener(this);
        language.setOnClickListener(this);
        singOut.setOnClickListener(this);
        delete.setOnClickListener(this);

        // Navigation bar Bottom
        BottomNavigationView bottomNavigationView = findViewById(R.id.navigation_bar_home);
        bottomNavigationView.setSelectedItemId(R.id.accountNavigation);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.homeNavigation) {
                startActivity(new Intent(UserAccountActivity.this, HomePageActivity.class));
                return true;
            } else if (id == R.id.searchNavigation) {
                startActivity(new Intent(UserAccountActivity.this, SearchPageActivity.class));
                return true;
            } else if (id == R.id.accountNavigation) {
                return true;
            } else if (id == R.id.categoryNavigation) {
                startActivity(new Intent(UserAccountActivity.this, CategoryPageActivity.class));
                return true;
            } else if (id == R.id.shoppingCartNavigation) {
                startActivity(new Intent(UserAccountActivity.this, ShoppingCartActivity.class));
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
                    }
                });
    }

    @Override
    public void onClick(View view) {
        if (view == accountInfo) {
            startActivity(new Intent(UserAccountActivity.this, AccountInfoActivity.class));
        } else if (view == language) {
            startActivity(new Intent(UserAccountActivity.this, LanguageActivity.class));
        } else if (view == singOut) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(UserAccountActivity.this, LogInActivity.class));
            finish();
        } else if (view == delete) {
            dialogBuilder = new MaterialAlertDialogBuilder(UserAccountActivity.this);
            dialogBuilder.setMessage("هل أنت متأكد انك تريد حدف حسابك؟");
            dialogBuilder.setTitle("Confirmation");
            dialogBuilder.setCancelable(false);
            dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    firestore.collection("Users")
                            .document(FirebaseAuth.getInstance().getCurrentUser().getUid()).delete();
                    FirebaseAuth.getInstance().getCurrentUser().delete();
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(UserAccountActivity.this, LogInActivity.class));
                }
            });

            dialogBuilder.setNegativeButton("Cancel", (DialogInterface.OnClickListener) (dialog, which) -> {
                // If user click no then dialog box is canceled.
                dialog.cancel();
            });

            alertDialog = dialogBuilder.create();
            alertDialog.show();
        }
    }
}