package com.bm443.contactsnotesapp.service;

import android.content.Context;

import androidx.room.AutoMigration;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.bm443.contactsnotesapp.dao.INotDAO;
import com.bm443.contactsnotesapp.model.Note;

@Database(entities = {Note.class}, exportSchema = true, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase appDatabase;
    public abstract INotDAO getNoteDao();
    private static final String databaseName = "notes.db";

    public static AppDatabase getAppDatabase(Context context) {
        if (appDatabase == null) {
            appDatabase =
                    Room.databaseBuilder(context, AppDatabase.class, databaseName)
                            .allowMainThreadQueries()
                            .build();
        }
        return appDatabase;
    }

    public static void destroyInstance() {
        appDatabase = null;
    }
}
