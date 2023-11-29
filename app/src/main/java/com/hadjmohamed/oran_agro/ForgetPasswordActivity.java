package com.hadjmohamed.oran_agro;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;

public class ForgetPasswordActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText email;
    private Button submit;
    private ImageView btnGoBack;
    //dialog variable
    private MaterialAlertDialogBuilder dialogBuilder;
    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        Toolbar toolbar = findViewById(R.id.toolbarBack);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("تغير كلمة السر");
        btnGoBack = findViewById(R.id.btnGoBack);
        btnGoBack.setOnClickListener(this);


        email = findViewById(R.id.emailForgetPass);
        submit = findViewById(R.id.submitForgetPass);
        submit.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if (view == submit) {
            dialogBuilder = new MaterialAlertDialogBuilder(this);
            dialogBuilder.setMessage("هل تريد تغير كلمة سر؟،\n سوف يتم ارسال تغير كلمة سر في ايمايل خاص بك.");
            dialogBuilder.setTitle("الطلب تغير كلمة السر");
            dialogBuilder.setCancelable(false);
            dialogBuilder.setPositiveButton("تغير", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (editTextTest(email))
                        FirebaseAuth.getInstance().sendPasswordResetEmail(email.getText().toString());
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("plain/text");
                    startActivity(Intent.createChooser(intent, ""));
                }
            });

            dialogBuilder.setNegativeButton("إلغاء", (DialogInterface.OnClickListener) (dialog, which) -> {
                // If user click no then dialog box is canceled.
                dialog.cancel();
            });

            alertDialog = dialogBuilder.create();
            alertDialog.show();
        } else if (view == btnGoBack) {
            startActivity(new Intent(ForgetPasswordActivity.this, LogInActivity.class));
            finish();
        }
    }

    private boolean editTextTest(EditText failed) {
        if (!failed.getText().toString().equals("")) {
            failed.setBackgroundResource(R.drawable.edit_text_shap);
            return true;
        }
        failed.setBackgroundResource(R.drawable.edit_text_shap_error);
        return false;

    }
}