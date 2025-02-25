package com.dicoding.picodiploma.loginwithanimation.view.main

import android.annotation.SuppressLint
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityMainBinding
import com.dicoding.picodiploma.loginwithanimation.view.ViewModelFactory
import com.dicoding.picodiploma.loginwithanimation.view.adapter.StoryAdapter
import com.dicoding.picodiploma.loginwithanimation.view.detail.DetailActivity
import com.dicoding.picodiploma.loginwithanimation.view.map.MapsActivity
import com.dicoding.picodiploma.loginwithanimation.view.welcome.WelcomeActivity
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: StoryAdapter
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarmain)
        supportActionBar?.title = "Story App"
        binding.toolbarmain.setTitleTextColor(ContextCompat.getColor(this, android.R.color.white))

        swipeRefreshLayout = binding.swipeRefresh
        binding.swipeRefresh.setOnRefreshListener {
            adapter.refresh()
            binding.swipeRefresh.isRefreshing = false
        }

        setupView()
        setupAction()
        observeViewModel()
    }

    override fun onResume() {
        super.onResume()
        val isFromCreateActivity = intent.getBooleanExtra("isFromCreateActivity", false)
        if (isFromCreateActivity) {
            adapter.refresh()
            adapter.addLoadStateListener { loadState ->
                if (loadState.refresh is LoadState.NotLoading) {
                    binding.rvStories.scrollToPosition(0)
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setupView() {
        adapter = StoryAdapter { storyItem ->
            val intent = Intent(this, DetailActivity::class.java).apply {
                putExtra("id", storyItem.id)
                putExtra("name", storyItem.name)
                putExtra("description", storyItem.description)
                putExtra("photo_url", storyItem.photoUrl)
            }
            startActivity(intent)
        }
        binding.rvStories.layoutManager = LinearLayoutManager(this)
        binding.rvStories.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter { adapter.retry() }
        )
        adapter.addLoadStateListener { loadState ->
            binding.loadingIndicator.visibility = if (loadState.refresh is LoadState.Loading) {
                View.VISIBLE
            } else {
                View.GONE
            }

            val isNetworkAvailable = isNetworkAvailable()
            val isEmpty = adapter.itemCount == 0

            if (isEmpty) {
                binding.emptyMessage.apply {
                    visibility = View.VISIBLE
                    text = if (!isNetworkAvailable) {
                        "Tidak ada koneksi internet"
                    } else {
                        "Tidak ada data tersedia"
                    }
                }
                binding.rvStories.visibility = View.GONE
            } else {
                binding.emptyMessage.visibility = View.GONE
                binding.rvStories.visibility = View.VISIBLE
            }
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCapabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        return networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    }

    private fun setupAction() {
        binding.logoutButton.setOnClickListener {
            showLogoutConfirmationDialog()
        }
        binding.AddStoryButton.setOnClickListener {
            val intent = Intent(this, CreateActivity::class.java)
            startActivity(intent)
        }
    }

    private fun observeViewModel() {
        viewModel.session.observe(this) { user ->
            if (user.token.isEmpty()) {
                navigateToWelcome()
            }
        }

        viewModel.stories.observe(this) { pagingData ->
            lifecycleScope.launch {
                adapter.submitData(pagingData)

                if (adapter.itemCount == 0) {
                    binding.emptyMessage.visibility = View.VISIBLE
                } else {
                    binding.emptyMessage.visibility = View.GONE
                }
            }
        }

        viewModel.logoutStatus.observe(this) { isLoggedOut ->
            if (isLoggedOut) {
                navigateToWelcome()
            } else {
                Toast.makeText(this, "Logout failed. Please try again.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showLogoutConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Konfirmasi Logout")
            .setMessage("Apakah Anda yakin ingin logout?")
            .setPositiveButton("Ya") { dialog, _ ->
                viewModel.logout()
                dialog.dismiss()
            }
            .setNegativeButton("Batal") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun navigateToWelcome() {
        val intent = Intent(this, WelcomeActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_map -> {
                binding.loadingIndicator.visibility = View.VISIBLE
                lifecycleScope.launch {
                    kotlinx.coroutines.delay(1000) // Delay 1 detik
                    val intent = Intent(this@MainActivity, MapsActivity::class.java) // Pastikan konteks benar
                    startActivity(intent)
                    binding.loadingIndicator.visibility = View.GONE
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}

