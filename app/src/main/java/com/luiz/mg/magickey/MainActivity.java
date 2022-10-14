package com.luiz.mg.magickey;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.luiz.mg.magickey.adapters.EntryAdapter;
import com.luiz.mg.magickey.adapters.FirestoreRecyclerAdapterForEntry;
import com.luiz.mg.magickey.adapters.FirestoreRecyclerAdapterForKey;
import com.luiz.mg.magickey.dao.EntryDAO;
import com.luiz.mg.magickey.db.FeedReaderDbHelper;
import com.luiz.mg.magickey.fragments.DatePickerFragment;
import com.luiz.mg.magickey.models.Entry;
import com.luiz.mg.magickey.models.Key;
import com.luiz.mg.magickey.models.User;
import com.luiz.mg.magickey.reports.MakeFile;
import com.luiz.mg.magickey.utils.LinearLayoutManagerWrapper;
import com.luiz.mg.magickey.utils.Utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;


public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        AdapterView.OnItemSelectedListener {

    public static FirebaseFirestore db = FirebaseFirestore.getInstance();

    private String userId = "";
    private TextView textViewUserId;

    @SuppressWarnings("rawtypes")
    public static BottomSheetBehavior bottomSheetBehavior;
    ArrayList<Entry> listEntry = new ArrayList<>();
    RecyclerView recyclerListOfEntry;
    TextView tvDate;

    ConstraintLayout progressBar;

    String date, date1;
    String sFilter = "Dia";

    Spinner spinnerFilter;

    FirestoreRecyclerAdapterForEntry adapterEntry;

    @SuppressLint("SimpleDateFormat")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        date = dtf1.format(LocalDateTime.now());
        date1 = dtf2.format(LocalDateTime.now());

        //Setando TextView Date com a data do dia.
        tvDate = findViewById(R.id.dateOfFilterId);
        tvDate.setText(date.split(" ")[0]);
        tvDate.setOnClickListener(view -> showDatePickerDialog(tvDate));

        //Callback da mudança de Estado
        bottomSheetBehavior.addBottomSheetCallback(callbackSheetBehavior());

        //RecyclerView de Entry
        recyclerListOfEntry = findViewById(R.id.listEntryId);
        recyclerListOfEntry.setLayoutManager(new LinearLayoutManagerWrapper(this));
        recyclerListOfEntry.addItemDecoration(
                new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));
        recyclerListOfEntry.setHasFixedSize(true);

        Query query = db.collection("ano").document("2022")
                .collection("mes").document("10")
                .collection("dia").document("10")
                .collection("entry")
                .orderBy("dateTimeTakeKey", Query.Direction.DESCENDING);

        //FirebaseRecyclerOptions
        FirestoreRecyclerOptions<Entry> optionsEntry = new FirestoreRecyclerOptions.Builder<Entry>()
                .setQuery(query, Entry.class)
                .build();

        //FirestoreRecyclerAdapter para Chave
        adapterEntry = new FirestoreRecyclerAdapterForEntry(optionsEntry, null);

        recyclerListOfEntry.setAdapter(adapterEntry);

        //Botão Lista de Entradas
        FloatingActionButton floatBtnListOfEntries = findViewById(R.id.btnListAllEntriesId);


        //Setando lista das Entry do dia no ReclycerView
        //filter(date);
        //Log.d("appkey", "Filtro: "+sFilter+", "+date);

        //Clique do botão
        floatBtnListOfEntries.setOnClickListener(view ->
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED));

        //Floating Botão Salvar Relatório
        FloatingActionButton fabSaveReport = findViewById(R.id.btnShareReportId);
        fabSaveReport.setOnClickListener(view -> {
            if (listEntry.isEmpty()) {
                showAlert(Utils.CREATE_FAIL, Utils.REPORT_EMPTY);
            }else {
                saveReport();
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
        btnFiltar.setOnClickListener(view -> {
            fillListEntry();
        });

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

            showAlert("Campo Vazio!", "Entre com o Nº de sua Matrícula ou CPF");

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
                            showAlert("Erro de Login!", "Usuário não encontrado!");

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
                                .getMessage());
                        Log.d("appkey", task.getException().getMessage());

                    }
                });

        }
    }

    private void fillListEntry() {

        String[] dateTime = tvDate.getText().toString().split(" ");
        String[] date = dateTime[0].split("/");
        String ano = date[0];
        String mes = date[1];
        String dia = date[2];

        adapterEntry.stopListening();
        recyclerListOfEntry = null;
        recyclerListOfEntry = findViewById(R.id.listEntryId);
        recyclerListOfEntry.setLayoutManager(new LinearLayoutManagerWrapper(this));
        recyclerListOfEntry.addItemDecoration(
                new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));
        recyclerListOfEntry.setHasFixedSize(true);

        Query query = db.collection("ano").document(ano)
                .collection("mes").document(mes)
                .collection("dia").document(dia)
                .collection("entry")
                .orderBy("dateTimeTakeKey", Query.Direction.DESCENDING);

        //FirebaseRecyclerOptions
        FirestoreRecyclerOptions<Entry> optionsEntry = new FirestoreRecyclerOptions.Builder<Entry>()
                .setQuery(query, Entry.class)
                .build();

        //FirestoreRecyclerAdapter para Chave
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

    private void saveReport () {

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

            //Se não, vamos criar a imagem
            Bitmap bitmap = Utils.screenShot(recyclerListOfEntry);

            File dir = new File(
                    Environment.getExternalStorageDirectory() + Utils.DIRECTORY_REPORTS);

            MakeFile makeFile = new MakeFile(dir);

            String[] datesplit = tvDate.getText().toString().split("/");
            String referencia;
            String date_ref_for_file = datesplit[0]+"-"+datesplit[1]+"-"+datesplit[2];

            if (sFilter.equals("Dia")){
                referencia = tvDate.getText().toString();

            } else if (sFilter.equals("Mês")) {
                referencia = datesplit[1]+"/"+datesplit[2];
            } else {
                referencia = datesplit[2];
            }

            int status = makeFile.savePdf(
                    bitmap, "Relatório-"+sFilter+"-"+date_ref_for_file,
                    referencia);

            if (status == 1) {

                Toast.makeText(
                        getApplicationContext(), Utils.SAVE_SUCCESS, Toast.LENGTH_SHORT).show();

                //Enviar e-mail
                sendEmail(referencia, dir,
                        "/Relatório-"+sFilter+"-"+date_ref_for_file+".pdf");

            } else {

                showAlert(Utils.CREATE_FAIL, Utils.SAVE_FAIL);
            }

        }
    }

    private void sendEmail(String referencia, File dir, String fileName) {
        String[] address = {Utils.ADDRESS_EMAIL_TO_SEND_REPORTS};

        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, address);

        intent.putExtra(
                Intent.EXTRA_SUBJECT,
                "Relatório Movimentações de Chaves-"+ referencia);

        intent.putExtra(Intent.EXTRA_TEXT,
                "Segue em anexo relatório com movimentações de chaves.\nReferência: "
                        + referencia+"\n\nEnviado do App MagicKey");

        intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://"+dir+fileName));

        startActivity(intent);
    }

    private void showAlert(String title, String msg) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(msg);

        // Add the buttons
        builder.setPositiveButton(R.string.ok, (dialog, id) -> {
            // User clicked OK button
        });
        // Create the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();

    }



    private BottomSheetBehavior.BottomSheetCallback callbackSheetBehavior() {
        return (new BottomSheetBehavior.BottomSheetCallback() {
            @SuppressLint("SimpleDateFormat")
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    filter(tvDate.getText().toString());
                } else if (newState == BottomSheetBehavior.STATE_HIDDEN){
                    sFilter = Utils.DAY;
                    spinnerFilter.setSelection(0);
                    tvDate.setText(date);
                    filter(date);
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

    public void filter(String date) {

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
        //sFilter = adapterView.getItemAtPosition(i).toString();
        //filter(tvDate.getText().toString());
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