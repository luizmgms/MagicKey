package com.luiz.mg.magickey.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.luiz.mg.magickey.MainActivity;
import com.luiz.mg.magickey.R;
import com.luiz.mg.magickey.models.Entry;
import com.luiz.mg.magickey.models.Key;
import com.luiz.mg.magickey.models.User;
import com.luiz.mg.magickey.utils.Utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author Luiz Magno
 * Classe Adapter para Lista de Chaves
 * Extentida da classe FirestoreRecyclerAdapter
 */
public class FirestoreRecyclerAdapterForKey extends FirestoreRecyclerAdapter<Key,
        FirestoreRecyclerAdapterForKey.KeyViewHolder> {

    private final User user;

    /**
     * Construtor da Classe
     * @param options criado a partir de um Query do Firebase e uma Classe Key
     * @param user Objeto usuário
     */
    public FirestoreRecyclerAdapterForKey(@NonNull FirestoreRecyclerOptions<Key> options, User user) {
        super(options);

        this.user = user;

    }

    /**
     * Método sobrescrito para setar views do item da lista.
     * Dependendo do estado da chave, ele mostra de a chave está emprestada e para quem.
     * Também mostra o botão Devolver ou Pegar se ele estiver emprestada ou não.
     * @param holder view pai com views a serem setadas
     * @param position posição do item na lista
     * @param key Objeto Key da lista
     */
    @Override
    protected void onBindViewHolder(@NonNull KeyViewHolder holder, int position, @NonNull Key key) {

        String textBor;
        String nameUser;
        int colorTextBor;
        int textBtn;
        int colorBackgroundBtn;

        holder.nameKey.setText(key.getName());
        holder.deptKey.setText(key.getDept());

        if (key.getBorr()) {

            textBor = holder.borr.getContext().getResources().getString(R.string.borrowed)
                    + " a " + key.getNameBorr();
            colorTextBor = holder.borr.getContext()
                    .getResources().getColor(R.color.red, holder.borr.getContext().getTheme());
            textBtn = R.string.back;
            colorBackgroundBtn = holder.btnTakeOrBack.getContext()
                    .getResources().getColor(R.color.red,
                            holder.btnTakeOrBack.getContext().getTheme());

        } else {

            textBor = holder.borr.getContext().getResources().getString(R.string.no_bor);
            colorTextBor = holder.borr.getContext()
                    .getResources().getColor(R.color.green, holder.borr.getContext().getTheme());
            textBtn = R.string.take;
            colorBackgroundBtn = holder.btnTakeOrBack.getContext()
                    .getResources().getColor(R.color.green,
                            holder.btnTakeOrBack.getContext().getTheme());

        }

        holder.borr.setText(textBor);
        holder.borr.setTextColor(colorTextBor);
        holder.btnTakeOrBack.setText(textBtn);
        holder.btnTakeOrBack.setBackgroundColor(colorBackgroundBtn);

        if (user == null ) {

            holder.btnTakeOrBack.setVisibility(View.INVISIBLE);

        } else {

            if (key.getBorr()) {

                nameUser = key.getNameBorr().split(" ")[0] + " " +
                        key.getNameBorr().split(" ")[1];

                if (key.getMatBorr().equals(user.getMat())) {
                    nameUser = "Você";
                }

                textBor = Utils.BORR + " a " + nameUser;

                holder.borr.setText(textBor);

                holder.btnTakeOrBack.setOnClickListener(view -> {
                    /*devolver*/
                    backKey(key, user, holder.btnTakeOrBack.getContext(), "devolver");
                });

            } else {

                holder.btnTakeOrBack.setOnClickListener(view -> {
                    /*pegar*/
                    takeKey(key, user, holder.btnTakeOrBack.getContext(), "pegar");
                });

            }
        }

    }

    /**
     * Método que inicia o processo de pegar uma Chave.
     * Ele primeiro tentar adicionar uma nova Entry ao FireStore criada a partir dos dados dos
     * Objetos key e user, se obtiver sucesso, ele chama o método updateKey().
     * @param key Objeto chave
     * @param user Objeto Usuário
     * @param ctx Contexto da aplicação para exibição de Toasts
     * @param message String para mensagens Toast
     */
    public static void takeKey(Key key, User user, Context ctx, String message) {

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        String dateTimeTakeKey = dtf.format(LocalDateTime.now());

        Entry newEntry = new Entry(
                key.getName(),
                user.getMat(), user.getName(), dateTimeTakeKey,
                "", "", ""
        );

        //Add Entry
        MainActivity.db.collection("entry")
                .add(newEntry)
                .addOnSuccessListener(documentReference -> {
                    /*updateChave*/
                    updateKey(key, user, ctx, message);
                }).addOnFailureListener(e ->
                        Toast.makeText(ctx, "Erro ao tentar "+message+ " chave!",
                                Toast.LENGTH_SHORT).show());

    }

    /**
     * Método que inicia processo de atualização do estado da chave no FireSTore.
     * Dependendo do estado se está emprestada ou não, ela é atualizada para o contrário.
     * @param key objeto key
     * @param u objeto Usuário
     * @param ctx Contexto da aplicação para exibição de Toasts
     * @param message String para mensagens Toast
     */
    public static void updateKey(Key key, User u,Context ctx, String message) {

        MainActivity.db.collection("keys").document(key.getName())
            .update("borr", !key.getBorr(),
                    "matBorr", u.getMat(), "nameBorr", u.getName())
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(ctx, "Sucesso ao "+message+" chave!",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ctx, "Erro ao tentar "+message+" chave!",
                            Toast.LENGTH_SHORT).show();
                }
            })
            .addOnFailureListener(e ->
                    Toast.makeText(ctx, "Erro ao tentar "+message+" chave!",
                            Toast.LENGTH_SHORT).show());

    }

    public static void backKey(Key key, User user, Context ctx, String message){

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        String dateTimeBackKey = dtf.format(LocalDateTime.now());

        MainActivity.db.collection("entry")
                .whereEqualTo("nameKey", key.getName())
                .whereEqualTo("matUserBackKey", "")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        if (!task.getResult().isEmpty()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {

                                String idEntry = document.getId();

                                Log.d("appkey", "idEntry: "+idEntry);

                                /*upDateEntry*/
                                upDateEntry(idEntry, key, user, dateTimeBackKey, ctx, message);

                            }

                        } else {
                            Toast.makeText(ctx, "Erro ao tentar "+message+" chave!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(ctx, "Erro ao tentar "+message+" chave!",
                                Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(e ->
                        Toast.makeText(ctx, "Erro ao tentar "+message+" chave!",
                                Toast.LENGTH_SHORT).show());
    }

    private static void upDateEntry(String idEntry, Key key, User user, String dateTimeBackKey,
                                    Context ctx, String message) {

        MainActivity.db.collection("entry")
                .document(idEntry)
                .update("matUserBackKey",
                        user.getMat(),
                        "nameUserBackKey",
                        user.getName(),
                        "dateTimeBackKey",
                        dateTimeBackKey)
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        /*updateKey*/
                        updateKey(key, user,ctx, message);

                    } else {
                        Toast.makeText(ctx, "Erro ao tentar "+message+" chave!",
                                Toast.LENGTH_SHORT).show();
                    }

                } ).addOnFailureListener(e ->
                        Toast.makeText(ctx, "Erro ao tentar "+message+" chave!",
                                Toast.LENGTH_SHORT).show());

    }


    @NonNull
    @Override
    public KeyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_list_keys,
                parent, false);

        return new KeyViewHolder(view);

    }

    public static class KeyViewHolder extends RecyclerView.ViewHolder {

        private final TextView nameKey;
        private final TextView deptKey;
        private final TextView borr;
        private final Button btnTakeOrBack;

        public KeyViewHolder(@NonNull View itemView) {
            super(itemView);

            nameKey = itemView.findViewById(R.id.nameOfKeyId);
            deptKey = itemView.findViewById(R.id.deptOfKeyId);
            borr = itemView.findViewById(R.id.borId);
            btnTakeOrBack = itemView.findViewById(R.id.btnTakeOrBackId);

        }
    }
}
