package com.darwinsys.contentstacker;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import android.content.ContentValues;
import android.net.Uri;

public class HelperTests {

	StackingContentProvider scp;

	@Before public void setUp() {
		scp = new StackingContentProvider();
	}

	@Test
	public void testSwapAuthority() {
		Uri uri = null;
		String auth = "tweekdleum.tweedledee.day";
		StackingContentProvider.swapAuthority(uri, auth) ;
	}

	protected void requireAtLeastOneProvider() {
		ContentValues cv = new ContentValues();
		cv.put(StackingContentProvider.CONTENT_PROVIDER_AUTHORITY, "com.tweedle");
		scp.insert(Uri.parse("content://" + StackingContentProvider.CONTENT_PROVIDER_AUTHORITY), cv);
	}

	public void testIsForMe() {
		fail("not written yet");
	}
}
