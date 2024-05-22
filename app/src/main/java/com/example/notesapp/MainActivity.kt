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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        dbHelper = NotesDatabaseHelper(this)
        database = dbHelper.readableDatabase
        notesAdapter = NotesAdapter(dbHelper.getAllNotes(), this)

        searchView = findViewById(R.id.searchView)
        recyclerView = findViewById(R.id.notesRecyclerView)


        binding.notesRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.notesRecyclerView.adapter = notesAdapter

        binding.addButton.setOnClickListener {
            val intent = Intent(this, AddNoteActivity::class.java)
            startActivity(intent)
        }


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

    override fun onDestroy() {
        dbHelper.close()
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        notesAdapter.refreshData(dbHelper.getAllNotes())
    }



}