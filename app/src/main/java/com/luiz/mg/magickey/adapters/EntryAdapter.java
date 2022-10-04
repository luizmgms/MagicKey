package com.luiz.mg.magickey.adapters;


import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.luiz.mg.magickey.MainActivity;
import com.luiz.mg.magickey.R;
import com.luiz.mg.magickey.TakeOrBackKeyActivity;
import com.luiz.mg.magickey.dao.UserDAO;
import com.luiz.mg.magickey.models.Entry;
import com.luiz.mg.magickey.models.User;
import com.luiz.mg.magickey.utils.Utils;

import java.util.ArrayList;

public class EntryAdapter extends RecyclerView.Adapter<EntryAdapter.ViewHolder> {

    private final ArrayList<Entry> listEntry;

    //Classe Interna
    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView nameKey;

        private final TextView nameUserTaked;
        private final TextView dateTimeTaked;

        private final TextView nameUserBacked;
        private final TextView dateTimeBacked;

        private final ConstraintLayout backgroundItemListEntry;


        public ViewHolder(View view) {
            super(view);

            nameKey = view.findViewById(R.id.nameOfKeyId);

            nameUserTaked = view.findViewById(R.id.nameOfUserTakedKeyId);
            dateTimeTaked = view.findViewById(R.id.dateTimeTakedKeyId);

            nameUserBacked = view.findViewById(R.id.nameOfUserBackedKeyId);
            dateTimeBacked = view.findViewById(R.id.dateTimeBackedKeyId);

            backgroundItemListEntry = view.findViewById(R.id.backgroundItemListEntryId);

        }

    }

    //Construtor
    public EntryAdapter(ArrayList<Entry> entries) {
        listEntry = entries;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.layout_item_list_entry, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        Entry entry = listEntry.get(position);

        //Set Nome da Chave
        String nkb = Utils.KEY+" "+entry.getName();
        viewHolder.nameKey.setText(nkb);

        //Set Nome de quem pegou a chave
        String nutk = Utils.DELIVERED+" "+entry.getNameUserTakeKey();
        viewHolder.nameUserTaked.setText(nutk);

        //Set Data e Hora da entrega
        String dtt = Utils.DAY+" "+entry.getDateTakeKey()+" as "+entry.getTimeTakeKey();
        viewHolder.dateTimeTaked.setText(dtt);

        //Set Nome de quem devolveu a chave
        String nubk = Utils.BACKED+" "+entry.getNameUserBackKey();
        viewHolder.nameUserBacked.setText(nubk);

        //Set Data e Hora da devolução
        String dtb = Utils.DAY+" "+entry.getDateBackKey()+" as "+entry.getTimeBackKey();
        viewHolder.dateTimeBacked.setText(dtb);

        //Mudar cor se a chave não foi devolvida
        if (entry.getDateBackKey() == null) {
            viewHolder.nameUserBacked.setText(Utils.NO_BACKED);
            viewHolder.dateTimeBacked.setText("");
            viewHolder.backgroundItemListEntry.setOnClickListener(view -> openActivity(view, entry));

        }
    }

    @Override
    public int getItemCount() {
        return listEntry.size();
    }

    private void openActivity(View view, Entry entry) {

        UserDAO userDAO = new UserDAO(MainActivity.dbHelper);
        User user = userDAO.consultUser(entry.getMatUserTakeKey());

        if (user != null) {

            Intent i = new Intent(view.getContext(), TakeOrBackKeyActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.putExtra(Utils.NAME_USER, user.getName());
            i.putExtra(Utils.MAT_USER, user.getMat());
            i.putExtra(Utils.DEPT_USER, user.getDept());
            view.getContext().getApplicationContext().startActivity(i);

            final Handler handler = new Handler();
            handler.postDelayed(() ->
                    MainActivity.bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN),
                    3000);

        }
    }
}

