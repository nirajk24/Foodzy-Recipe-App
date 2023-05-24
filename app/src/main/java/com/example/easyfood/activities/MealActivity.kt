package com.example.easyfood.activities

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.easyfood.R
import com.example.easyfood.databinding.ActivityMealBinding
import com.example.easyfood.db.MealDatabase
import com.example.easyfood.fragments.HomeFragment
import com.example.easyfood.pojo.Meal
import com.example.easyfood.viewModel.MealViewModel
import com.example.easyfood.viewModel.MealViewModelFactory
import com.google.android.material.snackbar.Snackbar

class MealActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMealBinding

    // variables for Meal values
    private lateinit var mealId: String
    private lateinit var mealName: String
    private lateinit var mealThumb: String
    private lateinit var youtubeLink: String

    // ViewModel declaration
    private lateinit var mealMvvm: MealViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMealBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.statusBarColor = getColor(R.color.accent)

        // Initializing the instance of MealViewModel
        // mealMvvm = ViewModelProviders.of(this)[MealViewModel::class.java]

        // Creating an instance of mealDatabase
        val mealDatabase = MealDatabase.getInstance(this)

        // Creating instance of ViewModel Factory to pass to viewModel provider
        val viewModelFactory = MealViewModelFactory(mealDatabase)
        mealMvvm = ViewModelProvider(this, viewModelFactory)[MealViewModel::class.java]

        // Initial Data
        getMealInformationFromIntent()  // -> Getting id, name and url of meal
        setInformationInViews()  // -> Setting id, name and image through url

        // Showing the loading animation
        loadingCase()

        // Remaining Data
        mealMvvm.getMealDetailByID(mealId)  // -> Getting all the meal details using id
        observeMealDetailsLiveData()  // -> Setting up remaining views

        // Setting up onclick Listener on Youtube icon
        onYoutubeImageClick()

        // Adding meal to favorites
        onFavoriteClick()

    }

    private fun onFavoriteClick() {
        binding.fabFavorites.setOnClickListener {

            binding.fabFavorites.setBackgroundDrawable(getDrawable(R.color.accent))
            mealToSave?.let {
                mealMvvm.insertMeal(it)
                Toast.makeText(this, "Meal Saved", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Function to redirect user to youtube link
    private fun onYoutubeImageClick() {
        binding.imgYoutube.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(youtubeLink))
            startActivity(intent)
        }
    }

    // Getting information from intent and storing in variables
    private fun getMealInformationFromIntent() {
        val intent = intent
        mealId = intent.getStringExtra(HomeFragment.MEAL_ID)!!
        mealName = intent.getStringExtra(HomeFragment.MEAL_NAME)!!
        mealThumb = intent.getStringExtra(HomeFragment.MEAL_THUMB)!!

    }

    // Setting the initial information in Views
    private fun setInformationInViews() {
        // Using Glide library to load image in Imageview
        Glide.with(applicationContext)
            .load(mealThumb)
            .centerCrop()
            .into(binding.imgMealDetails)

        // Setting up Title
        binding.apply {
            collapsingToolbar.title = mealName
        }
    }

    // Saving meal data to a var to pass into room database
    private var mealToSave: Meal? = null
    // To set up the remaining data
    private fun observeMealDetailsLiveData() {
        // Observing Livedata to setup other fields - Instructions, category, area...
        mealMvvm.observerMealDetailsLiveData().observe(this) { meal ->
            onResponseCase()  // -> Hiding loading animation and showing other views

            mealToSave = meal  // -> saving meal instance into another var to pass to room database

            val ingredients = "• " + meal.strIngredient1 + ": " + meal.strMeasure1 + "\n" +
                    "• " + meal.strIngredient2 + ": " + meal.strMeasure2 + "\n" +
                    "• " + meal.strIngredient3 + ": " + meal.strMeasure3 + "\n" +
                    "• " + meal.strIngredient4 + ": " + meal.strMeasure4 + "\n" +
                    "• " + meal.strIngredient5 + ": " + meal.strMeasure5 + "\n" +
                    "• " + meal.strIngredient6 + ": " + meal.strMeasure6 + "\n" +
                    "• " + meal.strIngredient7 + ": " + meal.strMeasure7 + "\n"
//                    "• " + meal.strIngredient8 + ": " + meal.strMeasure8 + "\n" +
//                    "• " + meal.strIngredient9 + ": " + meal.strMeasure9 + "\n" +
//                    "• " + meal.strIngredient10+ ": " + meal.strMeasure10 + "\n" +
//                    "• " + meal.strIngredient11 + ": " + meal.strMeasure11 + "\n" +
//                    "• " + meal.strIngredient12 + ": " + meal.strMeasure12

                    // Setting up views on meal activity
            binding.apply {
                tvMealCategory.text = "Category : ${meal.strCategory}"
                tvMealArea.text = "Area : ${meal.strArea}"
                tvIngredients.text = ingredients.trim()
                tvSteps.text = meal.strInstructions.toString()
            }

            // Setting up youtube link
            youtubeLink = meal.strYoutube.toString()
        }
    }

    // Setting up Loading animation
    private fun loadingCase(){  // -> Function to hide views while loading
        binding.apply {
            progressBar.visibility = View.VISIBLE

            fabFavorites.visibility = View.INVISIBLE
            tvMealCategory.visibility = View.INVISIBLE
            tvMealArea.visibility = View.INVISIBLE
            imgYoutube.visibility = View.INVISIBLE

            tvSteps.visibility = View.INVISIBLE
            tvStepsTitle.visibility = View.INVISIBLE
            tvIngredients.visibility = View.INVISIBLE
            tvIngredientsTitle.visibility = View.INVISIBLE
        }
    }

    private fun onResponseCase(){  // -> Function to hide loading when data is received
        binding.apply {
            progressBar.visibility = View.INVISIBLE

            fabFavorites.visibility = View.VISIBLE
            tvMealCategory.visibility = View.VISIBLE
            tvMealArea.visibility = View.VISIBLE
            imgYoutube.visibility = View.VISIBLE

            tvSteps.visibility = View.VISIBLE
            tvStepsTitle.visibility = View.VISIBLE
            tvIngredients.visibility = View.VISIBLE
            tvIngredientsTitle.visibility = View.VISIBLE
        }
    }




}