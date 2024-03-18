package com.example.notecook.Fragement;

import static androidx.recyclerview.widget.RecyclerView.HORIZONTAL;
import static com.example.notecook.Utils.Constants.Recipes_Fav_User;
import static com.example.notecook.Utils.Constants.TAG_LOCAL;
import static com.example.notecook.Utils.Constants.TAG_ONLINE;
import static com.example.notecook.Utils.Constants.list_recipe;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.notecook.Adapter.Adapter_RC_RecipeDt;
import com.example.notecook.Model.Recipe;
import com.example.notecook.R;
import com.example.notecook.databinding.FragmentFrgFavBinding;
import com.example.notecook.databinding.FragmentFrgRecipeFavBinding;

import java.util.ArrayList;
import java.util.List;


public class Frg_recipe_fav extends Fragment {

    FragmentFrgRecipeFavBinding binding;


    public Frg_recipe_fav() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        bindingRcV_FavRecipes(Recipes_Fav_User,binding.RcFavRecipeByUser,true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentFrgRecipeFavBinding.inflate(inflater, container, false);
        bindingRcV_FavRecipes(Recipes_Fav_User,binding.RcFavRecipeByUser,true);
        return binding.getRoot();
    }

    public void bindingRcV_FavRecipes(List<Recipe> list, RecyclerView mRecyclerView, boolean isgarde) {
        List<Recipe> list_recipes = new ArrayList<>();
        Adapter_RC_RecipeDt adapter_rc_recipeDt;

        //if(TAG_CONNEXION==200)
        adapter_rc_recipeDt = new Adapter_RC_RecipeDt(getContext(),list, TAG_ONLINE);
        // adapter_rc_recipeDt = new Adapter_RC_RecipeDt(list_recipe,true);
        GridLayoutManager manager = new GridLayoutManager(getContext(), 2);
        manager.setOrientation(HORIZONTAL);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(adapter_rc_recipeDt);
    }
}