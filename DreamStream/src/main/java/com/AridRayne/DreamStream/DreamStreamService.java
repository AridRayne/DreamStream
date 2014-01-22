package com.AridRayne.DreamStream;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

import org.mcsoxford.rss.RSSFeed;
import org.mcsoxford.rss.RSSItem;
import org.mcsoxford.rss.RSSReader;
import org.mcsoxford.rss.RSSReaderException;

import android.os.AsyncTask;
import android.os.Handler;
import android.service.dreams.DreamService;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class DreamStreamService extends DreamService {
	ImageView iv;
	Queue<String> images = new LinkedList<String>();
	Random randomizer;
	int repeatTime = 5000;
	Handler imageHandler;
	int position = 0;
	Boolean random = false;
	RSSReader reader;
	RSSFeed feed;
	
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
		
		ReadFeed rf = new ReadFeed();
		rf.execute("http://backend.deviantart.com/rss.xml?q=boost%3Apopular+in%3Adigitalart+max_age%3A24h&type=deviation");
		
	}

	@Override
	public void onDreamingStarted() {
		super.onDreamingStarted();
		imageLoader.run();
	}

	Runnable imageLoader = new Runnable() {
		@Override
		public void run() {
			loadImage(images.poll());
			imageHandler.postDelayed(imageLoader, repeatTime);
		}
	};
	
	@Override
	public void onDreamingStopped() {
		super.onDreamingStopped();
		imageHandler.removeCallbacks(imageLoader);
	}

	public void loadImage(String imageUrl) {
		if (imageUrl == null)
			return;
		Picasso.with(this)
		.load(imageUrl)
		.centerInside()
		.fit()
		.into(iv);
	}
	
	public class ReadFeed extends AsyncTask<String, Void, RSSFeed> {

		@Override
		protected RSSFeed doInBackground(String... urls) {
			RSSReader reader = new RSSReader();
			try {
				return reader.load(urls[0]);
			} catch (RSSReaderException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(RSSFeed result) {
			feed = result;
			for (RSSItem item : result.getItems()) {
				images.add(item.getMediaContent().getUrl().toString());
			}
			super.onPostExecute(result);
		}
	}
}
