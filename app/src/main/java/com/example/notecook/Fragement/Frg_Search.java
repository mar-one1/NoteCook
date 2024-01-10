package com.example.notecook.Fragement;

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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notecook.Adapter.Adapter_RC_RecipeDt;
import com.example.notecook.Api.ApiClient;
import com.example.notecook.Api.ApiService;
import com.example.notecook.BuildConfig;
import com.example.notecook.Model.Recipe;
import com.example.notecook.R;
import com.example.notecook.Utils.Constants;
import com.example.notecook.databinding.FragmentFrgSearchBinding;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Frg_Search extends Fragment {

    Recipe mRecipe;
    FragmentFrgSearchBinding binding;
    private RecyclerView mRecyclerView;
    private Drawable defaultImagelike;


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
        bindingRcV_recipes(binding.RcRecipeSearch, null, "default");
        //defaultImagelike=binding.HeartImgeclk;
        defaultImagelike = getResources().getDrawable(R.drawable.ic_baseline_favorite_24);

        binding.HeartImgeclk.setOnClickListener(view -> {

            if (!defaultImagelike.getConstantState().equals(binding.HeartImgeclk.getDrawable().getConstantState())) {
                binding.HeartImgeclk.setImageDrawable(defaultImagelike);
            } else
                binding.HeartImgeclk.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_border_black_24dp));
        });

        binding.HeartImgeclk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSettings();
            }
        });

        binding.txtRecherche.addTextChangedListener(new TextWatcher() {
            List<String> list = new ArrayList<>();

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Search_list.clear();
                // binding.txtRecherche.setText(s + " \n");
                if(!list.contains(s.toString()) && s!="")
                list.add(s.toString());
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchRecipes(String.valueOf(s));
            }

            @Override
            public void afterTextChanged(Editable s) {
                List<String> listcurrent =new ArrayList<>();
                for (String item: list){
                    if(item.contains(s) && !listcurrent.contains(s)) listcurrent.add(s.toString());
                }
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, listcurrent);
                binding.txtRecherche.setAdapter(arrayAdapter);
                binding.txtRecherche.setThreshold(2);
            }
        });



        return binding.getRoot();
    }
    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    public void bindingRcV_recipes(RecyclerView recyclerView, List<Recipe> searchList, String tag) {

        List<Recipe> list_recipes = new ArrayList<>();
        Adapter_RC_RecipeDt adapter_rc_recipeDt;
        for (int i = 0; i < 8; i++) {
//            mRecipe = new Recipe(getResources().getDrawable(R.drawable.ic_search_fill_gray), "jus orange",true);
            mRecipe = new Recipe();
            list_recipes.add(mRecipe);
        }
        if (!tag.equals("search"))
            adapter_rc_recipeDt = new Adapter_RC_RecipeDt(Remotelist_recipe, TAG_REMOTE);
        else adapter_rc_recipeDt = new Adapter_RC_RecipeDt(Search_list, TAG_REMOTE);
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