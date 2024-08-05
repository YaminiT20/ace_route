package com.aceroute.mobile.software.fragment;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import com.aceroute.mobile.software.R;

public class ImaveViewer extends AppCompatActivity {

    ImageView imageView;
    String uri;
    Intent i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imave_viewer);

        imageView = findViewById(R.id.viewers);
        i = getIntent();
        uri = i.getExtras().getString("imagepath");
        imageView.setImageURI(Uri.parse(uri));
    }
}