package com.example.week14_firebase

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import com.example.week14_firebase.MainActivity.Companion.DESKRIPSI
import com.example.week14_firebase.MainActivity.Companion.ID
import com.example.week14_firebase.MainActivity.Companion.NAMA
import com.example.week14_firebase.MainActivity.Companion.TANGGAL
import com.example.week14_firebase.databinding.TambahDataBinding
import com.google.firebase.firestore.FirebaseFirestore


class TambahData : AppCompatActivity() {
    private var idFeedback = ""
    private val firestore = FirebaseFirestore.getInstance()
    private val budgetCollectionRef = firestore.collection("tes")
    private lateinit var binding: TambahDataBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = TambahDataBinding.inflate(layoutInflater)
        setContentView(binding.root)

        idFeedback = intent.getStringExtra(ID) ?: ""
        val nama = intent.getStringExtra(NAMA)
        val deskripsi = intent.getStringExtra(DESKRIPSI)
        val tanggal = intent.getStringExtra(TANGGAL)

        // Kemudian, gunakan data yang diterima untuk mengisi EditText atau di tempat lainnya
        binding.edtNama.setText(nama)
        binding.edtDesc.setText(deskripsi)
        binding.edtDate.setText(tanggal)

        binding.btnAdd.setOnClickListener {
            onUpdateClicked() // Panggil fungsi onUpdateClicked saat tombol diklik
        }

        binding.btnAdd.setOnClickListener {
            val nama = binding.edtNama.text.toString()
            val deskprsi = binding.edtDesc.text.toString()
            val tanggal = binding.edtDate.text.toString()
            val newBudget = Detail(nama = nama, description = deskprsi,
                date = tanggal)
            if (idFeedback.isNotEmpty()) {
                newBudget.id = idFeedback
                updateFeedback(newBudget)
            } else {
                addBudget(newBudget)
            }
        }
    }

    private fun updateFeedback(feedback: Detail) {
        feedback.id = idFeedback
        budgetCollectionRef.document(idFeedback)
            .set(feedback)
            .addOnSuccessListener {
                Log.d("SecondActivity", "Successfully Updating Feedback")
                navigateToMainActivity()
            }
            .addOnFailureListener {
                Log.d("MainActivity", "Error updating feedback: ", it)
            }
    }

    private fun addBudget(feedback: Detail) {
        budgetCollectionRef.add(feedback)
            .addOnSuccessListener { documentReference ->
                val createdFeedbackId = documentReference.id
                feedback.id = createdFeedbackId
                documentReference.set(feedback)
                    .addOnSuccessListener {
                        Log.d("SecondActivity", "Successfully Adding Feedback")
                        navigateToMainActivity()
                    }
                    .addOnFailureListener {
                        Log.d("MainActivity", "Error adding feedback ID: ", it)
                    }
            }
            .addOnFailureListener {
                Log.d("MainActivity", "Error adding feedback: ", it)
            }
    }

    private fun onUpdateClicked() {
        val nama = binding.edtNama.text.toString()
        val deskripsi = binding.edtDesc.text.toString()
        val tanggal = binding.edtDate.text.toString()
        val updateFeedback = Detail(nama = nama, description = deskripsi,
            date = tanggal)

        if (idFeedback.isNotEmpty()) {
            updateFeedback.id = idFeedback
            updateFeedback(updateFeedback)
        } else {
            addBudget(updateFeedback)
        }
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

}