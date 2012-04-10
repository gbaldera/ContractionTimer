package com.ianhanniballake.contractiontimer.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.ianhanniballake.contractiontimer.BuildConfig;
import com.ianhanniballake.contractiontimer.R;
import com.ianhanniballake.contractiontimer.actionbar.ActionBarFragmentActivity;
import com.ianhanniballake.contractiontimer.analytics.AnalyticsManagerService;

/**
 * Stand alone activity used to view the details of an individual contraction
 */
public class EditActivity extends ActionBarFragmentActivity
{
	@Override
	public void onAnalyticsServiceConnected()
	{
		if (BuildConfig.DEBUG)
			Log.d(getClass().getSimpleName(), "Showing activity");
		AnalyticsManagerService.trackPageView(this);
	}

	@Override
	public void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit);
		if (findViewById(R.id.edit) == null)
		{
			// A null details view means we no longer need this activity
			finish();
			return;
		}
		if (savedInstanceState == null)
			showFragment();
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu)
	{
		if (Intent.ACTION_INSERT.equals(getIntent().getAction()))
			getMenuInflater().inflate(R.menu.activity_add, menu);
		else
			getMenuInflater().inflate(R.menu.activity_edit, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item)
	{
		switch (item.getItemId())
		{
			case android.R.id.home:
				Intent intent;
				if (Intent.ACTION_INSERT.equals(getIntent().getAction()))
				{
					if (BuildConfig.DEBUG)
						Log.d(getClass().getSimpleName(), "Add selected home");
					AnalyticsManagerService.trackEvent(this, "Add", "Home");
					intent = new Intent(this, MainActivity.class);
				}
				else
				{
					if (BuildConfig.DEBUG)
						Log.d(getClass().getSimpleName(), "Edit selected home");
					AnalyticsManagerService.trackEvent(this, "Edit", "Home");
					intent = new Intent(Intent.ACTION_VIEW, getIntent()
							.getData());
				}
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				finish();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onStart()
	{
		super.onStart();
		getActionBarHelper().setDisplayHomeAsUpEnabled(true);
	}

	/**
	 * Creates and shows the fragment associated with the current contraction
	 */
	private void showFragment()
	{
		final EditFragment viewFragment = new EditFragment();
		// Execute a transaction, replacing any existing fragment
		// with this one inside the frame.
		final FragmentTransaction ft = getSupportFragmentManager()
				.beginTransaction();
		ft.replace(R.id.edit, viewFragment);
		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		ft.commit();
	}
}
