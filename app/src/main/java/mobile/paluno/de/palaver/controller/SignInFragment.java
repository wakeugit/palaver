package mobile.paluno.de.palaver.controller;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import mobile.paluno.de.palaver.R;
import mobile.paluno.de.palaver.backend.HttpRequest;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignInFragment extends Fragment {
    ViewGroup container;
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

    public SignInFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.container = container;
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_signin, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();

        sharedPreferences = getContext().getSharedPreferences("mobile.paluno.de.palaver.login", MODE_PRIVATE);
        mLoginFormView = container.findViewById(R.id.login_form);
        mProgressView  = container.findViewById(R.id.login_progress);

        Boolean isChecked = sharedPreferences.getBoolean("mobile.paluno.de.palaver.Checked", false);
        Boolean mainResume = sharedPreferences.getBoolean("mobile.paluno.de.palaver.MainLaden", false);

        // Set up the login form.
        mUsernameView = (EditText) container.findViewById(R.id.username_signin);
        mPasswordView = (EditText) container.findViewById(R.id.password_signin);

        if(isChecked || mainResume){
            mUsernameView.setText(loadUsername());
            mPasswordView.setText(loadPassword());

            attemptLogin();
        }

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

        Button mUserSignInButton = (Button) container.findViewById(R.id.user_sign_in_button);
        mUserSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        Button mRegisterButton = (Button) container.findViewById(R.id.register_fragment_button);

        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create fragment and give it an argument specifying the article it should show
                RegisterFragment registerFragment = new RegisterFragment();

                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                // Replace whatever is in the fragment_container view with this fragment,
                // and add the transaction to the back stack so the user can navigate back
                transaction.replace(R.id.fragment_auth_container, registerFragment);
                transaction.addToBackStack(null);

                // Commit the transaction
                transaction.commit();
            }
        });
    }


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

    private void save(String username, String password){
        editor = sharedPreferences.edit();

        mStaySigned =(CheckBox) container.findViewById(R.id.staySignIn);

        editor.clear();
        editor.putString("mobile.paluno.de.palaver.Username", username);
        editor.putString("mobile.paluno.de.palaver.Password", password);
        editor.putBoolean("mobile.paluno.de.palaver.Checked", mStaySigned.isChecked());
        editor.commit();
    }

    private String loadUsername(){
        return sharedPreferences.getString("mobile.paluno.de.palaver.Username", null);
    }

    private String loadPassword(){
        return sharedPreferences.getString("mobile.paluno.de.palaver.Password", null);
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

            if(msgType == 1) return true;

            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                System.out.println(getActivity());
                //Erfolgreiche Verbidung, navigieren weiter
                Intent intent = new Intent(getActivity(), PalaverMainActivity.class);
                save(mUsername, mPassword);
                startActivity(intent);
                getActivity().finish();
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
                else ;
                    Toast.makeText(getActivity(), "Fehler", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}
