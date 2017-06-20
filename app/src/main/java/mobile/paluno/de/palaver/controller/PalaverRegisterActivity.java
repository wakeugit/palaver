package mobile.paluno.de.palaver.controller;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import mobile.paluno.de.palaver.R;
import mobile.paluno.de.palaver.backend.HttpRequest;

/**
 * A login screen that offers login via email/password.
 */
public class PalaverRegisterActivity extends AppCompatActivity /*implements LoaderCallbacks<Cursor> */{

    /*
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserRegisterTask mRegisterTask = null;

    // UI references.
    private EditText mUsernameView;
    private EditText mPasswordView;
    private EditText mPasswordConfirmView;
    private View mProgressView;
    private View mRegisterFormView;

    // Fehlerbehandlung beim Anmelden
    private Boolean usernameInUse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUsernameInUse(false);

        setContentView(R.layout.activity_palaver_register);
        // Set up the registration form.
        mUsernameView = (EditText) findViewById(R.id.username_register);
        mPasswordView = (EditText) findViewById(R.id.password_register);
        mPasswordConfirmView = (EditText) findViewById(R.id.password_confirm);
        mPasswordConfirmView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.register || id == EditorInfo.IME_NULL) {
                    attemptRegister();
                    return true;
                }
                return false;
            }
        });

        Button mUserRegisterButton = (Button) findViewById(R.id.user_register_button);
        mUserRegisterButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegister();
            }
        });

        mRegisterFormView = findViewById(R.id.register_form);
        mProgressView = findViewById(R.id.register_progress);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(PalaverRegisterActivity.this, PalaverLoginActivity.class);
        startActivity(intent);
        finish();
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        setUsernameInUse(false);
//    }

    public Boolean getUsernameInUse() {
        return usernameInUse;
    }

    public void setUsernameInUse(Boolean usernameInUse) {
        this.usernameInUse = usernameInUse;
    }

    /**
     * Attempts to sign in or register the account specified by the register form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptRegister() {
        if (mRegisterTask != null) {
            return;
        }

        View focusView = null;
        boolean cancel = false;

        if (getUsernameInUse()) {
            mUsernameView.setError(getString(R.string.error_existing_username_register));
            focusView = mUsernameView;
            cancel = true;
        }

        // Reset errors.
        mUsernameView.setError(null);
        mPasswordView.setError(null);
        mPasswordConfirmView.setError(null);

        // Store values at the time of the login attempt.
        String username = mUsernameView.getText().toString();
        String password = mPasswordView.getText().toString();
        String password_confirm = mPasswordConfirmView.getText().toString();

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        }

        if (TextUtils.isEmpty(password_confirm)) {
            mPasswordConfirmView.setError(getString(R.string.error_field_required));
            focusView = mPasswordConfirmView;
            cancel = true;
        }

        //Prüfen ob Passwörter gleich sind
        if (!password.equals(password_confirm)){
            mPasswordConfirmView.setError(getString(R.string.error_not_matching_password));
            focusView = mPasswordConfirmView;
            cancel = true;
        }

        // Check for a valid username address.
        if (TextUtils.isEmpty(username)) {
            mUsernameView.setError(getString(R.string.error_field_required));
            focusView = mUsernameView;
            cancel = true;
        }

        if(getUsernameInUse()){
            mUsernameView.setError(getString(R.string.error_existing_username_register));
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

            mRegisterTask = new UserRegisterTask(username, password);
            mRegisterTask.execute((Void) null);
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

            mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mRegisterFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

//    @Override
//    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
//        return new CursorLoader(this,
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
//                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
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
//
//
//
//    private interface ProfileQuery {
//        String[] PROJECTION = {
//                ContactsContract.CommonDataKinds.Email.ADDRESS,
//                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
//        };
//
//        int ADDRESS = 0;
//        int IS_PRIMARY = 1;
//    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserRegisterTask extends AsyncTask<Void, Void, Boolean> {

        private final String mUsername;
        private final String mPassword;
        private JSONObject res= new JSONObject();

        UserRegisterTask(String username, String password) {
            mUsername = username;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            //Verbindung herstellen mit Palavaer Server
            HttpRequest httpRequest = new HttpRequest();

            try {
                // Antwort des Palaver Servers
                res = httpRequest.benutzerRegistrieren(mUsername, mPassword);
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

            if(msgType == 1) {
                setUsernameInUse(false);
                return true;
            }
            else if(msgType == 0){
                setUsernameInUse(true);
            }
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mRegisterTask = null;
            showProgress(false);

            if (success) {
                try{
                    Toast.makeText(PalaverRegisterActivity.this, res.getString("Info"), Toast.LENGTH_LONG).show();
                } catch (JSONException e){
                    e.printStackTrace();
                }
                Intent intent = new Intent(PalaverRegisterActivity.this, PalaverLoginActivity.class);
                startActivity(intent);
                finish();
            } else {
                try {
                    if(res.getInt("MsgType") == 0){
                        setUsernameInUse(true);
                        mUsernameView.setError(getString(R.string.error_existing_username_register));
                        mUsernameView.requestFocus();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
//                Toast.makeText(PalaverRegisterActivity.this, "Fehler", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onCancelled() {
            mRegisterTask = null;
            showProgress(false);
        }
    }
}
