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
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;

import com.jfeinstein.jazzyviewpager.JazzyViewPager;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Picasso.LoadedFrom;
import com.squareup.picasso.Target;

public class DreamStream implements OnGestureListener, OnDoubleTapListener, OnLongClickListener, OnPageChangeListener {
//	private ImageView iv;
	private static ArrayList<String> images = new ArrayList<String>();
	private static int imageDelay;
	private static Handler imageHandler;
	private static Handler pageHandler;
	private static int position = 0;
	private boolean shuffle;
	private GestureDetector gDetector;
	//TODO: Set this up to get the splash image from preferences.
//	private static String splashUri;// = "http://fc01.deviantart.net/fs70/f/2010/291/e/d/please_wait_by_naolito-d311p2z.jpg";
//	private static boolean showSplash = true;
	private static boolean pause = false;
	private static Context context;
	private boolean isWallpaper = false;
	private newImageCallback callback;
	private static ImageTarget target;
	private static SharedPreferences preferences;
	private static DreamPagerAdapter pagerAdapter;
	private static JazzyViewPager viewPager;
	
	private static DreamStream instance;
	
	private DreamStream() {
		
	}
	
	public JazzyViewPager getViewPager() {
		return viewPager;
	}

	public static DreamStream getInstance() {
		if (instance == null)
			instance = new DreamStream();
		return instance;
	}

	public void initialize(Context context) {
		DreamStream.context = context;
		target = new ImageTarget();
		gDetector = new GestureDetector(context, this);
//		sgDetector = new ScaleGestureDetector(context, this);
		imageHandler = new Handler();
		pageHandler = new Handler();
		preferences = PreferenceManager.getDefaultSharedPreferences(context);
//		splashUri = preferences.getString("splash_uri", "");
		shuffle = preferences.getBoolean("shuffle", false);
		imageDelay = (int) (Float.valueOf(preferences.getString("image_delay", "5")) * 1000);
		viewPager = new JazzyViewPager(context);
		pagerAdapter = new DreamPagerAdapter();
		pagerAdapter.setViewPager(viewPager);
		pagerAdapter.setContext(context);
		viewPager.setAdapter(pagerAdapter);
		viewPager.setOnPageChangeListener(this);
		pageHandler.removeCallbacksAndMessages(null);
		pageHandler.postDelayed(nextPage, imageDelay);
//		viewPager.setOnLongClickListener(this);
	}
	
	public void setTarget(ImageTarget target) {
		DreamStream.target = target;
	}
	
	public void start() {
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
		if (shuffle)
			Collections.shuffle(images);
//		if (showSplash && !splashUri.isEmpty())
//			images.add(0, splashUri);
		pagerAdapter.setUris(images);
		if (isWallpaper)
			imageLoader.run();
		else
			pageHandler.postDelayed(nextPage, imageDelay);
	}
	
	public void stop() {
		imageHandler.removeCallbacksAndMessages(null);
		Picasso.with(context).cancelRequest(target);;
	}
	
//	public void loadSplash() {
//		loadImage(splashUri);
//	}
	
	static Runnable nextPage = new Runnable() {
		@Override
		public void run() {
			int pageNum = viewPager.getCurrentItem() + 1;
			if (pageNum >= pagerAdapter.getCount())
				pageNum = 0;
			viewPager.setCurrentItem(pageNum, true);
//			pageHandler.removeCallbacksAndMessages(null);
//			pageHandler.postDelayed(nextPage, imageDelay);
		}
	};
	
	static Runnable imageLoader = new Runnable() {
		@Override
		public void run() {
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
		if (imageUrl == null || imageUrl.isEmpty()) {
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
				imageHandler.postDelayed(imageLoader, imageDelay);
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
				imageHandler.postDelayed(imageLoader, imageDelay);
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
		return gDetector.onTouchEvent(event);
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
		System.out.println("DreamStream.onLongPress()");
		if (!isWallpaper)
			togglePlayPause();
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
		return false;
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

//	@Override
//	public boolean onScale(ScaleGestureDetector detector) {
//		return true;
//	}
//
//	@Override
//	public boolean onScaleBegin(ScaleGestureDetector detector) {
//		return true;
//	}
//
//	@Override
//	public void onScaleEnd(ScaleGestureDetector detector) {
//		if (isWallpaper)
//			return;
//		pause();
//		System.out.println("scaling");
//	}

	@Override
	public boolean onLongClick(View v) {
		System.out.println("DreamStream.onLongClick()");
		if (!isWallpaper) {
			togglePlayPause();
			return true;
		}
		return false;
	}

	@Override
	public void onPageScrollStateChanged(int state) {
		// TODO Auto-generated method stub
		pageHandler.removeCallbacksAndMessages(null);
		if (state == ViewPager.SCROLL_STATE_IDLE) {
			if (!pause)
				pageHandler.postDelayed(nextPage, imageDelay);
			else
				pageHandler.removeCallbacksAndMessages(null);
		}
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onPageSelected(int arg0) {
	}

}
