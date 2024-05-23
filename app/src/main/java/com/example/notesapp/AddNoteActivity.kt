package com.example.notesapp

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.notesapp.databinding.ActivityAddNoteBinding
import java.util.*

/**
 * AddNoteActivity est une activité qui permet à l'utilisateur d'ajouter une nouvelle note.
 */
class AddNoteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddNoteBinding
    private lateinit var db: NotesDatabaseHelper
    private lateinit var remindButton: Button
    private lateinit var alarmManager: AlarmManager
    private lateinit var timePicker: TimePicker
    private lateinit var datePicker: DatePicker

    // Variables pour stocker l'heure et la date sélectionnées
    private var selectedHour: Int = 0
    private var selectedMinute: Int = 0
    private var selectedYear: Int = 0
    private var selectedMonth: Int = 0
    private var selectedDay: Int = 0

    /**
     * Appelée lors de la création initiale de l'activité.
     * @param savedInstanceState Si l'activité est re-initialisée après avoir été
     * précédemment fermée, ce Bundle contient les données qu'elle avait fournies récemment.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialisation de la base de données, du gestionnaire d'alarmes, du sélecteur d'heure et du sélecteur de date
        db = NotesDatabaseHelper(this)
        alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        timePicker = TimePicker(this)
        datePicker = DatePicker(this)

        // Gestion du bouton de sauvegarde
        binding.saveButton.setOnClickListener {
            val title = binding.titleEditText.text.toString()
            val content = binding.contentEditText.text.toString()
            val note = Note(0, title, content)
            db.insertNote(note)
            finish()
            Toast.makeText(this, "Note sauvegardée", Toast.LENGTH_SHORT).show()
        }
    }
}
