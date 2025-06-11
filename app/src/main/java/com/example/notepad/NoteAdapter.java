package com.example.notepad;

import android.content.Context;
import android.util.Log;
import android.view.*;
import android.widget.*;

import androidx.annotation.NonNull;

import java.util.List;

public class NoteAdapter extends ArrayAdapter<Note> {

    private final NoteDao noteDao;

    public NoteAdapter(Context context, List<Note> notes, NoteDao noteDao) {
        super(context, 0, notes);
        this.noteDao = noteDao;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        Note note = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.note_item, parent, false);
        }

        TextView noteText = convertView.findViewById(R.id.note_text);
        ImageButton deleteButton = convertView.findViewById(R.id.delete_button);

        assert note != null;
        noteText.setText(note.text);

        deleteButton.setOnClickListener(v -> {
            Log.d("NOTE_APP", "Deleting note: " + note.text);
            NoteDatabase.databaseWriteExecutor.execute(() -> {
                noteDao.delete(note);
                Log.d("NOTE_APP", "Note removed from database: " + note.text);
            });
        });

        return convertView;
    }
}

