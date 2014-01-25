package com.AridRayne.DreamStream;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.mcsoxford.rss.RSSFeed;
import org.mcsoxford.rss.RSSItem;
import org.mcsoxford.rss.RSSLoader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Picasso.LoadedFrom;
import com.squareup.picasso.Target;

public class DreamStream implements OnGestureListener, OnDoubleTapListener, OnScaleGestureListener {
//	private ImageView iv;
	private static ArrayList<String> images = new ArrayList<String>();
	private static int repeatTime = 5000;
	private static Handler imageHandler;
	private static int position = 0;
	private boolean random = true;
	private ScaleGestureDetector sgDetector;
	private GestureDetector gDetector;
	//TODO: Set this up to get the splash image from preferences.
	private static String splashImage = "http://fc01.deviantart.net/fs70/f/2010/291/e/d/please_wait_by_naolito-d311p2z.jpg";
	private static boolean showSplash = true;
	private static boolean pause = false;
	private static Context context;
	private boolean isWallpaper = false;
	private newImageCallback callback;
	private static ImageTarget target;
	
	private static DreamStream instance;
	
	private DreamStream() {
		
	}
	
	public static DreamStream getInstance() {
		if (instance == null)
			instance = new DreamStream();
		return instance;
	}

	public void initialize(Context context) {
		DreamStream.context = context;
//		iv = new ImageView(context);
		target = new ImageTarget();
		gDetector = new GestureDetector(context, this);
		sgDetector = new ScaleGestureDetector(context, this);
		imageHandler = new Handler();
		//TODO: Add some code for an initial image?
	}
	
//	public ImageView getImageView() {
//		return iv;
//	}
	
	public void setTarget(ImageTarget target) {
		DreamStream.target = target;
	}
	
	public void start() {
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
	
	public void stop() {
		imageHandler.removeCallbacksAndMessages(null);
		Picasso.with(context).cancelRequest(target);;
	}
	
	static Runnable imageLoader = new Runnable() {
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
	
	public static void incrementPosition() {
		if (++position >= images.size())
			position = 0;
	}
	
	public void decrementPosition() {
		if (--position < 0)
			position = images.size() - 1;
	}
	
	public static void loadImage(String imageUrl) {
		if (imageUrl == null) {
			imageHandler.removeCallbacksAndMessages(null);
			imageHandler.post(imageLoader);
			return;
		}
		if (imageUrl.startsWith("file|")) {
			Picasso.with(context)
			.load(new File(imageUrl))
			.into(target);
		}
		else {
			Picasso.with(context)
			.load(imageUrl)
			.into(target);
		}
	}
	
	public static class ImageTarget implements Target {

		@Override
		public void onBitmapFailed(Drawable errorDrawable) {
			incrementPosition();
			imageHandler.removeCallbacksAndMessages(null);
			imageHandler.post(imageLoader);
		}

		@Override
		public void onBitmapLoaded(Bitmap bitmap, LoadedFrom from) {
			incrementPosition();
			imageHandler.removeCallbacksAndMessages(null);
			if (!pause)
				imageHandler.postDelayed(imageLoader, repeatTime);
//			iv.setImageBitmap(bitmap);
		}

		@Override
		public void onPrepareLoad(Drawable placeHolderDrawable) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	public class ImageCallback implements Callback {

		@Override
		public void onSuccess() {
			incrementPosition();
			imageHandler.removeCallbacksAndMessages(null);
			if (!pause) {
				imageHandler.postDelayed(imageLoader, repeatTime);
				if (callback != null)
					callback.imageLoaded();
			}
		}

		@Override
		public void onError() {
			incrementPosition();
			imageHandler.removeCallbacksAndMessages(null);
			imageHandler.post(imageLoader);
		}
	}
	
	public void setImageLoadedCallback(newImageCallback callback) {
		this.callback = callback;
	}
	
	public void setIsWallpaper(boolean isWallpaper) {
		this.isWallpaper = isWallpaper;
	}
	
	public interface newImageCallback {
		void imageLoaded();
	}

	public boolean touchEvent(MotionEvent event) {
		boolean retVal = sgDetector.onTouchEvent(event);
		return gDetector.onTouchEvent(event) || retVal;
	}
	
	public void togglePlayPause() {
		pause = !pause;
		if (pause)
			imageHandler.removeCallbacksAndMessages(null);
		else
			imageHandler.post(imageLoader);	
	}
	
	public void pause() {
		pause = true;
		imageHandler.removeCallbacksAndMessages(null);
	}
	
	public void play() {
		pause = false;
		imageHandler.post(imageLoader);
	}

	@Override
	public boolean onDown(MotionEvent arg0) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean onFling(MotionEvent start, MotionEvent finish, float xVelocity,
			float yVelocity) {
		if (isWallpaper)
			return false;
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
		if (isWallpaper) {
			incrementPosition();
			imageHandler.removeCallbacksAndMessages(null);
			imageHandler.post(imageLoader);
			return true;
		}
			
		togglePlayPause();
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

	@Override
	public boolean onScale(ScaleGestureDetector detector) {
		if (isWallpaper)
			return false;
		pause();
		System.out.println("scaling");
		
		return true;
	}

	@Override
	public boolean onScaleBegin(ScaleGestureDetector detector) {
		return true;
	}

	@Override
	public void onScaleEnd(ScaleGestureDetector detector) {
	}
	
}
