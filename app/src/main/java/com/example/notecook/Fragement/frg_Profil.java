package com.example.notecook.Fragement;

import static com.example.notecook.Activity.MainActivity.Type_User;
import static com.example.notecook.Api.env.BASE_URL;
import static com.example.notecook.Utils.Constants.MODE_ONLINE;
import static com.example.notecook.Utils.Constants.TAG_MODE_INVITE;
import static com.example.notecook.Utils.Constants.decod;
import static com.example.notecook.Utils.Constants.decodeBase64ToBitmap;
import static com.example.notecook.Utils.Constants.getUserInput;
import static com.example.notecook.Utils.Constants.user_login;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
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
import com.example.notecook.Model.User;
import com.example.notecook.R;
import com.example.notecook.Utils.Constants;
import com.example.notecook.Utils.FragmentLifecycle;
import com.example.notecook.Utils.ImageHelper;
import com.example.notecook.ViewModel.RecipeViewModel;
import com.example.notecook.ViewModel.UserViewModel;
import com.example.notecook.databinding.FragmentFrgProfilBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class frg_Profil extends Fragment implements FragmentLifecycle {

    public static FragmentFrgProfilBinding bindingProfil;
    private String TAG = "Profil";
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
        extracted();
        Log.d(TAG, "onResume");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    private void extracted() {

        if (!Type_User.equals(Constants.TAG_MODE_INVITE)) {
            user = new User();
            if (user_login.getUser() != null) {
                user = user_login.getUser();
                bindingProfil.txtUsername.setText(user.getUsername());
                bindingProfil.txtGradeStatus.setText(user.getGrade() + " " + user.getStatus());
                if (user.getPathimageuser() != null && !user.getPathimageuser().isEmpty()) {
                    String imageUrl = "";
                    if(user.getPathimageuser().startsWith("data:")){
                        imageUrl = user.getPathimageuser().replaceFirst("^data:image/[^;]+;base64,", "");
                        bindingProfil.iconProfil.setImageBitmap(decodeBase64ToBitmap(imageUrl));
                    }
                    else
                    if (user.getPathimageuser().startsWith("/data")) {
                        bindingProfil.iconProfil.setImageBitmap(ImageHelper.loadImageFromPath(user.getPathimageuser()));
                    } else if (user.getPathimageuser().startsWith("http")) {
                        Picasso.get().load(user.getPathimageuser()).into(bindingProfil.iconProfil);
                    } else {
                        imageUrl = BASE_URL + "uploads/" + user.getPathimageuser();
                        Picasso.get().load(imageUrl).into(bindingProfil.iconProfil);
                    }
                } else
                    bindingProfil.iconProfil.setImageDrawable(getResources().getDrawable(R.drawable.aec4b1a59b7165562698470ce91494be));
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
        bindingProfil = FragmentFrgProfilBinding.inflate(inflater, container, false);
        viewPager2 = bindingProfil.vp2Profil;
        tabLayout = bindingProfil.tl;
        b = getActivity().findViewById(R.id.floating_action_button);
        b.show();
        tabLayout.addTab(tabLayout.newTab().setText("MY RECIPES"));
        tabLayout.addTab(tabLayout.newTab().setText("MY BONUSES"));
        viewPager2.setUserInputEnabled(true);
        recipeVM = new RecipeViewModel(requireContext(), requireActivity());
        userVM = new UserViewModel(requireContext(), requireActivity());
        recipeVM = new ViewModelProvider(this, recipeVM).get(RecipeViewModel.class);
        userVM = new ViewModelProvider(this, userVM).get(UserViewModel.class);
        getUserInfo();

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
        bindingProfil.ImgVSetting.setOnClickListener(view -> {
            if (Type_User.equals(Constants.TAG_MODE_INVITE)) {
                Toast.makeText(getContext(), "" + Constants.TAG_MODE_INVITE, Toast.LENGTH_LONG).show();
            } else {
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.ly_vp_con, new Frg_EditProfil());
                fragmentTransaction.commitNow();
            }
        });
        setViewPagerAdapter();

        return bindingProfil.getRoot();
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
        bindingProfil.vp2Profil.setAdapter(viewPager2Adapter);
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


    private void getUserInfo() {
        if (!Type_User.equals(TAG_MODE_INVITE)) {
            Constants.loading_ui(getContext(), getActivity(), "Loading...");
            String s1 = getUserInput(getContext());
            if (MODE_ONLINE)
                userVM.getUser(s1).observe(getViewLifecycleOwner(), new Observer<User>() {
                    @Override
                    public void onChanged(User user) {
                        if (user != null) {
                            Toast.makeText(getContext(), "user get by observe", Toast.LENGTH_SHORT).show();
                            extracted();
                        }
                        Constants.dismissLoadingDialog();
                    }
                });
            else userVM.getUserLocal(s1, "").observe(getViewLifecycleOwner(), new Observer<User>() {
                @Override
                public void onChanged(User user) {
                    if (user != null) {
                        Toast.makeText(getContext(), "user get by observe", Toast.LENGTH_SHORT).show();
                        extracted();
                        Constants.dismissLoadingDialog();
                    }
                }
            });
        }
    }
}