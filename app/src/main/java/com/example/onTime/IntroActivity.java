package com.example.onTime;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import com.example.onTime.mrt.HomeActivity;
import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.github.paolorotolo.appintro.model.SliderPage;

public class IntroActivity extends AppIntro {

    private int PRIMARY_COLOR;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.PRIMARY_COLOR = this.getResources().getColor(R.color.colorPrimary);

        SliderPage sp = new SliderPage();
        sp.setTitle("Bienvenue sur onTime !");
        sp.setDescription("Créer votre routine matinale n'a jamais été aussi simple !");
        sp.setImageDrawable(R.drawable.ic_alarm_on_56px);
        sp.setBgColor(Color.WHITE);
        sp.setTitleColor(PRIMARY_COLOR);
        sp.setDescColor(PRIMARY_COLOR);

        this.setImmersive(true);
        this.setColorSkipButton(PRIMARY_COLOR);
        this.setColorDoneText(PRIMARY_COLOR);

        this.addSlide(AppIntroFragment.newInstance(sp));
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        finish();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);

        // Enregistrement du fait que l'utilisateur a passé l'intro, pour plus ne lui montrer ensuite
        Context context = getApplicationContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences("onTimePreferences", Context.MODE_PRIVATE);
        sharedPreferences.edit().putBoolean("userHasFinishedInitialSetup", true).apply();

        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);

        startActivity(intent);
    }
}