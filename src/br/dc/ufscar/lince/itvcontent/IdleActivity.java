package br.dc.ufscar.lince.itvcontent;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;
import br.dc.ufscar.lince.itvcontent.config.iTVConfig;
import br.dc.ufscar.lince.itvcontent.logic.iTVLogic;
import br.dc.ufscar.lince.itvcontent.messaging.iTVMessaging;
import br.dc.ufscar.lince.itvcontent.messaging.iTVMessagingListener;

public class IdleActivity extends Activity implements iTVMessagingListener {

	private final String TAG = "IdleActivity";
	private AlertDialog alertDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_idle);

		// Sets audio volume
		AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 20, 0);

		iTVLogic logic = iTVLogic.getInstance();
		TextView mediaTitle = (TextView) findViewById(R.id.media_title);
		mediaTitle.setText(logic.getProjectName());

		iTVLogic itvLogic = iTVLogic.getInstance();

		iTVMessaging messaging = iTVMessaging.getInstance();
		messaging.connect(this, itvLogic.getMessagingTopic());
		
	}

	protected void onResume() {
		super.onResume();

		iTVMessaging messaging = iTVMessaging.getInstance();
		messaging.setMessagingListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.idle, menu);
		return true;
	}

	public void onMessage(String message) {

		Log.d(TAG, "onMessage event received");
		try {
			JSONObject jObject = new JSONObject(message);
			String mediaId = jObject.getString("media_name");
			String typeString = jObject.getString("type");
			String action = jObject.getString("action");

			int type = -1;

			if (typeString.equals("TEXT")) {
				type = iTVConfig.MEDIA_TEXT;
			} else if (typeString.equals("IMAGE")) {
				type = iTVConfig.MEDIA_IMAGE;
			} else if (typeString.equals("VIDEO")) {
				type = iTVConfig.MEDIA_VIDEO;
			} else if (typeString.equals("AUDIO")) {
				type = iTVConfig.MEDIA_AUDIO;
			}

			if (action.equals("show")) {
				final int typeHolder = type;
				final String mediaIdHolder = mediaId;
				
				runOnUiThread(new Runnable() {
					public void run() {
						showMedia(typeHolder, mediaIdHolder);
					}
				});
			}
		} catch (JSONException e) {
			Log.e(TAG, "Exception parsing message JSON: " + e);
		}
	}

	public void showMedia(int type, String mediaId) {
		
		if(alertDialog != null){
			alertDialog.dismiss();
		}

		Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		// Vibrate for 500 milliseconds
		v.vibrate(500);
		
//		try {
//	        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//	        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
//	        r.play();
//	    } catch (Exception e) {}
		
		Intent intent = null;
		switch (type) {
		case iTVConfig.MEDIA_TEXT:
			
			intent = new Intent(this, TextActivity.class);
			intent.putExtra("mediaId", mediaId);
			startActivity(intent);

			break;
		case iTVConfig.MEDIA_IMAGE:
			
			intent = new Intent(this, ImageActivity.class);
			intent.putExtra("mediaId", mediaId);
			startActivity(intent);
			break;
		case iTVConfig.MEDIA_VIDEO:
			this.showAudioVideo(mediaId);
			break;

		case iTVConfig.MEDIA_AUDIO:
			this.showAudioVideo(mediaId);
			break;

		default:
			break;
		}

	}
	
	public void showAudioVideo(String mediaId){
		final String mediaIdHolder = mediaId;
		
		alertDialog = new AlertDialog.Builder(this)
	    .setTitle("Media")
	    .setMessage("Deseja reproduzir?")
	    .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) { 
	            // continue with delete
				Intent intent = new Intent(IdleActivity.this, VideoActivity.class);
				intent.putExtra("mediaId", mediaIdHolder);
				startActivity(intent);
	        }
	     })
	    .setNegativeButton("Não", new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) { 
	            // do nothing
	        }
	     })
	     .show();

	}

	public void connectionSucceeded() {
		runOnUiThread(new Runnable() {
			public void run() {
				ImageView imageView = (ImageView) findViewById(R.id.status_image);
				imageView.setImageResource(R.drawable.ok);
			}
		});

	}

	public void connectionFailed(String message) {
		runOnUiThread(new Runnable() {
			public void run() {
				ImageView imageView = (ImageView) findViewById(R.id.status_image);
				imageView.setImageResource(R.drawable.failed);
			}
		});

	}

	@Override
	public void onBackPressed() {
		iTVMessaging messaging = iTVMessaging.getInstance();
		messaging.disconnect();

		super.onBackPressed();
	}
}
