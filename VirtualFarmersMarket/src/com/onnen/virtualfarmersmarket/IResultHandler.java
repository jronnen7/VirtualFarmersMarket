package com.onnen.virtualfarmersmarket;

import com.onnen.virtualfarmersmarket.constants.MyResult;

public interface IResultHandler {
	int onResult(String result);
	void onError(int resultError);
}
