package com.AridRayne.DreamStream;

import android.service.dreams.DreamService;
import android.view.MotionEvent;

public class DreamStreamService extends DreamService {

	DreamStream dreamStream;
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		return super.dispatchTouchEvent(event) || dreamStream.touchEvent(event);
	}

	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();
		
		setInteractive(true);
		setFullscreen(true);
		dreamStream = new DreamStream();
		setContentView(dreamStream.initialize(this));
	}

	@Override
	public void onDreamingStarted() {
		super.onDreamingStarted();
		dreamStream.start();
	}

	@Override
	public void onDreamingStopped() {
		super.onDreamingStopped();
		dreamStream.stop();
	}

}
