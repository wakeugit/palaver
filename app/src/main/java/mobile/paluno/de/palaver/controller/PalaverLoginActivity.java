package mobile.paluno.de.palaver.controller;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;

import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import mobile.paluno.de.palaver.R;
import mobile.paluno.de.palaver.backend.HttpRequest;

/**
 * A login screen that offers login via email/password.
 */
public class PalaverLoginActivity extends AppCompatActivity{

    /*
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private EditText mUsernameView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private CheckBox mStaySigned;

    //Speichern und übergeben von Daten an andere Aktivitäten
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_palaver_login);

//        Intent intent = new Intent(PalaverLoginActivity.this, PalaverAuthActivity.class);
//        startActivity(intent);
//        finish();

        //Animation Splash
        startAnimation();

        // Set up the login form.
        mUsernameView = (EditText) findViewById(R.id.username);
        mPasswordView = (EditText) findViewById(R.id.password);


        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mUserRegisterInButton = (Button) findViewById(R.id.user_register_button);
        mUserRegisterInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PalaverLoginActivity.this, PalaverRegisterActivity.class);
                moveTaskToBack(true);
                startActivity(intent);
            }
        });

        Button mUserSignInButton = (Button) findViewById(R.id.user_sign_in_button);
        mUserSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    public void startAnimation() {
        final ImageView imageView = (ImageView) findViewById(R.id.palaver_logo);
        //TODO:
        //Position muss in der Mitte des Bildschirms starten, hier provisorisch 300
        Display display = getWindowManager().getDefaultDisplay();
        final Point size = new Point();
        display.getSize(size);
        float centerY=size.y/3;
        imageView.setY(centerY);
        Animation fadeInAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in_animation);
        fadeInAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                findViewById(R.id.username).setVisibility(View.GONE);
                findViewById(R.id.password).setVisibility(View.GONE);
                findViewById(R.id.staySignIn).setVisibility(View.INVISIBLE);
                findViewById(R.id.user_register_button).setVisibility(View.INVISIBLE);
                findViewById(R.id.user_sign_in_button).setVisibility(View.INVISIBLE);
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
                        findViewById(R.id.username).setVisibility(View.VISIBLE);
                        findViewById(R.id.password).setVisibility(View.VISIBLE);
                        findViewById(R.id.staySignIn).setVisibility(View.VISIBLE);
                        findViewById(R.id.user_register_button).setVisibility(View.VISIBLE);
                        findViewById(R.id.user_sign_in_button).setVisibility(View.VISIBLE);
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

    private void save(String username, String password){
        editor = sharedPreferences.edit();

        mStaySigned =(CheckBox) findViewById(R.id.staySignIn);

        editor.clear();
        editor.putString("Username", username);
        editor.putString("Password", password);
        editor.putBoolean("Checked", mStaySigned.isChecked());
        editor.commit();
    }

    private String loadUsername(){
        return sharedPreferences.getString("Username", null);
    }

    private String loadPassword(){
        return sharedPreferences.getString("Password", null);
    }

    @Override
    protected void onResume() {

        sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);
        Boolean isChecked = sharedPreferences.getBoolean("Checked", false);
        Boolean mainResume = sharedPreferences.getBoolean("MainLaden", false);

        if(isChecked || mainResume){
            mUsernameView.setText(loadUsername());
            mPasswordView.setText(loadPassword());

            attemptLogin();
        }

        super.onResume();
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mUsernameView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String username = mUsernameView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Testen ob PW eingegeben wurde
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        }

        // Username eingegeben?
        if (TextUtils.isEmpty(username)) {
            mUsernameView.setError(getString(R.string.error_field_required));
            focusView = mUsernameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            //Toast.makeText(PalaverLoginActivity.this, "Username and password correct", Toast.LENGTH_LONG).show();

            mAuthTask = new UserLoginTask(username, password);
            mAuthTask.execute((Void) null);
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

//    @Override
//    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
//        return null;/*new CursorLoader(this,
//                // Retrieve data rows for the device user's 'profile' contact.
//                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
//                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,
//
//                // Select only email addresses.
//                ContactsContract.Contacts.Data.MIMETYPE +
//                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
//                .CONTENT_ITEM_TYPE},
//
//                // Show primary email addresses first. Note that there won't be
//                // a primary email address if the user hasn't specified one.
//                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");*/
//    }
//
//    @Override
//    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
//        /*List<String> emails = new ArrayList<>();
//        cursor.moveToFirst();
//        while (!cursor.isAfterLast()) {
//            //emails.add(cursor.getString(ProfileQuery.ADDRESS));
//            cursor.moveToNext();
//        }
//
//        addEmailsToAutoComplete(emails);*/
//    }
//
//    @Override
//    public void onLoaderReset(Loader<Cursor> cursorLoader) {
//
//    }
////
////
////
////    private interface ProfileQuery {
////        String[] PROJECTION = {
////                ContactsContract.CommonDataKinds.Email.ADDRESS,
////                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
////        };
////
////        int ADDRESS = 0;
////        int IS_PRIMARY = 1;
////    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mUsername;
        private final String mPassword;
        private JSONObject res= new JSONObject();

        UserLoginTask(String username, String password) {
            mUsername = username;
            mPassword = password;
        }

        public JSONObject getRes() {
            return res;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            //Verbindung herstellen mit Palavaer Server
            HttpRequest httpRequest = new HttpRequest();

            try {
                // Antwort des Palaver Servers
                res = httpRequest.benutzerValidate(mUsername, mPassword);
            } catch (Exception e) {
                //Wenn hier nicht klappt JSON Fehler oder kein Internet
                return false;
            }

            int msgType = 0;
            try{
                msgType = res.getInt("MsgType");

            }
            catch (Exception e){
                return false;
            }
            //Login ok
            if(msgType == 1) return true;

            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                //Erfolgreiche Verbidung, navigieren weiter
                try {
                    Toast.makeText(PalaverLoginActivity.this, res.getString("Info"), Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(PalaverLoginActivity.this, PalaverMainActivity.class);
                save(mUsername, mPassword);
                startActivity(intent);
                finish();
            } else {

                //Herausstellen von Fehlern
                String info = "";
                try {
                    info = res.getString("Info");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if(info.equals("Passwort nicht korrekt")) {
                    mPasswordView.setError(info);
                    mPasswordView.requestFocus();
                } else if (info.equals("Benutzer existiert nicht")) {
                    mUsernameView.setError(info);
                    mUsernameView.requestFocus();
                }
                else
                    Toast.makeText(PalaverLoginActivity.this, "Fehler", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

