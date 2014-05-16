package com.example.util;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import android.content.ContentValues;

@RunWith(RobolectricTestRunner.class)
public class ContentValuesUtilsReflectionTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test @Ignore // Cursor is harder to mock - need Mockito
	public void testPatientDataFromCursor() {
		fail("Not yet implemented");
	}

	@Test
	public void testDataFromContentValues() {
		ContentValues cv = new ContentValues();
		cv.put("minimum", -1);
		cv.put("maximum", 100);
		cv.put("average", 57);
		final Data data = ContentValueUtilsReflection.dataFromContentValues(cv);
		assertEquals(-1, data.minimum);
		assertEquals(100, data.maximum);
		assertEquals(57, data.average);
	}

	@Test @Ignore // no time today
	public void testContentValuesFromData() {
		fail("Not yet implemented");
	}

}
