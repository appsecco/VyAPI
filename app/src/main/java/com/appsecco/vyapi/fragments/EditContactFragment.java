package com.appsecco.vyapi.fragments;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.appsecco.vyapi.R;
import com.appsecco.vyapi.contacts.Contact;
import com.appsecco.vyapi.contacts.ContactViewModel;

import java.security.InvalidKeyException;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditContactFragment extends Fragment {

    private int selected_id = -1;
    private String selected_fname;
    private String selected_lname;
    private String selected_phonenumber;
    private String selected_email;
    private String selected_website;
    private String selected_location;

    private EditText et_id_update;
    private EditText et_fname_update;
    private EditText et_lname_update;
    private EditText et_phonenumber_update;
    private EditText et_email_update;
    private EditText et_website_update;
    private EditText et_location_update;

    private Button btn_save_contact_update;
    private Button btn_go_back_contact_update;

    private ContactViewModel contactViewModel;


    public EditContactFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_contact, container, false);

        et_id_update = (EditText) view.findViewById(R.id.et_id_update);
        et_fname_update = (EditText) view.findViewById(R.id.et_fname_update);
        et_lname_update = (EditText) view.findViewById(R.id.et_lname_update);
        et_phonenumber_update = (EditText) view.findViewById(R.id.et_phonenumber_update);
        et_email_update = (EditText) view.findViewById(R.id.et_email_update);
        et_website_update = (EditText) view.findViewById(R.id.et_website_update);
        et_location_update = (EditText) view.findViewById(R.id.et_location_update);

        btn_save_contact_update = (Button) view.findViewById(R.id.btn_save_contact_update);
        btn_go_back_contact_update = (Button) view.findViewById(R.id.btn_go_back_contact_update);

        populateContactEditForm();

        btn_go_back_contact_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goBack();
            }
        });

        btn_save_contact_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    btnUpdateContact();
                } catch (InvalidKeyException e) {
                    e.printStackTrace();
                }
            }
        });

        contactViewModel = new ViewModelProvider(getActivity()).get(ContactViewModel.class);
        return view;
    }

    public void goBack(){
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
    }

    public void btnUpdateContact() throws InvalidKeyException {
        selected_id = HomeFragment.selected_id;

//        if(fname.trim().isEmpty() || phonenumber.trim().isEmpty() || email.trim().isEmpty() || website.trim().isEmpty()){
//            Toast.makeText(getActivity(), "Please provide following details: First name, Phone number, Email, Website", Toast.LENGTH_SHORT).show();
//            return;
//        }

        if(selected_id == -1){
            Toast.makeText(getContext(), "Contact could not be updated", Toast.LENGTH_SHORT).show();
            return;
        }

        selected_fname = et_fname_update.getText().toString().trim();
        selected_lname = et_lname_update.getText().toString().trim();
        selected_phonenumber = CreateContactFragment.encrypt(et_phonenumber_update.getText().toString().trim());
        selected_email = CreateContactFragment.encrypt(et_email_update.getText().toString().trim());
        selected_website = et_website_update.getText().toString().trim();
        selected_location = et_location_update.getText().toString().trim();

        Contact contact_to_update = new Contact(selected_fname, selected_lname, selected_phonenumber, selected_email, selected_website, selected_location);
        contact_to_update.setId(selected_id);

        contactViewModel.update(contact_to_update);
        Toast.makeText(getContext(), "Contact has been updated", Toast.LENGTH_SHORT).show();
        goBack();
    }

    public void populateContactEditForm(){
        et_fname_update.setText(HomeFragment.selected_fname);
        et_lname_update.setText(HomeFragment.selected_lname);
        et_phonenumber_update.setText(HomeFragment.selected_phonenumber);
        et_email_update.setText(HomeFragment.selected_email);
        et_website_update.setText(HomeFragment.selected_website);
        et_location_update.setText(HomeFragment.selected_location);
    }
}
