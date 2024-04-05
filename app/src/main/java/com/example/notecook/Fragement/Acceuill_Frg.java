package com.example.notecook.Fragement;

import static androidx.recyclerview.widget.RecyclerView.HORIZONTAL;
import static com.example.notecook.Utils.Constants.Remotelist_recipe;
import static com.example.notecook.Utils.Constants.TAG_OFFLINE;
import static com.example.notecook.Utils.Constants.TAG_ONLINE;
import static com.example.notecook.Utils.Constants.list_recipe;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.example.notecook.Adapter.Adapter_RC_MenuCat;
import com.example.notecook.Adapter.Adapter_RC_RecipeDt;
import com.example.notecook.Model.Categorie_Food;
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
    Categorie_Food categorie_food;
    private FragmentAcceuillFrgBinding binding;
    private SwipeRefreshLayout swipeRefreshLayout;
    private  RecipeViewModel recipeVM;
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

        bindingRcV_categories(binding.RcCatMenu, true);
        fragmentActivity = (FragmentActivity) getContext();
        recipeVM = new RecipeViewModel(getContext(),getActivity());
        recipeVM = new ViewModelProvider(this,recipeVM).get(RecipeViewModel.class);
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
        binding.txtRecherche.setOnTouchListener((view, motionEvent) -> binding.seeMoreTxt.callOnClick());
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        //Toast.makeText(getContext(), "changed " + Remotelist_recipe.getValue().size(), Toast.LENGTH_SHORT).show();
        if(Remotelist_recipe.getValue()!=null)
        bindingRcV_recipes(Remotelist_recipe.getValue(), binding.RcCatPopular, true);
    }

    private void fetchRecipe()
    {
        recipeVM.getRecipes().observe(getViewLifecycleOwner(),new Observer<List<Recipe>>() {
            @Override
            public void onChanged(@Nullable List<Recipe> recipeList) {
                if(recipeList!=null) {
                    Remotelist_recipe.setValue(recipeList);
                    bindingRcV_recipes(recipeList, binding.RcCatPopular, true);
                    Toast.makeText(getContext(), "changed main " + "recipe by observe" + recipeList.size(), Toast.LENGTH_SHORT).show();
                }
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

    private void heart_click(ImageView IV) {
        Drawable defaultImagelike = getResources().getDrawable(R.drawable.ic_baseline_favorite_24);
        IV.setOnClickListener(view -> {
            if (!defaultImagelike.getConstantState().equals(IV.getDrawable().getConstantState())) {
                IV.setImageDrawable(defaultImagelike);
            } else
                IV.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_border_black_24dp));
        });
    }
    // TODO Make category recipe git from server
    public void bindingRcV_categories(RecyclerView recyclerView, boolean isgarde) {
        List<Categorie_Food> list_categorie_foods = new ArrayList<>();
        Adapter_RC_MenuCat adapter_rc_menuCat;
        List<Drawable> drs = new ArrayList<>();
        drs.add(getResources().getDrawable(R.drawable.barbecue));
        drs.add(getResources().getDrawable(R.drawable.breakfast));
        drs.add(getResources().getDrawable(R.drawable.chicken));
        drs.add(getResources().getDrawable(R.drawable.beef));
        drs.add(getResources().getDrawable(R.drawable.brunch));
        drs.add(getResources().getDrawable(R.drawable.dinner));
        drs.add(getResources().getDrawable(R.drawable.wine));
        drs.add(getResources().getDrawable(R.drawable.italian));

        for (int i = 0; i < 8; i++) {
            categorie_food = new Categorie_Food(drs.get(i), Constants.DEFAULT_SEARCH_CATEGORIES[i]);
            list_categorie_foods.add(categorie_food);
        }
        adapter_rc_menuCat = new Adapter_RC_MenuCat(list_categorie_foods, true);
        GridLayoutManager manager = new GridLayoutManager(getContext(), 4);
        recyclerView.setLayoutManager(manager);
        adapter_rc_menuCat.notifyDataSetChanged();
        recyclerView.setAdapter(adapter_rc_menuCat);
    }


    public void bindingRcV_recipes(List<Recipe> list, RecyclerView mRecyclerView, boolean isgarde) {
        Adapter_RC_RecipeDt adapter_rc_recipeDt;
        if (list != null && list.size() != 0) {
            adapter_rc_recipeDt = new Adapter_RC_RecipeDt(getContext(), getActivity(), Remotelist_recipe.getValue(), TAG_ONLINE);
            LinearLayoutManager manager = new LinearLayoutManager(getContext());
            manager.setOrientation(HORIZONTAL);
            mRecyclerView.setLayoutManager(manager);
            mRecyclerView.setAdapter(adapter_rc_recipeDt);
        } else if (list_recipe != null && list_recipe.getValue().size() != 0) {
            adapter_rc_recipeDt = new Adapter_RC_RecipeDt(getContext(), getActivity(), list_recipe.getValue(), TAG_OFFLINE);
            LinearLayoutManager manager = new LinearLayoutManager(getContext());
            manager.setOrientation(HORIZONTAL);
            mRecyclerView.setLayoutManager(manager);
            mRecyclerView.setAdapter(adapter_rc_recipeDt);
        }

    }

}