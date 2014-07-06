package br.dc.ufscar.lince.itvcontent;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import br.dc.ufscar.lince.itvcontent.config.iTVConfig;
import br.dc.ufscar.lince.itvcontent.timer.TimerListener;
import br.dc.ufscar.lince.itvcontent.timer.TimerManager;

public class MainActivity extends ActionBarActivity implements TimerListener {

	int time = 20;
	Handler handler;
	TimerManager timerManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
				
		handler = new Handler();
		timerManager = new TimerManager(time, handler, this);
		
		TextView versionTextView = (TextView) findViewById(R.id.version);
		versionTextView.setText("Versão: " + iTVConfig.VERSION);
		
		if (!iTVConfig.DEBUG_MODE){
			TextView timeTextView = (TextView) findViewById(R.id.timer_label);
			timeTextView.setVisibility(View.GONE);
		}
	
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    // Inflate the menu items for use in the action bar
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main, menu);
	    return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.action_config:
	    		Intent intent = new Intent(this, SettingsActivity.class);
	    		startActivity(intent);
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	public void nextTouched(View view) {
		Intent intent = new Intent(this, SyncActivity.class);
		startActivity(intent);
	}

	@Override
	public void timeTick(long time) {
		// TODO Auto-generated method stub
		TextView tv1 = (TextView) findViewById(R.id.timer_label);
		tv1.setText(""+time);
	}

	@Override
	public void timeEvent(long time) {
		// TODO Auto-generated method stub
		
	}
	
	

}
