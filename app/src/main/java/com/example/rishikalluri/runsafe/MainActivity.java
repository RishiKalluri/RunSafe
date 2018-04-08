package com.example.rishikalluri.runsafe;

import android.Manifest;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.IntegerRes;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.text.InputType;
import android.util.Base64;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.location.LocationListener;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public ArrayList<String> runWatchers = new ArrayList<>();
    public int pin=0000;
    public int pinCount=0;
    //public String phoneNo = new String("18156700836");
    public String nonEmergency = new String("RUNWATCHER! I AM IN A NON EMERGENCY SITUATION! ");
    public String dEmergency = new String("RUNWATCHER! I AM IN A DANGEROUS SITUATION AND AM IN NEED OF HELP!  ");
    public String mEmergency = new String("RUNWATCHER! I AM IN A MEDICAL EMERGENCY AND AM IN NEED OF HELP! ");
    public String pEmergency = new String("RUNWATCHER! I AM IN AN EMERGENCY, AND AM IN NEED OF LAW ENFORCEMENT AND MEDICAL SERVICES! ");
    public String client_id = new String("gk1nFtbQr4pBpJD0rzAp3vaSi555sm4s");
    public String client_secret = new String("eWTSj_izMvD3nBJFXxkRDZF4aXDGKofYRZyzw_31oer31kuoY6-OVDs27nEHJu0B");
    public String redirectUri = new String("runsafe://callback");
    public String userName;
    public String accessToken;
    private static final int PERMISSION_SEND_SMS = 1;
    public String longitude, latitude;
    public double longitudeD, latitudeD;
    public Alarm alarm;
    public String alarmId;
    public AccessToken token;
    public boolean alarmSet = false;
    public boolean runwatcherSet = false;
    Button runWatcher, danger, medical, panic;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);




        runWatcher = (Button) findViewById(R.id.button);
        danger = (Button) findViewById(R.id.button2);
        medical = (Button) findViewById(R.id.button3);
        panic = (Button) findViewById(R.id.button4);

        runWatcher.setOnClickListener(this);
        danger.setOnClickListener(this);
        medical.setOnClickListener(this);
        panic.setOnClickListener(this);

    }


    @Override
    protected void onResume() {
        super.onResume();

       if (token==null) {
            Uri uri = getIntent().getData();
            System.out.println("yay!");
            if (uri != null && uri.toString().startsWith(redirectUri)) {
                final String code = uri.getQueryParameter("code");
                System.out.println("code:" + code);
                Toast.makeText(this, code, Toast.LENGTH_SHORT).show();

                if (code != null) {
                    //get access token
                    Retrofit.Builder builder = new Retrofit.Builder()
                            .baseUrl("https://api-sandbox.safetrek.io/v1/")
                            .addConverterFactory(GsonConverterFactory.create());

                    Retrofit retrofit = builder.build();

                    //String basicAuth = "Basic" + "base64(" + client_id + ":" + client_secret);
                    Client client = retrofit.create(Client.class);

                    String clientIdSecret = String.format("%s:%s", client_id, client_secret);
                    String authString = Base64.encodeToString(clientIdSecret.getBytes(), Base64.NO_WRAP);
                    System.out.println(authString);

                    Map<String, String> headers = new HashMap<>();

                    headers.put("Content-Type", "application/json");
                    headers.put("Authorization", "Basic " + authString);

                    Map<String, String> body = new HashMap<String, String>();

                    body.put("grant_type", "authorization_code");
                    body.put("code", code);
                    body.put("redirect_uri", redirectUri);


                    final Call<AccessToken> accessTokenCall = client.getAccessToken(
                            headers,
                            body
                    );
                    Thread t = new Thread(new Runnable() {
                        @Override
                        public void run() {

                            try {
                                token = accessTokenCall.execute().body();
                                accessToken = token.getAccessToken();
                                System.out.println("AToken: " + accessToken);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            System.out.println("Access Token" + token.getAccessToken());
                        }
                    });
                    t.start();

                } else if (uri.getQueryParameter("error") != null) {
                    Toast.makeText(this, "ERROR", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }





    @Override
    public void onClick(View v) {
        //Toast.makeText(this, "Button", Toast.LENGTH_SHORT).show();


        if (v.getId() == R.id.button) { // RIDEWATCHER BUTTON

           if (runwatcherSet == false) {
               Toast.makeText(this, "YOU HAVE NO RUNWATCHER SET", Toast.LENGTH_SHORT).show();
           }
           else {
               alertRunWatchers(nonEmergency);
               Toast.makeText(this, "SMS SENT", Toast.LENGTH_SHORT).show();

           }
        }


        if (v.getId() == R.id.button2) {   // DANGER BUTTON CODE

            if (runwatcherSet == false && accessToken != null) {
                Toast.makeText(this, "YOU HAVE NO RUNWATCHER SET: ALARM SENT REGARDLESS", Toast.LENGTH_SHORT).show();
                createAlarm(true, false, false);
            }
            else if (runwatcherSet == true && accessToken != null) {
                alertRunWatchers(dEmergency);
                createAlarm(true, false, false);
                Toast.makeText(this, "ALARM SENT", Toast.LENGTH_SHORT).show();
                //checkPin();
            }
            else {
                Toast.makeText(this, "YOU MUST LOGIN TO SEND ALARMS", Toast.LENGTH_SHORT).show();
            }
        }

        if (v.getId() == R.id.button3) { // MEDICAL BUTTON

            if (runwatcherSet == false && accessToken != null) {
                Toast.makeText(this, "YOU HAVE NO RUNWATCHER SET: ALARM SENT REGARDLESS", Toast.LENGTH_SHORT).show();
                createAlarm(true, false, false);

            }
            else if (runwatcherSet == true && accessToken != null) {
                alertRunWatchers(mEmergency);
                createAlarm(false, false, true);
                //checkPin();
                Toast.makeText(this, "ALARM SENT", Toast.LENGTH_SHORT).show();

            }
            else {
                Toast.makeText(this, "YOU MUST LOGIN TO SEND ALARMS", Toast.LENGTH_SHORT).show();
            }
        }

        if (v.getId() == R.id.button4) {  // PANIC BUTTON

            if (runwatcherSet == false && accessToken != null) {
                Toast.makeText(this, "YOU HAVE NO RUNWATCHER SET: ALARM SENT REGARDLESS", Toast.LENGTH_SHORT).show();
                createAlarm(false, false, true);

            }
            else if (runwatcherSet == true && accessToken != null) {
                alertRunWatchers(pEmergency);
                createAlarm(true, false, true);
                Toast.makeText(this, "ALARM SENT", Toast.LENGTH_SHORT).show();
                //checkPin();
            }
            else {
                Toast.makeText(this, "YOU MUST LOGIN TO SEND ALARMS", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.setRunWatcher) {
            Toast.makeText(this, "test run watcher", Toast.LENGTH_SHORT).show();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Add RunWatcher (TYPE PHONE NUMBER BELOW)");

            final EditText input = new EditText(this);

            input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_TEXT_VARIATION_NORMAL);
            builder.setView(input);

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int i) {
                    String m_Text = input.getText().toString();
                    runWatchers.add(m_Text);
                    runwatcherSet = true;
                }

            });

            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int i) {
                    dialog.cancel();
                }
            });

            builder.show();

            return true;

        } else if (id == R.id.setPin) {
            Toast.makeText(this, "test set pin", Toast.LENGTH_SHORT).show();

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Set Pin Number (4 DIGIT)");

            final EditText input = new EditText(this);


            input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            builder.setView(input);

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int i) {

                    String m_Text = input.getText().toString();

                    if (m_Text.length() == 4) {
                        pin = Integer.parseInt((m_Text));
                    } else {
                        Toast.makeText(MainActivity.this, "INVALID PIN", Toast.LENGTH_SHORT).show();
                    }
                }

            });

            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int i) {
                    dialog.cancel();
                }
            });

            builder.show();

            return true;

        } else if (id == R.id.setName) {
            Toast.makeText(this, "test set name", Toast.LENGTH_SHORT).show();

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Enter Name: ");

            final EditText input = new EditText(this);


            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
            builder.setView(input);

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int i) {

                    String m_Text = input.getText().toString();
                    userName = m_Text;
                    userName = userName.toUpperCase();
                    nonEmergency = "RUWATCHER! " + userName + " IS IN A NON EMERGENCY SITUATION";
                    dEmergency = "RUNWATCHER! " + userName + " IS IN A DANGEROUS SITUATION AND AM IN NEED OF HELP!  ";
                    mEmergency = "RUNWATCHER! "  + userName + " IS IN A MEDICAL EMERGENCY AND AM IN NEED OF HELP! ";
                    pEmergency = "RUNWATCHER! " + userName + " IS IN AN EMERGENCY, AND AM IN NEED OF LAW ENFORCEMENT AND MEDICAL SERVICES! ";

                }

            });

            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int i) {
                    dialog.cancel();
                }
            });

            builder.show();

            return true;
        } else if (id == R.id.clearRunWatchers) {

            runWatchers.clear();
            runwatcherSet = false;
            return true;
        }

        else if (id == R.id.login){
            String url = "https://account-sandbox.safetrek.io/authorize" + "?audience=https://api-sandbox.safetrek.io" + "&client_id=" + client_id + "&scope=openid%20phone%20offline_access&response_type=code&redirect_uri=" + redirectUri;
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            System.out.println(url);
            startActivity(intent);
        }

        else if (id == R.id.cancelAlarm) {

            if(alarmSet == true){
                checkPin();
            }
            else {

                Toast.makeText(this, "YOU HAVE NOT SENT AN ALARM!", Toast.LENGTH_SHORT).show();

            }

        }


        return super.onOptionsItemSelected(item);
    }


    public void alertRunWatchers(String alert){

            for (int x = 0; x < runWatchers.size(); x++) {


                try {


                    runWatchers = runWatchers;
                    nonEmergency = nonEmergency;
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, PERMISSION_SEND_SMS);
                    } else {
                        getLocation();
                        sendText(runWatchers.get(x), alert);
                       // Toast.makeText(getApplicationContext(), "SMS Sent!",
                              //  Toast.LENGTH_LONG).show();
                    }


                } catch (Exception e) {
                    if (runWatchers.size() == 0) {
                        Toast.makeText(getApplicationContext(),
                                "YOU HAVE NO RUNWATCHER SET",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, "Your RunWatcher has an invalid phone number. Please clear your list and re-enter their phone number!", Toast.LENGTH_SHORT).show();
                    }
                    e.printStackTrace();
                }
                //Toast.makeText(this, "ff", Toast.LENGTH_SHORT).show();
            }




    }


    public void sendText(String phone, String message) {

        SmsManager smsManager = SmsManager.getDefault();
        message = message + "" +
                "MY LOCATION IS --> Latitude: " + latitude + " Longitude: " + longitude;
        smsManager.sendTextMessage(phone, null, message, null, null);

    }

    public void getLocation() {

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 123);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 123);
        } else {

            LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            longitude = String.valueOf(location.getLongitude());
            longitudeD = location.getLongitude();
            latitude = String.valueOf(location.getLatitude());
            latitudeD = location.getLatitude();
        }
    }

    public void createAlarm(boolean police, boolean fire, boolean medical) {

        if (accessToken == null){
            Toast.makeText(this, "YOU MUST LOGIN TO SEND ALARMS", Toast.LENGTH_SHORT).show();
        }

        else {



            getLocation();

            Retrofit.Builder builder = new Retrofit.Builder()
                    .baseUrl("https://api-sandbox.safetrek.io/v1/")
                    .addConverterFactory(GsonConverterFactory.create());


            Map<String, String> headers = new HashMap<>();

            headers.put("Content-Type", "application/json");
            headers.put("Authorization", "Bearer " + accessToken);


            Services services = new Services(police, fire, medical);
            Locations locations = new Locations(latitudeD, longitudeD, 5);

            AlarmRequest alarmRequest = new AlarmRequest(services, locations);

            Retrofit retrofit = builder.build();
            Client client = retrofit.create(Client.class);

            final Call<Alarm> sendAlarm = client.sendAlarm(
                    headers,
                    alarmRequest
            );

            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {

                    try {
                        alarm = sendAlarm.execute().body();
                        alarmSet = true;
                        alarmId = alarm.getId();
                        //Toast.makeText(MainActivity.this, "ALARM SENT", Toast.LENGTH_SHORT).show();
                        //System.out.println("AToken: " + accessToken);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            });
            t.start();


        }



    }

    public void cancelAlarm (){

        String uri = new String();

        uri = "https://api-sandbox.io/v1/alarms/" + alarmId + "/status";

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("https://api-sandbox.safetrek.io/v1/alarms/")
                .addConverterFactory(GsonConverterFactory.create());


        Map<String, String> headers = new HashMap<>();

        headers.put("Content-Type", "application/json");
        headers.put("Authorization", "Bearer " + accessToken);

        CancelAlarm body = new CancelAlarm("CANCELED");


        Retrofit retrofit = builder.build();
        Client client = retrofit.create(Client.class);
        final Call<Alarm> cancelAlarm = client.cancelAlarm(
                uri,
                headers,
                body);

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    alarm = cancelAlarm.execute().body();
                    Toast.makeText(MainActivity.this, "ALARM CANCELLED", Toast.LENGTH_SHORT).show();
                    alarmSet = false;
                    alarmId = null;
                    //System.out.println("AToken: " + accessToken);
                } catch (IOException e) {
                    Toast.makeText(MainActivity.this, "CANCEL ERROR", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                System.out.println("Access Token" + token.getAccessToken());
            }
        });
        t.start();



    }

    public void checkPin(){

        if (pinCount>=5){
            Toast.makeText(this, "ALARM SET. PIN ATTEMPTS EXCEEDED.", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("ENTER 4 DIGIT PIN TO CANCEL ALARM:");

        final EditText input = new EditText(this);


        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

        builder.setPositiveButton("CANCEL ALARM", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {

                String m_Text = input.getText().toString();
                int temp = Integer.parseInt(m_Text);
                if (temp==pin){
                    cancelAlarm();
                }

                else{
                    pinCount++;
                    Toast.makeText(MainActivity.this, "INCORRECT PIN", Toast.LENGTH_SHORT).show();
                    dialog.cancel();
                }
            }

        });

        builder.setNegativeButton("KEEP ALARM", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.cancel();
            }
        });

        builder.show();
        return;
    }
}