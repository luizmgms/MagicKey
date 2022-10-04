package com.luiz.mg.magickey.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

import com.luiz.mg.magickey.db.FeedReaderContract;
import com.luiz.mg.magickey.db.FeedReaderDbHelper;
import com.luiz.mg.magickey.models.Key;
import com.luiz.mg.magickey.utils.Utils;

import java.util.ArrayList;

public class KeyDAO {

    private final FeedReaderDbHelper dbHelper;

    public KeyDAO(FeedReaderDbHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    //Adicionar uma chave
    public int addKey (Key key) {
        //Se a chave não existe
        if (consultKey(key.getName()) == null) {

            // Gets the data repository in write mode
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            // Create a new map of values, where column names are the keys
            ContentValues values = new ContentValues();
            values.put(FeedReaderContract.Feedkeys.COLUMN_NAME_NAME, key.getName());
            values.put(FeedReaderContract.Feedkeys.COLUMN_NAME_DEPT, key.getDept());
            values.put(FeedReaderContract.Feedkeys.COLUMN_NAME_BORROWED, key.getBorr());

            // Insert the new row, returning the primary key value of the new row
            long newRowId = db.insert(FeedReaderContract.Feedkeys.TABLE_NAME,
                    null, values);

            if (newRowId == -1) {

                return -1; //Erro ao adicionar chave

            } else {

                return 1; //Chave cadastrada com sucesso
            }

        } else {
            return 0; //Chave já existe
        }
    }

    //Adicionar Chaves de uma Lista
    public int addKeysOfList(ArrayList<Key> list) {

        int i = 0;

        if (!list.isEmpty()) {

            for (Key k : list) {
                int status = addKey(k);
                if (status == 0) {
                    Log.d("appkey", "Adicionar Chave " + k.getName() +
                            ": Chave já existe!");
                } else if (status == 1) {
                    Log.d("appkey", "Adicionar Chave " + k.getName() +
                            ": Sucesso!");
                    i++;
                } else {
                    Log.d("appkey", "Adicionar Chave " + k.getName() +
                            ": Erro Desconhecido!");
                }
            }
        }

        return i;
    }

    //Consultar chave
    public Key consultKey (String nameKey) {
        Key key = null;

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                BaseColumns._ID,
                FeedReaderContract.Feedkeys.COLUMN_NAME_NAME,
                FeedReaderContract.Feedkeys.COLUMN_NAME_DEPT,
                FeedReaderContract.Feedkeys.COLUMN_NAME_BORROWED
        };

        // Filter results WHERE "title" = 'My Title'
        String selection = FeedReaderContract.Feedkeys.COLUMN_NAME_NAME + " = ?";
        String[] selectionArgs = { nameKey };

        // How you want the results sorted in the resulting Cursor
        String sortOrder =
                FeedReaderContract.Feedkeys.COLUMN_NAME_NAME + " ASC";

        Cursor cursor = db.query(
                FeedReaderContract.Feedkeys.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                sortOrder               // The sort order
        );


        //ArrayList<Key> keys = new ArrayList<>();

        while(cursor.moveToNext()) {

            String name = cursor.getString(
                    cursor.getColumnIndexOrThrow(FeedReaderContract.Feedkeys.COLUMN_NAME_NAME));
            String dept = cursor.getString(
                    cursor.getColumnIndexOrThrow(FeedReaderContract.Feedkeys.COLUMN_NAME_DEPT));
            String bor = cursor.getString(
                    cursor.getColumnIndexOrThrow(FeedReaderContract.Feedkeys.COLUMN_NAME_BORROWED));

            //key = new Key(name, dept, bor);
            //keys.add(key);

        }

        cursor.close();

        return key;
    }

    //Deletar uma chave
    public int deleteKey (String nameKey) {

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Filter results WHERE "title" = 'My Title'
        String selection = FeedReaderContract.Feedkeys.COLUMN_NAME_NAME + " = ?";
        String[] selectionArgs = { nameKey };

        return db.delete(FeedReaderContract.Feedkeys.TABLE_NAME,
                selection, selectionArgs);

    }

