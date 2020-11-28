package com.example.onTime.activities;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import com.example.onTime.R;
import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.github.paolorotolo.appintro.model.SliderPage;

/**
 * Activity pour le tutoriel
 */
public class IntroActivity extends AppIntro {

    private int PRIMARY_COLOR;
    private int UNSELECTED_PRIMARY_COLOR;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.PRIMARY_COLOR = this.getResources().getColor(R.color.colorPrimary);
        this.UNSELECTED_PRIMARY_COLOR = Color.parseColor("#609dd1");

        SliderPage sp = new SliderPage();
        sp.setTitle(getString(R.string.slide0_title));
        sp.setDescription(getString(R.string.slide0_desc));
        sp.setImageDrawable(R.drawable.ic_alarm_on_56px);
        sp.setBgColor(Color.WHITE);
        sp.setTitleColor(PRIMARY_COLOR);
        sp.setDescColor(PRIMARY_COLOR);

        SliderPage sp1 = new SliderPage();
        sp1.setTitle(getString(R.string.slide1_title));
        sp1.setDescription(getString(R.string.slide1_desc));
        sp1.setImageDrawable(R.drawable.ic_alarm_on_56px);
        sp1.setBgColor(Color.WHITE);
        sp1.setTitleColor(PRIMARY_COLOR);
        sp1.setDescColor(PRIMARY_COLOR);
        sp1.setImageDrawable(R.drawable.slide1);

        SliderPage sp2 = new SliderPage();
        sp2.setTitle(getString(R.string.slide2_title));
        sp2.setDescription(getString(R.string.slide2_desc));
        sp2.setImageDrawable(R.drawable.ic_alarm_on_56px);
        sp2.setBgColor(Color.WHITE);
        sp2.setTitleColor(PRIMARY_COLOR);
        sp2.setDescColor(PRIMARY_COLOR);
        sp2.setImageDrawable(R.drawable.slide2);

        SliderPage sp3 = new SliderPage();
        sp3.setTitle(getString(R.string.slide3_title));
        sp3.setDescription(getString(R.string.slide3_desc));
        sp3.setImageDrawable(R.drawable.ic_alarm_on_56px);
        sp3.setBgColor(Color.WHITE);
        sp3.setTitleColor(PRIMARY_COLOR);
        sp3.setDescColor(PRIMARY_COLOR);
        sp3.setImageDrawable(R.drawable.slide3);

        SliderPage sp4 = new SliderPage();
        sp4.setTitle(getString(R.string.slide4_title));
        sp4.setDescription(getString(R.string.slide4_desc));
        sp4.setImageDrawable(R.drawable.ic_alarm_on_56px);
        sp4.setBgColor(Color.WHITE);
        sp4.setTitleColor(PRIMARY_COLOR);
        sp4.setDescColor(PRIMARY_COLOR);
        sp4.setImageDrawable(R.drawable.slide4);

        SliderPage sp5 = new SliderPage();
        sp5.setTitle(getString(R.string.slide5_title));
        sp5.setDescription(getString(R.string.slide5_desc));
        sp5.setImageDrawable(R.drawable.ic_alarm_on_56px);
        sp5.setBgColor(Color.WHITE);
        sp5.setTitleColor(PRIMARY_COLOR);
        sp5.setDescColor(PRIMARY_COLOR);
        sp5.setImageDrawable(R.drawable.slide5);

        SliderPage sp6 = new SliderPage();
        sp6.setTitle(getString(R.string.slide6_title));
        sp6.setDescription(getString(R.string.slide6_desc));
        sp6.setImageDrawable(R.drawable.ic_alarm_on_56px);
        sp6.setBgColor(Color.WHITE);
        sp6.setTitleColor(PRIMARY_COLOR);
        sp6.setDescColor(PRIMARY_COLOR);
        sp6.setImageDrawable(R.drawable.slide6);

        this.setImmersive(true);
        this.setColorSkipButton(PRIMARY_COLOR);
        this.setNextArrowColor(PRIMARY_COLOR);
        this.setIndicatorColor(PRIMARY_COLOR, UNSELECTED_PRIMARY_COLOR);
        this.setSeparatorColor(Color.WHITE);

        this.setColorDoneText(PRIMARY_COLOR);
        this.setSlideOverAnimation();

        this.addSlide(AppIntroFragment.newInstance(sp));
        this.addSlide(AppIntroFragment.newInstance(sp1));
        this.addSlide(AppIntroFragment.newInstance(sp2));
        this.addSlide(AppIntroFragment.newInstance(sp3));
        this.addSlide(AppIntroFragment.newInstance(sp4));
        this.addSlide(AppIntroFragment.newInstance(sp5));
        this.addSlide(AppIntroFragment.newInstance(sp6));
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        this.saveAndQuit();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        this.saveAndQuit();
    }

    private void saveAndQuit() {
        // Enregistrement du fait que l'utilisateur a passé l'intro, pour plus ne lui montrer ensuite
        Context context = getApplicationContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences("onTimePreferences", Context.MODE_PRIVATE);
        sharedPreferences.edit().putBoolean("userHasFinishedInitialSetup", true).apply();

        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
        finish();
    }
}