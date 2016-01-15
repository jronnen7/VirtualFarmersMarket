package com.onnen.virtualfarmersmarket;

import com.onnen.virtualfarmersmarket.utils.MyResult;

public interface IResultHandler {
	int onResult(String result);
	void onError(int resultError);
}
