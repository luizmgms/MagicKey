package com.luiz.mg.magickey.utils;

import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.luiz.mg.magickey.models.Key;
import com.luiz.mg.magickey.models.User;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class ReadFile {

    public static ArrayList<User> getListUsersOfFile (String path) {

        ArrayList<User> list = new ArrayList<>();

        File dir = new File(path);

        BufferedReader bufferedReader = null;
        String line = "";
        String div = ",";

        try {

            bufferedReader = new BufferedReader(new FileReader(dir));
            while ((line = bufferedReader.readLine()) != null) {

                String[] user = line.split(div);

                User user1 = new User(user[0], user[1], user[2]);

                list.add(user1);

            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return list;
    }

    public static ArrayList<Key> getListOfKeysOfFile(String path) {

        ArrayList<Key> list = new ArrayList<>();

        File dir = new File(path);

        BufferedReader bufferedReader = null;
        String line = "";
        String div = ",";

        try {

            bufferedReader = new BufferedReader(new FileReader(dir));
            while ((line = bufferedReader.readLine()) != null) {

                String[] key = line.split(div);

                Key k = new Key(key[0], key[1], key[2]);

                list.add(k);

            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return list;
    }
}
