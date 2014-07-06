package br.dc.ufscar.lince.itvcontent;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.ImageView;
import br.dc.ufscar.lince.itvcontent.config.iTVConfig;
import br.dc.ufscar.lince.itvcontent.logic.iTVLogic;
import br.dc.ufscar.lince.itvcontent.messaging.iTVMessaging;
import br.dc.ufscar.lince.itvcontent.messaging.iTVMessagingListener;

public class SyncActivity extends Activity implements iTVMessagingListener {
	
	private final String TAG = "SyncActivity";
	private final boolean DEBUG = false;
	
	private ProgressDialog progressDialog;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sync);
		
		if (!DEBUG){			
			iTVMessaging messaging = iTVMessaging.getInstance();
			messaging.connect(this, iTVConfig.MESSAGING_DEFAULT_SYNC_TOPIC);
		}
		//carrega JSON do arquivo
		else{
			
			String jsonString = null;
		    try {
		        Resources res = getResources();
		        InputStream in = res.openRawResource(R.raw.json_test);
		        BufferedReader br = new BufferedReader(new InputStreamReader(in));
		        try {
		            StringBuilder sb = new StringBuilder();
		            String line = br.readLine();

		            while (line != null) {
		                sb.append(line);
		                sb.append('\n');
		                line = br.readLine();
		            }
		            jsonString = sb.toString();
		        } finally {
		            br.close();
		        }
		        
		     sync(jsonString);
		        
		    } catch (Exception e) {
		        e.printStackTrace();
		    }
		}

	}
	
	private void sync(String jsonString){
        
        iTVLogic logic = iTVLogic.getInstance();
        logic.parseMediaJSON(jsonString);
        List<String[]> files = logic.getFileNames();
        
		/*Creating and executing background task*/
        
		GetXMLTask task = new GetXMLTask(this);
        task.execute(files);
		
		progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("In progress...");
        progressDialog.setMessage("Loading...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setIndeterminate(false);
        progressDialog.setMax(100);
        progressDialog.setCancelable(true);
        progressDialog.show();
	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.sync, menu);
		return true;
	}
	
	private class GetXMLTask extends AsyncTask<List<String[]>, Integer, Integer> {
        private Activity context;
        
        int fileCount = -1;
        
        int noOfURLs;
        public GetXMLTask(Activity context) {
            this.context = context;
        }
 
        @Override
        protected Integer doInBackground(List<String[]>... listaMedias) {
            noOfURLs = listaMedias.length;
            for (List<String[]> media : listaMedias) {
            	this.fileCount = media.size();
            	for (int i = 0; i < media.size(); i++) {
            		publishProgress((int) ((i / (float) fileCount) * 100));
            		saveMedia(media.get(i));
            	}
            }
            
            Log.d(TAG,"----- Arquivos salvos----");
            File mydir = context.getDir("", Context.MODE_PRIVATE);              
            File lister = mydir.getAbsoluteFile();
            for (String list : lister.list())
            {
               Log.d(TAG, list);
            }
            Log.d(TAG,"\n\n");
            
            return 0;
        }
 
        private void saveMedia(String[] mediaInfo) {
        	
        	//essas medias não serão armazenadas em cache
        	if(mediaInfo[2].equals("VIDEO") || mediaInfo[2].equals("AUDIO")){
        		return;
        	}
 
        	int count;
        	
            try {
                URL url = new URL(mediaInfo[1]);
                URLConnection conection = url.openConnection();
                conection.connect();
                // getting file length
                int lenghtOfFile = conection.getContentLength();
     
                // input stream to read file - with 8k buffer
                InputStream input = new BufferedInputStream(url.openStream(), 8192);
                
                String fileName = mediaInfo[0];
              
                FileOutputStream output = openFileOutput(fileName, Context.MODE_WORLD_READABLE); //Use the stream as usual to write into the file.
                     
                byte data[] = new byte[1024];
     
                long total = 0;
     
                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress((int)((total*100)/lenghtOfFile));
     
                    // writing data to file
                    output.write(data, 0, count);
                }
     
                // flushing output
                output.flush();
     
                // closing streams
                output.close();
                input.close();
                
                Log.d(TAG, "File ["+fileName+"] successfully written!");
     
            } 
            catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
     
        }
 
        protected void onProgressUpdate(Integer... progress) {
            progressDialog.setProgress(progress[0]);
           progressDialog.setMessage("Loading " + fileCount + "/" + noOfURLs);
            
       }
 
       @Override
       protected void onPostExecute(Integer i) {
			progressDialog.dismiss();

			Intent intent = new Intent(context, IdleActivity.class);
			startActivity(intent);
       }    
 
    }
	
	@Override
	public void onMessage(String message) {
	
	iTVMessaging messaging = iTVMessaging.getInstance();
	messaging.disconnect();
		
	final String internalMessage = message;
		runOnUiThread(new Runnable() {
		     public void run() {
		    	 sync(internalMessage);
		     }
		});
	}
	
	public void connectionSucceeded(){
		runOnUiThread(new Runnable() {
		     public void run() {
		 		ImageView imageView = (ImageView) findViewById(R.id.sync_status_image);
				imageView.setImageResource(R.drawable.ok);		
		    }
		});

	}
	
	public void connectionFailed (String message){
		runOnUiThread(new Runnable() {
		     public void run() {
				ImageView imageView = (ImageView) findViewById(R.id.sync_status_image);
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
