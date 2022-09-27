package com.luiz.mg.magickey;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.luiz.mg.magickey.adapters.KeyAdapter;
import com.luiz.mg.magickey.adapters.UserAdapter;
import com.luiz.mg.magickey.dao.KeyDAO;
import com.luiz.mg.magickey.dao.UserDAO;
import com.luiz.mg.magickey.models.Key;
import com.luiz.mg.magickey.models.User;
import com.luiz.mg.magickey.utils.DialogButtonClickWrapper;
import com.luiz.mg.magickey.utils.Utils;

import java.util.ArrayList;
import java.util.Objects;

public class RestrictAreaActivity extends AppCompatActivity implements View.OnClickListener,
        AdapterView.OnItemSelectedListener {

    private TextView titleList;
    private RecyclerView recyclerViewOfList;
    private ArrayList<User> listUsers;
    private ArrayList<Key> listKeys;
    private boolean isUsers = true;
    private String dept = Utils.sector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resctrict_area);

        //Lista de Usuários
        UserDAO userDAO = new UserDAO(getApplicationContext());
        listUsers =  userDAO.listUsers();

        //Lista de Chaves
        KeyDAO keyDAO = new KeyDAO(MainActivity.dbHelper);
        listKeys = keyDAO.listKeys();

        Button btnAddUser = findViewById(R.id.btnAddUserId);
        Button btnAddKey = findViewById(R.id.btnAddKeyId);
        Button btnListUsers = findViewById(R.id.btnListUsersId);
        Button btnListKeys = findViewById(R.id.btnListKeysId);

        titleList = findViewById(R.id.titleListId);

        recyclerViewOfList = findViewById(R.id.listItemId);
        recyclerViewOfList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerViewOfList.addItemDecoration(
                new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));

        setListUser(listUsers);

        btnAddUser.setOnClickListener(this);
        btnListUsers.setOnClickListener(this);
        btnListKeys.setOnClickListener(this);
        btnAddKey.setOnClickListener(this);

        SearchView etSearch = findViewById(R.id.editTextSearchId);
        etSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                updateSearchList(newText);

                return false;
            }
        });
    }

    private void updateSearchList(String newText) {

        if (isUsers) {

            ArrayList<User> listSearch = new ArrayList<>();
            for (User u : listUsers) {
                if (u.getName().toLowerCase().contains(newText.toLowerCase())) {
                    listSearch.add(u);
                }
            }
            setListUser(listSearch);

        } else {

            ArrayList<Key> listSearch = new ArrayList<>();
            for (Key k : listKeys) {
                if (k.getNameKey().toLowerCase().contains(newText.toLowerCase())) {
                    listSearch.add(k);
                }
            }
            setListKey(listSearch);
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {

        int id = view.getId();

        switch (id){
            case R.id.btnAddUserId:
                showDialogAddUser();
                break;
            case R.id.btnAddKeyId:
                showDialogAddKey();
                break;
            case R.id.btnListUsersId:
                isUsers = true;
                setListUser(listUsers);
                break;
            case R.id.btnListKeysId:
                isUsers = false;
                setListKey(listKeys);
                break;
        }
    }

    private void setListUser(ArrayList<User> list) {
        UserAdapter userAdapter = new UserAdapter(list);
        recyclerViewOfList.setAdapter(userAdapter);
        recyclerViewOfList.setHasFixedSize(true);
        titleList.setText(R.string.title_list_user);
    }

    private void setListKey(ArrayList<Key> list) {
        KeyAdapter keyAdapter = new KeyAdapter(
                list, null , false, getApplicationContext(), MainActivity.dbHelper);
        recyclerViewOfList.setAdapter(keyAdapter);
        recyclerViewOfList.setHasFixedSize(true);
        titleList.setText(R.string.title_list_key);
    }

    @SuppressLint("InflateParams")
    private void showDialogAddUser(){

        AlertDialog dialog;
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View inflate;

        //Definição do Layout
        inflate = inflater.inflate(R.layout.layout_add_user, null);

        //Definição dos Campos
        final TextInputEditText itMatUser = inflate.findViewById(R.id.matUserId);
        final TextInputEditText itNameUser = inflate.findViewById(R.id.nameUserId);

        //spinner
        final Spinner spinner = inflate.findViewById(R.id.deptUserId);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.depts_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        //Spinner Item selected
        spinner.setOnItemSelectedListener(this);

        //SetView no dialog
        builder.setView(inflate);

        //Botão Cancelar
        builder.setNegativeButton(Utils.CANCEL, (dialogInterface, i) -> {
            //Cancelar
        });

        //Não Cancelável
        builder.setCancelable(false);

        //Criar
        dialog = builder.create();

        //Set Botão cadastrar (Não colocar nada)
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, Utils.CAD, (dialog1, which) -> {});

        //Mostrar Dialog
        dialog.show();

        //Customizando botão cadastrar
        Button theButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        theButton.setOnClickListener(new DialogButtonClickWrapper(dialog) {
            @Override
            protected boolean onClicked() {
                return addNewUser(itMatUser, itNameUser, spinner);//Retornando true fecha o dialog
            }
        });

    }

    @SuppressLint("InflateParams")
    private void showDialogAddKey(){

        AlertDialog dialog;
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View inflate;

        //Definição do Layout
        inflate = inflater.inflate(R.layout.layout_add_key, null);

        //Definição dos Campos
        final TextInputEditText itNameKey = inflate.findViewById(R.id.nameKeyId);

        //spinner
        final Spinner spinner = inflate.findViewById(R.id.deptKeyId);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.depts_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        //Spinner Item selected
        spinner.setOnItemSelectedListener(this);

        //SetView no dialog
        builder.setView(inflate);

        //Botão Cancelar
        builder.setNegativeButton(Utils.CANCEL, (dialogInterface, i) -> {
            //Cancelar
        });

        //Não Cancelável
        builder.setCancelable(false);

        //Criar
        dialog = builder.create();

        //Set Botão cadastrar (Não colocar nada)
        dialog.setButton(
                DialogInterface.BUTTON_POSITIVE, Utils.CAD, (dialog1, which) -> {});

        //Mostrar Dialog
        dialog.show();

        //Customizando botão cadastrar
        Button theButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        theButton.setOnClickListener(new DialogButtonClickWrapper(dialog) {
            @Override
            protected boolean onClicked() {
                return addNewKey(itNameKey, spinner);//Retornando true fecha o dialog
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private boolean addNewUser(TextInputEditText mat, TextInputEditText name, Spinner spinner) {

        if (Objects.requireNonNull(mat.getText()).toString().equals("")) {

            mat.setError(getResources().getString(R.string.mat_require));
            return false;

        } else if (Objects.requireNonNull(name.getText()).toString().equals("")) {

            name.setError(getResources().getString(R.string.name_require));
            return false;

        } else if (dept.equals(Utils.sector)) {

            Toast.makeText(getApplicationContext(),
                    R.string.dept_require, Toast.LENGTH_LONG).show();
            return false;

        } else { //Todos os campos foram preenchidos

            UserDAO userDAO  = new UserDAO(getApplicationContext());
            User newUser = new User(
                    mat.getText().toString(),
                    name.getText().toString(),
                    dept);

            int statusId = userDAO.addUser(newUser);

            if ( statusId == 1) { //Usuário adicionado com sucesso

                Log.d("appkey", Utils.ADD_USER_SUCCESS);

                Toast.makeText(getApplicationContext(),
                        R.string.add_user_succes, Toast.LENGTH_LONG).show();

                listUsers.add(newUser);
                Objects.requireNonNull(recyclerViewOfList.getAdapter()).notifyDataSetChanged();

                mat.setText("");
                name.setText("");
                spinner.setSelection(0);

                return true;

            } else if (statusId == -1 ) { //Erro ao adicionar usuário

                Log.d("appkey", Utils.ADD_USER_FAIL);

                Toast.makeText(getApplicationContext(),
                        R.string.add_user_fail, Toast.LENGTH_LONG).show();

                return false;

            } else { //Usuário já cadastrado

                Log.d("appkey", Utils.ADD_USER_EXISTS);

                Toast.makeText(getApplicationContext(),
                        R.string.add_user_erro, Toast.LENGTH_LONG).show();

                return false;
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private boolean addNewKey(TextInputEditText nameKey, Spinner spinner) {

        if (Objects.requireNonNull(nameKey.getText()).toString().equals("")) {

            nameKey.setError(getResources().getString(R.string.name_key_require));

            return false;

        } else if (dept.equals(Utils.sector)) {

            Toast.makeText(getApplicationContext(),
                    R.string.dept_require, Toast.LENGTH_LONG).show();

            return false;

        } else {

            //Todos os campos foram preenchidos
            KeyDAO keyDAO = new KeyDAO(MainActivity.dbHelper);

            Key newKey = new Key(
                    nameKey.getText().toString(),
                    dept,
                    getResources().getString(R.string.no)
            );

            int statusId = keyDAO.addKey(newKey);

            if ( statusId == 1) { //Chave adicionada com sucesso

                Log.d("appkey", Utils.ADD_KEY_SUCCESS);
                Toast.makeText(getApplicationContext(),
                        R.string.add_key_succes, Toast.LENGTH_LONG).show();

               listKeys.add(newKey);
               Objects.requireNonNull(recyclerViewOfList.getAdapter()).notifyDataSetChanged();

                nameKey.setText("");
                spinner.setSelection(0);

                return true;

            } else if (statusId == -1 ) { //Erro ao adicionar chave

                Log.d("appkey", Utils.ADD_KEY_FAIL);
                Toast.makeText(getApplicationContext(),
                        R.string.add_key_fail, Toast.LENGTH_LONG).show();

                return false;

            } else { //chave já cadastrado

                Log.d("appkey", Utils.ADD_KEY_EXISTS);
                Toast.makeText(getApplicationContext(),
                        R.string.add_key_erro, Toast.LENGTH_LONG).show();

                return false;
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        dept = adapterView.getItemAtPosition(i).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        dept = Utils.sector;
    }

}