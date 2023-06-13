package com.bm443.contactsnotesapp.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.bm443.contactsnotesapp.MainActivity;
import com.bm443.contactsnotesapp.R;
import com.bm443.contactsnotesapp.dao.INotDAO;
import com.bm443.contactsnotesapp.model.Note;

import java.util.List;

public class NotesRecyclerViewAdapter extends RecyclerView.Adapter<NotesRecyclerViewAdapter.NotesViewHolder> {
    private final String TAG = "NotesRecyclerViewAdapter";
    private List<Note> notes;
    private OnNotesClickListener mOnNotesClickListener;
    public NotesRecyclerViewAdapter(List<Note> notes, OnNotesClickListener onNotesClickListener) {
        this.notes = notes;
        this.mOnNotesClickListener = onNotesClickListener;
    }

    @NonNull
    @Override
    public NotesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_element, parent, false);
        return new NotesViewHolder(mView, mOnNotesClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull NotesViewHolder holder, int position) {
        Note note = notes.get(position);
        holder.lblNumara.setText(note.getContactNumber());
        holder.lblAd.setText(note.getContactName());
        holder.lblNot.setText(note.getNote());
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    // Inner holder class
    public class NotesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView lblNumara, lblAd, lblNot;
        OnNotesClickListener onNotesClickListener;

        public NotesViewHolder(View view, OnNotesClickListener onNotesClickListener){
            super(view);
            lblNumara = view.findViewById(R.id.lblNumara);
            lblAd = view.findViewById(R.id.lblAd);
            lblNot = view.findViewById(R.id.lblNot);
            this.onNotesClickListener = onNotesClickListener;
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onNotesClickListener.onNoteClick(getAdapterPosition());
        }
    }

    public interface OnNotesClickListener{
        void onNoteClick(int position);
    }
}
