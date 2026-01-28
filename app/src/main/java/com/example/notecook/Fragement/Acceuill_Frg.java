package com.example.notecook.Fragement;

import static androidx.recyclerview.widget.RecyclerView.HORIZONTAL;
import static com.example.notecook.Activity.MainActivity.TAG_LOCAL;
import static com.example.notecook.Activity.MainActivity.TAG_REMOTE;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.example.notecook.Activity.MainActivity;
import com.example.notecook.Adapter.Adapter_RC_MenuCat;
import com.example.notecook.Adapter.Adapter_RC_RecipeDt;
import com.example.notecook.Dto.RecipesResponce;
import com.example.notecook.Model.Category_Recipe;
import com.example.notecook.Model.Recipe;
import com.example.notecook.R;
import com.example.notecook.Utils.Constants;
import com.example.notecook.ViewModel.SharedRecipeViewModel;
import com.example.notecook.ViewModel.IngredientsViewModel;
import com.example.notecook.ViewModel.RecipeViewModel;
import com.example.notecook.databinding.FragmentAcceuillFrgBinding;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class Acceuill_Frg extends Fragment {

    public LayoutInflater inflater;
    Category_Recipe mCategoryRecipe;
    private FragmentAcceuillFrgBinding binding;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecipeViewModel recipeVM;
    private IngredientsViewModel ingredientsVM;
    private Adapter_RC_RecipeDt adapter_rc_recipeDt;
    private LinearLayoutManager manager;
    public static Drawable defaultImagelike;
    public Drawable defaultImagenot;
    private SharedRecipeViewModel viewModel;

    public Acceuill_Frg() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentAcceuillFrgBinding.inflate(inflater, container, false);
        // Inflate the layout for this fragment
        binding.allTxt.setOnClickListener(view -> {
            ViewPager2 viewPager2 = getActivity().findViewById(R.id.vp2);
            viewPager2.setCurrentItem(2, false);
        });
        viewModel = new ViewModelProvider(requireActivity()).get(SharedRecipeViewModel.class);

        defaultImagelike = ContextCompat.getDrawable(requireContext(),R.drawable.ic_baseline_favorite_24);
        defaultImagenot = ContextCompat.getDrawable(requireContext(),R.drawable.ic_favorite_border_black_24dp);

        binding.seeMoreTxt.setOnClickListener(view -> {
            ViewPager2 viewPager2 = requireActivity().findViewById(R.id.vp2);
            viewPager2.setCurrentItem(2, false);
        });

        bindingRcV_categories(binding.RcCatMenu, true, getContext());
        recipeVM = new RecipeViewModel(getContext(), getActivity(),viewModel);
        recipeVM = new ViewModelProvider(this, recipeVM).get(RecipeViewModel.class);
        //Get All Ingredients Recipes
        ingredientsVM = new IngredientsViewModel(getContext(), getActivity(),viewModel);
        ingredientsVM.getAllIngredientsApi();
        fetchRecipe(1);

        binding.RcCatPopular.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView rv, int dx, int dy) {
                RecipesResponce recipesResponce=viewModel.getRemoteRecipesByPages().getValue();
                if (!rv.canScrollVertically(1) &&  recipesResponce.getPage()< recipesResponce.getTotalPages()) {
                    recipesResponce.setPage(recipesResponce.getPage()+1);
                    viewModel.getRemoteRecipesByPages().getValue().getRecipes().addAll(recipesResponce.getRecipes());
                    fetchRecipe(recipesResponce.getPage());
                }
            }
        });

        swipeRefreshLayout = binding.swipeRefreshLayout;
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Perform your data refreshing operations here
                // Simulate refresh delay (remove this in your actual code)
                //onResume();
                fetchRecipe(viewModel.getRemoteRecipesByPages().getValue().getPage());
                ingredientsVM.getAllIngredientsApi();

                new android.os.Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Finish refreshing
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 2000); // 2 seconds simulated refresh time (adjust as needed)
            }
        });

        binding.txtRecherche.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b) MainFragment.viewPager2.setCurrentItem(2,false);
            }
        });
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void fetchRecipe() {
        recipeVM.getRecipes().observe(getViewLifecycleOwner(), new Observer<List<Recipe>>() {
            @Override
            public void onChanged(@Nullable List<Recipe> recipeList) {
                if (recipeList != null) {
                    viewModel.setRemoteListRecipe(recipeList);
                    MainActivity.bindingRcV_recipes(binding.RcCatPopular,recipeList, true);
                    Toast.makeText(getContext(), "changed main " + "recipe by observe" + recipeList.size(), Toast.LENGTH_SHORT).show();
                } else
                    MainActivity.bindingRcV_recipes(binding.RcCatPopular,viewModel.getListRecipe().getValue(), true);
            }
        });
    }

    private void fetchRecipe(int page) {
        recipeVM.getRecipes(page, 10).observe(getViewLifecycleOwner(), new Observer<RecipesResponce>() {
            @Override
            public void onChanged(@Nullable RecipesResponce recipeList) {
                if (recipeList != null) {
                    viewModel.setremoteRecipesByPages(recipeList);
                   // bindingRcV_recipes(recipeList.getRecipes(), binding.RcCatPopular, true);
                    adapter_rc_recipeDt.addRecipes(recipeList.getRecipes());
                    Toast.makeText(getContext(), "changed main " + "recipe by observe" + recipeList.getRecipes().size(), Toast.LENGTH_SHORT).show();
                } else
                    MainActivity.bindingRcV_recipes( binding.RcCatPopular,viewModel.getRemoteRecipesByPages().getValue().getRecipes(), true);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void heart_click(ImageView IV, Context context) {
        Drawable defaultImagelike = ContextCompat.getDrawable(context, R.drawable.italian);

        IV.setOnClickListener(view -> {
            if (defaultImagelike != null && !(defaultImagelike.getConstantState() == IV.getDrawable().getConstantState())) {
                IV.setImageDrawable(defaultImagelike);
            }
        });
    }

    public void bindingRcV_categories(RecyclerView recyclerView, boolean isgarde, Context context) {
        List<Category_Recipe> list_categoryRecipes = new ArrayList<>();
        Adapter_RC_MenuCat adapter_rc_menuCat;
        List<Drawable> drs = new ArrayList<>();
        drs.add(ContextCompat.getDrawable(context, R.drawable.barbecue));
        drs.add(ContextCompat.getDrawable(context, R.drawable.breakfast));
        drs.add(ContextCompat.getDrawable(context, R.drawable.chicken));
        drs.add(ContextCompat.getDrawable(context, R.drawable.beef));
        drs.add(ContextCompat.getDrawable(context, R.drawable.brunch));
        drs.add(ContextCompat.getDrawable(context, R.drawable.dinner));
        drs.add(ContextCompat.getDrawable(context, R.drawable.wine));
        drs.add(ContextCompat.getDrawable(context, R.drawable.italian));

        for (int i = 0; i < 8; i++) {
            mCategoryRecipe = new Category_Recipe(Constants.DEFAULT_SEARCH_CATEGORIES[i], drs.get(i));
            list_categoryRecipes.add(mCategoryRecipe);
        }
        adapter_rc_menuCat = new Adapter_RC_MenuCat(list_categoryRecipes, true);
        GridLayoutManager manager = new GridLayoutManager(getContext(), 4);
        recyclerView.setLayoutManager(manager);
        adapter_rc_menuCat.notifyDataSetChanged();
        recyclerView.setAdapter(adapter_rc_menuCat);
    }


    public void bindingRcV_recipes(List<Recipe> list, RecyclerView mRecyclerView, boolean isgarde) {
        if (list != null && !list.isEmpty()) {
            adapter_rc_recipeDt = new Adapter_RC_RecipeDt(getContext(), getActivity(), viewModel,list, TAG_REMOTE);
            manager = new LinearLayoutManager(getContext());
            manager.setOrientation(HORIZONTAL);
            mRecyclerView.setLayoutManager(manager);
            mRecyclerView.setAdapter(adapter_rc_recipeDt);

        } else if (viewModel.getListRecipe() != null && viewModel.getListRecipe().getValue() != null && !viewModel.getListRecipe().getValue().isEmpty()) {
            adapter_rc_recipeDt = new Adapter_RC_RecipeDt(getContext(), getActivity(),viewModel, viewModel.getListRecipe().getValue(), TAG_LOCAL);
            manager = new LinearLayoutManager(getContext());
            manager.setOrientation(HORIZONTAL);
            mRecyclerView.setLayoutManager(manager);
            mRecyclerView.setAdapter(adapter_rc_recipeDt);
        }
        mRecyclerView.setHasFixedSize(true);
    }

}