package rh.tripper;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class LandingActivity extends AppCompatActivity {

    Button loginButton = null;
    Button regButton = null;
    EditText emailBox = null;
    EditText passwordBox = null;
    RelativeLayout progress = null;
    String email = null;
    String password = null;
    Boolean result = false;
    Context context = null;
    int count = 0;
    View viewVar = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_landing);

        loginButton = findViewById(R.id.loginButton);
        regButton = findViewById(R.id.registerButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emailBox = findViewById(R.id.emailBox);
                passwordBox = findViewById(R.id.passwordBox);

                email = emailBox.getText().toString();
                password = passwordBox.getText().toString();

                if(email.length() == 0){
                    emailBox.setError("Email is required.");
                    count++;
                }

                if(password.length() == 0){
                    passwordBox.setError("Password is required.");
                    count++;
                }

                if(count == 0)
                {
                    viewVar = view;
                    LogUserIn logIn = new LogUserIn();
                    logIn.execute(password);
                }

            }
        });

        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, RegisterActivity.class);
                startActivityForResult(i, 1);
            }
        });

    }

    private class LogUserIn extends AsyncTask<String, Void, Boolean> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress = findViewById(R.id.progressBarLogin);
            progress.setVisibility(View.VISIBLE);
        }

        @Override
        protected Boolean doInBackground(String... passwordIn) {

            HttpURLConnection urlConnection = null;
            InputStream in = null;

            try{
                URL url = new URL("http://10.0.2.2:3000/login?email="+ email + "&password=" + password);
                urlConnection = (HttpURLConnection) url.openConnection();
                in = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                result = Boolean.valueOf(reader.readLine());
                urlConnection.disconnect();
                in.close();
            }
            catch (MalformedURLException e) {
                e.printStackTrace();
            }
            catch (IOException e){
                e.printStackTrace();
            }
            finally
            {
                return result;
            }
        }

        @Override
        protected void onPostExecute(Boolean resultB) {
            progress.setVisibility(View.INVISIBLE);

            if (result == true)
            {
                Intent i = new Intent(viewVar.getContext(), MainActivity.class);
                i.putExtra("email", email);
                startActivityForResult(i, 1);
            } else {
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
                alertBuilder.setMessage("The email or password you entered was incorrect.");
                alertBuilder.setCancelable(true);

                alertBuilder.setPositiveButton(
                        "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog logInAlert = alertBuilder.create();
                logInAlert.show();
            }
        }
    }
}
