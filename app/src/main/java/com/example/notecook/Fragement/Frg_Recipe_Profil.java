package com.example.notecook.Fragement;

import static com.example.notecook.Activity.MainActivity.Type_User;
import static com.example.notecook.Utils.Constants.MODE_ONLINE;
import static com.example.notecook.Utils.Constants.RemotelistFullRecipe;
import static com.example.notecook.Utils.Constants.TAG_LOCAL;
import static com.example.notecook.Utils.Constants.TAG_MODE_INVITE;
import static com.example.notecook.Utils.Constants.getUserInput;
import static com.example.notecook.Utils.Constants.getUserSynch;
import static com.example.notecook.Utils.Constants.list_recipe;
import static com.example.notecook.Utils.Constants.saveUserSynch;
import static com.example.notecook.Utils.Constants.user_login;
import static com.example.notecook.Utils.Constants.user_login_local;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notecook.Adapter.Adapter_RC_RecipeDt;
import com.example.notecook.Dto.RecipeResponse;
import com.example.notecook.Model.Recipe;
import com.example.notecook.Model.User;
import com.example.notecook.ViewModel.RecipeViewModel;
import com.example.notecook.ViewModel.UserViewModel;
import com.example.notecook.databinding.FragmentFrgRecipeProfilBinding;

import java.util.List;


public class Frg_Recipe_Profil extends Fragment {
    private Recipe mRecipe;
    private FragmentFrgRecipeProfilBinding binding;
    private RecyclerView recyclerView;
    private RecipeViewModel recipeVM;
    private UserViewModel userVM;
    private Adapter_RC_RecipeDt adapter_rc_recipeDt;

    public Frg_Recipe_Profil() {
        // Required empty public constructor
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
        recipeVM = new RecipeViewModel(requireContext(), requireActivity());
        userVM = new UserViewModel(requireContext(), requireActivity());
        //recipeVM = new ViewModelProvider(requireActivity(), recipeVM).get(RecipeViewModel.class);
        fetchRecipeUser();
        return binding.getRoot();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void bindingRcV_recipes(RecyclerView recyclerView, List<Recipe> recipes) {

        adapter_rc_recipeDt = new Adapter_RC_RecipeDt(getContext(), getActivity(), recipes, TAG_LOCAL);
        GridLayoutManager manager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(manager);
        adapter_rc_recipeDt.notifyDataSetChanged();
        recyclerView.setAdapter(adapter_rc_recipeDt);
    }

    @Override
    public void onResume() {
        super.onResume();
        //list_recipe.setValue(recipes);
        bindingRcV_recipes(binding.RcRecipeProfil, list_recipe);
    }

    private void fetchRecipeUser() {
        if (!Type_User.equals(TAG_MODE_INVITE)) {
            if (user_login_local.getUser() != null && user_login_local.getUser().getId_User() != 0) {
                recipeVM.getRecipesLocal(user_login_local.getUser().getId_User()).observe(requireActivity(), new Observer<List<Recipe>>() {
                    @Override
                    public void onChanged(List<Recipe> recipes) {
                        if (recipes != null) {
                            list_recipe = recipes;
                            recipeVM.getFullRecipesByUsername(user_login_local.getUser().getUsername()).observe(getViewLifecycleOwner(), new Observer<List<RecipeResponse>>() {
                                @Override
                                public void onChanged(List<RecipeResponse> recipes) {
                                    if (recipes != null)
                                        RemotelistFullRecipe.setValue(recipes);
                                    if (!getUserSynch(user_login.getUser().getUsername(), requireContext())) {
                                        recipeVM.synchronisationRecipes(list_recipe, RemotelistFullRecipe.getValue(), user_login_local.getUser().getUsername()).observe(getViewLifecycleOwner(), new Observer<Boolean>() {
                                            @Override
                                            public void onChanged(Boolean aBoolean) {
                                                saveUserSynch(user_login.getUser().getUsername(), aBoolean, requireContext());
                                                    recipeVM.getRecipesLocal(user_login_local.getUser().getId_User()).observe(getViewLifecycleOwner(), new Observer<List<Recipe>>() {
                                                        @Override
                                                        public void onChanged(List<Recipe> recipes) {
                                                            if (recipes != null)
                                                                list_recipe = recipes;
                                                            bindingRcV_recipes(binding.RcRecipeProfil, list_recipe);
                                                        }
                                                    });
                                            }
                                        });
                                    }
                                }
                            });
                        }
                    }
                });
            } else {
                userVM.getUserLocal(getUserInput(requireContext()), "success").observe(getViewLifecycleOwner(), new Observer<User>() {
                    @Override
                    public void onChanged(User user) {
                        if (user != null)
                            recipeVM.getRecipesLocal(user_login_local.getUser().getId_User()).observe(getViewLifecycleOwner(), new Observer<List<Recipe>>() {
                                @Override
                                public void onChanged(List<Recipe> recipes) {
                                    if (recipes != null)
                                        list_recipe.addAll(recipes);
//                                    if (MODE_ONLINE)
//                                        recipeVM.getRecipesByUsername(user_login_local.getUser().getUsername()).observe(requireActivity(), recipeList -> {
//                                            RemotelistByIdUser_recipe.setValue(recipeList);
//                                            Toast.makeText(getContext(), "changed main " + RemotelistByIdUser_recipe.getValue().size(), Toast.LENGTH_SHORT).show();
//                                        });
                                }
                            });
                        if (MODE_ONLINE)
                            recipeVM.getFullRecipesByUsername(user_login_local.getUser().getUsername()).observe(getViewLifecycleOwner(), new Observer<List<RecipeResponse>>() {
                                @Override
                                public void onChanged(List<RecipeResponse> recipes) {
                                    if (recipes != null)
                                        RemotelistFullRecipe.setValue(recipes);
                                    if (!getUserSynch(user_login.getUser().getUsername(), requireContext())) {
                                        recipeVM.synchronisationRecipes(list_recipe, RemotelistFullRecipe.getValue(), user_login_local.getUser().getUsername()).observe(getViewLifecycleOwner(), new Observer<Boolean>() {
                                            @Override
                                            public void onChanged(Boolean aBoolean) {
                                                saveUserSynch(user_login.getUser().getUsername(), aBoolean, requireContext());
                                                if (aBoolean)
                                                    recipeVM.getRecipesLocal(user_login_local.getUser().getId_User()).observe(getViewLifecycleOwner(), new Observer<List<Recipe>>() {
                                                        @Override
                                                        public void onChanged(List<Recipe> recipes) {
                                                            if (recipes != null)
                                                                list_recipe = recipes;
                                                        }
                                                    });
                                            }
                                        });
                                    }
                                }
                            });
                    }
                });
            }
        }
    }

}