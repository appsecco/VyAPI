package com.appsecco.vyapi.fragments;


import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.appsecco.vyapi.R;
import com.appsecco.vyapi.contacts.Contact;
import com.appsecco.vyapi.contacts.ContactAdapter;
import com.appsecco.vyapi.contacts.ContactViewModel;
import com.appsecco.vyapi.provider.ContactDBProvider;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private ContactViewModel contactViewModel;
    private ImageView iv_addnewcontact;
    private ImageView iv_editcontact;
    private ImageView iv_call;
    private ImageView iv_sendsms;
    private ImageView iv_sendemail;
    private ImageView iv_openwebsite;
    private ImageView iv_openlocation;

    public static int selected_id;
    public static String selected_fname;
    public static String selected_lname;
    public static String selected_phonenumber;
    public static String selected_email;
    public static String selected_website;
    public static String selected_location;

    private int row_index = -1; // Default: No row is selected
    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 1;

    private String sms_body = "";
    private LayoutInflater factory;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_home, container, false);
        factory = LayoutInflater.from(getContext());

        iv_addnewcontact = (ImageView) view.findViewById(R.id.iv_addnewcontact);
        iv_editcontact = (ImageView) view.findViewById(R.id.iv_editcontact);
        iv_call = (ImageView) view.findViewById(R.id.iv_call);
        iv_sendsms = (ImageView) view.findViewById(R.id.iv_sendsms);
        iv_sendemail = (ImageView) view.findViewById(R.id.iv_sendemail);
        iv_openwebsite = (ImageView) view.findViewById(R.id.iv_openwebsite);
        iv_openlocation = (ImageView) view.findViewById(R.id.iv_openlocation);

        iv_addnewcontact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new CreateContactFragment()).commit();
            }
        });

        // Reference to RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.contacts_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);

        // Reference to Adapter
        final ContactAdapter adapter = new ContactAdapter();
        recyclerView.setAdapter(adapter);

        // Get an instance of ContactViewModel
        contactViewModel = new ViewModelProvider(getActivity()).get(ContactViewModel.class);
        contactViewModel.getAllContacts().observe(getActivity(), new Observer<List<Contact>>() {
            @Override
            public void onChanged(List<Contact> contacts) {
                // Update the list in the adapter >> Update RecyclerView
                adapter.setContacts(contacts);
            }
        });

        // Swipe (left or right) to delete contact
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            // We are not using drag-and-drop feature
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                // Delete the swiped contact
                contactViewModel.delete(adapter.getContactAt(viewHolder.getAdapterPosition()));
                Toast.makeText(getContext(), "Contact deleted successfully!", Toast.LENGTH_SHORT).show();
            }
        }).attachToRecyclerView(recyclerView);


        iv_editcontact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Edit/update contact
//                EditContactFragment.setParameters(selected_fname, selected_lname, selected_phonenumber, selected_email, selected_website, selected_location);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new EditContactFragment()).commit();
            }
        });


        // Call a number
        iv_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Get calling permission
                if (getArguments() != null) {

                    if (Build.VERSION.SDK_INT >= 23) {
                        if (checkCallPermission(Manifest.permission.CALL_PHONE)) {
                            Log.e("permission", "Permission already granted.");
                        } else {
                            requestCallPermission(Manifest.permission.CALL_PHONE);
                        }
                    }

                }

                if (getActivity().checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                            Manifest.permission.CALL_PHONE)) {
                    } else {
                        ActivityCompat.requestPermissions(getActivity(), new String[]
                                {
                                        Manifest.permission.CALL_PHONE
                                }, PERMISSION_REQUEST_CODE);
                    }

                } else{
                    Intent callIntent = new Intent(Intent.ACTION_CALL);

                    // Decrypted phone number
                    callIntent.setData(Uri.parse("tel:" + selected_phonenumber));

                    callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(callIntent);
                }
            }
        });


        // Send SMS
        iv_sendsms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Get calling permission
                if (getArguments() != null) {

                    if (Build.VERSION.SDK_INT >= 23) {

                        if (checkCallPermission(Manifest.permission.SEND_SMS)) {
                            Log.e("permission", "Permission already granted.");
                        } else {
                            requestCallPermission(Manifest.permission.SEND_SMS);
                        }
                    }

                }

                if (getActivity().checkSelfPermission(Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                            Manifest.permission.SEND_SMS)) {
                    } else {
                        ActivityCompat.requestPermissions(getActivity(), new String[]
                                {
                                        Manifest.permission.SEND_SMS
                                }, MY_PERMISSIONS_REQUEST_SEND_SMS);
                    }

                } else{

                    // Get message
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("What do you want to say?");

                    // Set up the input
                    final EditText input = new EditText(getContext());
                    // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                    input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_SHORT_MESSAGE);
                    builder.setView(input);

                    // Set up the buttons
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            sms_body = input.getText().toString();

                            try {
                                sms_body = input.getText().toString();
                                SmsManager smsManager = SmsManager.getDefault();
                                smsManager.sendTextMessage(selected_phonenumber,null,sms_body,null,null);
                                Toast.makeText(getActivity(), "SMS sent to " + selected_fname + " " + selected_lname, Toast.LENGTH_SHORT).show();
                            }
                            catch (Exception e){
                                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    builder.show();

                }

            }
        });


        // Send Email
        iv_sendemail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                LayoutInflater factory = LayoutInflater.from(getContext());
                final View textEntryView = factory.inflate(R.layout.email_text_entry, null);
                // text_entry is an Layout XML file containing two text field to display in alert dialog
                final EditText input_email_subject = (EditText) textEntryView.findViewById(R.id.et_emailsubject);
                final EditText input_email_body = (EditText) textEntryView.findViewById(R.id.et_emailbody);
                final AlertDialog.Builder alert = new AlertDialog.Builder(getContext());

                alert.setIcon(R.drawable.sendemail)
                        .setTitle("Draft Your Email")
                        .setView(textEntryView)
                        .setPositiveButton("Send",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        Log.i("AlertDialog","TextEntry 1 Entered "+input_email_subject.getText().toString());
                                        Log.i("AlertDialog","TextEntry 2 Entered "+input_email_body.getText().toString());



                                        /* User clicked OK so do some stuff */
                                        final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
                                        emailIntent.setData(Uri.parse("mailto:"));
                                        emailIntent.setType("plain/text");

//                                        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] { "riddhi@appsecco.com" });
//                                        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] { selected_email });
                                        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] { selected_email });

                                        String email_subject = input_email_subject.getText().toString();
                                        String email_body = input_email_body.getText().toString();

