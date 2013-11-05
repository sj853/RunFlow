package com.sj.util;

import android.net.TrafficStats;

public class MyTrafficStats extends TrafficStats{
	/**
	 * 获取总的流量(发送和接收 以及包括2G、3G和WIFI的流量)
	 * @return
	 */
	public static long getTotalBytes(){
		return getTotalRxBytes()+getTotalTxBytes();
	}
	
	/**
	 * 获取总的2G/3G流量(发送和接收,除了WIFI流量)
	 * @return
	 */
	public static long getMobileTotalBytes(){
		return getMobileRxBytes()+getMobileTxBytes();
	}
	
	/**
	 * 获取总的通过wifi使用的流量(发送和接收，除了2G/3G流量)
	 * @return
	 */
	public static long getWifiTotalBytes(){
		return getTotalBytes()-getMobileTotalBytes();
	}
	
	/**
	 * 获取某个进程id下总的流量(发送和接收)
	 * @param uid
	 * @return
	 */
	public static long getUidTotalBytes(int uid){
		return getUidRxBytes(uid)+getUidTxBytes(uid);
	}
	
	
}
