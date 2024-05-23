package com.example.notesapp

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

/**
 * NotesAdapter est un adaptateur pour RecyclerView qui gère l'affichage et
 * les interactions avec les notes.
 *
 * @param notes Liste de notes à afficher
 * @param context Contexte de l'application
 */
class NotesAdapter(private var notes: List<Note>, context: Context) : RecyclerView.Adapter<NotesAdapter.NoteViewHolder>() {


    private val db: NotesDatabaseHelper = NotesDatabaseHelper(context)

        /**
         * NoteViewHolder est un ViewHolder pour les éléments de notes dans le RecyclerView.
         */
        class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
            val contentTextView: TextView = itemView.findViewById(R.id.contentTextView)
            val updateButton: ImageView = itemView.findViewById(R.id.updateButton)
            val deleteButton: ImageView = itemView.findViewById(R.id.deleteButton)
        }

    /**
     * Crée un nouveau ViewHolder lorsque le RecyclerView en a besoin.
     * @param parent Le ViewGroup parent
     * @param viewType Le type de vue
     * @return Un nouveau NoteViewHolder
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.note_item, parent, false)
        return NoteViewHolder(view)
    }

    /**
     * Retourne le nombre total de notes à afficher.
     * @return Le nombre de notes
     */
    override fun getItemCount(): Int = notes.size

    /**
     * Attribue les données de la note à la vue du ViewHolder.
     * @param holder Le NoteViewHolder
     * @param position La position de l'élément dans la liste
     */
    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = notes[position]
        holder.titleTextView.text = note.title
        holder.contentTextView.text = note.content

        holder.updateButton.setOnClickListener{
            val intent = Intent(holder.itemView.context, UpdateNoteActivity::class.java).apply {
                putExtra("note_id", note.id)
            }
            holder.itemView.context.startActivity(intent)
        }

        holder.deleteButton.setOnClickListener {
            db.deleteNote(note.id)
            refreshData(db.getAllNotes())
            Toast.makeText(holder.itemView.context, "Note supprimée", Toast.LENGTH_SHORT).show()

        }

    }

    /**
     * Met à jour les données de l'adaptateur avec une nouvelle liste de notes et notifie les changements.
     * @param newNotes La nouvelle liste de notes
     */
    fun refreshData(newNotes: List<Note>) {
        Log.d("NotesAdapter", "refreshData called with ${newNotes.size} notes")
        notes = newNotes
        notifyDataSetChanged()
        Log.d("NotesAdapter", "RecyclerView updated with ${notes.size} notes")
    }

    companion object {
        fun refreshData(notes: MutableList<Note>) {

        }
    }


}