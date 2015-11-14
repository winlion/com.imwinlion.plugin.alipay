package com.imwinlion.plugin.alipay;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.alipay.sdk.app.PayTask;

public class AlipayPlugin extends CordovaPlugin {
  public static final String LOG_TAG = "AlipayPlugin";
  private static final int SDK_PAY_FLAG = 1;

  private static final int SDK_CHECK_FLAG = 2;


  public Activity activity;
  public Context content;
  public CallbackContext callback_context;
  private String orderInfo;
  private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SDK_PAY_FLAG: {
				PayResult payResult = new PayResult((String) msg.obj);

				

				String resultStatus = payResult.getResultStatus();

				// 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
				if (TextUtils.equals(resultStatus, "9000")) {
					cbSuccess("支付成功");
				} else {
					// 判断resultStatus 为非“9000”则代表可能支付失败
					// “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
					if (TextUtils.equals(resultStatus, "8000")) {
						cbSuccess("支付结果确认中");
						

					} else {
						
						cbFail("支付失败");
					}
				}
				break;
			}
			case SDK_CHECK_FLAG: {
				cbFail(msg.obj.toString());
				
				break;
			}
			default:
				cbFail("发生未知错误");
				break;
			}
		};
	};;
  public boolean execute(String action, JSONArray args, CallbackContext callbackContext)  {
	  
		try {
			activity = cordova.getActivity();
			content = cordova.getActivity().getApplicationContext();
			callback_context = callbackContext;
			
			 if("dopay".equals(action)){
				orderInfo = args.getString(0);
				Runnable payRunnable = new Runnable() {
					@Override
					public void run() {
						// 构造PayTask 对象
						PayTask alipay = new PayTask(activity);
						// 调用支付接口，获取支付结果
						String result = alipay.pay(orderInfo);

						Message msg = new Message();
						msg.what = SDK_PAY_FLAG;
						msg.obj = result;
						mHandler.sendMessage(msg);
					}
				};

				// 必须异步调用
				Thread payThread = new Thread(payRunnable);
				payThread.start();
				
				
				
			};
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			cbFail(e.toString());
		}
	
   
    return true;
  }
  
  

  
  /**
   * 
  * @Description: 统一回掉函数 
   * @param status
   * @param msg      
   * @return: void      
   * @throws 
   * @author: winlion@hu    
   * @date:   2015-11-13 下午2:21:37   
   * @version V1.0
   */
  private void cbreturn(PluginResult.Status status,String msg){
	  PluginResult pluginResult = new PluginResult(status,msg );
      pluginResult.setKeepCallback(true);
      callback_context.sendPluginResult(pluginResult);
  }
  
  private void cbSuccess(String msg){
	  cbreturn(PluginResult.Status.OK,msg);
  }
  private void cbFail(String msg){
	  cbreturn(PluginResult.Status.ERROR,msg);
  }
}
