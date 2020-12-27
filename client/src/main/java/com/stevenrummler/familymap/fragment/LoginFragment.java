package com.stevenrummler.familymap.fragment;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.stevenrummler.familymap.R;
import com.stevenrummler.familymap.activity.MainActivity;
import com.stevenrummler.familymap.data.ServerProxy;

import request.LoginRequest;
import request.RegisterRequest;

public class LoginFragment extends Fragment {
    private EditText host;
    private EditText port;
    private EditText username;
    private EditText password;
    private EditText first;
    private EditText last;
    private EditText email;
    private RadioGroup radio;
    private RadioButton male;
    private RadioButton female;
    private Button login;
    private Button register;

    // UI Setup and Overrides

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        host = view.findViewById(R.id.hostField);
        port = view.findViewById(R.id.portField);
        username = view.findViewById(R.id.userNameField);
        password = view.findViewById(R.id.passwordField);
        first = view.findViewById(R.id.firstNameField);
        last = view.findViewById(R.id.lastNameField);
        email = view.findViewById(R.id.emailField);
        radio = view.findViewById(R.id.radio);
        male = view.findViewById(R.id.radio_male);
        female = view.findViewById(R.id.radio_female);
        login = view.findViewById(R.id.login);
        register = view.findViewById(R.id.register);

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateButtons();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        RadioGroup.OnCheckedChangeListener onCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                updateButtons();
            }
        };

        Button.OnClickListener loginListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        };

        Button.OnClickListener registerListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        };

        host.addTextChangedListener(textWatcher);
        port.addTextChangedListener(textWatcher);
        username.addTextChangedListener(textWatcher);
        password.addTextChangedListener(textWatcher);
        first.addTextChangedListener(textWatcher);
        last.addTextChangedListener(textWatcher);
        email.addTextChangedListener(textWatcher);
        radio.setOnCheckedChangeListener(onCheckedChangeListener);
        login.setOnClickListener(loginListener);
        register.setOnClickListener(registerListener);

        updateButtons();

        return view;
    } // OnCreateView

    // Enables and disables Login and Register buttons as the user types
    public void updateButtons() {
        boolean loginReady = !host.getText().toString().equals("") &&
                !port.getText().toString().equals("") &&
                !username.getText().toString().equals("") &&
                !password.getText().toString().equals("");
        login.setEnabled(loginReady);
        boolean registerReady = loginReady &&
                !first.getText().toString().equals("") &&
                !last.getText().toString().equals("") &&
                !email.getText().toString().equals("") &&
                (male.isChecked() || female.isChecked());
        register.setEnabled(registerReady);
    }

    // Login and Register Functionality

    LoginRequest loginRequest;
    RegisterRequest registerRequest;
    String hostString;
    String portString;
    boolean registering;

    public void login() {
        hostString = host.getText().toString();
        portString = port.getText().toString();

        loginRequest = new LoginRequest();
        loginRequest.userName = username.getText().toString();
        loginRequest.password = password.getText().toString();

        registering = false;

        (new ServerTask()).execute();
    }

    public void register() {
        hostString = host.getText().toString();
        portString = port.getText().toString();

        int genderId = radio.getCheckedRadioButtonId();
        boolean male = (genderId == this.male.getId());

        registerRequest = new RegisterRequest();
        registerRequest.userName = username.getText().toString();
        registerRequest.password = password.getText().toString();
        registerRequest.firstName = first.getText().toString();
        registerRequest.lastName = last.getText().toString();
        registerRequest.email = email.getText().toString();
        registerRequest.gender = male ? "m" : "f";

        registering = true;

        (new ServerTask()).execute();
    }

    private class ServerTask extends AsyncTask<String, Integer, String> {
        ServerProxy serverProxy;

        @Override
        protected String doInBackground(String... strings) {
            serverProxy = new ServerProxy(hostString, portString);
            if (registering) {
                serverProxy.register(registerRequest);
            } else {
                serverProxy.login(loginRequest);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            MainActivity activity = (MainActivity) getActivity();
            if (serverProxy.isSuccess()) {
                activity.openMap();
            } else {
                Toast toast = Toast.makeText(activity, serverProxy.getMessage(), Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP, 0, 10);
                toast.show();
            }
        }
    }
}