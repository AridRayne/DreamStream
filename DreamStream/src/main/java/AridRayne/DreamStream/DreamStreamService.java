package AridRayne.DreamStream;

import java.util.Random;

import android.os.Handler;
import android.service.dreams.DreamService;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class DreamStreamService extends DreamService {
	ImageView iv;
	String images[] = { "http://www.simonebeautytherapy.co.za/wp-content/uploads/2013/09/placeholder2.jpg",
						"http://terryshoemaker.files.wordpress.com/2013/03/placeholder1.jpg",
						"http://www.zwaldtransport.com/images/placeholders/placeholder1.jpg",
						"http://taimapedia.org/images/5/5f/Placeholder.jpg",
						"http://wp.tx.ncsu.edu/fashioning-health/wp-content/uploads/2012/02/placeholder.jpg"
	};
	Random randomizer;
	int repeatTime = 5000;
	Handler imageHandler;
	int position = 0;
	Boolean random = false;
	
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
		imageLoader.run();
	}

	Runnable imageLoader = new Runnable() {
		@Override
		public void run() {
			if (random)
				position = randomizer.nextInt(images.length);
			loadImage(images[position]);
			position++;
			if (position >= images.length)
				position = 0;
			imageHandler.postDelayed(imageLoader, repeatTime);
		}
	};
	
	@Override
	public void onDreamingStopped() {
		super.onDreamingStopped();
		imageHandler.removeCallbacks(imageLoader);
	}

	public void loadImage(String imageUrl) {
		Picasso.with(this)
		.load(imageUrl)
		.centerInside()
		.fit()
		.into(iv);
	}
}
