package com.luiz.mg.magickey.utils;

import android.os.Environment;
import android.util.Log;

import com.luiz.mg.magickey.models.User;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class ReadFile {


    public static ArrayList<User> getListUsersOfFile () {
        ArrayList<User> list = new ArrayList<>();

        String arquivoCSV = "/Terceirizados2.csv";

        File dir = new File(
                Environment.getExternalStorageDirectory() + Utils.DIRECTORY_REPORTS + arquivoCSV);

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
}
