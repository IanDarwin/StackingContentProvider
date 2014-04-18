package org.ehealthinnovation.contentstacker;

import java.util.ArrayList;
import java.util.List;

import android.annotation.TargetApi;
import android.content.ContentProvider;
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
 * comments on each method for how things play out in thr multi-provider arena.
 * 
 * In general, <b>order matters</b> in the following senses:
 * <li>The URI returned by the first CP's insert() method becomes my return value;</li>
 * @author Ian Darwin, darwinsys.com
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class StackingContentProvider extends ContentProvider {

	List<ContentProvider> providers = new ArrayList<ContentProvider>();
	
	/**
	 * The only constructor; we can't have a constructor that takes
	 * a ContentProvider... argument because Android wants to instantiate
	 * the CP itself.
	 */
	public StackingContentProvider() {
		// empty
	}
	
	// List methods
	
	/** Adds a Content Provider to the list, maintaining the general contract for List.add().
	 * @param cp
	 * @return
	 */
	public boolean addProvider(ContentProvider cp) {
		return providers.add(cp);
	}
	
	/** Adds a Content Provider to the list, maintaining the general contract for List.insert().
	 * @param cp
	 * @return
	 */
	public ContentProvider addProvider(int location, ContentProvider cp) {
		return providers.set(location, cp);
	}
	
	/** Removes a Content Provider from the list, maintaining the general contract for List.remove().
	 * @param cp
	 * @return
	 */
	public boolean removeProvider(ContentProvider cp) {
		return providers.remove(cp);
	}
	
	// Lifecycle Methods
	
	@Override
	public boolean onCreate() {
		// I don't believe we need to do anything here.
		return true;
	}

	// Delegation Methods

	/** Get the type from the FIRST provider. */
	@Override
	public String getType(Uri uri) {
		return providers.get(0).getType(uri);
	}
	
	// CREATE

	/** Insert the values at the URI in EACH Content Provider; returns the URI for the FIRST one. */
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		requireAtLeastOneProvider();
		Uri ret = null;
		for (ContentProvider cp : providers) {
			final Uri inserted = cp.insert(uri, values);
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
		for (ContentProvider cp : providers) {
			final Cursor results = cp.query(uri, projection, selection, selectionArgs, sortOrder);
			if (ret != null)
				ret = results;
		};
		return ret;
	}
	
	// UPDATE

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		requireAtLeastOneProvider();
		int ret = -1;
		for (ContentProvider cp : providers) {
			final int results = cp.update(uri, values, selection, selectionArgs);
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
		for (ContentProvider cp : providers) {
			final int results = cp.delete(uri, selection, selectionArgs);
			if (ret != -1)
				ret = results;
		};
		return ret;
	}
	
	// Helperz
	
	private void requireAtLeastOneProvider() {
		if (providers.isEmpty()) {
			throw new IllegalStateException("Must have at least one stacked Content Provider before calling action methods");
		}
	}
}
