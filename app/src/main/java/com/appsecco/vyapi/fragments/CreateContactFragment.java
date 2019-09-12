package com.appsecco.vyapi.fragments;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.appsecco.vyapi.R;
import com.appsecco.vyapi.contacts.Contact;
import com.appsecco.vyapi.contacts.ContactViewModel;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

/**
 * A simple {@link Fragment} subclass.
 */
public class CreateContactFragment extends Fragment {

    private EditText et_fname;
    private EditText et_lname;
    private EditText et_phonenumber;
    private EditText et_email;
    private EditText et_website;
    private EditText et_location;

    private Button btn_save_contact;
    private Button btn_go_back_contact;

    private ContactViewModel contactViewModel;
    private static String AES = "AES";

    public CreateContactFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_create_contact, container, false);

        et_fname = (EditText) view.findViewById(R.id.et_fname);
        et_lname = (EditText) view.findViewById(R.id.et_lname);
        et_phonenumber = (EditText) view.findViewById(R.id.et_phonenumber);
        et_email = (EditText) view.findViewById(R.id.et_email);
        et_website = (EditText) view.findViewById(R.id.et_website);
        et_location = (EditText) view.findViewById(R.id.et_location);

        btn_save_contact = (Button) view.findViewById(R.id.btn_save_contact);
        btn_go_back_contact = (Button) view.findViewById(R.id.btn_go_back_contact);

        btn_save_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    btnAddContact();
                } catch (InvalidKeyException e) {
                    e.printStackTrace();
                }
            }
        });

        btn_go_back_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goBack();
            }
        });

        contactViewModel = new ViewModelProvider(getActivity()).get(ContactViewModel.class);
        return view;
    }


    public void btnAddContact() throws InvalidKeyException {
        String fname = et_fname.getText().toString().trim();
        String lname = et_lname.getText().toString().trim();
        String phonenumber = encrypt(et_phonenumber.getText().toString().trim());
        String email = encrypt(et_email.getText().toString().trim());
        String website = et_website.getText().toString().trim();
        String location = et_location.getText().toString().trim();

        // Business logic validation
        if(fname.trim().isEmpty() || phonenumber.trim().isEmpty() || email.trim().isEmpty() || website.trim().isEmpty()){
            Toast.makeText(getActivity(), "Please provide following details: First name, Phone number, Email, Website", Toast.LENGTH_SHORT).show();
            return;
        }

        contactViewModel.insert(new Contact(fname, lname, phonenumber, email, website, location));
        goBack();
    }


    public void goBack(){
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
    }


    public static String encrypt(String plaintextData) throws InvalidKeyException {
        SecretKeySpec key = generateKey("secretKey");
        try {
            Cipher cipher = Cipher.getInstance(AES);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encryptedData = cipher.doFinal(plaintextData.getBytes());
            String encryptedValue = Base64.encodeToString(encryptedData, Base64.URL_SAFE);
            return encryptedValue.replaceAll("\r","").replaceAll("\n", "");

        } catch (NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }

        return "";
    }


    public static String decrypt(String ciphertextData) throws InvalidKeyException {
        SecretKeySpec key = generateKey("secretKey");
        try {
            Cipher cipher = Cipher.getInstance(AES);
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] decodedData = Base64.decode(ciphertextData, Base64.URL_SAFE);
            byte[] decimalValue = cipher.doFinal(decodedData);
            String decryptedData = new String(decimalValue);
            return decryptedData;

        } catch (NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }

        return "";
    }


    public static SecretKeySpec generateKey(String secretKey) {
        SecretKeySpec secretKeySpec = null;
        try {
            final MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = secretKey.getBytes("UTF-8");
            digest.update(bytes,0, bytes.length);
            byte[] key = digest.digest();
            secretKeySpec = new SecretKeySpec(key, "AES");
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return secretKeySpec;
    }

}
