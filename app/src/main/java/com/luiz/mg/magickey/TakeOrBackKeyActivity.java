package com.luiz.mg.magickey;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.luiz.mg.magickey.adapters.FirestoreRecyclerAdapterForEntry;
import com.luiz.mg.magickey.adapters.FirestoreRecyclerAdapterForKey;
import com.luiz.mg.magickey.adapters.KeyAdapter;
import com.luiz.mg.magickey.models.Entry;
import com.luiz.mg.magickey.models.Key;
import com.luiz.mg.magickey.models.User;
import com.luiz.mg.magickey.utils.LinearLayoutManagerWrapper;
import com.luiz.mg.magickey.utils.Utils;

import java.util.ArrayList;

public class TakeOrBackKeyActivity extends AppCompatActivity {

    @SuppressWarnings("rawtypes")
    private BottomSheetBehavior bottomSheetBehaviorAllKeys;
    @SuppressWarnings("rawtypes")
    private BottomSheetBehavior bottomSheetBehaviorSearch;
    User user;

    FirestoreRecyclerAdapterForKey adapterAllKeys;
    FirestoreRecyclerAdapterForEntry adapterEntryUser;
    RecyclerView rViewListKeysUser;
    RecyclerView rViewAllKeys;
    RecyclerView rViewSearch;
    KeyAdapter keyAdapter;
    ArrayList<Key> listKeySearch = new ArrayList<>();
    TextView tvListEmptyId;
    SearchView etSearch;


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

        //RecyclerView de lista de chaves de procura
        rViewSearch = findViewById(R.id.rViewListSearchId);

        //Layout do BottomSheet Outras Chaves
        ConstraintLayout layoutBottomSheet = findViewById(R.id.layoutBottomSheetYoursKeysId);

        //Layout da lista de Procura
        ConstraintLayout layoutListSearch = findViewById(R.id.layoutListSearchId);

        //Texto Bottom Pegar/Devolver Outra Chave
        TextView titleOthersKeys = findViewById(R.id.textOtherKeyId);

        //Pesquisa
        etSearch = findViewById(R.id.etSearchKeyId);

        //Pegando informações do usuário logado
        Intent intent = getIntent();
        String mat = intent.getExtras().getString(Utils.MAT_USER);
        String name = intent.getExtras().getString(Utils.NAME_USER);
        String dept = intent.getExtras().getString(Utils.DEPT_USER);

        //Criando objeto usuário
        user = new User(mat, name, dept);

        //Setando View com nome do usuário
        String[] splitName = name.split(" ");
        String nameUser;

        if (splitName[1].length() <= 2)
            nameUser = splitName[0]+" "+splitName[1]+" "+splitName[2];
        else
            nameUser = splitName[0]+" "+splitName[1];

        nameOfUser.setText(nameUser);

        //Popular Lista de chaves que o usuário pegou emprestado
        setViewListEntryUser(user);

        //Popular Lista de todas as chaves
        setViewListAllKeys();

        /* BottomSheet */
        //Comportamento do BottomSheet Outras Chaves
        bottomSheetBehaviorAllKeys = BottomSheetBehavior.from(layoutBottomSheet);
        //Callback de chamado do bottomSheet
        //bottomSheetBehaviorAllKeys.addBottomSheetCallback(callbackSheetBehavior());

        //Comportamento do ListSearch
        bottomSheetBehaviorSearch = BottomSheetBehavior.from(layoutListSearch);
        bottomSheetBehaviorSearch.addBottomSheetCallback(callbackSheetBehavior());
        setHeightMaxOfView(rViewAllKeys, bottomSheetBehaviorSearch);

        /* Eventos */
        //Evento ao clicar no texto OUTRA CHAVE
        titleOthersKeys.setOnClickListener(view -> {
            //Expandir Bottom Sheet Todas as Chaves
            bottomSheetBehaviorAllKeys.setState(BottomSheetBehavior.STATE_EXPANDED);
        });

        rViewSearch.setLayoutManager(new LinearLayoutManagerWrapper(this));
        rViewSearch.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        rViewSearch.setHasFixedSize(true);


        //Ao abrir pesquisa, mostrar SearchList
        etSearch.setOnSearchClickListener(view -> {
            fillListKeySearch();
            bottomSheetBehaviorSearch.setState(BottomSheetBehavior.STATE_EXPANDED);
        });

        //Ao fechar Pesquisa, esconde SearchList
       etSearch.setOnCloseListener(() -> {
            bottomSheetBehaviorSearch.setState(BottomSheetBehavior.STATE_HIDDEN);
            return false;
        });

