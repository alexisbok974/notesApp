package com.example.notesapp

import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.SearchView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notesapp.databinding.ActivityMainBinding
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var searchView: SearchView
    private lateinit var recyclerView: RecyclerView
    private lateinit var dbHelper: NotesDatabaseHelper
    private lateinit var database: SQLiteDatabase
    private lateinit var notesAdapter: NotesAdapter



    /**
     * Appelée lors de la création initiale de l'activité.
     * @param savedInstanceState Si l'activité est re-initialisée après avoir été
     * précédemment fermée, ce Bundle contient les données qu'elle avait fournies récemment.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialisation du helper de base de données et de la base de donnée
        dbHelper = NotesDatabaseHelper(this)
        database = dbHelper.readableDatabase
        notesAdapter = NotesAdapter(dbHelper.getAllNotes(), this)

        // Initialisation des vues du fichier xml
        searchView = findViewById(R.id.searchView)
        recyclerView = findViewById(R.id.notesRecyclerView)

        // Configuration de RecyclerView avec un LinearLayoutManager et le NotesAdapter
        binding.notesRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.notesRecyclerView.adapter = notesAdapter

        // Configuration du bouton d'ajout pour lancer AddNoteActivity
        binding.addButton.setOnClickListener {
            val intent = Intent(this, AddNoteActivity::class.java)
            startActivity(intent)
        }

        // Configuration du SearchView pour filtrer les notes en fonction de l'entrée utilisateur
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    searchNotes(it)
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    searchNotes(it)
                }
                return false
            }
        })

    }

    /**
     * Recherche dans la base de données des notes correspondant à la requête
     * et met à jour le RecyclerView.
     * @param query La requête de recherche entrée par l'utilisateur.
     */
    private fun searchNotes(query: String) {
        Log.d("MainActivity", "Searching for: $query")
        database?.let { db ->
            val cursor: Cursor = db.rawQuery(
                "SELECT * FROM allnotes WHERE title LIKE ? OR content LIKE ?",
                arrayOf("%$query%", "%$query%")
            )
            val notes = mutableListOf<Note>()
            while (cursor.moveToNext()) {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val title = cursor.getString(cursor.getColumnIndexOrThrow("title"))
                val content = cursor.getString(cursor.getColumnIndexOrThrow("content"))
                notes.add(Note(id, title, content))
            }
            cursor.close()
            Log.d("MainActivity", "Found ${notes.size} notes")
            notesAdapter.refreshData(notes)
        }
    }

    /**
     * Ferme le helper de base de données lorsque l'activité est détruite.
     */
    override fun onDestroy() {
        dbHelper.close()
        super.onDestroy()
    }

    /**
     * Rafraîchit les données des notes lorsque l'activité est reprise.
     */
    override fun onResume() {
        super.onResume()
        notesAdapter.refreshData(dbHelper.getAllNotes())
    }



}