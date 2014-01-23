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
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class DreamStreamService extends DreamService implements OnGestureListener {
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
	GestureDetector gDetector;
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		gDetector.onTouchEvent(event);
		return super.dispatchTouchEvent(event);
	}

	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();
		
		setInteractive(true);
		setFullscreen(true);
		iv = new ImageView(this);
		setContentView(iv);
		Picasso.with(this).setDebugging(true);
		randomizer = new Random();
		imageHandler = new Handler();
		gDetector = new GestureDetector(this);
		//TODO: Add some code for an initial image?
	}

	@Override
	public void onDreamingStarted() {
		super.onDreamingStarted();
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
		imageLoader.run();
	}

	Runnable imageLoader = new Runnable() {
		@Override
		public void run() {
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
			.centerInside()
			.fit()
			.into(iv, new ImageCallback());
		}
		else {
			Picasso.with(this)
			.load(imageUrl)
			.centerInside()
			.fit()
			.into(iv, new ImageCallback());
		}
	}
	
	public class ImageCallback implements Callback {

		@Override
		public void onSuccess() {
			incrementPosition();
			imageHandler.removeCallbacksAndMessages(null);
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
			System.out.println("swipe left -> right! " + position);
			imageHandler.removeCallbacksAndMessages(null);
			imageHandler.post(imageLoader);
		}
		else if (start.getRawX() > finish.getRawX()) {//Swipe right -> left
			System.out.println("swipe right -> left! " + position);
			imageHandler.removeCallbacksAndMessages(null);
			imageHandler.post(imageLoader);
		}
		return false;
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

}
