package com.example.easyfood.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.easyfood.databinding.CategoryItem2Binding
import com.example.easyfood.databinding.CategoryItemBinding
import com.example.easyfood.pojo.Category
import com.example.easyfood.pojo.categoryImage

class CategoriesAdapter : RecyclerView.Adapter<CategoriesAdapter.CategoriesViewHolder>() {

    // ArrayList to store the categories items (name, url, details, etc)
    private var categoryList = ArrayList<Category>()

    //
    var onItemClick: ((Category) -> Unit)? = null

    // function to set category list -> called from HomeFragment
    fun setCategoryList(categoriesList: List<Category>){
        this.categoryList = categoriesList as ArrayList<Category>
        notifyDataSetChanged()
    }


    // Below codes are for ViewHolder (repeated many times)
    inner class CategoriesViewHolder(val binding: CategoryItem2Binding)
        : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriesViewHolder {
        return CategoriesViewHolder(
            CategoryItem2Binding.inflate(
                LayoutInflater.from(parent.context)
            )
        )
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    override fun onBindViewHolder(holder: CategoriesViewHolder, position: Int) {

        // Loading image for categoriesItem
//        Glide.with(holder.itemView)
//            .load(categoryList[position].strCategoryThumb)
//            .into(holder.binding.imgCategory)

        holder.binding.imgCategory.setImageResource(categoryImage[position])

        // 1 - chicken, 6 - pasta(lil),

        // Setting up name
        holder.binding.tvCategoryName.text = categoryList[position].strCategory

        // lambda variable for onClick method
        holder.itemView.setOnClickListener {
            onItemClick!!.invoke(categoryList[position])
        }


    }
}