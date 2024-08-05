package com.aceroute.mobile.software.camera;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.aceroute.mobile.software.R;

public class MainActivity extends Activity {
    Button b1;
    ImageView signImage;
 
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main_new);
        b1 = (Button) findViewById(R.id.getSign);
        signImage = (ImageView) findViewById(R.id.imageView1);
    
        b1.setOnClickListener(onButtonClick);
    }
    Button.OnClickListener onButtonClick = new Button.OnClickListener() {
    	 
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            Intent i = new Intent(MainActivity.this, CaptureSignature.class);
            startActivityForResult(i, 0);
        }
    };
 
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
    	   Bitmap bitMapImg = null;
        if (resultCode == 1) {
            Bitmap b = BitmapFactory.decodeByteArray(
                    data.getByteArrayExtra("byteArray"), 0,
                    data.getByteArrayExtra("byteArray").length);
            signImage.setImageBitmap(b);
        }
    }
}
