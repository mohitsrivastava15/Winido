package com.winido.app.helper;

/**
 * Created by mohit on 12/6/17.
 */

public class Config {
    // server URL configuration
    public static final String URL_VERIFY_INVITE_CODE = "http://ec2-52-66-191-47.ap-south-1.compute.amazonaws.com/winido-iotbutton/verifyInviteCodeForEmail.php";
    public static final String URL_REQUEST_OTP = "http://ec2-52-66-191-47.ap-south-1.compute.amazonaws.com/winido-iotbutton/requestSms.php";
    public static final String URL_VERIFY_OTP = "http://ec2-52-66-191-47.ap-south-1.compute.amazonaws.com/winido-iotbutton/verifySms.php";

    // SMS provider identification
    // It should match with your SMS gateway origin
    // You can use  MSGIND, TESTER and ALERTS as sender ID
    // If you want custom sender Id, approve MSG91 to get one
    public static final String SMS_ORIGIN = "WINIDO";

    // special character to prefix the otp. Make sure this character appears only once in the sms
    public static final String OTP_DELIMITER = ":";

    public static final String DOWNLOAD_IMAGE = "Image";

    public static final double CONST_CRITICAL_STOCK_LEVEL = 0.2;

    public static int i = 1;

    public static final int CHECKPRESENCEOFINVITECODEFOREMAIL_INVITE_CODE_FOUND_FOR_EMAIL =  0;
    public static final int CHECKPRESENCEOFINVITECODEFOREMAIL_USER_RECORD_FOUND_FOR_EMAIL = i++;
    public static final int CHECKPRESENCEOFINVITECODEFOREMAIL_EMAIL_IS_EMPTY = i++;
    public static final int CHECKPRESENCEOFINVITECODEFOREMAIL_NO_INVITE_CODE_FOR_EMAIL = i++;
    public static final int CHECKPRESENCEOFINVITECODEFOREMAIL_DB_INCONSISTENCY = i++;

    public static final int VERIFYINVITECODEFOREMAIL_SUCCESS =  0;
    public static final int VERIFYINVITECODEFOREMAIL_EMAIL_IS_EMPTY = i++;
    public static final int VERIFYINVITECODEFOREMAIL_INVITE_CODE_IS_EMPTY = i++;
    public static final int VERIFYINVITECODEFOREMAIL_INVALID_INVITE_CODE = i++;
    public static final int VERIFYINVITECODEFOREMAIL_INVITE_CODE_ALREADY_USED = i++;

    public static final int REQUESTSMS_SUCCESS =  0;
    public static final int REQUESTSMS_EMAIL_IS_EMPTY = i++;
    public static final int REQUESTSMS_MOBILE_IS_EMPTY = i++;

    public static final int VERIFYSMS_SUCCESS =  0;
    public static final int VERIFYSMS_EMAIL_IS_EMPTY = i++;
    public static final int VERIFYSMS_MOBILE_IS_EMPTY = i++;
    public static final int VERIFYSMS_OTP_IS_EMPTY = i++;
    public static final int VERIFYSMS_OTP_IS_INCORRECT = i++;

    public static final int CREATEUSER_SUCCESS =  0;
    public static final int CREATEUSER_EMAIL_IS_EMPTY = i++;
    public static final int CREATEUSER_EMAIL_ID_ALREADY_EXISTS = i++;
    public static final int CREATEUSER_MOBILE_IS_EMPTY = i++;
    public static final int CREATEUSER_NAME_IS_EMPTY = i++;
    public static final int CREATEUSER_ADDRESS_IS_EMPTY = i++;
    public static final int CREATEUSER_INVITE_ID_IS_MISSING = i++;
    public static final int CREATEUSER_INVALID_INVITE_ID = i++;
    public static final int CREATEUSER_DB_EXCEPTION = i++;
    public static final int CREATEUSER_UNVERIFIED_MOBILE = i++;

    public static final int GETSENSORFOREMAIL_SUCCESS =  0;
    public static final int GETSENSORFOREMAIL_EMAIL_IS_EMPTY = i++;
    public static final int GETSENSORFOREMAIL_INVALID_EMAIL = i++;
    public static final int GETSENSORFOREMAIL_NO_SENSOR_DETAILS_FOUND = i++;

    public static final int CREATEORDER_SUCCESS =  0;
    public static final int CREATEORDER_MAC_IS_EMPTY = i++;
    public static final int CREATEORDER_NO_SENSOR_FOUND_FOR_PROVIDED_MAC = i++;

}