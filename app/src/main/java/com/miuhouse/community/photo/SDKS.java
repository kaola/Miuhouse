package com.miuhouse.community.photo;

import android.annotation.TargetApi;
import android.view.View;

@TargetApi(16)
public class SDKS {

	public static void postOnAnimation(View view, Runnable r) {
		view.postOnAnimation(r);
	}
	
}
