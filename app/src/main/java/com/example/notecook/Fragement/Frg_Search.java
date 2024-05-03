package com.example.notecook.Fragement;

import static com.example.notecook.Utils.Constants.MODE_ONLINE;
import static com.example.notecook.Utils.Constants.Remotelist_recipe;
import static com.example.notecook.Utils.Constants.Search_list;
import static com.example.notecook.Utils.Constants.TAG_REMOTE;
import static com.example.notecook.Utils.Constants.Token;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notecook.Adapter.Adapter_RC_RecipeDt;
import com.example.notecook.Api.ApiClient;
import com.example.notecook.Api.ApiService;
import com.example.notecook.BuildConfig;
import com.example.notecook.Model.Recipe;
import com.example.notecook.R;
import com.example.notecook.Utils.Constants;
import com.example.notecook.ViewModel.RecipeViewModel;
import com.example.notecook.databinding.FragmentFrgSearchBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Frg_Search extends Fragment {

    Recipe mRecipe;
    private FragmentFrgSearchBinding binding;
    private RecyclerView mRecyclerView;
    private Drawable defaultImagelike;
    private RecipeViewModel recipeVM;
    private FragmentActivity fragmentActivity;


    public Frg_Search() {
        // Required empty public constructor
    }


    @Override
    public void onResume() {
        super.onResume();
        //bindingRcV_recipes(binding.RcRecipeSearch, null, "default");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentFrgSearchBinding.inflate(inflater, container, false);
        // Inflate the layout for this fragment
        fragmentActivity = (FragmentActivity) getContext();
        recipeVM = new RecipeViewModel(getContext(), getActivity());
        bindingRcV_recipes(binding.RcRecipeSearch, null, "default");
        //defaultImagelike=binding.HeartImgeclk;
        defaultImagelike = getResources().getDrawable(R.drawable.ic_baseline_favorite_24);

        binding.filtreSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //openSettings();
                // Construct filter parameters
                binding.llFiltre.setVisibility(binding.llFiltre.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
            }
        });
        binding.filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchText = !(binding.txtRecherche.getText().equals("")) ? binding.txtRecherche.getText().toString().trim() : binding.txtRecherche.getText().toString();
                //double minPrice = Double.parseDouble(binding.minPriceEditText.getText().toString().trim());
                //double maxPrice = Double.parseDouble(binding.maxPriceEditText.getText().toString().trim());
                //String category = binding.categorySpinner.getSelectedItem().toString().trim();
                binding.llFiltre.setVisibility(View.GONE);
            }
        });

        binding.txtRecherche.addTextChangedListener(new TextWatcher() {
            Set<String> list = new HashSet<>();

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Search_list.clear();
                // binding.txtRecherche.setText(s + " \n");
                String txt = s.toString().trim();
                if (!list.contains(txt) && !txt.isEmpty()) {
                    list.add(txt);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //searchRecipes(String.valueOf(s));
                Map<String, String> condition = new HashMap<>();
                condition.put("searchText", s.toString());
                condition.put("Level_recipe", binding.levelRecipeSearch.getSelectedItem().toString());
                //condition.put("userId", "1");
                if (MODE_ONLINE)
                    recipeVM.SearchRecipeByCondition(condition).observe(requireActivity(), new Observer<List<Recipe>>() {
                        @Override
                        public void onChanged(List<Recipe> recipes) {
                            if (recipes != null && recipes.size() > 0)
                                bindingRcV_recipes(binding.RcRecipeSearch, recipes, "search");
                        }
                    });
            }

            @Override
            public void afterTextChanged(Editable s) {
                List<String> listcurrent = new ArrayList<>();
                for (String item : list) {
                    if (item.contains(s) && !listcurrent.contains(s)) listcurrent.add(s.toString());
                }
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, listcurrent);
                binding.txtRecherche.setAdapter(arrayAdapter);
                binding.txtRecherche.setThreshold(2);
            }
        });

        Constants.level(binding.levelRecipeSearch,getContext());


        return binding.getRoot();
    }

    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null);
        intent.setData(uri);

        startActivityForResult(intent, 101);
    }

    public void bindingRcV_recipes(RecyclerView recyclerView, List<Recipe> searchList, String tag) {
        Adapter_RC_RecipeDt adapter_rc_recipeDt;
        if (!tag.equals("search") && Remotelist_recipe.getValue() != null)
            adapter_rc_recipeDt = new Adapter_RC_RecipeDt(getContext(), getActivity(), Remotelist_recipe.getValue(), TAG_REMOTE);
        else {
            if (searchList == null) searchList = new ArrayList<>();
            adapter_rc_recipeDt = new Adapter_RC_RecipeDt(getContext(), getActivity(), searchList, TAG_REMOTE);
        }
        GridLayoutManager manager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(manager);
        adapter_rc_recipeDt.notifyDataSetChanged();
        recyclerView.setAdapter(adapter_rc_recipeDt);
    }

    private void searchRecipes(String key) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        Call<List<Recipe>> call = apiService.searchRecipes(Token, key);
        call.enqueue(new Callback<List<Recipe>>() {
            @Override
            public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) {
                if (response.isSuccessful()) {
                    Constants.Search_list = response.body();
                    if (Search_list.size() > 0)
                        bindingRcV_recipes(binding.RcRecipeSearch, Search_list, "search");
                    Log.d("TAG", Constants.Search_list.toString());
                    // Handle the list of products obtained from the server
                } else {
                    // Handle unsuccessful response
                }
            }

            @Override
            public void onFailure(Call<List<Recipe>> call, Throwable t) {
                // Handle failure to make the API call
            }
        });
    }
}