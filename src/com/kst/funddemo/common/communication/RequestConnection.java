package com.kst.funddemo.common.communication;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;

import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BrowserCompatSpec;
import org.apache.http.impl.cookie.CookieSpecBase;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.kst.funddemo.common.application.ApplicationData;
import com.kst.funddemo.common.exception.NetworkConnectException;
import com.kst.funddemo.common.exception.NetworkForceCloseException;
import com.kst.funddemo.common.exception.NetworkNotException;
import com.kst.funddemo.common.exception.NetworkTimeoutException;
import com.kst.funddemo.common.util.AppConstant;
import com.kst.funddemo.common.util.Tools;
import com.kst.funddemo.common.util.ToolsFile;
import com.kst.funddemo.model.NetworkInfo;

import android.util.Log;


/**
 * 联网底层类 负责网络数据的接收和发送
 * 
 */
public class RequestConnection {

	/** 连接超时时间 */
	private static final int TIMEOUT = 30000;

	/** 自定义header头,判断是否压缩为gzip */
	public static final String dataEncoding = "Data-Encoding";

	/** 是否取消联网获取数据 */
	private boolean isCancelled = false;

	/**
	 * 取消联网
	 */
	public void setCancel() {
		isCancelled = true;
	}

	/**
	 * 得到联网状态
	 * 
	 * @return 是否取消联网
	 */
	public boolean getState() {
		return isCancelled;
	}

	/**
	 * 获得Header
	 * 
	 * @param params
	 *            参数列表
	 * @return 头信息
	 */
	private Header[] getHeaders(List<NameValuePair> params) {
		Header[] headers = {
				new BasicHeader("COOKIE", "sessionid=86f6124e0ee6426da63cf3ee3cc93d8c"),
				new BasicHeader("X-Client", getXClient(params)) };
		return headers;
	}

	/**
	 * webView请求时发送的客户端信息
	 * 
	 * @return xClient信息
	 */
//	public static Map<String, String> webViewXClient(String referer) {
//		Map<String, String> map = new HashMap<String, String>();
//		try {
//			map.put("Referer", referer);
//			map.put("X-Client", getXClient(null));
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return map;
//	}

