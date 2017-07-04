package com.winido.app.login;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.winido.app.R;
import com.winido.app.helper.VolleyRequest;
import com.winido.app.helper.Config;
import com.winido.app.helper.ValidationHelper;
import com.winido.app.iotbutton.activity.DashMainActivity;

import org.json.JSONException;
import org.json.JSONObject;


import java.util.HashMap;
import java.util.Map;

import ernestoyaquello.com.verticalstepperform.VerticalStepperFormLayout;
import ernestoyaquello.com.verticalstepperform.interfaces.VerticalStepperForm;

public class OtpActivity extends AppCompatActivity implements VerticalStepperForm {
    private static final String TAG = OtpActivity.class.getSimpleName();
    private Context appContext;

    private VerticalStepperFormLayout verticalStepperForm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        this.appContext = getApplicationContext();

        getUserProfileFromIntent();

        initializeFormSteps();
    }

    private String email;
    private String name;
    private String accountId;
    private String photoUrl;
    private String mobile;

    Intent dashMainActivity;

    private void getUserProfileFromIntent() {

        this.name = getIntent().getStringExtra("name");
        this.email = getIntent().getStringExtra("email");
        this.photoUrl = getIntent().getStringExtra("photoUrl");
        this.accountId = getIntent().getStringExtra("id");

        dashMainActivity= new Intent(OtpActivity.this, DashMainActivity.class);
        dashMainActivity.putExtra("name", name);
        dashMainActivity.putExtra("email", email);
        dashMainActivity.putExtra("id", accountId);
        dashMainActivity.putExtra("photoUrl", photoUrl);

    }

    private void initializeFormSteps() {
        //Here we setup the steps for the activity
        String[] mySteps = {"Mobile Number", "OTP", "Address"};

        int colorPrimary = ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary);
        int colorPrimaryDark = ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark);


        // Finding the view
        verticalStepperForm = (VerticalStepperFormLayout) findViewById(R.id.vertical_stepper_form);


        // Setting up and initializing the form
        VerticalStepperFormLayout.Builder.newInstance(verticalStepperForm, mySteps, this, this)
                .primaryColor(colorPrimary)
                .primaryDarkColor(colorPrimaryDark)
                .displayBottomNavigation(true) // It is true by default, so in this case this line is not necessary
                .init();
    }

    // Information about the steps/fields of the form
    private static final int MOBILE_STEP_NUM = 0;
    private static final int OTP_STEP_NUM = 1;
    private static final int ADDRESS_STEP_NUM = 2;

    // METHODS THAT HAVE TO BE IMPLEMENTED TO MAKE THE LIBRARY WORK
    // (Implementation of the interface "VerticalStepperForm")

    @Override
    public View createStepContentView(int stepNumber) {
        // Here we generate the content view of the correspondent step and we return it so it gets
        // automatically added to the step layout (AKA stepContent)
        View view = null;
        switch (stepNumber) {
            case MOBILE_STEP_NUM:
                view = createMobileStep();
                break;
            case OTP_STEP_NUM:
                view = createOtpStep();
                break;
            case ADDRESS_STEP_NUM:
                view = createAddressStep();
                break;
        }
        return view;
    }

    private EditText editTextMobile;
    private View createMobileStep() {
        // Here we generate programmatically the view that will be added by the system to the step content layout
        LayoutInflater inflater = LayoutInflater.from(getBaseContext());
        LinearLayout mobileLayout = (LinearLayout) inflater.inflate(R.layout.layout_mobile, null, false);
        editTextMobile = (EditText) mobileLayout.findViewById(R.id.editTxt_inputMobile);

        editTextMobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!ValidationHelper.isValidPhoneNumber(editTextMobile.getText().toString()))
                    verticalStepperForm.setStepAsUncompleted(0, errorMobile);
                else {
                    //TODO: write code to call requestSms.php
                    mobile = editTextMobile.getText().toString();
                    requestOtpForMobile(appContext, email, mobile);
                    //verticalStepperForm.setActiveStepAsCompleted();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        return mobileLayout;
    }

    private EditText editTextOtp;
    private View createOtpStep() {
        // We use the OTP view because we need much more than just an editText (option to send the OTP again)
        LayoutInflater inflater = LayoutInflater.from(getBaseContext());
        LinearLayout otpLayout = (LinearLayout) inflater.inflate(R.layout.layout_otp, null, false);
        editTextOtp = (EditText) otpLayout.findViewById(R.id.editTxt_inputOtp);
        editTextOtp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!ValidationHelper.isValidOtp(editTextOtp.getText().toString()))
                    verticalStepperForm.setStepAsUncompleted(1, errorOtp);
                else {
                    //TODO: call verifyOtpForMobile code
                    //SetStepAsCompleted will only be called once we have verified from the backend that the OTP is valid
                    verifyOtpForMobile(appContext, email, mobile, editTextOtp.getText().toString());
                    //verticalStepperForm.setActiveStepAsCompleted();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return otpLayout;
    }

    private EditText editTextAddress;
    private View createAddressStep() {
        // Here we generate programmatically the view that will be added by the system to the step content layout
        editTextAddress = new EditText(this);
        editTextAddress.setSingleLine(true);
        editTextAddress.setHint("Your Address (e.g. SF 101)");
        editTextAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!ValidationHelper.isValidAddress(editTextAddress.getText().toString()))
                    verticalStepperForm.setStepAsUncompleted(2, errorAddress);
                else {
                    //TODO: Here you have to call the createUserRecord function on the backend
                    verticalStepperForm.setActiveStepAsCompleted();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        return editTextAddress;
    }

    private String errorMobile = "Mobile should have exactly 10 digits!";
    private String errorOtp = "OTP should have exactly 6 digits!";
    private String errorAddress = "Address should be of the form SF101!";
    private String incorrectOtp = "Incorrect OTP provided! Please retry!";

    @Override
    public void onStepOpening(int stepNumber) {

        switch (stepNumber) {
            case MOBILE_STEP_NUM:
                if(!ValidationHelper.isValidPhoneNumber(editTextMobile.getText().toString()))
                    verticalStepperForm.setStepAsUncompleted(stepNumber, errorMobile);
                else
                    verticalStepperForm.setActiveStepAsCompleted();
                break;
            case OTP_STEP_NUM:
                if(!ValidationHelper.isValidOtp(editTextOtp.getText().toString()))
                    verticalStepperForm.setStepAsUncompleted(stepNumber, errorOtp);
                else {
                    //TODO: Here we have to make a call to the backend to verify the provided OTP
                    //SetStepAsCompleted will only be called once we have verified from the backend that the OTP is valid
                    verticalStepperForm.setActiveStepAsCompleted();
                }
                break;
            case ADDRESS_STEP_NUM:
                if(!ValidationHelper.isValidAddress(editTextAddress.getText().toString()))
                    verticalStepperForm.setStepAsUncompleted(stepNumber, errorAddress);
                else {
                    //TODO: Here you have to call the createUserRecord function on the backend
                    verticalStepperForm.setStepAsCompleted(stepNumber);
                }
                break;
        }
    }
    private ProgressDialog progressDialog;
    @Override
    public void sendData() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(true);
        progressDialog.show();
        progressDialog.setMessage("Creating User Record!");
        executeDataSending();
    }

    private void executeDataSending() {
        //Let us call the DashMainActivity

        dashMainActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        appContext.startActivity(dashMainActivity);
    }

    private void dismissDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        progressDialog = null;
    }



    /**
     * Method to request OTP to the user's mobile
     * @param email  user email address
     * @param mobile user mobile
     */
    public void requestOtpForMobile(final Context context, final String email, final String mobile) {
        StringRequest strReq = new StringRequest(Request.Method.POST,
                Config.URL_REQUEST_OTP, new Response.Listener<String>() {

            @Override
            public void onResponse(final String response) {
                Log.d(TAG, response.toString());

                try {
                    JSONObject responseObj = new JSONObject(response);

                    // Parsing json object response
                    // response will be a json object
                    int code = responseObj.getInt("code");
                    final String message = responseObj.getString("message");
                    //progressDialogNonUI.dismiss();
                    hideLoadingDialog();
                    if (code == 0) {
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                        /**
                         * We can proceed to the next stage of vertical stepper
                         */
                        verticalStepperForm.setActiveStepAsCompleted();
                    } else {
                        /**
                         * If you have landed here, there is no invite code present for the user. Ask him to enter an invite code
                         * and then re-call this function with the provided invite code.
                         */
                        Toast.makeText(context, "Error: Could not send OTP to user mobile. Please retry!" + message, Toast.LENGTH_SHORT).show();

                    }


                } catch (JSONException e) {
                    Toast.makeText(context, "Error: " + response.toString(), Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(final VolleyError error) {
                Log.e(TAG, "Error: " + error.getMessage());
                Toast.makeText(context, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
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
                params.put("mobile", mobile);
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(appContext, "Sending OTP to your mobile!", Toast.LENGTH_SHORT).show();
                    }
                });
//                Toast.makeText(context, "Sending OTP to your mobile!", Toast.LENGTH_SHORT).show();

//                progressDialogNonUI.show("Sending OTP to your mobile!");
                //showLoadingDialog("", "Sending OTP to your mobile!");

                Log.i(TAG, "Posting params: " + params.toString());

                return params;
            }

        };

        // Adding request to request queue
        VolleyRequest.getInstance(appContext).addToRequestQueue(strReq);
    }
    /**
     * Method to validate the input OTP
     * @param email  user email address
     * @param mobile user mobile
     * @param otp the received OTP
     */
    public void verifyOtpForMobile(final Context context, final String email, final String mobile, final String otp) {
        StringRequest strReq = new StringRequest(Request.Method.POST,
                Config.URL_VERIFY_OTP, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, response.toString());

                try {
                    JSONObject responseObj = new JSONObject(response);

                    // Parsing json object response
                    // response will be a json object
                    int code = responseObj.getInt("code");
                    String message = responseObj.getString("message");

                    if (code == 0) {
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

                        /**
                         * We can proceed to the next stage of vertical stepper
                         */
                        verticalStepperForm.setActiveStepAsCompleted();
                    } else {
                        /**
                         * If you have landed here, there is no invite code present for the user. Ask him to enter an invite code
                         * and then re-call this function with the provided invite code.
                         */
                        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                        verticalStepperForm.setStepAsUncompleted(1, incorrectOtp);
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
                params.put("mobile", mobile);
                params.put("otp", otp);

                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(appContext, "Verifying OTP="+otp+" to your mobile!", Toast.LENGTH_SHORT).show();
                    }
                });

                //showProgressDialog("Verifying OTP to your mobile!");

                Log.i(TAG, "Posting params: " + params.toString());

                return params;
            }

        };

        // Adding request to request queue
        VolleyRequest.getInstance(appContext).addToRequestQueue(strReq);
    }

    private ProgressDialog mLoadingDialog;
    private Handler mHandler = new Handler();
    private void showLoadingDialog(final String title, final String msg) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if(mLoadingDialog == null) {
                    mLoadingDialog = ProgressDialog.show(appContext, title, msg);
                }
                mLoadingDialog.setTitle(title);
                mLoadingDialog.setMessage(msg);
            }
        });
    }

    private void hideLoadingDialog() {
        mHandler.post(new Runnable() { //Make sure it happens in sequence after showLoadingDialog
            @Override
            public void run() {
                if(mLoadingDialog != null) {
                    mLoadingDialog.dismiss();
                }
            }
        });
    }
}
