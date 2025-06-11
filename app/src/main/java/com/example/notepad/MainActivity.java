package com.example.notepad;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText editNote;
    private ListView noteList;
    private NoteAdapter adapter;
    private NoteDao noteDao;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editNote = findViewById(R.id.editNote);
        Button addButton = findViewById(R.id.addButton);
        noteList = findViewById(R.id.noteList);

        NoteDatabase db = NoteDatabase.getDatabase(this);
        noteDao = db.noteDao();

        LiveData<List<Note>> notes = noteDao.getAll();
        notes.observe(this, newNotes -> {
            adapter = new NoteAdapter(MainActivity.this, newNotes, noteDao);
            noteList.setAdapter(adapter);
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = editNote.getText().toString().trim();
                if (!text.isEmpty()) {
                    Log.d("NOTE_APP", "Adding note: " + text);

                    NoteDatabase.databaseWriteExecutor.execute(() -> {
                        noteDao.insert(new Note(text));
                        Log.d("NOTE_APP", "Note saved in the database: " + text);
                    });

                    editNote.setText("");
                } else {
                    Toast.makeText(MainActivity.this, "Type your note first!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        Button deleteAllButton = findViewById(R.id.deleteAllButton);

        deleteAllButton.setOnClickListener(v -> {
            new android.app.AlertDialog.Builder(MainActivity.this)
                    .setTitle("Confirm Deletion")
                    .setMessage("Are you sure you want to delete all notes?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        NoteDatabase.databaseWriteExecutor.execute(() -> {
                            noteDao.deleteAll();
                            Log.d("NOTE_APP", "All notes deleted from database");
                        });
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

    }
}
