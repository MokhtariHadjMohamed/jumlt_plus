package com.hadjmohamed.oran_agro.AdminAndDelivery;

import static android.app.Activity.RESULT_OK;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hadjmohamed.oran_agro.Category;
import com.hadjmohamed.oran_agro.Product;
import com.hadjmohamed.oran_agro.R;
import com.hadjmohamed.oran_agro.User;

import org.checkerframework.checker.units.qual.C;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AddProductFragment extends Fragment implements View.OnClickListener {

    //firebase
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;

    // Element
    private EditText name, priceUni, priceCarton, qnt;
    private Button submitProduct, cancelBtn;
    private View view;

    //Spinner
    private Spinner spinner;
    private List<String> namesList;
    private List<Category> categoryList;

    // Progress Dialog
    private ProgressDialog progressDialog;
    // Image
    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView uploadImage;
    private Uri imageUri;
    private FirebaseStorage storage;
    private StorageReference storageReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_add_product, container, false);
        // Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Progress
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Fetching data...");
        progressDialog.show();

        // Element
        name = view.findViewById(R.id.productName);
        priceUni = view.findViewById(R.id.productPriceUnitaire);
        priceCarton = view.findViewById(R.id.productPriceCarton);
        qnt = view.findViewById(R.id.productQnt);
        submitProduct = view.findViewById(R.id.submitProduct);
        submitProduct.setOnClickListener(this);
        cancelBtn = view.findViewById(R.id.cancelBtnAddProduct);
        cancelBtn.setOnClickListener(this);

        // spinner Category
        spinner = view.findViewById(R.id.productCategory);
        namesList = new ArrayList<>();
        categoryList = new ArrayList<>();
        getCategory();

        //Image
        uploadImage = view.findViewById(R.id.imageUploadProduct);
        uploadImage.setOnClickListener(this);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        return view;
    }

    private void getCategory() {
        firestore.collection("SubCategory")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (!task.isSuccessful())
                            Log.e("Error:", "Field get users");
                        for (QueryDocumentSnapshot d : task.getResult()) {
                            namesList.add(d.toObject(Category.class).getName());
                            categoryList.add(d.toObject(Category.class));
                        }
                        String[] names = new String[namesList.size()];
                        namesList.toArray(names);
                        ArrayAdapter arrayAdapter = new ArrayAdapter(getContext(),
                                android.R.layout.simple_spinner_item, names);
                        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner.setAdapter(arrayAdapter);
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();
                    }
                });
    }

    private void addProduct(Product product) {
        String uid = firestore.collection("Products").document().getId();
        product.setIdProduct(uid);
        firestore.collection("Products")
                .document(uid)
                .set(product)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        uploadImage(product.getNameProduct());
                    }
                });
        Toast.makeText(getContext(), "Product Add", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            try {
                Bitmap bitmapOrigin = MediaStore.Images.Media.getBitmap(
                        getContext().getContentResolver(), imageUri);
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                Bitmap bitmap = ImageResizer.reduceBitmapSize(bitmapOrigin, 307200);
                uploadImage.setImageBitmap(bitmap);
                Toast.makeText(getContext(), "Upload", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Error Upload", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getContext(), "Filed", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadImage(String udi) {
        if (imageUri == null)
            return;

        ProgressDialog progressDialog
                = new ProgressDialog(getContext());
        progressDialog.setTitle("Uploading...");
        progressDialog.show();

        StorageReference ref = storageReference.child("Image/"
                + udi + ".png");
        Bitmap bitmapOrigin = null;
        try {
            bitmapOrigin = MediaStore.Images.Media.getBitmap(
                    getContext().getContentResolver(), imageUri);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //convert uri to byteArray
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Bitmap bitmap = ImageResizer.reduceBitmapSize(bitmapOrigin, 307200);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

        ref.putBytes(bytes.toByteArray())
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(getContext(),
                                        "Image Uploaded!!",
                                        Toast.LENGTH_SHORT)
                                .show();
                        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                        fragmentManager.beginTransaction().remove(AddProductFragment.this).commit();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(),
                                        "Failed " + e.getMessage(),
                                        Toast.LENGTH_SHORT)
                                .show();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    // Progress Listener for loading
                    // percentage on the dialog box
                    @Override
                    public void onProgress(
                            UploadTask.TaskSnapshot taskSnapshot) {
                        double progress
                                = (100.0
                                * taskSnapshot.getBytesTransferred()
                                / taskSnapshot.getTotalByteCount());
                        progressDialog.setMessage(
                                "Uploaded "
                                        + (int) progress + "%");
                        if (progress == 100)
                            progressDialog.cancel();
                    }
                });
    }

    @Override
    public void onClick(View view) {
        if (view == submitProduct) {
            Product product = new Product();
            product.setNameProduct(name.getText().toString());
            product.setPrixCarton(Float.parseFloat(priceCarton.getText().toString()));
            product.setPrixUnitaire(Float.parseFloat(priceUni.getText().toString()));
            product.setQuantite(Integer.parseInt(qnt.getText().toString()));
            product.setIDCategorie(categoryList.get(spinner.getSelectedItemPosition()).getIdCategory());
            addProduct(product);
        } else if (view == uploadImage) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(
                    intent,
                    "Select Image from here..."), PICK_IMAGE_REQUEST);
        } else if (view == cancelBtn){
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction().remove(AddProductFragment.this).commit();
        }
    }
}