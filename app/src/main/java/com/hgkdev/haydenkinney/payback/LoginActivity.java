package com.hgkdev.haydenkinney.payback;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;
import com.parse.SignUpCallback;

import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class LoginActivity extends Activity {

    private TextView forgotPassword;
    private Button loginButton;
    private Button registerButton;
    private EditText emailEditText;
    private EditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        ParseUser currentUser = ParseUser.getCurrentUser();
        if( currentUser != null ) {
            Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(mainIntent);
            finish();
        }

        forgotPassword = (TextView) this.findViewById(R.id.forgotPasswordTextView);
        registerButton = (Button) this.findViewById(R.id.registerButton);
        loginButton = (Button) this.findViewById(R.id.loginButton);
        emailEditText = (EditText) this.findViewById(R.id.emailEditText);
        passwordEditText = (EditText) this.findViewById(R.id.passwordEditText);

    }

    public void registerUser(View v) {
        if( checkTextValues() ) {
            ParseUser user = new ParseUser();

            user.setUsername(emailEditText.getText().toString());
            user.setPassword(passwordEditText.getText().toString());
            user.setEmail(emailEditText.getText().toString());

            user.signUpInBackground(new SignUpCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(mainIntent);
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "Email already used. Use forgot password if necessary", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

    }

    public void loginUser(View v) {
        if( checkTextValues() ) {
            ParseUser.logInInBackground(emailEditText.getText().toString(), passwordEditText.getText().toString(), new LogInCallback() {
                public void done(ParseUser user, ParseException e) {
                    if (user != null) {
                        Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(mainIntent);
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "Login Failed. Please try again, or use forgot password if necessary", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    public void forgotPassword(View v) {
        if( checkEmailValue() ) {
            new Change_Password(LoginActivity.this, emailEditText.getText().toString() ).execute();
        }
    }


    private boolean checkEmailValue() {
        String emailAddress = emailEditText.getText().toString();

        boolean emailEmpty = emailAddress.equals("");
        if (!emailEmpty) {
            Pattern emailPattern = Pattern.compile(".+@.+\\.[a-z]+");
            Matcher emailMatcher = emailPattern.matcher(emailAddress);
            boolean emailCorrect = emailMatcher.matches();

            if (emailCorrect) {
                return true;
            }
        }
        Toast.makeText(LoginActivity.this, "Please enter your email in the email field, and try again.", Toast.LENGTH_LONG).show();
        return false;
    }


    private boolean checkTextValues() {
        String emailAddress = emailEditText.getText().toString();

        boolean emailEmpty = emailAddress.equals("");
        boolean passwordEmpty = passwordEditText.getText().toString().equals("");

        if( !emailEmpty ) {
            if( !passwordEmpty ) {
                Pattern emailPattern = Pattern.compile(".+@.+\\.[a-z]+");
                Matcher emailMatcher = emailPattern.matcher(emailAddress);
                boolean emailCorrect = emailMatcher.matches();

                if( emailCorrect ) { return true; }
                else {
                    Toast.makeText(LoginActivity.this, "Please enter a valid email address", Toast.LENGTH_LONG ).show();
                    return false;
                }
            } else {
                Toast.makeText(LoginActivity.this, "Please fill all fields", Toast.LENGTH_LONG ).show();
                return false;
            }
        } else {
            Toast.makeText(LoginActivity.this, "Please fill all fields", Toast.LENGTH_LONG ).show();
            return false;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

class Change_Password extends AsyncTask<Void, Void, Void> {

    Activity mActivity;
    String   mEmailAccount;
    ProgressDialog progress;

    public Change_Password( Activity activity, String emailAccount ) {
        mActivity = activity;
        mEmailAccount = emailAccount;
        progress = new ProgressDialog( mActivity );
    }

    protected void onPreExecute() {
        super.onPreExecute();
        progress.setCancelable(true);
        progress.setMessage("Sending Reset Email");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.show();
    }

    @Override
    protected Void doInBackground(Void... params) {
        ParseUser.requestPasswordResetInBackground(mEmailAccount,
                new RequestPasswordResetCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Toast.makeText(mActivity, "Password reset email successfully sent", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(mActivity, "Email address does not exist, please try again or register.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
        }

        return null;
    }
    protected void onPostExecute(Void results) {
        super.onPostExecute(results);
        progress.dismiss();
    }
}