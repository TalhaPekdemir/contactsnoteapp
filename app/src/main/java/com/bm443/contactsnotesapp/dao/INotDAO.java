package com.bm443.contactsnotesapp.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.bm443.contactsnotesapp.model.Note;

import java.util.List;

@Dao
public interface INotDAO {
    @Insert
    void insertNote(Note note);

    @Update
    void updateNote(Note note);

    @Delete
    void deleteNote(Note note);

    @Query("SELECT * FROM note")
    List<Note> loadAllNotes();
}
