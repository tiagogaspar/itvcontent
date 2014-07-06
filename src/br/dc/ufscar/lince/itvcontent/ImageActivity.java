package br.dc.ufscar.lince.itvcontent;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;
import br.dc.ufscar.lince.itvcontent.logic.iTVLogic;
import br.dc.ufscar.lince.itvcontent.messaging.iTVMessaging;
import br.dc.ufscar.lince.itvcontent.messaging.iTVMessagingListener;

public class ImageActivity extends Activity implements iTVMessagingListener {

	private final String TAG = "ImageActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image);
		
		Bundle bundle = getIntent().getExtras();
		
		String mediaId = bundle.getString("mediaId"); 
        if(mediaId != null){
        	byte[] data = iTVLogic.getInstance().getFileContent(this, mediaId);		
        	Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            
        	ImageView imageView = (ImageView) findViewById(R.id.image_content);
        	imageView.setImageBitmap(bitmap);
        }

	}

	protected void onResume() {
		super.onResume();
		iTVMessaging messaging = iTVMessaging.getInstance();
		messaging.setMessagingListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.image, menu);
		return true;
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
