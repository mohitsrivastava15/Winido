package com.winido.app.login;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.mikepenz.iconics.context.IconicsLayoutInflater;
import com.winido.app.R;
import com.winido.app.helper.VolleyRequest;
import com.winido.app.helper.Config;
import com.winido.app.helper.PrefManager;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class GoogleSignInActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {
    private static final String TAG = GoogleSignInActivity.class.getSimpleName();
    private static final int RC_SIGN_IN = 9001;

    private GoogleApiClient mGoogleApiClient;
    private ProgressDialog mProgressDialog;
    private TextView mStatusTextView;
    private Context appContext;

    private View inviteCodeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LayoutInflaterCompat.setFactory(getLayoutInflater(), new IconicsLayoutInflater(getDelegate()));
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_google_sign_in);

        //Setup inviteCode view
        this.inviteCodeView = findViewById(R.id.relativeLayout_inviteCode);
        this.setupInviteCodeRelativeLayout();

        findViewById(R.id.sign_in_button).setOnClickListener(this);
        this.appContext = getApplicationContext();

        this.mProgressDialog = new ProgressDialog(this);

        // [START configure_signin]
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                //.requestScopes(new Scope(Scopes.PLUS_LOGIN))
                .requestEmail()
                .build();
        // [END configure_signin]

        // [START build_client]
        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                //.addApi(Plus.API)
                .build();
        // [END build_client]

        // [START customize_button]
        // Customize sign-in button. The sign-in button can be displayed in
        // multiple sizes and color schemes. It can also be contextually
        // rendered based on the requested scopes. For example. a red button may
        // be displayed when Google+ scopes are requested, but a white button
        // may be displayed when only basic profile is requested. Try adding the
        // Scopes.PLUS_LOGIN scope to the GoogleSignInOptions to see the
        // difference.
        SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_WIDE);
        signInButton.setScopes(gso.getScopeArray());
        signInButton.setColorScheme(SignInButton.COLOR_LIGHT);
        // [END customize_button]
    }

    public void setupInviteCodeRelativeLayout() {

    }

    @Override
    public void onStart() {
        super.onStart();

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.d(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            showProgressDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    hideProgressDialog();
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }

    // [START onActivityResult]
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }
    // [END onActivityResult]

    // [START handleSignInResult]
    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            updateUI(true, acct);

            //sendSMS("+918197433877", "Your OTP is 6523");

        } else {
            // Signed out, show unauthenticated UI.
            updateUI(false, null);
        }
    }
    // [END handleSignInResult]

    // [START signIn]
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    // [END signIn]

    // [START signOut]
    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // [START_EXCLUDE]
                        updateUI(false, null);
                        // [END_EXCLUDE]
                    }
                });
    }
    // [END signOut]

    // [START revokeAccess]
    private void revokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // [START_EXCLUDE]
                        updateUI(false, null);
                        // [END_EXCLUDE]
                    }
                });
    }
    // [END revokeAccess]

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }
    private String email;
    private String name;
    private String accountId;
    private String photoUrl;

    private void updateUI(boolean signedIn,GoogleSignInAccount acct) {
        if (signedIn) {
            this.email = acct.getEmail();
            this.name = acct.getDisplayName();
            this.accountId = acct.getId();
            this.photoUrl = acct.getPhotoUrl().toString();

            /**
             * Check if the user needs an invite code.
             * If yes, show the View for entering the invite code. If no, re-direct to the home screen
             */
            showProgressDialog("Please wait while we complete your account sign-in!");
            verifyInviteCode(appContext, acct.getEmail(), "");

            setupLoginCache(acct);


        } else {

            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
        }
    }

    private void setupLoginCache(GoogleSignInAccount acct) {
        PrefManager.saveToPrefs(getApplicationContext(), PrefManager.IS_LOGGED_IN_KEY, true);
        PrefManager.saveToPrefs(getApplicationContext(), PrefManager.NAME_KEY, acct.getDisplayName());
        PrefManager.saveToPrefs(getApplicationContext(), PrefManager.EMAIL_KEY, acct.getEmail());
        PrefManager.saveToPrefs(getApplicationContext(), PrefManager.PHOTO_KEY, acct.getPhotoUrl().toString());
        PrefManager.saveToPrefs(getApplicationContext(), PrefManager.ID_KEY, acct.getId());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
        }
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    private void showProgressDialog(String message) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setIndeterminate(true);
        }
        mProgressDialog.setMessage(message);

        mProgressDialog.show();
        //timerDelayRemoveDialog(10000, mProgressDialog);
    }

    /**
     * Method to validate the user email / invite code on the server
     * @param email  user email address
     */
    public void verifyInviteCode(final Context context, final String email, final String inviteCode) {
        StringRequest strReq = new StringRequest(Request.Method.POST,
                Config.URL_VERIFY_INVITE_CODE, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, response.toString());

                try {
                    JSONObject responseObj = new JSONObject(response);

                    // Parsing json object response
                    // response will be a json object
                    int code = responseObj.getInt("code");
                    String message = responseObj.getString("message");

                    if (code == Config.CHECKPRESENCEOFINVITECODEFOREMAIL_INVITE_CODE_FOUND_FOR_EMAIL) {
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                        hideProgressDialog();
                        /**
                         * If the user has reached this stage, there is no user record found for the user. Hence we should proceed
                         * to he
                         */
                        Intent i = new Intent(GoogleSignInActivity.this, OtpActivity.class);
                        i.putExtra("name", name);
                        i.putExtra("email", email);
                        i.putExtra("id", accountId);
                        i.putExtra("photoUrl", photoUrl);

                        startActivity(i);
                    } else if (code == Config.CHECKPRESENCEOFINVITECODEFOREMAIL_USER_RECORD_FOUND_FOR_EMAIL) {
                        /**
                         * TODO: Redirect the user to his home screen
                         */
                        hideProgressDialog();
                        /**
                         * If the user has reached this stage, there is no user record found for the user. Hence we should proceed
                         * to he
                         */
                        Intent i = new Intent(GoogleSignInActivity.this, OtpActivity.class);
                        i.putExtra("name", name);
                        i.putExtra("email", email);
                        i.putExtra("id", accountId);
                        i.putExtra("photoUrl", photoUrl);

                        startActivity(i);
                    } else {
                        /**
                         * If you have landed here, there is no invite code present for the user. Ask him to enter an invite code
                         * and then re-call this function with the provided invite code.
                         */
                        Toast.makeText(context,
                                "Error: " + message,
                                Toast.LENGTH_LONG).show();
                        inviteCodeView.setVisibility(View.VISIBLE);
                    }


                } catch (JSONException e) {
                    Toast.makeText(context,
                            "Error: " + response.toString(),
                            Toast.LENGTH_LONG).show();

                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error: " + error.getMessage());
                Toast.makeText(context,
                        error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {

            /**
             * Passing user parameters to our server
             * @return
             */
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                params.put("invite_code", inviteCode);

                Log.i(TAG, "Posting params: " + params.toString());

                return params;
            }

        };

        // Adding request to request queue
        VolleyRequest.getInstance(appContext).addToRequestQueue(strReq);
    }
}
