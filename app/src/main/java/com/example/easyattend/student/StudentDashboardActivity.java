package com.example.easyattend.student;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.os.Bundle;
import android.view.View;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.example.easyattend.R;
import com.example.easyattend.databinding.ActivityRegisterBinding;
import com.example.easyattend.databinding.ActivityStudentDashboardBinding;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class StudentDashboardActivity extends AppCompatActivity {
    ActivityStudentDashboardBinding studentDashboardBinding;
    NavHostFragment navHostFragment;
    NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        studentDashboardBinding = ActivityStudentDashboardBinding.inflate(getLayoutInflater());
        setContentView(studentDashboardBinding.getRoot());
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.main));

        navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();

        setBottomNavigation();

    }

    private void setBottomNavigation() {

        studentDashboardBinding.bottomNav.bottomNavigation.add(new MeowBottomNavigation.Model(1, R.drawable.ic_baseline_home_24));
        studentDashboardBinding.bottomNav.bottomNavigation.add(new MeowBottomNavigation.Model(2, R.drawable.ic_app));
        studentDashboardBinding.bottomNav.bottomNavigation.add(new MeowBottomNavigation.Model(3, R.drawable.ic_group));
        studentDashboardBinding.bottomNav.bottomNavigation.add(new MeowBottomNavigation.Model(4, R.drawable.ic_notification));
        studentDashboardBinding.bottomNav.bottomNavigation.add(new MeowBottomNavigation.Model(5, R.drawable.ic_setting));

        navController.navigate(R.id.studentHomeFragment);
        studentDashboardBinding.bottomNav.bottomNavigation.show(1, true);

        studentDashboardBinding.bottomNav.bottomNavigation.setOnClickMenuListener(new Function1<MeowBottomNavigation.Model, Unit>() {
            @Override
            public Unit invoke(MeowBottomNavigation.Model model) {

                switch (model.getId()) {
                    case 1:
                        navController.navigate(R.id.studentHomeFragment);
                        break;

                    case 2:
                        navController.navigate(R.id.approvalFragment);
                        break;

                    case 3:
                        navController.navigate(R.id.attendanceFragment);
                        break;
                    case 4:
                        navController.navigate(R.id.notificationFragment);
                        break;
                    case 5:
                        navController.navigate(R.id.settingFragment);
                        break;
                }

                return null;
            }
        });

        studentDashboardBinding.bottomNav.home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.studentHomeFragment);
                studentDashboardBinding.bottomNav.bottomNavigation.show(1, true);
            }
        });
        studentDashboardBinding.bottomNav.leaves.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                studentDashboardBinding.bottomNav.bottomNavigation.show(2, true);
                navController.navigate(R.id.approvalFragment);
            }
        });
        studentDashboardBinding.bottomNav.group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.attendanceFragment);
                studentDashboardBinding.bottomNav.bottomNavigation.show(3, true);
            }
        });

        studentDashboardBinding.bottomNav.notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.notificationFragment);
                studentDashboardBinding.bottomNav.bottomNavigation.show(4, true);
            }
        });

        studentDashboardBinding.bottomNav.setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.settingFragment);
                studentDashboardBinding.bottomNav.bottomNavigation.show(5, true);
            }
        });
    }

}