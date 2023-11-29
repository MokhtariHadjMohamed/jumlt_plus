package com.hadjmohamed.oran_agro;

import static android.content.ContentValues.TAG;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LogInActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText email, password;
    private TextView forgetPassword, register, errorPassOrEmail;
    private Button submit;
    private ProgressDialog progressDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        email = findViewById(R.id.emailLogIn);
        password = findViewById(R.id.passwordLogIn);

        forgetPassword = findViewById(R.id.forgetPasswordLogIn);
        register = findViewById(R.id.registerLogIn);
        errorPassOrEmail = findViewById(R.id.errorPassOrEmailLogIn);
        forgetPassword.setOnClickListener(this);
        register.setOnClickListener(this);

        submit = findViewById(R.id.submitLogIn);
        submit.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if (view == forgetPassword) {
            startActivity(new Intent(LogInActivity.this, ForgetPasswordActivity.class));
        } else if (view == register) {
            startActivity(new Intent(LogInActivity.this, RegisterActivity.class));
        } else if (view == submit) {
            if (email.getText().toString().isEmpty() || password.getText().toString().isEmpty()) {
                if(email.getText().toString().isEmpty())
                    email.setBackgroundResource(R.drawable.edit_text_shap_error);
                if (password.getText().toString().isEmpty())
                    password.setBackgroundResource(R.drawable.edit_text_shap_error);
            } else {
                // Progress
                progressDialog = new ProgressDialog(this);
                progressDialog.setCancelable(false);
                progressDialog.setMessage("Log In...");
                progressDialog.show();
                FirebaseAuth.getInstance().signInWithEmailAndPassword(email.getText().toString(),
                        password.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        testUser(authResult.getUser().getUid());
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        errorPassOrEmail.setText("رقم الهاتف او كلمة مرور خطأة");
                        email.setBackgroundResource(R.drawable.edit_text_shap_error);
                        password.setBackgroundResource(R.drawable.edit_text_shap_error);
                        Log.e(TAG, e.getMessage());
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();
                    }
                });;

            }
        }
    }

    private void testUser(String idUser) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("Users")
                .document(idUser)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (!task.isSuccessful()){
                            if (progressDialog.isShowing())
                                progressDialog.dismiss();
                            Log.e("Failed", "Failed");
                            return;
                        }
                        User user = task.getResult().toObject(User.class);
                        if (user.getType().equals("user")) {
                            startActivity(new Intent(LogInActivity.this, HomePageActivity.class));
                            finish();
                        } else if (user.getType().equals("deliveryBoy")) {
                            startActivity(new Intent(LogInActivity.this, HomePageDeliveryBoyActivity.class));
                            finish();
                        }
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();
                    }
                });
    }

}