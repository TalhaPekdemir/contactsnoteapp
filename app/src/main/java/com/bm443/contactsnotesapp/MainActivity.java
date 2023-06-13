package com.bm443.contactsnotesapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bm443.contactsnotesapp.adapter.NotesRecyclerViewAdapter;
import com.bm443.contactsnotesapp.dao.INotDAO;
import com.bm443.contactsnotesapp.model.Note;
import com.bm443.contactsnotesapp.service.AppDatabase;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;


public class MainActivity extends AppCompatActivity implements NotesRecyclerViewAdapter.OnNotesClickListener {
    private final String TAG = "MainActivity";
    private FloatingActionButton fabAddNote;
    private RecyclerView notes;
    NotesRecyclerViewAdapter mNotesRecyclerViewAdapter;
    private final int CONTACTS_PERMISSION_CODE = 1;
    private final int CONTACTS_REQUEST_CODE = 10;
    private AppDatabase appDatabase;
    private INotDAO mNotDAO;
    private List<Note> mNoteList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(getApplicationContext().checkSelfPermission(android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_CONTACTS}, CONTACTS_PERMISSION_CODE);

        initComponents();
        registerListeners();
        loadData();
    }

    private void initComponents() {
        fabAddNote = findViewById(R.id.fabAddNote);
        notes = findViewById(R.id.notes);

        appDatabase = AppDatabase.getAppDatabase(MainActivity.this);
        mNotDAO = appDatabase.getNoteDao();
    }

    private void registerListeners(){
        fabAddNoteListener();
    }

    private void fabAddNoteListener(){
        fabAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchContactsIntent();
            }
        });
    }

    private void launchContactsIntent() {
        Intent pickContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        pickContact.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
        startActivityForResult(pickContact, CONTACTS_REQUEST_CODE);
    }

    private void launchSaveNoteIntent(String contactNumber, String contactName){
        Intent addNewNote = new Intent(MainActivity.this, SaveNoteActivity.class);
        Note note = new Note();
        note.setId(-1);
        note.setContactNumber(contactNumber);
        note.setContactName(contactName);
        addNewNote.putExtra("new_note", note);
        startActivity(addNewNote);
    }

    private void getContactNumberAndName(@Nullable Intent data){
        Uri contactUri = data.getData();
        String[] projection = new String[]{
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
        };
        Cursor cursor = getApplicationContext().getContentResolver().query(contactUri, projection,
                null, null, null);

        // If the cursor returned is valid, get the phone number and name
        if (cursor != null && cursor.moveToFirst()) {
            int numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            int nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
            String number = cursor.getString(numberIndex);
            String name = cursor.getString(nameIndex);

            // Not yazılacak intent'e git.
            launchSaveNoteIntent(number, name);
        }

        if(cursor != null){
            cursor.close();
        }
    }

    private void loadData(){
        mNoteList = mNotDAO.loadAllNotes();

        LinearLayoutManager llm = new LinearLayoutManager(this);
        notes.setLayoutManager(llm);
        notes.setHasFixedSize(true);
        notes.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        mNotesRecyclerViewAdapter = new NotesRecyclerViewAdapter(mNoteList, this);
        notes.setAdapter(mNotesRecyclerViewAdapter);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(notes);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            switch (requestCode){
                case CONTACTS_REQUEST_CODE:
                    getContactNumberAndName(data);
            }
        }
    }

    @Override
    protected void onResume() {
        loadData();
        super.onResume();
    }

    @Override
    public void onNoteClick(int position) {
        Intent intent = new Intent(this, SaveNoteActivity.class);
        intent.putExtra("update_note", mNoteList.get(position));
        startActivity(intent);
    }

    ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Not Siliniyor")
                    .setMessage("Notu silmek istediğinize emin misiniz?")
                    .setPositiveButton("EVET", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mNotDAO.deleteNote(mNoteList.get(viewHolder.getAdapterPosition()));
                            mNoteList.remove(viewHolder.getAdapterPosition());
                            mNotesRecyclerViewAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                        }
                    })
                    .setNegativeButton("HAYIR", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mNotesRecyclerViewAdapter.notifyDataSetChanged();
                            Toast.makeText(MainActivity.this, "Silme işlemi iptal edildi.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .create();
            alertDialog.show();
        }
    };
}