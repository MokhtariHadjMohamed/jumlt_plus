package com.hadjmohamed.oran_agro.AdminAndDelivery;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hadjmohamed.oran_agro.R;
import com.hadjmohamed.oran_agro.User;

import java.util.Objects;

public class AddUserFragment extends Fragment implements View.OnClickListener {

    // TODO View var
    private View view;
    // TODO info
    private EditText name, fName, address, phone, email;
    private Spinner typeUser;
    private Button submit, cancel;

    // TODO Firebase
    private FirebaseFirestore firestore;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Firebase
        firestore = FirebaseFirestore.getInstance();

        // TODO View
        view = inflater.inflate(R.layout.fragment_add_user, container, false);

        // TODO info
        name = view.findViewById(R.id.nameAddClientFragment);
        fName = view.findViewById(R.id.familyNameAddClientFragment);
        address = view.findViewById(R.id.addressAddClientFragment);
        phone = view.findViewById(R.id.phoneNumberAddClientFragment);
        email = view.findViewById(R.id.emailAddClientFragment);
        submit = view.findViewById(R.id.submitAddClientFragment);
        typeUser = view.findViewById(R.id.typeUser);
        submit.setOnClickListener(this);
        cancel = view.findViewById(R.id.cancelBtnAddClient);
        cancel.setOnClickListener(this);

        // Create an ArrayAdapter using the string array and a default spinner layout.
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.users_array,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeUser.setAdapter(adapter);


        return view;
    }

    private void addClient(User user) {
        String uid = firestore.collection("Users").document().getId();
        user.setIdUser(uid);
        firestore.collection("Users")
                .document(uid)
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(AddUserFragment.this.getContext(), "Add User Done",
                                Toast.LENGTH_SHORT).show();
                        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                        fragmentManager.beginTransaction().remove(AddUserFragment.this).commit();
                        getActivity().finish();
                        startActivity(getActivity().getIntent());
                    }
                });
    }

    @Override
    public void onClick(View view) {
        if (view == submit) {
            User user = new User();
            user.setName(name.getText().toString());
            user.setFamilyName(fName.getText().toString());
            user.setAddress(address.getText().toString());
            user.setPhone(Integer.parseInt(phone.getText().toString()));
            user.setEmail(email.getText().toString());
            user.setType(typeUser.getSelectedItem().toString());
            addClient(user);
        } else if (view == cancel) {
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction().remove(AddUserFragment.this).commit();
        }
    }
}