        //Ao digitar pesquisar
        etSearch.setOnQueryTextListener(listenerSearch());

        //Hide Teclado ao pressionar Pesquisar do teclado
        etSearch.setOnKeyListener((view, i, keyEvent) -> {
            if (i == KeyEvent.KEYCODE_ENTER)
                hideKeyboard(getApplicationContext(), view);
            return false;
        });

    }

    @SuppressLint("NotifyDataSetChanged")
    private void fillListKeySearch() {
        listKeySearch.clear();
        listKeySearch = new ArrayList<>();
        MainActivity.db.collection("keys")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            if (document.exists()) {
                                Key k = document.toObject(Key.class);
                                listKeySearch.add(k);
                            }
                        }
                        keyAdapter = new KeyAdapter(listKeySearch, user, this);
                        rViewSearch.setAdapter(keyAdapter);
                        keyAdapter.notifyDataSetChanged();
                    } else {
                        Log.d("appkey", "Error getting documents: ", task.getException());
                    }
                });
    }

    @SuppressWarnings("rawtypes")
    private void setHeightMaxOfView(View v, BottomSheetBehavior bottomSheetBehavior) {
        ViewTreeObserver viewTreeObserver = v.getViewTreeObserver();
        viewTreeObserver.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {

                v.getViewTreeObserver().removeOnPreDrawListener(this);
                int heightPxFromView = v.getMeasuredHeight();
                bottomSheetBehavior.setMaxHeight(heightPxFromView);
                return true;

            }
        });
    }

    public static void hideKeyboard(Context context, View editText) {
        InputMethodManager imm = (InputMethodManager)
                context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    //Lista de chaves que o usuário pegou
    private void setViewListEntryUser(User u) {

        rViewListKeysUser.setLayoutManager(new LinearLayoutManagerWrapper(this));
        rViewListKeysUser.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        rViewListKeysUser.setHasFixedSize(true);

        Query query = MainActivity.db
                .collection("entry")
                .whereEqualTo("matUserTakeKey", u.getMat())
                .whereEqualTo("matUserBackKey", "");

        FirestoreRecyclerOptions<Entry> optionsEntry =
                new FirestoreRecyclerOptions.Builder<Entry>().setQuery(query, Entry.class).build();

        adapterEntryUser = new FirestoreRecyclerAdapterForEntry(optionsEntry, user);

        rViewListKeysUser.setAdapter(adapterEntryUser);

    }

    //Lista de todas as chaves
    private void setViewListAllKeys() {

        rViewAllKeys.setLayoutManager(new LinearLayoutManagerWrapper(this));
        rViewAllKeys.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        rViewAllKeys.setHasFixedSize(true);

        Query query = MainActivity.db.collection("keys")
                .orderBy("name", Query.Direction.ASCENDING);

        //FirebaseRecyclerOptions
        FirestoreRecyclerOptions<Key> optionsKey = new FirestoreRecyclerOptions.Builder<Key>()
                .setQuery(query, Key.class)
                .build();


        //FirestoreRecyclerAdapter para Chave
        adapterAllKeys = new FirestoreRecyclerAdapterForKey(optionsKey, user);
        rViewAllKeys.setAdapter(adapterAllKeys);

    }

    //Listener da Pesquisa
    private SearchView.OnQueryTextListener listenerSearch() {
        return (
            new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) { return false; }

                @Override
                public boolean onQueryTextChange(String newText) {
                    filter(newText);
                    return false;
                }
            }
        );
    }

    private void filter(String text) {

        ArrayList<Key> filteredList = new ArrayList<>();

        for (Key item : listKeySearch) {
            if (item.getName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        if (!filteredList.isEmpty()) {

            keyAdapter.filterList(filteredList);

        }
    }

    private BottomSheetBehavior.BottomSheetCallback callbackSheetBehavior() {
        return (new BottomSheetBehavior.BottomSheetCallback() {
            @SuppressLint("SimpleDateFormat")
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    fillListKeySearch();
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
    }

    @Override
    public void onBackPressed() {

        if (bottomSheetBehaviorSearch.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehaviorSearch.setState(BottomSheetBehavior.STATE_HIDDEN);
            bottomSheetBehaviorAllKeys.setState(BottomSheetBehavior.STATE_COLLAPSED);
            etSearch.onActionViewCollapsed();
        }else if (bottomSheetBehaviorAllKeys.getState() == BottomSheetBehavior.STATE_EXPANDED)
            bottomSheetBehaviorAllKeys.setState(BottomSheetBehavior.STATE_COLLAPSED);
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