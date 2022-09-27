package com.luiz.mg.magickey.dao;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

import com.luiz.mg.magickey.db.FeedReaderContract;
import com.luiz.mg.magickey.db.FeedReaderDbHelper;
import com.luiz.mg.magickey.models.Entry;
import com.luiz.mg.magickey.models.Key;
import com.luiz.mg.magickey.models.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class EntryDAO {

    private final FeedReaderDbHelper dbHelper;

    public EntryDAO(FeedReaderDbHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    //Adiciona Entrada
    public int addEntry(Key key, User user) {

        // Gets the data repository in write mode
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Date dateTimeStamp = new Date();
        @SuppressLint("SimpleDateFormat")
        String date = new SimpleDateFormat("dd/MM/yyyy").format(dateTimeStamp);
        @SuppressLint("SimpleDateFormat")
        String time = new SimpleDateFormat("HH:mm:ss").format(dateTimeStamp);

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();

        //Nome da Chave
        values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_KEY, key.getNameKey());

        //Matrícula, Nome, Data e Hora de quem pegou a Chave
        values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_MAT_TAKE, user.getMat());
        values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_NAME_TAKE, user.getName());
        values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_DATE_TAKE, date);
        values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_TIME_TAKE, time);

        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(FeedReaderContract.FeedEntry.TABLE_NAME,
                null, values);

        Log.d("appkey", "DateTimeTaked: "+key.getNameKey()+", "+user.getMat()+", "+ date+" "+time);

        if (newRowId == -1) {

            return -1; //Erro ao pegar chave

        } else {

            return 1; //Sucesso ao pegar chave
        }


    }

    //Atualiza Entrada
    public int upDateEntry(Key key, User user) {

        // Gets the data repository in write mode
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();

        Date dateTimeStamp = new Date();
        @SuppressLint("SimpleDateFormat")
        String date = new SimpleDateFormat("dd/MM/yyyy").format(dateTimeStamp);
        @SuppressLint("SimpleDateFormat")
        String time = new SimpleDateFormat("HH:mm:ss").format(dateTimeStamp);

        values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_MAT_BACK, user.getMat());
        values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_NAME_BACK, user.getName());
        values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_DATE_BACK, date);
        values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_TIME_BACK, time);

        // Upgrade Entry quando id_key = idKey AND mat_take = MatUser
        String selection = FeedReaderContract.FeedEntry.COLUMN_NAME_KEY + " = ? AND " +
                FeedReaderContract.FeedEntry.COLUMN_NAME_NAME_BACK + " IS NULL";
        String[] selectionArgs = { key.getNameKey() };

        long statusId = db.update(
                FeedReaderContract.FeedEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs);

        if (statusId == -1) {
            return  -1;
        } else {
            return 1;
        }
    }

    //Lista todas as entradas do usuário
    public ArrayList<Entry> listEntriesOfUser(String matUser) {
        ArrayList<Entry> entries = new ArrayList<>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();
//        String query = "SELECT * FROM entries WHERE mat = '" + matUser + "'";
//        Cursor cursor = db.rawQuery(query, null);

        // Filter results WHERE "title" = 'My Title'
        String selection = FeedReaderContract.FeedEntry.COLUMN_NAME_MAT_TAKE + " = ? AND "
                +FeedReaderContract.FeedEntry.COLUMN_NAME_DATE_BACK + " IS NULL";

        String[] selectionArgs = { matUser };

        // How you want the results sorted in the resulting Cursor
        String sortOrder =
                FeedReaderContract.FeedEntry.COLUMN_NAME_KEY + " ASC";

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                BaseColumns._ID,

                FeedReaderContract.FeedEntry.COLUMN_NAME_KEY,

                FeedReaderContract.FeedEntry.COLUMN_NAME_MAT_TAKE,
                FeedReaderContract.FeedEntry.COLUMN_NAME_NAME_TAKE,
                FeedReaderContract.FeedEntry.COLUMN_NAME_DATE_TAKE,
                FeedReaderContract.FeedEntry.COLUMN_NAME_TIME_TAKE,

                FeedReaderContract.FeedEntry.COLUMN_NAME_MAT_BACK,
                FeedReaderContract.FeedEntry.COLUMN_NAME_NAME_BACK,
                FeedReaderContract.FeedEntry.COLUMN_NAME_DATE_BACK,
                FeedReaderContract.FeedEntry.COLUMN_NAME_TIME_BACK
        };

        Cursor cursor = db.query(
                FeedReaderContract.FeedEntry.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                sortOrder               // The sort order
        );
        //Log.d("appkey:", "query: "+query);

        while(cursor.moveToNext()) {

            String nameKey = cursor.getString(
                    cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_KEY));

            String matUserTaked = cursor.getString(
                    cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_MAT_TAKE));
            String nameUserTaked = cursor.getString(
                    cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_NAME_TAKE));
            String dateTaked = cursor.getString(
                    cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_DATE_TAKE));
            String timeTaked = cursor.getString(
                    cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_TIME_TAKE));

            String matUserBacked = cursor.getString(
                    cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_MAT_BACK));
            String nameUserBacked= cursor.getString(
                    cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_NAME_BACK));
            String dateBacked = cursor.getString(
                    cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_DATE_BACK));
            String timeBacked = cursor.getString(
                    cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_TIME_BACK));

            Entry entry = new Entry(nameKey,
                    matUserTaked, nameUserTaked, dateTaked, timeTaked,
                    matUserBacked, nameUserBacked, dateBacked, timeBacked
            );

            entries.add(entry);

        }

        cursor.close();

        return entries;
    }

    //Lista todas as entradas do usuário por dia
    public ArrayList<Entry> listEntriesForDateTake(String dateTaked) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Filter results WHERE "title" = 'My Title'
        String selection = FeedReaderContract.FeedEntry.COLUMN_NAME_DATE_TAKE + " = ?";
        String[] selectionArgs = { dateTaked };

        // How you want the results sorted in the resulting Cursor
        String sortOrder =
                FeedReaderContract.FeedEntry.COLUMN_NAME_DATE_TAKE + " ASC";

        Cursor cursor = db.query(
                FeedReaderContract.FeedEntry.TABLE_NAME,   // The table to query
                null,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                sortOrder               // The sort order
        );


        ArrayList<Entry> entries = new ArrayList<>();

        while(cursor.moveToNext()) {

            String nameKey = cursor.getString(
                    cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_KEY));

            String matUserTaked = cursor.getString(
                    cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_MAT_TAKE));
            String nameUserTaked = cursor.getString(
                    cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_NAME_TAKE));
            String dateTake = cursor.getString(
                    cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_DATE_TAKE));
            String timeTaked = cursor.getString(
                    cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_TIME_TAKE));

            String matUserBacked = cursor.getString(
                    cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_MAT_BACK));
            String nameUserBacked= cursor.getString(
                    cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_NAME_BACK));
            String dateBacked = cursor.getString(
                    cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_DATE_BACK));
            String timeBacked = cursor.getString(
                    cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_TIME_BACK));


            Entry entry = new Entry(
                    nameKey,
                    matUserTaked, nameUserTaked, dateTake, timeTaked,
                    matUserBacked, nameUserBacked, dateBacked, timeBacked
            );

            entries.add(entry);

        }

        cursor.close();

        return entries;
    }

    //Lista todas as entradas do usuário por mês
    public ArrayList<Entry> listEntriesForMonthTake(String dateTaked) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                BaseColumns._ID,

                FeedReaderContract.FeedEntry.COLUMN_NAME_KEY,

                FeedReaderContract.FeedEntry.COLUMN_NAME_MAT_TAKE,
                FeedReaderContract.FeedEntry.COLUMN_NAME_NAME_TAKE,
                FeedReaderContract.FeedEntry.COLUMN_NAME_DATE_TAKE,
                FeedReaderContract.FeedEntry.COLUMN_NAME_TIME_TAKE,

                FeedReaderContract.FeedEntry.COLUMN_NAME_MAT_BACK,
                FeedReaderContract.FeedEntry.COLUMN_NAME_NAME_BACK,
                FeedReaderContract.FeedEntry.COLUMN_NAME_DATE_BACK,
                FeedReaderContract.FeedEntry.COLUMN_NAME_TIME_BACK
        };

        // Filter results WHERE "title" = 'My Title'
        String selection = FeedReaderContract.FeedEntry.COLUMN_NAME_DATE_TAKE + " LIKE ?";

        String[] dateP = dateTaked.split("/");
        String like = "__/"+dateP[1]+"/"+dateP[2];

        String[] selectionArgs = { like };
        Log.d("appkey", "Like: "+like);

        // How you want the results sorted in the resulting Cursor
        String sortOrder =
                FeedReaderContract.FeedEntry.COLUMN_NAME_DATE_TAKE + " ASC";

        Cursor cursor = db.query(
                FeedReaderContract.FeedEntry.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                sortOrder               // The sort order
        );

        ArrayList<Entry> entries = new ArrayList<>();

        while(cursor.moveToNext()) {

            String nameKey = cursor.getString(
                    cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_KEY));

            String matUserTaked = cursor.getString(
                    cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_MAT_TAKE));
            String nameUserTaked = cursor.getString(
                    cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_NAME_TAKE));
            String dateTake = cursor.getString(
                    cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_DATE_TAKE));
            String timeTaked = cursor.getString(
                    cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_TIME_TAKE));

            String matUserBacked = cursor.getString(
                    cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_MAT_BACK));
            String nameUserBacked= cursor.getString(
                    cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_NAME_BACK));
            String dateBacked = cursor.getString(
                    cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_DATE_BACK));
            String timeBacked = cursor.getString(
                    cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_TIME_BACK));

            Entry entry = new Entry(
                    nameKey,
                    matUserTaked, nameUserTaked, dateTake, timeTaked,
                    matUserBacked, nameUserBacked, dateBacked, timeBacked
            );

            entries.add(entry);

        }

        cursor.close();

        return entries;
    }

    //Lista todas as entradas do usuário por ano
    public ArrayList<Entry> listEntriesForYearTake(String dateTaked) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                BaseColumns._ID,

                FeedReaderContract.FeedEntry.COLUMN_NAME_KEY,

                FeedReaderContract.FeedEntry.COLUMN_NAME_MAT_TAKE,
                FeedReaderContract.FeedEntry.COLUMN_NAME_NAME_TAKE,
                FeedReaderContract.FeedEntry.COLUMN_NAME_DATE_TAKE,
                FeedReaderContract.FeedEntry.COLUMN_NAME_TIME_TAKE,

                FeedReaderContract.FeedEntry.COLUMN_NAME_MAT_BACK,
                FeedReaderContract.FeedEntry.COLUMN_NAME_NAME_BACK,
                FeedReaderContract.FeedEntry.COLUMN_NAME_DATE_BACK,
                FeedReaderContract.FeedEntry.COLUMN_NAME_TIME_BACK
        };

        // Filter results WHERE "title" = 'My Title'
        String selection = FeedReaderContract.FeedEntry.COLUMN_NAME_DATE_TAKE + " LIKE ?";

        String[] dateP = dateTaked.split("/");
        String like = "__/__/"+dateP[2];

        String[] selectionArgs = { like };
        Log.d("appkey", "Like: "+like);

        // How you want the results sorted in the resulting Cursor
        String sortOrder =
                FeedReaderContract.FeedEntry.COLUMN_NAME_DATE_TAKE + " ASC";

        Cursor cursor = db.query(
                FeedReaderContract.FeedEntry.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                sortOrder               // The sort order
        );

        ArrayList<Entry> entries = new ArrayList<>();

        while(cursor.moveToNext()) {

            String nameKey = cursor.getString(
                    cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_KEY));

            String matUserTaked = cursor.getString(
                    cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_MAT_TAKE));
            String nameUserTaked = cursor.getString(
                    cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_NAME_TAKE));
            String dateTake = cursor.getString(
                    cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_DATE_TAKE));
            String timeTaked = cursor.getString(
                    cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_TIME_TAKE));

            String matUserBacked = cursor.getString(
                    cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_MAT_BACK));
            String nameUserBacked= cursor.getString(
                    cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_NAME_BACK));
            String dateBacked = cursor.getString(
                    cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_DATE_BACK));
            String timeBacked = cursor.getString(
                    cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_TIME_BACK));

            Entry entry = new Entry(
                    nameKey,
                    matUserTaked, nameUserTaked, dateTake, timeTaked,
                    matUserBacked, nameUserBacked, dateBacked, timeBacked
            );

            entries.add(entry);

        }

        cursor.close();

        return entries;
    }

    /*public int deleteAllRowsOfEntry () {
        // Gets the data repository in write mode
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.delete(FeedReaderContract.FeedEntry.TABLE_NAME,
                null, null);

        if (newRowId == -1) {

            Log.d("appkey", "DeleteAllEntry: Erro ao deletar todas as Entry");

            return -1;

        } else {
            Log.d("appkey", "DeleteAllEntry: Sucesso ao deletar todas as Entry");

            return 1;
        }
    }*/

    /*public int verifyBorrToUSer(Key key, User user) {

        Entry entry = null;

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                BaseColumns._ID,
                FeedReaderContract.FeedEntry.COLUMN_NAME_MAT,
                FeedReaderContract.FeedEntry.COLUMN_NAME_NAME,
                FeedReaderContract.FeedEntry.COLUMN_NAME_CHAVE,
                FeedReaderContract.FeedEntry.COLUMN_NAME_DATE_TAKE,
                FeedReaderContract.FeedEntry.COLUMN_NAME_TIME_TAKE,
                FeedReaderContract.FeedEntry.COLUMN_NAME_DATE_BACK,
                FeedReaderContract.FeedEntry.COLUMN_NAME_TIME_BACK
        };

        // Filter results WHERE "title" = 'My Title'
        String selection = FeedReaderContract.FeedEntry.COLUMN_NAME_MAT + " = ?"+
                " AND "+FeedReaderContract.FeedEntry.COLUMN_NAME_CHAVE + " = ?";
        String[] selectionArgs = { user.getMat(), key.getName() };

        Cursor cursor = db.query(
                FeedReaderContract.FeedEntry.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null               // The sort order
        );

        while(cursor.moveToNext()) {

            String matr = cursor.getString(
                    cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_MAT));
            String name = cursor.getString(
                    cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_NAME));
            String nameKey = cursor.getString(
                    cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_CHAVE));
            String dat = cursor.getString(
                    cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_DATE_TAKE));
            String tit = cursor.getString(
                    cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_TIME_TAKE));
            String dab = cursor.getString(
                    cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_DATE_BACK));
            String tib = cursor.getString(
                    cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_TIME_BACK));


            //Log.d("entry", matr+" | "+name+" | "+nameKey+" | "+dat+" | "+tit+" | "+dab+" | "+tib);
            entry = new Entry(matr, name, nameKey, dat, tit, dab, tib);

        }

        cursor.close();

        if (entry == null)
            return -1;
        else
            return 1;
    }*/

    /*public ArrayList<Entry> listAllEntries() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                BaseColumns._ID,
                FeedReaderContract.FeedEntry.COLUMN_NAME_MAT,
                FeedReaderContract.FeedEntry.COLUMN_NAME_NAME,
                FeedReaderContract.FeedEntry.COLUMN_NAME_CHAVE,
                FeedReaderContract.FeedEntry.COLUMN_NAME_DATE_TAKE,
                FeedReaderContract.FeedEntry.COLUMN_NAME_TIME_TAKE,
                FeedReaderContract.FeedEntry.COLUMN_NAME_DATE_BACK,
                FeedReaderContract.FeedEntry.COLUMN_NAME_TIME_BACK
        };

        // How you want the results sorted in the resulting Cursor
        String sortOrder =
                FeedReaderContract.FeedEntry.COLUMN_NAME_DATE_TAKE + " DESC";

        Cursor cursor = db.query(
                FeedReaderContract.FeedEntry.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                sortOrder               // The sort order
        );


        ArrayList<Entry> entries = new ArrayList<>();

        while(cursor.moveToNext()) {

            String matr = cursor.getString(
                    cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_MAT));
            String name = cursor.getString(
                    cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_NAME));
            String nameKey = cursor.getString(
                    cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_CHAVE));
            String dat = cursor.getString(
                    cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_DATE_TAKE));
            String tit = cursor.getString(
                    cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_TIME_TAKE));
            String dab = cursor.getString(
                    cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_DATE_BACK));
            String tib = cursor.getString(
                    cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_TIME_BACK));


            //Log.d("entry", matr+" | "+name+" | "+nameKey+" | "+dat+" | "+tit+" | "+dab+" | "+tib);
            Entry entry = new Entry(matr, name, nameKey, dat, tit, dab, tib);
            entries.add(entry);

        }

        cursor.close();

        return entries;
    }*/
}
