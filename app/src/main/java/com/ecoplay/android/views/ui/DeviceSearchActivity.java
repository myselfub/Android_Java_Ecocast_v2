package com.ecoplay.android.views.ui;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import com.ecoplay.android.R;
import com.ecoplay.android.models.model.DeviceModel;
import com.ecoplay.android.viewmodels.DeviceViewModel;
import com.ecoplay.android.views.adapter.DeviceSearchAdapter;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class DeviceSearchActivity extends AppCompatActivity {
    private static final String TAG = DeviceSearchActivity.class.getSimpleName();
    private final static int BLUETOOTH_REQUEST_CODE = 100;
    private final int PERMISSIONS_REQUEST_CODE = 1;
    private ViewModelProvider.AndroidViewModelFactory viewModelFactory;
    private DeviceViewModel deviceViewModel;
    private DeviceSearchAdapter deviceSearchAdapter;
    private ListView listView;
    private BluetoothAdapter bluetoothAdapter;
    private List<BluetoothDevice> bluetoothDevices;
    private long pressedTime;
    private Toast toast;
    private int selectDevice;
    private final String[] permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_serach);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "블루투스를 지원하지 않는 단말기 입니다.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (!bluetoothAdapter.isEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, BLUETOOTH_REQUEST_CODE);
        }

        listView = (ListView) findViewById(R.id.lv_device_pairable);

        if (viewModelFactory == null) {
            viewModelFactory = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication());
        }

//        deviceViewModel = new DeviceViewModel();
        deviceViewModel = new ViewModelProvider(this, viewModelFactory).get(DeviceViewModel.class);
        deviceSearchAdapter = new DeviceSearchAdapter();
        deviceSearchAdapter.setMutableLiveData(deviceViewModel);
        listView.setAdapter(deviceSearchAdapter);
        bluetoothDevices = new ArrayList<>();

        IntentFilter searchFilter = new IntentFilter();
        searchFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED); // BluetoothAdapter.ACTION_DISCOVERY_STARTED : 블루투스 검색 시작
        searchFilter.addAction(BluetoothDevice.ACTION_FOUND); // BluetoothDevice.ACTION_FOUND : 블루투스 디바이스 찾음
        searchFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED); // BluetoothAdapter.ACTION_DISCOVERY_FINISHED : 블루투스 검색 종료
        searchFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(bluetoothSearchReceiver, searchFilter);
        selectDevice = -1;

        onBluetoothSearch();

        // 검색된 디바이스목록 클릭시 페어링 요청
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BluetoothDevice device = bluetoothDevices.get(position);
                try {
                    // 선택한 디바이스 페어링 요청
                    Method method = device.getClass().getMethod("createBond", (Class[]) null);
                    method.invoke(device, (Object[]) null);
                    selectDevice = position;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }
        unregisterReceiver(bluetoothSearchReceiver);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (pressedTime == 0) {
            toast = Toast.makeText(this, " 한 번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT);
            toast.show();
            pressedTime = System.currentTimeMillis();
        } else {
            int seconds = (int) (System.currentTimeMillis() - pressedTime);
            if (seconds > 2000) {
                toast = Toast.makeText(this, " 한 번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT);
                toast.show();
                pressedTime = 0;
            } else {
                super.onBackPressed();
                if (toast != null) {
                    toast.cancel();
                }
                finish();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case BLUETOOTH_REQUEST_CODE:
                // 블루투스 활성화 승인
                if (resultCode == Activity.RESULT_OK) {
                    onBluetoothSearch();
                }
                // 블루투스 활성화 거절
                else {
                    Toast.makeText(this, "블루투스를 활성화해야 합니다.", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean grantBoolean = true;
                    for (int grantResult : grantResults) {
                        if (grantResult != PackageManager.PERMISSION_GRANTED) {
                            grantBoolean = false;
                            break;
                        }
                    }
                    if (!grantBoolean) {
                        showDialogForPermission("앱을 실행하려면 권한을 허가하셔야합니다.");
                    }
                }
                break;
        }
    }

    private void showDialogForPermission(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("알림");
        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                requestPermissions(permissions, PERMISSIONS_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                finish();
            }
        });
        builder.create().show();
    }

    // 블루투스 검색결과 BroadcastReceiver
    private BroadcastReceiver bluetoothSearchReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                // 블루투스 디바이스 검색 종료
                case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
                    deviceViewModel.getObservableArrayList().clear();
                    bluetoothDevices.clear();
                    Toast.makeText(DeviceSearchActivity.this, "블루투스 검색 시작", Toast.LENGTH_SHORT).show();
                    break;
                // 블루투스 디바이스 찾음
                case BluetoothDevice.ACTION_FOUND:
                    // 검색한 블루투스 디바이스의 객체를 구한다
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    // 데이터 저장
                    boolean contain = false;
                    for (DeviceModel deviceModel : deviceViewModel.getObservableArrayList()) {
                        if (deviceModel.getMacAddress().equals(device.getAddress())) {
                            contain = true;
                        }
                    }
                    if (!contain) {
                        // device.getName() : 블루투스 디바이스의 이름, device.getAddress() : 블루투스 디바이스의 MAC 주소
                        deviceViewModel.setObservableArrayList(device.getName(), device.getAddress());
                        deviceSearchAdapter.notifyDataSetChanged();
                        // 블루투스 디바이스 저장
                        bluetoothDevices.add(device);
                    }
                    break;
                // 블루투스 디바이스 검색 종료
                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                    Toast.makeText(DeviceSearchActivity.this, "블루투스 검색 종료", Toast.LENGTH_SHORT).show();
                    break;
                // 블루투스 디바이스 페어링 상태 변화
                case BluetoothDevice.ACTION_BOND_STATE_CHANGED:
                    if (bluetoothAdapter.isDiscovering()) {
                        bluetoothAdapter.cancelDiscovery();
                    }
                    BluetoothDevice paired = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    paired.createBond();
                    if (paired.getBondState() == BluetoothDevice.BOND_BONDED) {
                        Toast.makeText(DeviceSearchActivity.this, "페어링 성공", Toast.LENGTH_SHORT).show();
                        // 검색된 목록
                        if (selectDevice != -1) {
                            bluetoothDevices.remove(selectDevice);
                            deviceViewModel.getObservableArrayList().remove(selectDevice);
                            deviceSearchAdapter.notifyDataSetChanged();
                            selectDevice = -1;
                            Intent intent1 = new Intent(context, DevicePairedActivity.class);
                            intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent1);
                            finish();
                        }
                    }
                    break;
            }
        }
    };

    // 블루투스 검색
    private void onBluetoothSearch() {
        if (bluetoothAdapter.isDiscovering()) { // 블루투스 검색중인지 여부 확인
            bluetoothAdapter.cancelDiscovery(); // 블루투스 검색 취소
            return;
        }
        boolean hasPermissions = true;
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_DENIED) {
                hasPermissions = false;
                break;
            }
        }
        if (!hasPermissions) {
            Toast.makeText(this, "위치 권한이 필요합니다.", Toast.LENGTH_LONG).show();
            ActivityCompat.requestPermissions(this, permissions, PERMISSIONS_REQUEST_CODE);
            return;
        }
        bluetoothAdapter.startDiscovery(); // 블루투스 검색 시작
    }
}