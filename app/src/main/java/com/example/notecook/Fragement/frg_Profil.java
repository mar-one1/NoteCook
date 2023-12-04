package com.example.notecook.Fragement;

import static com.example.notecook.MainActivity.decod;
import static com.example.notecook.Utils.Constants.user_login;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import com.example.notecook.Adapter.Adapter_Vp2_recipeProfil;
import com.example.notecook.Api.ApiClient;
import com.example.notecook.Api.ApiService;
import com.example.notecook.MainActivity;
import com.example.notecook.Model.User;
import com.example.notecook.R;
import com.example.notecook.Utils.Constants;
import com.example.notecook.databinding.FragmentFrgProfilBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class frg_Profil extends Fragment {

    FragmentFrgProfilBinding binding;
    TabLayout tabLayout;
    ViewPager2 viewPager2;
    Button ImgV_Setting;
    User user;
    MainActivity m;
    Context context;
    FloatingActionButton b;

    public frg_Profil() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        m = new MainActivity();
        if (!MainActivity.Type_User.equals(Constants.TAG_MODE_INVITE)) {
            user = new User();
            if (user_login != null) {
                user = user_login.getUser();
                binding.txtUsername.setText(user.getUsername());

                if (user.getIcon() != null) {
                    binding.iconProfil.setImageBitmap(decod(user.getIcon()));
                } else
                    binding.iconProfil.setImageDrawable(Constants.DEFAUL_IMAGE);
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
        ImgV_Setting = binding.ImgVSetting;
        b = getActivity().findViewById(R.id.floating_action_button);
        b.show();
        tabLayout.addTab(tabLayout.newTab().setText("MY RECIPES"));
        tabLayout.addTab(tabLayout.newTab().setText("MY BONUSES"));
        setViewPagerAdapter();
        viewPager2.setUserInputEnabled(true);
        //new TabLayoutMediator(tabLayout, viewPager2, (TabLayoutMediator.TabConfigurationStrategy) getActivity()).attach();
        //Toast.makeText(getActivity(), "on create view", Toast.LENGTH_SHORT).show();
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

            if (MainActivity.Type_User.equals(Constants.TAG_MODE_INVITE)) {
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



}