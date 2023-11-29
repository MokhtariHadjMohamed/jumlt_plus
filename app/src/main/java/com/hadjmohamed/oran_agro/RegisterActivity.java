package com.hadjmohamed.oran_agro;

import static android.content.ContentValues.TAG;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
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
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    private EditText name, familyName, password, phoneNumber, address, invitation, email;
    private TextView invitationError;
    private Button submit;
    private ImageView btnGoBack;
    private ProgressDialog progressDialog;

    // Location
    private final int FINE_PERMISSION_CODE = 1;
    private FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // toolbar
        Toolbar toolbar = findViewById(R.id.toolbarBack);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("تسجيل");
        btnGoBack = findViewById(R.id.btnGoBack);
        btnGoBack.setOnClickListener(this);

        name = findViewById(R.id.nameRegisterActivity);
        familyName = findViewById(R.id.familyNameRegisterActivity);
        password = findViewById(R.id.passwordRegisterActivity);
        phoneNumber = findViewById(R.id.phoneNumberRegisterActivity);
        invitation = findViewById(R.id.invitationRegisterActivity);
        address = findViewById(R.id.addressNameRegisterActivity);
        email = findViewById(R.id.emailRegisterActivity);

        invitationError = findViewById(R.id.invitationErrorRegister);

        invitation.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b)
                    return;
                categorySeparation(invitation.getText().toString());
            }
        });

        submit = findViewById(R.id.submitRegisterActivity);
        submit.setOnClickListener(this);

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Location
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
    }

    @Override
    public void onClick(View view) {
        if (view == submit) {
            EditText[] editTexts = {name, familyName, address, password, email, phoneNumber, invitation};
            for (EditText e :
                    editTexts) {
                editTextTest(e);
            }
            // Progress
            progressDialog = new ProgressDialog(this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Fetching data...");
            progressDialog.show();
            categorySeparation(invitation.getText().toString());
        } else if (view == btnGoBack) {
            startActivity(new Intent(RegisterActivity.this, LogInActivity.class));
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

    private void addUserToAuth(double latitude,double longitude) {
        auth.createUserWithEmailAndPassword(email.getText().toString(),
                password.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                addUserToFirestor(authResult.getUser().getUid(), latitude, longitude);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
                Log.d("Error create User:", e.getMessage());
            }
        });
    }

    private void addUserToFirestor(String idUser, double latitude, double longitude) {
        User user = new User(idUser, name.getText().toString(), familyName.getText().toString(),
                address.getText().toString(), email.getText().toString(),
                Integer.parseInt(phoneNumber.getText().toString()),
                invitation.getText().toString(), "user", latitude, longitude);

        firestore.collection("Users").document(String.valueOf(idUser))
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(RegisterActivity.this, "Register", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(RegisterActivity.this, HomePageActivity.class));
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }

    private void categorySeparation(String code) {
        firestore.collection("Category")
                .whereEqualTo("code", code)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.getDocuments().isEmpty()) {
                            if (progressDialog.isShowing())
                                progressDialog.dismiss();
                            invitation.setBackgroundResource(R.drawable.edit_text_shap_error);
                            invitationError.setText("الدعوة خاطأ");
                        } else {
                            getLastLocation();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();
                        Log.e("Error Code:", e.getMessage());
                    }
                });
    }

    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, FINE_PERMISSION_CODE);
            if (progressDialog.isShowing())
                progressDialog.dismiss();
            Log.e("Error Location:", "active location");
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    Geocoder geocoder = new Geocoder(RegisterActivity.this,
                            Locale.getDefault());
                    try {
                        List<Address> addressList =
                                geocoder.getFromLocation(location.getLatitude(),
                                        location.getLongitude(), FINE_PERMISSION_CODE);
                        double latitude = addressList.get(0).getLatitude();
                        double longitude = addressList.get(0).getLongitude();
                        addUserToAuth(latitude, longitude);

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
                Log.e("Location", e.getMessage());
            }
        });
    }

}