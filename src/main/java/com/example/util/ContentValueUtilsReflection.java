package com.example.util;

import java.lang.reflect.Field;

import android.content.ContentValues;
import android.database.Cursor;

/** Convert between Data objects and ContentValues using Reflection.
 * Simple form: only handles int-valued fields at present...
 * @author Ian Darwin
 */
public class ContentValueUtilsReflection {
	
	/** This Data subtype is simple, all fields are ints. */
	private static Field[] dataFields;
	
	/** We walk the array a lot, so it's worth pulling out serialVersionUID if it is found, 
	 * so we don't have to string compare to skip it each time.
	 */
	static {
		System.out.println("ContentValueUtilsReflection.<static{}()");
		Field[] fields = Data.class.getDeclaredFields();
		int where = -1;
		for (int i = 0; i < fields.length; i++) {
			if (fields[i].getName().equals("serialVersionUID")) {
				where = i;
				break;
			}
		}
		int n = fields.length - (where >= 0 ? 1 : 0);
		dataFields = new Field[n];
		for (int i = 0, j = 0; i < fields.length; i++) {
			if (i == where) {
				continue;
			}
			dataFields[j++] = fields[i];
		}
	}
	
	/** Extract a Data from the current row of the Cursor */
	public static Data patientDataFromCursor(Cursor cv) {
		Data data = new Data();
		for (Field f : dataFields) {
			try {
				f.setInt(data, cv.getInt(cv.getColumnIndex(f.getName())));
			} catch (Exception e) {
				System.err.println("ContentValueUtilsReflection.DataFromCursor() failed to get field " + f.getName());
				e.printStackTrace();
				break;
			}
		}
		return data;
	}

	/** Extract a Data from the given ContentValues */
	public static Data dataFromContentValues(ContentValues cv) {
		System.out.println("ContentValueUtilsReflection.DataFromContentValues()");
		Data data = new Data();
		for (Field f : dataFields) {
			try {
				Integer value = cv.getAsInteger(f.getName());
				if (value == null) {
					continue;			// try next field
				}
				f.setInt(data, value);
			} catch (Exception e) {
				throw new RuntimeException("ContentValueUtilsReflection.dataFromContentValues() failed to get field " + f.getName());
			}
		}
		return data;
	}

	public static ContentValues contentValuesFromData(Data data) {
		ContentValues cv = new ContentValues();
		for (Field f : dataFields) {
			try {
				int n = f.getInt(data);
				cv.put(f.getName(), n);
			} catch (Exception e) {
				throw new RuntimeException("ContentValueUtilsReflection.contentValuesFromData() failed to get field " + f.getName());
			}
		}
		return cv;
	}
}
