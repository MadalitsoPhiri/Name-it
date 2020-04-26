package com.example.nameit;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;

import android.os.CountDownTimer;
import android.view.View;

import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.Menu;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.example.nameit.R.id.coordinator;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public  NDbhelper databasehepler = new NDbhelper(this);
    public int Score;
    public long MilisecondsLeft;
    //variable for storing the two timers
    public static CountDownTimer Timer1;
    public static CountDownTimer Timer2;
    public long TimerLength = 180000;
   // variable for tracking the alphabet
    public static char Alphabet = 'A';
    public static Fragment MyFragment;
    public static NavigationView navigationView;
    //variables for tracking timers
    public static boolean timer_Is_Running = false;
    public static boolean timer2_Is_Running;
    //variable for tracking alertbox state
    public static boolean alert_Dialog_on = false;
    public int Value;
    public int finalScore;
    public boolean score_Is_Less_Than;
    int Maximum;
     public String formattedDate;

     public WifiP2pManager mManager;
     public WifiP2pManager.Channel channel;
     public WifiBroadcastReciever Reciever;
     public IntentFilter filter;
     public static WifiP2pManager.PeerListListener myPeerListListener;
     public static boolean wifiIsOn;
     public List<WifiP2pDevice> devicePeers = new ArrayList<WifiP2pDevice>();
     public ListView WifiDeviceList;
     public ProgressBar waitingBar;
     public boolean groupCreated;
     public static boolean isGroupOwner;
     public static WifiP2pManager.ConnectionInfoListener connectionInfoListener;
     public boolean IsConnected = false;
     public FragmentManager FragManager = getSupportFragmentManager();
     public WifiP2pGroup group;

    @Override
    protected void onResume() {
        registerReceiver(Reciever,filter);

        if(timer_Is_Running == true){

            if(alert_Dialog_on == false) {
                //set tracker variable for alert dialog box
                alert_Dialog_on = true;

                // dialog box for when the app resumes from paused state
                new AlertDialog.Builder(MainActivity.this)

                        .setTitle("PAUSED")
                        .setCancelable(false)
                        .setMessage("Press Resume to Continue!")

                        .setPositiveButton("Resume", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                alert_Dialog_on = false;

                                //Resume timer when app resumes using a second timer(timer2)

                                Timer2 = new CountDownTimer(MilisecondsLeft, 1000) {
                                    @Override
                                    public void onTick(long millisUntilFinished) {

                                        timer_Is_Running = true;
                                        timer2_Is_Running = true;
                                        MilisecondsLeft = millisUntilFinished;
                                        TextView Clock = findViewById(R.id.clock);
                                        ProgressBar progressBar = findViewById(R.id.ProgressBar);

                                        int SecondsLeft = (int) millisUntilFinished % 60000 / 1000;
                                        int TotalSeconds =180;
                                        int completedSceconds = TotalSeconds-(int)MilisecondsLeft/1000;
                                        if (SecondsLeft < 10) {
                                            Clock.setText("" + millisUntilFinished / 60000 + " : " + "0" + millisUntilFinished % 60000 / 1000);
                                            progressBar.setProgress(completedSceconds);

                                        } else {
                                            Clock.setText("" + millisUntilFinished / 60000 + " : " + millisUntilFinished % 60000 / 1000);
                                            progressBar.setProgress(completedSceconds);
                                        }
                                    }

                                    @Override
                                    public void onFinish() {
                                        TimerLength = 180000;
                                        timer_Is_Running = false;
                                        TimeOut();

                                    }
                                };

                                Timer2.start();
                            }
                        })
                        .setNegativeButton("Quit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // deletes the Score from the Current Game by deleting the first row in the database

                                SQLiteDatabase database = databasehepler.getWritableDatabase();
                                String Selection = NDbhelper.COLUMN_ID + "=?";
                                String[] Args = {"1"};
                                database.delete(NDbhelper.TABLE_NAME,Selection,Args);

                                // close app
                                finish();
                                System.exit(0);

                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show()
                ;


            }else{


            }



        }


            super.onResume();
    }
// method for when the app pauses
    @Override
    protected void onPause() {
        unregisterReceiver(Reciever);
        //check if timer is running
        if(timer_Is_Running == true ){
           // check if timer2 is running
           if(timer2_Is_Running){
               Timer2.cancel();
           }else{
               Timer1.cancel();
           }



        }
        super.onPause();


    }
    /*private void checkFirstRun() {

        final String PREFS_NAME = "MyPrefsFile";
        final String PREF_VERSION_CODE_KEY = "version_code";
        final int DOESNT_EXIST = -1;

        // Get current version code
        int currentVersionCode = BuildConfig.VERSION_CODE;

        // Get saved version code
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int savedVersionCode = prefs.getInt(PREF_VERSION_CODE_KEY, DOESNT_EXIST);

        // Check for first run or upgrade
        if (currentVersionCode == savedVersionCode) {

            // This is just a normal run
            return;

        } else if (savedVersionCode == DOESNT_EXIST) {

            // This is a new install (or the user cleared the shared preferences)
            SQLiteDatabase database1 = databasehepler.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(NDbhelper.COLUMN1_ID,1);
            values.put(NDbhelper.SCORES, 0);
            database1.insert(NDbhelper.HIGH_SCORE, null, values);

        } else if (currentVersionCode > savedVersionCode) {

            // TODO This is an upgrade
        }

        // Update the shared preferences with the current version code
        prefs.edit().putInt(PREF_VERSION_CODE_KEY, currentVersionCode).apply();
    }*/

    // getter method for variable timer_is_running
    public static boolean getTimerIsRunning(){

        return timer_Is_Running;
    }
    // getter method for variable timer2_is_running
 public static boolean getTimer2IsRunning(){

        return timer2_Is_Running;
 }

