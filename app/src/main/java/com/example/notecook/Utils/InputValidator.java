package com.example.notecook.Utils;

import android.util.Patterns;
import android.widget.EditText;

public class InputValidator {

    public boolean isValidLogin(EditText usernameEditText, EditText passwordEditText) {
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (username.isEmpty()) {
            usernameEditText.setError("Username cannot be empty");
            return false;
        }

        if (password.isEmpty()) {
            passwordEditText.setError("Password cannot be empty");
            return false;
        }

        return true;
    }

    public boolean isValidRegistration(EditText firstNameEditText, EditText lastNameEditText,
                                       EditText phoneNumberEditText, EditText emailEditText,
                                       EditText passwordEditText, EditText passwordConfirmationEditText
    ) {
        String firstname = firstNameEditText.getText().toString().trim();
        String lastname = lastNameEditText.getText().toString().trim();
        String phone = phoneNumberEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String passwordConfirmation = passwordConfirmationEditText.getText().toString().trim();

        if (firstname.isEmpty()) {
            firstNameEditText.setError("Username cannot be empty");
            return false;
        }
        if (lastname.isEmpty()) {
            lastNameEditText.setError("Username cannot be empty");
            return false;
        }

        if (phone.isEmpty()) {
            phoneNumberEditText.setError("phone cannot be empty");
            return false;
        }
        if (!Patterns.PHONE.matcher(phone).matches()) {
            phoneNumberEditText.setError("Invalid phone number");
            return false;
        }

        if (email.isEmpty()) {
            emailEditText.setError("Email cannot be empty");
            return false;
        }


        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Invalid email address");
            return false;
        }

        if (password.isEmpty()) {
            passwordEditText.setError("Password cannot be empty");
            return false;
        }

        if (passwordConfirmation.isEmpty()) {
            passwordConfirmationEditText.setError("PasswordConfirmation cannot be empty");
            return false;
        }else if(!passwordConfirmation.equals(password)){
            passwordConfirmationEditText.setError("PasswordConfirmation will be Match with password");
            return false;
        }

        return true;
    }
}
