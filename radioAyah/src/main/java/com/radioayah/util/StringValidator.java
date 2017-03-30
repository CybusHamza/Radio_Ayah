package com.radioayah.util;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;

public class StringValidator {

    public static boolean lengthValidator(Context context, String input,
                                          int minLength, int maxLength, String label) {
        String error = "";
        if (input.isEmpty()) {
            error = label + " cannot be empty field.";
        } else if (input.length() < minLength) {
            error = label + " cannot be less than " + minLength + " characters";
        } else if (input.length() > maxLength) {
            error = label + " cannot be more than " + maxLength + " characters";
        }

        if (error.equalsIgnoreCase("")) {
            return true;
        } else {
            new GenericDialogBox(context, error, "", "Error");
            return false;
        }
    }

    public static boolean isJSONValid(String test) {
        try {
            new JSONObject(test);
        } catch (JSONException ex) {
            try {
                new JSONArray(test);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }

    public static boolean ValidateEmail(Context context, String input) {
        String EMAIL_REGEX = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
        boolean b = input.matches(EMAIL_REGEX);
        if (b == false) {
            new GenericDialogBox(context, "Email is not in proper format.", "",
                    "Error");
        }
        return b;
    }

    public static String checkPasswordStrength(String password) {
        int strengthPercentage = 0;
        String[] partialRegexChecks = {".*[a-z]+.*", // lower
                ".*[A-Z]+.*", // upper
                ".*[\\d]+.*", // digits
                ".*[@#$%]+.*" // symbols
        };

        if (password.matches(partialRegexChecks[0])) {
            strengthPercentage += 25;
        }
        if (password.matches(partialRegexChecks[1])) {
            strengthPercentage += 25;
        }
        if (password.matches(partialRegexChecks[2])) {
            strengthPercentage += 25;
        }
        if (password.matches(partialRegexChecks[3])) {
            strengthPercentage += 25;
        }
        String result = "";
        if (strengthPercentage == 0) {
            result = "Very Poor Password";
        } else if (strengthPercentage == 25) {
            result = "Poor Password";
        } else if (strengthPercentage == 50) {
            result = "Normal Password";
        } else if (strengthPercentage == 75) {
            result = "Good Password";
        } else if (strengthPercentage == 100) {
            result = "Strong Password";
        }
        return result;
    }

    public static boolean match(Context c, String a, String b) {
        if (a.equals(b)) {
            return true;
        } else {
            new GenericDialogBox(c,
                    "Password and Confirm Password dont match.", "", "Alert!");
            return false;

        }
    }

    public static boolean ageCheck(Context c, String days, String month,
                                   String year) {
        final Calendar c1 = Calendar.getInstance();
        int year1 = c1.get(Calendar.YEAR);
        c1.get(Calendar.MONTH);
        c1.get(Calendar.DAY_OF_MONTH);

        if (year != null) {
            int yearInt = Integer.parseInt(year);
            if (year1 - yearInt < 18) {
                new GenericDialogBox(
                        c,
                        "Minimum age of user should be 18 years to register for Mwallet.",
                        "", "Alert!");
                return false;
            } else {
                return true;
            }
        }
        return false;

    }

    public static boolean specialCharacters(Context c, String check) {
        String specialChars = "+";
        for (int i = 0; i < check.length(); i++) {
            if (specialChars.contains(check.substring(i, 1))) {
                new GenericDialogBox(c,
                        "Input cannot contain special characters", "", "Alert");
                return false;
            }
        }
        return true;
    }

    public static boolean ValidateTime(Context context, String input) {
        DateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        try {
            Date dt = formatter.parse(input + ":00");
            Calendar cal = Calendar.getInstance();
            cal.setTime(dt);
        } catch (java.text.ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean ValidateTimeTwelveHours(Context context, String input) {
        StringTokenizer st = new StringTokenizer(input, ":");
        int tokens = st.countTokens();
        try {
            if (tokens == 3) {
                if (st.hasMoreTokens()) {
                    int hours = Integer.parseInt(st.nextToken());
                    if (hours > 0 && hours < 13) {
                        if (st.hasMoreTokens()) {
                            int minutes = Integer.parseInt(st.nextToken());
                            if (minutes > -1 && minutes < 60) {
                                if (st.hasMoreElements()) {
                                    int seconds = Integer.parseInt(st.nextToken());
                                    if (seconds > -1 && seconds < 60) {
                                        return true;
                                    } else {
                                        new GenericDialogBox(
                                                context,
                                                "Please Enter Valid Time in 12 hour format.",
                                                "", "Alert");
                                        return false;
                                    }
                                }
                            } else {
                                new GenericDialogBox(
                                        context,
                                        "Please Enter Valid Time in 12 hour format.",
                                        "", "Alert");
                                return false;
                            }
                        } else {
                            new GenericDialogBox(context,
                                    "Please Enter Valid Time in 12 hour format.",
                                    "", "Alert");
                            return false;
                        }
                    } else {
                        new GenericDialogBox(context,
                                "Please Enter Valid Time in 12 hour format.", "",
                                "Alert");
                        return false;
                    }
                }
            } else {
                new GenericDialogBox(context,
                        "Please Enter Valid Time in 12 hour format.", "", "Alert");
                return false;
            }
        } catch (NumberFormatException e) {
            new GenericDialogBox(
                    context,
                    "Please Enter Valid Time in 12 hour format.",
                    "", "Alert");
            return false;
        }
        return false;
    }

    public static String convertTwentyFourToTwelveHours(String time) {
        StringTokenizer st = new StringTokenizer(time, ":");
        StringBuilder sb = new StringBuilder();
        if (st.hasMoreElements()) {
            try {
                String token = st.nextToken();
                String token1 = st.nextToken();
                String token2 = st.nextToken();
                int hours = Integer.parseInt(token);
                if (hours > 12) {
                    if (hours - 12 < 10) {
                        if (hours == 0)
                            sb.append("12");
                        else
                            sb.append("0").append(hours - 12);
                    }
                    else
                        sb.append(hours - 12);
                } else if (hours < 10) {
                    if (hours == 0)
                        sb.append("12");
                    else
                    sb.append("0").append(hours);
                }
                else
                    sb.append(hours);
                sb.append(":");
                int minute = Integer.parseInt(token1);
                if (minute < 10)
                    sb.append("0").append(minute);
                else
                    sb.append(minute);
                sb.append(":");
                int seconds = Integer.parseInt(token2);
                if (seconds < 10)
                    sb.append("0").append(seconds);
                else
                    sb.append(seconds);
            }
            catch (NumberFormatException e)
            {
                return time;
            }
        }
        return sb.toString();
    }
    public static boolean validateDateYMD(String date,Context context,String label) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            format.parse(date);
            return true;
        } catch (ParseException e) {
            new GenericDialogBox(context, label + " is not in proper format. (YYYY-MM-DD)","","Alert!");
            return false;
        }
    }
    public static boolean calculateDateDiff(Context context, String date1,String date2)
    {
        try {
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date startDate = format.parse(date1);
            Date endDate = format.parse(date2);
            long duration = endDate.getTime() - startDate.getTime();
            if (duration < 0) {
                new GenericDialogBox(context, "Date of birth cannot be greater than date of death", "", "Alert!");
                return false;
            }
            return true;
        }
        catch (ParseException e)
        {
            return false;
        }
    }
}

