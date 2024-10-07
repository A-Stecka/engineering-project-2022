package com.zpi.view.search

import android.annotation.SuppressLint
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.database.MatrixCursor
import android.provider.BaseColumns
import android.util.Log
import android.widget.CursorAdapter
import android.widget.SearchView
import android.widget.SimpleCursorAdapter
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.zpi.R
import com.zpi.viewmodel.SearchViewModel
import java.util.*


class QueryHandler(private val context: Context, private val activity: FragmentActivity, private val searchView: SearchView, private val searchActive: Boolean = false) {
    private val tableName = "query"

    private val from = arrayOf(tableName)
    private val to = intArrayOf(android.R.id.text1)
    private val adapter: SimpleCursorAdapter = SimpleCursorAdapter(activity, android.R.layout.simple_list_item_1,
        null, from, to, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER)

    init {
        searchView.setOnQueryTextListener(OnQueryText())
        searchView.setOnSuggestionListener(OnQuerySuggestion())
        searchView.suggestionsAdapter = adapter
    }

    inner class OnQueryText : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?): Boolean {
            if(searchActive){
                if (query != null) {
                    ViewModelProvider(activity)[SearchViewModel::class.java].searchForStories(query)
                    return true
                }
                return false
            }
            val intent = Intent(activity, SearchActivity::class.java)
            intent.action = Intent.ACTION_SEARCH
            Log.i("SEARCH", query.toString())
            intent.putExtra(SearchManager.QUERY, query.toString())
            startActivity(context, intent, null)
            return true
        }

        override fun onQueryTextChange(text: String?): Boolean {
            text?.let { populateAdapter(it) }
            return true
        }

        private fun populateAdapter(query: String) {
            val sharedPref = activity.getSharedPreferences(activity.getString(R.string.prefs_for_all), Context.MODE_PRIVATE)
            val savedQueries: Set<String>? = sharedPref?.getStringSet(activity.getString(R.string.saved_search_queries), setOf())
            val c = MatrixCursor(arrayOf(BaseColumns._ID, tableName))
            for (i in savedQueries!!.indices) {
                if (savedQueries.elementAt(i).lowercase(Locale.getDefault()).startsWith(query.lowercase(Locale.getDefault()))) {
                    c.addRow(arrayOf<Any>(i, savedQueries.elementAt(i)))
                }
            }
            adapter.changeCursor(c)
        }
    }

    inner class OnQuerySuggestion : SearchView.OnSuggestionListener {
        override fun onSuggestionSelect(p0: Int): Boolean {
            return true
        }

        @SuppressLint("Range")
        override fun onSuggestionClick(position: Int): Boolean {
            val cursor: Cursor = adapter.getItem(position) as Cursor
            val txt: String = cursor.getString(cursor.getColumnIndex(tableName))
            searchView.setQuery(txt, true)
            return true
        }

    }
}