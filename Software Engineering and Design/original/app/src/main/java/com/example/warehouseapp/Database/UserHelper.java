package com.example.warehouseapp.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class UserHelper extends SQLiteOpenHelper {

    private static final String databaseName = "warehouse_inventory.db"; // name of the sql lite database
    private static final int databaseVersion = 2;

    private static final String table = "users"; // points to the table for users
    private static final String usernameColumn =  "username"; // key for the username col
    private static final String passwordColumn = "password"; // key for the password col

    // constructor
    public UserHelper(Context context) {
        super(context, databaseName, null, databaseVersion);
    }

    // on database create method
    @Override
    public void onCreate(SQLiteDatabase db) {
        // standard sql table creation query, assigning username column as the primary key for the table
        String query = String.format("CREATE TABLE IF NOT EXISTS %s (%s TEXT PRIMARY KEY, %s TEXT)", table, usernameColumn, passwordColumn);
        // execute the query
        db.execSQL(query);
    }

    // required method from extended abstract class
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + table); // drop the table
        onCreate(db); // recreate the table
    }

    public boolean checkUserExists(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        // execute a new parameterized query by creating a cursor object. String[]{username} will bind to the where clause, only returning objects that exist in the column
        Cursor cursor = db.query(table, new String[]{usernameColumn}, usernameColumn + "=?", new String[]{username}, null, null, null);

        // determine if the user does exist
        boolean exists = (cursor.getCount() > 0);
        cursor.close(); // close the cursor

        return exists; // return the result
    }

    /**
     * Method to register a new user
     * @param username The user's new username
     * @param password The user's password. In a production app this would be stored as a hash and compared on login for security.
     * @return boolean indicating if the query was successful
     */
    public boolean registerUser(String username, String password) {
        if(checkUserExists(username)) return false; // if the user exists, this method cannot be executed

        SQLiteDatabase db = this.getWritableDatabase();

        // create new cv object
        ContentValues values = new ContentValues();
        // place our params into their corresponding table columns
        values.put(usernameColumn, username);
        values.put(passwordColumn, password);

        // insert the new object into table, and get a result to return a success indicator
        long result = db.insert(table, null, values);

        // close the db conn
        db.close();

        // return the success result
        return result != -1; // (db.insert will return -1 if it fails to execute)
    }

    /**
     * Method to validate if the user's inputted information is correct in the database
     * @param username The inputted username
     * @param password The inputted password
     * @return boolean indicating if the login was a success
     */
    public boolean validateUser (String username, String password) {

        SQLiteDatabase db = this.getReadableDatabase();

        // create a new cursor object
        Cursor cursor = db.query(table, new String[]{usernameColumn}, usernameColumn + "=? AND " + passwordColumn + "=?", new String[]{username, password}, null, null, null);

        boolean valid = (cursor.getCount() > 0); // essentially we just searched the database for a row that contained a match for both the user/pass columns.

        cursor.close(); // close cursor

        return valid; // return the result
    }

}
