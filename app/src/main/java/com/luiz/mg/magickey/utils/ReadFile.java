package com.luiz.mg.magickey.utils;

import com.luiz.mg.magickey.models.Key;
import com.luiz.mg.magickey.models.User;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Classe responsável por ler os arquivos CSV com a lista de usuários e chaves
 */
public class ReadFile {

    /**
     * Método responsável por ler um arquivo CSV com a lista dos usuários e retorná-los como um
     * ArrayList
     * @param path caminho do arquivo
     * @return ArrayList de Objetos de Usuários
     */
    public static ArrayList<User> getListUsersOfFile (String path) {

        ArrayList<User> list = new ArrayList<>();

        File dir = new File(path);

        BufferedReader bufferedReader = null;
        String line = "";
        String div = ",";

        try {

            bufferedReader = new BufferedReader(new FileReader(dir));
            while ((line = bufferedReader.readLine()) != null) {

                String[] field = line.split(div);

                User user = new User(field[0], field[1], field[2]);

                list.add(user);

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

    /**
     * Método responsável por ler um arquivo CSV com a lista das chaves e retorná-las como um
     * ArrayList
     * @param path caminho do arquivo
     * @return ArrayList de Objetos de Chaves
     */
    public static ArrayList<Key> getListOfKeysOfFile(String path) {

        ArrayList<Key> list = new ArrayList<>();

        File dir = new File(path);

        BufferedReader bufferedReader = null;
        String line = "";
        String div = ",";

        try {

            bufferedReader = new BufferedReader(new FileReader(dir));
            while ((line = bufferedReader.readLine()) != null) {

                String[] field = line.split(div);

                Key k = new Key(field[0], field[1], false, "", "");

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
