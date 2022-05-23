package com.vuzix.blade.devkit.sensors_sample;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import com.vuzix.hud.actionmenu.ActionMenuActivity;

import java.io.File;
import java.util.List;
import android.os.StatFs;


import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

import java.io.BufferedReader;

import java.io.FileReader;


/**
 * Main Activity that extend ActionMenuActivity.
 * This main class provide the basic information acquiring data from the device sensors.
 * For more information please reference:
 * https://developer.android.com/guide/topics/sensors/sensors_overview
 * Used Android API Classes:
 * https://developer.android.com/reference/android/hardware/SensorManager
 * https://developer.android.com/reference/android/hardware/SensorEvent
 * https://developer.android.com/reference/android/hardware/SensorEventListener
 */
public class MainActivity extends ActionMenuActivity implements SensorEventListener {

    private final String TAG = "VuzixBDK-Sensors_Sample";
    private SensorManager sensorManager;
    private File samplexyzFile;
    private EditText textArea;
    float accelx,accely,accelz;
    float magnex,magney,magnez;
    float gyrosx,gyrosy,gyrosz;
    //////
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textArea = (EditText)findViewById(R.id.editText);       ///////
        /*

        The SensorManager provides information about on device sensors.

        Implementing the MySensorEventListener and overriding the MySensorEventListener.onSensorChanged method, updates to sensors and sensor values can be handled.

        For more information on the SensorManager, MySensorEventListener, Sensor, or SensorEvent classes, please see https://developer.android.com/reference/android/hardware/SensorManager

         */
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        samplexyzFile = new File(Environment.getExternalStorageDirectory() + "/samplexyz-file.csv");
        listDevices();
    }


    //Registration of Event Listeners should be done here for performance.
    @Override
    protected void onResume() {
        super.onResume();
        logStorageInfo();                       ///////

        // register this class as a listener for the gyroscope sensor
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
                SensorManager.SENSOR_DELAY_NORMAL);

        // register this class as a listener for the gyroscope sensor
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);


        // register this class as a listener for the gyroscope sensor
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_NORMAL);

        // register this class as a listener for the gyroscope sensor
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT),
                SensorManager.SENSOR_DELAY_NORMAL);

        // register this class as a listener for the gyroscope sensor
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE),
                SensorManager.SENSOR_DELAY_NORMAL);

        if (isExternalStorageWritable()) {
            textArea.append("Attempting to Write to storage \n");
            this.writeFileToExternalStorage();
        } else {
            Log.e(TAG, "Cannot write to external storage");
            textArea.setText("");
            textArea.append("ERROR: \n");
            textArea.append("Cannot write to external storage");
        }
        // It's recommended to make sure the external storage is available and can be read from before reading from it.
        if (isExternalStorageReadable()) {

            textArea.append("Attempting to READ from storage \n");
            this.readFileFromExternalStorage();
        }
        else {
            Log.e(TAG, "Cannot read from external storage");
            textArea.setText("");
            textArea.append("ERROR: \n");
            textArea.append("Cannot read from external storage");

        }

    }
    private void logStorageInfo() {
        Log.d(TAG, "Location: " + Environment.getExternalStorageDirectory());
        Log.d(TAG, "State: " + Environment.getExternalStorageState());
        Log.d(TAG, "Removable: " + Environment.isExternalStorageRemovable(samplexyzFile));
        Log.d(TAG, "Emulated: " + Environment.isExternalStorageEmulated(samplexyzFile));
        Log.d(TAG, "Path: " + Environment.getExternalStorageDirectory().getAbsolutePath());
        Log.d(TAG, "SD mount state: " + isSdMounted());
        Log.d(TAG, "Internal Space: " + getAvailableSpaceInMBInternal() + " MB");
        if (isSdMounted()) {
            Log.d(TAG, "SD card free space: " + getAvailableSpaceInMbSD() + " MB");
        }

        textArea.append("Location: " + Environment.getExternalStorageDirectory() + "\n");
        //textArea.append("State: " + Environment.getExternalStorageState() + "\n");
        //textArea.append("Removable: " + Environment.isExternalStorageRemovable(samplexyzFile)  + "\n");
        //textArea.append("Emulated: " + Environment.isExternalStorageEmulated(samplexyzFile)  + "\n");
        textArea.append("Internal Space: " + getAvailableSpaceInMBInternal() + " MB"  + "\n");
        textArea.append("SD card mounted: " +  isSdMounted() + "\n");
        if (isSdMounted()) {
            textArea.append("SD card free space: " + getAvailableSpaceInMbSD() + " MB" + "\n");
        }
    }
    public static long getAvailableSpaceInMBInternal(){
        final long SIZE_KB = 1024L;
        final long SIZE_MB = SIZE_KB * SIZE_KB;
        long availableSpace = -1L;
        StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
        availableSpace = stat.getAvailableBlocksLong() * stat.getBlockSizeLong();
        return availableSpace/SIZE_MB;
    }
    public static long getAvailableSpaceInMbSD(){
        final long SIZE_KB = 1024L;
        final long SIZE_MB = SIZE_KB * SIZE_KB;
        long availableSpace = -1L;
        StatFs stat = new StatFs(Environment.getRootDirectory().getParent() + "/storage/external/");
        availableSpace = stat.getAvailableBlocksLong() * stat.getBlockSizeLong();
        return availableSpace/SIZE_MB;
    }
    private boolean isSdMounted(){
        File file = new File("/storage/external/");
        //hardcoded path to external storage as getExternalStorageDirectory() returns emulated storage.
        return android.os.Environment.getExternalStorageState(file).equals(android.os.Environment.MEDIA_MOUNTED) &&
                android.os.Environment.isExternalStorageRemovable(file);
    }
    private boolean isExternalStorageWritable() {

        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());

    }
    private boolean isExternalStorageReadable() {

        String state = Environment.getExternalStorageState();

        return (Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state));

    }
    private void writeFileToExternalStorage() {

        try {

            samplexyzFile.createNewFile();

            FileOutputStream outputStream = new FileOutputStream(samplexyzFile);
            OutputStreamWriter streamWriter = new OutputStreamWriter(outputStream);
            streamWriter.append("VUZIX BLADE a ");
            streamWriter.append("test");
            streamWriter.append((char) accelx);
            streamWriter.append((char) accely);
            streamWriter.append((char) accelz);

            streamWriter.close();

            outputStream.flush();
            outputStream.close();

        } catch (Exception ex) {
            Log.e(TAG, "Error writing to file: " + ex.toString());
            textArea.setText("");
            textArea.append("ERROR: \n");
            textArea.append("Error writing to file: " + ex.toString());
        }
    }
    private void sensoryazdir(){
        try {

            FileOutputStream outputStream = new FileOutputStream(samplexyzFile);
            OutputStreamWriter streamWriter = new OutputStreamWriter(outputStream);
            streamWriter.append("VUZIX BLADE a ");
            streamWriter.append("test");
            streamWriter.append((char) accelx);
            streamWriter.append((char) accely);
            streamWriter.append((char) accelz);

            streamWriter.close();
            outputStream.flush();
            outputStream.close();

        } catch (Exception ex) {
            Log.e(TAG, "Error writing to file: " + ex.toString());
            textArea.setText("");
            textArea.append("ERROR: \n");
            textArea.append("Error writing to file: " + ex.toString());
        }
    }


    //Remember to always remove the listeners that you don't need to reduce leakage and battery usage
    @Override
    protected void onPause() {
        super.onPause();
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    /**
     * Simply get a list of sensor from the SensorManager and loop over the list, logging basic information about each sensor
     */
    private void listDevices() {

        List<Sensor> sensorList = sensorManager.getSensorList(Sensor.TYPE_ALL);


        for (Sensor sensor : sensorList) {
            Log.d(TAG, "====================================================================================================");
            Log.d(TAG, "Name: \t\t\t\t" + sensor.getName());
            Log.d(TAG, "Vendor: \t\t\t" + sensor.getVendor());
            Log.d(TAG, "Type: \t\t\t\t" + sensor.getStringType());
            Log.d(TAG, "Maximum Range: \t\t" + Float.toString(sensor.getMaximumRange()));
            Log.d(TAG, "Resolution: \t\t" + Float.toString(sensor.getResolution()));
            Log.d(TAG, "Power: \t\t\t\t" + Float.toString(sensor.getPower()));
            Log.d(TAG, "Minimum Display: \t" + Integer.toString(sensor.getMinDelay()));
        }

    }

    /*

    When information about the gyroscope changes, log the new values

     */
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        Log.d(TAG, sensorEvent.sensor.toString());

        switch (sensorEvent.sensor.getStringType()){

            case Sensor.STRING_TYPE_ACCELEROMETER:
                ((TextView)findViewById(R.id.sensor_accel_data_textView)).setText(String.valueOf(sensorEvent.values[0]));
                Log.d(TAG, "Acceleration minus Gx on the x-axis: " + sensorEvent.values[0]);
                accelx=sensorEvent.values[0];

                if (sensorEvent.values.length > 1)
                {
                    Log.d(TAG, "Acceleration minus Gy on the y-axis: " + sensorEvent.values[1]);
                    Log.d(TAG, "Acceleration minus Gz on the z-axis: " + sensorEvent.values[2]);
                    ((TextView)findViewById(R.id.sensor_accel_data_textView2)).setText(String.valueOf(sensorEvent.values[1]));
                    ((TextView)findViewById(R.id.sensor_accel_data_textView3)).setText(String.valueOf(sensorEvent.values[2]));
                    accely=sensorEvent.values[1];
                    accelz=sensorEvent.values[2];
                }
                break;
            case Sensor.STRING_TYPE_MAGNETIC_FIELD:
                ((TextView)findViewById(R.id.sensor_mag_data_textView)).setText(String.valueOf(sensorEvent.values[0]));
                Log.d(TAG, "Magnetometer Data: " + sensorEvent.values[0]);
                magnex=sensorEvent.values[0];

                if (sensorEvent.values.length > 1)
                {
                    Log.d(TAG, "Magnetometer Data2: " + sensorEvent.values[1]);
                    Log.d(TAG, "Magnetometer Data:3 " + sensorEvent.values[2]);
                    ((TextView)findViewById(R.id.sensor_mag_data_textView2)).setText(String.valueOf(sensorEvent.values[1]));
                    ((TextView)findViewById(R.id.sensor_mag_data_textView3)).setText(String.valueOf(sensorEvent.values[2]));
                    magney=sensorEvent.values[1];
                    magnez=sensorEvent.values[2];
                }
                break;
            case Sensor.STRING_TYPE_GYROSCOPE:
                ((TextView)findViewById(R.id.sensor_gyro_data_textView)).setText(String.valueOf(sensorEvent.values[0]));
                Log.d(TAG, "GYROSCOPE minus Gx on the x-axis: " + sensorEvent.values[0]);
                gyrosx=sensorEvent.values[0];
                if (sensorEvent.values.length > 1)
                {
                    Log.d(TAG, "GYROSCOPE minus Gy on the y-axis: " + sensorEvent.values[1]);
                    Log.d(TAG, "GYROSCOPE minus Gz on the z-axis: " + sensorEvent.values[2]);
                    ((TextView)findViewById(R.id.sensor_gyro_data_textView2)).setText(String.valueOf(sensorEvent.values[1]));
                    ((TextView)findViewById(R.id.sensor_gyro_data_textView3)).setText(String.valueOf(sensorEvent.values[2]));
                    gyrosx=sensorEvent.values[1];
                    gyrosx=sensorEvent.values[2];
                }
                break;
            case Sensor.STRING_TYPE_LIGHT:
                ((TextView)findViewById(R.id.sensor_light_data_textView)).setText(String.valueOf(sensorEvent.values[0]));
                Log.d(TAG, "Ambient Light Data: " + sensorEvent.values[0]);

                break;
            case Sensor.STRING_TYPE_PRESSURE:
                ((TextView)findViewById(R.id.sensor_press_data_textView)).setText(String.valueOf(sensorEvent.values[0]));
                Log.d(TAG, "Pressure/Barometer Data: " + sensorEvent.values[0]);

                break;

        }


        sensoryazdir();
    }

    /*

    This is a simple example of logging accuracy changes to a sensor

     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        Log.d(TAG, sensor.toString());
    }
    private void readFileFromExternalStorage() {

        String fileContent = "";

        if (samplexyzFile.exists()) {

            BufferedReader reader;

            try {

                reader = new BufferedReader(new FileReader(samplexyzFile));

                String line;

                while ((line = reader.readLine()) != null) {

                    fileContent += line;

                }

                reader.close();

            } catch (Exception ex) {

                Log.e(TAG, "Error reading from file: " + ex.toString());
                textArea.setText("");
                textArea.append("ERROR: \n");
                textArea.append("Error reading from file: " + ex.toString());
            }
        }

        Log.d(TAG, "File contents: " + fileContent);
        textArea.append("File contents: " + fileContent + "\n");

    }

}
