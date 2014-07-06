package br.dc.ufscar.lince.itvcontent;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import br.dc.ufscar.lince.itvcontent.config.iTVConfig;

public class SettingsActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);

		// Restore preferences
		SharedPreferences settings = getSharedPreferences(iTVConfig.PREFERENCE_FILE, 0);
		
		String host = settings.getString(iTVConfig.PREF_HOST, iTVConfig.MESSAGING_DEFAULT_HOST);		
		EditText hostEdit = (EditText) findViewById(R.id.settings_host);
		hostEdit.setText(host);
		
		String port = settings.getString(iTVConfig.PREF_PORT, iTVConfig.MESSAGING_DEFAULT_PORT);		
		EditText portEdit = (EditText) findViewById(R.id.settings_port);
		portEdit.setText(port);

		String topic = settings.getString(iTVConfig.PREF_TOPIC, iTVConfig.MESSAGING_DEFAULT_TOPIC);		
		EditText topicEdit = (EditText) findViewById(R.id.settings_topic);
		topicEdit.setText(topic);

		String userName = settings.getString(iTVConfig.PREF_USER_NAME, iTVConfig.MESSAGING_DEFAULT_USER);
		EditText userNameEdit = (EditText) findViewById(R.id.settings_user_name);
		userNameEdit.setText(userName);

		String password = settings.getString(iTVConfig.PREF_PWD, iTVConfig.MESSAGING_DEFAULT_PWD);
		EditText passwordEdit = (EditText) findViewById(R.id.settings_pwd);
		passwordEdit.setText(password);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.settings, menu);
		return true;
	}
	
	public void saveSettings(View view) {
		
		// Restore preferences
		SharedPreferences settings = getSharedPreferences(iTVConfig.PREFERENCE_FILE, 0);
		SharedPreferences.Editor editor = settings.edit();
	    
		EditText hostEdit = (EditText) findViewById(R.id.settings_host);
		editor.putString(iTVConfig.PREF_HOST, hostEdit.getText().toString());
		
		EditText portEdit = (EditText) findViewById(R.id.settings_port);
		editor.putString(iTVConfig.PREF_PORT, portEdit.getText().toString());
				
		EditText topicEdit = (EditText) findViewById(R.id.settings_topic);
		editor.putString(iTVConfig.PREF_TOPIC, topicEdit.getText().toString());
		
		EditText userNameEdit = (EditText) findViewById(R.id.settings_user_name);
		editor.putString(iTVConfig.PREF_USER_NAME, userNameEdit.getText().toString());

		EditText passwordEdit = (EditText) findViewById(R.id.settings_pwd);
		editor.putString(iTVConfig.PREF_PWD, passwordEdit.getText().toString());
		
		editor.commit();
		
		finish();
	}


}
