package com.kode.test

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.*
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.recipe_list_view_holder.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap


class MainActivity : AppCompatActivity() {
    private var viewModel: MainViewModel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel = ViewModelProvider(this, MainViewModel.factory).get(MainViewModel::class.java)
        viewModel?.adapter?.observe(this) {
            list.adapter = it
            list.visibility = View.VISIBLE
            progress.visibility = View.GONE
        }
    }


    @SuppressLint("RestrictedApi")
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (menu is MenuBuilder)
            menu.setOptionalIconsVisible(true)
        menuInflater.inflate(R.menu.main_menu, menu)
        val sortTypeMenuItems = HashMap<DataProcessor.SortType, MenuItem>(4)
        with(sortTypeMenuItems) {
            put(DataProcessor.SortType.TIME_DESC, menu.findItem(R.id.time_desc))
            put(DataProcessor.SortType.TIME_ACS, menu.findItem(R.id.time_asc))
            put(DataProcessor.SortType.NAME_ACS, menu.findItem(R.id.name_asc))
            put(DataProcessor.SortType.NAME_DESC, menu.findItem(R.id.name_desc))
        }
        val searchView = (menu.findItem(R.id.search).actionView as SearchView)
        viewModel?.filterState?.observe(this) {
            sortTypeMenuItems.values.forEach { it.isChecked = false }
            sortTypeMenuItems[it.sortType]?.isChecked = true
            searchView.setQuery(it.query, false)
        }
        searchView.setOnQueryTextListener(viewModel)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.renew -> {
                viewModel?.renew()
                true
            }
            R.id.name_asc -> {
                viewModel?.filterState?.changeSortType(DataProcessor.SortType.NAME_ACS)
                true
            }
            R.id.name_desc -> {
                viewModel?.filterState?.changeSortType(DataProcessor.SortType.NAME_DESC)
                true
            }
            R.id.time_asc -> {
                viewModel?.filterState?.changeSortType(DataProcessor.SortType.TIME_ACS)
                true
            }
            R.id.time_desc -> {
                viewModel?.filterState?.changeSortType(DataProcessor.SortType.TIME_DESC)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    class RecipeListAdapter(
        private val data: Array<RecipeList>,
        private val filterState: LiveData<DataProcessor>
    ) :
        RecyclerView.Adapter<RecipeListViewHolder>(), Observer<DataProcessor> {

        var processedData = data.clone()

        override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
            super.onAttachedToRecyclerView(recyclerView)
            filterState.observe(recyclerView.context as LifecycleOwner, this)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeListViewHolder {
            return RecipeListViewHolder(parent)
        }

        override fun onBindViewHolder(holder: RecipeListViewHolder, position: Int) {
            holder.fill(processedData[position])
        }

        override fun getItemCount(): Int {
            return processedData.size
        }

        override fun onChanged(t: DataProcessor) {
            processedData = t.processData(data)
            notifyDataSetChanged()
        }
    }

    class RecipeListViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.recipe_list_view_holder, parent, false)
    ) {

        fun fill(value: RecipeList) {
            itemView.name.text = value.name
            itemView.rating.rating = value.difficulty.toFloat()
            itemView.image_count.setImageResource(
                image_count_icons[
                        (value.images.size - 1).coerceAtMost(9).coerceAtLeast(0)
                ]
            )
            if (value.description == null)
                itemView.description.visibility = View.GONE
            else {
                itemView.description.visibility = View.VISIBLE
                itemView.description.text = value.description
            }
            itemView.lastUpdated.text = dateFormat.format(Date(value.lastUpdated * 1000L))

            itemView.setOnClickListener {
                val intent = Intent(it.context, DetailActivity::class.java)
                intent.putExtra("uuid", value.uuid)
                it.context.startActivity(intent)
            }
            Picasso.with(itemView.context)
                .load(value.images.first())
                .error(R.drawable.error)
                .placeholder(android.R.drawable.ic_menu_report_image)
                .centerCrop()
                .fit()
                .into(itemView.image)
        }

    }

    companion object {
        private val dateFormat = SimpleDateFormat("dd/MM/yyyy hh:MM")
        private val image_count_icons = arrayListOf(
            R.drawable.vector_image_count_1,
            R.drawable.vector_image_count_2,
            R.drawable.vector_image_count_3,
            R.drawable.vector_image_count_4,
            R.drawable.vector_image_count_5,
            R.drawable.vector_image_count_6,
            R.drawable.vector_image_count_7,
            R.drawable.vector_image_count_8,
            R.drawable.vector_image_count_9,
            R.drawable.vector_image_count_9_plus,
        )

    }

}