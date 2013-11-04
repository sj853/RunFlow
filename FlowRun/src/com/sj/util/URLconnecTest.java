package com.sj.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;


/**
 * 
 * @author shang URLconnection 试用
 */
public class URLconnecTest {

	public static long totalLength = 0;

	public static long currentLength = 0;

	public static int currentUseTime = 0;

	public static String fileName = "pcmaster_5.02.zip";

	public static String domain = "http://down.ruanmei.com/pcmaster/";
	
	public static String downloadTemp = "";

	public static void download() {
		String urlStr = domain + fileName;
		URL url;
		HttpURLConnection urlConnection;
		InputStream is = null;
		FileOutputStream fileOutputStream = null;
		try {
			url = new URL(urlStr);
			urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setConnectTimeout(15000);
			if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				totalLength = urlConnection.getContentLength();
				System.out.println("total-size: " + totalLength);
				is = urlConnection.getInputStream();
				fileOutputStream = new FileOutputStream(downloadTemp + fileName);
				int count = 0;
				int bite = 0;
				long startTime = 0;
				long useTime = 0;
				while (count == 0) {
					startTime = System.currentTimeMillis();
					count = is.available();
					if (currentLength < totalLength) {
						if (count == 0)
							continue;
						// System.out.println("count : " + count);
						byte[] b = new byte[count];
						bite = is.read(b);
						if (bite == -1) {
							fileOutputStream.flush();
							count = -1;
						} else {
							fileOutputStream.write(b);
							useTime = System.currentTimeMillis() - startTime;
							currentUseTime += useTime;
							// System.out.println("useTime: " + useTime);
							currentLength += bite;
							try {
//								System.out
//										.println("bite: "
//												+ bite
//												+ " usetime: "
//												+ useTime
//												+ " currentLenth: "
//												+ currentLength
//												+ " currentTime: "
//												+ currentUseTime
//												+ " speed: "
//												+ sizeParse(currentLength
//														/ currentUseTime * 1000)
//												+ "/S");
							} catch (Exception e) {
							}
							count = 0;
						}
					} else {
						count = -1;
					}

				}
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
				fileOutputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * 容量计算
	 * @param bitLen Byte
	 * @return 计算后的带单位容量
	 */
	public static String sizeParse(long bitLen) {
		long SIZE_KB = 1024;
		long SIZE_MB = SIZE_KB * 1024;
		long SIZE_GB = SIZE_MB * 1024;

		if (bitLen < SIZE_KB) {
			return String.format("%d B", bitLen);
		} else if (bitLen < SIZE_MB) {
			return String.format("%.2f KB", (float) bitLen / SIZE_KB);
		} else if (bitLen < SIZE_GB) {
			return String.format("%.2f MB", (float) bitLen / SIZE_MB);
		} else {
			return String.format("%.2f GB", (float) bitLen / SIZE_GB);
		}

	}
	
	/**
	 * 计算当前百分比
	 * @param Byte 
	 * @return 完成百分比
	 */
	public static String doPercentage(long currentByte,long totalByte){
		return String.format("%.2f", (float)currentByte/(float)totalByte*100).concat("%");
	}


	public static long furtherLength = 0;
	public static String percenteage = "0%";

	public static void main(String[] args) {
		Timer timer = new Timer(true);
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				try {
					if (furtherLength != 0 && furtherLength == totalLength)
						this.cancel();
					System.out.println("currentSpeed: "
							+ sizeParse(currentLength - furtherLength)+"/S");
					furtherLength = currentLength;
				} catch (Exception e) {
				}
			}
		}, 0, 1000);

		new Thread(new Runnable() {

			@Override
			public void run() {

				long startTime = System.currentTimeMillis();
				URLconnecTest.download();
				long useTime = System.currentTimeMillis() - startTime;
				try {
					System.out.println("total size : "
							+ sizeParse(totalLength)
							+ " total use time: "
							+ useTime
							+ " avg speed: "
							+ sizeParse(URLconnecTest.totalLength / useTime
									* 1000) + "/S");
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
							if(currentLength == totalLength && currentLength!=0 ) break;
							percenteage = doPercentage(currentLength, totalLength);
							System.out.println("currentPercenteage: "+percenteage);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}catch (Exception e) {
							e.printStackTrace();
						}
					}
			}
		}).start();

	}

}
