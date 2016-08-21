package com.gattaca.bitalinoecgchartwithlibrary;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;
import android.widget.Toast;

import com.bitalino.comm.BITalinoDevice;
import com.bitalino.comm.BITalinoException;
import com.bitalino.comm.BITalinoFrame;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public class BitalinoUniversal {

    private static final int CHANNELS_BITRATE[] = {10, 10, 10, 10, 6, 6};

    private static final String TAG = "BitalinoConnector";
    private static final UUID MY_UUID = UUID
            .fromString("00001101-0000-1000-8000-00805F9B34FB");

    private MonitorActivity mActivity;
    private BITalinoDevice bitalino;
    private BluetoothSocket sock = null;
    private static final int SAMPLE_RATE = 100;
    private final int channel;
    public AtomicBoolean isConnected;

    BitalinoUniversal (MonitorActivity mainActivity, int Channel) {
        mActivity = mainActivity;
        channel = Channel;
        isConnected = new AtomicBoolean(false);
    }

    SimpleECG get() {
        SimpleECG currentECG = null;
        try {
            BITalinoFrame currentFrame = (bitalino.read(1)[0]);
            currentECG = new SimpleECG(currentFrame.getAnalog(channel), CHANNELS_BITRATE[channel]);
        } catch (BITalinoException e) {}
        finally {
            return currentECG;
        }
    }

    Thread start() {
        Thread currentThread = new Thread(new BitalinoConnector());
        currentThread.start();
        return currentThread;
    }

    class BitalinoConnector implements Runnable {
        private BluetoothDevice dev = null;

        @Override
        public void run() {
            try {
                final String remoteDevice = "20:15:10:26:64:45";
                final BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
                dev = btAdapter.getRemoteDevice(remoteDevice);
                btAdapter.cancelDiscovery();
                sock = dev.createRfcommSocketToServiceRecord(MY_UUID);
                sock.connect();
                isConnected.set(true);
                bitalino = new BITalinoDevice(SAMPLE_RATE, new int[]{0, 1, 2, 3, 4, 5});
                publishProgress("Connecting to BITalino [" + remoteDevice + "]..");
                bitalino.open(sock.getInputStream(), sock.getOutputStream());
                publishProgress("Connected.");
                publishProgress("Version: " + bitalino.version());
                bitalino.start();
//                setStart();
            } catch (IOException e) {
                Log.e(TAG, "Socket can't connect!!!", e);
                isConnected.set(false);
            } catch (BITalinoException bit) {
                Log.e(TAG, "BITalino exception!!!", bit);
            }
        }

        class MakeToast implements Runnable {
            String message;

            MakeToast(String s) {
                message = s;
            }

            @Override
            public void run() {
                Toast.makeText(mActivity, message, Toast.LENGTH_SHORT).show();
            }
        }

        protected void publishProgress(String message) {
            mActivity.runOnUiThread(new MakeToast(message));
        }

    }

    void stop() {
        try {
            bitalino.stop();
            sock.close();
        } catch (Exception e) {
            Log.e(TAG, "There was an error in stop", e);
        }
    }
}