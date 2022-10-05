package com.luiz.mg.magickey.adapters;

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

        if (entry.getNameUserBackKey().equals("")) {

            holder.nameUserBackKey.setText(R.string.no_back);
            holder.nameUserBackKey.setTextColor(holder.nameUserBackKey.getContext().getResources()
                    .getColor(R.color.red, null));
            holder.constraintLayout.setBackgroundColor(holder.constraintLayout.getContext()
                    .getResources().getColor(R.color.red_background_color_entry_key, null));
            holder.dateTimeBackKey.setText("");

        } else {

            String nubk = Utils.BACKED+" "+entry.getNameUserBackKey();
            holder.nameUserBackKey.setText(nubk);
            holder.constraintLayout.setBackgroundColor(holder.constraintLayout.getContext()
                    .getResources().getColor(R.color.green_background_color_entry_key, null));
            String dtb = Utils.DAY+" "+entry.getDateTimeBackKey();
            holder.dateTimeBackKey.setText(dtb);

        }

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