// method for when the app is closed or destroyed
    @Override
    protected void onDestroy() {

        // deletes the Score from the Current Game by deleting the first row in the database

         SQLiteDatabase database = databasehepler.getWritableDatabase();
         String Selection = NDbhelper.COLUMN_ID + "=?";
         String[] Args = {"1"};
         database.delete(NDbhelper.TABLE_NAME,Selection,Args);
         if(groupCreated) {
             mManager.cancelConnect(channel, new WifiP2pManager.ActionListener() {
                 @Override
                 public void onSuccess() {

                 }

                 @Override
                 public void onFailure(int reason) {

                 }
             });

             mManager.removeGroup(channel, new WifiP2pManager.ActionListener() {
                 @Override
                 public void onSuccess() {
                     unregisterReceiver(Reciever);

                 }

                 @Override
                 public void onFailure(int reason) {

                 }
             });
         }
     unregisterReceiver(Reciever);

        super.onDestroy();
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //call to delete database to prepare for new values
        SQLiteDatabase database = databasehepler.getWritableDatabase();
        String Selection = NDbhelper.COLUMN_ID + "=?";
        String[] Args = {"1"};
        database.delete(NDbhelper.TABLE_NAME,Selection,Args);

        if(savedInstanceState == null){


        navigationView.setCheckedItem(R.id.nav_home);}






        //initializing the wifiP2pmanager, Broadcast Reciever and Channel

        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = mManager.initialize(getApplicationContext(),getMainLooper(),null);
        Reciever = new WifiBroadcastReciever(channel,mManager,MyFragment,FragManager);

        //initializing the intent filter for the broadcastreceiver

        filter = new IntentFilter();
        filter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        filter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        filter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        filter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

      //the connection info listener for when a connection is created to listen for group device ip address and such
        connectionInfoListener = new WifiP2pManager.ConnectionInfoListener() {
            @Override
            public void onConnectionInfoAvailable(WifiP2pInfo info) {
                InetAddress ownerIp =  info.groupOwnerAddress;


                if(info.groupFormed && info.isGroupOwner){
                    Server server = new Server();
                    server.start();





                }
                else if(info.groupFormed){
                    Client client = new Client(ownerIp);
                    client.start();
           mManager.requestGroupInfo(channel, new WifiP2pManager.GroupInfoListener() {
               @Override
               public void onGroupInfoAvailable(WifiP2pGroup group) {

               }
           });

                }


            }
        };





        //initializing the peerlistlistener for getting current peer list
       myPeerListListener = new WifiP2pManager.PeerListListener() {
            @Override
            public void onPeersAvailable(WifiP2pDeviceList peers) {
                ArrayList<String> devices = new ArrayList<String>();

                if(peers.getDeviceList().isEmpty()){
                    //There is no device available
                    Snackbar.make(findViewById(coordinator),"No devices Available",Snackbar.LENGTH_LONG).show();
                    devices.clear();


                }else{

                if(!devicePeers.equals(peers.getDeviceList())){

                    devicePeers.clear();
                    devices.clear();
                    devicePeers.addAll(peers.getDeviceList());


                    for(WifiP2pDevice device:peers.getDeviceList()){
                        devices.add(device.deviceName);
                    }


                    WifiDeviceList = findViewById(R.id.wifip2pDeviceListView2);
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1,devices);

                    WifiDeviceList.setAdapter(adapter);
                    WifiDeviceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            final WifiP2pDevice device = devicePeers.get(position);
                            WifiP2pConfig config = new WifiP2pConfig();
                            config.deviceAddress = device.deviceAddress;

                            mManager.connect(channel, config, new WifiP2pManager.ActionListener() {
                                @Override
                                public void onSuccess() {
                                    MyFragment = new waiting();
                                    getSupportFragmentManager().beginTransaction().replace(R.id.container,MyFragment).commit();
                                }

                                @Override
                                public void onFailure(int reason) {
                                    Snackbar.make(findViewById(coordinator),"Failed to Connected to "+device.deviceName,Snackbar.LENGTH_LONG).show();

                                }
                            });}

                    });
                    WifiDeviceList.setVisibility(View.VISIBLE);

                    adapter.notifyDataSetChanged();
                    Toast.makeText(getApplicationContext(),"running listview!",Toast.LENGTH_SHORT).show();
                    waitingBar = findViewById(R.id.Search_bar);
                    waitingBar.setVisibility(View.INVISIBLE);

                }

                }



            }
        };



        // intialization of the first countdown timer
        Timer1 = new CountDownTimer(TimerLength,1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                 timer_Is_Running = true;
                timer2_Is_Running = false;

                MilisecondsLeft =  millisUntilFinished;
                TextView Clock = findViewById(R.id.clock);
                ProgressBar progressBar = findViewById(R.id.ProgressBar);

                int SecondsLeft = (int) millisUntilFinished % 60000 / 1000;
                int TotalSeconds =180;
                int completedSceconds = TotalSeconds-(int)MilisecondsLeft/1000;
                if (SecondsLeft < 10) {
                    Clock.setText("" + millisUntilFinished / 60000 + " : " + "0" + millisUntilFinished % 60000 / 1000);
                    progressBar.setProgress(completedSceconds);

                } else {
                    Clock.setText("" + millisUntilFinished / 60000 + " : " + millisUntilFinished % 60000 / 1000);
                    progressBar.setProgress(completedSceconds);
                }
            }
            @Override
            public void onFinish() {
                TimerLength = 180000;
                timer_Is_Running = false;
                TimeOut();

            }
        } ;






        getSupportFragmentManager().beginTransaction().replace(R.id.container,new Welcome()).commit();





    }

    public void connectionResponse(){
        Snackbar.make(findViewById(coordinator),"Device connected!",Snackbar.LENGTH_SHORT).show();
        MyFragment = new connected();
        getSupportFragmentManager().beginTransaction().replace(R.id.container,MyFragment).commit();
    }


    @Override
    public void onBackPressed() {

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            new AlertDialog.Builder(MainActivity.this)

                    .setTitle("Quit")
                    .setCancelable(false)
                    .setMessage("Are you sure you want to Quit?")

                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // deletes the Score from the Current Game
                            SQLiteDatabase database = databasehepler.getWritableDatabase();
                            String Selection = NDbhelper.COLUMN_ID + "=?";
                            String[] Args = {"1"};
                            database.delete(NDbhelper.TABLE_NAME, Selection, Args);
                           finish();
                            System.exit(0);

                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();



        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
            if(MyFragment != null && MyFragment.isVisible()) {
                new AlertDialog.Builder(MainActivity.this)

                        .setTitle("Quit")
                        .setCancelable(false)
                        .setMessage("You are about to Cancel current game are you sure you want to quit?")

                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // deletes the Score from the Current Game
                                SQLiteDatabase database = databasehepler.getWritableDatabase();
                                String Selection = NDbhelper.COLUMN_ID + "=?";
                                String[] Args = {"1"};
                                database.delete(NDbhelper.TABLE_NAME, Selection, Args);

                                Alphabet='A';

                                if(timer2_Is_Running){
                                    Timer2.cancel();
                                    MilisecondsLeft = 0;
                                }else{
                                    Timer1.cancel();
                                    MilisecondsLeft = 0;
                                }
                                timer_Is_Running = false;
                                getSupportFragmentManager().beginTransaction().remove(MyFragment).commitAllowingStateLoss();
                                Intent intent = new Intent(getApplicationContext(),settings.class);
                                startActivity(intent);

                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                navigationView.setCheckedItem(R.id.nav_home);
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show()
                ;
            }else {
                Intent intent = new Intent(getApplicationContext(),settings.class);
                startActivity(intent);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home){
            if(MyFragment != null && MyFragment.isVisible()){
        Toast.makeText(getApplicationContext(),"Keep PLaying",Toast.LENGTH_LONG).show();

            }else {

                getSupportFragmentManager().beginTransaction().replace(R.id.container, new Welcome()).commit();
            }
        } else if (id == R.id.nav_highscores) {


            if(MyFragment != null && MyFragment.isVisible()){
                new AlertDialog.Builder(MainActivity.this)

                .setTitle("Quit")
                        .setCancelable(false)
                        .setMessage("You are about to Cancel current game are you sure you want to quit?")

                .setPositiveButton("Yes", new DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // deletes the Score from the Current Game
                        SQLiteDatabase database = databasehepler.getWritableDatabase();
                        String Selection = NDbhelper.COLUMN_ID + "=?";
                        String[] Args = {"1"};
                        database.delete(NDbhelper.TABLE_NAME, Selection, Args);

                        Alphabet='A';

                        if(timer2_Is_Running){
                            Timer2.cancel();
                            MilisecondsLeft = 0;
                        }else{
                            Timer1.cancel();
                            MilisecondsLeft = 0;
                        }
                        getSupportFragmentManager().beginTransaction().replace(R.id.container,new Highscores()).commit();
                    }
                })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                navigationView.setCheckedItem(R.id.nav_home);
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                .setIcon(android.R.drawable.ic_dialog_alert)
                        .show()
                ;


            }else{
                getSupportFragmentManager().beginTransaction().replace(R.id.container,new Highscores()).commit();

            }

        } else if (id == R.id.nav_instructions) {
            if(MyFragment != null && MyFragment.isVisible()) {
                new AlertDialog.Builder(MainActivity.this)

                        .setTitle("Quit")
                        .setCancelable(false)
                        .setMessage("You are about to Cancel current game are you sure you want to quit?")

                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // deletes the Score from the Current Game
                                SQLiteDatabase database = databasehepler.getWritableDatabase();
                                String Selection = NDbhelper.COLUMN_ID + "=?";
                                String[] Args = {"1"};
                                database.delete(NDbhelper.TABLE_NAME, Selection, Args);

                                Alphabet='A';
                                if(timer2_Is_Running){
                                    Timer2.cancel();
                                    MilisecondsLeft = 0;
                                }else{
                                    Timer1.cancel();
                                    MilisecondsLeft = 0;
                                }
                                getSupportFragmentManager().beginTransaction().replace(R.id.container, new Instructions()).commit();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                navigationView.setCheckedItem(R.id.nav_home);
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show()
                ;
            }else {
                getSupportFragmentManager().beginTransaction().replace(R.id.container, new Instructions()).commit();
            }
        } else if (id == R.id.nav_settings) {
            if(MyFragment != null && MyFragment.isVisible()) {
                new AlertDialog.Builder(MainActivity.this)

                        .setTitle("Quit")
                        .setCancelable(false)
                        .setMessage("You are about to Cancel current game are you sure you want to quit?")

                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // deletes the Score from the Current Game
                                SQLiteDatabase database = databasehepler.getWritableDatabase();
                                String Selection = NDbhelper.COLUMN_ID + "=?";
                                String[] Args = {"1"};
                                database.delete(NDbhelper.TABLE_NAME, Selection, Args);

                                Alphabet='A';

                                if(timer2_Is_Running){
                                    Timer2.cancel();
                                    MilisecondsLeft = 0;
                                }else{
                                    Timer1.cancel();
                                    MilisecondsLeft = 0;
                                }
                                timer_Is_Running = false;
                                getSupportFragmentManager().beginTransaction().remove(MyFragment).commitAllowingStateLoss();
                                Intent intent = new Intent(getApplicationContext(),settings.class);
                                startActivity(intent);

                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                navigationView.setCheckedItem(R.id.nav_home);
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show()
                ;
            }else {
                Intent intent = new Intent(getApplicationContext(),settings.class);
                startActivity(intent);
            }


        }


        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void Done(View v){

        //TextField for Country
        //An array to check validity of country
        String Countries [] = {"","Afghanistan","Albania","Algeria","Andorra","Angola","Antigua and Barbuda","Argentina","Armenia","Australia","Austria","Azerbaijan","Bahamas","Bahrain","Bangladesh","Barbados","Belarus","Belgium","Belize","Benin","Bhutan","Bolivia","Bosnia and Herzegovina","Botswana","Brazil","Brunei","Bulgaria","Burkina Faso","Burundi","Cote d'Ivoire","Cabo Verde","Cambodia","Cameroon","Canada","Central African Republic","Chad","Chile","China","Colombia","Comoros","Congo","Costa Rica","Croatia","Cuba","Cyprus","Czechia","Czech Republic","Democratic Republic of Congo","Denmark","Djibouti","Dominica","Dominican Republic","Ecuador","Egypt","El Salvador","Equatorial Guinea","Eritrea","Estonia","Ethiopia","East Timor","Fiji","Finland","France","Gabon","Gambia","Georgia","Germany","Ghana","Greece","Grenada","Guatemala","Guinea","Guinea-Bissau","Guyana","Haiti","Holy See","Honduras","Hungary","Iceland","India","Indonesia","Iran","Iraq","Ireland","Israel","Italy","Ivory Coast","Jamaica","Japan","Jordan","Kazakhstan","Kenya","Kiribati","Kuwait","Kyrgyzstan","Laos","Latvia","Lebanon","Lesotho","Liberia","Libya","Liechtenstein","Lithuania","Luxembourg","Madagascar","Malawi","Malaysia","Maldives","Mali","Malta","Marshall Islands","Mauritania","Mauritius","Mexico","Micronesia","Moldova","Monaco","Mongolia","Montenegro","Morocco","Mozambique","Myanmar","Namibia","Nauru","Nepal","Netherlands","New Zealand","Nicaragua","Niger","Nigeria","North Korea","North Macedonia","Norway","Oman","Pakistan","Palau","Palestine State","Panama","Papua New Guinea","Paraguay","Peru","Philippines","Poland","Portugal","Qatar","Romania","Russia","Rwanda","Saint Kitts and Nevis","Saint Lucia","Saint Vincent and the Grenadines","Samoa","San Marino","Sao Tome and Principe","Saudi Arabia","Senegal","Serbia","Seychelles","Sierra Leone","Singapore","Slovakia","Slovenia","Solomon Islands","Somalia","South Africa","South Korea","South Sudan","Spain","Sri Lanka","Sudan","Suriname","Swaziland","Sweden","Switzerland","Syria","Scotland","Taiwan","Tajikistan","Tanzania","Thailand","Timor-Leste","Togo","Tonga","Trinidad and Tobago","Tunisia","Turkey","Turkmenistan","Tuvalu","Uganda","Ukraine","United Arab Emirates","United Kingdom","United States of America","Uruguay","Uzbekistan","Vanuatu","Venezuela","Vietnam","Wales","Yemen","Zambia","Zimbabwe"};


        // get Text from the user input fields and filter the data
        EditText country = findViewById(R.id.country);
        String Text1 = country.getText().toString();
        Text1=Text1.toUpperCase();

        boolean found = false;

        for(String Current:Countries){
            String CurrentCountry = Current.toUpperCase();
            if (CurrentCountry.compareTo(Text1)==0) {
                found = true;
                break;
            } else {

                found = false;

            }
        }



        if (Text1.matches("")){
            Text1 = "nothing 0 points!";

        } else if (Text1.charAt(0) != Alphabet) {
            country.setError("That Country does not start with Letter "+Alphabet);

        } else if(found==false){

            country.setError("That Country Does not Exist");


        } else {
            Text1 += " 10 points!";
            Score += 10;
        }



        //****** Text Field for cities   *********/

        //get input from user
        EditText city = findViewById(R.id.city);
        String Text2 = city.getText().toString();
        // set the input String to uppercase
        Text2=Text2.toUpperCase();

        boolean found1 = false;

        //an arrayList for the look up of Valid cities
        ArrayList<String> cities = new ArrayList<String>();

        cities.add("");


        //accessing a local text file using the assetmanager,inputStream,inputStreamReader and BufferReader

        AssetManager assetsManager = getApplicationContext().getAssets(); // or getBaseContext()
        try{
            InputStream inputStream = assetsManager.open("Cities.txt");
            InputStreamReader read = new InputStreamReader(inputStream);
            BufferedReader Read = new BufferedReader(read);


            String current;
            while ((current = Read.readLine())!=null) {
                cities.add(current);
            }
            //Closing the reader
            Read.close();
        }catch (IOException ex){
            //House keeping staff for handling an exception
            ex.printStackTrace();
        }


       //An Enhanced for loop iterating through the words arraylist made from the Readers for the checking of validity of the city entered by the user
        for(String Current:cities){
            String CurrentCity = Current.toUpperCase();
            if (CurrentCity.compareTo(Text2)==0) {
                found1 = true;
                break;
            } else {

                found1 = false;

            }
        }



        if (Text2.matches("")){
            Text2 = "nothing 0 points!";

        } else if (Text2.charAt(0) != Alphabet) {
            city.setError("That City does not start with Letter "+Alphabet);


        }
        else if (found1 == false) {
            city.setError("That City does not Exist");


        }


        else {
            Text2 += " 10 points!";
            Score += 10;
        }



        //TextField for Name
        EditText name = findViewById(R.id.name);
        String Text3 = name.getText().toString();
        Text3=Text3.toUpperCase();

        boolean found2 = false;


        //an arrayList for the look up of Valid cities
        ArrayList<String> names = new ArrayList<String>();

        names.add("");


        //accessing a local text file using the assetmanager,inputStream,inputStreamReader and BufferReader

        AssetManager assetsManager1 = getApplicationContext().getAssets(); // or getBaseContext()
        try{
            InputStream inputStream = assetsManager1.open("Names.txt");
            InputStreamReader read = new InputStreamReader(inputStream);
            BufferedReader Read = new BufferedReader(read);


            String current;
            while ((current = Read.readLine())!=null) {
                names.add(current);
            }
            //Closing the reader
            Read.close();
        }catch (IOException ex){
            //House keeping staff for handling an exception
            ex.printStackTrace();
        }


        //An Enhanced for loop iterating through the words arraylist made from the Readers for the checking of validity of the city entered by the user
        for(String Current:names){
            String CurrentName = Current.toUpperCase();
            if (CurrentName.compareTo(Text3)==0) {
                found2 = true;
                break;
            } else {

                found2 = false;

            }
        }

        if (Text3.matches("")){
            Text3 = "nothing 0 points!";

        } else if (Text3.charAt(0) != Alphabet) {
            name.setError("That Name does not start with Letter "+Alphabet);


        }
        else if (found2 == false) {
            name.setError("That Name does not Exist");

        }else {
            Text3 += " 10 points!";
            Score += 10;
        }

// ********Color TextField ********//

        EditText color = findViewById(R.id.color);
        String Text4 = color.getText().toString();
        Text4=Text4.toUpperCase();

        boolean found3 = false;

        //an arrayList for the look up of Valid cities
        ArrayList<String> colors = new ArrayList<String>();

        colors.add("");


        //accessing a local text file using the assetmanager,inputStream,inputStreamReader and BufferReader

        AssetManager assetsManager2 = getApplicationContext().getAssets(); // or getBaseContext()
        try{
            InputStream inputStream = assetsManager2.open("Colors.txt");
            InputStreamReader read = new InputStreamReader(inputStream);
            BufferedReader Read = new BufferedReader(read);


            String current;
            while ((current = Read.readLine())!=null) {
                colors.add(current);
            }
            //Closing the reader
            Read.close();
        }catch (IOException ex){
            //House keeping staff for handling an exception
            ex.printStackTrace();
        }


        //An Enhanced for loop iterating through the colors arraylist made from the Readers for the checking of validity of the city entered by the user
        for(String Current:colors){
            String CurrentColor = Current.toUpperCase();
            if (CurrentColor.compareTo(Text4)==0) {
                found3 = true;
                break;
            } else {

                found3 = false;

            }
        }

        if (Text4.matches("")){
            Text4 = "nothing 0 points!";

        } else if (Text4.charAt(0) != Alphabet) {
            color.setError("That Color does not start with Letter "+Alphabet);


        }
        else if (found3 == false) {
            color.setError("That Color does not Exist");

        }

        else {
            Text4 += " 10 points!";
            Score += 10;
        }



       //

        EditText food = findViewById(R.id.food);
        String Text5 = food.getText().toString();
        Text5=Text5.toUpperCase();

        boolean found5 = false;

        //an arrayList for the look up of Valid cities
        ArrayList<String> foods = new ArrayList<String>();

        foods.add("");


        //accessing a local text file using the assetmanager,inputStream,inputStreamReader and BufferReader

        AssetManager assetsManager4 = getApplicationContext().getAssets(); // or getBaseContext()
        try{
            InputStream inputStream = assetsManager4.open("Food.txt");
            InputStreamReader read = new InputStreamReader(inputStream);
            BufferedReader Read = new BufferedReader(read);


            String current;
            while ((current = Read.readLine())!=null) {
                foods.add(current);
            }
            //Closing the reader
            Read.close();
        }catch (IOException ex){
            //House keeping staff for handling an exception
            ex.printStackTrace();
        }


        //An Enhanced for loop iterating through the words arraylist made from the Readers for the checking of validity of the city entered by the user
        for(String Current:foods){
            String CurrentFood = Current.toUpperCase();
            if (CurrentFood.compareTo(Text5)==0) {
                found5 = true;
                break;
            } else {

                found5 = false;

            }
        }



        if (Text5.matches("")){
            Text5 = "nothing 0 points!";

        } else if (Text5.charAt(0) != Alphabet) {
            food.setError("That Food does not start with Letter "+Alphabet);

        }
        else if (found5 == false) {
            food.setError("That Food is not Valid");

        }

        else {
            Text5 += " 10 points!";
            Score += 10;
        }


        //********Animal TextField ********/

        EditText animal = findViewById(R.id.animal);
        String Text6 = animal.getText().toString();
        Text6=Text6.toUpperCase();

        boolean found4 = false;

        //an arrayList for the look up of Valid cities
        ArrayList<String> animals = new ArrayList<String>();

        animals.add("");


        //accessing a local text file using the assetmanager,inputStream,inputStreamReader and BufferReader

        AssetManager assetsManager3 = getApplicationContext().getAssets(); // or getBaseContext()
        try{
            InputStream inputStream = assetsManager3.open("Animals.txt");
            InputStreamReader read = new InputStreamReader(inputStream);
            BufferedReader Read = new BufferedReader(read);


            String current;
            while ((current = Read.readLine())!=null) {
                animals.add(current);
            }
            //Closing the reader
            Read.close();
        }catch (IOException ex){
            //House keeping staff for handling an exception
            ex.printStackTrace();
        }


        //An Enhanced for loop iterating through the words arraylist made from the Readers for the checking of validity of the city entered by the user
        for(String Current:animals){
            String CurrentAnimal = Current.toUpperCase();
            if (CurrentAnimal.compareTo(Text6)==0) {
                found4 = true;
                break;
            } else {

                found4 = false;

            }
        }

        if (Text6.matches("")){
            Text6 = "nothing 0 points!";

        } else if (Text6.charAt(0) != Alphabet) {
            animal.setError("That Animal does not start with Letter "+Alphabet);

        }
        else if (found4 == false) {
            animal.setError("That Animal does not Exist");

        }


        else {
            Text6 += " 10 points!";
            Score += 10;
        }


        EditText car = findViewById(R.id.car);
        String Text7 = car.getText().toString();
        Text7=Text7.toUpperCase();


        boolean found6 = false;

        //an arrayList for the look up of Valid cities
        ArrayList<String> Cars = new ArrayList<String>();

        Cars.add("");


        //accessing a local text file using the assetmanager,inputStream,inputStreamReader and BufferReader

        AssetManager assetsManager5 = getApplicationContext().getAssets(); // or getBaseContext()
        try{
            InputStream inputStream = assetsManager5.open("Cars.txt");
            InputStreamReader read = new InputStreamReader(inputStream);
            BufferedReader Read = new BufferedReader(read);


            String current;
            while ((current = Read.readLine())!=null) {
                Cars.add(current);
            }
            //Closing the reader
            Read.close();
        }catch (IOException ex){
            //House keeping staff for handling an exception
            ex.printStackTrace();
        }


        //An Enhanced for loop iterating through the words arraylist made from the Readers for the checking of validity of the city entered by the user
        for(String Current:Cars){
            String CurrentCar = Current.toUpperCase();
            if (CurrentCar.compareTo(Text7)==0) {
                found6 = true;
                break;
            } else {

                found6 = false;

            }
        }

        if (Text7.matches("")){
            Text7 = "nothing 0 points!";

        } else if (Text7.charAt(0) != Alphabet) {
            car.setError("That Car does not start with Letter "+Alphabet);

        }
        else if (found6 == false) {
            car.setError("That Car does not Exist");

        }

        else {
            Text7 += " 10 points!";
            Score += 10;
        }

//Stop the user from entering invalid data
        if (found == false || found1 ==false ||found2 == false||found3 ==false||found4 == false || found5 == false|| found6 == false|| (Text1.charAt(0) != Alphabet && Text1!="nothing 0 points!")||(Text2.charAt(0) != Alphabet && Text2 !="nothing 0 points!") ||(Text3.charAt(0) != Alphabet && Text3 !="nothing 0 points!")||(Text4.charAt(0) != Alphabet && Text4 !="nothing 0 points!") ||(Text5.charAt(0) != Alphabet && Text5 !="nothing 0 points!")||(Text6.charAt(0) != Alphabet && Text6 !="nothing 0 points!")||(Text7.charAt(0) != Alphabet && Text7 !="nothing 0 points!") )
        {

            Toast.makeText(getApplicationContext()," OOP's Something's wrong!",Toast.LENGTH_LONG).show();
            Score = 0;


            return;
        }

       // Updates the database with the players Current Score
        Update_score();

// get the Scorepage fragment and attach a bundle to it for data transfer

       String ScoreString = Integer.toString(Score);

        ScorePage currentfragment = new ScorePage();
        Bundle args = new Bundle();

        args.putString("Country",Text1);
        args.putString("City",Text2);
        args.putString("Name",Text3);
        args.putString("Color",Text4);
        args.putString("Food",Text5);
        args.putString("Animal",Text6);
        args.putString("Car",Text7);
        args.putString("Score",ScoreString);

        currentfragment.setArguments(args);
   if(timer2_Is_Running){
       Timer2.cancel();
   }else{
       Timer1.cancel();
   }

        timer_Is_Running = false;
        getSupportFragmentManager().beginTransaction().replace(R.id.container,currentfragment).commit();



    }

    public void next(View view){


        //This is regulating the alphabet during gameplay
        //it changes the letter in the alphabet
        if(Alphabet=='Z'){

            new AlertDialog.Builder(MainActivity.this)

                    .setTitle("The End!")
                    .setCancelable(false)
                    .setMessage("OOPs You are all out of letters!\n Want to play again?")

                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            //Update highscores database
                            Highscores_Update();


                           // delete temporary score database
                            // deletes the Score from the Current Game by deleting the first row in the database

                            SQLiteDatabase database = databasehepler.getWritableDatabase();
                            String Selection = NDbhelper.COLUMN_ID + "=?";
                            String[] Args = {"1"};
                            database.delete(NDbhelper.TABLE_NAME,Selection,Args);

                            //open new fragment for new game
                            MyFragment = new play();
                            getSupportFragmentManager().beginTransaction().replace(R.id.container, MyFragment).commit();


                        }
                    })
                    .setNegativeButton("No",  new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            //Update highscores database
                            Highscores_Update();

                            // deletes the Score from the Previous Game by deleting the first row in the database

                            SQLiteDatabase database = databasehepler.getWritableDatabase();
                            String Selection = NDbhelper.COLUMN_ID + "=?";
                            String[] Args = {"1"};
                            database.delete(NDbhelper.TABLE_NAME,Selection,Args);
                            MyFragment = null;
                            getSupportFragmentManager().beginTransaction().replace(R.id.container, new Welcome()).commit();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show()
            ;


            Alphabet='A';

        }
        else {
            Alphabet++;
            // go back to the play game screen
            MyFragment = new play();
            getSupportFragmentManager().beginTransaction().replace(R.id.container,MyFragment).commit();
        }



        // set The score Variable to 0 for next round
        Score = 0;


    }

    public static String getLetter(){

      String Letter = Character.toString(Alphabet);
      return Letter;
    }
    // method to update the permanent high score database
    public void Highscores_Update() {

        //read from the non permanent table the scoretable
        SQLiteDatabase database = databasehepler.getReadableDatabase();
        String[] projection = {NDbhelper.SCORE};
        String selection = NDbhelper.COLUMN_ID + "=?";
        String[] args = {"1"};
        Cursor cursor = database.query(NDbhelper.TABLE_NAME, projection, selection, args, null, null, null);
        while (cursor.moveToNext()) {
            finalScore = cursor.getInt(cursor.getColumnIndex(NDbhelper.SCORE));
        }
        cursor.close();

        SQLiteDatabase db = databasehepler.getWritableDatabase();
        String count = "SELECT count(*) FROM ";
        Cursor mcursor = db.rawQuery(count+NDbhelper.HIGH_SCORE, null);
        mcursor.moveToFirst();
        int icount = mcursor.getInt(0);
        if(icount>0){


        //Read from the highscore table
        SQLiteDatabase database2 = databasehepler.getReadableDatabase();
        ArrayList<Integer> highscoreList = new ArrayList<Integer>();

        Cursor cursor1 = database2.query(NDbhelper.HIGH_SCORE, null, null, null, null, null, null);

                        while (cursor1.moveToNext()) {
                            int score1 = cursor1.getColumnIndex(NDbhelper.SCORES);
                            int Score1 = cursor1.getInt(score1);
                            highscoreList.add(Score1);
                        }

                        cursor1.close();
                        //iterate through the highscorelist to compare scores with current score
                        Maximum = highscoreList.get(0);

                        for (int s = 0; s < highscoreList.size(); s++) {
                            if (highscoreList.get(s) > Maximum)
                                Maximum = highscoreList.get(s);

                        }

                        if (Maximum < finalScore) {
                            score_Is_Less_Than = true;
                        } else {
                            score_Is_Less_Than = false;
                        }


                        //if score is less than current score then add it to the databbase
                        if (score_Is_Less_Than == true) {
                            Date c = Calendar.getInstance().getTime();

                            SimpleDateFormat df = new SimpleDateFormat("dd/MMM/yyyy");
                            formattedDate = df.format(c);

                            SQLiteDatabase database1 = databasehepler.getWritableDatabase();
                            ContentValues values = new ContentValues();
                            values.put(NDbhelper.SCORES, finalScore);
                            values.put(NDbhelper.DATE, formattedDate);
                            database1.insert(NDbhelper.HIGH_SCORE, null, values);
                        }






        }else{
            Date c = Calendar.getInstance().getTime();

            SimpleDateFormat df = new SimpleDateFormat("dd/MMM/yyyy");
            formattedDate = df.format(c);

            SQLiteDatabase database1 = databasehepler.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(NDbhelper.SCORES, finalScore);
            values.put(NDbhelper.DATE, formattedDate);
            database1.insert(NDbhelper.HIGH_SCORE, null, values);


        }

        mcursor.close();

        }


    //method for when the time runs out
    public void TimeOut(){

        //TextField for Country
        //An array to check validity of country
        String Countries [] = {"","Afghanistan","Albania","Algeria","Andorra","Angola","Antigua and Barbuda","Argentina","Armenia","Australia","Austria","Azerbaijan","Bahamas","Bahrain","Bangladesh","Barbados","Belarus","Belgium","Belize","Benin","Bhutan","Bolivia","Bosnia and Herzegovina","Botswana","Brazil","Brunei","Bulgaria","Burkina Faso","Burundi","Cote d'Ivoire","Cabo Verde","Cambodia","Cameroon","Canada","Central African Republic","Chad","Chile","China","Colombia","Comoros","Congo","Costa Rica","Croatia","Cuba","Cyprus","Czechia","Czech Republic","Democratic Republic of Congo","Denmark","Djibouti","Dominica","Dominican Republic","Ecuador","Egypt","El Salvador","Equatorial Guinea","Eritrea","Estonia","Ethiopia","Fiji","Finland","France","Gabon","Gambia","Georgia","Germany","Ghana","Greece","Grenada","Guatemala","Guinea","Guinea-Bissau","Guyana","Haiti","Holy See","Honduras","Hungary","Iceland","India","Indonesia","Iran","Iraq","Ireland","Israel","Italy","Ivory Coast","Jamaica","Japan","Jordan","Kazakhstan","Kenya","Kiribati","Kuwait","Kyrgyzstan","Laos","Latvia","Lebanon","Lesotho","Liberia","Libya","Liechtenstein","Lithuania","Luxembourg","Madagascar","Malawi","Malaysia","Maldives","Mali","Malta","Marshall Islands","Mauritania","Mauritius","Mexico","Micronesia","Moldova","Monaco","Mongolia","Montenegro","Morocco","Mozambique","Myanmar","Namibia","Nauru","Nepal","Netherlands","New Zealand","Nicaragua","Niger","Nigeria","North Korea","North Macedonia","Norway","Oman","Pakistan","Palau","Palestine State","Panama","Papua New Guinea","Paraguay","Peru","Philippines","Poland","Portugal","Qatar","Romania","Russia","Rwanda","Saint Kitts and Nevis","Saint Lucia","Saint Vincent and the Grenadines","Samoa","San Marino","Sao Tome and Principe","Saudi Arabia","Senegal","Serbia","Seychelles","Sierra Leone","Singapore","Slovakia","Slovenia","Solomon Islands","Somalia","South Africa","South Korea","South Sudan","Spain","Sri Lanka","Sudan","Suriname","Swaziland","Sweden","Switzerland","Syria","Taiwan","Tajikistan","Tanzania","Thailand","Timor-Leste","Togo","Tonga","Trinidad and Tobago","Tunisia","Turkey","Turkmenistan","Tuvalu","Uganda","Ukraine","United Arab Emirates","United Kingdom","United States of America","Uruguay","Uzbekistan","Vanuatu","Venezuela","Vietnam","Yemen","Zambia","Zimbabwe"};


        // get Text from the user input fields and filter the data
        EditText country = findViewById(R.id.country);
        String Text1 = country.getText().toString();
        Text1=Text1.toUpperCase();

        boolean found = false;

        for(String Current:Countries){
            String CurrentCountry = Current.toUpperCase();
            if (CurrentCountry.compareTo(Text1)==0) {
                found = true;
                break;
            } else {

                found = false;

            }
        }



        if (Text1.matches("")){
            Text1 = "nothing 0 points!";

        } else if (Text1.charAt(0) != Alphabet) {
            Text1 ="Wrong Letter 0 points!";

        } else if(found==false){

            Text1 ="That Country is invalid 0 points!";


        } else {
            Text1 += " 10 points!";
            Score += 10;
        }



        //****** Text Field for cities   *********/

        //get input from user
        EditText city = findViewById(R.id.city);
        String Text2 = city.getText().toString();
        // set the input String to uppercase
        Text2=Text2.toUpperCase();

        boolean found1 = false;

        //an arrayList for the look up of Valid cities
        ArrayList<String> cities = new ArrayList<String>();

        cities.add("");


        //accessing a local text file using the assetmanager,inputStream,inputStreamReader and BufferReader

        AssetManager assetsManager = getApplicationContext().getAssets(); // or getBaseContext()
        try{
            InputStream inputStream = assetsManager.open("Cities.txt");
            InputStreamReader read = new InputStreamReader(inputStream);
            BufferedReader Read = new BufferedReader(read);


            String current;
            while ((current = Read.readLine())!=null) {
                cities.add(current);
            }
            //Closing the reader
            Read.close();
        }catch (IOException ex){
            //House keeping staff for handling an exception
            ex.printStackTrace();
        }


        //An Enhanced for loop iterating through the words arraylist made from the Readers for the checking of validity of the city entered by the user
        for(String Current:cities){
            String CurrentCity = Current.toUpperCase();
            if (CurrentCity.compareTo(Text2)==0) {
                found1 = true;
                break;
            } else {

                found1 = false;

            }
        }



        if (Text2.matches("")){
            Text2 = "nothing 0 points!";

        } else if (Text2.charAt(0) != Alphabet) {
            Text2 = "Wrong Letter 0 points!";


        }
        else if (found1 == false) {
            Text2 = "That City is invalid 0 points!";


        }


        else {
            Text2 += " 10 points!";
            Score += 10;
        }



        //TextField for Name
        EditText name = findViewById(R.id.name);
        String Text3 = name.getText().toString();
        Text3=Text3.toUpperCase();

        boolean found2 = false;


        //an arrayList for the look up of Valid cities
        ArrayList<String> names = new ArrayList<String>();

        names.add("");


        //accessing a local text file using the assetmanager,inputStream,inputStreamReader and BufferReader

        AssetManager assetsManager1 = getApplicationContext().getAssets(); // or getBaseContext()
        try{
            InputStream inputStream = assetsManager1.open("Names.txt");
            InputStreamReader read = new InputStreamReader(inputStream);
            BufferedReader Read = new BufferedReader(read);


            String current;
            while ((current = Read.readLine())!=null) {
                names.add(current);
            }
            //Closing the reader
            Read.close();
        }catch (IOException ex){
            //House keeping staff for handling an exception
            ex.printStackTrace();
        }


        //An Enhanced for loop iterating through the words arraylist made from the Readers for the checking of validity of the city entered by the user
        for(String Current:names){
            String CurrentName = Current.toUpperCase();
            if (CurrentName.compareTo(Text3)==0) {
                found2 = true;
                break;
            } else {

                found2 = false;

            }
        }

        if (Text3.matches("")){
            Text3 = "nothing 0 points!";

        } else if (Text3.charAt(0) != Alphabet) {
            Text3 = "Wrong Letter 0 points!";


        }
        else if (found2 == false) {
             Text3 = "That Name is invalid 0 points!";

        }else {
            Text3 += " 10 points!";
            Score += 10;
        }

// ********Color TextField ********//

        EditText color = findViewById(R.id.color);
        String Text4 = color.getText().toString();
        Text4=Text4.toUpperCase();

        boolean found3 = false;

        //an arrayList for the look up of Valid cities
        ArrayList<String> colors = new ArrayList<String>();

        colors.add("");


        //accessing a local text file using the assetmanager,inputStream,inputStreamReader and BufferReader

        AssetManager assetsManager2 = getApplicationContext().getAssets(); // or getBaseContext()
        try{
            InputStream inputStream = assetsManager2.open("Colors.txt");
            InputStreamReader read = new InputStreamReader(inputStream);
            BufferedReader Read = new BufferedReader(read);


            String current;
            while ((current = Read.readLine())!=null) {
                colors.add(current);
            }
            //Closing the reader
            Read.close();
        }catch (IOException ex){
            //House keeping staff for handling an exception
            ex.printStackTrace();
        }


        //An Enhanced for loop iterating through the colors arraylist made from the Readers for the checking of validity of the city entered by the user
        for(String Current:colors){
            String CurrentColor = Current.toUpperCase();
            if (CurrentColor.compareTo(Text4)==0) {
                found3 = true;
                break;
            } else {

                found3 = false;

            }
        }

        if (Text4.matches("")){
            Text4 = "nothing 0 points!";

        } else if (Text4.charAt(0) != Alphabet) {
            Text4 = "Wrong Letter 0 points!";


        }
        else if (found3 == false) {
            Text4 = "That Color is invalid 0 points!";

        }

        else {
            Text4 += " 10 points!";
            Score += 10;
        }



        //

        EditText food = findViewById(R.id.food);
        String Text5 = food.getText().toString();
        Text5=Text5.toUpperCase();

        boolean found5 = false;

        //an arrayList for the look up of Valid cities
        ArrayList<String> foods = new ArrayList<String>();

        foods.add("");


        //accessing a local text file using the assetmanager,inputStream,inputStreamReader and BufferReader

        AssetManager assetsManager4 = getApplicationContext().getAssets(); // or getBaseContext()
        try{
            InputStream inputStream = assetsManager4.open("Food.txt");
            InputStreamReader read = new InputStreamReader(inputStream);
            BufferedReader Read = new BufferedReader(read);


            String current;
            while ((current = Read.readLine())!=null) {
                foods.add(current);
            }
            //Closing the reader
            Read.close();
        }catch (IOException ex){
            //House keeping staff for handling an exception
            ex.printStackTrace();
        }


        //An Enhanced for loop iterating through the words arraylist made from the Readers for the checking of validity of the city entered by the user
        for(String Current:foods){
            String CurrentFood = Current.toUpperCase();
            if (CurrentFood.compareTo(Text5)==0) {
                found5 = true;
                break;
            } else {

                found5 = false;

            }
        }



        if (Text5.matches("")){
            Text5 = "nothing 0 points!";

        } else if (Text5.charAt(0) != Alphabet) {
            Text5 = "Wrong Letter 0 points!";

        }
        else if (found5 == false) {
            Text5 = "That Food is invalid 0 points!";
        }

        else {
            Text5 += " 10 points!";
            Score += 10;
        }


        //********Animal TextField ********/

        EditText animal = findViewById(R.id.animal);
        String Text6 = animal.getText().toString();
        Text6=Text6.toUpperCase();

        boolean found4 = false;

        //an arrayList for the look up of Valid cities
        ArrayList<String> animals = new ArrayList<String>();

        animals.add("");


        //accessing a local text file using the assetmanager,inputStream,inputStreamReader and BufferReader

        AssetManager assetsManager3 = getApplicationContext().getAssets(); // or getBaseContext()
        try{
            InputStream inputStream = assetsManager3.open("Animals.txt");
            InputStreamReader read = new InputStreamReader(inputStream);
            BufferedReader Read = new BufferedReader(read);


            String current;
            while ((current = Read.readLine())!=null) {
                animals.add(current);
            }
            //Closing the reader
            Read.close();
        }catch (IOException ex){
            //House keeping staff for handling an exception
            ex.printStackTrace();
        }


        //An Enhanced for loop iterating through the words arraylist made from the Readers for the checking of validity of the city entered by the user
        for(String Current:animals){
            String CurrentAnimal = Current.toUpperCase();
            if (CurrentAnimal.compareTo(Text6)==0) {
                found4 = true;
                break;
            } else {

                found4 = false;

            }
        }

        if (Text6.matches("")){
            Text6 = "nothing 0 points!";

        } else if (Text6.charAt(0) != Alphabet) {
           Text6 = "Wrong Letter 0 points!";

        }
        else if (found4 == false) {
           Text6 = "That Animal is invalid 0 points!";

        }


        else {
            Text6 += " 10 points!";
            Score += 10;
        }


        EditText car = findViewById(R.id.car);
        String Text7 = car.getText().toString();
        Text7=Text7.toUpperCase();


        boolean found6 = false;

        //an arrayList for the look up of Valid cities
        ArrayList<String> Cars = new ArrayList<String>();

       //Add an empty String to the dataset for when the textfield receives an empty String
        Cars.add("");


        //accessing a local text file using the assetmanager,inputStream,inputStreamReader and BufferReader

        AssetManager assetsManager5 = getApplicationContext().getAssets(); // or getBaseContext()
        try{
            InputStream inputStream = assetsManager5.open("Cars.txt");
            InputStreamReader read = new InputStreamReader(inputStream);
            BufferedReader Read = new BufferedReader(read);


            String current;
            while ((current = Read.readLine())!=null) {
                Cars.add(current);
            }
            //Closing the reader
            Read.close();
        }catch (IOException ex){
            //House keeping staff for handling an exception
            ex.printStackTrace();
        }


        //An Enhanced for loop iterating through the words arraylist made from the Readers for the checking of validity of the city entered by the user
        for(String Current:Cars){
            String CurrentCar = Current.toUpperCase();
            if (CurrentCar.compareTo(Text7)==0) {
                found6 = true;
                break;
            } else {

                found6 = false;

            }
        }

        if (Text7.matches("")){
            Text7 = "nothing 0 points!";

        } else if (Text7.charAt(0) != Alphabet) {
            Text7 = "Wrong letter 0 points!";

        }
        else if (found6 == false) {
            Text7 = "That Car is invalid 0 points!";

        }

        else {
            Text7 += " 10 points!";
            Score += 10;
        }

// Updates the database with the players Current Score
        Update_score();

// get the Scorepage fragment and attach a bundle to it for data transfer

        String ScoreString = Integer.toString(Score);

        ScorePage currentfragment = new ScorePage();
        Bundle args = new Bundle();

        args.putString("Country",Text1);
        args.putString("City",Text2);
        args.putString("Name",Text3);
        args.putString("Color",Text4);
        args.putString("Food",Text5);
        args.putString("Animal",Text6);
        args.putString("Car",Text7);
        args.putString("Score",ScoreString);

        currentfragment.setArguments(args);

        if(timer2_Is_Running){
            Timer2.cancel();
        }else{
            Timer1.cancel();
        }
        timer_Is_Running = false;
        getSupportFragmentManager().beginTransaction().replace(R.id.container,currentfragment).commit();
        Toast.makeText(getApplicationContext()," OOP's You are out of Time",Toast.LENGTH_LONG).show();


    }
    //Play button method for when its clicked
    public void Play(View view){
      //check if wifi is on
        if(wifiIsOn){
            MyFragment = new Connect();

            getSupportFragmentManager().beginTransaction().replace(R.id.container,MyFragment).commit();

        }else{
            //show a snackbar if wifi is not turned on
            Snackbar.make(findViewById(coordinator),"Please turn on your WIFI",Snackbar.LENGTH_LONG).show();
        }




    }
    // Method to update score variable from database
    public void Update_score(){
        //if the letter is not A then proceed as planned
       //get Previous score from database and add it to the Current Score
           SQLiteDatabase score1 = databasehepler.getReadableDatabase();
           String[] projection = {NDbhelper.SCORE};
           String selection = NDbhelper.COLUMN_ID + "=?";
           String[] args = {"1"};


        Cursor cursor = score1.query(NDbhelper.TABLE_NAME, projection, selection, args, null, null, null);
           while (cursor.moveToNext()){
               int Score_Index = cursor.getColumnIndex(NDbhelper.SCORE);
               int Current_Score = cursor.getInt(Score_Index);
               Value = Current_Score + Score;
           }
           cursor.close();

           SQLiteDatabase score2 = databasehepler.getWritableDatabase();
           String Selection = NDbhelper.COLUMN_ID + "=?";
           String[] Args = {"1"};

           ContentValues values = new ContentValues();
           values.put(NDbhelper.SCORE, Value);
           score2.update(NDbhelper.TABLE_NAME, values, Selection, Args);


       }
       //Method for when the join button is clicked to join the a wifip2p group
       public void startJoin(){

        if(wifiIsOn == true){

            //Starting discovery of peers to join gameplay
            mManager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                    MyFragment = new Join();
                    getSupportFragmentManager().beginTransaction().replace(R.id.container,MyFragment).commit();
                }


                @Override
                public void onFailure(int reason) {

                }
            });

        }else{

            Snackbar.make(findViewById(coordinator),"Please turn on your WIFI",Snackbar.LENGTH_LONG).show();
        }


       }
    //Method for when the host button is clicked to host the a wifip2p group
       public void startHost(){

        if(wifiIsOn == true){
           mManager.createGroup(channel, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                    groupCreated= true;
                    isGroupOwner = true;
                    getSupportFragmentManager().beginTransaction().replace(R.id.container,new host()).commit();


                }

                @Override
                public void onFailure(int reason) {

                    Snackbar.make(findViewById(coordinator),"group creation failed! log= "+reason,Snackbar.LENGTH_LONG).show();

                }
            });

        }else{
            Snackbar.make(findViewById(coordinator),"Please turn on your WIFI",Snackbar.LENGTH_LONG).show();

        }


       }

       //Thread for the Server Side Socket communication
      public class Server extends Thread{
        int PortNumber = 8080;
        ServerSocket serverSocket;
        Socket socket;

          @Override
          public void run() {
              try {
                  serverSocket = new ServerSocket(PortNumber);
                  socket = serverSocket.accept();
                  sendReceive SendReceive = new sendReceive(socket);
                  SendReceive.start();

              }catch(IOException e){
                  e.printStackTrace();

              }
              super.run();
          }




      }

//Thread for the Client Side Communication
      public class Client extends Thread{
        InetAddress hostAddress;
        Socket socket = new Socket();
        public Client(InetAddress hostadd){
            this.hostAddress = hostadd;

        }

          @Override
          public void run() {
              try {
                  socket.connect(new InetSocketAddress(hostAddress, 8080),1000);
                  sendReceive SendReceive = new sendReceive(socket);
                  SendReceive.start();
              }catch (IOException e){
                  e.printStackTrace();

              }
          }
      }
   public class sendReceive extends Thread{
        Socket socket;
        ObjectInputStream Objectin;
        ObjectOutputStream Objectout;

       public sendReceive(Socket current_socket){
           this.socket = current_socket;
           try{
               Objectin = new ObjectInputStream(socket.getInputStream());
               Objectout= new ObjectOutputStream(socket.getOutputStream());

           }catch(IOException e){
               e.printStackTrace();
           }

       }

       @Override
       public void run() {
              while(socket!= null){

              }

       }
   }








    }








