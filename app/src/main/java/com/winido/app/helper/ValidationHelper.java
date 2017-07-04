package com.winido.app.helper;

/**
 * Created by mohit on 12/6/17.
 */

public class ValidationHelper {
    /**
     * Regex to validate the mobile number
     * mobile number should be of 10 digits length
     *
     * @param mobile
     * @return
     */
    public static boolean isValidPhoneNumber(String mobile) {
        String regEx = "^[0-9]{10}$";
        return mobile.matches(regEx);
    }

    /**
     * Regex to validate the mobile number
     * mobile number should be of 10 digits length
     *
     * @param otp
     * @return
     */
    public static boolean isValidOtp(String otp) {
        String regEx = "^[0-9]{6}$";
        return otp.matches(regEx);
    }

    /**
     * Regex to validate the mobile number
     * mobile number should be of 10 digits length
     *
     * @param otp
     * @return
     */
    public static boolean isValidInviteCode(String otp) {
        String regEx = "^[a-zA-Z]{4}[0-9]{4}$";
        return otp.matches(regEx);
    }

    /**
     * Regex to validate the Address (which for now will only be for Shriram Spandhana)
     * Address should be of the type NN104
     *
     * @param address
     * @return
     */
    public static boolean isValidAddress(String address) {
        String regEx = "^[a-zA-Z]{1,2}\\s*[0-9]{3}$";
        return address.matches(regEx);
    }
}
