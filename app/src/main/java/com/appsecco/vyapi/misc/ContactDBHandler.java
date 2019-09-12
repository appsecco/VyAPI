package com.appsecco.vyapi.misc;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.appsecco.vyapi.contacts.Contact;

public class ContactDBHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "vyapi_contacts_db";
    public static final String CONTACTS_TABLE = "contacts_table";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_FNAME = "fname";
    public static final String COLUMN_LNAME = "lname";
    public static final String COLUMN_PHONENUMBER = "phonenumber";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_WEBSITE = "website";
    public static final String COLUMN_LOCATION = "location";

    public ContactDBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " +
                CONTACTS_TABLE + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY,"
                + COLUMN_FNAME + " TEXT,"
                + COLUMN_LNAME + " TEXT,"
                + COLUMN_PHONENUMBER + " TEXT,"
                + COLUMN_EMAIL + " TEXT,"
                + COLUMN_WEBSITE + " TEXT,"
                + COLUMN_LOCATION + " TEXT"
                + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public Contact getContact(String fname){
        String query = "Select * FROM contacts_table WHERE fname = " + "\"" + fname + "\"";

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        Contact contact = new Contact();

        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            contact.setId(Integer.parseInt(cursor.getString(0)));
            contact.setFname(cursor.getString(1));
            contact.setLname(cursor.getString(2));
            contact.setPhonenumber(cursor.getString(3));
            contact.setEmail(cursor.getString(4));
            contact.setWebsite(cursor.getString(5));
            contact.setLocation(cursor.getString(6));
            cursor.close();
        } else {
            contact = null;
        }
        db.close();
        return contact;
    }
}
