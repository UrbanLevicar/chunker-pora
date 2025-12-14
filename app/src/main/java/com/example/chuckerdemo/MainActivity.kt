package com.example.chuckerdemo

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.chuckerteam.chucker.api.Chucker
import com.example.chuckerdemo.api.RetrofitClient
import com.example.chuckerdemo.databinding.ActivityMainBinding
import com.example.chuckerdemo.server.ApiServer
import com.example.chuckerdemo.api.PostDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val apiServer = ApiServer()

    private lateinit var apiService: com.example.chuckerdemo.api.ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        apiService = RetrofitClient.createApiService(this)

        startApiServer()
        setupClickListeners()
    }

    private fun startApiServer() {
        lifecycleScope.launch {
            showLoading("🚀 Zaganjam API server...")
            apiServer.start(port = 8080)
            delay(1000) // da se server zažene
            showResult("API Server teče na http://localhost:8080\n\nPripravljeno za testiranje!")
            Toast.makeText(this@MainActivity, "API Server zagnаn!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupClickListeners() {

        // GET vsi posti
        binding.btnGetPosts.setOnClickListener {
            lifecycleScope.launch {
                try {
                    showLoading("Nalagam poste...")
                    val response = withContext(Dispatchers.IO) {
                        apiService.getPosts()
                    }

                    if (response.isSuccessful) {
                        val posts = response.body()
                        showResult("Pridobljeno ${posts?.size} postov\n\n${posts?.joinToString("\n\n") {
                            "ID: ${it.id}\nTitle: ${it.title}\nBody: ${it.body}"
                        }}")
                    } else {
                        showError("Napaka: ${response.code()}")
                    }
                } catch (e: Exception) {
                    showError("Napaka: ${e.message}")
                }
            }
        }

        // POST calculate factorial
        binding.btnCalculateFactorial.setOnClickListener {
            lifecycleScope.launch {
                try {
                    showLoading("Računam faktoriel števila 10...")
                    val response = withContext(Dispatchers.IO) {
                        apiService.calculateFactorial(mapOf("number" to 10))
                    }

                    if (response.isSuccessful) {
                        val result = response.body()
                        showResult("Faktoriel števila 10 je: ${result?.factorial}")
                    } else {
                        showError("Napaka: ${response.code()}")
                    }
                } catch (e: Exception) {
                    showError("Napaka: ${e.message}")
                }
            }
        }

        // GET en post
        binding.btnGetPost.setOnClickListener {
            lifecycleScope.launch {
                try {
                    showLoading("Nalagam post z ID=1...")
                    val response = withContext(Dispatchers.IO) {
                        apiService.getPost(1)
                    }

                    if (response.isSuccessful) {
                        val post = response.body()
                        showResult("Post pridobljen:\n\nID: ${post?.id}\nTitle: ${post?.title}\nBody: ${post?.body}\nCreated: ${post?.createdAt}")
                    } else {
                        showError("Napaka: ${response.code()}")
                    }
                } catch (e: Exception) {
                    showError("Napaka: ${e.message}")
                }
            }
        }

        // POST nov post
        binding.btnCreatePost.setOnClickListener {
            lifecycleScope.launch {
                try {
                    showLoading("Ustvarjam nov post...")
                    val newPost = PostDto(
                        id = 0,
                        title = "Nov post iz aplikacije",
                        body = "To je testna vsebina, ustvarjena ob ${System.currentTimeMillis()}",
                        userId = 1
                    )

                    val response = withContext(Dispatchers.IO) {
                        apiService.createPost(newPost)
                    }

                    if (response.isSuccessful) {
                        val created = response.body()
                        showResult("Post ustvarjen!\n\nID: ${created?.id}\nTitle: ${created?.title}\nCreated: ${created?.createdAt}")
                    } else {
                        showError("Napaka: ${response.code()}")
                    }
                } catch (e: Exception) {
                    showError("Napaka: ${e.message}")
                }
            }
        }

        // GET uporabniki
        binding.btnGetUsers.setOnClickListener {
            lifecycleScope.launch {
                try {
                    showLoading("Nalagam uporabnike...")
                    val response = withContext(Dispatchers.IO) {
                        apiService.getUsers()
                    }

                    if (response.isSuccessful) {
                        val users = response.body()
                        showResult("Pridobljeno ${users?.size} uporabnikov:\n\n${users?.joinToString("\n\n") {
                            "${it.name} (${it.email})"
                        }}")
                    } else {
                        showError("Napaka: ${response.code()}")
                    }
                } catch (e: Exception) {
                    showError("Napaka: ${e.message}")
                }
            }
        }

        // simulira napako API-ja
        binding.btnError.setOnClickListener {
            lifecycleScope.launch {
                try {
                    showLoading("Pošiljam zahtevek, ki bo povzročil napako...")
                    val response = withContext(Dispatchers.IO) {
                        apiService.getError()
                    }

                    showError("API vrnil napako ${response.code()}: ${response.message()}")
                } catch (e: Exception) {
                    showError("Napaka: ${e.message}")
                }
            }
        }

        // odpre chucker UI
        binding.btnOpenChucker.setOnClickListener {
            startActivity(Chucker.getLaunchIntent(this))
        }
    }

    private fun showLoading(message: String) {
        binding.tvResult.text = message
    }

    private fun showResult(result: String) {
        binding.tvResult.text = result
        Toast.makeText(this, "Uspešno! Preveri Chucker.", Toast.LENGTH_SHORT).show()
    }

    private fun showError(error: String) {
        binding.tvResult.text = error
        Toast.makeText(this, "Napaka! Preveri Chucker za detajle.", Toast.LENGTH_LONG).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        apiServer.stop()
    }
}