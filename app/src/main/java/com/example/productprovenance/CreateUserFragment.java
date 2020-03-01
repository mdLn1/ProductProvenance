package com.example.productprovenance;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class CreateUserFragment extends Fragment {
    private TextInputEditText inputUsernameText;
    private TextInputLayout inputUsernameLayout;
    private TextInputEditText inputLocationText;
    private TextInputLayout inputLocationLayout;
    private TextInputEditText inputRoleText;
    private TextInputLayout inputRoleLayout;
    private TextInputEditText inputAccountAddressText;
    private TextInputLayout inputAccountAddressLayout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.create_user_fragment, container, false);
        return view;
    }
}
