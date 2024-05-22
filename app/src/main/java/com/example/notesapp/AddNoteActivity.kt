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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = NotesDatabaseHelper(this)
        alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        timePicker = TimePicker(this)
        datePicker = DatePicker(this)
        remindButton = findViewById(R.id.remindButton)

        // Gestion du clic sur le bouton de rappel
        remindButton.setOnClickListener {
            showReminderPicker()
        }

        binding.saveButton.setOnClickListener{
            val title = binding.titleEditText.text.toString()
            val content = binding.contentEditText.text.toString()
            val note = Note(0, title, content)
            db.insertNote(note)
            scheduleNotification(getSelectedTimeInMillis(), note)
            finish()
            Toast.makeText(this, "Note sauvegardée", Toast.LENGTH_SHORT).show()
        }
    }

    // Afficher le dialogue de sélection de l'heure et de la date
    private fun showReminderPicker() {
        val calendar = Calendar.getInstance()

        val timePickerDialog = TimePickerDialog(
            this,
            { _, hourOfDay, minute ->
                selectedHour = hourOfDay
                selectedMinute = minute

                // Une fois l'heure sélectionnée, afficher le dialogue de sélection de la date
                val datePickerDialog = DatePickerDialog(
                    this,
                    { _, year, monthOfYear, dayOfMonth ->
                        selectedYear = year
                        selectedMonth = monthOfYear
                        selectedDay = dayOfMonth

                        // Une fois l'heure et la date sélectionnées, afficher la notification
                        val selectedTimeInMillis = getSelectedTimeInMillis()
                        Toast.makeText(
                            this,
                            "Rappel programmé pour ${formatTime(selectedHour, selectedMinute)} le ${formatDate(selectedYear, selectedMonth, selectedDay)}",
                            Toast.LENGTH_LONG
                        ).show()
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                )

                // Afficher le dialogue de sélection de la date
                datePickerDialog.show()
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        )

        // Afficher le dialogue de sélection de l'heure
        timePickerDialog.show()
    }

    // Obtenir l'heure sélectionnée par l'utilisateur et la convertir en millisecondes
    private fun getSelectedTimeInMillis(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(selectedYear, selectedMonth, selectedDay, selectedHour, selectedMinute)
        return calendar.timeInMillis
    }

    // Formater l'heure et la date
    private fun formatTime(hour: Int, minute: Int): String {
        return String.format("%02d:%02d", hour, minute)
    }

    private fun formatDate(year: Int, month: Int, day: Int): String {
        return String.format("%d-%02d-%02d", year, month + 1, day)
    }

    private fun scheduleNotification(timeInMillis: Long, note: Note) {
        val intent = Intent(this, NotificationReceiver::class.java)
        intent.putExtra("title", note.title)
        intent.putExtra("content", note.content)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Vérifier la version d'Android pour utiliser RTC_WAKEUP ou setExactAndAllowWhileIdle
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)
        }

        Toast.makeText(this, "Rappel programmé pour ${formatDateTime(timeInMillis)}", Toast.LENGTH_LONG).show()
    }

    private fun formatDateTime(timeInMillis: Long): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timeInMillis
        val dateFormat = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }
}
