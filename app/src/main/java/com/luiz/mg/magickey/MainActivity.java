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

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.luiz.mg.magickey.adapters.EntryAdapter;
import com.luiz.mg.magickey.dao.EntryDAO;
import com.luiz.mg.magickey.dao.KeyDAO;
import com.luiz.mg.magickey.dao.UserDAO;
import com.luiz.mg.magickey.db.FeedReaderDbHelper;
import com.luiz.mg.magickey.fragments.DatePickerFragment;
import com.luiz.mg.magickey.models.Entry;
import com.luiz.mg.magickey.models.User;
import com.luiz.mg.magickey.reports.MakeFile;
import com.luiz.mg.magickey.utils.Utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        AdapterView.OnItemSelectedListener {

    public static FeedReaderDbHelper dbHelper;

    private String userId = "";
    private TextView textViewUserId;

    @SuppressWarnings("rawtypes")
    public static BottomSheetBehavior bottomSheetBehavior;
    ArrayList<Entry> listEntry = new ArrayList<>();
    EntryDAO entryDAO;
    RecyclerView recyclerListOfEntry;
    TextView tvDate;

    String date, date1;
    String sFilter = "Dia";

    Spinner spinnerFilter;

    @SuppressLint("SimpleDateFormat")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new FeedReaderDbHelper(getApplicationContext());

        //KeyDAO keyDAO = new KeyDAO(dbHelper);
        //keyDAO.deleteAllKeys();
        //keyDAO.addKeysOfList(Utils.getListAllKeys());

        //UserDAO userDAO = new UserDAO(getApplicationContext());
        //userDAO.addUsersOfList(ReadFile.getListUsersOfFile());
        //userDAO.deleteAllUsers();

        //Botão de Área Restrita
        FloatingActionButton floatBtnLock = findViewById(R.id.lockButtonId);
        floatBtnLock.setOnClickListener(view -> enterAreaLock());

        //Layout do BottomSheet
         ConstraintLayout layoutBottomSheet = findViewById(R.id.layoutBottomSheetId);

        //Comportamento do BottomSheet
        bottomSheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);

        //Pegando data do dia
        Date dateTimeStamp = new Date();
        date = new SimpleDateFormat("dd/MM/yyyy").format(dateTimeStamp);
        date1 = new SimpleDateFormat("dd-MM-yyyy").format(dateTimeStamp);

        //Setando TextView Date com a data do dia.
        tvDate = findViewById(R.id.dateOfFilterId);
        tvDate.setText(date);
        tvDate.setOnClickListener(view -> showDatePickerDialog(tvDate));

        //Callback da mudança de Estado
        bottomSheetBehavior.addBottomSheetCallback(callbackSheetBehavior());

        //RecyclerView de Entry
        recyclerListOfEntry = findViewById(R.id.listEntryId);
        recyclerListOfEntry.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerListOfEntry.addItemDecoration(
                new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));

        //Botão Lista de Entradas
        FloatingActionButton floatBtnListOfEntries = findViewById(R.id.btnListAllEntriesId);

        //Inicializando DAO Entry
        entryDAO = new EntryDAO(dbHelper);

        //Setando lista das Entry do dia no ReclycerView
        filter(date);
        Log.d("appkey", "Filtro: "+sFilter+", "+date);

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
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.filter, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinnerFilter.setAdapter(adapter);
        //Ação ao selecionar um item
        spinnerFilter.setOnItemSelectedListener(this);

        //Matrícula
        textViewUserId = findViewById(R.id.inputTextId);

        Button btn0= findViewById(R.id.button0Id);
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

            UserDAO userDAO = new UserDAO(dbHelper);
            User user = userDAO.consultUser(userId);

            if ( user == null) {

                showAlert("Erro de Login!", "Usuário não encontrado!");

            } else {

                userId = "";
                textViewUserId.setText(userId);
                openTakeOrBackKeysActivity(user);

            }
        }
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
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    filter(tvDate.getText().toString());
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
    }

    public void showDatePickerDialog(TextView v) {
        DialogFragment newFragment = new DatePickerFragment(v, MainActivity.this);
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void filter(String date) {
        Log.d("appkey", "Chamou Filter: "+ sFilter +": "+date);
        if (sFilter.equals("Dia")){
            setListEntryForDay(date);
        } else if (sFilter.equals("Mês")) {
            setListEntryForMonth(date);
        } else {
            setListEntryForYear(tvDate.getText().toString());
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void setListEntryForDay(String date) {
        listEntry = entryDAO.listEntriesForDateTake(date);
        EntryAdapter entryAdapter = new EntryAdapter(listEntry);
        recyclerListOfEntry.setAdapter(entryAdapter);
        entryAdapter.notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void setListEntryForMonth(String date) {
        listEntry = entryDAO.listEntriesForMonthTake(date);
        EntryAdapter entryAdapter = new EntryAdapter(listEntry);
        recyclerListOfEntry.setAdapter(entryAdapter);
        entryAdapter.notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void setListEntryForYear(String date) {
            listEntry = entryDAO.listEntriesForYearTake(date);
            EntryAdapter entryAdapter = new EntryAdapter(listEntry);
            recyclerListOfEntry.setAdapter(entryAdapter);
            entryAdapter.notifyDataSetChanged();
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
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}