package com.example.notesapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.notesapp.databinding.ActivityUpdateNoteBinding

/**
 * UpdateNoteActivity est une activité qui permet à l'utilisateur de mettre à jour une note existante.
 */
class UpdateNoteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUpdateNoteBinding
    private lateinit var db: NotesDatabaseHelper
    private var noteId: Int = -1

    /**
     * Appelée lors de la création initiale de l'activité.
     * @param savedInstanceState Si l'activité est re-initialisée après avoir été
     * précédemment fermée, ce Bundle contient les données qu'elle avait fournies récemment.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialisation de la base de données
        db = NotesDatabaseHelper(this)

        // Récupération de l'ID de la note passée en extra
        noteId = intent.getIntExtra("note_id", -1)
        if (noteId == -1) {
            finish()
            return
        }

        // Récupération de la note depuis la base de données
        val note = db.getNoteByID(noteId)
        binding.updateTitleEditText.setText(note.title)
        binding.updateContentEditText.setText(note.content)

        // Gestion du bouton de sauvegarde des modifications
        binding.updateSaveButton.setOnClickListener {
            val newTitle = binding.updateTitleEditText.text.toString()
            val newContent = binding.updateContentEditText.text.toString()
            val updatedNote = Note(noteId, newTitle, newContent)
            db.updateNote(updatedNote)
            finish()
            Toast.makeText(this, "Changements sauvegardés", Toast.LENGTH_SHORT).show()
        }
    }
}
