package com.example.notecook.Fragement;

import static androidx.recyclerview.widget.RecyclerView.HORIZONTAL;
import static com.example.notecook.Utils.Constants.Remotelist_recipe;
import static com.example.notecook.Utils.Constants.TAG_LOCAL;
import static com.example.notecook.Utils.Constants.TAG_OFFLINE;
import static com.example.notecook.Utils.Constants.TAG_ONLINE;
import static com.example.notecook.Utils.Constants.TAG_REMOTE;
import static com.example.notecook.Utils.Constants.list_recipe;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
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
import com.example.notecook.Model.Category_Recipe;
import com.example.notecook.Model.Recipe;
import com.example.notecook.R;
import com.example.notecook.Utils.Constants;
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
    private FragmentActivity fragmentActivity;

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

        binding.seeMoreTxt.setOnClickListener(view -> {
            ViewPager2 viewPager2 = getActivity().findViewById(R.id.vp2);
            viewPager2.setCurrentItem(2, false);
        });

        bindingRcV_categories(binding.RcCatMenu, true, getContext());
        fragmentActivity = (FragmentActivity) getContext();
        recipeVM = new RecipeViewModel(getContext(), getActivity());
        recipeVM = new ViewModelProvider(this, recipeVM).get(RecipeViewModel.class);
        fetchRecipe();

        swipeRefreshLayout = binding.swipeRefreshLayout;
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Perform your data refreshing operations here
                // Simulate refresh delay (remove this in your actual code)
                //onResume();
                fetchRecipe();

                new android.os.Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Finish refreshing
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 2000); // 2 seconds simulated refresh time (adjust as needed)
            }
        });
        binding.txtRecherche.setOnClickListener(view -> {binding.seeMoreTxt.callOnClick();});

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        //Toast.makeText(getContext(), "changed " + Remotelist_recipe.getValue().size(), Toast.LENGTH_SHORT).show();
        //if(Remotelist_recipe.getValue()!=null) bindingRcV_recipes(Remotelist_recipe.getValue(), binding.RcCatPopular, true);
    }

    private void fetchRecipe() {
        recipeVM.getRecipes().observe(getViewLifecycleOwner(), new Observer<List<Recipe>>() {
            @Override
            public void onChanged(@Nullable List<Recipe> recipeList) {
                if (recipeList != null) {
                    Remotelist_recipe.setValue(recipeList);
                    bindingRcV_recipes(recipeList, binding.RcCatPopular, true);
                    Toast.makeText(getContext(), "changed main " + "recipe by observe" + recipeList.size(), Toast.LENGTH_SHORT).show();
                } else
                    bindingRcV_recipes(Remotelist_recipe.getValue(), binding.RcCatPopular, true);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    /*
     * This function allows to adapt different fragments in main fragment.
     */

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
        Adapter_RC_RecipeDt adapter_rc_recipeDt;
        if (list != null && !list.isEmpty()) {
            adapter_rc_recipeDt = new Adapter_RC_RecipeDt(getContext(), getActivity(), Remotelist_recipe.getValue(), TAG_REMOTE);
            LinearLayoutManager manager = new LinearLayoutManager(getContext());
            manager.setOrientation(HORIZONTAL);
            mRecyclerView.setLayoutManager(manager);
            mRecyclerView.setAdapter(adapter_rc_recipeDt);
        } else if (list_recipe != null && !list_recipe.getValue().isEmpty()) {
            adapter_rc_recipeDt = new Adapter_RC_RecipeDt(getContext(), getActivity(), list_recipe.getValue(), TAG_LOCAL);
            LinearLayoutManager manager = new LinearLayoutManager(getContext());
            manager.setOrientation(HORIZONTAL);
            mRecyclerView.setLayoutManager(manager);
            mRecyclerView.setAdapter(adapter_rc_recipeDt);
        }

    }

}