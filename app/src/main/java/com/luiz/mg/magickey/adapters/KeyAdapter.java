package com.luiz.mg.magickey.adapters;


import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.luiz.mg.magickey.R;
import com.luiz.mg.magickey.dao.EntryDAO;
import com.luiz.mg.magickey.dao.KeyDAO;
import com.luiz.mg.magickey.db.FeedReaderDbHelper;
import com.luiz.mg.magickey.models.Key;
import com.luiz.mg.magickey.models.User;
import com.luiz.mg.magickey.utils.Utils;

import java.util.ArrayList;

@SuppressWarnings("deprecation")
public class KeyAdapter extends RecyclerView.Adapter<KeyAdapter.ViewHolder> {

    private final ArrayList<Key> listKeys;
    private final User user;
    private final Context context;
    private final EntryDAO entryDAO;
    private final KeyDAO keyDAO;
    private final boolean isListOfUser;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView name;
        private final TextView dept;
        private final TextView bor;
        private final Button btnTakeOrBackKey;
        private final ConstraintLayout constraintLayout;

        public ViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.nameOfKeyId);
            dept = view.findViewById(R.id.deptOfKeyId);
            bor = view.findViewById(R.id.borId);
            btnTakeOrBackKey = view.findViewById(R.id.btnTakeOrBackId);
            constraintLayout = view.findViewById(R.id.layoutBackgroudItemListKeyId);
        }

    }

    public KeyAdapter(ArrayList<Key> keys, User user, boolean isListOfUser,Context ctx, FeedReaderDbHelper dbHelper) {
        this.listKeys = keys;
        this.user = user;
        this.isListOfUser = isListOfUser;
        this.context = ctx;
        this.entryDAO = new EntryDAO(dbHelper);
        this.keyDAO = new KeyDAO(dbHelper);
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.layout_item_list_keys, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        Key key = listKeys.get(position);

        viewHolder.name.setText(key.getNameKey());
        viewHolder.dept.setText(key.getDeptKey());

        //Se user for null esconde botão e setLongClick
        if (user == null) {

            viewHolder.btnTakeOrBackKey.setVisibility(View.INVISIBLE);

            viewHolder.constraintLayout.setClickable(true);
            viewHolder.constraintLayout.setFocusable(true);
            TypedValue outValue = new TypedValue();
            context.getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
            viewHolder.constraintLayout.setBackgroundResource(outValue.resourceId);

            viewHolder.constraintLayout.setOnLongClickListener(view -> {

                view.setOnCreateContextMenuListener((contextMenu, view1, contextMenuInfo) ->
                        contextMenu.add(Utils.DEL).setOnMenuItemClickListener(menuItem -> {

                            deleteKey(view1.getContext(), position);

                            return true;
                        }));

                return false;
            });

        } else {
            viewHolder.btnTakeOrBackKey.setVisibility(View.VISIBLE);
            viewHolder.btnTakeOrBackKey.setOnClickListener(view ->
                    takeOrBackKey(key, user, viewHolder));
        }

        // Se a chave não está emprestada
        if (key.getBorrowedKey().equals(Utils.NO_KEY)) {

            setViewsLikeTake(viewHolder);

        } else { // Se a chave está emprestada

            setViewsLikeBack(viewHolder);

        }
    }

    private void deleteKey(Context context, int pos) {

        int status = keyDAO.deleteKey(listKeys.get(pos).getNameKey());

        if (status == -1) {
            Toast.makeText(context, R.string.erro_delete_key, Toast.LENGTH_SHORT).show();
        } else {
            listKeys.remove(pos);
            notifyItemRemoved(pos);
            Toast.makeText(context, R.string.key_deleted, Toast.LENGTH_SHORT).show();
        }
    }

    //Set View Como Devolver
    private void setViewsLikeBack(ViewHolder viewHolder) {

        viewHolder.bor.setTextColor(context.getResources().getColor(R.color.red));
        viewHolder.bor.setText(R.string.borrowed);
        viewHolder.btnTakeOrBackKey.setText(R.string.back);
        viewHolder.btnTakeOrBackKey.setBackgroundColor(
                context.getResources().getColor(R.color.red));
    }


    private void setViewsLikeTake(ViewHolder viewHolder) {

        viewHolder.bor.setTextColor(context.getResources().getColor(R.color.green));
        viewHolder.bor.setText(R.string.no_bor);
        viewHolder.btnTakeOrBackKey.setText(R.string.take);
        viewHolder.btnTakeOrBackKey.setBackgroundColor(
                context.getResources().getColor(R.color.green));

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return listKeys.size();
    }

    //Pegar chave
    private void takeOrBackKey(Key key, User user, ViewHolder viewHolder) {

        //Se chave não está emprestada, pegar chave
        if (key.getBorrowedKey().equals(Utils.NO_KEY)) {

            Log.d("appkey", "tentando pegar chave...");

            takeKey(key, user, viewHolder);

        } else { //Se está emprestada, devolver chave

            Log.d("appkey", "tentando devolver chave...");

            backKey(key, user, viewHolder);

        }
    }


    //Pegar chave
    @SuppressLint("NotifyDataSetChanged")
    private void takeKey(Key key, User user, ViewHolder viewHolder) {

        //Tentar add Entry
        // Se -1, erro ao tentar add
        if(entryDAO.addEntry(key, user) == -1) {

            Log.d("appkey", "Erro ao tentar adicionar entry no DB- TAKE KEY");
            Toast.makeText(context, R.string.erro_take_key, Toast.LENGTH_SHORT).show();

        } else { // Se 1, sucesso

            Log.d("appkey", "Sucesso ao add Entry - TAKE KEY");

            //Tentar alterar campo emprestada
            //Se -1, Erro ao tentar alterar
            if (keyDAO.takeKey(key) == -1 ) {

                Log.d("appkey", "Erro ao tentar update chave como Emprestada no DB - TAKE KEY");
                Toast.makeText(context, R.string.erro_take_key, Toast.LENGTH_SHORT).show();

            } else { // Se 1, Sucesso

                Log.d("appkey", "Sucesso ao tentar update chave como Emprestada - TAKE KEY");
                Toast.makeText(context, R.string.succes_take_key, Toast.LENGTH_SHORT).show();

                setViewsLikeBack(viewHolder);
                listKeys.get(viewHolder.getAdapterPosition()).setBorrowedKey(Utils.YES_KEY);
                notifyDataSetChanged();

            }
        }
    }

    private void backKey(Key key, User user, ViewHolder viewHolder) {

        //Tentar add Entry
        // Se -1, erro ao tentar add
        if(entryDAO.upDateEntry(key, user) == -1) {

            Log.d("appkey", "Erro ao tentar adicionar Entry - BACK KEY");
            Toast.makeText(context, R.string.erro_take_key, Toast.LENGTH_SHORT).show();

        } else { // Se 1, sucesso

            Log.d("appkey", "Sucesso ao adicionar Entry - BACK KEY");

            //Tentar alterar campo emprestada
            //Se -1, Erro ao tentar alterar
            if (keyDAO.backKey(key) == -1 ) {

                Log.d("appkey", "Erro ao update Borrowed - BACK KEY");
                Toast.makeText(context, R.string.erro_back_key, Toast.LENGTH_SHORT).show();

            } else { // Se 1, Sucesso

                Log.d("appkey", "Sucesso ao update Borrowed - BACK KEY");
                Toast.makeText(context, R.string.succes_back_key, Toast.LENGTH_SHORT).show();

                setViewsLikeTake(viewHolder);
                listKeys.get(viewHolder.getAdapterPosition()).setBorrowedKey(Utils.NO_KEY);

                if (isListOfUser) {
                    listKeys.remove(viewHolder.getAdapterPosition());
                    notifyItemRemoved(viewHolder.getAdapterPosition());
                }

                notifyItemChanged(viewHolder.getAdapterPosition());



            }
        }

    }



}

