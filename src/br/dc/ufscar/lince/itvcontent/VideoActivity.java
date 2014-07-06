package br.dc.ufscar.lince.itvcontent;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.VideoView;
import br.dc.ufscar.lince.itvcontent.logic.iTVLogic;
import br.dc.ufscar.lince.itvcontent.messaging.iTVMessaging;
import br.dc.ufscar.lince.itvcontent.messaging.iTVMessagingListener;

public class VideoActivity extends Activity implements iTVMessagingListener {
	
	private final String TAG = "VideoActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_video);
		
		Bundle bundle = getIntent().getExtras();
		
		String mediaId = bundle.getString("mediaId"); 
        if(mediaId != null){
        	
			iTVLogic logic = iTVLogic.getInstance();
			String url = logic.getFileURL(mediaId);
			
			if(url != null){			
				//specify the location of media file  
		        Uri uri = Uri.parse(url); 
		        
				VideoView videoView = (VideoView) findViewById(R.id.video_content);
				 videoView.setVideoURI(uri);
				 videoView.start();
			}
        }
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.video, menu);
		return true;
	}
	
	protected void onResume(){
		super.onResume();
		iTVMessaging messaging = iTVMessaging.getInstance();
		messaging.setMessagingListener(this);
	}

	@Override
	public void onMessage(String message) {

		Log.d(TAG, "onMessage event received");
		try {
			JSONObject jObject = new JSONObject(message);
			String action = jObject.getString("action");

			if (!action.equals("show")) {
				hideMedia();
			}

		} catch (JSONException e) {
			Log.e(TAG, "Exception parsing message JSON: " + e);
		}
	}
	
	public void hideMedia() {
		Log.d(TAG, "hideMedia event received");
		finish();
	}

	@Override
	public void connectionSucceeded() {
		Log.d(TAG, "connectionSucceeded event received");		
	}

	@Override
	public void connectionFailed(String message) {
		Log.d(TAG, "connectionFailed event received");
	}
}
