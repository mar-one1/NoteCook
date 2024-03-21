package com.example.notecook.Fragement;

import static com.example.notecook.Api.ApiClient.BASE_URL;
import static com.example.notecook.MainActivity.Type_User;
import static com.example.notecook.MainActivity.decod;
import static com.example.notecook.Utils.Constants.getUserInput;
import static com.example.notecook.Utils.Constants.user_login;
import static com.example.notecook.Utils.Constants.user_login_local;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.example.notecook.Adapter.Adapter_Vp2_recipeProfil;
import com.example.notecook.Model.Recipe;
import com.example.notecook.Model.User;
import com.example.notecook.R;
import com.example.notecook.Utils.Constants;
import com.example.notecook.Utils.FragmentLifecycle;
import com.example.notecook.ViewModel.RecipeViewModel;
import com.example.notecook.ViewModel.UserViewModel;
import com.example.notecook.databinding.FragmentFrgProfilBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class frg_Profil extends Fragment implements FragmentLifecycle {

    private FragmentFrgProfilBinding binding;
    private TabLayout tabLayout;
    private ViewPager2 viewPager2;
    private User user;
    private FloatingActionButton b;
    private RecipeViewModel recipeVM;
    private UserViewModel userVM;

    public frg_Profil() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        onResumeFragment();
        extracted();
    }

    private void extracted() {
        if (!Type_User.equals(Constants.TAG_MODE_INVITE)) {
            user = new User();
            if (user_login.getUser() != null) {
                user = user_login.getUser();
                binding.txtUsername.setText(user.getUsername());
                binding.txtGradeStatus.setText(user.getGrade() + " " + user.getStatus());

                if (user_login.getUser().getPathimageuser() != null && !user_login.getUser().getPathimageuser().equals("")) {
                    String imageUrl = "";
                    if (user_login.getUser().getPathimageuser().startsWith("http"))
                        imageUrl = user_login.getUser().getPathimageuser();
                    else
                        imageUrl = BASE_URL + "uploads/" + user_login.getUser().getPathimageuser();
                    Picasso.get().load(imageUrl).into(binding.iconProfil);
                    //binding.iconProfil.setImageDrawable(Constants.DEFAUL_IMAGE);
                } else if (user_login_local.getUser() != null && user_login_local.getUser().getIcon() != null) {
                    binding.iconProfil.setImageBitmap(decod(user.getIcon()));
                }
            } else if (user_login_local.getUser() != null) {
                user_login.setUser(user_login_local.getUser());
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
        binding = FragmentFrgProfilBinding.inflate(inflater, container, false);
        viewPager2 = binding.vp2Profil;
        tabLayout = binding.tl;
        b = getActivity().findViewById(R.id.floating_action_button);
        b.show();
        tabLayout.addTab(tabLayout.newTab().setText("MY RECIPES"));
        tabLayout.addTab(tabLayout.newTab().setText("MY BONUSES"));
        recipeVM = new RecipeViewModel(getContext());
        userVM = new UserViewModel(getContext());
        recipeVM = new ViewModelProvider(this,recipeVM).get(RecipeViewModel.class);
        //userVM = new ViewModelProvider(this,userVM).get(UserViewModel.class);
        if (user_login_local.getUser() != null && user_login_local.getUser().getId_User() != 0) {
            recipeVM.getRecipesLocal(user_login_local.getUser().getId_User()).observe(getViewLifecycleOwner(), new Observer<List<Recipe>>() {
                @Override
                public void onChanged(List<Recipe> recipes) {
                    setViewPagerAdapter();
                }
            });
        } else {
            userVM.getUserLocal(getUserInput(getContext()), "success").observe(getActivity(), new Observer<User>() {
                @Override
                public void onChanged(User user) {
                    extracted();
                    recipeVM.getRecipesLocal(user.getId_User()).observe(getViewLifecycleOwner(), new Observer<List<Recipe>>() {
                        @Override
                        public void onChanged(List<Recipe> recipes) {
                            setViewPagerAdapter();
                        }
                    });
                }
            });

        }




        viewPager2.setUserInputEnabled(true);

        tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.red));
        tabLayout.setSelectedTabIndicatorHeight((int) (3 * getResources().getDisplayMetrics().density));
        tabLayout.setTabTextColors(getResources().getColor(R.color.gray), Color.parseColor("#000000"));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition(), false);
//                b.hide();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                Toast.makeText(getContext(), "select none", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
//                b.hide();
            }

            @Override
            public void onPageSelected(int position) {
                tabLayout.selectTab(tabLayout.getTabAt(position));
//                b.hide();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
//                b.hide();
            }
        });
        binding.ImgVSetting.setOnClickListener(view -> {

            if (Type_User.equals(Constants.TAG_MODE_INVITE)) {
                Toast.makeText(getContext(), "" + Constants.TAG_MODE_INVITE, Toast.LENGTH_LONG).show();
            } else {
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.ly_vp_con, new Frg_EditProfil());
                fragmentTransaction.commitNow();
            }
        });
        return binding.getRoot();
    }

    public void setViewPagerAdapter() {
        Adapter_Vp2_recipeProfil viewPager2Adapter = new
                Adapter_Vp2_recipeProfil(getActivity());
        //create an ArrayList of Fragments
        ArrayList<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(new Frg_Recipe_Profil());
        fragmentList.add(new Frg_recipe_fav());
        viewPager2Adapter.setData(fragmentList);
        //set the data for the adapter
        binding.vp2Profil.setAdapter(viewPager2Adapter);
    }


    @Override
    public void onPauseFragment() {
        Toast.makeText(getContext(), "on Pause", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResumeFragment() {
        Toast.makeText(getContext(), "on Resume", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onPause() {
        super.onPause();
        onPauseFragment();
    }


}