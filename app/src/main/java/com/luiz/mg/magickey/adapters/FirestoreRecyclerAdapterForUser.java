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
import com.luiz.mg.magickey.models.User;

public class FirestoreRecyclerAdapterForUser extends FirestoreRecyclerAdapter<User,
        FirestoreRecyclerAdapterForUser.UserViewHolder> {

    public FirestoreRecyclerAdapterForUser(@NonNull FirestoreRecyclerOptions<User> options) {
        super(options);
    }



    @Override
    protected void onBindViewHolder(@NonNull UserViewHolder holder, int position, @NonNull User user) {

        holder.mat.setText(user.getMat());
        holder.name.setText(user.getName());
        holder.dept.setText(user.getDept());
        holder.constraintLayout.setOnClickListener(view -> {

        });

    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.layout_item_list_users, parent, false);

        return new UserViewHolder(view);
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {

        private final TextView mat;
        private final TextView name;
        private final TextView dept;
        private final ConstraintLayout constraintLayout;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);

            mat = itemView.findViewById(R.id.matId);
            name = itemView.findViewById(R.id.nameId);
            dept = itemView.findViewById(R.id.deptId);
            constraintLayout = itemView.findViewById(R.id.layoutItemListUsersId);

        }
    }
}
