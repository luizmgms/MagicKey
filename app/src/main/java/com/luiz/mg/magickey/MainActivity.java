package com.luiz.mg.magickey;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.luiz.mg.magickey.adapters.FirestoreRecyclerAdapterForEntry;
import com.luiz.mg.magickey.fragments.DatePickerFragment;
import com.luiz.mg.magickey.models.Entry;
import com.luiz.mg.magickey.models.User;
import com.luiz.mg.magickey.reports.MakeFile;
import com.luiz.mg.magickey.utils.DialogButtonClickWrapper;
import com.luiz.mg.magickey.utils.LinearLayoutManagerWrapper;
import com.luiz.mg.magickey.utils.Utils;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;


public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        AdapterView.OnItemSelectedListener {

    @SuppressLint("StaticFieldLeak")
    public static FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private String userId = "";
    private TextView textViewUserId;

    @SuppressWarnings("rawtypes")
    public static BottomSheetBehavior bottomSheetBehavior;
    RecyclerView recyclerListOfEntry;
    TextView tvDate;

    ConstraintLayout progressBar;

    String dateTime, dateTime1;
    String sFilter = "Dia";

    Spinner spinnerFilter;

    FirestoreRecyclerAdapterForEntry adapterEntry;

    public static SharedPreferences preferences;

    @SuppressLint("SimpleDateFormat")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Preferences
        preferences = getSharedPreferences("appkey", MODE_PRIVATE);

        //Autenticação no Firebase anonimamente
        mAuth.signInAnonymously()
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("appkey", "signInAnonymously:success");
                        //FirebaseUser user = mAuth.getCurrentUser();
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("appkey", "signInAnonymously:failure", task.getException());

                    }
                });

        //Barra de progresso
        progressBar = findViewById(R.id.progressBarId);

        //Botão de Área Restrita
        FloatingActionButton floatBtnLock = findViewById(R.id.lockButtonId);
        floatBtnLock.setOnClickListener(view -> enterAreaLock());

        //Layout do BottomSheet
         ConstraintLayout layoutBottomSheet = findViewById(R.id.layoutBottomSheetId);

        //Comportamento do BottomSheet
        bottomSheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);

        //Pegando data do dia
        DateTimeFormatter dtf1 = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("dd-MM-yyyy-HH-mm-ss");
        dateTime = dtf1.format(LocalDateTime.now());
        dateTime1 = dtf2.format(LocalDateTime.now());

        //Setando TextView Date com a data do dia.
        tvDate = findViewById(R.id.dateOfFilterId);
        String[] dateTimeSplit = dateTime.split(" ");
        tvDate.setText(dateTimeSplit[0]);
        tvDate.setOnClickListener(view -> showDatePickerDialog(tvDate));

        //Callback da mudança de Estado
        bottomSheetBehavior.addBottomSheetCallback(callbackSheetBehavior());

        //RecyclerView de Entry
        fillListEntry(sFilter);

        //Botão Lista de Entradas
        FloatingActionButton floatBtnListOfEntries = findViewById(R.id.btnListAllEntriesId);

        //Clique do botão
        floatBtnListOfEntries.setOnClickListener(view ->
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED));

        //Floating Botão Salvar Relatório
        FloatingActionButton fabSaveReport = findViewById(R.id.btnShareReportId);
        fabSaveReport.setOnClickListener(view -> {
            if (Objects.requireNonNull(recyclerListOfEntry.getAdapter()).getItemCount() == 0) {
                showAlert(Utils.CREATE_FAIL, Utils.REPORT_EMPTY, false);
            } else {
                saveAndSendReport(recyclerListOfEntry);
            }
        });

        spinnerFilter = findViewById(R.id.spinnerFilterId);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.filter, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFilter.setAdapter(adapter);

        //Ação ao selecionar um item
        spinnerFilter.setOnItemSelectedListener(this);

        //Botão filtar
        Button btnFiltar = findViewById(R.id.btnFilterEntryId);
        btnFiltar.setOnClickListener(view -> fillListEntry(sFilter));

        //Matrícula
        textViewUserId = findViewById(R.id.inputTextId);

        Button btn0 = findViewById(R.id.button0Id);
        btn0.setOnClickListener(this);
        Button btn1 = findViewById(R.id.button1Id);
        btn1.setOnClickListener(this);
        Button btn2 = findViewById(R.id.button2Id);
        btn2.setOnClickListener(this);
        Button btn3 = findViewById(R.id.button3Id);
        btn3.setOnClickListener(this);
        Button btn4 = findViewById(R.id.button4Id);
        btn4.setOnClickListener(this);
        Button btn5 = findViewById(R.id.button5Id);
        btn5.setOnClickListener(this);
        Button btn6 = findViewById(R.id.button6Id);
        btn6.setOnClickListener(this);
        Button btn7 = findViewById(R.id.button7Id);
        btn7.setOnClickListener(this);
        Button btn8 = findViewById(R.id.button8Id);
        btn8.setOnClickListener(this);
        Button btn9 = findViewById(R.id.button9Id);
        btn9.setOnClickListener(this);
        Button btnOK = findViewById(R.id.buttonOKId);
        btnOK.setOnClickListener(this);
        Button btnClear = findViewById(R.id.buttonClearId);
        btnClear.setOnClickListener(this);
        btnClear.setOnLongClickListener(view -> {
            userId = "";
            textViewUserId.setText(userId);
            return true;
        });

    }

    private void logIn(){

        if (userId.equals("")) {

            showAlert("Campo Vazio!", "Entre com o Nº de sua Matrícula ou CPF", false);

        } else {

            //Exibir barra de progresso
            progressBar.setVisibility(View.VISIBLE);

            //Consultar Usuário
            db.collection("users")
                .whereEqualTo("mat", userId)
                .get()
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        if (task.getResult().isEmpty()) {

                            userId = "";
                            textViewUserId.setText(userId);
                            progressBar.setVisibility(View.INVISIBLE);
                            showAlert("Erro de Login!", "Usuário não encontrado!", false);

                        } else {

                            for (QueryDocumentSnapshot document : task.getResult()) {

                                Log.d("appkey", document.getId() + " => "
                                        + document.getData());

                                User user = document.toObject(User.class);

                                userId = "";
                                textViewUserId.setText(userId);
                                progressBar.setVisibility(View.INVISIBLE);
                                openTakeOrBackKeysActivity(user);

                            }
                        }

                    } else {

                        userId = "";
                        textViewUserId.setText(userId);
                        progressBar.setVisibility(View.INVISIBLE);
                        showAlert("Falha!", Objects.requireNonNull(task.getException())
                                .getMessage(), false);
                        Log.d("appkey", task.getException().getMessage());

                    }
                });

        }
    }

    private void fillListEntry(String filter) {

        String[] dateTime = tvDate.getText().toString().split(" ");
        String[] date = dateTime[0].split("/");
        String dia = date[0];
        String mes = date[1];
        String ano = date[2];

        recyclerListOfEntry = null;
        recyclerListOfEntry = findViewById(R.id.listEntryId);
        recyclerListOfEntry.setLayoutManager(new LinearLayoutManagerWrapper(this));
        recyclerListOfEntry.addItemDecoration(
                new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));
        recyclerListOfEntry.setHasFixedSize(true);

        Query query;

        if (filter.equals(Utils.DAY)) {
            query = db.collection("entry")
                    .whereEqualTo("dia", dia)
                    .whereEqualTo("mes", mes)
                    .whereEqualTo("ano", ano)
                    .orderBy("dateTimeTakeKey", Query.Direction.DESCENDING);

        } else if (filter.equals(Utils.MONTH)) {
            query = db.collection("entry")
                    .whereEqualTo("mes", mes)
                    .whereEqualTo("ano", ano)
                    .orderBy("dateTimeTakeKey", Query.Direction.ASCENDING);
        } else {
            query = db.collection("entry")
                    .whereEqualTo("ano", ano)
                    .orderBy("dateTimeTakeKey", Query.Direction.DESCENDING);
        }

        //FirebaseRecyclerOptions
        FirestoreRecyclerOptions<Entry> optionsEntry = new FirestoreRecyclerOptions.Builder<Entry>()
                .setQuery(query, Entry.class)
                .build();

        //FirestoreRecyclerAdapter para Entry
        adapterEntry = null;
        adapterEntry = new FirestoreRecyclerAdapterForEntry(optionsEntry, null);

        recyclerListOfEntry.setAdapter(adapterEntry);
        adapterEntry.startListening();
    }

    private void openTakeOrBackKeysActivity(User user) {
        Intent i = new Intent(MainActivity.this, TakeOrBackKeyActivity.class);
        i.putExtra(Utils.NAME_USER, user.getName());
        i.putExtra(Utils.MAT_USER, user.getMat());
        i.putExtra(Utils.DEPT_USER, user.getDept());
        startActivity(i);
    }

    private void enterAreaLock(){

        Intent intent = new Intent(MainActivity.this, RestrictAreaActivity.class);
        startActivity(intent);

    }

    private void saveAndSendReport (RecyclerView recyclerView) {

        //Get Preferences
        SharedPreferences sharedPreferences = getSharedPreferences("appkey", MODE_PRIVATE);

        //Pegar email
        String emailToSendReport = sharedPreferences.getString("email", "");

        //Se tiver vazio, pede para cadastrar um email
        if (emailToSendReport.equals("")) {

            showAlert("EMAIL NÃO CADASTRADO!",
                    "Não existe nenhum email cadastrado para se enviar os relatórios. " +
                            "Se deseja cadastrar emails, toque em OK.", true);

        } else {

            // Verifica  o estado da permissão de WRITE_EXTERNAL_STORAGE
            int permissionCheck = ContextCompat
                    .checkSelfPermission(
                            this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                // Se for diferente de PERMISSION_GRANTED, então vamos exibir a tela padrão
                ActivityCompat
                        .requestPermissions(
                                this, new String[]{
                                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                1);
            } else {

                //Diretório de salvamento dos relatórios
                File dir = new File(
                        Environment.getExternalStorageDirectory() + Utils.DIRECTORY_REPORTS);

                MakeFile makeFile = new MakeFile(dir);

                String[] dateSplit = tvDate.getText().toString().split("/");
                String ref;
                String date_ref_for_file;

                if (sFilter.equals(Utils.DAY)) {

                    ref = tvDate.getText().toString();
                    date_ref_for_file = dateSplit[0] + "-" + dateSplit[1] + "-" + dateSplit[2];

                } else if (sFilter.equals(Utils.MONTH)) {

                    ref = dateSplit[1] + "/" + dateSplit[2];
                    date_ref_for_file = dateSplit[1] + "-" + dateSplit[2];

                } else {

                    ref = dateSplit[2];
                    date_ref_for_file = dateSplit[2];

                }

                //Criar o arquivo PDF
                int status = makeFile.createPdf(recyclerView,
                        "Relatório-" + sFilter + "-" + date_ref_for_file + ".pdf", ref);

                //Se foi criado com sucesso, abrir Intent de envio de e-mail
                if (status == 1) {

                    Toast.makeText(
                            getApplicationContext(), Utils.SAVE_SUCCESS, Toast.LENGTH_SHORT).show();

                    //Enviar e-mail
                    sendEmail(ref, dir, "/Relatório-" + sFilter + "-" + date_ref_for_file + ".pdf");

                } else {

                    //Erro ao criar o arquivo
                    showAlert(Utils.CREATE_FAIL, Utils.SAVE_FAIL, false);
                }

            }
        }
    }

    private void sendEmail(String referencia, File dir, String fileName) {

        SharedPreferences preferences = getSharedPreferences("appkey", MODE_PRIVATE);

        String emails = preferences.getString("email", "");

        String[] address = emails.split("\n");

        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, address);

        intent.putExtra(
                Intent.EXTRA_SUBJECT,
                "Relatório de Movimentações de Chaves-"+ referencia);

        intent.putExtra(Intent.EXTRA_TEXT,
                "Segue em anexo relatório com movimentações de chaves.\nReferência: "
                        + referencia+"\n\nEnviado do App MagicKey");

        intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://"+dir+fileName));

        startActivity(intent);
    }

    private void showAlert(String title, String msg, boolean isEmail) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(msg);

        // Add the buttons
        builder.setPositiveButton(R.string.ok, (dialog, id) -> {
            // User clicked OK button
            if (isEmail) {
                showDialogAddEmail();
            }
        });
        // Create the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    @SuppressLint("InflateParams")
    private void showDialogAddEmail(){

        AlertDialog dialog;
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View inflate;

        //Definição do Layout
        inflate = inflater.inflate(R.layout.layout_add_email, null);

        //Definição dos Campos
        final TextInputEditText itEmail = inflate.findViewById(R.id.emailsId);

        //SetView no dialog
        builder.setView(inflate);

        //Botão Cancelar
        builder.setNegativeButton(Utils.CANCEL, (dialogInterface, i) -> {
            //Cancelar
        });

        //Não Cancelável
        builder.setCancelable(false);

        //Criar
        dialog = builder.create();

        //Set Botão cadastrar (Não colocar nada)
        dialog.setButton(
                DialogInterface.BUTTON_POSITIVE, Utils.CAD, (dialog1, which) -> {});

        //Mostrar Dialog
        dialog.show();

        //Customizando botão cadastrar
        Button theButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        theButton.setOnClickListener(new DialogButtonClickWrapper(dialog) {
            @Override
            protected boolean onClicked() {

                return addEmails(itEmail);

            }
        });
    }

    public static boolean addEmails(TextInputEditText itEmail) {

        String strEmails = Objects.requireNonNull(itEmail.getText()).toString();

        if (!strEmails.contains("\n"))
            strEmails = strEmails+"\n";

        String[] aOfEmails = strEmails.split("\n");

        if (aOfEmails.length == 0) {

            itEmail.setError("Email inválido!");
            return false;

        } else {

            for (String e: aOfEmails) {
                if (!e.contains("@")) {
                    itEmail.setError("Algum email inválido!");
                    return false;
                }
            }

            boolean success = preferences.edit().putString(
                    "email", itEmail.getText().toString()).commit();

            if (success) {
                Toast.makeText(itEmail.getContext(), "Email(s) cadastrado(s) com sucesso!",
                        Toast.LENGTH_SHORT).show();
                return true;
            } else {
                Toast.makeText(itEmail.getContext(), "Erro ao cadastradar!",
                        Toast.LENGTH_SHORT).show();
                return false;
            }
        }

    }


    private BottomSheetBehavior.BottomSheetCallback callbackSheetBehavior() {
        return (new BottomSheetBehavior.BottomSheetCallback() {
            @SuppressLint("SimpleDateFormat")
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {

                    sFilter = Utils.DAY;
                    DateTimeFormatter dT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
                    String dt = dT.format(LocalDateTime.now());
                    String[] dateTimeSplit = dt.split(" ");
                    tvDate.setText(dateTimeSplit[0]);
                    fillListEntry(sFilter);

                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
    }

    public void showDatePickerDialog(TextView v) {
        DialogFragment newFragment = new DatePickerFragment(v);
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    @Override
    public void onBackPressed() {
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED)  {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        sFilter = adapterView.getItemAtPosition(i).toString();
        Log.d("appkey", "Filtro: " + sFilter);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        sFilter = "Dia";
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {

        int id = view.getId();

        switch (id) {
            case R.id.button0Id:
                userId = userId + "0";
                textViewUserId.setText(userId);
                break;
            case R.id.button1Id:
                userId = userId + "1";
                textViewUserId.setText(userId);
                break;
            case R.id.button2Id:
                userId = userId + "2";
                textViewUserId.setText(userId);
                break;
            case R.id.button3Id:
                userId = userId + "3";
                textViewUserId.setText(userId);
                break;
            case R.id.button4Id:
                userId = userId + "4";
                textViewUserId.setText(userId);
                break;
            case R.id.button5Id:
                userId = userId + "5";
                textViewUserId.setText(userId);
                break;
            case R.id.button6Id:
                userId = userId + "6";
                textViewUserId.setText(userId);
                break;
            case R.id.button7Id:
                userId = userId + "7";
                textViewUserId.setText(userId);
                break;
            case R.id.button8Id:
                userId = userId + "8";
                textViewUserId.setText(userId);
                break;
            case R.id.button9Id:
                userId = userId + "9";
                textViewUserId.setText(userId);
                break;
            case R.id.buttonClearId:
                int nc = userId.length();
                if (nc != 0) {
                    userId = userId.substring(0, nc - 1);
                    textViewUserId.setText(userId);
                }
                break;
            case R.id.buttonOKId:
                logIn();
                break;
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        adapterEntry.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapterEntry.stopListening();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}