//                                        // Encrypt email subject and email body
//                                        try {
//                                            encrypted_email_subject = encrypt(email_subject);
//                                            encrypted_email_body = encrypt(email_body);
//                                        } catch (InvalidKeyException e) {
//                                            e.printStackTrace();
//                                        }
//
//                                        emailIntent.putExtra(Intent.EXTRA_SUBJECT, encrypted_email_subject);
//                                        emailIntent.putExtra(Intent.EXTRA_TEXT,encrypted_email_body);

                                        emailIntent.putExtra(Intent.EXTRA_SUBJECT, email_subject);
                                        emailIntent.putExtra(Intent.EXTRA_TEXT, email_body);
                                        try{
                                            startActivity(Intent.createChooser(emailIntent,"Sending email..."));
                                            Log.i("SMS sent to " + selected_email, "");
                                        }catch (Exception e){
                                            Toast.makeText(getActivity(), "There is no email client installed.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int whichButton) {
                                    }
                                });
                alert.show();
            }
        });


        // Open Website
        iv_openwebsite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String URL;
                URL = selected_website;
                String tempWebsite = null;

                // [Secure] Fetch data from SQLite database and display in a pop-up
//                tempWebsite = contactViewModel.getContact(selected_fname).getEmail();

                // [Insecure]
                Uri uri_find_contact_by_fname = Uri.parse(ContactDBProvider.CONTENT_URI + "/contact/" + "fname");
                Cursor cursor = getActivity().getContentResolver().query(uri_find_contact_by_fname, new String[]{"website"}, "fname=?", new String[]{selected_fname}, null);
//                Cursor cursor = getContext().getContentResolver().query(uri_find_contact_by_fname, null, null, null, null);

                if(cursor == null){
                    Toast.makeText(getActivity(), "Cursor is null", Toast.LENGTH_SHORT).show();
                } else if(cursor.getCount() < 1){
                    Toast.makeText(getActivity(), "No results found", Toast.LENGTH_SHORT).show();
                } else {
//                    Toast.makeText(getActivity(), "Cursor has data", Toast.LENGTH_SHORT).show();
                    cursor.moveToFirst();
                    tempWebsite = cursor.getString(0);
                }

                final AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                final View data_from_db_view = factory.inflate(R.layout.display_data_from_db, null);

                TextView tv_data_from_db_title = data_from_db_view.findViewById(R.id.tv_data_from_db_title);
                TextView tv_data_from_db_display = data_from_db_view.findViewById(R.id.tv_data_from_db_display);

                tv_data_from_db_title.setText("You are being redirected to:");
                tv_data_from_db_display.setText(tempWebsite);
                alert.setView(data_from_db_view)
                    .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // Pass URL to fragment
                            Bundle bundle = new Bundle();
                            bundle.putString("URL", URL);

                            ViewWebsiteFragment viewWebsiteFragment = new ViewWebsiteFragment();
                            viewWebsiteFragment.setArguments(bundle);

                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, viewWebsiteFragment).commit();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
                        }
                    })
                    .show();
            }
        });


        iv_openlocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //                Uri gmmIntentUri = Uri.parse("geo:37.7749,-122.4194");
                Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + selected_location);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                if (mapIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(mapIntent);
                }
            }
        });

        return view;
    }


    public boolean checkCallPermission(String permission) {
        int CallPermissionResult = ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), permission);
        return CallPermissionResult == PackageManager.PERMISSION_GRANTED;
    }


    private void requestCallPermission(String permission) {
        ActivityCompat.requestPermissions(getActivity(), new String[]
                {
                        permission
                }, PERMISSION_REQUEST_CODE);
    }

}
