package com.appsecco.vyapi.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import com.appsecco.vyapi.misc.ContactDBHandler;

public class ContactDBProvider extends ContentProvider {

    private static final String AUTHORITY = "com.appsecco.vyapi.ContactDBProvider";
    private static final String CONTACT_PATH = "contacts";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + CONTACT_PATH);

    public static final int ALL_CONTACTS = 1;
    public static final int SINGLE_CONTACT = 2;

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    private ContactDBHandler contactDBHandler;

    static {
        uriMatcher.addURI(AUTHORITY, CONTACT_PATH, ALL_CONTACTS);
        uriMatcher.addURI(AUTHORITY, CONTACT_PATH + "/contact/*", SINGLE_CONTACT);
    }

    public ContactDBProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO: Implement this to handle requests to insert a new row.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public boolean onCreate() {
        contactDBHandler = new ContactDBHandler(getContext(), null, null, 1);
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(contactDBHandler.CONTACTS_TABLE);

        int uriType = uriMatcher.match(uri);

        switch (uriType) {
            case SINGLE_CONTACT:
                queryBuilder.appendWhere(contactDBHandler.COLUMN_FNAME + "="
                        + uri.getLastPathSegment());
                break;
            case ALL_CONTACTS:
                break;
            default:
                throw new IllegalArgumentException("Unknown URI");
        }

        Cursor cursor = queryBuilder.query(contactDBHandler.getReadableDatabase(),
                projection, selection, selectionArgs, null, null,
                sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(),
                uri);
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
