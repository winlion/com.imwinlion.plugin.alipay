# Cordova Android Alipay Plugin

## Installation

    cordova plugin add com.imwinlion.plugin.alipay

## Use
step1: build signa package

   you should build a data package like this
   
	var dataPackage='_input_charset="utf-8"&body="这是一个测试订单"&notify_url="http://pmall.hunankeji.com/alipay/notify.html"&out_trade_no="cp20151102163121634"&partner="2088911275690703"&payment_type="1"&seller_id="2088911275690703"&service="mobile.securitypay.pay"&subject="这是一个测试订单"&total_fee="0.01"&sign="qBfYhvo9cu%2BoI0ZCRh5RosdJVMtux5FGihEs3Bxyqz2IlwzgHjiU3S%2Fu%2FxaQi8m5BpR2DC151%2FoTEQiBjeyCpE9BPrDsZwonoQKjGo%2F1522dcm3h26z33p4nfd3DDwUyIWK5or%2Bbp%2BItuE6Q9EV7JXYXQ1JVGZ%2F1uFxuUBISzLM%3D"&sign_type="RSA"';
   
   actually, the package should be build in web service ,use php/java/ or c# ..
   
   those web page will help you 
   1、	http://doc.open.alipay.com/doc2/detail?treeId=59&articleId=103663&docType=1
   2、      http://doc.open.alipay.com/doc2/detail?treeId=58&articleId=103591&docType=1 
   
   iwill give you a php demo 
   
   
    api.php 
   
   
			 <?php
			header('Access-Control-Allow-Origin:*');
			header('content-type:application/json;charset=utf8');
			$config = 
				 array (
			    'partner' => '2088911275690703',//合作伙伴ID,需要配置
			    'privatekeypath' => 'J:/pinmall.hunankeji.com/code/pem/rsaprivate.pem',//rsa 私钥地址,需要配置
			    'publickeypath' => 'J:/code/pem/public.pem',//支付宝 rsa 公钥地址,需要配置
			    'cacertpath' => 'J:/code/pem/cacert.pem',//证书地址,需要配置
			    'signtype' => 'RSA',//采用rsa加密,无需配置
			    'inputcharset' => 'utf-8',//采用utf-8 编码,也可以是gbk
			    'transport' => 'http',//
			    'notifyurl' => 'http://pmall.hunankeji.com/alipay/notify.html',//支付成功后,支付宝
			  );
			
			/*如下是订单信息
			实际是从数据库查出来的,这里只是模拟
			*/
			$orderInfo = array(
			"orderno"=>"",
			"subject"=>"这是商品描述",
			"body"=>"这是交易描述",
			"total_fee"=>"0.01",//这是交易金额,单位元
			);
			$orderInfo["orderno"] = date("YmdHis",time()) . mt_rand(10000,99999);
			
			//如下拼接支付数据包,
			//注意双引号不能去掉
			
			$pakage["partner"] = "\"".$config["partner"]."\"";
			$pakage["seller_id"] = "\"".$config["partner"]."\"";
			$pakage["out_trade_no"] = "\"".$orderInfo["orderno"] ."\"";
			$pakage["subject"] = "\"".$orderInfo["subject"] ."\"";
			$pakage["body"] = "\"".$orderInfo["body"] ."\"";
			$pakage["total_fee"] = "\"".$orderInfo["total_fee"] . "\"";
			$pakage["notify_url"] = "\"".$config["notifyurl"]."\"";
			$pakage["service"] = "\"mobile.securitypay.pay\"";
			$pakage["payment_type"] = "\""."1"."\"";
			$pakage["_input_charset"] = "\"".$config["inputcharset"]."\"";
			//然后对这个数据包进行排序
			ksort($pakage);
			
			//接着把这个数据包拼接成a=x&b=y 的形式,像下面这样
			//_input_charset="utf-8"&body="这是一个测试订单"&notify_url="http://pmall.hunankeji.com/alipay/notify.html"&out_trade_no="cp20151102163121634"&partner="2088911275690703"&payment_type="1"&seller_id="2088911275690703"&service="mobile.securitypay.pay"&subject="这是一个测试订单"&total_fee="0.01"
			
			
			$retstr = urldecode(http_build_query($pakage));
			
			//接着做签名
			$priKey = file_get_contents ( $config["privatekeypath"] );
			$res = openssl_get_privatekey ( $priKey );
			openssl_sign ( $retstr, $sign, $res );
			openssl_free_key ( $res );
			$sign = base64_encode ( $sign );
			
			//最后再次拼接
			$retstr .= "&sign=" . "\"". urlencode($sign) ."\"";
			$retstr .= "&sign_type="  .  "\"".$config["signtype"]."\"";;
			echo json_encode(array("data"=>$retstr,"status"=>1));
			?>


step2: 
	/*设置成功回调函数
	msg:返回的提示信息
	*/
	alipay.succ = function(msg){
		
		alert(msg);
	}
	/*
	设置成功回调函数
	msg:返回的提示信息
	*/
	alipay.fail = function(msg){
		alert(msg);
	}
	
	/*通过jquery.post 请求 json*/
	var orderno = "这个是订单号";
	$.post(
	"api.php",
	{orderno:orderno},
	function(json){
		if(json.status){
			/*开始支付*/
			 alipay.dopay(json.data);
		}
	})
	
### Supported Platforms
