package com.example.warehouseapp.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.warehouseapp.Database.Hashing.Hash;

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
        String query = String.format("CREATE TABLE IF NOT EXISTS %s (%s TEXT PRIMARY KEY, %s TEXT, salt TEXT)", table, usernameColumn, passwordColumn);
        // execute the query
        db.execSQL(query);
    }

    // required method from extended abstract class
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + table); // drop the table
        onCreate(db); // recreate the table
    }

    public void drop() {
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("DROP TABLE IF EXISTS users");

        String query = String.format("CREATE TABLE IF NOT EXISTS %s (%s TEXT PRIMARY KEY, %s TEXT, salt TEXT)", table, usernameColumn, passwordColumn);
        // execute the query
        db.execSQL(query);
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

        // Generate a new salt
        String salt = Hash.generateSalt();

        // Hash the password with the salt
        String hashedPassword = Hash.hashPassword(password, salt);

        // create new cv object
        ContentValues values = new ContentValues();
        // place our params into their corresponding table columns
        values.put(usernameColumn, username);
        values.put(passwordColumn, hashedPassword);
        values.put("salt", salt);

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
    public boolean validateUser(String username, String password) {

        // Open the database for reading
        SQLiteDatabase db = this.getReadableDatabase();

        // Query the database to retrieve the stored hashed password and salt for the given username
        // We're only selecting the password and salt columns from the user table
        Cursor cursor = db.query(table, new String[]{passwordColumn, "salt"},
                usernameColumn + "=?", new String[]{username},
                null, null, null);

        // Check if a matching user was found
        if (cursor.moveToFirst()) {
            // Get the stored hashed password from the result
            String storedHash = cursor.getString(cursor.getColumnIndexOrThrow(passwordColumn));

            // Get the salt associated with that user's password
            String salt = cursor.getString(cursor.getColumnIndexOrThrow("salt"));

            // Hash the input password using the same salt
            String inputHash = Hash.hashPassword(password, salt);

            // Close the cursor after use to free resources
            cursor.close();

            // Compare the stored hashed password with the newly hashed input password
            // If they match, the login is successful
            return storedHash.equals(inputHash);
        }

        // No matching user found, or cursor failed to move to the first result
        // Close the cursor if it was opened
        cursor.close();

        // Login failed due to user not found or password mismatch
        return false;
    }


}
