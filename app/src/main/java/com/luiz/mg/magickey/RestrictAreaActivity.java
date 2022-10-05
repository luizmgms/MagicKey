package com.luiz.mg.magickey;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
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

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.luiz.mg.magickey.adapters.FirestoreRecyclerAdapterForKey;
import com.luiz.mg.magickey.adapters.FirestoreRecyclerAdapterForUser;
import com.luiz.mg.magickey.adapters.KeyAdapter;
import com.luiz.mg.magickey.adapters.UserAdapter;
import com.luiz.mg.magickey.dao.KeyDAO;
import com.luiz.mg.magickey.dao.UserDAO;
import com.luiz.mg.magickey.models.Key;
import com.luiz.mg.magickey.models.User;
import com.luiz.mg.magickey.utils.DialogButtonClickWrapper;
import com.luiz.mg.magickey.utils.PathUtil;
import com.luiz.mg.magickey.utils.ReadFile;
import com.luiz.mg.magickey.utils.Utils;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RestrictAreaActivity extends AppCompatActivity implements View.OnClickListener,
        AdapterView.OnItemSelectedListener {

    private TextView titleList;
    private RecyclerView rViewOfListUser, rViewOfListKey;
    private boolean isUsers = true;
    private boolean isLotUsers = true;
    private String dept = Utils.sector;

    FirebaseFirestore db;
    FirestoreRecyclerAdapterForUser adapterUser;
    FirestoreRecyclerAdapterForKey adapterKey;

    ActivityResultLauncher<String> mGetContent =
        registerForActivityResult(new ActivityResultContracts.GetContent(),

            uri -> {

                String path="";
                try {
                    path = PathUtil.getPath(this, uri);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }

                if (path != null && path.endsWith(".csv")) {

                    if (isLotUsers) {
                        //Toast.makeText(this, path, Toast.LENGTH_SHORT).show();
                        //addUsersLot(path);
                    }
                    else {
                        //addKeysLot(path);
                    }

                } else {
                    Toast.makeText(this, "Arquivo Inválido! Escolha um arquivo .csv", Toast.LENGTH_LONG).show();
                }

            }

        );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resctrict_area);

        //Banco de Dados
        db = FirebaseFirestore.getInstance();

        //Título da Lista
        titleList = findViewById(R.id.titleListId);

        Button btnAddUser = findViewById(R.id.btnAddUserId);
        Button btnAddKey = findViewById(R.id.btnAddKeyId);
        Button btnListUsers = findViewById(R.id.btnListUsersId);
        Button btnListKeys = findViewById(R.id.btnListKeysId);
        Button btnAddUsersLot = findViewById(R.id.btnAddUsersLotId);
        Button btnAddKeysLot = findViewById(R.id.btnAddKeysLotId);

        titleList.setText(R.string.title_list_user);

        //SetListUser
        setViewListUser();

        //setListKey
        setViewListKey();

        btnAddUser.setOnClickListener(this);
        btnListUsers.setOnClickListener(this);
        btnListKeys.setOnClickListener(this);
        btnAddKey.setOnClickListener(this);
        btnAddUsersLot.setOnClickListener(this);
        btnAddKeysLot.setOnClickListener(this);

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



        } else {


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
                titleList.setText(R.string.title_list_user);
                showListUsers();
                break;
            case R.id.btnListKeysId:
                isUsers = false;
                titleList.setText(R.string.title_list_key);
                showListKeys();
                break;
            case R.id.btnAddUsersLotId:
                isLotUsers = true;
                if (requestPermission())
                    showDialogAddUsersLot();
                break;
            case R.id.btnAddKeysLotId:
                isLotUsers = false;
                if (requestPermission())
                    showDialogAddKeysLot();
                break;
        }
    }

    private void showListUsers() {
        rViewOfListKey.setVisibility(View.INVISIBLE);
        rViewOfListUser.setVisibility(View.VISIBLE);
    }

    private void showListKeys() {
        rViewOfListUser.setVisibility(View.INVISIBLE);
        rViewOfListKey.setVisibility(View.VISIBLE);
    }

    private void setViewListUser() {

        //Lista de Usuários
        rViewOfListUser = findViewById(R.id.listUserId);
        rViewOfListUser.setHasFixedSize(true);
        rViewOfListUser.setLayoutManager(new LinearLayoutManager(this));
        rViewOfListUser.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));

        Query query = db.collection("users");

        //FirebaseRecyclerOptions
        FirestoreRecyclerOptions<User> optionsUsers = new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(query, User.class)
                .build();

        //FirestoreRecyclerAdapter para Usuário
        adapterUser = new FirestoreRecyclerAdapterForUser(optionsUsers);

        rViewOfListUser.setAdapter(adapterUser);

    }

    private void setViewListKey() {

        //Lista de Chaves
        rViewOfListKey = findViewById(R.id.listKeyId);
        rViewOfListKey.setHasFixedSize(true);
        rViewOfListKey.setLayoutManager(new LinearLayoutManager(this));
        rViewOfListKey.addItemDecoration( new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));

        Query query = db.collection("keys");

        //FirebaseRecyclerOptions
        FirestoreRecyclerOptions<Key> optionsKey = new FirestoreRecyclerOptions.Builder<Key>()
                .setQuery(query, Key.class)
                .build();

        //FirestoreRecyclerAdapter para Chave
        adapterKey = new FirestoreRecyclerAdapterForKey(optionsKey);

        rViewOfListKey.setAdapter(adapterKey);


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
                return addNewUser(itMatUser, itNameUser, spinner, dialog);//Retornando true fecha o dialog
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
                return addNewKey(itNameKey, dialog);//Retornando true fecha o dialog
            }
        });
    }

    private void consultUser (User user, AlertDialog dialog) {

        db.collection("users")
                .whereEqualTo("mat", user.getMat())
                .get()
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        if (task.getResult().isEmpty()) {

                            addUserInDatabase(user, dialog);

                        } else {

                            Toast.makeText(this, R.string.user_exists, Toast.LENGTH_SHORT)
                                    .show();
                        }

                    } else {

                        Toast.makeText(this, "Falha!", Toast.LENGTH_SHORT).show();

                    }
                });

    }

    private void consultKey (Key key, AlertDialog dialog) {

        db.collection("keys")
                .whereEqualTo("name", key.getName())
                .get()
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        if (task.getResult().isEmpty()) {

                            addKeyInDatabase(key, dialog);

                        } else {

                            Toast.makeText(this, R.string.key_exists, Toast.LENGTH_SHORT)
                                    .show();

                        }

                    } else {

                        Toast.makeText(this, "Falha!", Toast.LENGTH_SHORT).show();

                    }


                });
    }

    private void addKeyInDatabase(Key key, AlertDialog dialog) {

        db.collection("keys").add(key)
            .addOnSuccessListener(documentReference -> {

                dialog.dismiss();
                Toast.makeText(this, R.string.add_key_succes, Toast.LENGTH_SHORT).show();

            }).addOnFailureListener(e -> Toast.makeText(this, R.string.add_key_erro,
                    Toast.LENGTH_SHORT).show());

    }

    private void addUserInDatabase(User user, AlertDialog dialog) {

        db.collection("users").add(user)
            .addOnSuccessListener(documentReference -> {

                dialog.dismiss();
                Toast.makeText(this, Utils.ADD_USER_SUCCESS, Toast.LENGTH_SHORT).show();

            }).addOnFailureListener(e -> Toast.makeText(this, Utils.ADD_USER_FAIL,
                    Toast.LENGTH_SHORT).show());
    }

    @SuppressLint("NotifyDataSetChanged")
    private boolean addNewUser(TextInputEditText mat, TextInputEditText name, Spinner spinner, AlertDialog dialog) {

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

            User newUser = new User(mat.getText().toString(), name.getText().toString(), dept);

            consultUser(newUser, dialog);

            return false;

        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private boolean addNewKey(TextInputEditText nameKey, AlertDialog dialog) {

        if (Objects.requireNonNull(nameKey.getText()).toString().equals("")) {

            nameKey.setError(getResources().getString(R.string.name_key_require));

            return false;

        } else if (dept.equals(Utils.sector)) {

            Toast.makeText(getApplicationContext(),
                    R.string.dept_require, Toast.LENGTH_LONG).show();

            return false;

        } else {

            Key key = new Key(nameKey.getText().toString(), dept,false);
            consultKey(key, dialog);

            return false;

        }
    }

    @SuppressLint("InflateParams")
    private void showDialogAddUsersLot() {

        AlertDialog dialog;
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View inflate;

        //Definição do Layout
        inflate = inflater.inflate(R.layout.layout_add_users_lot, null);

        //SetView no dialog
        builder.setView(inflate);

        //Icon
        builder.setIcon(R.drawable.ic_baseline_attach_file_24);

        //Title
        builder.setTitle(R.string.pick_file);

        //Message
        builder.setMessage(Utils.PICK_FILE_CSV);

        //Set PositiveButton
        builder.setPositiveButton(R.string.pick_file, (dialogInterface, i) ->
                mGetContent.launch("*/*"));

        //Set NegativeButton
        builder.setNegativeButton(R.string.cancel, (dialogInterface, i) -> {});

        //Criar
        dialog = builder.create();

        //Mostrar Dialog
        dialog.show();

    }

    /*private void addUsersLot(String path) {

        ArrayList<User> listNewUsers = ReadFile.getListUsersOfFile(path);

        if (!listNewUsers.isEmpty()) {

            UserDAO userDAO = new UserDAO(MainActivity.dbHelper);
            int numOfNewRows = userDAO.addUsersOfList(listNewUsers);

            if (numOfNewRows > 0) {
                listUsers.addAll(listNewUsers);
                setListUser(listUsers);
            }

            Toast.makeText(this, numOfNewRows + " usuário(s) adicionado(s)!",
                    Toast.LENGTH_SHORT).show();

        } else {

            Toast.makeText(this, "Lista vazia!", Toast.LENGTH_SHORT).show();

        }
    }

    private void addKeysLot(String path) {

        ArrayList<Key> listNewKeys = ReadFile.getListOfKeysOfFile(path);

        if (!listNewKeys.isEmpty()) {

            KeyDAO keyDAO = new KeyDAO(MainActivity.dbHelper);
            int numOfNewRows = keyDAO.addKeysOfList(listNewKeys);

            if (numOfNewRows > 0) {
                listKeys.addAll(listNewKeys);
                setListKey(listKeys);
            }

            Toast.makeText(this, numOfNewRows + " chave(s) adicionada(s)!",
                    Toast.LENGTH_SHORT).show();

        } else {

            Toast.makeText(this, "Lista vazia!", Toast.LENGTH_SHORT).show();

        }


    }*/

    @SuppressLint("InflateParams")
    private void showDialogAddKeysLot() {

        AlertDialog dialog;
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View inflate;

        //Definição do Layout
        inflate = inflater.inflate(R.layout.layout_add_keys_lot, null);

        //SetView no dialog
        builder.setView(inflate);

        //Icon
        builder.setIcon(R.drawable.ic_baseline_attach_file_24);

        //Title
        builder.setTitle(R.string.pick_file);

        //Message
        builder.setMessage(Utils.PICK_FILE_CSV);

        //Set PositiveButton
        builder.setPositiveButton(R.string.pick_file, (dialogInterface, i) ->
                mGetContent.launch("*/*"));

        //Set NegativeButton
        builder.setNegativeButton(R.string.cancel, (dialogInterface, i) -> {});

        //Criar
        dialog = builder.create();

        //Mostrar Dialog
        dialog.show();

    }

    private boolean requestPermission() {
        // Verifica  o estado da permissão de WRITE_EXTERNAL_STORAGE
        int permissionCheck = ContextCompat
                .checkSelfPermission(
                        this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            // Se for diferente de PERMISSION_GRANTED, então vamos exibir a tela padrão
            ActivityCompat
                    .requestPermissions(
                            this, new String[]{
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            1);
            return false;

        } else {

            return true;
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            if (isLotUsers)
                showDialogAddUsersLot();
            else
                showDialogAddKeysLot();

        } else {

            finish();

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapterUser.startListening();
        adapterKey.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapterUser.stopListening();
        adapterKey.stopListening();
    }
}