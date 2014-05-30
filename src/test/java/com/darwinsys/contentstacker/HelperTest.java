package com.darwinsys.contentstacker;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.*;
import android.content.ContentValues;
import android.net.Uri;

@RunWith(RobolectricTestRunner.class)
public class HelperTest {

	StackingContentProvider scp;

	@Before public void setUp() {
		scp = new StackingContentProvider();
	}

	@Test
	public void testSwapAuthority() {
		Uri uri = Uri.parse("content://" + "trot.trot.provider" + "/bee");;
		String auth = "tweedleum.tweedledee.day.provider";
		final Uri swappedAuthority = StackingContentProvider.swapAuthority(uri, auth);
		assertEquals("swapAuth", "content://" + auth + "/bee", swappedAuthority.toString());
	}

	@Test
	public void testRequireAtLeastOneProvider() {
		ContentValues cv = new ContentValues();
		cv.put(StackingContentProvider.CONTENT_PROVIDER_AUTHORITY_KEY, "com.tweedle");
		scp.insert(StackingContentProvider.CONTENT_URI, cv);
		scp.requireAtLeastOneProvider();
		// If we get here w/ no exception, pass!
	}
	
	@Test(expected=IllegalStateException.class)
	public void testRequireAtLeastOneProviderBad() {
		scp.requireAtLeastOneProvider();
	}

	@Test
	public void testIsForMe() {
		assertTrue(
			StackingContentProvider.isForMe(
			StackingContentProvider.CONTENT_URI));
	}
}
