package com.example.productprovenance.functional_fragments;

import android.os.Bundle;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.productprovenance.MainActivity;
import com.example.productprovenance.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

public class LoginFragment extends Fragment {

    private MainActivity parentActivity;
    private TextInputEditText passwordEditText;
    private TextInputEditText usernameEditText;
    private TextInputEditText nodeAddressEditText;
    private TextView errorMessageLogin;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.login_fragment, container, false);
        final TextInputLayout passwordTextInput = view.findViewById(R.id.password_text_input);
        final TextInputLayout usernameTextInput = view.findViewById(R.id.username_text_input);
        final TextInputLayout nodeAddressTextInput = view.findViewById(R.id.node_address_text_input);
        passwordEditText = view.findViewById(R.id.passwordEditText);
        usernameEditText = view.findViewById(R.id.usernameEditText);
        nodeAddressEditText = view.findViewById(R.id.nodeAddressEditText);
        nodeAddressEditText.setText("http://51.132.38.7:8001");

        errorMessageLogin = view.findViewById(R.id.loginErrorText);
        Button loginButton = view.findViewById(R.id.buttonLogin);
        Button skipButton = view.findViewById(R.id.skipButton);
        parentActivity = (MainActivity) getActivity();
        skipButton.setOnClickListener(new View.OnClickListener() {
                                          @Override
                                          public void onClick(View v) {
                                              parentActivity.onClick(v);
                                          }
                                      }

        );
        loginButton.setOnClickListener(new View.OnClickListener() {
                                           @Override
                                           public void onClick(View v) {
                                               // verifying input fields
                                               String address = Objects.requireNonNull(nodeAddressEditText.getText()).toString();
                                               if (!isPasswordValid(passwordEditText.getText()) || usernameEditText.getText().length() < 2
                                               ) {
                                                   if (!isPasswordValid(passwordEditText.getText()))
                                                       passwordTextInput.setError(getString(R.string.error_password));
                                                   if (usernameEditText.getText().length() < 2)
                                                       usernameTextInput.setError(getString(R.string.error_username));

                                               } else if (!address.startsWith("http://")) {
                                                       nodeAddressTextInput.setError(getString(R.string.error_node_address));

                                               } else {
                                                   passwordTextInput.setError(null);
                                                   usernameTextInput.setError(null);
                                                   nodeAddressTextInput.setError(null);
//                    ((NavigationHost) getActivity()).navigatePrevious();
                                                   parentActivity.onClick(v);

                                               }
                                           }
                                       }

        );

        // Clear the error once more than 8 characters are typed.
        passwordEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (isPasswordValid(passwordEditText.getText())) {
                    passwordTextInput.setError(null); //Clear the error
                }
                return false;
            }
        });
        return view;
    }

    public String getPasswordValue() {
        return Objects.requireNonNull(passwordEditText.getText()).toString();
    }

    public void showInvalidLogin(String msg) {
        errorMessageLogin.setVisibility(View.VISIBLE);
        errorMessageLogin.setText(msg);
    }

    public String getUsernameValue() {
        return Objects.requireNonNull(usernameEditText.getText()).toString();
    }

    public String getNodeAddressValue() {
        return Objects.requireNonNull(nodeAddressEditText.getText()).toString();
    }

    /*
        In reality, this will have more complex logic including, but not limited to, actual
        authentication of the username and password.
     */
    private boolean isPasswordValid(@Nullable Editable text) {
        return text != null && text.length() >= 7;
    }
}
