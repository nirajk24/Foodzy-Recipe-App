package com.example.easyfood.fragments

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.easyfood.R
import com.example.easyfood.activities.CategoryMealsActivity
import com.example.easyfood.activities.MainActivity
import com.example.easyfood.activities.MealActivity
import com.example.easyfood.adapters.CategoriesAdapter
import com.example.easyfood.adapters.MostPopularAdapter
import com.example.easyfood.databinding.FragmentHomeBinding
import com.example.easyfood.fragments.bottomsheet.MealBottomSheetFragment
import com.example.easyfood.pojo.MealsByCategory
import com.example.easyfood.pojo.Meal
import com.example.easyfood.pojo.dummyData
import com.example.easyfood.viewModel.HomeViewModel
import kotlin.random.Random

class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewModel: HomeViewModel

    // randomMeal to store the data of Random Meal appearing on Top of page
    private lateinit var randomMeal: Meal

    // Adapter variable for Popular meals recycler view
    private lateinit var popularItemsAdapter: MostPopularAdapter
    // Adapter variable for categories recycler view
    private lateinit var categoriesAdapter: CategoriesAdapter

    companion object {  // -> Constants for sending meal data through intent
        // For MealActivity
        const val MEAL_ID = "com.example.easyfood.fragments.idMeal"
        const val MEAL_NAME = "com.example.easyfood.fragments.nameMeal"
        const val MEAL_THUMB = "com.example.easyfood.fragments.thumbMeal"
        const val MEAL_AREA = "com.example.easyfood.fragments.areaMeal"
        // For CategoryMealActivity
        const val CATEGORY_NAME = "com.example.easyfood.fragments.categoryName"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initializing the instance of HomeViewModel from MainActivity
        viewModel = (activity as MainActivity).viewModel


        // Initializing the instance of MostPopularAdapter
        popularItemsAdapter = MostPopularAdapter()
        // Initializing the instance of CategoriesAdapter
        categoriesAdapter = CategoriesAdapter()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        // Starting shimmer effect
        binding.shimmerLayoutHome.startShimmer()
        // Stops in HomeViewModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        preparePopularItemsRecyclerView()  // -> Setting up recycler view to show popular meals
        prepareCategoriesRecyclerView()  // -> Setting up recycler view to show categories

        // Calling the getRandomMeal method of HomeViewModel
        viewModel.getRandomMeal(binding)
        observeRandomMealLiveData()  // -> Setting up observer on the randomMeal
        onRandomMealClick()  // -> Setting up onClick Function on Random Card

        // Calling getPopularItems to set popularItemsList in ViewModel
        viewModel.getPopularItems()
        observePopularItemLiveData()  // -> Setting up observer on the popularMealsList
        onPopularItemClick()  // -> Setting up the on click method on popular items
        onPopularItemLongClick()  // -> Setting up the on long click method on popular items

//        // Calling getCategories method to set categoriesList in ViewModel
        viewModel.getCategories()
        observeCategoriesLiveData()  // -> Setting up observer on the categoriesList
        onCategoryItemClick() // -> Setting up onClick listener on category items

        onSearchItemClick()

        // Causing issue with backstack
        // onCategoriesSeeAllClick()
        binding.tvCategoriesSeeAll.visibility = View.GONE
//
    }

    private fun onCategoriesSeeAllClick() {
        binding.tvCategoriesSeeAll.setOnClickListener {
            it.findNavController().navigate(R.id.action_homeFragment_to_categoriesFragment)
        }
    }

    private fun onSearchItemClick() {
        binding.imgSearch.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_searchFragment)
        }
    }


    // Observe the RandomMeal and Update UI using Glide
    private fun observeRandomMealLiveData() {
        viewModel.observeRandomMealLiveData().observe(viewLifecycleOwner) { meal ->
            // Glide library to load Image
            Glide.with(this@HomeFragment)
                .load(meal!!.strMealThumb) // Loading the image from url
                .centerCrop() // Cropping the image to fit in view
                .into(binding.imgRandomMeal) // Show the image in imageView

            val rand = Random(10).nextInt(20)

            binding.tvRandomMealName.text = meal.strMeal
            binding.tvRandomMealRating.text = " " + dummyData[rand].rating
            binding.tvRandomMealDetails.text =
                meal.strCategory + " • " + meal.strArea + " • " + dummyData[rand].time

            this.randomMeal = meal  // -> storing the value of current random meal


            // Hiding shimmer effect and show main layout after data is received
            Handler(Looper.getMainLooper()).postDelayed({
                //Do something after 100ms
                // Hide shimmer effect
                binding.shimmerLayoutHome.stopShimmer()
                binding.shimmerLayoutHome.visibility = View.GONE
                // Make the view visible again
                binding.homeMain.visibility = View.VISIBLE
            }, 200)
        }
    }

    // Function to set up On Click listener on Random Meal Card
    private fun onRandomMealClick() {
        binding.cvRandomMeal.setOnClickListener {
            if (randomMeal == null) {
                Toast.makeText(activity, "Turn on the Net fucker", Toast.LENGTH_LONG).show()

            } else {
                // Intent to navigate to Meal Activity
                val intent = Intent(activity, MealActivity::class.java)
                intent.apply {  // Passing values through intent
                    putExtra(MEAL_ID, randomMeal.idMeal)
                    putExtra(MEAL_NAME, randomMeal.strMeal)
                    putExtra(MEAL_THUMB, randomMeal.strMealThumb)
                    putExtra(MEAL_AREA, randomMeal.strArea)
                    putExtra(CATEGORY_NAME, randomMeal.strCategory)

                    startActivity(this)
                }
            }
        }
    }


    // Setting up recycler view for popular items
    private fun preparePopularItemsRecyclerView() {
        binding.rvPopularMeals.apply {
            layoutManager = LinearLayoutManager(
                activity,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            adapter = popularItemsAdapter
        }
    }


    // Observe the RandomMeal and set data into RecyclerView adapter
    private fun observePopularItemLiveData() {
        viewModel.observePopularItemsLiveData().observe(viewLifecycleOwner) { mealsList ->
            popularItemsAdapter.setMeals(mealsList = mealsList as ArrayList<MealsByCategory>)
        }
    }

    // Function to navigate to MealActivity from popular item list onClick
    private fun onPopularItemClick() {
        popularItemsAdapter.onItemClick = { meal ->
            val intent = Intent(activity, MealActivity::class.java)
            intent.apply {
                putExtra(MEAL_ID, meal.idMeal)
                putExtra(MEAL_NAME, meal.strMeal)
                putExtra(MEAL_THUMB, meal.strMealThumb)


                startActivity(intent)
            }
        }
    }

    private fun onPopularItemLongClick() {
        popularItemsAdapter.onLongItemClick = {
            val mealBottomSheetFragment = MealBottomSheetFragment.newInstance(it.idMeal)
            mealBottomSheetFragment.show(childFragmentManager, "Meal Info")
        }
    }

    // Setting up recycler view for categories
    private fun prepareCategoriesRecyclerView() {
        binding.rvCategories.apply {
            layoutManager = GridLayoutManager(context, 3, GridLayoutManager.VERTICAL, false)
            adapter = categoriesAdapter
        }
    }

    // Observe the Categories and set up recycler view
    private fun observeCategoriesLiveData() {
        viewModel.observeCategoriesLiveData().observe(viewLifecycleOwner) { categories ->
            categoriesAdapter.setCategoryList(categories)

        }
    }

    // Function to navigate to CategoryMealsActivity from popular item list onClick
    private fun onCategoryItemClick() {
        categoriesAdapter.onItemClick = { category ->
            val intent = Intent(activity, CategoryMealsActivity::class.java)
            intent.putExtra(CATEGORY_NAME, category.strCategory)
            startActivity(intent)
        }
    }

}
