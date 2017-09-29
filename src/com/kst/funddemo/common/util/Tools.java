package com.kst.funddemo.common.util;

import java.text.DecimalFormat;
import java.util.Locale;
import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import com.kst.funddemo.R;
import com.kst.funddemo.common.application.ApplicationData;
import com.kst.funddemo.model.NetworkInfo;

public class Tools {
	
	/**
	 * 读取域名
	 */
	public static String readAddress() {
		return getString(R.string.address);
	}
	
	/**
	 * 获取字符串
	 * 
	 * @return
	 */
	public static String getString(int id) {
		return ApplicationData.globalContext.getString(id);
	}

	/**
	 * 调用系统网络设置
	 * 
	 * @param activity
	 */
	public static void openSystemNetworkSetting(Activity activity) {
		try {
			Intent i = new Intent(
					android.provider.Settings.ACTION_WIRELESS_SETTINGS);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			activity.startActivity(i);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 获得屏幕的宽度
	 * 
	 * @return
	 */
	public static int getScreenWidth() {
		DisplayMetrics dm = new DisplayMetrics();
		dm = ApplicationData.globalContext.getApplicationContext()
				.getResources().getDisplayMetrics();
		return dm.widthPixels;
	}

	/**
	 * 获得屏幕的高度
	 * 
	 * @return
	 */
	public static int getScreenHeight() {
		DisplayMetrics dm = new DisplayMetrics();
		try {
			dm = ApplicationData.globalContext.getApplicationContext()
					.getResources().getDisplayMetrics();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dm.heightPixels;
	}

	/**
	 * 获取完整URL地址
	 * @param url 当前URL，可能不包含服务器地址
	 * @return
     */
	public static String getValidUrl(String url){
		if (!TextUtils.isEmpty(url) && !url.trim().toLowerCase(Locale.getDefault())
				.contains(AppConstant.httpHeader)) {
			url = readAddress() + url;
		}
		return url;
	}
	
	/**
	 * 获得系统版本号
	 * 
	 * @return
	 */
	public static String getSDK() {
		try {
			String release = android.os.Build.VERSION.RELEASE;
			if (null != release) {
				return release;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	/**
	 * 获得手机型号
	 * 
	 * @return
	 */
	public static String getPhoneModel() {
		try {
			String phoneVersion = android.os.Build.MODEL;
			if (null != phoneVersion) {
				return phoneVersion;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	/**
	 * 获取当前根路径
	 * 
	 * @return
	 */
	public static String getRootPath() {
		String rootPath = "";
		try {
			if (Environment.MEDIA_MOUNTED.equals(Environment
					.getExternalStorageState())
					&& Environment.getExternalStorageDirectory().canWrite()) {
				rootPath = AppConstant.sdcardRootPath;
			} else {
				rootPath = AppConstant.dataRootPath;
			}
		} catch (Exception e) {
			e.printStackTrace();
			rootPath = AppConstant.dataRootPath;
		}
		return rootPath;
	}
	
	/**
	 * 获取软件包名
	 * 
	 * @return
	 */
	public static String getPackageName() {
		return ApplicationData.globalContext.getPackageName();
	}
	
	/**
	 * 获得手机IMEI
	 * 
	 * @return
	 */
	public static String getIMEI() {
		try {
			TelephonyManager tm = (TelephonyManager) ApplicationData.globalContext
					.getSystemService(Context.TELEPHONY_SERVICE);
			String imei = tm.getDeviceId();
			if (null != imei) {
				return imei;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 获得手机IMSI
	 * 
	 * @return
	 */
	public static String getIMSI() {
		try {
			TelephonyManager tm = (TelephonyManager) ApplicationData.globalContext
					.getSystemService(Context.TELEPHONY_SERVICE);
			String imsi = tm.getSubscriberId();
			if (null != imsi) {
				return imsi;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	/**
	 * 获取wifi的mac地址
	 * 
	 * @return
	 */
	public static String getMacAddress() {
		try {
			WifiManager wifi = (WifiManager) ApplicationData.globalContext
					.getSystemService(Context.WIFI_SERVICE);
			WifiInfo info = wifi.getConnectionInfo();
			String mac = info.getMacAddress();
			if (null != mac) {
				return mac;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	
	/**
	 * 获取随机数
	 * 
	 * @return
	 */
	public static String getRandomNumber() {
		return new DecimalFormat("0000000000").format(new Random()
				.nextInt(1000000000));
	}
	
	/**
	 * dip转成pixel
	 * 
	 * @param dip
	 *            dip尺寸
	 * @return
	 */
	public static int dipToPixel(float dip) {
		try {
			return (int) (dip
					* ApplicationData.globalContext.getResources()
							.getDisplayMetrics().density + 0.5);
		} catch (Exception e) {
			e.printStackTrace();
			return (int) dip;
		}
	}
	
	/**
	 * 返回当前时间，单位毫秒
	 * 
	 * @return
	 */
	public static long getCurrentTimeMillis() {
		return System.currentTimeMillis();
	}
	
	/**
	 * 获取网络信息
	 * @return
	 */
	public static NetworkInfo getNetworkInfo() {
		NetworkInfo myNetworkInfo = new NetworkInfo();
		try {
			ConnectivityManager manager = (ConnectivityManager) ApplicationData.globalContext
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			android.net.NetworkInfo networkInfo = manager
					.getActiveNetworkInfo();
			if (networkInfo != null && networkInfo.isAvailable()
					&& networkInfo.isConnected()) {
				myNetworkInfo.setConnectToNetwork(true);
				myNetworkInfo.setType(networkInfo.getType());
				if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
					myNetworkInfo.setProxy(false);
					myNetworkInfo.setProxyName(networkInfo.getTypeName());
				} else {
					// 取得代理信息
					String proxyHost = android.net.Proxy.getDefaultHost();
					if (proxyHost != null) {
						myNetworkInfo.setProxy(true);
						myNetworkInfo.setProxyHost(proxyHost);
						myNetworkInfo.setProxyPort(android.net.Proxy
								.getDefaultPort());
					} else {
						myNetworkInfo.setProxy(false);
					}
					myNetworkInfo.setProxyName(networkInfo.getExtraInfo());
				}
			} else {
				myNetworkInfo.setConnectToNetwork(false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return myNetworkInfo;
	}

}
