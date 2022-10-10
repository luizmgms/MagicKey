package com.luiz.mg.magickey.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.luiz.mg.magickey.R;
import com.luiz.mg.magickey.models.Entry;
import com.luiz.mg.magickey.utils.Utils;

public class FirestoreRecyclerAdapterForEntry extends FirestoreRecyclerAdapter<Entry,
        FirestoreRecyclerAdapterForEntry.EntryViewHolder> {


    public FirestoreRecyclerAdapterForEntry(@NonNull FirestoreRecyclerOptions<Entry> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull EntryViewHolder holder, int position, @NonNull Entry entry) {

        String nok = Utils.KEY+" "+entry.getNameKey();
        holder.nameKey.setText(nok);

        String nutk = Utils.DELIVERED+" "+entry.getNameUserTakeKey();
        holder.nameUserTakeKey.setText(nutk);

        String dtt = Utils.DAY+" "+entry.getDateTimeTakeKey();
        holder.dateTimeTakeKey.setText(dtt);

        String textNameUserBackKey;
        String textDateTimeBackKey;
        int colorTextBackKey, colorBackground;

        if (entry.getNameUserBackKey().equals("")) {

            textNameUserBackKey = holder.nameUserBackKey.getContext().getResources()
                    .getString(R.string.no_back);
            colorTextBackKey = holder.nameUserBackKey.getContext().getResources()
                    .getColor(R.color.red, holder.nameUserBackKey.getContext().getTheme());
            textDateTimeBackKey = "";
            colorBackground = holder.constraintLayout.getContext()
                    .getResources().getColor(R.color.red_background_color_entry_key,
                            holder.constraintLayout.getContext().getTheme());

        } else {

            textNameUserBackKey = Utils.BACKED+" "+entry.getNameUserBackKey();
            colorTextBackKey = holder.dateTimeBackKey.getContext().getResources().getColor(
                    R.color.blue_1, holder.dateTimeBackKey.getContext().getTheme());
            textDateTimeBackKey = Utils.DAY+" "+entry.getDateTimeBackKey();
            colorBackground = holder.constraintLayout.getContext()
                    .getResources().getColor(R.color.green_background_color_entry_key,
                            holder.constraintLayout.getContext().getTheme());

        }

        holder.nameUserBackKey.setText(textNameUserBackKey);
        holder.nameUserBackKey.setTextColor(colorTextBackKey);
        holder.dateTimeBackKey.setText(textDateTimeBackKey);
        holder.constraintLayout.setBackgroundColor(colorBackground);

    }

    @NonNull
    @Override
    public EntryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.layout_item_list_entry, parent, false);

        return new EntryViewHolder(view);
    }

    public static class EntryViewHolder extends RecyclerView.ViewHolder {

        private final TextView nameKey;
        private final TextView nameUserTakeKey;
        private final TextView dateTimeTakeKey;
        private final TextView nameUserBackKey;
        private final TextView dateTimeBackKey;
        private final ConstraintLayout constraintLayout;

        public EntryViewHolder(@NonNull View itemView){
            super(itemView);

            nameKey = itemView.findViewById(R.id.nameOfKeyId);
            nameUserTakeKey = itemView.findViewById(R.id.nameOfUserTakedKeyId);
            dateTimeTakeKey = itemView.findViewById(R.id.dateTimeTakedKeyId);
            nameUserBackKey = itemView.findViewById(R.id.nameOfUserBackedKeyId);
            dateTimeBackKey = itemView.findViewById(R.id.dateTimeBackedKeyId);
            constraintLayout = itemView.findViewById(R.id.backgroundItemListEntryId);

        }

    }


}
