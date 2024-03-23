package com.hadjmohamed.oran_agro.AdminAndDelivery;

import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hadjmohamed.oran_agro.Category;
import com.hadjmohamed.oran_agro.Product;
import com.hadjmohamed.oran_agro.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UpdateProductFragment extends Fragment implements View.OnClickListener {

    // firebase
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;

    // Element
    private EditText name, priceUni, priceCarton, qnt;
    private Button submitProduct, cancelBtn, deleteBtn;
    private View view;
    private String uid;

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
        view = inflater.inflate(R.layout.fragment_update_product, container, false);
        // get data
        uid = getArguments().getString("uid");

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
        cancelBtn = view.findViewById(R.id.cancelBtnUpdateProduct);
        cancelBtn.setOnClickListener(this);
        deleteBtn = view.findViewById(R.id.deleteBtnUpdateProduct);
        deleteBtn.setOnClickListener(this);
        getProduct(uid);

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

    private void getProduct(String uid) {
        firestore.collection("Products")
                .document(uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (!task.isSuccessful())
                            Log.e("Failed", "Error get Product");
                        Product product = task.getResult().toObject(Product.class);
                        name.setText(product.getNameProduct());
                        qnt.setText(String.valueOf(product.getQuantite()));
                        priceCarton.setText(String.valueOf(product.getPrixCarton()));
                        priceUni.setText(String.valueOf(product.getPrixUnitaire()));
                        retrieveImage(uploadImage, product.getNameProduct());
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
                        fragmentManager.beginTransaction().remove(UpdateProductFragment.this).commit();
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

    private void updateProduct(Product product) {
        firestore.collection("Products")
                .document(uid)
                .update(product.toHashMap())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        uploadImage(product.getNameProduct());
                    }
                });
        Toast.makeText(getContext(), "Product Add", Toast.LENGTH_SHORT).show();
    }

    private void retrieveImage(ImageView imageView, String image) {
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageReference = firebaseStorage.getReference().child("Image")
                .child(image + ".png");
        storageReference.delete();

        final File file;
        try {
            file = File.createTempFile("img", ".png");

            storageReference.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    imageView.setImageBitmap(BitmapFactory.decodeFile(file.getAbsolutePath()));
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    imageView.setImageResource(R.drawable.baseline_file_upload_24);
                    Log.e("Image: " + image, e.getMessage());
                }
            });
        } catch (IOException e) {
            imageView.setImageResource(R.drawable.baseline_file_upload_24);
            throw new RuntimeException(e);
        }
    }

    private void deleteImage(String image){
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageReference = firebaseStorage.getReference().child("Image")
                .child(image + ".png");
        storageReference.delete();
    }

    @Override
    public void onClick(View view) {
        if (view == submitProduct) {
            Product product = new Product();
            product.setIdProduct(uid);
            product.setNameProduct(name.getText().toString());
            product.setPrixCarton(Float.parseFloat(priceCarton.getText().toString()));
            product.setPrixUnitaire(Float.parseFloat(priceUni.getText().toString()));
            product.setQuantite(Integer.parseInt(qnt.getText().toString()));
            product.setIDCategorie(categoryList.get(spinner.getSelectedItemPosition()).getIdCategory());
            updateProduct(product);
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction().remove(UpdateProductFragment.this).commit();
        } else if (view == uploadImage) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(
                    intent,
                    "Select Image from here..."), PICK_IMAGE_REQUEST);
        } else if (view == cancelBtn) {
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction().remove(UpdateProductFragment.this).commit();
        } else if (view == deleteBtn) {
            DialogInterface.OnClickListener dialogClickListener
                    = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        // on below line we are setting a click listener
                        // for our positive button
                        case DialogInterface.BUTTON_POSITIVE:
                            // on below line we are displaying a toast message.
                            firestore.collection("Products").document(uid).delete();
                            deleteImage(name.getText().toString());
                            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                            fragmentManager.beginTransaction().remove(UpdateProductFragment.this).commit();
                            break;
                        // on below line we are setting click listener
                        // for our negative button.
                        case DialogInterface.BUTTON_NEGATIVE:
                            // on below line we are dismissing our dialog box.
                            dialog.dismiss();
                    }
                }
            };
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            // on below line we are setting message for our dialog box.
            builder.setMessage("هل أنت متأكد من أنك تريد حذف هذا المنتج؟")
                    // on below line we are setting positive button
                    // and setting text to it.
                    .setPositiveButton("نعم", dialogClickListener)
                    // on below line we are setting negative button
                    // and setting text to it.
                    .setNegativeButton("لا", dialogClickListener)
                    // on below line we are calling
                    // show to display our dialog.
                    .show();
        }
    }
}