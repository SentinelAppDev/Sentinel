package com.pwc.sentinel;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class Splash extends AppCompatActivity {

   private static final int SPLASH_SCREEN = 3000;
   ImageView image;
   TextView text, text1;

   @Override
   protected void onCreate(@Nullable Bundle savedInstanceState) {

      super.onCreate(savedInstanceState);
      requestWindowFeature(Window.FEATURE_NO_TITLE);
      this.getWindow().setFlags(
              WindowManager.LayoutParams.FLAG_FULLSCREEN,
              WindowManager.LayoutParams.FLAG_FULLSCREEN);
      setContentView(R.layout.splash);

      Animation topanimate = AnimationUtils.loadAnimation(this, R.anim.top_animation);
      Animation botanimate = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);
      image = findViewById(R.id.welcomeimg);
      image.setAnimation(topanimate);
      text = findViewById(R.id.welcomemsg);
      text.setAnimation(botanimate);
      text1 = findViewById(R.id.welcomemsg1);
      text1.setAnimation(botanimate);

      new Handler().postDelayed(() -> {
         Intent intent = new Intent(Splash.this, MainActivity.class);

         Pair[] pairs = new Pair[3];
         pairs[0] = new Pair<View, String> (image, "logo_image");
         pairs[1] = new Pair<View, String> (text, "logo_text");
         pairs[2] = new Pair<View, String> (text1, "logo_text1");

         ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(Splash.this, pairs);
         startActivity(intent, options.toBundle());
         finish();
      }, SPLASH_SCREEN);
   }
}
