package com.example.notecook.Fragement;

import static com.example.notecook.Utils.Constants.TAG_LOCAL;
import static com.example.notecook.Utils.Constants.getUserInput;
import static com.example.notecook.Utils.Constants.list_recipe;
import static com.example.notecook.Utils.Constants.user_login_local;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notecook.Adapter.Adapter_RC_RecipeDt;
import com.example.notecook.Model.Recipe;
import com.example.notecook.Model.User;
import com.example.notecook.ViewModel.RecipeViewModel;
import com.example.notecook.ViewModel.UserViewModel;
import com.example.notecook.databinding.FragmentFrgRecipeProfilBinding;

import java.util.List;


public class Frg_Recipe_Profil extends Fragment {
    Recipe mRecipe;
    FragmentFrgRecipeProfilBinding binding;
    private RecyclerView recyclerView;
    private RecipeViewModel recipeVM;
    private UserViewModel userVM;

    public Frg_Recipe_Profil() {
        // Required empty public constructor
    }

    private void fetchRecipeUser() {
        if (user_login_local.getUser() != null && user_login_local.getUser().getId_User() != 0) {
            recipeVM.getRecipesLocal(user_login_local.getUser().getId_User()).observe(getViewLifecycleOwner(), new Observer<List<Recipe>>() {
                @Override
                public void onChanged(List<Recipe> recipes) {
                    if(recipes!=null)
                    bindingRcV_recipes(binding.RcRecipeProfil, recipes);
                }
            });
        } else {
            userVM.getUserLocal(getUserInput(getContext()), "success").observe(getActivity(), new Observer<User>() {
                @Override
                public void onChanged(User user) {
                    if(user!=null)
                    recipeVM.getRecipesLocal(user.getId_User()).observe(getViewLifecycleOwner(), new Observer<List<Recipe>>() {
                        @Override
                        public void onChanged(List<Recipe> recipes) {
                            bindingRcV_recipes(binding.RcRecipeProfil, recipes);
                        }
                    });
                }
            });
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentFrgRecipeProfilBinding.inflate(inflater, container, false);
        // Inflate the layout for this fragment
        recipeVM = new RecipeViewModel(getContext(), getActivity());
        userVM = new UserViewModel(getContext(),getActivity());
        recipeVM = new ViewModelProvider(this, recipeVM).get(RecipeViewModel.class);
        return binding.getRoot();
    }

    public void bindingRcV_recipes(RecyclerView recyclerView, List<Recipe> recipes) {
        Adapter_RC_RecipeDt adapter_rc_recipeDt;
        adapter_rc_recipeDt = new Adapter_RC_RecipeDt(getContext(), getActivity(), recipes, TAG_LOCAL);
        GridLayoutManager manager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(manager);
        adapter_rc_recipeDt.notifyDataSetChanged();
        recyclerView.setAdapter(adapter_rc_recipeDt);
    }

    @Override
    public void onResume() {
        super.onResume();
        //model.getRecipesLocal(user_login_local.getUser().getId_User());
        fetchRecipeUser();
    }
}