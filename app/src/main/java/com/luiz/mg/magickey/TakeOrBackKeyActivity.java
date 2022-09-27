package com.luiz.mg.magickey;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.luiz.mg.magickey.adapters.KeyAdapter;
import com.luiz.mg.magickey.dao.EntryDAO;
import com.luiz.mg.magickey.dao.KeyDAO;
import com.luiz.mg.magickey.models.Entry;
import com.luiz.mg.magickey.models.Key;
import com.luiz.mg.magickey.models.User;
import com.luiz.mg.magickey.utils.Utils;

import java.util.ArrayList;
import java.util.Objects;

public class TakeOrBackKeyActivity extends AppCompatActivity {

    @SuppressWarnings("rawtypes")
    private BottomSheetBehavior bottomSheetBehavior;
    User user;
    KeyDAO keyDAO;
    EntryDAO entryDAO;
    ArrayList<Entry> listEntryOfUser;
    ArrayList<Key> listAllKeys;
    ArrayList<Key> listKeysOfDeptOfUser;
    KeyAdapter keyAdapter;
    RecyclerView recyclerViewListOfKeysOfUser;
    RecyclerView recyclerViewAllKeys;
    TextView tvListEmptyId;
    SwitchCompat switchSector;
    String sDigited = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_or_back_key);

        /* Views */
        //TextView Nome do usuário logado
        TextView nameOfUser = findViewById(R.id.nameOfUserId);
        //TextView se Lista de chaves vazia
        tvListEmptyId = findViewById(R.id.tvListEmptyId);
        //Recycler View da lista das chaves que usuário pegou emprestado
        recyclerViewListOfKeysOfUser = findViewById(R.id.reclyclerListOfKeysOfUserId);
        //RecyclerView de todas as Chaves
        recyclerViewAllKeys = findViewById(R.id.listOthersKeysId);
        //Layout do BottomSheet Outras Chaves
        ConstraintLayout layoutBottomSheet = findViewById(R.id.layoutBottomSheetYoursKeysId);
        //Texto Bottom Pegar/Devolver Outra Chave
        TextView titleOthersKeys = findViewById(R.id.textOtherKeyId);
        //Pesquisa
        SearchView etSearch = findViewById(R.id.etSearchKeyId);

        //Pegando informações do usuário logado
        Intent intent = getIntent();
        String mat = intent.getExtras().getString(Utils.MAT_USER);
        String name = intent.getExtras().getString(Utils.NAME_USER);
        String dept = intent.getExtras().getString(Utils.DEPT_USER);

        //Criando objeto usuário
        user = new User(mat, name, dept);
        String[] splitName = name.split(" ");
        //Setendo View com nome do usuário
        nameOfUser.setText(splitName[0]);

        //DAOs
        entryDAO = new EntryDAO(MainActivity.dbHelper);
        keyDAO = new KeyDAO(MainActivity.dbHelper);

        //Popular Lista com todas as chaves
        listAllKeys = keyDAO.listKeys();
        //Popular Lista de chaves do setor do usuário
        listKeysOfDeptOfUser = getListKeysOfSetorOfUser(user.getDept(), listAllKeys);
        //Popular lista de Entry do Usuário
        listEntryOfUser = entryDAO.listEntriesOfUser(user.getMat());

        Log.d("appkey", "---------------------------------------------------------------------------");
        Log.d("appkey", "Mat: "+user.getMat()+", Nome: "+user.getName()+", Setor: "+user.getDept());

        for (Entry e: listEntryOfUser){
            Log.d("appkey", "Lista Entry -  Mat: "+e.getMatUserTakeKey()+", "+e.getDateTakeKey()+" "+e.getTimeTakeKey()+", "+e.getDateBackKey());
        }

        /* RecyclerViews */
        //Setando RecyclerView da lista da chaves que o usuário pegou e não devolveu
        recyclerViewListOfKeysOfUser.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerViewListOfKeysOfUser.addItemDecoration(
                new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));
        //Adapter
        keyAdapter = new KeyAdapter(
                getListKeysOfUser(listEntryOfUser), user, true, getApplicationContext(), MainActivity.dbHelper);
        recyclerViewListOfKeysOfUser.setAdapter(keyAdapter);
        recyclerViewListOfKeysOfUser.setHasFixedSize(true);

        //Setando RecyclerView da lista de todas as chaves
        recyclerViewAllKeys.setLayoutManager( new LinearLayoutManager(getApplicationContext()));
        recyclerViewAllKeys.addItemDecoration(
                new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));
        keyAdapter = new KeyAdapter(
                listAllKeys, user, false, getApplicationContext(), MainActivity.dbHelper);
        recyclerViewAllKeys.setAdapter(keyAdapter);
        recyclerViewAllKeys.setHasFixedSize(true);


        /* BottomSheet */
        //Comportamento do BottomSheet Outras Chaves
        bottomSheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);
        //Callback de chamado do bottomSheet
        bottomSheetBehavior.addBottomSheetCallback(callbackSheetBehavior());


        /* Eventos */
        //Evento ao clicar no texto OUTRA CHAVE
        titleOthersKeys.setOnClickListener(view -> {
            //Expandir Bottom Sheet Todas as Chaves
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        });

        //Switch Filtro chave por setor
        switchSector = findViewById(R.id.switchFilterId);
        switchSector.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                etSearch.clearFocus();
                setListKeysOfSetorOfUser(listKeysOfDeptOfUser);
            } else {
                if (!sDigited.equals("")) {
                    setListAllKeys(getListKeysSearch(listAllKeys));
                } else {
                    setListAllKeys(listAllKeys);
                }
            }

        });

        //TextView da Pesquisa
        etSearch.setOnQueryTextListener(listenerSearch());

    }

    private ArrayList<Key> getListKeysOfUser(ArrayList<Entry> listEntry) {

        ArrayList<Key> listKey = new ArrayList<>();

        for(Entry e : listEntry) {
            String deptKey = consultDeptOfKey(e.getNameKey());
            Key k = new Key(e.getNameKey(), deptKey, Utils.YES_KEY);
            listKey.add(k);
        }

        return listKey;
    }

    private String consultDeptOfKey(String nameKey) {
        Key k = keyDAO.consultKey(nameKey);
        if (k == null)
            return "";
        else
            return k.getDeptKey();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void setListKeysOfSetorOfUser(ArrayList<Key> listKeys) {
        recyclerViewAllKeys.setAdapter(
                new KeyAdapter(
                        listKeys, user, false, getApplicationContext(), MainActivity.dbHelper
                )
        );
        Objects.requireNonNull(recyclerViewAllKeys.getAdapter()).notifyDataSetChanged();
    }

    //Lista de todas as chaves
    @SuppressLint("NotifyDataSetChanged")
    private void setListAllKeys(ArrayList<Key> list) {
        recyclerViewAllKeys.setAdapter(
                new KeyAdapter(
                        list, user, false, getApplicationContext(), MainActivity.dbHelper
                )
        );
        Objects.requireNonNull(recyclerViewAllKeys.getAdapter()).notifyDataSetChanged();
    }

    //Setar Lista de chaves que estão sobre posse do usuário
    @SuppressLint("NotifyDataSetChanged")
    private void setListKeysOfUser(ArrayList<Key> list) {

        recyclerViewListOfKeysOfUser.setAdapter(
                new KeyAdapter(
                        list, user, true, getApplicationContext(), MainActivity.dbHelper
                )
        );

        Objects.requireNonNull(recyclerViewListOfKeysOfUser.getAdapter()).notifyDataSetChanged();

    }

    //Listener da Pesquisa
    private SearchView.OnQueryTextListener listenerSearch() {
        return (
            new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {

                    sDigited = newText;
                    ArrayList<Key> listSearch = new ArrayList<>();

                    for (Key k: listAllKeys) {
                        if (k.getNameKey().toLowerCase().contains(sDigited.toLowerCase())) {
                            listSearch.add(k);
                        }
                    }

                    setListAllKeys(listSearch);
                    return false;
                }
            }
        );
    }

    //Setar Lista de chaves por setor do usuário que não estão emprestadas
    private ArrayList<Key> getListKeysOfSetorOfUser(String dept, ArrayList<Key> listKeys) {
        ArrayList<Key> list = new ArrayList<>();

        for (Key k: listKeys) {
            if (k.getDeptKey().equals(dept))
                list.add(k);
        }

        return list;
    }

    //Callback de chamada do bottomSheet
    private BottomSheetBehavior.BottomSheetCallback callbackSheetBehavior() {
        return (new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

                //Se minimizou bottomSheet, ...
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {

                    // ... Set Lista das chaves que pegou

                    listEntryOfUser.clear();
                    listEntryOfUser = entryDAO.listEntriesOfUser(user.getMat());
                    setListKeysOfUser(getListKeysOfUser(listEntryOfUser));

                    Log.d("appkeys", "BottomSheet TODAS AS CHAVES - COLLAPSED");

                    // ... Senão, Se expandiu bottom, ...
                } else if (newState == BottomSheetBehavior.STATE_EXPANDED) {

                    // ... Se filtro do setor ativo, ...
                    if (switchSector.isChecked()) {

                        //... Set lista com as chaves do setor do usuário
                        listKeysOfDeptOfUser.clear();
                        listKeysOfDeptOfUser = keyDAO.listKeys(user.getDept());
                        setListKeysOfSetorOfUser(listKeysOfDeptOfUser);

                    } else { // ... Senão, ...

                        //Se String de pesquisa é diferente de vazia, ...
                        if (!sDigited.isEmpty()) {

                            //Set lista com chaves pesquisada
                            listAllKeys.clear();
                            listAllKeys = keyDAO.listKeys();
                            setListAllKeys(getListKeysSearch(listAllKeys));

                        } else { //... Senão, ...

                            // ... Set lista com todas as chaves
                            listAllKeys.clear();
                            listAllKeys = keyDAO.listKeys();
                            setListAllKeys(listAllKeys);

                        }
                    }
                    Log.d("appkeys", "BottomSheet TODAS AS CHAVES - EXPANDED");
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
    }

    private ArrayList<Key> getListKeysSearch(ArrayList<Key> list) {
        ArrayList<Key> listSearch = new ArrayList<>();

        for (Key k: list) {
            if (k.getNameKey().toLowerCase().contains(sDigited.toLowerCase())) {
                listSearch.add(k);
            }
        }

        return listSearch;
    }

    @Override
    public void onBackPressed() {
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED)
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        else {
            super.onBackPressed();
            finish();
        }
    }

}