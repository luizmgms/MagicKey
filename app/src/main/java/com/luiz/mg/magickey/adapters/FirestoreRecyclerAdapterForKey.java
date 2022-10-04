package com.luiz.mg.magickey.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.luiz.mg.magickey.R;
import com.luiz.mg.magickey.models.Key;
import com.luiz.mg.magickey.utils.Utils;

public class FirestoreRecyclerAdapterForKey extends FirestoreRecyclerAdapter<Key,
        FirestoreRecyclerAdapterForKey.ViewHolder> {
    public FirestoreRecyclerAdapterForKey(@NonNull FirestoreRecyclerOptions<Key> options) {
        super(options);
    }



    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Key key) {

        holder.nameKey.setText(key.getName());
        holder.deptKey.setText(key.getDept());

        if (key.getBorr())
            holder.borr.setText(R.string.borrowed);
        else
            holder.borr.setText(R.string.no_bor);

        holder.btnTakeOrBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_list_keys, parent, false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView nameKey;
        private final TextView deptKey;
        private final TextView borr;
        private final Button btnTakeOrBack;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            nameKey = itemView.findViewById(R.id.nameOfKeyId);
            deptKey = itemView.findViewById(R.id.deptOfKeyId);
            borr = itemView.findViewById(R.id.borId);
            btnTakeOrBack = itemView.findViewById(R.id.btnTakeOrBackId);

        }
    }
}
