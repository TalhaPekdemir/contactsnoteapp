package com.bm443.contactsnotesapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {
    private final String TAG = "MainActivity";
    private Button button;
    private ListView listView;
    private final int CONTACTS_PERMISSION_CODE = 1;
    private final int CONTACTS_REQUEST_CODE = 10;
    private final int test = 9999;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(getApplicationContext().checkSelfPermission(android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_CONTACTS}, CONTACTS_PERMISSION_CODE);

        initComponents();
        registerListeners();
    }

    private void initComponents() {
        button = findViewById(R.id.button);
        listView = findViewById(R.id.contacts_list);
    }

    private void registerListeners(){
        contactsButtonListener();
        listviewItemClickedListener();
    }

    private void contactsButtonListener(){
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchContactsIntent();
            }
        });
    }

    private void listviewItemClickedListener(){
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object item =  listView.getItemAtPosition(position);
                Toast.makeText(MainActivity.this, "You selected : " + item, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void launchContactsIntent() {
        Intent pickContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        pickContact.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
        startActivityForResult(pickContact, CONTACTS_REQUEST_CODE);
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
            Toast.makeText(this, "Numara: " + number + "Name:" + name, Toast.LENGTH_SHORT).show();
        }

        if(cursor != null){
            cursor.close();
        }
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
}