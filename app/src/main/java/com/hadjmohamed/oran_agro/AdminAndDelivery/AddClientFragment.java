package com.hadjmohamed.oran_agro.AdminAndDelivery;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hadjmohamed.oran_agro.R;

public class AddClientFragment extends Fragment implements View.OnClickListener {

    // TODO View var
    private View view;
    // TODO info
    private EditText name, fName, address, phone, email;
    private Button submit, cancel;

    // TODO Firebase
    private FirebaseFirestore firestore;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Firebase
        firestore = FirebaseFirestore.getInstance();

        // TODO View
        view = inflater.inflate(R.layout.fragment_add_client, container, false);

        // TODO info
        name = view.findViewById(R.id.nameAddClientFragment);
        fName = view.findViewById(R.id.familyNameAddClientFragment);
        address = view.findViewById(R.id.addressAddClientFragment);
        phone = view.findViewById(R.id.phoneNumberAddClientFragment);
        email = view.findViewById(R.id.emailAddClientFragment);
        submit = view.findViewById(R.id.submitAddClientFragment);
        submit.setOnClickListener(this);
        cancel = view.findViewById(R.id.cancelBtnAddClient);
        cancel.setOnClickListener(this);

        return view;
    }
    private void addClient(Client client) {
        String uid = firestore.collection("Users").document().getId();
        client.setIdUser(uid);
        firestore.collection("Users")
                .document(uid)
                .set(client)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(AddClientFragment.this.getContext(), "Add User Done",
                                Toast.LENGTH_SHORT).show();
                        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                        fragmentManager.beginTransaction().remove(AddClientFragment.this).commit();
                    }
                });
    }
    @Override
    public void onClick(View view) {
        if (view == submit){
            Client client = new Client();
            client.setName(name.getText().toString());
            client.setFamilyName(fName.getText().toString());
            client.setAddress(address.getText().toString());
            client.setPhone(Integer.parseInt(phone.getText().toString()));
            client.setEmail(email.getText().toString());
            addClient(client);
        } else if (view == cancel) {
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction().remove(AddClientFragment.this).commit();
        }
    }
}