	   /**
     * 获得上传的X-Client信息
     *
     * @return xClient信息
     */
    public static String getXClient(List<NameValuePair> params) {

        String sdk = "";
        try {
            sdk = URLEncoder.encode(Tools.getSDK(), HTTP.UTF_8);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String type = "";
        try {
            type = URLEncoder.encode(Tools.getPhoneModel(), HTTP.UTF_8);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String rootPath = "";
        try {
            rootPath = URLEncoder.encode((Tools.getRootPath()), HTTP.UTF_8);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String screenSize = Tools.getScreenWidth() + "*"
                + Tools.getScreenHeight();
        String imei = Tools.getIMEI();
        String imsi = Tools.getIMSI();
        String mac = Tools.getMacAddress();
        // String cell_id = Tools.getNetWorkBaseStation();
        String version = "6.3.50.12";

        String rn = "";
        try {
            rn = URLEncoder.encode(Tools.getRandomNumber(), HTTP.UTF_8);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String ipAddress = "";
//        if(Tools.isUseVmIp()){
//            if(ToolsFile.isFileExit(AppConstant.vmIpFile)){
//                File ipfile = new File(AppConstant.vmIpFile);
//                try {
//                    InputStream in = new FileInputStream(ipfile);
//                    ipAddress = new String(ToolsFile.readBytes(in));
//                } catch (FileNotFoundException e) {
//                    ipAddress = "";
//                } catch (IOException e1) {
//                    ipAddress = "";
//                }
//            }
//        }

        String xClient = "sdk="
                + sdk
                + ";"
                + "screenSize="
                + screenSize
                + ";"
                + "type="
                + type
                + ";"
                + "imei="
                + imei
                + ";"
                + "imsi="
                + imsi
                + ";"
                + "version="
                + version
                + ";"
                + "mac="
                + mac
                + ";"
                + "rootPath="
                + rootPath
                + ";"
                + "rn="
                + rn
                + ";"
                + "tdcn="
                + "aeb56195628ff9b7e045e0efb0f801fd" + ";"
                + "hotfix="    //xclient中添加hotfix字段，内部版本号，用于区分一个版本多次灰度和正式包
                + "1"
                + ";"
                + "ip="
                + ipAddress
                + ";";

        return xClient;
    }


	/**
	 * get请求
	 * 
	 * @param url
	 *            链接地址
	 * @param params
	 *            参数列表
	 * @return 服务端响应的内容
	 * @throws NetworkNotException
	 * @throws NetworkConnectException
	 * @throws NetworkForceCloseException
	 */
	public String doGet(String url, List<NameValuePair> params)
			throws NetworkNotException, NetworkConnectException,
			NetworkTimeoutException, NetworkForceCloseException {
		if (params != null&&params.size()>0) {  
			url = url + "?" + URLEncodedUtils.format(params, HTTP.UTF_8);
		}
		HttpGet get = new HttpGet(url);
		get.setHeaders(getHeaders(params));
		return doRequest(url, get);
	}

	/**
	 * post请求
	 * 
	 * @param url
	 *            链接地址
	 * @param params
	 *            参数列表
	 * @return 服务端响应的信息
	 * @throws NetworkConnectException
	 * @throws NetworkNotException
	 * @throws NetworkForceCloseException
	 */
	public String doPost(String url, List<NameValuePair> params)
			throws NetworkConnectException, NetworkNotException,
			NetworkTimeoutException, NetworkForceCloseException {
		HttpPost post = new HttpPost(url);
		if (params != null) {
			try {
				post.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
				try {
					Log.e("tadu", "post : " + EntityUtils.toString(post.getEntity()));
				} catch (ParseException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		post.setHeaders(getHeaders(params));
		return doRequest(url, post);
	}

	/**
	 * 请求
	 * 
	 * @param req
	 *            http请求
	 * @return 服务端返回的内容
	 * @throws NetworkConnectException
	 * @throws NetworkNotException
	 * @throws NetworkForceCloseException
	 */
	private String doRequest(String url, HttpUriRequest req)
			throws NetworkConnectException, NetworkNotException,
			NetworkTimeoutException, NetworkForceCloseException {
		Log.e("tadu", "请求url" + url);
		// 网络状态信息
		String httpcode = "200";
		// 网络接入点名
		String apnName = "";
		// 文件大小
		int size = 0;
		// 联网时间
		long startTime = Tools.getCurrentTimeMillis();

		HttpClient httpClient = null;
		try {
			if (isCancelled)
				throw new NetworkForceCloseException();

			// 设置连接超时
			HttpParams httpParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParams, TIMEOUT);
			HttpConnectionParams.setSoTimeout(httpParams, TIMEOUT);
			// 初始化HttpClient
			httpClient = new DefaultHttpClient(httpParams);
			HttpResponse httpResponse = null;

			// 网络状态判断
			NetworkInfo networkInfo = Tools.getNetworkInfo();
			apnName = networkInfo.getProxyName();
			if (!networkInfo.isConnectToNetwork()) {
				throw new NetworkNotException();
			}

			if (networkInfo.isProxy()) {
				// 为连连接设置代理
				HttpHost proxy = new HttpHost(networkInfo.getProxyHost(),
						networkInfo.getProxyPort(), "http");
				httpClient.getParams().setParameter(
						ConnRoutePNames.DEFAULT_PROXY, proxy);
				// 设置域名
				String pUrl = req.getURI().toURL().toString()
						.replace("http://", "");
				int index = pUrl.indexOf("/");
				String host = pUrl.substring(0, index);
				HttpHost httpHost = new HttpHost(host, 80, "http");

				if (isCancelled)
					throw new NetworkForceCloseException();
				httpResponse = httpClient.execute(httpHost, req);
				// size=(int)httpResponse.getEntity().getContentLength();
				// 废弃页处理
				Header header = httpResponse.getEntity().getContentType();

				if (header.getValue().startsWith("text/vnd.wap.wml")) {
					httpClient.getConnectionManager().shutdown();
					httpClient = new DefaultHttpClient(httpParams);
					httpClient.getParams().setParameter(
							ConnRoutePNames.DEFAULT_PROXY, proxy);
					httpResponse = httpClient.execute(httpHost, req);
				}
			} else {
				if (isCancelled)
					throw new NetworkForceCloseException();
				httpResponse = httpClient.execute(req);
			}
			if (isCancelled)
				throw new NetworkForceCloseException();

			int statusCode = httpResponse.getStatusLine().getStatusCode();
			httpcode = String.valueOf(statusCode);
			if (statusCode == HttpStatus.SC_OK) {
				InputStream is = httpResponse.getEntity().getContent();

				// 判断网关是否加入了压缩
				Header contentEncoding = httpResponse.getEntity()
						.getContentEncoding();
				if (contentEncoding != null) {
					String value = contentEncoding.getValue().toLowerCase();
					if (value.indexOf("gzip") != -1) {
						is = new GZIPInputStream(is);
					}
				}

				// 判断服务器是否加入了压缩
				Header[] dataEncodingHeader = httpResponse
						.getHeaders(dataEncoding);
				for (int i = 0; i < dataEncodingHeader.length; i++) {
					if (dataEncodingHeader[i] != null) {
						String value = dataEncodingHeader[i].getValue()
								.toLowerCase();
						if (value.indexOf("gzip") != -1) {
							is = new GZIPInputStream(is);
						}
					}
				}

				// 获得Cookie的值
//				Header[] allHeaders = httpResponse.getAllHeaders();
//				CookieSpecBase cookieSpecBase = new BrowserCompatSpec();
//				CookieOrigin origin = new CookieOrigin(Tools.readAddress(), 80,
//						"/", false);
//				for (Header header : allHeaders) {
//					List<Cookie> parse = cookieSpecBase.parse(header, origin);
//					for (Cookie cookie : parse) {
//						if (cookie.getName().equals("sessionid")
//								&& cookie.getValue() != null
//								&& cookie.getValue() != "") {
//							ApplicationData.globalContext.getUserManager()
//									.setSessionId(cookie.getValue().toString());
//						}
//					}
//				}

				if (isCancelled)
					throw new NetworkForceCloseException();

				byte[] data = ToolsFile.readBytes(is);
				is.close();
				// 文件大小
				size = data.length;
				if (isCancelled)
					throw new NetworkForceCloseException();

				String response = new String(data, HTTP.UTF_8);
				Log.e("tadu", "url:"+url+"返回信息"+response);
				return response;
			} else {
				throw new NetworkConnectException(httpcode);
			}
		} catch (NetworkForceCloseException e) {
			httpcode = AppConstant.NetworkForceCloseException;
			throw new NetworkForceCloseException();
		} catch (NetworkNotException e) {
			httpcode = AppConstant.NoNetWork;
			throw new NetworkNotException();
		} catch (SocketTimeoutException e) {
			httpcode = AppConstant.NetworkTimeoutException;
			throw new NetworkTimeoutException();
		} catch (Exception e) {
			httpcode = AppConstant.NetWorkConnectException;
			throw new NetworkConnectException();
		} finally {
			if (httpClient != null) {
				httpClient.getConnectionManager().shutdown();
			}
		}
	}

	/**
	 * 下载文件
	 * 
	 * @param url
	 *            链接地址
	 * @param params
	 *            参数列表
	 * @return 服务端返回的内容
	 * @throws NetworkConnectException
	 * @throws NetworkNotException
	 * @throws NetworkTimeoutException
	 * @throws NetworkForceCloseException
	 */
	public byte[] doGetFile(String url, List<NameValuePair> params)
			throws NetworkConnectException, NetworkNotException,
			NetworkTimeoutException, NetworkForceCloseException {
		if (params != null) {
			url = url + "?" + URLEncodedUtils.format(params, HTTP.UTF_8);
		}
		HttpUriRequest req = new HttpGet(url);
		req.setHeaders(getHeaders(params));

		// 网络状态信息
		String httpcode = "200";
		// 网络接入点名
		String apnName = "";
		// 文件大小
		int size = 0;
		// 联网时间
		long startTime = Tools.getCurrentTimeMillis();

		HttpClient httpClient = null;
		try {
			if (isCancelled)
				throw new NetworkForceCloseException();

			// 设置连接超时
			HttpParams httpParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParams, TIMEOUT);
			HttpConnectionParams.setSoTimeout(httpParams, TIMEOUT);
			// 初始化HttpClient
			httpClient = new DefaultHttpClient(httpParams);
			HttpResponse httpResponse = null;

			// 网络状态判断
			NetworkInfo networkInfo = Tools.getNetworkInfo();
			apnName = networkInfo.getProxyName();
			if (!networkInfo.isConnectToNetwork()) {
				throw new NetworkNotException();
			}
			if (networkInfo.isProxy()) {
				// 为连连接设置代理
				HttpHost proxy = new HttpHost(networkInfo.getProxyHost(),
						networkInfo.getProxyPort(), "http");
				httpClient.getParams().setParameter(
						ConnRoutePNames.DEFAULT_PROXY, proxy);
				// 设置域名
				String pUrl = req.getURI().toURL().toString()
						.replace("http://", "");
				int index = pUrl.indexOf("/");
				String host = pUrl.substring(0, index);
				HttpHost httpHost = new HttpHost(host, 80, "http");

				if (isCancelled)
					throw new NetworkForceCloseException();

				httpResponse = httpClient.execute(httpHost, req);
				// size=(int)httpResponse.getEntity().getContentLength();
				// 废弃页处理
				Header header = httpResponse.getEntity().getContentType();
				if (header.getValue().startsWith("text/vnd.wap.wml")) {
					httpClient.getConnectionManager().shutdown();
					httpClient = new DefaultHttpClient(httpParams);
					httpClient.getParams().setParameter(
							ConnRoutePNames.DEFAULT_PROXY, proxy);
					httpResponse = httpClient.execute(httpHost, req);
				}
			} else {
				if (isCancelled)
					throw new NetworkForceCloseException();
				httpResponse = httpClient.execute(req);
			}
			if (isCancelled)
				throw new NetworkForceCloseException();

			int statusCode = httpResponse.getStatusLine().getStatusCode();
			httpcode = String.valueOf(statusCode);
			if (statusCode == HttpStatus.SC_OK) {
				InputStream is = httpResponse.getEntity().getContent();

				// 判断网关是否加入了压缩
				Header contentEncoding = httpResponse.getEntity()
						.getContentEncoding();
				if (contentEncoding != null) {
					String value = contentEncoding.getValue().toLowerCase();
					if (value.indexOf("gzip") != -1) {
						is = new GZIPInputStream(is);
					}
				}

				// 判断服务器是否加入了压缩
				Header[] dataEncodingHeader = httpResponse
						.getHeaders(dataEncoding);
				for (int i = 0; i < dataEncodingHeader.length; i++) {
					if (dataEncodingHeader[i] != null) {
						String value = dataEncodingHeader[i].getValue()
								.toLowerCase();
						if (value.indexOf("gzip") != -1) {
							is = new GZIPInputStream(is);
						}
					}
				}

				if (isCancelled)
					throw new NetworkForceCloseException();

				byte[] data = ToolsFile.readBytes(is);
				is.close();
				// 获取文件大小
				size = data.length;
				if (isCancelled)
					throw new NetworkForceCloseException();

				return data;
			} else {
				throw new NetworkConnectException(httpcode);
			}
		} catch (NetworkForceCloseException e) {
			httpcode = AppConstant.NetworkForceCloseException;
			throw new NetworkForceCloseException();
		} catch (NetworkNotException e) {
			httpcode = AppConstant.NoNetWork;
			throw new NetworkNotException();
		} catch (SocketTimeoutException e) {
			httpcode = AppConstant.NetworkTimeoutException;
			throw new NetworkTimeoutException();
		} catch (Exception e) {
			httpcode = AppConstant.NetWorkConnectException;
			throw new NetworkConnectException();
		} finally {
			if (httpClient != null) {
				httpClient.getConnectionManager().shutdown();
			}
		}
	}

	/**
	 * 上传文件
	 * 
	 * @param url
	 *            上传地址
	 * @param filePath
	 *            文件路径
	 * @return 服务端响应的内容
	 * @throws NetworkConnectException
	 * @throws NetworkNotException
	 * @throws NetworkTimeoutException
	 * @throws NetworkForceCloseException
	 */
	public String doPostFile(String url, String filePath)
			throws NetworkConnectException, NetworkNotException,
			NetworkTimeoutException, NetworkForceCloseException {
		org.apache.commons.httpclient.HttpClient httpClient = null;
		PostMethod postMethod = null;

		if (isCancelled)
			throw new NetworkForceCloseException();

		try {
			httpClient = new org.apache.commons.httpclient.HttpClient();

			// 设置超时时间
			HttpConnectionManagerParams managerParams = httpClient
					.getHttpConnectionManager().getParams();
			// 设置连接超时时间(单位毫秒)
			managerParams.setConnectionTimeout(TIMEOUT);
			// 设置读数据超时时间(单位毫秒)
			managerParams.setSoTimeout(TIMEOUT);

			// 加入代理
			NetworkInfo networkInfo = Tools.getNetworkInfo();
			if (!networkInfo.isConnectToNetwork()) {
				throw new NetworkNotException();
			}

			if (networkInfo.isProxy()) {
				HostConfiguration hostConfiguration = new HostConfiguration();
				URI uri = new URI(url, true);
				hostConfiguration.setHost(uri);
				httpClient.setHostConfiguration(hostConfiguration);
				httpClient.getHostConfiguration().setProxy(
						networkInfo.getProxyHost(), networkInfo.getProxyPort());
			}

			File file = new File(filePath);
			Part[] parts = { new FilePart(file.getName(), file) };

			postMethod = new PostMethod(url);
			postMethod.setRequestEntity(new MultipartRequestEntity(parts,
					postMethod.getParams()));
			Header[] headers = getHeaders(null);
			for (int i = 0; i < headers.length; i++) {
				postMethod.setRequestHeader(headers[i].getName(),
						headers[i].getValue());
			}

			if (isCancelled)
				throw new NetworkForceCloseException();

			httpClient.executeMethod(postMethod);

			if (isCancelled)
				throw new NetworkForceCloseException();

			int statusCode = postMethod.getStatusCode();
			if (statusCode == HttpStatus.SC_OK) {
				InputStream is = postMethod.getResponseBodyAsStream();

				org.apache.commons.httpclient.Header[] header = postMethod
						.getResponseHeaders();
				for (int i = 0; i < header.length; i++) {
					String values = header[i].getValue();
					if (values.indexOf("gzip") != -1) {
						is = new GZIPInputStream(is);
					}
				}

				byte[] data = ToolsFile.readBytes(is);
				is.close();

				if (isCancelled)
					throw new NetworkForceCloseException();

				String response = new String(data, HTTP.UTF_8);
				return response;
			} else {
				throw new NetworkConnectException(String.valueOf(statusCode));
			}
		} catch (NetworkForceCloseException e) {
			throw new NetworkForceCloseException();
		} catch (NetworkNotException e) {
			throw new NetworkNotException();
		} catch (SocketTimeoutException e) {
			throw new NetworkTimeoutException();
		} catch (NetworkConnectException e) {
			throw new NetworkConnectException(e.getMessage());
		} catch (Exception e) {
			throw new NetworkConnectException();
		} finally {
			if (postMethod != null) {
				postMethod.releaseConnection();
			}
		}
	}

}
