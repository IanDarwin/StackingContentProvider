package com.darwinsys.contentstacker;

import java.util.ArrayList;
import java.util.List;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;

/** 
 * Provide stacking of Content Providers so that, e.g., data may be saved in multiple providers
 * such as one which saves in a local SQLite3 datastore and a second which backs it onto a
 * cloud or server-based database. 
 * 
 * The CP model doesn't "scale" well to stackability, but we do the best we can; please read the
 * comments on each method for how things play out in the multi-provider arena.
 * 
 * In general, <b>order matters</b> in the following senses:
 * <li>The URI returned by the first CP's insert() method becomes my return value;</li>
 * @author Ian Darwin, darwinsys.com
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class StackingContentProvider extends ContentProvider {

	public static final String CONTENT_PROVIDER_AUTHORITY = "CONTENT_PROVIDER_AUTHORITY";

	public static final String AUTHORITY =
			"com.darwinsys.contentstacker";
	public static final Uri CONTENT_URI =
			Uri.parse("content://" + AUTHORITY + "/");
	
	List<String> authorities = new ArrayList<String>();
	ContentResolver resolver;
	
	// List methods
	
	/** Adds a Content Provider to the list, maintaining the general contract for List.add().
	 * @param cp
	 * @return
	 */
	public boolean addProvider(String cp) {
		return authorities.add(cp);
	}
	
	/** Adds a Content Provider to the list, maintaining the general contract for List.set().
	 * @param cp
	 * @return
	 */
	public String addProvider(int location, String cp) {
		return authorities.set(location, cp);
	}
	
	/** Removes a Content Provider from the list, maintaining the general contract for List.remove().
	 * @param cp
	 * @return
	 */
	public boolean removeProvider(String cp) {
		return authorities.remove(cp);
	}
	
	// Helper methods
	
	/** Replace MY authority part of a URI with the designated CP's */
	static Uri swapAuthority(Uri uri, String cp) {
		String ssp = uri.getPath().replaceFirst("[^.]*/", cp);
		Uri ret = Uri.fromParts("content://", ssp, null);
		return ret;
	}
	
	protected void requireAtLeastOneProvider() {
		if (authorities.isEmpty()) {
			throw new IllegalStateException("Must have at least one stacked Content Provider before calling action methods");
		}
	}
	
	protected static boolean isForMe(Uri uri) {
		return uri.getAuthority().equals(AUTHORITY);
	}
	
	// Lifecycle Methods
	
	@Override
	public boolean onCreate() {
		// getContext is valid "after onCreate has been called", i.e., here
		resolver = getContext().getContentResolver();
		return true;
	}

	// Delegation Methods

	/** Get the type from the FIRST provider. */
	@Override
	public String getType(Uri uri) {
		return getContext().getContentResolver().getType(uri);
	}
	
	// CREATE

	/** Insert the values at the URI in EACH Content Provider; returns the URI for the FIRST one. */
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		/**
		 * If the authority is for us, add it to our list.
		 * we can't have a constructor that takes
		 * a ContentProvider... argument because Android wants to instantiate
		 * the CP itself.
		 */
		if (isForMe(uri)) {
			String auth = values.getAsString(CONTENT_PROVIDER_AUTHORITY);
			if (!addProvider(auth)) {
				System.err.println("Already added " + auth);
			}
			return ContentUris.withAppendedId(CONTENT_URI, authorities.lastIndexOf(auth));
		}
		requireAtLeastOneProvider();
		Uri ret = null;
		for (String cp : authorities) {
			final Uri insertUri = swapAuthority(uri, cp);
			final Uri inserted = resolver.insert(insertUri, values);
			if (ret != null)
				ret = inserted;
		};
		return ret;
	}
	
	// INSERT

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		requireAtLeastOneProvider();
		Cursor ret = null;
		for (String cp : authorities) {
			final Cursor results = 
					resolver.query(swapAuthority(uri, cp), projection, selection, selectionArgs, sortOrder);
			if (ret != null) {
				ret = results;
			}
		};
		return ret;
	}
	
	// UPDATE

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		requireAtLeastOneProvider();
		int ret = -1;
		for (String cp : authorities) {
			final int results = resolver.update(swapAuthority(uri, cp), values, selection, selectionArgs);
			if (ret != -1)
				ret = results;
		};
		return ret;
	}
	
	// DELETE
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		requireAtLeastOneProvider();
		int ret = -1;
		for (String cp : authorities) {
			final int results = resolver.delete(swapAuthority(uri, cp), selection, selectionArgs);
			if (ret != -1)
				ret = results;
		};
		return ret;
	}

}
