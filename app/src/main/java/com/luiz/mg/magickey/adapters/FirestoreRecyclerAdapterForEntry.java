package com.luiz.mg.magickey.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.luiz.mg.magickey.MainActivity;
import com.luiz.mg.magickey.R;
import com.luiz.mg.magickey.TakeOrBackKeyActivity;
import com.luiz.mg.magickey.models.Entry;
import com.luiz.mg.magickey.models.Key;
import com.luiz.mg.magickey.models.User;
import com.luiz.mg.magickey.utils.Utils;

/**
 * @author Luiz Magno
 * Classe Adapter para Lista de Registro de movimentações de chaves
 * Extentida da classe FirestoreRecyclerAdapter
 */
public class FirestoreRecyclerAdapterForEntry extends FirestoreRecyclerAdapter<Entry,
        FirestoreRecyclerAdapterForEntry.EntryViewHolder> {

    User user;

    /**
     * Construtor da Classe
     * @param options criado a partir de um Query do Firebase e uma Classe Entry
     * @param user Objeto usuário
     */
    public FirestoreRecyclerAdapterForEntry(@NonNull FirestoreRecyclerOptions<Entry> options,
                                            User user) {
        super(options);

        this.user = user;
    }

    /**
     * Método sobrescrito para setar views do item da lista.
     * Dependendo do estado de uma Entry, ele mostra se aquela chave já foi devolvida ou não.
     * @param holder view pai com views a serem setadas
     * @param position posição do item na lista
     * @param entry Objeto Entry da lista
     */
    @Override
    protected void onBindViewHolder(@NonNull EntryViewHolder holder, int position,
                                    @NonNull Entry entry) {

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

        if (user != null) {

            holder.btnBackKey.setVisibility(View.VISIBLE);
            holder.btnBackKey.setOnClickListener(view ->
                    backKey(entry.getNameKey(), user, holder.btnBackKey.getContext()));
            String text = "Entregue a você.";
            holder.nameUserTakeKey.setText(text);

        } else {

            holder.btnBackKey.setVisibility(View.INVISIBLE);

            if (entry.getNameUserBackKey().equals("")) {
                holder.constraintLayout.setOnClickListener(view ->
                        openActivity(holder.constraintLayout, entry));
            }
        }

    }

    /**
     * Método que inicia o processo de devolução da Chave no FireStore.
     * Ele pesquisa o id da chave e caso tenha sucesso chama o método estático backKey() da classe
     * FirestoreRecyclerAdapterForKey
     * @param nameKey nome da chave
     * @param u Objeto Usuário
     * @param ctx Contexto da aplicação para exibição de Toasts
     */
    private void backKey(String nameKey, User u, Context ctx) {
        MainActivity.db.collection("keys").document(nameKey)
        .get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                Key k = document.toObject(Key.class);
                if (document.exists() && k != null) {
                    FirestoreRecyclerAdapterForKey.backKey(k,u, ctx, "devolver");
                } else {
                    Toast.makeText(ctx, R.string.erro_back_key, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(ctx, R.string.erro_back_key, Toast.LENGTH_SHORT).show();
            }
        });


    }

    /**
     * Método que monta (infla) cada um dos itens da lista
     * @param parent ViewGroup (pai)
     * @param viewType tipo da view
     * @return Objeto EntryViewHolder
     */
    @NonNull
    @Override
    public EntryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.layout_item_list_entry, parent, false);

        return new EntryViewHolder(view);
    }

    /**
     * Método para abrir activity TakeOrBackKeyActivity caso a chave ainda não tenha sido devolvida.
     * Ele procura no FireStore o usuário que pegou a chave e caso tenha sucesso,
     * abre a activity TakeOrBackKeyActivity passando as informações do usuário.
     * @param view para se pegar o contexto da aplicação
     * @param entry item da lista que chamou o método
     */
    private void openActivity(View view, Entry entry) {

        MainActivity.db.collection("users")
                .document(entry.getMatUserTakeKey())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()){
                        User u = documentSnapshot.toObject(User.class);
                        if (u != null) {
                            Intent i = new Intent(view.getContext(), TakeOrBackKeyActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            i.putExtra(Utils.NAME_USER, u.getName());
                            i.putExtra(Utils.MAT_USER, u.getMat());
                            i.putExtra(Utils.DEPT_USER, u.getDept());
                            view.getContext().getApplicationContext().startActivity(i);
                        }

                    }
                }).addOnFailureListener(e -> Toast.makeText(view.getContext(),
                        "Falha!", Toast.LENGTH_SHORT).show());


    }

    /**
     * Classe interna usada para atribuir as views do itemView da lista
     */
    public static class EntryViewHolder extends RecyclerView.ViewHolder {

        private final TextView nameKey;
        private final TextView nameUserTakeKey;
        private final TextView dateTimeTakeKey;
        private final TextView nameUserBackKey;
        private final TextView dateTimeBackKey;
        private final Button btnBackKey;
        private final ConstraintLayout constraintLayout;

        public EntryViewHolder(@NonNull View itemView){
            super(itemView);

            nameKey = itemView.findViewById(R.id.nameOfKeyId);
            nameUserTakeKey = itemView.findViewById(R.id.nameOfUserTakedKeyId);
            dateTimeTakeKey = itemView.findViewById(R.id.dateTimeTakedKeyId);
            nameUserBackKey = itemView.findViewById(R.id.nameOfUserBackedKeyId);
            dateTimeBackKey = itemView.findViewById(R.id.dateTimeBackedKeyId);
            btnBackKey = itemView.findViewById(R.id.btnBackKeyId);
            constraintLayout = itemView.findViewById(R.id.backgroundItemListEntryId);

        }

    }


}
