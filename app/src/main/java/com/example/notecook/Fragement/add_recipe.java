package com.example.notecook.Fragement;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.notecook.R;
import com.example.notecook.databinding.FragmentAddRecipeBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class add_recipe extends Fragment {

    FragmentAddRecipeBinding binding;

    public add_recipe() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAddRecipeBinding.inflate(inflater, container, false);
        int bnvId = R.id.bottom_nav;
        BottomNavigationView btnV = getActivity().findViewById(bnvId);

        binding.dtRecipeTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickhid(binding.detailRecipeLy);
            }
        });

        binding.stepTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickhid(binding.stepLy);
            }
        });

        binding.btnPlusTime.setOnClickListener(view -> {clickPlus(binding.txtTotTime,binding.btnPlusTime,binding.btnMoinsTime);});
        binding.btnMoinsTime.setOnClickListener(view -> { clickMoins(binding.txtTotTime,binding.btnMoinsTime,binding.btnMoinsTime);});
        binding.btnPlusTimesp.setOnClickListener(view -> {clickPlus(binding.txtTotTiemsp,binding.btnPlusTimesp,binding.btnMoinsTimesp);});
        binding.btnMoinsTimesp.setOnClickListener(view -> { clickMoins(binding.txtTotTiemsp,binding.btnMoinsTimesp,binding.btnMoinsTimesp);});
        binding.btnPlusCal.setOnClickListener(view -> {clickPlus(binding.txtTotCal,binding.btnPlusCal,binding.btnMoinsCal);});
        binding.btnMoinsCal.setOnClickListener(view -> { clickMoins(binding.txtTotCal,binding.btnMoinsCal,binding.btnMoinsCal);});

        btnV.setOnNavigationItemSelectedListener(
                item -> {
                    FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.detach(add_recipe.this);
                    fragmentTransaction.commitNow();
                    int i = 0;
                    switch (item.getItemId()) {
                        case R.id.tips:
                            i = 0;
                            break;
                        case R.id.fav:
                            i = 1;
                            break;
                        case R.id.search:
                            i = 2;
                            break;
                        case R.id.cart:
                            i = 3;
                            break;
                        case R.id.parson:
                            i = 4;
                            break;
                    }
                    MainFragment.viewPager2.setCurrentItem(i, false);
                    return false;
                });
        // Inflate the layout for this fragmentè
        return binding.getRoot();
    }

    private void expand(LinearLayout linearLayout) {
        linearLayout.setVisibility(View.VISIBLE);

        final int widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        final int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        linearLayout.measure(widthSpec, heightSpec);

        ValueAnimator mAnimator = slideAnimator(0, linearLayout.getMeasuredHeight(), linearLayout);
        mAnimator.start();
    }

    private void collapse(LinearLayout linearLayout) {
        int finalHeight = linearLayout.getHeight();

        ValueAnimator mAnimator = slideAnimator(finalHeight, 0, linearLayout);

        mAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                //Height=0, but it set visibility to GONE
                linearLayout.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }

        });
        mAnimator.start();
    }

    private ValueAnimator slideAnimator(int start, int end, LinearLayout linearLayout) {

        ValueAnimator animator = ValueAnimator.ofInt(start, end);

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                //Update Height
                int value = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = linearLayout.getLayoutParams();
                layoutParams.height = value;
                linearLayout.setLayoutParams(layoutParams);
            }
        });
        return animator;
    }

    private void clickhid(LinearLayout linearLayout) {
        if (linearLayout.getVisibility() == View.GONE) {
            expand(linearLayout);
        } else {
            collapse(linearLayout);
        }
    }

    private void clickMoins(TextView textView, Button buttonPlus,Button buttonMoins) {
        int t = Integer.parseInt(textView.getText().toString());
        if (t <= 0) {
            buttonMoins.setEnabled(false);
        } else
            t--;
        textView.setText("" + t);
    }

    private void clickPlus(TextView textView, Button button,Button buttonMoins) {
        int t = Integer.parseInt(textView.getText().toString());
        buttonMoins.setEnabled(true);
        t++;
        textView.setText("" + t);
    }
}