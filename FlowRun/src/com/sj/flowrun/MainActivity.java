package com.sj.flowrun;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.sj.util.MyTrafficStats;
import com.sj.util.URLconnecTest;

public class MainActivity extends Activity {
	
	private TextView currentSpeed;
	private TextView currentPercenteage;
	private TextView result;
	private boolean isStart = true;
	private ConnectivityManager cm;
	
	private Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				currentSpeed.setText((CharSequence) msg.obj);
				break;
			case 1:
				currentPercenteage.setText((CharSequence) msg.obj);
				break;
			case 2:
				result.setText((CharSequence) msg.obj);
				break;
			default:
				break;
			}
		}
		
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		currentSpeed = (TextView) findViewById(R.id.currentSpeedTxt);
		currentPercenteage = (TextView) findViewById(R.id.currentPercenteage);
		result = (TextView) findViewById(R.id.resultTxt);
		cm =  (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
		
		Button startBtn = (Button) findViewById(R.id.startBtn);
		
		Button stopBtn = (Button) findViewById(R.id.stopBtn);
		
		startBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

							Timer timer = new Timer(true);
							timer.schedule(new TimerTask() {
								@Override
								public void run() {
									try {
										Message msg = new Message();
										msg.what = 0;
										msg.obj = "currentSpeed: "
												+ URLconnecTest.sizeParse(URLconnecTest.currentLength - URLconnecTest.furtherLength)+"/S"
												+" processID : "+Process.myUid()
												+" networkType : "+ cm.getActiveNetworkInfo().getTypeName()
											+ " 目前消耗了-"+URLconnecTest.sizeParse(MyTrafficStats.getWifiTotalBytes())+"-文件大小 "+URLconnecTest.sizeParse(URLconnecTest.currentLength)+"  流量";
										handler.sendMessage(msg);
										if (URLconnecTest.furtherLength != 0 && URLconnecTest.furtherLength == URLconnecTest.totalLength){
											this.cancel();
										}else{
											URLconnecTest.furtherLength = URLconnecTest.currentLength;
										}
									} catch (Exception e) {
									}
								}
							}, 0, 1000);

							new Thread(new Runnable() {

								@Override
								public void run() {
 
									long startTime = System.currentTimeMillis();
									URLconnecTest.downloadTemp = MainActivity.this.getCacheDir().getAbsolutePath(); 
									URLconnecTest.download();
									long useTime = System.currentTimeMillis() - startTime;
									try {
										Message msg = new Message();
										msg.what = 2;
										msg.obj = "total size : "
												+ URLconnecTest.sizeParse(URLconnecTest.totalLength)
												+ " total use time: "
												+ useTime
												+ " avg speed: "
												+ URLconnecTest.sizeParse(URLconnecTest.totalLength / useTime
														* 1000) + "/S";
										handler.sendMessage(msg);
									} catch (Exception e) {
									}
								}
							}).start();
							
							new Thread(new Runnable() {
								@Override
								public void run() {
										while(true){
											try {
												Thread.sleep(1000);
												URLconnecTest.percenteage = URLconnecTest.doPercentage(URLconnecTest.currentLength, URLconnecTest.totalLength);
												Message msg = new Message();
												msg.what = 1;
												msg.obj = "currentPercenteage: "+URLconnecTest.percenteage;
												handler.sendMessage(msg);
												if(URLconnecTest.currentLength == URLconnecTest.totalLength && URLconnecTest.currentLength!=0 ) {
													break;
												}
											} catch (InterruptedException e) {
												e.printStackTrace();
											}catch (Exception e) {
												e.printStackTrace();
											}
										}
								}
							}).start();

						
			}
		});
		
		stopBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				URLconnecTest.currentLength = 0;
				URLconnecTest.totalLength = 0;
				URLconnecTest.furtherLength = 0;
			}
		});
		
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
