/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse.starter;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.util.List;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnKeyListener{

    boolean signUpActive = true;
    EditText passwordEditText;

    public void showUserList() {
        Intent intent = new Intent(getApplicationContext(), UserListActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (keyCode == event.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN ) {// Avoid click twice
            register(v);
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.login) {
            Log.i("AppInfo", "mode change to Login");
            if (signUpActive) {// set sign up key to the login when click login TextView
                Button button = (Button) findViewById(R.id.signup);
                button.setText("Login");
                TextView changeSignModeTextView = (TextView) findViewById(R.id.login);
                changeSignModeTextView.setText("sign up");
                signUpActive = false;
            } else { // set the prev login button back to sign up
                Button button = (Button) findViewById(R.id.signup);
                button.setText("Sign Up");
                TextView changeSignModeTextView = (TextView) findViewById(R.id.login);
                changeSignModeTextView.setText("Login");
                signUpActive = true;
            }

        } else if (view.getId() == R.id.logo || view.getId() == R.id.background) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    public void register(View view) {
        // got text from view
        EditText usernameEditText = (EditText) findViewById(R.id.username);
        EditText passwordEditText = (EditText) findViewById(R.id.password);

        if (usernameEditText == null || usernameEditText.getText().toString().equals("")) {
            Toast.makeText(this, "Please input Username",Toast.LENGTH_SHORT).show();
        } else if (passwordEditText == null || passwordEditText.getText().toString().equals("")) {
            Toast.makeText(this, "Please input Password",Toast.LENGTH_SHORT).show();
        }  else {
            ParseUser parseUser = new ParseUser();
            parseUser.setUsername(usernameEditText.getText().toString());
            parseUser.setPassword(passwordEditText.getText().toString());

            if (signUpActive) {
                parseUser.signUpInBackground(new SignUpCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            showUserList();
                            Log.i("Sign up", "successful");
                        } else {
                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else {
                parseUser.logInInBackground(usernameEditText.getText().toString(), passwordEditText.getText().toString(), new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException e) {
                        if (user != null) {
                            showUserList();
                            Log.i("Login", "Successful");
                        } else {
                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Share you images");
        // add lisener
        TextView changeSignModeTextView = (TextView) findViewById(R.id.login);
        RelativeLayout background = (RelativeLayout) findViewById(R.id.background);
        ImageView image = (ImageView) findViewById(R.id.logo);

        changeSignModeTextView.setOnClickListener(this);
        background.setOnClickListener(this);
        image.setOnClickListener(this);

        // pree Enter to login/signup
        this.passwordEditText= (EditText) findViewById(R.id.password);
        this.passwordEditText.setOnKeyListener(this);

        if (ParseUser.getCurrentUser() != null) {
            showUserList();
        }
        ParseAnalytics.trackAppOpenedInBackground(getIntent());
    }


}