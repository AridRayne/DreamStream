package com.AridRayne.DreamStream;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.mcsoxford.rss.RSSFeed;
import org.mcsoxford.rss.RSSItem;
import org.mcsoxford.rss.RSSLoader;
import org.mcsoxford.rss.RSSReader;

import android.os.Handler;
import android.service.dreams.DreamService;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class DreamStreamService extends DreamService {
	ImageView iv;
//	Queue<String> images = new LinkedList<String>();
	ArrayList<String> images = new ArrayList<String>();
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
		imageLoader.run();
	}

	Runnable imageLoader = new Runnable() {
		@Override
		public void run() {
			if (random)
				loadImage(images.get(randomizer.nextInt(images.size())));
			else {
				loadImage(images.get(position));
				DreamStreamService.this.position++;
				if (DreamStreamService.this.position >= images.size())
					DreamStreamService.this.position = 0;
				System.out.println(DreamStreamService.this.position);
			}
		}
	};
	
	@Override
	public void onDreamingStopped() {
		super.onDreamingStopped();
		imageHandler.removeCallbacks(imageLoader);
	}

	public void loadImage(String imageUrl) {
		if (imageUrl == null) {
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
			imageHandler.postDelayed(imageLoader, repeatTime);
		}

		@Override
		public void onError() {
			imageHandler.post(imageLoader);
		}
	}
	
//	public class ReadFeed extends AsyncTask<String, Void, RSSFeed> {
//
//		@Override
//		protected RSSFeed doInBackground(String... urls) {
//			RSSReader reader = new RSSReader();
//			try {
//				return reader.load(urls[0]);
//			} catch (RSSReaderException e) {
//				e.printStackTrace();
//			}
//			return null;
//		}
//
//		@Override
//		protected void onPostExecute(RSSFeed result) {
//			feed = result;
//			for (RSSItem item : result.getItems()) {
//				images.add(item.getMediaContent().getUrl().toString());
//			}
//			super.onPostExecute(result);
//		}
//	}
}
