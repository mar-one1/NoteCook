package com.example.notecook.Fragement;


import static com.example.notecook.Api.ApiClient.BASE_URL;
import static com.example.notecook.MainActivity.decod;
import static com.example.notecook.MainActivity.synchronizeDataDetailRecipe;
import static com.example.notecook.Utils.Constants.CURRENT_RECIPE;
import static com.example.notecook.Utils.Constants.Detail_CurrentRecipe;
import static com.example.notecook.Utils.Constants.User_CurrentRecipe;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.notecook.Adapter.Adapter_Vp2_recipeProfil;
import com.example.notecook.MainActivity;
import com.example.notecook.Model.Recipe;
import com.example.notecook.R;
import com.example.notecook.Utils.Constants;
import com.example.notecook.databinding.FragmentFrgDetailRecipeBinding;
import com.google.android.material.tabs.TabLayout;
import com.squareup.picasso.Picasso;

import org.chromium.base.Log;

import java.util.ArrayList;
import java.util.List;


public class Frg_detail_recipe extends Fragment {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private FragmentFrgDetailRecipeBinding binding;
    private TextView txtTime, txtCal, txtLevel;
    private List<String> titles;
    private Drawable defaultImagelike;
    private Drawable defaultImageRate;
    private Recipe recipe;


    public Frg_detail_recipe() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        binding.vp2Detairecipe.setCurrentItem(0);
        if (!MainActivity.Type_User.equals(Constants.TAG_MODE_INVITE)) {
            if (Detail_CurrentRecipe != null) {
                if (User_CurrentRecipe != null && CURRENT_RECIPE != null) {
                    binding.recipeNameTxt.setText(CURRENT_RECIPE.getNom_recipe());
                    binding.NomUserRecipe.setText(User_CurrentRecipe.getUsername());
                    if(CURRENT_RECIPE.getIcon_recipe()!=null) {
                        binding.iconRecipe.setImageBitmap(decod(CURRENT_RECIPE.getIcon_recipe()));
                    } else {
                        String url = BASE_URL + "uploads/" + CURRENT_RECIPE.getPathimagerecipe();
                        Picasso.get().load(url).into(binding.iconRecipe);
                    }
                    if (!User_CurrentRecipe.getPathimageuser().equals("")) {
                        String url = BASE_URL + "uploads/" + User_CurrentRecipe.getPathimageuser();
                        Picasso.get().load(url).into(binding.iconProfilDetailrecipe);
                    }
                    Log.d("TAG", String.valueOf(Detail_CurrentRecipe.getId_detail_recipe()));
                    binding.icludeRoxDetailRecipe.vlTime.setText(String.valueOf(Detail_CurrentRecipe.getTime()));
                    binding.icludeRoxDetailRecipe.valCal.setText(String.valueOf(Detail_CurrentRecipe.getCal()));
                    binding.icludeRoxDetailRecipe.valLevel.setText(String.valueOf(Detail_CurrentRecipe.getLevel()));
                    //synchronizeDataDetailRecipe(Detail_CurrentRecipe, getContext());
                }
            }

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
        binding = FragmentFrgDetailRecipeBinding.inflate(inflater, container, false);
        View rootView = inflater.inflate(R.layout.recipe_detail_row, container, false);


        viewPager = binding.vp2Detairecipe;
        tabLayout = binding.tl;

        tabLayout.addTab(tabLayout.newTab().setText("INGREDIENTS"));
        tabLayout.addTab(tabLayout.newTab().setText("PROCESS"));
        tabLayout.addTab(tabLayout.newTab().setText("REVIEWS"));

        setViewPagerAdapter();
        viewPager.setUserInputEnabled(true);
        tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.red));
        tabLayout.setSelectedTabIndicatorHeight((int) (3 * getResources().getDisplayMetrics().density));
        tabLayout.setTabTextColors(getResources().getColor(R.color.gray), Color.parseColor("#000000"));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition(), false);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                //   Toast.makeText(getContext(), "unselected" + tab.getText().toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });

        defaultImagelike = getResources().getDrawable(R.drawable.ic_baseline_favorite_24);

        binding.IVFavorit.setOnClickListener(view -> {
            if (!defaultImagelike.getConstantState().equals(binding.IVFavorit.getDrawable().getConstantState())) {
                binding.IVFavorit.setImageDrawable(defaultImagelike);
            } else
                binding.IVFavorit.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_border_black_24dp));
        });

        defaultImageRate = getResources().getDrawable(R.drawable.ic_star_black_24dp);
        binding.FavRecipeIcone.setOnClickListener(view -> {
            if (!defaultImageRate.getConstantState().equals(binding.FavRecipeIcone.getDrawable().getConstantState())) {
                binding.FavRecipeIcone.setImageDrawable(defaultImageRate);
            } else
                binding.FavRecipeIcone.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_fill0_wght400_grad0_opsz48));
        });

        binding.btnBackDR.setOnClickListener(view -> {
            viewPager = getActivity().findViewById(R.id.vp2);
            viewPager.setCurrentItem(0);
        });
        binding.btnShareDR.setOnClickListener(view -> {
            try {
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("text/plain");
                share.putExtra(Intent.EXTRA_TEXT, "false");

                getContext().startActivity(Intent.createChooser(share, "Share"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        /*txtTime = rootView.findViewById(R.id.vl_time);
        txtCal = rootView.findViewById(R.id.val_cal);
        txtLevel = rootView.findViewById(R.id.val_level);*/


        return binding.getRoot();
    }


    public void setViewPagerAdapter() {
        Adapter_Vp2_recipeProfil viewPager2Adapter = new
                Adapter_Vp2_recipeProfil(getActivity());
        //create an ArrayList of Fragments
        ArrayList<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(new Frg_recipe_ingredients());
        fragmentList.add(new Frg_Step_Recipe());
        fragmentList.add(new Frg_Fav());

        viewPager2Adapter.setData(fragmentList);
        //set the data for the adapter
        binding.vp2Detairecipe.setAdapter(viewPager2Adapter);
    }
}