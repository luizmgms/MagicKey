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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.WriteBatch;
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
     * @param options criado a partir de uma Query do Firebase e uma Classe Key
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

        //Se a chave está emprestada
        if (key.getBorr()) {

            textBor = holder.borr.getContext().getResources().getString(R.string.borrowed)
                    + " a " + key.getNameBorr();
            colorTextBor = holder.borr.getContext()
                    .getResources().getColor(R.color.red, holder.borr.getContext().getTheme());
            textBtn = R.string.back;
            colorBackgroundBtn = holder.btnTakeOrBack.getContext()
                    .getResources().getColor(R.color.red,
                            holder.btnTakeOrBack.getContext().getTheme());

        //Se não está emprestada
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

        //Se usuário é null, retirar botão PEGAR/DEVOLVER
        if (user == null ) {

            holder.btnTakeOrBack.setVisibility(View.INVISIBLE);

        } else {

            //Se existe usuário e a chave está emprestada, a chave deve ser devolvida
            if (key.getBorr()) {

                //Nome do usuário que pegou a chave
                String[] names = key.getNameBorr().split(" ");
                nameUser = names[0] + " " + names[names.length - 1];

                //Se a matrícula de quem pegou a chave for igual a do usuário
                if (key.getMatBorr().equals(user.getMat())) {
                    nameUser = "Você";
                }

                textBor = Utils.BORR + " a " + nameUser;
                holder.borr.setText(textBor);

                holder.btnTakeOrBack.setOnClickListener(view -> {
                    /*devolver*/
                    backKey(key, user, holder.btnTakeOrBack.getContext(), "devolver");
                });

            //Se não está emprestada, então a chave pode ser emprestada
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
     * Ele faz as operações de inserção de Entry e Atualização de Chave em lote
     * Se uma dessas operações falhar o Firebase faz o rollback automaticamente
     * @param key Objeto chave
     * @param user Objeto Usuário
     * @param ctx Contexto da aplicação para exibição de Toasts
     * @param message String para mensagens Toast
     */
    public static void takeKey(Key key, User user, Context ctx, String message) {

        //Data de hora
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        String dateTimeTakeKey = dtf.format(LocalDateTime.now());

        //Criando nova Entry
        Entry newEntry = new Entry(
                key.getName(),
                user.getMat(), user.getName(), dateTimeTakeKey,
                "", "", ""
        );

        Log.d("appkey", "Iniciando processo de PEGAR CHAVE...");

        //Gravação em lote
        WriteBatch batch = MainActivity.db.batch();

        //Criando Referência do Documento Entry
        DocumentReference docRefEntry = MainActivity.db.collection("entry").document();

        //Adicionando Entry
        batch.set(docRefEntry, newEntry);

        //Pegando Referência do Documento Key
        DocumentReference docRefKey = MainActivity.db.collection("keys")
                .document(key.getName());

        //Atualizado estado da chave
        batch.update(docRefKey, "borr", !key.getBorr(),
                "matBorr", user.getMat(),
                "nameBorr", user.getName()
        );

        //Commit transações
        batch.commit().addOnSuccessListener(unused -> {

            //Sucesso
            Toast.makeText(ctx, "Sucesso ao "+message+" chave!", Toast.LENGTH_SHORT).show();
            Log.d("appkey", "Sucesso no processo de PEGAR CHAVE!");

        }).addOnFailureListener(e -> {

                //Falhou, Firebase faz o Rollback automaticamente
                Toast.makeText(ctx, "Erro ao tentar "+message+" chave!",
                        Toast.LENGTH_SHORT).show();
                Log.d("appkey", "Erro no processo de PEGAR CHAVE");
                e.printStackTrace();

        });

    }

    /**
     * Método que inicia o processo de devolução de uma chave
     * Ele pesquisa no FireStore o id da Entry em aberto (chave não devolvida),
     * se obtiver sucesso, ele chama o método updateEntry()
     * @param key Objeto Key
     * @param user Objeto Usuário
     * @param ctx  Contexto da aplicação para exibição de Toasts
     * @param message String para mensagens Toast
     */
    public static void backKey(Key key, User user, Context ctx, String message){

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        String dateTimeBackKey = dtf.format(LocalDateTime.now());

        Log.d("appkey", "Iniciando processo de DEVOLVER CHAVE...");

        MainActivity.db.collection("entry")
                .whereEqualTo("nameKey", key.getName())
                .whereEqualTo("matUserBackKey", "")
                .get()
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()){

                        if (!task.getResult().isEmpty()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {

                                String idEntry = document.getId();

                                Log.d("appkey", "Chamando Método upDateEntry(idEntry, " +
                                        "user, dateTimeBackKey, 'devolver')");

                                /*upDateEntry*/
                                updateEntry(idEntry, key, user, dateTimeBackKey, ctx, message);

                            }

                        } else {
                            Toast.makeText(ctx, "Erro ao tentar "+message+" chave!",
                                    Toast.LENGTH_SHORT).show();
                            Log.d("appkey", "Erro ao tentar devolver chave dentro de" +
                                    " backKey(): Task is Empty");
                        }

                    } else {
                        Toast.makeText(ctx, "Erro ao tentar "+message+" chave!",
                                Toast.LENGTH_SHORT).show();
                        Log.d("appkey", "Erro ao tentar devolver chave dentro de" +
                                " backKey(): Task Failure");
                    }
                }).addOnFailureListener(e -> {
                    Toast.makeText(ctx, "Erro ao tentar "+message+" chave!",
                                Toast.LENGTH_SHORT).show();
                    Log.d("appkey", "Erro ao tentar devolver chave dentro de" +
                            " backKey(): Task Failure");
                });
    }

    /**
     * Método que inicia o processo de atualização de uma Entry em aberto (Chave não devolvida).
     * Ele faz as operações de atualização de Entry e atualização de Chave em lote
     * Se uma dessas operações falhar o Firebase faz o rollback automaticamente
     * @param idEntry id da Entry a ser atualizada
     * @param key Objeto Key
     * @param user Objeto User
     * @param dateTimeBackKey Data e hora de devolução da chave
     * @param ctx Contexto da aplicação para exibição de Toasts
     * @param message  String para mensagens Toast
     */
    private static void updateEntry(String idEntry, Key key, User user, String dateTimeBackKey,
                                    Context ctx, String message) {

        //Atualização em lote
        WriteBatch batch = MainActivity.db.batch();

        //Referência da Entry a ser atualizada
        DocumentReference docRefEntry = MainActivity.db.collection("entry")
                .document(idEntry);

        //Update Entry
        batch.update(docRefEntry, "matUserBackKey", user.getMat(),
                "nameUserBackKey", user.getName(),
                "dateTimeBackKey", dateTimeBackKey);

        //Referência da Key a ser atualizada
        DocumentReference docRefKey = MainActivity.db.collection("keys")
                .document(key.getName());

        //Update Key
        batch.update(docRefKey, "borr", !key.getBorr(),
                "matBorr", user.getMat(),
                "nameBorr", user.getName());

        //Commit o lote
        batch.commit().addOnSuccessListener(unused -> {

            Toast.makeText(ctx, "Sucesso ao "+message+" chave!",
                    Toast.LENGTH_SHORT).show();
            Log.d("appkey", "Sucesso no rocesso de DEVOLVER CHAVE!");

        }).addOnFailureListener(e -> {

            Toast.makeText(ctx, "Erro ao tentar "+message+" chave!",
                    Toast.LENGTH_SHORT).show();
            Log.d("appkey", "Erro ao tentar atualizar chave!");
            e.printStackTrace();

        });
    }

    /**
     * Método que monta (infla) cada um dos itens da lista
     * @param parent ViewGroup (pai)
     * @param viewType tipo da view
     * @return Objeto KeyViewHolder
     */
    @NonNull
    @Override
    public KeyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_list_keys,
                parent, false);

        return new KeyViewHolder(view);

    }

    /**
     * Classe interna usada para atribuir as views do itemView da lista
     */
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
