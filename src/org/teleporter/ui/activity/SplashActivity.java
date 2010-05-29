package org.teleporter.ui.activity;

import org.teleporter.R;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

/**
 * @author Nicolas Gramlich
 * @since 16:43:49 - 26.05.2010
 */
public class SplashActivity extends Activity {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================
	
	private AnimationDrawable mLogoAnimationDrawable;

	// ===========================================================
	// Constructors
	// ===========================================================

	@Override
	protected void onCreate(Bundle pSavedInstanceState) {
		super.onCreate(pSavedInstanceState);

		this.setContentView(R.layout.splash);

		final ImageView ivSplashLogo = (ImageView)this.findViewById(R.id.iv_splash_logo);

		ivSplashLogo.setBackgroundResource(R.drawable.logo_animation);
		this.mLogoAnimationDrawable = (AnimationDrawable) ivSplashLogo.getBackground();
		
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				SplashActivity.this.startActivity(new Intent(SplashActivity.this, RidesListActivity.class));
				SplashActivity.this.finish();
			}
		}, 3000);
	}

	@Override
	protected void onResume() {
		super.onResume();
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
			    SplashActivity.this.mLogoAnimationDrawable.start();
			}
		}, 100);
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
