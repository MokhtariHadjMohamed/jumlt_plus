package com.hadjmohamed.oran_agro;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.MemoryCacheSettings;
import com.google.firebase.firestore.PersistentCacheSettings;
import com.hadjmohamed.oran_agro.AdminAndDelivery.ClientsActivity;
import com.hadjmohamed.oran_agro.AdminAndDelivery.EmployeesActivity;
import com.hadjmohamed.oran_agro.AdminAndDelivery.Expenses;
import com.hadjmohamed.oran_agro.AdminAndDelivery.ExpensesActivity;
import com.hadjmohamed.oran_agro.AdminAndDelivery.HomePageAdminActivity;
import com.hadjmohamed.oran_agro.AdminAndDelivery.NewSaleActivity;
import com.hadjmohamed.oran_agro.AdminAndDelivery.SalesActivity;
import com.hadjmohamed.oran_agro.AdminAndDelivery.WarehouseActivity;

public class MainActivity extends AppCompatActivity {

    // TODO Firebase Declaration
    private FirebaseFirestore firestore;
    // TODO ProgressDialog Declaration
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 100);
        // TODO Firebase
        firestore = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings =
                new FirebaseFirestoreSettings.Builder(firestore.getFirestoreSettings())
                        // Use memory-only cache
                        .setLocalCacheSettings(MemoryCacheSettings.newBuilder().build())
                        // Use persistent disk cache (default)
                        .setLocalCacheSettings(PersistentCacheSettings.newBuilder()
                                .build())
                        .build();
        firestore.setFirestoreSettings(settings);
        // TODO Progress
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Fetching data...");
        progressDialog.show();

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            Log.d("User", "IN");
            testUser(FirebaseAuth.getInstance().getCurrentUser().getUid());
        }else {
            startActivity(new Intent(MainActivity.this, LogInActivity.class));
            if (progressDialog.isShowing())
                progressDialog.dismiss();
        }
    }

    private void testUser(String idUser) {
        firestore.collection("Users")
                .document(idUser)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (!task.isSuccessful())
                            Log.e("Failed", "Failed");
                        User user = task.getResult().toObject(User.class);
                        if (user.getType().equals("user")) {
                            startActivity(new Intent(MainActivity.this, HomePageActivity.class));
                            if (progressDialog.isShowing())
                                progressDialog.dismiss();
                            finish();
                        } else if (user.getType().equals("admin") ||
                                user.getType().equals("employee") ||
                                user.getType().equals("deliveryBoy")) {
                            startActivity(new Intent(MainActivity.this, HomePageAdminActivity.class));
                            if (progressDialog.isShowing())
                                progressDialog.dismiss();
                            finish();
                        }
                    }
                });
    }
}