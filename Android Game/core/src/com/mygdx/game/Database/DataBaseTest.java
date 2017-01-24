package com.mygdx.game.Database;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.sql.Database;
import com.badlogic.gdx.sql.DatabaseCursor;
import com.badlogic.gdx.sql.DatabaseFactory;
import com.badlogic.gdx.sql.SQLiteGdxException;

/**
 * Created by Terry on 23/01/2017.
 */

public class DataBaseTest {

    Database dbHandler;

    public static final String TABLE_SETTINGS = "settings";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_ID_USER = "idUser";
    public static final String COLUMN_VOLUME = "volume";
    public static final String COLUMN_VIBREUR = "vibreur";

    private static final String DATABASE_NAME = "comments.db";
    private static final int DATABASE_VERSION = 1;

    private String test;
    private DatabaseCursor cursor;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table if not exists "
            + TABLE_SETTINGS + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_ID_USER + " integer not null, "
            + COLUMN_VOLUME + " integer not null, "
            + COLUMN_VIBREUR + " varchar not null);";

    public DataBaseTest() {

    }

    public void createDatabase(){
        Gdx.app.log("DatabaseTest", "creation started");
        dbHandler = DatabaseFactory.getNewDatabase(DATABASE_NAME,
                DATABASE_VERSION, DATABASE_CREATE, null);

        dbHandler.setupDatabase();
        try {
            dbHandler.openOrCreateDatabase();
            dbHandler.execSQL(DATABASE_CREATE);
        } catch (SQLiteGdxException e) {
            e.printStackTrace();
        }

        Gdx.app.log("DatabaseTest", "created successfully");

    }

    public void insertData(){
        try {
            dbHandler.execSQL("INSERT INTO settings ('idUser', 'volume', 'vibreur') VALUES (1, 1, 'off')");
        } catch (SQLiteGdxException e) {
            e.printStackTrace();
        }
    }

    public void deleteTable(){
        try {
            dbHandler.execSQL("DROP TABLE settings");
            Gdx.app.log("DatabaseTest", "Supprimé");
        } catch (SQLiteGdxException e) {
            e.printStackTrace();
        }
    }

    public void selectData(){
        DatabaseCursor cursor = null;

        try {
            cursor = dbHandler.rawQuery("SELECT * FROM settings");
        } catch (SQLiteGdxException e) {
            e.printStackTrace();
        }

        if(cursor != null) {
            while (cursor.next()) {
                Gdx.app.log("FromDb", String.valueOf(cursor.getInt(1)));
                Gdx.app.log("FromDb", String.valueOf(cursor.getInt(2)));
                Gdx.app.log("FromDb", String.valueOf(cursor.getString(3)));
            }
        }
    }

    public String returnData(String table, String column, String parameter, int data){
        DatabaseCursor cursor = null;
        String test = null;

        try {
            //cursor = dbHandler.rawQuery("SELECT " + column + " FROM " + table + " WHERE " + parameter + " = " + data);
            cursor = dbHandler.rawQuery("SELECT * FROM " + table + " WHERE " + parameter + " = " + data);
            System.out.println("SELECT " + column + " FROM " + table + " WHERE " + parameter + " = " + data);
        } catch (SQLiteGdxException e) {
            e.printStackTrace();
        }

        if(cursor != null) {
            while (cursor.next()) {
                Gdx.app.log("FromDb", String.valueOf(cursor.getString(3)));
                test = String.valueOf(cursor.getString(3));
            }
        }

        return test;
    }

    public void updateData(String table, String column,String newData, String parameter, int data){
        try {
            System.out.println("UPDATE " + table + " SET " + column + " = '" + newData + "' WHERE " + parameter + " = " + data);
            dbHandler.execSQL("UPDATE " + table + " SET " + column + " = '" + newData + "' WHERE " + parameter + " = " + data);
        } catch (SQLiteGdxException e) {
            e.printStackTrace();
        }
    }



    public void closeDatabase(){

        try {
            dbHandler.closeDatabase();
        } catch (SQLiteGdxException e) {
            e.printStackTrace();
        }
        dbHandler = null;
        Gdx.app.log("DatabaseTest", "dispose");
    }


}
