package com.luiz.mg.magickey.adapters;


import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.luiz.mg.magickey.R;
import com.luiz.mg.magickey.models.Key;
import com.luiz.mg.magickey.models.User;

import java.util.ArrayList;

/**
 * @author Luiz Magno
 * Classe Adapter para Lista de Chaves da Busca literal
 * Extentida da classe RecyclerView.Adapter
 */
public class KeyAdapter extends RecyclerView.Adapter<KeyAdapter.ViewHolder> {

    private ArrayList<Key> listKeys;
    private final User user;
    private final Context context;

    /**
     * Classe interna usada para atribuir as views do itemView da lista
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView name;
        private final TextView dept;
        private final TextView bor;
        private final Button btnTakeOrBackKey;

        public ViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.nameOfKeyId);
            dept = view.findViewById(R.id.deptOfKeyId);
            bor = view.findViewById(R.id.borId);
            btnTakeOrBackKey = view.findViewById(R.id.btnTakeOrBackId);
        }

    }

    /**
     * Construtor da Classe
     * @param keys lista das chaves
     * @param user Objeto User
     * @param ctx Contexto da Aplicação
     */
    public KeyAdapter(ArrayList<Key> keys, User user, Context ctx) {
        this.listKeys = keys;
        this.user = user;
        this.context = ctx;

    }

    /**
     * Método que monta (infla) cada um dos itens da lista
     * @param viewGroup ViewGroup (pai)
     * @param viewType tipo da view
     * @return Objeto ViewHolder
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.layout_item_list_keys, viewGroup, false);

        return new ViewHolder(view);
    }

    /**
     * Método sobrescrito para setar views do item da lista.
     * @param viewHolder view pai com views a serem setadas
     * @param position posição do item na lista
     */
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        Key key = listKeys.get(position);

        viewHolder.name.setText(key.getName());
        viewHolder.dept.setText(key.getDept());
        if (key.getBorr()) {
            /* Emprestada --> Devolver */
            setViewsLikeBack(viewHolder, key, user);
        } else {
            /* Não Emprestada --> Pegar */
            setViewsLikeTake(viewHolder);
        }

        viewHolder.btnTakeOrBackKey.setOnClickListener(view -> {
            if (key.getBorr()) {
                /* Devolver */
                backKey(key, user, viewHolder);
            } else {
                /* Pegar */
                takeKey(key, user, viewHolder);
            }
        });


    }

    /**
     * Método para setar views para devolver chave (A chave está emprestada)
     * @param viewHolder view pai com views a serem setadas
     * @param k Objeto Key
     * @param u Objeto User
     */
    private void setViewsLikeBack(ViewHolder viewHolder, Key k, User u) {

        viewHolder.bor.setTextColor(context.getResources().getColor(R.color.red,
                viewHolder.bor.getContext().getTheme()));
        String textBor;
        if (k.getNameBorr().equals(u.getName())) {
            textBor = viewHolder.bor.getResources().getString(R.string.borrowed)
                    + " a Você";
        } else {

            String[] nameUserSplit = u.getName().split(" ");

            textBor = viewHolder.bor.getResources().getString(R.string.borrowed)
                    + " a " + nameUserSplit[0] + " "
                    + nameUserSplit[1];
        }
        viewHolder.bor.setText(textBor);
        viewHolder.btnTakeOrBackKey.setText(R.string.back);
        viewHolder.btnTakeOrBackKey.setBackgroundColor(
                context.getResources().getColor(R.color.red, context.getTheme()));
    }

    /**
     * Método para setar views para pegar chave (A chave não está emprestada)
     * @param viewHolder view pai com views a serem setadas
     */
    private void setViewsLikeTake(ViewHolder viewHolder) {

        viewHolder.bor.setTextColor(context.getResources().getColor(R.color.green,
                viewHolder.bor.getContext().getTheme()));
        viewHolder.bor.setText(R.string.no_bor);
        viewHolder.btnTakeOrBackKey.setText(R.string.take);
        viewHolder.btnTakeOrBackKey.setBackgroundColor(
                context.getResources().getColor(R.color.green,
                        viewHolder.btnTakeOrBackKey.getContext().getTheme()));

    }

    /**
     * Método sobrescrito que retorna a quantidade de itens na lista
     * @return Inteiro - tamanho da lista
     */
    @Override
    public int getItemCount() {
        return listKeys.size();
    }

    /**
     * Método que inicia o processo de emprestimo de chave
     * Ele chama o método estático takeKey() da classe FirestoreRecyclerAdapterForKey
     * @param key Objeto Key
     * @param user Objeto User
     * @param viewHolder view pai com views a serem setadas
     */
    private void takeKey(Key key, User user, ViewHolder viewHolder) {
        FirestoreRecyclerAdapterForKey.takeKey(key, user, context, "pegar");
        key.setMatBorr(user.getMat());
        key.setNameBorr(user.getName());
        setViewsLikeBack(viewHolder, key, user);
    }

    /**
     * Método que inicia o processo de devolução de chave
     * Ele chama o método estático backKey() da classe FirestoreRecyclerAdapterForKey
     * @param key Objeto Key
     * @param user Objeto User
     * @param viewHolder view pai com views a serem setadas
     */
    private void backKey(Key key, User user, ViewHolder viewHolder) {
        FirestoreRecyclerAdapterForKey.backKey(key, user, context, "devolver");
        setViewsLikeTake(viewHolder);
    }

    /**
     * Método de filtragem da lista de chaves usada na pesquisa de chave
     * @param filterlist ListKey filtrada
     */
    @SuppressLint("NotifyDataSetChanged")
    public void filterList(ArrayList<Key> filterlist) {
        listKeys = filterlist;
        notifyDataSetChanged();
    }



}

