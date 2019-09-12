package com.appsecco.vyapi.contacts;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ContactDAO {

    @Insert
    void insert(Contact contact);

    @Update
    void update(Contact contact);

    @Delete
    void delete(Contact contact);

    @Query("delete from contacts_table")
    void deleteAllContacts();

    @Query("select * from contacts_table order by id desc")
    LiveData<List<Contact>> getAllContacts();

    @Query("select * from contacts_table where fname = :fname limit 1")
    Contact getContact(String fname);
}
