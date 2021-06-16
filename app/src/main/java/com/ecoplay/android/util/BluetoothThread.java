package com.ecoplay.android.util;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

public class BluetoothThread extends Thread {
    private static final String TAG = BluetoothThread.class.getSimpleName();
    private Context context;
    private String macAddress;
    private UUID BT_UUID;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice bluetoothDevice;
    private DataOutputStream dataOutputStream;
    private BluetoothSocket bluetoothSocket;

    private BluetoothThread() {
    }

    public BluetoothThread(Context context, String macAddress) {
        this.context = context;
        this.macAddress = macAddress;
        this.BT_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    @Override
    public void run() {
        // 소켓생성하기 위해 Blutooth 장치 객체 얻어오기
        bluetoothDevice = bluetoothAdapter.getRemoteDevice(macAddress);
        // 디바이스를 통해 소켓연결
        try {
            bluetoothSocket = bluetoothDevice.createInsecureRfcommSocketToServiceRecord(BT_UUID);
            bluetoothSocket.connect(); // 연결 시도

            // 만약 연결이 성공되었다고 메세지
            Toast.makeText(context, "연결 성공", Toast.LENGTH_SHORT);
            dataOutputStream = new DataOutputStream(bluetoothSocket.getOutputStream());
        } catch (IOException e) {
            Toast.makeText(context, "연결 중 오류 발생", Toast.LENGTH_SHORT);
            try {
                if (dataOutputStream != null) {
                    dataOutputStream.close();
                }
            } catch (IOException ioe) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void interrupt() {
        if (dataOutputStream != null) {
            try {
                dataOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        super.interrupt();
    }

    public boolean isConnected() {
        return bluetoothSocket.isConnected();
    }

    public void sendMessage(String message) {
        if (bluetoothSocket.isConnected()) {
            if (dataOutputStream != null) {
                if (message.trim().length() > 0) {
                    try {
                        dataOutputStream.writeUTF(message);
                        dataOutputStream.flush();
                    } catch (IOException e) {
                        try {
                            if (dataOutputStream != null) {
                                dataOutputStream.close();
                            }
                        } catch (IOException ioe) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
}
