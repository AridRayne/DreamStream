package com.AridRayne.DreamStream;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.mcsoxford.rss.RSSFeed;
import org.mcsoxford.rss.RSSItem;
import org.mcsoxford.rss.RSSLoader;
import org.mcsoxford.rss.RSSReader;

import android.os.Handler;
import android.service.dreams.DreamService;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class DreamStreamService extends DreamService implements OnGestureListener, OnDoubleTapListener {
	ImageView iv;
//	Queue<String> images = new LinkedList<String>();
	ArrayList<String> images = new ArrayList<String>();
	Random randomizer;
	int repeatTime = 5000;
	Handler imageHandler;
	int position = 0;
	Boolean random = true;
	RSSReader reader;
	RSSFeed feed;
	ScaleGestureDetector sgDetector;
	GestureDetector gDetector;
	String splashImage = "http://fc01.deviantart.net/fs70/f/2010/291/e/d/please_wait_by_naolito-d311p2z.jpg";
	Boolean showSplash = true;
	Boolean pause = false;
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		Boolean retVal;
//		Boolean retVal = sgDetector.onTouchEvent(event);
		retVal = gDetector.onTouchEvent(event);// || retVal;
		return super.dispatchTouchEvent(event) || retVal;
	}

	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();
		
		setInteractive(true);
		setFullscreen(true);
		iv = new ImageView(this);
		gDetector = new GestureDetector(this, this);
//		sgDetector = new ScaleGestureDetector(this, this);
//		iv.setScaleType(ImageView.ScaleType.MATRIX);
		setContentView(iv);
		Picasso.with(this).setDebugging(true);
		randomizer = new Random();
		imageHandler = new Handler();
		//TODO: Add some code for an initial image?
	}

	@Override
	public void onDreamingStarted() {
		super.onDreamingStarted();
		imageLoader.run();
		RSSLoader loader = RSSLoader.fifo();
		Future<RSSFeed> future;
		RSSFeed feed;
		String[] uris = { "http://backend.deviantart.com/rss.xml?q=boost%3Apopular+in%3Adigitalart+max_age%3A24h&type=deviation" };
		for (String uri : uris) {
			loader.load(uri);
		}
		for (int i = 0; i < uris.length; i++) {
			try {
				future = loader.take();
				feed = future.get();
				for (RSSItem item : feed.getItems()) {
					images.add(item.getMediaContent().getUrl().toString());
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (random)
			Collections.shuffle(images);
	}

	Runnable imageLoader = new Runnable() {
		@Override
		public void run() {
			if (showSplash) {
				showSplash = false;
				loadImage(splashImage);
			}
			else
				loadImage(images.get(position));
		}
	};
	
	public void incrementPosition() {
		if (++position >= images.size())
			position = 0;
	}
	
	public void decrementPosition() {
		if (--position < 0)
			position = images.size() - 1;
	}
	
	@Override
	public void onDreamingStopped() {
		super.onDreamingStopped();
		imageHandler.removeCallbacksAndMessages(imageLoader);
	}

	public void loadImage(String imageUrl) {
		if (imageUrl == null) {
			imageHandler.removeCallbacksAndMessages(null);
			imageHandler.post(imageLoader);
			return;
		}
		if (imageUrl.startsWith("file|")) {
			Picasso.with(this)
			.load(new File(imageUrl))
//			.centerInside()
//			.fit()
			.into(iv, new ImageCallback());
		}
		else {
			Picasso.with(this)
			.load(imageUrl)
//			.centerInside()
//			.fit()
			.into(iv, new ImageCallback());
		}
	}
	
	public class ImageCallback implements Callback {

		@Override
		public void onSuccess() {
			incrementPosition();
			imageHandler.removeCallbacksAndMessages(null);
			if (!pause)
				imageHandler.postDelayed(imageLoader, repeatTime);
		}

		@Override
		public void onError() {
			incrementPosition();
			imageHandler.removeCallbacksAndMessages(null);
			imageHandler.post(imageLoader);
		}
	}

	@Override
	public boolean onDown(MotionEvent arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onFling(MotionEvent start, MotionEvent finish, float xVelocity,
			float yVelocity) {
		if (start.getRawX() < finish.getRawX()) {//Swipe left -> right
			decrementPosition();
			decrementPosition();
			imageHandler.removeCallbacksAndMessages(null);
			imageHandler.post(imageLoader);
		}
		else if (start.getRawX() > finish.getRawX()) {//Swipe right -> left
			imageHandler.removeCallbacksAndMessages(null);
			imageHandler.post(imageLoader);
		}
		return true;
	}

	@Override
	public void onLongPress(MotionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent arg0) {
		// TODO Auto-generated method stub
		return false;
	}
	

	@Override
	public boolean onDoubleTap(MotionEvent e) {
		pause = !pause;
		if (pause)
			imageHandler.removeCallbacksAndMessages(null);
		else
			imageHandler.post(imageLoader);			
		return true;
	}

	@Override
	public boolean onDoubleTapEvent(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onSingleTapConfirmed(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

}
