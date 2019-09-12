package com.appsecco.vyapi.contacts;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

public class ContactRepository {

    private ContactDAO contactDAO;
    private LiveData<List<Contact>> allContacts;
    private Contact singleContact;

    public ContactRepository(Application application) {

        ContactDatabase database = ContactDatabase.getInstance(application);
        contactDAO = database.contactDAO();
        allContacts = contactDAO.getAllContacts();
    }

    public void insert(Contact contact){
        new InsertContactAsyncTask(contactDAO).execute(contact);
    }

    public void update(Contact contact){
        new UpdateContactAsyncTask(contactDAO).execute(contact);
    }

    public void delete(Contact contact){
        new DeleteContactAsyncTask(contactDAO).execute(contact);
    }

    public void deleteAllContacts(){
        new DeleteAllContactAsyncTask(contactDAO).execute();
    }

    public LiveData<List<Contact>> getAllContacts(){
        return allContacts;
    }

    public Contact getContact(String fname){
        singleContact = contactDAO.getContact(fname);
        return singleContact;
    }


    private static class InsertContactAsyncTask extends AsyncTask<Contact, Void, Void> {
        private ContactDAO contactDAO;

        private InsertContactAsyncTask(ContactDAO contactDAO){
            this.contactDAO = contactDAO;
        }

        @Override
        protected Void doInBackground(Contact... contacts) {
            contactDAO.insert(contacts[0]);
            return null;
        }
    }


    private static class UpdateContactAsyncTask extends AsyncTask<Contact, Void, Void> {
        private ContactDAO contactDAO;

        private UpdateContactAsyncTask(ContactDAO contactDAO){
            this.contactDAO = contactDAO;
        }

        @Override
        protected Void doInBackground(Contact... contacts) {
            contactDAO.update(contacts[0]);
            return null;
        }
    }


    private static class DeleteContactAsyncTask extends AsyncTask<Contact, Void, Void> {
        private ContactDAO contactDAO;

        private DeleteContactAsyncTask(ContactDAO contactDAO){
            this.contactDAO = contactDAO;
        }

        @Override
        protected Void doInBackground(Contact... contacts) {
            contactDAO.delete(contacts[0]);
            return null;
        }
    }


    private static class DeleteAllContactAsyncTask extends AsyncTask<Void, Void, Void> {
        private ContactDAO contactDAO;

        private DeleteAllContactAsyncTask(ContactDAO contactDAO){
            this.contactDAO = contactDAO;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            contactDAO.deleteAllContacts();
            return null;
        }
    }

}
