package co.rapiddelivery.src;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.support.design.widget.TextInputLayout;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import co.rapiddelivery.RDApplication;
import co.rapiddelivery.network.APIClient;
import co.rapiddelivery.network.LoginResponse;
import co.rapiddelivery.utils.KeyConstants;
import co.rapiddelivery.utils.SPrefUtils;
import co.rapiddelivery.views.CustomTextInputEditText;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();
    // UI references.
    private TextInputLayout mInputLayoutUsername;
    private TextInputLayout mInputLayoutPassword;
    private CustomTextInputEditText mEdtUsername;
    private CustomTextInputEditText mEdtPassword;
    private View mProgressView;
    private View mLoginFormView;

    private Context mAppContext;
    private Context mActivityContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAppContext = this.getApplicationContext();
        mActivityContext = this;

        int loginStatus = SPrefUtils.getIntegerPreference(mAppContext, SPrefUtils.LOGIN_STATUS, KeyConstants.LOGIN_STATUS_BLANK);

        if (loginStatus == KeyConstants.LOGIN_STATUS_LOGGED_IN) {
            String loggedInUserDetails = SPrefUtils.getStringPreference(mAppContext, SPrefUtils.LOGGEDIN_USER_DETAILS);

            if (loggedInUserDetails != null) {
                ((RDApplication)getApplication()).setAppOwnerData(new Gson().fromJson(loggedInUserDetails, LoginResponse.class));
            }


            Intent intent = new Intent(mActivityContext, TabActivity.class);
            mActivityContext.startActivity(intent);
            finish();

            Toast.makeText(mActivityContext, "Welcome back " + RDApplication.getAppOwnerData().getName() + "!", Toast.LENGTH_SHORT).show();
        }
        // Set up the login form.
        mInputLayoutUsername = (TextInputLayout) findViewById(R.id.txt_input_layout_username);
        mInputLayoutPassword = (TextInputLayout) findViewById(R.id.txt_input_layout_password);
        mEdtUsername = (CustomTextInputEditText) findViewById(R.id.edt_username);
        mEdtPassword = (CustomTextInputEditText) findViewById(R.id.edt_password);
        mEdtPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        mEdtUsername.setText("aaqyl.chagla");
        mEdtPassword.setText("rapid123");

        Button mSignInButton = (Button) findViewById(R.id.sign_in_button);
        mSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {

        // Reset errors.
        mEdtUsername.setError(null);
        mEdtPassword.setError(null);

        // Store values at the time of the login attempt.
        final String username = mEdtUsername.getText().toString();
        final String password = mEdtPassword.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mEdtPassword.setError(getString(R.string.error_invalid_password));
            focusView = mEdtPassword;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(username)) {
            mEdtUsername.setError(getString(R.string.error_field_required));
            focusView = mEdtUsername;
            cancel = true;
        } else if (!isEmailValid(username)) {
            mEdtUsername.setError(getString(R.string.error_invalid_username));
            focusView = mEdtUsername;
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

            APIClient.getClient().login(username, password).enqueue(new Callback<LoginResponse>() {
                @Override
                public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                    showProgress(false);
                    LoginResponse loginResponse = response.body();
                    switch (loginResponse.getStatusCode()) {
                        case "200" :
                            loginResponse.setPassword(password);
                            loginResponse.setUserName(username);
                            SPrefUtils.setIntegerPreference(mAppContext, SPrefUtils.LOGIN_STATUS, KeyConstants.LOGIN_STATUS_LOGGED_IN);
                            SPrefUtils.setStringPreference(mAppContext, SPrefUtils.LOGGEDIN_USER_DETAILS, new Gson().toJson(loginResponse));

                            Intent intent = new Intent(mActivityContext, TabActivity.class);
                            mActivityContext.startActivity(intent);
                            finish();

                            Toast.makeText(mActivityContext, "Welcome " + loginResponse.getName() + "!", Toast.LENGTH_SHORT).show();
                            break;
                        case "400" :
                            Toast.makeText(mActivityContext, loginResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            break;
                        case "401" :
                            Toast.makeText(mActivityContext, loginResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            break;
                    }
                    Log.i(TAG, "LoginResponse : status_code => " + loginResponse.getStatusCode() + " message =>" + loginResponse.getMessage());
                }

                @Override
                public void onFailure(Call<LoginResponse> call, Throwable t) {
                    showProgress(false);
                    Log.e(TAG,  "LoginResponse : " + t.getLocalizedMessage(), t);
                }
            });
        }
    }

    private boolean isEmailValid(String email) {
        return !TextUtils.isEmpty(email);
    }

    private boolean isPasswordValid(String password) {
        return !TextUtils.isEmpty(password) && password.length() > 4;
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
}

