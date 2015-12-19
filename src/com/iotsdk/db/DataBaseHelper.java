package com.iotsdk.db;

import java.io.File;
import java.util.List;

import com.iostdk.utils.Utils;
import com.iotsdk.config.SdkConfig;
import com.iotsdk.error.IotSdkException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DataBaseHelper {
	public class NuageDataOpenHelper {

		private  String nuageDbPath = SdkConfig.IOSDK_SD_DB_DEFAULT_PATH;
		private final String sqlCreate = "CREATE TABLE IF NOT EXISTS ";

		// for tab and tab's Column like tabs.put(tabname,
		// "(TagId INTEGER PRIMARY KEY, Name TEXT)")
		private List<IotTab> tabs;
		private String name;
		private int version;
		private SQLiteDatabase mDb;
		private Context context;
		public static final boolean useSdcard = true;

		/**
		 * 
		 * @param name
		 *            table name
		 * @param version
		 *            the version of the database
		 * @param tabs
		 *            for tab and tab's Column like
		 * @throws SdcardNotAvailableException
		 */
		public NuageDataOpenHelper(String name, int version, List<IotTab> tabs,
				Context context) {
			this.context = context;
			this.name = name;
			this.version = version;
			this.tabs = tabs;

			createDBpathIfNotExist(nuageDbPath);
		}

		public SQLiteDatabase getWritableDatabase() throws IotSdkException {
			// long t = System.currentTimeMillis();
			if (useSdcard)
				Utils.checkSdcard();

			if (mDb != null && mDb.isOpen() && !mDb.isReadOnly()) {
				return mDb;
			} else {
				safeclose();
			}

			createDBpathIfNotExist(nuageDbPath);

			if (useSdcard)
				mDb = SQLiteDatabase.openDatabase(nuageDbPath + File.separator
						+ name, null, SQLiteDatabase.CREATE_IF_NECESSARY
						| SQLiteDatabase.OPEN_READWRITE);
			else
				mDb = context.openOrCreateDatabase(name, 0, null);

			if (mDb.getVersion() == 0) {
				createTabs();
			} else if (mDb.getVersion() != version) {
				update();
			}
			// Log.v("NuageDB", "DBOPENDb:" + (System.currentTimeMillis() - t));
			return mDb;
		}

		public SQLiteDatabase getReadAbleDatabase() throws IotSdkException {
			Utils.checkSdcard();

			if (mDb != null && mDb.isOpen() && !mDb.isReadOnly()) {
				return mDb;
			} else {
				safeclose();
			}

			createDBpathIfNotExist(nuageDbPath);

			if (useSdcard)
				mDb = SQLiteDatabase.openDatabase(nuageDbPath + File.separator
						+ name, null, SQLiteDatabase.OPEN_READONLY);
			else
				mDb = context.openOrCreateDatabase(name, 0, null);
			return mDb;
		}

		private void createTabs() {
			if (tabs != null && !tabs.isEmpty()) {
				for (IotTab tab : tabs) {
					mDb.execSQL(sqlCreate + tab.getTabName() + tab.getColumns());
					if (tab.getIndexs() != null) {
						StringBuffer sqlbuf = new StringBuffer();
						for (int i = 0; i < tab.getIndexs().length; i++) {
							sqlbuf.setLength(0);
							sqlbuf.append("CREATE INDEX IF NOT EXISTS ")
									.append("index_").append(tab.getTabName())
									.append(i).append(" on ")
									.append(tab.getTabName())
									.append(tab.getIndexs()[i]);
							Log.v("NuageDataOpenHelper", new String(sqlbuf));
							mDb.execSQL(new String(sqlbuf));
						}
					}
				}
			}
			mDb.setVersion(version);
		}

		private void update() {
			if (tabs != null && !tabs.isEmpty()) {
				for (IotTab tab : tabs) {
					mDb.execSQL("DROP TABLE IF EXISTS " + tab.getTabName());
				}
			}
			createTabs();
		}

		public void close() {
			safeclose();
		}

		private void safeclose() {
			if (mDb != null && mDb.isOpen()) {
				mDb.close();
			}
			mDb = null;
		}

		private void createDBpathIfNotExist(String path) {
			if (useSdcard) {
				File filePath = new File(path);
				if (!filePath.exists()) {
					filePath.mkdirs();
				}
			}
		}
	}

}
