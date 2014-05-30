package com.darwinsys.contentstacker;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Browser;
import android.util.Log;

public class DemoActivity extends Activity {
	
	private static final String TAG = DemoActivity.class.getSimpleName();
	
	String[] from = {
            Browser.BookmarkColumns.TITLE,
            Browser.BookmarkColumns.URL,
    };


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ContentResolver cr = getContentResolver();
		
		// First set up the StackingContentProvider's configuration: 
		// "insert" the Bookmarks Authority into the Stacker
		ContentValues values = new ContentValues();
		values.put(StackingContentProvider.CONTENT_PROVIDER_AUTHORITY_KEY, 
				Browser.BOOKMARKS_URI.toString());
		final Uri inserted1 = cr.insert(StackingContentProvider.CONTENT_URI, values);
		Log.d(TAG, "First insert is " + inserted1);
		
		// Do it again so we can double up insertions (this is just
		// a contrived demo after all).
		final Uri inserted2 = cr.insert(StackingContentProvider.CONTENT_URI, values);
		Log.d(TAG, "Second insert is " + inserted2);
		
		// Now we do an actual insert
		values.clear();
		values.put(Browser.BookmarkColumns.TITLE, "Test URL 1");
		values.put(Browser.BookmarkColumns.URL, "http://darwinsys.com/");
		final Uri insertedBookmark = cr.insert(StackingContentProvider.CONTENT_URI, values);
		Log.d(TAG, "Bookmark URL is " + insertedBookmark);
		
	}
}