    //Deletar todas as chaves
    public int deleteAllKeys() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete(FeedReaderContract.Feedkeys.TABLE_NAME,
                null, null);
    }

    //Lista todas as chaves
    public ArrayList<Key> listKeys (){
        ArrayList<Key> listKeys = new ArrayList<>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // How you want the results sorted in the resulting Cursor
        String sortOrder =
                FeedReaderContract.Feedkeys.COLUMN_NAME_NAME + " ASC";

        Cursor cursor = db.query(
                FeedReaderContract.Feedkeys.TABLE_NAME,   // The table to query
                null,             // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                sortOrder               // The sort order
        );

        while(cursor.moveToNext()) {

            String name = cursor.getString(
                    cursor.getColumnIndexOrThrow(FeedReaderContract.Feedkeys.COLUMN_NAME_NAME));
            String dept = cursor.getString(
                    cursor.getColumnIndexOrThrow(FeedReaderContract.Feedkeys.COLUMN_NAME_DEPT));
            String bor = cursor.getString(
                    cursor.getColumnIndexOrThrow(FeedReaderContract.Feedkeys.COLUMN_NAME_BORROWED));

            //Key key = new Key(name, dept, bor);
            //listKeys.add(key);
            //Log.d("listkey", name+"  "+dept+ ""+ bor);

        }

        cursor.close();

        return listKeys;
    }

    //Lista somente chaves do setor
    public ArrayList<Key> listKeys(String dept) {

        ArrayList<Key> listKeys = new ArrayList<>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Filter results WHERE "title" = 'My Title'
        String selection = FeedReaderContract.Feedkeys.COLUMN_NAME_DEPT + " = ? AND " +
                FeedReaderContract.Feedkeys.COLUMN_NAME_BORROWED + " = ?";
        String[] selectionArgs = { dept, Utils.NO_KEY };

        // How you want the results sorted in the resulting Cursor
        String sortOrder =
                FeedReaderContract.Feedkeys.COLUMN_NAME_NAME + " ASC";

        Cursor cursor = db.query(
                FeedReaderContract.Feedkeys.TABLE_NAME,   // The table to query
                null,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                sortOrder               // The sort order
        );

        while(cursor.moveToNext()) {

            String name = cursor.getString(
                    cursor.getColumnIndexOrThrow(FeedReaderContract.Feedkeys.COLUMN_NAME_NAME));
            String sect = cursor.getString(
                    cursor.getColumnIndexOrThrow(FeedReaderContract.Feedkeys.COLUMN_NAME_DEPT));
            String bor = cursor.getString(
                    cursor.getColumnIndexOrThrow(FeedReaderContract.Feedkeys.COLUMN_NAME_BORROWED));

           // Key key = new Key(name, sect, bor);
            //listKeys.add(key);

        }

        cursor.close();

        return listKeys;
    }

    //Pegar a chave
    public int takeKey(Key key) {

        // Gets the data repository in write mode
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
                values.put(FeedReaderContract.Feedkeys.COLUMN_NAME_BORROWED, Utils.YES_KEY);


        // Filter results WHERE "title" = 'My Title'
        String selection = FeedReaderContract.Feedkeys.COLUMN_NAME_NAME + " = ?";
        String[] selectionArgs = { key.getName() };

        // Insert the new row, returning the primary key value of the new row
        long statusId = db.update(
                FeedReaderContract.Feedkeys.TABLE_NAME,
                values,
                selection,
                selectionArgs);

        if (statusId == -1) {
            return  -1;
        } else {
            return 1;
        }

    }

    //Devolver chave
    public int backKey(Key key) {

        // Gets the data repository in write mode
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(FeedReaderContract.Feedkeys.COLUMN_NAME_BORROWED, Utils.NO_KEY);


        // Filter results WHERE "title" = 'My Title'
        String selection = FeedReaderContract.Feedkeys.COLUMN_NAME_NAME + " = ?";
        String[] selectionArgs = { key.getName() };

        // Insert the new row, returning the primary key value of the new row
        long statusId = db.update(
                FeedReaderContract.Feedkeys.TABLE_NAME,
                values,
                selection,
                selectionArgs);

        if (statusId == -1) {
            return  -1;
        } else {
            return 1;
        }

    }

}
