package mobile.paluno.de.palaver.controller;

import android.graphics.Point;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import mobile.paluno.de.palaver.R;

public class PalaverAuthActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        if (findViewById(R.id.fragment_auth_container) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            // Create a new Fragment to be placed in the activity layout
            SignInFragment firstFragment = new SignInFragment();

            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            firstFragment.setArguments(getIntent().getExtras());

            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_auth_container, firstFragment).commit();
        }

        startAnimation();
    }

    public void startAnimation() {
        final ImageView imageView = (ImageView) findViewById(R.id.palaver_logo);
        //TODO:
        //Position muss in der Mitte des Bildschirms starten, hier provisorisch 300
        Display display = getWindowManager().getDefaultDisplay();
        final Point size = new Point();
        display.getSize(size);
        float height = size.y;
        float centerY = height/3;
//        Toast.makeText(PalaverAuthActivity.this, centerY + " " + imageView.height, Toast.LENGTH_LONG).show();
        imageView.setY(centerY);

        final Animation fadeInAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in_animation);
        fadeInAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                findViewById(R.id.fragment_auth_container).setVisibility(View.GONE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Animation slideUpAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.move_up);

                slideUpAnim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        findViewById(R.id.fragment_auth_container).setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

                imageView.startAnimation(slideUpAnim);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        imageView.startAnimation(fadeInAnim);
    }
}
