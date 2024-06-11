package com.example.easyattend.lecturer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.os.Bundle;
import android.view.View;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.example.easyattend.R;
import com.example.easyattend.databinding.ActivityLecturerDashboardBinding;
import com.example.easyattend.databinding.ActivityStudentDashboardBinding;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class LecturerDashboardActivity extends AppCompatActivity {
    ActivityLecturerDashboardBinding binding;
    NavHostFragment navHostFragment;
    NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLecturerDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.main));

        navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();

        setBottomNavigation();

    }

    private void setBottomNavigation() {

        binding.bottomNav.bottomNavigation.add(new MeowBottomNavigation.Model(1, R.drawable.ic_baseline_home_24));
        binding.bottomNav.bottomNavigation.add(new MeowBottomNavigation.Model(2, R.drawable.ic_app));
        binding.bottomNav.bottomNavigation.add(new MeowBottomNavigation.Model(3, R.drawable.ic_group));
        binding.bottomNav.bottomNavigation.add(new MeowBottomNavigation.Model(4, R.drawable.ic_notification));
        binding.bottomNav.bottomNavigation.add(new MeowBottomNavigation.Model(5, R.drawable.ic_setting));

        navController.navigate(R.id.lecturerHomeFragment);
        binding.bottomNav.bottomNavigation.show(1, true);

        binding.bottomNav.bottomNavigation.setOnClickMenuListener(new Function1<MeowBottomNavigation.Model, Unit>() {
            @Override
            public Unit invoke(MeowBottomNavigation.Model model) {

                switch (model.getId()) {
                    case 1:
                        navController.navigate(R.id.lecturerHomeFragment);
                        break;

                    case 2:
                        navController.navigate(R.id.lecturerApprovalFragment);
                        break;

                    case 3:
                        navController.navigate(R.id.lecturerSubjectFragment);
                        break;
                    case 4:
                        navController.navigate(R.id.lecturerNotificationFragment);
                        break;
                    case 5:
                        navController.navigate(R.id.lecturerSettingFragment);
                        break;
                }

                return null;
            }
        });

        binding.bottomNav.home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.lecturerHomeFragment);
                binding.bottomNav.bottomNavigation.show(1, true);
            }
        });
        binding.bottomNav.leaves.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.bottomNav.bottomNavigation.show(2, true);
                navController.navigate(R.id.lecturerApprovalFragment);
            }
        });
        binding.bottomNav.group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.lecturerSubjectFragment);
                binding.bottomNav.bottomNavigation.show(3, true);
            }
        });

        binding.bottomNav.notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.lecturerNotificationFragment);
                binding.bottomNav.bottomNavigation.show(4, true);
            }
        });

        binding.bottomNav.setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.lecturerSettingFragment);
                binding.bottomNav.bottomNavigation.show(5, true);
            }
        });
    }

}