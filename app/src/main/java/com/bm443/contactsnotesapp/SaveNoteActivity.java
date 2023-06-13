package com.bm443.contactsnotesapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bm443.contactsnotesapp.dao.INotDAO;
import com.bm443.contactsnotesapp.model.Note;
import com.bm443.contactsnotesapp.service.AppDatabase;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class SaveNoteActivity extends AppCompatActivity {
    private final String TAG = "SaveNoteActivity";
    private Button buttonSave;
    private TextView lblContactsNumber;
    private TextView lblContactsName;
    private TextInputLayout noteText;
    private AppDatabase appDatabase;
    private INotDAO notDAO;
    private Note currentNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_note);

        initComponents();
        registerListeners();
    }

    private void initComponents(){
        buttonSave = findViewById(R.id.btnSave);
        lblContactsNumber = findViewById(R.id.lblContactNumber);
        lblContactsName = findViewById(R.id.lblContactName);
        noteText = findViewById(R.id.editTextWrapper);
        appDatabase = AppDatabase.getAppDatabase(this);
        notDAO = appDatabase.getNoteDao();

        // Intent'e numara ve kayıtlı ismi getir.
        if(getIntent().hasExtra("new_note")){
            currentNote = getIntent().getParcelableExtra("new_note");
            lblContactsNumber.setText(String.format("Telefon Numarası: %s", currentNote.getContactNumber()));
            lblContactsName.setText(String.format("Kişi adı: %s", currentNote.getContactName()));
        }
        else if(getIntent().hasExtra("update_note")){
            currentNote = getIntent().getParcelableExtra("update_note");
            lblContactsNumber.setText(String.format("Telefon Numarası: %s", currentNote.getContactNumber()));
            lblContactsName.setText(String.format("Kişi adı: %s", currentNote.getContactName()));
            noteText.getEditText().setText(currentNote.getNote());
        }
        else{
            Log.w(TAG, "initComponents: hata!");
        }
    }

    private void registerListeners(){
        saveNote();
    }

    // Not update veya initial kaydetme
    private void saveNote(){
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Write to SQLite
                // Gelen id -1 ise henüz kaydedilmemiştir yeni kayıt oluştur
                if(currentNote.getId() == -1){
                    Note note = new Note();
                    note.setContactNumber(currentNote.getContactNumber());
                    note.setContactName(currentNote.getContactName());
                    note.setNote(noteText.getEditText().getText().toString());

                    notDAO.insertNote(note);
                }
                else{
                    // ama -1 den farklıysa kayıtlıdır, update et
                    currentNote.setNote(noteText.getEditText().getText().toString());

                    notDAO.updateNote(currentNote);
                }

                // kaydedince intent i kapat
                finish();
            }
        });
    }
}