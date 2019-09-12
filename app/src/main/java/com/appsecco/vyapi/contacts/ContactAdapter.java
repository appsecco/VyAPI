package com.appsecco.vyapi.contacts;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.appsecco.vyapi.R;
import com.appsecco.vyapi.fragments.CreateContactFragment;
import com.appsecco.vyapi.fragments.HomeFragment;

import java.security.InvalidKeyException;
import java.util.ArrayList;
import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactHolder> {

    private List<Contact> contacts = new ArrayList<>();
    private OnItemClickListener listener;
    private int index = -1;

    @NonNull
    @Override
    public ContactHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contact_item, parent, false);
        return new ContactHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactHolder holder, final int position) {
        Contact currentContact = contacts.get(position);
        holder.tvName.setText(currentContact.getFname() + " " + currentContact.getLname());
        holder.tvWebsite.setText(currentContact.getWebsite());

        holder.tvName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                index = position;
                notifyDataSetChanged();
            }
        });

        if(index == position){
            HomeFragment.selected_id = currentContact.getId();
            HomeFragment.selected_fname = currentContact.getFname();
            HomeFragment.selected_lname = currentContact.getLname();
            try {
                HomeFragment.selected_phonenumber = CreateContactFragment.decrypt(currentContact.getPhonenumber());
                HomeFragment.selected_email = CreateContactFragment.decrypt(currentContact.getEmail());
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            }
            HomeFragment.selected_website = currentContact.getWebsite();
            HomeFragment.selected_location = currentContact.getLocation();

            holder.tvName.setTextColor(Color.parseColor("#0033cc"));
        }else {
            holder.tvName.setTextColor(Color.parseColor("#000000"));
        }
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    // Populate the recycler view
    public void setContacts(List<Contact> contacts) {
        this.contacts = contacts;
        notifyDataSetChanged();
    }

    // Fetch a single contact
    public Contact getContactAt(int position) {
        return contacts.get(position);
    }

    class ContactHolder extends RecyclerView.ViewHolder {
        private TextView tvName;
//        private TextView tvPhone;
//        private TextView tvEmail;
        private TextView tvWebsite;
//        private TextView tvLocation;

        public ContactHolder(@NonNull View itemView) {
            super(itemView);
            this.tvName = itemView.findViewById(R.id.tvName);
            this.tvWebsite = itemView.findViewById(R.id.tvWebsite);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (listener != null && position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(contacts.get(position));
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Contact contact);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
