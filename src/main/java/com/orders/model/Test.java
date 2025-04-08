package com.orders.model;

import java.util.Hashtable;

import com.ecpay.payment.integration.AllInOne;
import com.ecpay.payment.integration.domain.QueryTradeInfoObj;

public class Test {
	 public static void main (String [] args) {
		 AllInOne all = new AllInOne("");
			QueryTradeInfoObj queryTradeInfoObj = new QueryTradeInfoObj();
			
			String MerchantID = "3002607";
			String tradeNo = "P20T250408202618";
			queryTradeInfoObj.setMerchantID(MerchantID); // 設定商戶ID
			queryTradeInfoObj.setMerchantTradeNo(tradeNo);
			
			String result = all.queryTradeInfo(queryTradeInfoObj);
			System.out.println("查詢結果: " + result);
			
			String[] strArr = result.split("&");
			Hashtable<String, String> ECPayReq = new Hashtable<>();
			
			for(String str : strArr) {
				String[] keyValue = str.split("=", 2);
				String key = keyValue[0];
				String Value = keyValue.length > 1 ?  keyValue[1] : "";
				ECPayReq.put(key, Value);
			}
			System.out.println(ECPayReq.get("TradeStatus"));
	 }
}
