package mobile.paluno.de.palaver.controller;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import mobile.paluno.de.palaver.R;

public class PalaverMainActivity extends AppCompatActivity {
    private static final String TAG = "PalaverMainActivity";



    private SectionsPageAdapter mSectionsPageAdapter;

    private ViewPager mViewPager;

    //Speichern und übergeben von Daten an andere Aktivitäten
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    //Ist der Button Abmelden gedrückt worden?
    //Dafür da, damit onPause nicht MainLaden auf true setzt
    private Boolean checkAbmelden = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_palaver_main);

        mSectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());
        // set up the view pager with the sections adapter
        mViewPager = (ViewPager) findViewById(R.id.container);
        setupViewPager(mViewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        Button btAbmelden = (Button) findViewById(R.id.btAbmelden);
        btAbmelden.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abmelden();
                checkAbmelden = true;
                Intent intent = new Intent(PalaverMainActivity.this, PalaverLoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //Der Kontakt Hinzufügen Button, soll nur im Kontakte Tab zu sehen sein
        final FloatingActionButton addContact=(FloatingActionButton)findViewById(R.id.addContact);
        addContact.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(PalaverMainActivity.this, NewContactActivity.class);
                startActivity(intent);

            }

        });
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager){
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                super.onTabSelected(tab);
                if(tab.getPosition() == 1)
                    addContact.setVisibility(View.VISIBLE);
                else
                    addContact.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    protected void onResume() {
        sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        String userName = sharedPreferences.getString("Username", null);
        TextView t = (TextView)findViewById(R.id.tbUsername);
        t.setText(userName.toUpperCase());

        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(!checkAbmelden) {
            editor.putBoolean("MainLaden", true);
            editor.commit();
        }

//        Toast.makeText(PalaverMainActivity.this, "onPause()", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onStop() {
        super.onStop();
//        Toast.makeText(PalaverMainActivity.this, "onStop()", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(!sharedPreferences.getBoolean("Checked", false)){
            abmelden();
        }
//        Toast.makeText(PalaverMainActivity.this, "onDestroy()", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
        super.onBackPressed();
    }

    //Löschen der gespeicherten Daten
    private  void abmelden(){
        editor.clear();
        editor.commit();
    }

    private void setupViewPager(ViewPager viewPager){
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());

        adapter.addFragment(new ChatsFragment(), "CHATS");
        adapter.addFragment(new ContactsFragment("wilfried", "wilfried"), "KONTAKTE");
        viewPager.setAdapter(adapter);

    }



}
