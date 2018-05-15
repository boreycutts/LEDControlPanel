package com.coreybutts.ledcontrolpanel;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.app.FragmentManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    /* VARIABLES **********************************************************************************/
    // Bluetooth Stuff
    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;
    OutputStream mmOutputStream;
    InputStream mmInputStream;
    Thread workerThread;
    byte[] readBuffer;
    int readBufferPosition;
    volatile boolean stopWorker;
    boolean open = false;

    // Fragment Managment Stuff
    FragmentManager fragmentManager = getFragmentManager();
    MyLeds fragment_my_leds;
    SolidColors fragment_solid_colors;
    StaticPatterns fragment_static_patterns;
    DynamicPatterns fragment_dynamic_patterns;
    /* ********************************************************************************************/

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        /* NAVIGATION BAR STUFF *******************************************************************/
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        /* ****************************************************************************************/

        fragmentManager.beginTransaction().replace(R.id.content_frame, new MyLeds(), "my_leds").commit();
        fragmentManager.executePendingTransactions();
        fragment_my_leds = (MyLeds)fragmentManager.findFragmentByTag("my_leds");
    }

    @Override
    public void onBackPressed()
    {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
        }
        else
        {
            super.onBackPressed();
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_leds)
        {
            fragmentManager.beginTransaction().replace(R.id.content_frame, new MyLeds(), "my_leds").commit();
            fragmentManager.executePendingTransactions();
            fragment_my_leds = (MyLeds)fragmentManager.findFragmentByTag("my_leds");
        }
        else if (id == R.id.nav_colors)
        {
            fragmentManager.beginTransaction().replace(R.id.content_frame, new SolidColors(), "solid_colors").commit();
            fragmentManager.executePendingTransactions();
            fragment_solid_colors = (SolidColors)fragmentManager.findFragmentByTag("solid_colors");
        }
        else if (id == R.id.nav_static)
        {
            fragmentManager.beginTransaction().replace(R.id.content_frame, new StaticPatterns(), "static_patterns").commit();
            fragmentManager.executePendingTransactions();
            fragment_static_patterns = (StaticPatterns) fragmentManager.findFragmentByTag("static_patterns");
        }
        else if (id == R.id.nav_dynamic)
        {
            fragmentManager.beginTransaction().replace(R.id.content_frame, new DynamicPatterns(), "dynamic_patterns").commit();
            fragmentManager.executePendingTransactions();
            fragment_dynamic_patterns = (DynamicPatterns) fragmentManager.findFragmentByTag("dynamic_patterns");
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void red_OnClick(View v) throws IOException
    {
        mmOutputStream.write(255);
        mmOutputStream.write(1);
        mmOutputStream.write(0);
        mmOutputStream.write(255);
        mmOutputStream.write(0);
    }

    public void orange_OnClick(View v) throws IOException
    {
        mmOutputStream.write(1);
    }

    public void yellow_OnClick(View v) throws IOException
    {
        mmOutputStream.write(2);
    }

    public void green_OnClick(View v) throws IOException
    {
        mmOutputStream.write(3);
    }

    public void blue_OnClick(View v) throws IOException
    {
        mmOutputStream.write(4);
    }

    public void indigo_OnClick(View v) throws IOException
    {
        mmOutputStream.write(5);
    }

    public void violet_OnClick(View v) throws IOException
    {
        mmOutputStream.write(6);
    }

    public void white_OnClick(View v) throws IOException
    {
        mmOutputStream.write(7);
    }

    public void open_OnClick(View v)
    {
        try
        {
            findBT();
            openBT();
        }
        catch (IOException ex)
        {

        }
        fragment_my_leds.textStatusSetText("Connected");
    }

    public void close_OnClick(View v)
    {
        try
        {
            closeBT();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        fragment_my_leds.textStatusSetText("Disconnected");
    }

    public void bluetooth_OnClick(View v)
    {
        if(!open)
        {
            try
            {
                findBT();
                openBT();
            }
            catch (IOException ex)
            {

            }

            //fragment_solid_colors.buttonBluetoothSetText("Bluetooth Open");
            open = true;
        }
        else
        {
            try
            {
                closeBT();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
           //fragment_solid_colors.buttonBluetoothSetText("Bluetooth Closed");
            open = false;
        }

    }

    void findBT()
    {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter == null)
        {
            // Bluetooth adapter is not available
        }

        if(!mBluetoothAdapter.isEnabled())
        {
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetooth, 0);
        }

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if(pairedDevices.size() > 0)
        {
            for(BluetoothDevice device : pairedDevices)
            {
                if(device.getName().equals("HC-06"))
                {
                    mmDevice = device;
                    break;
                }
            }
        }
    }

    void openBT() throws IOException
    {
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); //Standard SerialPortService ID
        mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
        mmSocket.connect();
        mmOutputStream = mmSocket.getOutputStream();
        mmInputStream = mmSocket.getInputStream();

        beginListenForData();
    }

    void beginListenForData()
    {
        final Handler handler = new Handler();
        final byte delimiter = 10; //This is the ASCII code for a newline character

        stopWorker = false;
        readBufferPosition = 0;
        readBuffer = new byte[1024];
        workerThread = new Thread(new Runnable()
        {
            public void run()
            {
                while(!Thread.currentThread().isInterrupted() && !stopWorker)
                {
                    try
                    {
                        int bytesAvailable = mmInputStream.available();
                        if(bytesAvailable > 0)
                        {
                            byte[] packetBytes = new byte[bytesAvailable];
                            mmInputStream.read(packetBytes);
                            for(int i=0;i<bytesAvailable;i++)
                            {
                                byte b = packetBytes[i];
                                if(b == delimiter)
                                {
                                    byte[] encodedBytes = new byte[readBufferPosition];
                                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                    final String data = new String(encodedBytes, "US-ASCII");
                                    readBufferPosition = 0;

                                    handler.post(new Runnable()
                                    {
                                        public void run()
                                        {
                                        }
                                    });
                                }
                                else
                                {
                                    readBuffer[readBufferPosition++] = b;
                                }
                            }
                        }
                    }
                    catch (IOException ex)
                    {
                        stopWorker = true;
                    }
                }
            }
        });

        workerThread.start();
    }

    void closeBT() throws IOException
    {
        stopWorker = true;
        mmOutputStream.close();
        mmInputStream.close();
        mmSocket.close();
    }

}
