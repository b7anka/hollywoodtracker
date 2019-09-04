package com.b7anka.hollywoodtracker.Helpers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidateUserInputs
{

    public boolean checkForEmptyInputs(String[] userInputs)
    {
        for(String s : userInputs)
        {
            if(s.isEmpty())
            {
                return true;
            }
        }

        return false;
    }

    public boolean verifyPassword(String pass)
    {

        boolean meetsLengthRequirements = pass.length() >= Constants.MIN_PASSWORD_LENGTH;
        boolean hasUpperCaseLetters = false;
        boolean hasLowerCaseLetters = false;
        boolean hasSymbols = false;
        boolean hasDecimalDigit = false;

        if(pass.contains(Constants.SEMI_COLON))
        {
            return false;
        }

        if(meetsLengthRequirements)
        {
            char[] p = pass.toCharArray();
            for(char c: p)
            {
                if(Character.isUpperCase(c)) hasUpperCaseLetters = true;
                else if(Character.isLowerCase(c)) hasLowerCaseLetters = true;
                else if(!Character.isLetterOrDigit(c)) hasSymbols = true;
                else if(Character.isDigit(c)) hasDecimalDigit = true;
            }
        }

        boolean isValid = meetsLengthRequirements && hasUpperCaseLetters && hasLowerCaseLetters && hasSymbols && hasDecimalDigit;

        return isValid;
    }


    public boolean verifyFullNameSize(String fullname)
    {
        String temp[] = fullname.split(" ");
        if(temp.length<Constants.FULL_NAME_SIZE)
        {
            return false;
        }
        return true;
    }

    public boolean verifyFullNameRegex(String fullname)
    {
        String pattern = "^[\\p{L} .'-]+$";
        if(fullname.matches(pattern))
        {
            return true;
        }
        return false;
    }

    public boolean verifyFullNameLength(String fullname)
    {
        if(fullname.length()>Constants.FULL_NAME_LENGTH)
        {
            return true;
        }
        return false;
    }

    public boolean verifyUsernameLength(String username)
    {
        if(username.length()>Constants.USERNAME_LENGTH)
        {
            return true;
        }
        return false;
    }

    public boolean verifyUsernameContent(String username)
    {
        String pattern = "^[a-zA-Z0-9.]+$";
        if(username.contains(Constants.SEMI_COLON))
        {
            return false;
        }
        else if(username.matches(pattern))
        {
            return true;
        }
        return false;
    }


    public boolean verifyValidEmailAddress(String email)
    {
        final Pattern VALID_EMAIL_ADDRESS_REGEX =
                Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,63}$", Pattern.CASE_INSENSITIVE);

        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX .matcher(email);
        return matcher.find();
    }

    public boolean verifyEmailAddressLength(String email)
    {
        if(email.length()>Constants.EMAIL_LENGTH)
        {
            return true;
        }
        return false;
    }

    public boolean verifyTitleLength(String title)
    {
        return title.length()>Constants.TITLE_LENGTH;
    }

    public boolean verifyWatchedTimeLength(String watchedTime)
    {
        return watchedTime.length()>Constants.WATCHED_TIME_LENGTH;
    }

    public boolean verifySeasonAndEpisodeLength(String season)
    {
        return season.length()>Constants.SEASON_AND_EPISODE_LENGTH;
    }

    public boolean verifyValidWatchedTime(String watchedTime)
    {
        final Pattern VALID_WATCHED_TIME_REGEX =
                Pattern.compile("^0{1}[0-3]+:{1}[0-5]{1}[0-9]+:{1}[0-5]{1}[0-9]$", Pattern.CASE_INSENSITIVE);

        Matcher matcher = VALID_WATCHED_TIME_REGEX .matcher(watchedTime);
        return matcher.find();
    }

    public boolean verifyValidSeason(String season)
    {
        final Pattern VALID_SEASON_REGEX =
                Pattern.compile("^{1}[0-5]{1}[0-9]$", Pattern.CASE_INSENSITIVE);

        Matcher matcher = VALID_SEASON_REGEX .matcher(season);
        return matcher.find();
    }

    public boolean verifyValidEpisode(String episode)
    {
        final Pattern VALID_EPISODE_REGEX =
                Pattern.compile("^{1}[0-7]{1}[0-9]$", Pattern.CASE_INSENSITIVE);

        Matcher matcher = VALID_EPISODE_REGEX .matcher(episode);
        return matcher.find();
    }


}
