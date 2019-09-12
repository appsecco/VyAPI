package com.appsecco.vyapi.contacts;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

public class ContactViewModel extends AndroidViewModel {
    private ContactRepository repository;
    private LiveData<List<Contact>> allContacts;
    private Contact singleContact;

    public ContactViewModel(@NonNull Application application) {
        super(application);
        repository = new ContactRepository(application);
        allContacts = repository.getAllContacts();
    }

    public void insert(Contact contact){
        repository.insert(contact);
    }

    public void update(Contact contact){
        repository.update(contact);
    }

    public void delete(Contact contact){
        repository.delete(contact);
    }

    public void deleteAllContacts(){
        repository.deleteAllContacts();
    }

    public LiveData<List<Contact>> getAllContacts(){
        return allContacts;
    }

    public Contact getContact(String fname) {
        singleContact = repository.getContact(fname);
        return singleContact;
    }

}
