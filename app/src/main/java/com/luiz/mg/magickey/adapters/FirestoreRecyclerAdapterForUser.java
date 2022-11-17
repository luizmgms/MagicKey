package com.luiz.mg.magickey.adapters;

import android.content.Intent;
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
import com.luiz.mg.magickey.TakeOrBackKeyActivity;
import com.luiz.mg.magickey.models.User;
import com.luiz.mg.magickey.utils.Utils;

/**
* @author Luiz Magno
* Classe Adapter para Lista de Usuários
* Extentida da classe FirestoreRecyclerAdapter
*/
public class FirestoreRecyclerAdapterForUser extends FirestoreRecyclerAdapter<User,
        FirestoreRecyclerAdapterForUser.UserViewHolder> {

    /**
     * Construtor da Classe
     * @param options criado a partir de uma Query do Firebase e uma Classe User
     */
    public FirestoreRecyclerAdapterForUser(@NonNull FirestoreRecyclerOptions<User> options) {
        super(options);
    }

    /**
     * Método sobrescrito para setar views do item da lista.
     * @param holder view pai com views a serem setadas
     * @param position posição do item na lista
     * @param user Objeto User da lista
     */
    @Override
    protected void onBindViewHolder(@NonNull UserViewHolder holder, int position, @NonNull User user) {

        holder.mat.setText(user.getMat());
        holder.name.setText(user.getName());
        holder.dept.setText(user.getDept());
        holder.constraintLayout.setOnClickListener(view -> {
            Intent i = new Intent(view.getContext(), TakeOrBackKeyActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.putExtra(Utils.NAME_USER, user.getName());
            i.putExtra(Utils.MAT_USER, user.getMat());
            i.putExtra(Utils.DEPT_USER, user.getDept());
            view.getContext().getApplicationContext().startActivity(i);
        });

    }

    /**
     * Método que monta (infla) cada um dos itens da lista
     * @param parent ViewGroup (pai)
     * @param viewType tipo da view
     * @return Objeto UserViewHolder
     */
    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.layout_item_list_users, parent, false);

        return new UserViewHolder(view);
    }

    /**
     * Classe interna usada para atribuir as views do itemView da lista
     */
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
