package com.aceroute.mobile.software.fragment;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.aceroute.mobile.software.R;
import com.aceroute.mobile.software.utilities.Utilities;
import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class SimpleScannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView mScannerView;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);

        try
        {
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException e){}
        setContentView(R.layout.simple_scanner_layout);


        ViewGroup contentFrame = (ViewGroup) findViewById(R.id.content_frame);
        mScannerView = new ZXingScannerView(this);
        contentFrame.addView(mScannerView);
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    public void handleResult(final Result rawResult) {
//        Toast.makeText(this, "Contents = " + rawResult.getText() +
//                ", Format = " + rawResult.getBarcodeFormat().toString(), Toast.LENGTH_SHORT).show();

        // Note:
        // * Wait 2 seconds to resume the preview.
        // * On older devices continuously stopping and resuming camera preview can result in freezing the app.
        // * I don't know why this is the case but I don't have the time to figure out.
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
              //  mScannerView.resumeCameraPreview(SimpleScannerActivity.this);

            }
        }, 2000);
        showDialog(rawResult.getText());
    }
    void showDialog(final String barcodedata){
        AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT);
        builder.setTitle(Html.fromHtml("<font color='#00C29D'>Barcode</font>"));
        builder.setMessage(barcodedata);

        builder.setNegativeButton(Html.fromHtml("<font color='#00C29D'>RESCAN</font>"), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();

                mScannerView.resumeCameraPreview(SimpleScannerActivity.this);
               /* Intent data = new Intent();
                data.putExtra("barcode_data",barcodedata);
                setResult(AppCompatActivity.RESULT_OK,data);
                finish();*/
            }
        });

        builder.setPositiveButton(Html.fromHtml("<font color='#00C29D'>USE BARCODE</font>"), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                Intent data = new Intent();
                data.putExtra("barcode_data",barcodedata);
                setResult(AppCompatActivity.RESULT_OK,data);
                finish();
            }
        });


        AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
        Utilities.setDividerTitleColor(alertDialog, 500, this);
        Button button_positive = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        Button button_negtaive = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        Utilities.setDefaultFont_12(button_negtaive);
        Utilities.setDefaultFont_12(button_positive);
    }




}
