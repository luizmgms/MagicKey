package com.luiz.mg.magickey;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.firestore.Query;
import com.luiz.mg.magickey.adapters.FirestoreRecyclerAdapterForEntry;
import com.luiz.mg.magickey.adapters.FirestoreRecyclerAdapterForKey;
import com.luiz.mg.magickey.models.Entry;
import com.luiz.mg.magickey.models.Key;
import com.luiz.mg.magickey.models.User;
import com.luiz.mg.magickey.utils.LinearLayoutManagerWrapper;
import com.luiz.mg.magickey.utils.Utils;

public class TakeOrBackKeyActivity extends AppCompatActivity {

    @SuppressWarnings("rawtypes")
    private BottomSheetBehavior bottomSheetBehaviorAllKeys;
    User user;

    FirestoreRecyclerAdapterForKey adapterAllKeys;
    FirestoreRecyclerAdapterForEntry adapterEntryUser;
    RecyclerView rViewListKeysUser;
    RecyclerView rViewAllKeys;
    TextView tvListEmptyId;


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

        //Pegando informações do usuário logado
        Intent intent = getIntent();
        String mat = intent.getExtras().getString(Utils.MAT_USER);
        String name = intent.getExtras().getString(Utils.NAME_USER);
        String dept = intent.getExtras().getString(Utils.DEPT_USER);

        //Criando objeto usuário
        user = new User(mat, name, dept);

        //Setando View com nome do usuário
        String[] splitName = name.split(" ");
        String nameUser = splitName[0]+ " " +splitName[splitName.length - 1];

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

        /* Eventos */
        //Evento ao clicar no texto OUTRA CHAVE
        titleOthersKeys.setOnClickListener(view -> {
            //Expandir Bottom Sheet Todas as Chaves
            bottomSheetBehaviorAllKeys.setState(BottomSheetBehavior.STATE_EXPANDED);
        });

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

    @Override
    public void onBackPressed() {

        if (bottomSheetBehaviorAllKeys.getState() == BottomSheetBehavior.STATE_EXPANDED)
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