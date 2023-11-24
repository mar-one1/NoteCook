package com.example.notecook.Fragement;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.fragment.app.Fragment;

import com.example.notecook.Login;
import com.example.notecook.Model.Step;
import com.example.notecook.R;
import com.example.notecook.Utils.Constants;
import com.example.notecook.Utils.SimpleService;
import com.example.notecook.databinding.FragmentFrgStepRecipeBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Frg_Step_Recipe extends Fragment {

    FragmentFrgStepRecipeBinding binding;
    CounterClass timer = new CounterClass(0,0);
    TextView textViewTime;
    Button btn_star, btn_cancel;
    private TimePicker picker_hours, picker_minute;
    private List<Step> steps = new ArrayList<>();
    private int current_id_step;
    private View.OnClickListener startListener = v -> getActivity().startService(new Intent(getActivity(), SimpleService.class));
    private View.OnClickListener stopListener = v -> getActivity().stopService(new Intent(getActivity(), SimpleService.class));

    public Frg_Step_Recipe() {
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
        binding = FragmentFrgStepRecipeBinding.inflate(inflater, container, false);
        //picker_hours = binding.pickerHours;
        //picker_minute = binding.pickerMin;
        textViewTime = binding.txtAff;
        btn_star = binding.btnStar;
        btn_cancel = binding.btnCancel;
        /*picker_minute.setMinValue(0);
        picker_minute.setMaxValue(60);*/

//        StepsDataSource stepsDataSource = new StepsDataSource(getContext());
//        stepsDataSource.open();
//        steps = stepsDataSource.getAllSTEP();
//        stepsDataSource.close();

        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        btn_cancel.setOnClickListener(view -> {
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAGS_CHANGED);
            btn_star.setEnabled(true);
            timer.start().cancel();
            timer.onFinish();
        });

        btn_star.setOnClickListener(v -> {
            // Keep the screen on
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            if (Integer.parseInt(String.valueOf(binding.editTime.getText())) != 0) {
                int t = Integer.parseInt(binding.editTime.getText().toString());
                timer = new CounterClass((long) t * 60 * 1000, 1000);
                timer.start();
                btn_star.setEnabled(false);
                binding.lyPicker.setVisibility(View.GONE);
                binding.lyTimer.setVisibility(View.VISIBLE);
            } else
                Toast.makeText(getContext(), "verifier picker minute", Toast.LENGTH_SHORT).show();
        });


        binding.editTime.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().equals("")) {
                    long millis = (Integer.parseInt(String.valueOf(s)) * 60 * 1000);
                    String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis), TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                            TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
                    textViewTime.setText(hms);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        binding.imgBackword.setOnClickListener(v -> {
            if (current_id_step != 0) {
                current_id_step = current_id_step - 1;
                step_switcher(current_id_step);
            }
        });
        binding.imgForward.setOnClickListener(v -> {
            if (current_id_step + 1 < steps.size()) {
                current_id_step = current_id_step + 1;
                step_switcher(current_id_step);
            }
        });
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        steps = Constants.Steps_CurrentRecipe;
        if (steps.size() > 0)
            step_switcher(0);
    }

    public void addNotification(String s, String chan) {
        int NOTIFICATION_ID = 234;
        @SuppressWarnings("AccessStaticViaInstance") NotificationManager notificationManager = (NotificationManager) getContext().getSystemService(getContext().NOTIFICATION_SERVICE);
        String CHANNEL_ID = "";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            CHANNEL_ID = "my_channel_01";
            CharSequence name = "my_channel";
            String Description = "This is my channel";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            mChannel.setDescription(Description);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            mChannel.setShowBadge(false);
            notificationManager.createNotificationChannel(mChannel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle("")
                .setContentText(s + " " + chan);

        Intent resultIntent = new Intent(getContext(), Login.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getContext());
        stackBuilder.addParentStack(Login.class);
        stackBuilder.addNextIntent(resultIntent);
        // PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        //builder.setContentIntent(resultPendingIntent);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    private void step_switcher(int position) {
        int time = steps.get(position).getTime_step();
        /*int hours = time / 100;
        int minutes = (time - hours * 100) % 60;*/
        if(timer==null)
        timer = new CounterClass((long) time * 60 * 1000, 1000);
        binding.editTime.setText(String.valueOf(time));
        binding.detailStep.setText(steps.get(position).getDetail_step());
        binding.orderStep.setText(steps.size() + "/" + (position+1));
    }

    public class CounterClass extends CountDownTimer {

        public CounterClass(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        String hms;
        @Override
        public void onTick(long millisUntilFinished) {
            long millis = millisUntilFinished;
            hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis), TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                    TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
            System.out.println(hms);
            textViewTime.setText(hms);
        }

        @Override
        public void onFinish() {
            addNotification("Time Finished", "recipe 1");
            Toast.makeText(getContext(), "Time Finished", Toast.LENGTH_SHORT).show();
            binding.lyPicker.setVisibility(View.VISIBLE);
            binding.lyTimer.setVisibility(View.GONE);
            hms = "00:00:00";
            getActivity().startService(new Intent(getContext(), SimpleService.class));

        }

    }

    @Override
    public void onPause() {
        super.onPause();

    }
}