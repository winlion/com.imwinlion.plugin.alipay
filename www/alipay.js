var Alipay = function(){}
Alipay._opts = {};
Alipay.prototype.dopay=function(str)
{
	var data=[];
	data[0] = str;
	cordova.exec(this.succ, this.fail, 'Alipay', "dopay",data);
}

Alipay.prototype.succ=function(msg)
{
	alert(msg);
	return this;
}
Alipay.prototype.fail=function(msg){
	alert(msg);
	return this;
}
module.exports = new Alipay();