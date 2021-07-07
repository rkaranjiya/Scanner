package com.example.scan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.android.gms.vision.text.Text;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.Result;
import com.google.zxing.qrcode.encoder.QRCode;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class MainActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {


    private ZXingScannerView scannerView;
    private TextView txtResult;
    private String inputValue;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference dbRef = database.getReference();
    DatabaseReference dbRef2 = database.getReference();
    DatabaseReference dbRef3 = database.getReference();
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("QRCodeData");

    static final String ACTION_SCAN = "com.google.zxing.client.android.SCAN";
    TextView data;
    Button ext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scannerView= findViewById(R.id.result);
        txtResult = findViewById(R.id.txt);
        ext = findViewById(R.id.ext);

        dbRef = database.getReference("/QRCodeScanData/data1");
        dbRef2 = database.getReference("/Servo/data");

        Dexter.withActivity(this)
                .withPermission(Manifest.permission.CAMERA)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        scannerView.setResultHandler(MainActivity.this);
                        scannerView.startCamera();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        Toast.makeText(MainActivity.this,"You Must accept this permission",Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                    }
                })
        .check();


        ext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }


    @Override
    protected void onDestroy() {
        scannerView.stopCamera();
        super.onDestroy();

    }

    @Override
    public void handleResult(Result result) {
        txtResult.setText(result.getText());
        inputValue=txtResult.getText().toString();
        dbRef.setValue(inputValue);

        Query query = reference.orderByChild("qrcode").equalTo(inputValue);
        dbRef3 =  database.getReference("/QRCodeData/"+inputValue+"/Attempt");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){

                    String attempt  = dataSnapshot.child(inputValue).child("Attempt").getValue().toString();
                    //Toast.makeText(getApplicationContext(),attempt,Toast.LENGTH_LONG).show();
                    int f =Integer.parseInt(attempt);


                    if (f < 1)
                    {
                        dbRef2.setValue("False");
                    }
                    //Toast.makeText(getApplicationContext(),"True",Toast.LENGTH_LONG).show();
                    else
                    {
                        dbRef2.setValue("True");
                        f--;
                        dbRef3.setValue(f);
                    }


                }
                else{

                    dbRef2.setValue("False");
                    //Toast.makeText(getApplicationContext(),"False",Toast.LENGTH_LONG).show();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        scannerView.resumeCameraPreview(MainActivity.this);
    }
}
