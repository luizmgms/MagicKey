package com.luiz.mg.magickey;

import static com.luiz.mg.magickey.MainActivity.db;

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

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.firestore.Query;
import com.luiz.mg.magickey.adapters.FirestoreRecyclerAdapterForEntry;
import com.luiz.mg.magickey.adapters.FirestoreRecyclerAdapterForKey;
import com.luiz.mg.magickey.adapters.KeyAdapter;
import com.luiz.mg.magickey.dao.EntryDAO;
import com.luiz.mg.magickey.dao.KeyDAO;
import com.luiz.mg.magickey.db.FeedReaderDbHelper;
import com.luiz.mg.magickey.models.Entry;
import com.luiz.mg.magickey.models.Key;
import com.luiz.mg.magickey.models.User;
import com.luiz.mg.magickey.utils.LinearLayoutManagerWrapper;
import com.luiz.mg.magickey.utils.Utils;

import java.util.ArrayList;
import java.util.Objects;

public class TakeOrBackKeyActivity extends AppCompatActivity {

    @SuppressWarnings("rawtypes")
    private BottomSheetBehavior bottomSheetBehavior;
    User user;
    KeyDAO keyDAO;
    ArrayList<Entry> listEntryOfUser;
    ArrayList<Key> listAllKeys;
    FirestoreRecyclerAdapterForKey adapterAllKeys;
    FirestoreRecyclerAdapterForEntry adapterEntryUser;
    RecyclerView rViewListKeysUser;
    RecyclerView rViewAllKeys;
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
        rViewListKeysUser = findViewById(R.id.reclyclerListOfKeysOfUserId);

        //RecyclerView de todas as Chaves
        rViewAllKeys = findViewById(R.id.listOthersKeysId);

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

        //Popular Lista de chaves que o usuário pegou emprestado
        setViewListKeysUser(user);

        //Popular Lista com todas as chaves
        setViewListAllKeys();

        //Popular Lista de chaves(entry) que o usuário pegou
        //setViewListKeysUSerTaked(user);

        //Popular lista de Entry do Usuário
        listEntryOfUser = new ArrayList<>();

        Log.d("appkey", "---------------------------------------------------------------------------");
        Log.d("appkey", "Mat: "+user.getMat()+", Nome: "+user.getName()+", Setor: "+user.getDept());

        /*for (Entry e: listEntryOfUser){
            Log.d("appkey", "Lista Entry -  Mat: "+e.getMatUserTakeKey()+", "+e.getDateTakeKey()+" "+e.getTimeTakeKey()+", "+e.getDateBackKey());
        }*/

