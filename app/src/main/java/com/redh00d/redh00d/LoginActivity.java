package com.redh00d.redh00d;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.*;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.redh00d.redh00d.Forms.LoginForm;
import com.redh00d.redh00d.Utils.RestClient;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Objects;


/**
 * A login screen that offers login via email/password.

 */
public class LoginActivity extends Activity {

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email_text);

        mPasswordView = (EditText) findViewById(R.id.password_text);
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

        Button exitButton = (Button) findViewById(R.id.exit_button);
        exitButton.setOnClickListener(new OnClickListener(){
            public void onClick(View view){
                setResult( Activity.RESULT_CANCELED );
                finish();
            }
        });

        /*
         * Sign in
         * - on click = try to log
         */
        Button mEmailSignInButton = (Button) findViewById(R.id.sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        /*
         * Register
         */
        Button registerButton = (Button) findViewById(R.id.register_button);
        registerButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent( LoginActivity.this, RegisterActivity.class );
                LoginActivity.this.startActivity( i );
                Log.i("Content", "Switch to login activity");
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        mEmailView.requestFocus(); // @todo in xml
    }

    /**
     * Method get triggered when login button is clicked
     *
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin() {
        if ( mAuthTask != null ) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        /*
         * Validation process
         * - Email is checked last to get always the focus in case of it has an error
         */
        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !(LoginForm.validatePassword(password))) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }
        // Check for a valid email address.
        if (TextUtils.isEmpty(email) || !LoginForm.validateEmail(email) ) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
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
            mAuthTask = new UserLoginTask(email, password);
            // Start the login task asynchronous
            mAuthTask.execute((Void) null);
            Log.i("ds","sqdsqd");
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    public void showProgress(final boolean show) {
        // show and hide the relevant UI components.
        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    // Uses AsyncTask to create a task away from the main UI thread. This task takes a
    // URL string and uses it to create an HttpUrlConnection. Once the connection
    // has been established, the AsyncTask downloads the contents of the webpage as
    // an InputStream. Finally, the InputStream is converted into a string, which is
    // displayed in the UI by the AsyncTask's onPostExecute method.
    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;
        private int statusCode;

        public UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        /**
         * Do the task after .execute call
         */
        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            Log.i("doAuthWithWS", "Start auth with web service");
            RequestParams rParams = new RequestParams();
            rParams.put("username", mEmail);
            rParams.put("password", mPassword);

            HashMap<String, Object> result;
            result = this.performAuth( rParams );

            statusCode = (Integer)result.get("status");

            // TODO: register the new account here.
            return (Boolean)result.get("success");
        }

        /**
         * When task is completed destroy the task and finish activity in success case
         */
        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if(success){
                if (statusCode == 200) {
                    Toast.makeText(getApplicationContext(), "You are successfully logged in!", Toast.LENGTH_LONG).show();
                }
            }
            else {
                if (statusCode == 401) {
                    Toast.makeText(getApplicationContext(), "Bad credentials", Toast.LENGTH_LONG).show();
                }
                // When Http response code is '404'
                else if (statusCode == 404) {
                    Toast.makeText(getApplicationContext(), "Requested resource not found", Toast.LENGTH_LONG).show();
                }
                // When Http response code is '500'
                else if (statusCode == 500) {
                    Toast.makeText(getApplicationContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                }
                // When Http response code other than 404, 500
                else {
                    Toast.makeText(getApplicationContext(), "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet or remote server is not up and running]", Toast.LENGTH_LONG).show();
                }
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }

        private HashMap<String, Object> performAuth( RequestParams params ){

            final HashMap<String, Object> result = new HashMap();
            // Make RESTful webservice call using AsyncHttpClient object
            RestClient.get("/auth", params, new AsyncHttpResponseHandler() {

                // When the response returned by REST has Http response code '200'
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                    result.put("success", true);
                    result.put("status", statusCode);
                }

                // When the response returned by REST has Http response code other than '200'
                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                    result.put("success", false);
                    result.put("status", statusCode);
                }

            }, true);
            return result;
        }
    }
}



