package com.AridRayne.DreamStream;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.AridRayne.DreamStream.DreamStream.ImageTarget;
import com.squareup.picasso.Picasso.LoadedFrom;

public class MainActivity extends Activity {

	ImageView iv;
	DreamStream dreamStream;
	AppTarget target;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        iv = new ImageView(this);
        setContentView(iv);
        dreamStream = DreamStream.getInstance();
        dreamStream.initialize(this);
        target = new AppTarget();
        dreamStream.setTarget(target);
        dreamStream.start();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    @Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		return dreamStream.touchEvent(event) || super.onTouchEvent(event);
	}

	public class AppTarget extends ImageTarget {

		@Override
		public void onBitmapLoaded(Bitmap bitmap, LoadedFrom from) {
			super.onBitmapLoaded(bitmap, from);
			iv.setImageBitmap(bitmap);
		}
    }
    
}
