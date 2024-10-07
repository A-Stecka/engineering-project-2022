package com.zpi.view.search

import android.annotation.SuppressLint
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.zpi.R
import com.zpi.databinding.ActivitySearchBinding
import com.zpi.viewmodel.SearchViewModel


class SearchActivity : AppCompatActivity() {

    private lateinit var viewModel: SearchViewModel
    private lateinit var binding: ActivitySearchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[SearchViewModel::class.java]
        handleIntent(intent)
    }

    @SuppressLint("MissingSuperCall")
    override fun onNewIntent(intent: Intent) {
        setIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        intent.getStringExtra(SearchManager.QUERY)?.also { query ->
            viewModel.searchForStories(query)
            viewModel.setQuery(query)
            val sharedPref = getSharedPreferences(getString(R.string.prefs_for_all), Context.MODE_PRIVATE);
            val savedQueries: MutableSet<String>? = sharedPref.getStringSet(getString(R.string.saved_search_queries), setOf())
            with(sharedPref.edit()) {
                val queries = mutableSetOf<String>()
                if (savedQueries != null) {
                    queries.addAll(savedQueries)
                }
                queries.add(query)
                this.putStringSet(getString(R.string.saved_search_queries), queries)
                this.apply()
            }
        }
    }
}