package com.example.onTime;

import androidx.annotation.ColorRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import com.example.onTime.mrt.HomeActivity;
import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.github.paolorotolo.appintro.AppIntroViewPager;
import com.github.paolorotolo.appintro.model.SliderPage;

public class IntroActivity extends AppIntro {

    private int PRIMARY_COLOR;
    private int UNSELECTED_PRIMARY_COLOR;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.PRIMARY_COLOR = this.getResources().getColor(R.color.colorPrimary);
        this.UNSELECTED_PRIMARY_COLOR = Color.parseColor("#609dd1");

        SliderPage sp = new SliderPage();
        sp.setTitle("Bienvenue sur onTime !");
        sp.setDescription("Créer votre routine matinale n'a jamais été aussi simple !");
        sp.setImageDrawable(R.drawable.ic_alarm_on_56px);
        sp.setBgColor(Color.WHITE);
        sp.setTitleColor(PRIMARY_COLOR);
        sp.setDescColor(PRIMARY_COLOR);

        SliderPage sp1 = new SliderPage();
        sp1.setTitle("Routine matinale");
        sp1.setDescription("Afin de ne plus jamais arriver en retard, créez votre routine matinale, ça se passe dans l'onglet Gérer > Mes morning routines");
        sp1.setImageDrawable(R.drawable.ic_alarm_on_56px);
        sp1.setBgColor(Color.WHITE);
        sp1.setTitleColor(PRIMARY_COLOR);
        sp1.setDescColor(PRIMARY_COLOR);

        SliderPage sp2 = new SliderPage();
        sp2.setTitle("Tâches");
        sp2.setDescription("Agrémentez votre routine matinale avec des tâches à réaliser pendant cette dernière. Un nom, une durée et c'est validé ! Vous pouvez même sauvegarder vos tâches pour les réutiliser dans d'autres Morning Routine : ce sont les tâches récurrentes !");
        sp2.setImageDrawable(R.drawable.ic_alarm_on_56px);
        sp2.setBgColor(Color.WHITE);
        sp2.setTitleColor(PRIMARY_COLOR);
        sp2.setDescColor(PRIMARY_COLOR);

        SliderPage sp3 = new SliderPage();
        sp3.setTitle("Tâches récurrentes");
        sp3.setDescription("Afin de ne pas toujours créer les même tâches, créez vos tâches récurrentes que vous pourrez réutiliser dans vos Morning Routine sans avoir à les recréer. Ça se passe dans l'onglet Gérer > Mes tâches récurrentes !");
        sp3.setImageDrawable(R.drawable.ic_alarm_on_56px);
        sp3.setBgColor(Color.WHITE);
        sp3.setTitleColor(PRIMARY_COLOR);
        sp3.setDescColor(PRIMARY_COLOR);

        SliderPage sp4 = new SliderPage();
        sp4.setTitle("Trajets");
        sp4.setDescription("Créez différents trajets pour vos Morning Routine. Ces trajets serviront de base de calcul pour l'application onTime afin de calculer votre heure de réveil à la minute près ! Pour les trajets, ça se passe dans l'onglet Gérer > Mes trajets !");
        sp4.setImageDrawable(R.drawable.ic_alarm_on_56px);
        sp4.setBgColor(Color.WHITE);
        sp4.setTitleColor(PRIMARY_COLOR);
        sp4.setDescColor(PRIMARY_COLOR);

        SliderPage sp5 = new SliderPage();
        sp5.setTitle("Heure d'arrivée");
        sp5.setDescription("Définissez une heure d'arrivée à votre point de destination pour le lendemain. Ainsi, vous pourrez cliquer sur la cloche pour mettre en place le prochain réveil à l'heure calculée !");
        sp5.setImageDrawable(R.drawable.ic_alarm_on_56px);
        sp5.setBgColor(Color.WHITE);
        sp5.setTitleColor(PRIMARY_COLOR);
        sp5.setDescColor(PRIMARY_COLOR);

        SliderPage sp6 = new SliderPage();
        sp6.setTitle("Bonne nuit !");
        sp6.setDescription("Ça y est ! Vous êtes près à aller vous coucher, l'application se charge du reste ! ;)");
        sp6.setImageDrawable(R.drawable.ic_alarm_on_56px);
        sp6.setBgColor(Color.WHITE);
        sp6.setTitleColor(PRIMARY_COLOR);
        sp6.setDescColor(PRIMARY_COLOR);

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

    private void saveAndQuit(){
        // Enregistrement du fait que l'utilisateur a passé l'intro, pour plus ne lui montrer ensuite
        Context context = getApplicationContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences("onTimePreferences", Context.MODE_PRIVATE);
        sharedPreferences.edit().putBoolean("userHasFinishedInitialSetup", true).apply();

        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);

        startActivity(intent);
    }
}