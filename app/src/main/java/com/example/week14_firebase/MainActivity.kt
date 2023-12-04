package com.example.week14_firebase

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.example.week14_firebase.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private val firestore = FirebaseFirestore.getInstance()
    private val budgetCollectionRef = firestore.collection("tes")
    private lateinit var binding: ActivityMainBinding
    private val detailListLiveData: MutableLiveData<List<Detail>> by lazy {
        MutableLiveData<List<Detail>>()
    }
    companion object {
        const val ID = "id"
        const val NAMA = "nama"
        const val DESKRIPSI = "deskripsi"
        const val TANGGAL = "tanggal"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnGoToMain.setOnClickListener {
            val intent = Intent(this, TambahData::class.java)
            startActivity(intent)
        }

        observeDetail()
        getAllDetail()
    }

    private fun getAllDetail() {
        observeDetailChanges()
    }

    private fun observeDetail() {
        detailListLiveData.observe(this) { datadetail ->
            val adapterFeed = DetailAdapter(this, datadetail)
            binding.listView.apply {
                adapter = adapterFeed
                layoutManager = LinearLayoutManager(this@MainActivity)
                setHasFixedSize(true)
            }
        }
    }

    private fun observeDetailChanges() {
        budgetCollectionRef.addSnapshotListener { snapshots, error ->
            if (error != null) {
                Log.d("MainActivity", "Error: ", error)
                return@addSnapshotListener
            }
            val detail = snapshots?.toObjects(Detail::class.java)
            if (detail != null) {
                detailListLiveData.postValue(detail)
            }
        }
    }

    inner class DetailAdapter(
        private val context: Context,
        private val detailList: List<Detail>
    ) : RecyclerView.Adapter<DetailAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val itemView = LayoutInflater.from(context).inflate(R.layout.datadetail, parent, false)
            return ViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val detail = detailList[position]
            holder.bind(detail)
        }

        override fun getItemCount(): Int {
            return detailList.size
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val namaTextView: TextView = itemView.findViewById(R.id.namaDetail)
            private val deskripsiTextView: TextView = itemView.findViewById(R.id.descDetail)
            private val tanggalTextView: TextView = itemView.findViewById(R.id.tgl)
            private val btnDelete: ImageButton = itemView.findViewById(R.id.btn_delete)

            fun bind(feedback: Detail) {
                // Bind the data to the views
                namaTextView.text = feedback.nama
                deskripsiTextView.text = feedback.description
                tanggalTextView.text = feedback.date

                // Set click listener for item click
                itemView.setOnClickListener {
                    val intent = Intent(context, TambahData::class.java)
                    intent.putExtra(ID, feedback.id)
                    intent.putExtra(NAMA, feedback.nama)
                    intent.putExtra(DESKRIPSI, feedback.description)
                    intent.putExtra(TANGGAL, feedback.date)
                    context.startActivity(intent)
                }

                // Set long click listener for item long click
                itemView.setOnLongClickListener {
                    deleteBudget(feedback)
                    true // Indicate that the long click event is handled
                }

                // Set click listener for delete button
                btnDelete.setOnClickListener {
                    // Handle delete button click
                    deleteBudget(feedback)
                }
            }
        }
    }

    private fun deleteBudget(feedback: Detail) {
        if (feedback.id.isEmpty()) {
            Log.d("MainActivity", "Error deleting: feedback ID is empty!")
            return
        }
        budgetCollectionRef.document(feedback.id)
            .delete()
            .addOnSuccessListener {
                Log.d("MainActivity", "Successfully Deleting Feedback")
            }
            .addOnFailureListener {
                Log.d("MainActivity", "Error deleting feedback: ", it)
            }
    }
}