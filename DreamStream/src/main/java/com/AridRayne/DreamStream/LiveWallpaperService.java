package com.AridRayne.DreamStream;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.RectF;
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
		private float xOffset, yOffset;
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
			this.height = getDesiredMinimumHeight();
			this.width = getDesiredMinimumWidth();
			matrix.reset();
			dreamStream.start();
//			Canvas canvas = holder.lockCanvas();
//			dreamStream.getViewPager().measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
//			dreamStream.getViewPager().layout(0, 0, width, height);
//			dreamStream.getViewPager().draw(canvas);
//			holder.unlockCanvasAndPost(canvas);
		}

		@Override
		public void onDesiredSizeChanged(int desiredWidth, int desiredHeight) {
			super.onDesiredSizeChanged(desiredWidth, desiredHeight);
			this.width = desiredWidth;
			this.height = desiredHeight;
		}

		@Override
		public void onOffsetsChanged(float xOffset, float yOffset,
				float xOffsetStep, float yOffsetStep, int xPixelOffset,
				int yPixelOffset) {
			super.onOffsetsChanged(xOffset, yOffset, xOffsetStep, yOffsetStep,
					xPixelOffset, yPixelOffset);
			if (!isRunning)
				isRunning = true;
			this.xOffset = xPixelOffset;
			this.yOffset = yPixelOffset;
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
						RectF imageRect = new RectF(0,0, bitmap.getWidth(), bitmap.getHeight());
						RectF viewRect = new RectF(0, 0, width, height);
						matrix.setRectToRect(imageRect, viewRect, Matrix.ScaleToFit.CENTER);
						c.translate(xOffset, yOffset);
						c.drawBitmap(bitmap, matrix, null);
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