        /* RecyclerViews */
        //Setando RecyclerView da lista da chaves que o usuário pegou e não devolveu
        rViewListKeysUser.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        rViewListKeysUser.addItemDecoration(
                new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));
        //Adapter
        //keyAdapter = new KeyAdapter(
        //        getListKeysOfUser(listEntryOfUser), user, true, getApplicationContext(), MainActivity.dbHelper);
        //rViewListKeysUser.setAdapter(keyAdapter);
        rViewListKeysUser.setHasFixedSize(true);




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

            } else {
                if (!sDigited.equals("")) {
                    //setListAllKeys(getListKeysSearch(listAllKeys));
                } else {
                    //setListAllKeys(listAllKeys);
                }
            }

        });

        //TextView da Pesquisa
        etSearch.setOnQueryTextListener(listenerSearch());

    }

    private ArrayList<Key> getListKeysOfUser(ArrayList<Entry> listEntry) {

        ArrayList<Key> listKey = new ArrayList<>();

        /*for(Entry e : listEntry) {
            String deptKey = consultDeptOfKey(e.getName());
            Key k = new Key(e.getName(), deptKey, Utils.YES_KEY);
            listKey.add(k);
        }*/

        return listKey;
    }

    private String consultDeptOfKey(String nameKey) {
        Key k = keyDAO.consultKey(nameKey);
        if (k == null)
            return "";
        else
            return k.getDept();
    }

    private void setViewListKeysUser(User u) {

        rViewListKeysUser.setLayoutManager(new LinearLayoutManagerWrapper(this));
        rViewListKeysUser.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        rViewListKeysUser.setHasFixedSize(true);

        Query query = MainActivity.db.collection("entry")
                .whereEqualTo("matUserTakeKey", u.getMat())
                .whereEqualTo("matUserBackKey", "");

        FirestoreRecyclerOptions<Entry> optionsEntry =
                new FirestoreRecyclerOptions.Builder<Entry>().setQuery(query, Entry.class).build();

        adapterEntryUser = new FirestoreRecyclerAdapterForEntry(optionsEntry);

        rViewListKeysUser.setAdapter(adapterEntryUser);

    }

    //Lista de todas as chaves
    private void setViewListAllKeys() {

        rViewAllKeys.setLayoutManager(new LinearLayoutManagerWrapper(this));
        rViewAllKeys.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        rViewAllKeys.setHasFixedSize(true);

        Query query = MainActivity.db.collection("keys");

        //FirebaseRecyclerOptions
        FirestoreRecyclerOptions<Key> optionsKey = new FirestoreRecyclerOptions.Builder<Key>()
                .setQuery(query, Key.class)
                .build();

        //FirestoreRecyclerAdapter para Chave
        adapterAllKeys = new FirestoreRecyclerAdapterForKey(optionsKey, user);
        rViewAllKeys.setAdapter(adapterAllKeys);

    }

    //Setar Lista de chaves que estão sobre posse do usuário
    @SuppressLint("NotifyDataSetChanged")
    private void setViewListKeysUserTaked(ArrayList<Key> list) {

        /*rViewListKeysUser.setAdapter(
                new KeyAdapter(
                        list, user, true, getApplicationContext(), MainActivity.dbHelper
                )
        );*/

        Objects.requireNonNull(rViewListKeysUser.getAdapter()).notifyDataSetChanged();

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
                        if (k.getName().toLowerCase().contains(sDigited.toLowerCase())) {
                            listSearch.add(k);
                        }
                    }

                    //setListAllKeys(listSearch);
                    return false;
                }
            }
        );
    }

    //Setar Lista de chaves por setor do usuário que não estão emprestadas
    private ArrayList<Key> getListKeysOfSetorOfUser(String dept, ArrayList<Key> listKeys) {
        ArrayList<Key> list = new ArrayList<>();

        for (Key k: listKeys) {
            if (k.getDept().equals(dept))
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

                    //listEntryOfUser.clear();
                    //listEntryOfUser = entryDAO.listEntriesOfUser(user.getMat());
                    //setListKeysOfUser(getListKeysOfUser(listEntryOfUser));

                    Log.d("appkeys", "BottomSheet TODAS AS CHAVES - COLLAPSED");

                    // ... Senão, Se expandiu bottom, ...
                } else if (newState == BottomSheetBehavior.STATE_EXPANDED) {

                    // ... Se filtro do setor ativo, ...
                    if (switchSector.isChecked()) { /*

                        //... Set lista com as chaves do setor do usuário
                        listKeysOfDeptOfUser.clear();
                        listKeysOfDeptOfUser = keyDAO.listKeys(user.getDept());
                        setListKeysOfSetorOfUser(listKeysOfDeptOfUser);*/

                    } else { // ... Senão, ...

                        //Se String de pesquisa é diferente de vazia, ...
                        if (!sDigited.isEmpty()) {/*

                            //Set lista com chaves pesquisada
                            listAllKeys.clear();
                            listAllKeys = keyDAO.listKeys();
                            setListAllKeys(getListKeysSearch(listAllKeys));*/

                        } else { //... Senão, ...

                           /* // ... Set lista com todas as chaves
                            listAllKeys.clear();
                            listAllKeys = new ArrayList<>();
                            setListAllKeys(listAllKeys);*/

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
            if (k.getName().toLowerCase().contains(sDigited.toLowerCase())) {
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

    @Override
    protected void onStart() {
        super.onStart();
        adapterEntryUser.startListening();
        adapterAllKeys.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapterEntryUser.stopListening();
        adapterAllKeys.stopListening();
    }
}