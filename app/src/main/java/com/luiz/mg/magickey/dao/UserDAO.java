package com.luiz.mg.magickey.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.luiz.mg.magickey.db.FeedReaderContract;
import com.luiz.mg.magickey.db.FeedReaderDbHelper;
import com.luiz.mg.magickey.models.User;

import java.util.ArrayList;

public class UserDAO {

    private final FeedReaderDbHelper dbHelper;

    public UserDAO(FeedReaderDbHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    //Adicionar Usuário
    public int addUser (User user) {
        
        //Se o usuário não existe
        if (consultUser(user.getMat()) == null) {
            
            // Gets the data repository in write mode
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            // Create a new map of values, where column names are the keys
            ContentValues values = new ContentValues();
            values.put(FeedReaderContract.FeedUsers.COLUMN_NAME_MAT, user.getMat());
            values.put(FeedReaderContract.FeedUsers.COLUMN_NAME_NAME, user.getName());
            values.put(FeedReaderContract.FeedUsers.COLUMN_NAME_DEPT, user.getDept());

            // Insert the new row, returning the primary key value of the new row
            long newRowId = db.insert(FeedReaderContract.FeedUsers.TABLE_NAME,
                    null, values);

            if (newRowId == -1) {

                return -1; //Erro ao adicionar o usúario

            } else {

                return 1; //Usuário cadastrado com sucesso
            }
            
        } else {
            return 0; //Usuário já existe
        }

    }

    //Consultar Usuário
    public User consultUser(String mat) {
        User user = null;

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Filter results WHERE "title" = 'My Title'
        String selection = FeedReaderContract.FeedUsers.COLUMN_NAME_MAT + " = ?";
        String[] selectionArgs = { mat };

        Cursor cursor = db.query(
                FeedReaderContract.FeedUsers.TABLE_NAME,   // The table to query
                null,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null               // The sort order
        );


        while(cursor.moveToNext()) {

            String matr = cursor.getString(
                    cursor.getColumnIndexOrThrow(FeedReaderContract.FeedUsers.COLUMN_NAME_MAT));
            String name = cursor.getString(
                    cursor.getColumnIndexOrThrow(FeedReaderContract.FeedUsers.COLUMN_NAME_NAME));
            String dept = cursor.getString(
                    cursor.getColumnIndexOrThrow(FeedReaderContract.FeedUsers.COLUMN_NAME_DEPT));

            user = new User(matr, name, dept);

        }

        cursor.close();

        return user;
    }


    //Deletar Usuário
    public int deleteUser(String mat) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String selection = FeedReaderContract.FeedUsers.COLUMN_NAME_MAT + " = ?";
        String[] selectionArgs = { mat };

        return db.delete(FeedReaderContract.FeedUsers.TABLE_NAME, selection, selectionArgs);
    }

    //Listar todos os Usuários
    public ArrayList<User> listUsers() {

        ArrayList<User> users = new ArrayList<>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        /*Cursor cursor = db.rawQuery(
                ctx.getResources().getString(R.string.query_list_users), null);*/


        // How you want the results sorted in the resulting Cursor
        String sortOrder =
                FeedReaderContract.FeedUsers.COLUMN_NAME_NAME + " ASC";

        Cursor cursor = db.query(
                FeedReaderContract.FeedUsers.TABLE_NAME,   // The table to query
                null,             // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                sortOrder               // The sort order
        );

        while(cursor.moveToNext()) {

            String matr = cursor.getString(
                    cursor.getColumnIndexOrThrow(FeedReaderContract.FeedUsers.COLUMN_NAME_MAT));
            String name = cursor.getString(
                    cursor.getColumnIndexOrThrow(FeedReaderContract.FeedUsers.COLUMN_NAME_NAME));
            String dept = cursor.getString(
                    cursor.getColumnIndexOrThrow(FeedReaderContract.FeedUsers.COLUMN_NAME_DEPT));

            User user = new User(matr, name, dept);
            users.add(user);

        }

        cursor.close();

        return users;

    }

    //Adicionar Usuários de uma Lista
    public int addUsersOfList (ArrayList<User> list) {

        int i = 0;

        if (!list.isEmpty()) {
            for (User user: list) {
                int status = addUser(user);
                if (status == 0) {
                    Log.d("appkey", "Adicionar Usuário " + user.getMat() +
                            ": Usuário já existe!");
                } else if (status == 1) {
                    Log.d("appkey", "Adicionar Usuário " + user.getMat() +
                            ": Sucesso!");
                    i++;
                } else {
                    Log.d("appkey", "Adicionar Usuário " + user.getMat() +
                            ": Erro Desconhecido!");
                }
            }
        }

        return i;

    }

    //Deletar Todos os Usuários
    public int deleteAllUsers() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        return db.delete(FeedReaderContract.FeedUsers.TABLE_NAME, null, null);
    }

}
