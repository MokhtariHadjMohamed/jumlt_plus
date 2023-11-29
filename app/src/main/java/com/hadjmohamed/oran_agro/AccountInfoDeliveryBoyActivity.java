package com.hadjmohamed.oran_agro;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class AccountInfoDeliveryBoyActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private ImageView btnGoBack;
    private EditText nameAccountInfo, familyNameAccountInfo;
    private FirebaseFirestore firestore;
    private Button submitAccountInfo;
    //dialog variable
    private MaterialAlertDialogBuilder dialogBuilder;
    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_info_delivery_boy);

        nameAccountInfo = findViewById(R.id.nameAccountInfo);
        familyNameAccountInfo = findViewById(R.id.familyNameAccountInfo);
        submitAccountInfo = findViewById(R.id.submitAccountInfo);

        // ToolBar
        toolbar = findViewById(R.id.toolbarBack);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("معلومات الحساب");
        btnGoBack = findViewById(R.id.btnGoBack);

        // Firestore
        firestore = FirebaseFirestore.getInstance();
        firestore.collection("Users")
                .document(FirebaseAuth.getInstance()
                        .getCurrentUser().getUid())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        User user = task.getResult().toObject(User.class);
                        nameAccountInfo.setText(user.getName());
                        familyNameAccountInfo.setText(user.getFamilyName());
                    }
                });

        btnGoBack.setOnClickListener(this);
        submitAccountInfo.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (btnGoBack == view) {
            startActivity(new Intent(AccountInfoDeliveryBoyActivity.this,
                    AccountPageDeliveryBoyActivity.class));
            finish();
        } else if (view == submitAccountInfo) {
            dialogBuilder = new MaterialAlertDialogBuilder(AccountInfoDeliveryBoyActivity.this);
            dialogBuilder.setMessage("هل أنت متأكد تريد تغير اسمك؟");
            dialogBuilder.setTitle("Confirmation");
            dialogBuilder.setCancelable(false);
            dialogBuilder.setPositiveButton("نعم", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    firestore.collection("Users").document(FirebaseAuth.getInstance()
                                    .getCurrentUser().getUid())
                            .update(
                                    "name", nameAccountInfo.getText().toString(),
                                    "familyName", familyNameAccountInfo.getText().toString()
                            ).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    startActivity(new Intent(AccountInfoDeliveryBoyActivity.this,
                                            UserAccountActivity.class));
                                }
                            });
                }
            });

            dialogBuilder.setNegativeButton("لا", (DialogInterface.OnClickListener) (dialog, which) -> {
                // If user click no then dialog box is canceled.
                dialog.cancel();
            });

            alertDialog = dialogBuilder.create();
            alertDialog.show();
        }
    }
}
