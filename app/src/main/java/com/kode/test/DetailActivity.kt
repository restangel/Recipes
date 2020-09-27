package com.kode.test

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.photo_view_holder.view.*
import kotlinx.android.synthetic.main.recipe_brief_view_holder.view.*


class DetailActivity : AppCompatActivity() {

    private var viewModel: DetailViewModel? = null
    private var pageChangeListener: ViewPager2.OnPageChangeCallback? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        pager.offscreenPageLimit = 2

        val uuid = intent.getStringExtra("uuid") ?: throw Exception("Have no uuid of recipe in extras")

        viewModel =
            ViewModelProvider(this, DetailViewModel.DetailViewModelFactory(uuid))
                .get(DetailViewModel::class.java)

        viewModel?.recipe?.observe(this) { it ->
            content.visibility = View.VISIBLE
            progress.visibility = View.GONE
            name.text = it.name
            rating.rating = it.difficulty.toFloat()
            if (it.description == null)
                description.visibility = View.GONE
            else {
                description.visibility = View.VISIBLE
                description.text = it.description
            }
            instructions.text = if (Build.VERSION.SDK_INT >= 24)
                Html.fromHtml(it.instructions ?: "", Html.FROM_HTML_MODE_COMPACT)
            else
                Html.fromHtml(it.instructions ?: "")
            pager.adapter = PhotoAdapter(it.images, onPhotoClick)
            similarList.adapter = SimilarAdapter(it.similar, onSimilarClick)
            image_count.text = String.format("%d/%d", 1, it.images.size)
            val pageChangeListener = object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    image_count.text = String.format("%d/%d", position + 1, it.images.size)
                }
            }
            pager.registerOnPageChangeCallback(pageChangeListener)
            this.pageChangeListener.let {
                if (it != null)
                    pager.unregisterOnPageChangeCallback(it)
            }
            this.pageChangeListener = pageChangeListener
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == android.R.id.home) {
            onBackPressed()
            true
        } else
            super.onOptionsItemSelected(item)
    }

    private val onPhotoClick = { url: String ->
        val intent = Intent(this, PhotoActivity::class.java)
        intent.putExtra("url", url)
        startActivity(intent)
    }

    private val onSimilarClick = { it: RecipeBrief ->
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra("uuid", it.uuid)
        startActivity(intent)
    }

    override fun onStop() {
        super.onStop()
        this.pageChangeListener.let {
            if (it != null)
                pager.unregisterOnPageChangeCallback(it)
        }
    }

    class SimilarAdapter(
        private val similar: Array<RecipeBrief>,
        private val onSimilarClick: (RecipeBrief) -> Unit
    ) : RecyclerView.Adapter<SimilarAdapter.SimilarViewHolder>() {
        class SimilarViewHolder(container: ViewGroup) :
            RecyclerView.ViewHolder(
                LayoutInflater.from(container.context)
                    .inflate(R.layout.recipe_brief_view_holder, container, false)
            ) {
            fun fill(value: RecipeBrief, onSimilarClick: (RecipeBrief) -> Unit) {
                itemView.name.text = value.name
                itemView.setOnClickListener {
                    onSimilarClick(value)
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimilarViewHolder {
            return SimilarViewHolder(parent)
        }

        override fun onBindViewHolder(holder: SimilarViewHolder, position: Int) {
            holder.fill(similar[position], onSimilarClick)
        }

        override fun getItemCount(): Int {
            return similar.size
        }
    }

    class PhotoAdapter(
        private val urlList: Array<String>,
        private val onPhotoClick: (String) -> Unit
    ) :
        RecyclerView.Adapter<PhotoAdapter.PhotoHolder>() {
        class PhotoHolder(container: ViewGroup, private val onPhotoClick: (String) -> Unit) :
            RecyclerView.ViewHolder(
                LayoutInflater.from(container.context)
                    .inflate(R.layout.photo_view_holder, container, false)
            ) {
            fun fill(url: String) {
                itemView.image.setOnClickListener {
                    onPhotoClick(url)
                }
                Picasso.with(itemView.context)
                    .load(url)
                    .fit()
                    .centerInside()
                    .error(R.drawable.error)
                    .placeholder(android.R.drawable.ic_menu_report_image)
                    .into(itemView.image)
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoHolder {
            return PhotoHolder(parent, onPhotoClick)
        }

        override fun onBindViewHolder(holder: PhotoHolder, position: Int) {
            holder.fill(urlList[position])
        }

        override fun getItemCount(): Int {
            return urlList.size
        }
    }


}