package com.mygdx.game.Database;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.sql.Database;
import com.badlogic.gdx.sql.DatabaseCursor;
import com.badlogic.gdx.sql.DatabaseFactory;
import com.badlogic.gdx.sql.SQLiteGdxException;

import java.util.ArrayList;
import java.util.Arrays;

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

    public static final String TABLE_SCORES = "scores";
    public static final String COLUMN_IDLEVEL = "idLevel";
    public static final String COLUMN_NAMELEVEL = "nameLevel";
    public static final String COLUMN_MAXSCORE = "maxScore";
    public static final String COLUMN_FINISHED = "finished";

    public static final String TABLE_USERS = "users";
    public static final String COLUMN_NAMEUSER = "nameUser";

    private static final String DATABASE_NAME = "comments.db";
    private static final int DATABASE_VERSION = 1;

    private DatabaseCursor cursor;

    private DataBaseTest db;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table if not exists "
            + TABLE_SETTINGS + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_ID_USER + " integer not null, "
            + COLUMN_VOLUME + " integer not null, "
            + COLUMN_VIBREUR + " varchar not null); \n create table if not exists "

            + TABLE_SCORES + "("
            + COLUMN_IDLEVEL + " integer primary key autoincrement, "
            + COLUMN_ID_USER + " integer not null, "
            + COLUMN_NAMELEVEL + " varchar not null, "
            + COLUMN_MAXSCORE + " integer not null, "
            + COLUMN_FINISHED + " varchar not null); \n create table if not exists "

            + TABLE_USERS + "("
            + COLUMN_ID_USER + " integer primary key autoincrement, "
            + COLUMN_NAMEUSER + " varchar not null);";

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

            dbHandler.execSQL("INSERT INTO scores ('idUser', 'nameLevel', 'maxScore', 'finished') VALUES (1, 'level1', 21500, 'true')");
            dbHandler.execSQL("INSERT INTO scores ('idUser', 'nameLevel', 'maxScore', 'finished') VALUES (1, 'level2', 34210, 'true')");
            dbHandler.execSQL("INSERT INTO scores ('idUser', 'nameLevel', 'maxScore', 'finished') VALUES (1, 'level3', 0, 'false')");
        } catch (SQLiteGdxException e) {
            e.printStackTrace();
        }
    }

    public void insertUser(String name){
        try {
            dbHandler.execSQL("INSERT INTO users ('nameUser') VALUES ('" + name + "')");
        } catch (SQLiteGdxException e) {
            e.printStackTrace();
        }
    }

    public void newUserData(String username){
        try {
            dbHandler.execSQL("INSERT INTO settings ('idUser', 'volume', 'vibreur') VALUES (" + getIdFromNameUser(username) + ", 1, 'on')");

            dbHandler.execSQL("INSERT INTO scores ('idUser', 'nameLevel', 'maxScore', 'finished') VALUES (" + getIdFromNameUser(username) + ", 'level1', 0, 'false')");
            dbHandler.execSQL("INSERT INTO scores ('idUser', 'nameLevel', 'maxScore', 'finished') VALUES (" + getIdFromNameUser(username) + ", 'level2', 0, 'false')");
            dbHandler.execSQL("INSERT INTO scores ('idUser', 'nameLevel', 'maxScore', 'finished') VALUES (" + getIdFromNameUser(username) + ", 'level3', 0, 'false')");
        } catch (SQLiteGdxException e) {
            e.printStackTrace();
        }
    }

    public void deleteTable(){
        try {
            dbHandler.execSQL("DROP TABLE settings");
            dbHandler.execSQL("DROP TABLE scores");
            dbHandler.execSQL("DROP TABLE users");
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
                Gdx.app.log("FromDb Settings", String.valueOf(cursor.getInt(1)));
                Gdx.app.log("FromDb", String.valueOf(cursor.getInt(2)));
                Gdx.app.log("FromDb", String.valueOf(cursor.getString(3)));
            }
        }

        cursor = null;

        try {
            cursor = dbHandler.rawQuery("SELECT * FROM scores");
        } catch (SQLiteGdxException e) {
            e.printStackTrace();
        }

        if(cursor != null) {
            while (cursor.next()) {
                Gdx.app.log("FromDb Scores", String.valueOf(cursor.getInt(1)));
                Gdx.app.log("FromDb", String.valueOf(cursor.getString(2)));
                Gdx.app.log("FromDb", String.valueOf(cursor.getInt(3)));
                Gdx.app.log("FromDb", String.valueOf(cursor.getString(4)));
            }
        }

        cursor = null;

        try {
            cursor = dbHandler.rawQuery("SELECT * FROM users");
        } catch (SQLiteGdxException e) {
            e.printStackTrace();
        }

        if(cursor != null) {
            while (cursor.next()) {
                Gdx.app.log("FromDb Users", String.valueOf(cursor.getString(1)));
                Gdx.app.log("FromDb Users ID", String.valueOf(cursor.getInt(0)));
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
            if(table.equalsIgnoreCase("settings")) {
                while (cursor.next()) {
                    Gdx.app.log("returnData", String.valueOf(cursor.getString(3)));
                    test = String.valueOf(cursor.getString(3));
                }
            }

            if(table.equalsIgnoreCase("scores")) {
                while (cursor.next()) {
                    Gdx.app.log("returnData", String.valueOf(cursor.getInt(3)));
                    test = String.valueOf(cursor.getInt(3));
                }
            }
        }

        return test;
    }

    //Retourne les scores de chaques niveaux
    public String returnLevelScore(int data, String data2){
        DatabaseCursor cursor = null;
        String val = null;

        try {
            System.out.println("SELECT * FROM scores WHERE idUser = " + data + " AND nameLevel = '" + data2 + "'");
            cursor = dbHandler.rawQuery("SELECT * FROM scores WHERE idUser = " + data + " AND nameLevel = '" + data2 + "'");
        } catch (SQLiteGdxException e) {
            e.printStackTrace();
        }

        if(cursor != null) {

                while (cursor.next()) {
                    Gdx.app.log("returnData", String.valueOf(cursor.getInt(3)));
                    val = String.valueOf(cursor.getInt(3));
                }

        }

        //Adapte l'affichage des scores
        if(val.equalsIgnoreCase("0")){
            val = "...";
        }

        return val;
    }

    //Retourne la liste des utilisateurs
    public ArrayList<String> returnAllUser(){
        DatabaseCursor cursor = null;
        ArrayList<String> obj = new ArrayList<String>();

        try {
            System.out.println("SELECT * FROM users");
            cursor = dbHandler.rawQuery("SELECT * FROM users");
        } catch (SQLiteGdxException e) {
            e.printStackTrace();
        }

        if(cursor != null) {

                while (cursor.next()) {
                    Gdx.app.log("returnAllUser", String.valueOf(cursor.getString(1)));
                    obj.add(String.valueOf(cursor.getString(1)));
                }

        }

        return obj;
    }



    //Retourne l'ID de l'utilisateur via son nameUser
    public int getIdFromNameUser(String username){
        DatabaseCursor cursor = null;
        int val = 0;

        try {
            cursor = dbHandler.rawQuery("SELECT * FROM users WHERE nameUser = '" + username + "'");
        } catch (SQLiteGdxException e) {
            e.printStackTrace();
        }

        if(cursor != null) {

            while (cursor.next()) {
                val = cursor.getInt(0);
            }

        }

        return val;
    }

    //Retourne les scores de chaques niveaux
    public ArrayList<String> checkLevel(String username){
        DatabaseCursor cursor = null;
        ArrayList<String> obj = new ArrayList<String>();

        try {
            System.out.println("SELECT * FROM scores WHERE idUser = " + getIdFromNameUser(username) + " AND finished = 'true'");
            cursor = dbHandler.rawQuery("SELECT * FROM scores WHERE idUser = " + getIdFromNameUser(username) + " AND finished = 'true'");
        } catch (SQLiteGdxException e) {
            e.printStackTrace();
        }

        if(cursor != null) {

            while (cursor.next()) {
                Gdx.app.log("CheckLevel", String.valueOf(cursor.getString(2)));
                obj.add(String.valueOf(cursor.getString(2)));
            }

        }


        return obj;
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
