package com.AridRayne.DreamStream;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.service.wallpaper.WallpaperService;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

import com.AridRayne.DreamStream.DreamStream.ImageTarget;
import com.squareup.picasso.Picasso.LoadedFrom;

public class LiveWallpaperService extends WallpaperService {

	private DreamStream dreamStream;

	@Override
	public Engine onCreateEngine() {
		return new WallpaperEngine();
	}

	@Override
	public void onCreate() {
		super.onCreate();
		dreamStream = DreamStream.getInstance();
		dreamStream.initialize(this);
		dreamStream.setIsWallpaper(true);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		dreamStream.stop();
	}
	
	public class WallpaperEngine extends Engine {
		
		@Override
		public void onTouchEvent(MotionEvent event) {
			dreamStream.touchEvent(event);
			super.onTouchEvent(event);
		}

		private int height, width;
		private int offset = 0;
		private Matrix matrix;
		private WallpaperTarget target;
		private boolean isRunning = false;
		
		@Override
		public void onCreate(SurfaceHolder surfaceHolder) {
			super.onCreate(surfaceHolder);
			target = new WallpaperTarget();
			dreamStream.setTarget(target);
			matrix = new Matrix();
		}

		@Override
		public void onSurfaceChanged(SurfaceHolder holder, int format,
				int width, int height) {
			super.onSurfaceChanged(holder, format, width, height);
			this.height = height;
			this.width = width;
			matrix.reset();
			dreamStream.start();
		}

		@Override
		public void onOffsetsChanged(float xOffset, float yOffset,
				float xOffsetStep, float yOffsetStep, int xPixelOffset,
				int yPixelOffset) {
			super.onOffsetsChanged(xOffset, yOffset, xOffsetStep, yOffsetStep,
					xPixelOffset, yPixelOffset);
			if (!isRunning)
				isRunning = true;
			System.out.println(xOffset + ", " + xOffsetStep + ", " + xPixelOffset);
			offset = xPixelOffset;
			if (this.isVisible())
				target.draw();
		}

		@Override
		public void onVisibilityChanged(boolean visible) {
			super.onVisibilityChanged(visible);
			if (visible && isRunning) {
				dreamStream.start();
			}
			else
				dreamStream.stop();
		}

		public class WallpaperTarget extends ImageTarget {

			Bitmap bitmap;
			
			@Override
			public void onBitmapLoaded(Bitmap bitmap, LoadedFrom from) {
				super.onBitmapLoaded(bitmap, from);
				this.bitmap = bitmap;
				draw();
			}
			
			public void draw() {
				if (this.bitmap == null)
					return;
				final SurfaceHolder holder = getSurfaceHolder();
				Canvas c = null;
				
				try {
					c = holder.lockCanvas();
					if (c != null) {
						c.drawColor(Color.BLACK);
						int centerX = (width - bitmap.getWidth()) / 2;
						int centerY = (height - bitmap.getHeight()) / 2;
//						c.translate(offset, 0.0f);
//						c.drawBitmap(bitmap, matrix, null);
						c.drawBitmap(bitmap, offset, centerY, null);
					}
				}
				finally {
					if (c != null)
						holder.unlockCanvasAndPost(c);
				}				
			}
		}

	}

}
