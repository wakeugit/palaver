package mobile.paluno.de.palaver.controller;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.View;

import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import com.google.android.gms.gcm.GcmListenerService;

import mobile.paluno.de.palaver.R;

import mobile.paluno.de.palaver.backend.HttpRequest;
import mobile.paluno.de.palaver.model.Message;

import mobile.paluno.de.palaver.gcm.PalaverGcmListenerService;

import mobile.paluno.de.palaver.model.SectionsPageAdapter;

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

    private String username = "";
    private String password = "";





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
                Intent intent = new Intent(PalaverMainActivity.this, PalaverAuthActivity.class);
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

        addContact.setVisibility(View.INVISIBLE);

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
        super.onResume();


        sharedPreferences = getSharedPreferences("mobile.paluno.de.palaver.login", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        String username1 = sharedPreferences.getString("mobile.paluno.de.palaver.Username", null);
        //String password = sharedPreferences.getString("mobile.paluno.de.palaver.Password", null);

        if (username1 != null && username1!="") {
            TextView t = (TextView) findViewById(R.id.tbUsername);
            t.setText(username1.toUpperCase());
        } else{
            checkAbmelden = true;
            Intent intent = new Intent(PalaverMainActivity.this, PalaverAuthActivity.class);
            startActivity(intent);
            finish();
        }


    }

    @Override
    protected void onPause() {
        super.onPause();
        if(!checkAbmelden) {
            editor.putBoolean("mobile.paluno.de.palaver.MainLaden", true);
            editor.commit();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(!sharedPreferences.getBoolean("mobile.paluno.de.palaver.Checked", false)){
            abmelden();
        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
        super.onBackPressed();
    }

    //Löschen der gespeicherten Daten und abmelden des GCM
    private  void abmelden(){
        PalaverGcmListenerService.isLoggedIn = false;
        editor.clear();
        editor.commit();
    }

    private void setupViewPager(ViewPager viewPager){

        sharedPreferences = getSharedPreferences("mobile.paluno.de.palaver.login", MODE_PRIVATE);
        username = sharedPreferences.getString("mobile.paluno.de.palaver.Username", null);
        password = sharedPreferences.getString("mobile.paluno.de.palaver.Password", null);
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new ChatsTabFragment(username, password), "CHATS");
        adapter.addFragment(new ContactsTabFragment(username, password), "FREUNDE");

        viewPager.setAdapter(adapter);
    }

}

