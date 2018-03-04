package info.manipal.aesher.manipal;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import info.manipal.aesher.manipal.adapters.MainPager;

public class MainActivity extends AppCompatActivity {

    private TextToSpeech tts;


    @BindView(R.id.tab)
    TabLayout tabLayout;
    @BindView(R.id.viewpager)
    ViewPager Pagerfragments;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.toolbar_title)
    TextView tTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        Pagerfragments.setOffscreenPageLimit(2);

        tabLayout.setupWithViewPager(Pagerfragments);
        Typeface customFont = Typeface.createFromAsset(getAssets(), "fonts/BrandonGrotesque-Bold.ttf");
        tTitle.setTypeface(customFont);

        Toast.makeText(this,"Please enjoy the voice assistant while we fetch you the latest view of Manipal",Toast.LENGTH_LONG).show();

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);

        MainPager adapter = new MainPager(getSupportFragmentManager());
        Pagerfragments.setAdapter(adapter);
        Pagerfragments.setCurrentItem(1);
        setIcons();


        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                switch (tab.getPosition()){
                    case 0:
                        tTitle.setText("Get Information By List");
                        break;
                    case 1:
                        tTitle.setText("Get Information On Spot");
                        break;


                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


    }



    public void setIcons(){
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_description);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_home);



        tabLayout.getTabAt(0).getIcon().setColorFilter(Color.parseColor("#810000"), PorterDuff.Mode.SRC_IN);
        tabLayout.getTabAt(1).getIcon().setColorFilter(Color.parseColor("#810000"), PorterDuff.Mode.SRC_IN);


    }



}
