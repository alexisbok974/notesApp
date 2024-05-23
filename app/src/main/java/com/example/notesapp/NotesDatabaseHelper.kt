package com.example.notesapp

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * NotesDatabaseHelper est une classe d'assistance pour la gestion de la base de données SQLite
 * de l'application NotesApp.
 *
 * @param context Le contexte de l'application
 */
class NotesDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "notesapp.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "allnotes"
        private const val COLUMN_ID = "id"
        private const val COLUMN_TITLE = "title"
        private const val COLUMN_CONTENT = "content"
    }

    /**
     * Appelée lors de la création initiale de la base de données.
     * @param db La base de données à créer
     */
    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery = "CREATE TABLE $TABLE_NAME ($COLUMN_ID INTEGER PRIMARY KEY, $COLUMN_TITLE TEXT, $COLUMN_CONTENT TEXT)"
        db?.execSQL(createTableQuery)
    }

    /**
     * Appelée lors de la mise à niveau de la base de données.
     * @param db La base de données à mettre à niveau
     * @param oldVersion L'ancienne version de la base de données
     * @param newVersion La nouvelle version de la base de données
     */
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val dropTableQuery = "DROP TABLE IF EXISTS $TABLE_NAME"
        db?.execSQL(dropTableQuery)
        onCreate(db)
    }

    /**
     * Insère une nouvelle note dans la base de données.
     * @param note La note à insérer
     */
    fun insertNote(note: Note) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TITLE, note.title)
            put(COLUMN_CONTENT, note.content)
        }
        db.insert(TABLE_NAME, null, values)
    }

    /**
     * Récupère toutes les notes de la base de données.
     * @return Une liste de toutes les notes
     */
    fun getAllNotes(): List<Note> {
        val notesList = mutableListOf<Note>()
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_NAME"
        val cursor = db.rawQuery(query, null)

        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
            val title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE))
            val content = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT))

            val note = Note(id, title, content)
            notesList.add(note)
        }
        cursor.close()
        return notesList
    }

    /**
     * Met à jour une note existante dans la base de données.
     * @param note La note à mettre à jour
     */
    fun updateNote(note: Note) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TITLE, note.title)
            put(COLUMN_CONTENT, note.content)
        }
        val whereClause = "$COLUMN_ID = ?"
        val whereArgs = arrayOf(note.id.toString())
        db.update(TABLE_NAME, values, whereClause, whereArgs)
    }

    /**
     * Récupère une note de la base de données par son ID.
     * @param noteId L'ID de la note à récupérer
     * @return La note correspondante
     */
    fun getNoteByID(noteId: Int): Note {
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_NAME WHERE $COLUMN_ID = $noteId"
        val cursor = db.rawQuery(query, null)
        cursor.moveToFirst()

        val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
        val title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE))
        val content = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT))

        cursor.close()
        return Note(id, title, content)
    }

    /**
     * Supprime une note de la base de données par son ID.
     * @param noteId L'ID de la note à supprimer
     */
    fun deleteNote(noteId: Int) {
        val db = writableDatabase
        val whereClause = "$COLUMN_ID = ?"
        val whereArgs = arrayOf(noteId.toString())
        db.delete(TABLE_NAME, whereClause, whereArgs)
    }
}
