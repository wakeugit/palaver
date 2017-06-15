package mobile.paluno.de.palaver.controller;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
 * A simple {@link Fragment} subclass.
 */
public class RegisterFragment extends Fragment {
    ViewGroup container;
    /*
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserRegisterTask mRegisterTask = null;

    // UI references.
    private EditText mUsernameView;
    private EditText mPasswordView;
    private EditText mPasswordConfirmView;
//    private View mProgressView;
//    private View mRegisterFormView;

    // Fehlerbehandlung beim Anmelden
    private Boolean usernameInUse;

    public RegisterFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.container = container;
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();

        setUsernameInUse(false);
        // Set up the registration form.
        mUsernameView = (EditText) container.findViewById(R.id.username_register);
        mPasswordView = (EditText) container.findViewById(R.id.password_register);
        mPasswordConfirmView = (EditText) container.findViewById(R.id.password_register_confirm);
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

        Button mUserRegisterButton = (Button) container.findViewById(R.id.user_register_button);
        mUserRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegister();
            }
        });

//        mRegisterFormView = container.findViewById(R.id.register_form);
//        mProgressView     = container.findViewById(R.id.register_progress);
    }

    public Boolean getUsernameInUse() {
        return usernameInUse;
    }

    public void setUsernameInUse(Boolean usernameInUse) {
        this.usernameInUse = usernameInUse;
    }

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
            //Toast.makeText(PalaverLoginActivity.this, "Username and password correct", Toast.LENGTH_LONG).show();

            mRegisterTask = new UserRegisterTask(username, password);
            mRegisterTask.execute((Void) null);
        }
    }

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
//            showProgress(false);

            if (success) {
                try{
                    Toast.makeText(getActivity(), res.getString("Info"), Toast.LENGTH_LONG).show();
                } catch (JSONException e){
                    e.printStackTrace();
                }
                Intent intent = new Intent(getActivity(), PalaverLoginActivity.class);
                startActivity(intent);
                getActivity().finish();
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
                Toast.makeText(getActivity(), "Fehler", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onCancelled() {
            mRegisterTask = null;
//            showProgress(false);
        }
    }


}
