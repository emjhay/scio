package com.scio.database;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;


public class DataBaseManager extends SQLiteOpenHelper {

	// The Android's default system path of your application database.
	// data/data/ and /databases remain the same always. The one that must be
	// changed is com.example which represents
	// the MAIN package of your project
	private static String DB_PATH = Environment.getDataDirectory()
			+ "/data/com.scio.ui/databases/";

	// the name of your database
	private static String DB_NAME = "database.sqlite";

	private static SQLiteDatabase mDataBase;

	private static DataBaseManager sInstance = null;
	// database version
	private static final int DATABASE_VERSION = 2;
	private static Context mContext;

	/**
	 * Constructor Takes and keeps a reference of the passed context in order to
	 * access to the application assets and resources.
	 */
	private DataBaseManager(Context context) {
		super(context, DB_NAME, null, DATABASE_VERSION);
		mContext = context;

		try {
			createDataBase();
			openDataBase();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Singleton for DataBase
	 * 
	 * @return singleton instance
	 */
	public static DataBaseManager instance(Context context) {

		if (sInstance == null) {
			sInstance = new DataBaseManager(context);
		}
		return sInstance;
	}


	/**
	 * Creates a empty database on the system and rewrites it with your own
	 * database.
	 * 
	 * @throws java.io.IOException
	 *             io exception
	 */
	private void createDataBase() throws IOException {

		boolean dbExist = checkDataBase();

		if (dbExist) {
			// do nothing - database already exist
			// mDataBase.execSQL(SCRIPT_DATABASE_CREATE);
			// int qtdeScripts = SCRIPT_DATABASE_CREATE.length;
			// System.out.println("qtdeScripts====len======="+qtdeScripts);
			// // Executa cada sql passado como par�metro
			// for (int i = 0; i < qtdeScripts; i++) {
			// String sql = SCRIPT_DATABASE_CREATE[i];
			// System.out.println("sql=============="+sql);
			// // Cria o banco de dados executando o script de cria��o
			// }
		} else {

			// By calling this method an empty database will be created into
			// the default system path
			// of your application so we are gonna be able to overwrite that
			// database with our database.
			this.getReadableDatabase();

			try {

				copyDataBase();

			} catch (IOException e) {

				throw new Error("Error copying database");
			}
		}
	}

	/**
	 * Check if the database already exist to avoid re-copying the file each
	 * time you open the application.
	 * 
	 * @return true if it exists, false if it doesn't
	 */
	private boolean checkDataBase() {

		SQLiteDatabase checkDB = null;

		try {

			String myPath = DB_PATH + DB_NAME;
			checkDB = SQLiteDatabase.openDatabase(myPath, null,
					SQLiteDatabase.OPEN_READWRITE);
			// checkDB = SQLiteDatabase.openOrCreateDatabase(myPath, null);

		} catch (SQLiteException e) {

			// database doesn't exist yet.

		}

		if (checkDB != null) {

			checkDB.close();

		}

		return checkDB != null;
	}

	/**
	 * Copies your database from your local assets-folder to the just created
	 * empty database in the system folder, from where it can be accessed and
	 * handled. This is done by transfering bytestream.
	 * 
	 * @throws java.io.IOException
	 *             io exception
	 */
	public void copyDataBase() throws IOException {

		// Open your local db as the input stream
		InputStream myInput = mContext.getAssets().open(DB_NAME);

		// Path to the just created empty db
		String outFileName = DB_PATH + DB_NAME;

		// Open the empty db as the output stream
		OutputStream myOutput = new FileOutputStream(outFileName);

		// transfer bytes from the inputfile to the outputfile
		byte[] buffer = new byte[1024];
		int length;
		while ((length = myInput.read(buffer)) > 0) {
			myOutput.write(buffer, 0, length);
		}

		// Close the streams
		myOutput.flush();
		myOutput.close();
		myInput.close();

	}

	private void openDataBase() throws SQLException {

		// Open the database
		String myPath = DB_PATH + DB_NAME;
		mDataBase = SQLiteDatabase.openDatabase(myPath, null,
				SQLiteDatabase.OPEN_READWRITE);


	}
	
	

	public boolean isTableExists(String tableName, boolean openDb) {
		if (openDb) {
			if (mDataBase == null || !mDataBase.isOpen()) {
				mDataBase = getReadableDatabase();
			}

			if (!mDataBase.isReadOnly()) {
				mDataBase.close();
				mDataBase = getReadableDatabase();
			}
		}

		Cursor cursor = mDataBase.rawQuery(
				"select DISTINCT tbl_name from sqlite_master where tbl_name = '"
						+ tableName + "'", null);
		if (cursor != null) {
			if (cursor.getCount() > 0) {
				cursor.close();
				return true;
			}
			cursor.close();
		}
		return false;
	}

	/**
	 * Select method
	 * 
	 * @param query
	 *            select query
	 * @return - Cursor with the results
	 * @throws android.database.SQLException
	 *             sql exception
	 */
	public Cursor select(String query) throws SQLException {
		return mDataBase.rawQuery(query, null);
	}

	public Cursor query(String table, String[] columns) throws SQLException {
		return mDataBase.query(table, columns, null, null, null, null, null);
	}

	/**
	 * Insert method
	 * 
	 * @param table
	 *            - name of the table
	 * @param values
	 *            values to insert
	 * @throws android.database.SQLException
	 *             sql exception
	 */
	public void insert(String table, ContentValues values) throws SQLException {
		mDataBase.insert(table, null, values);
	}

	/**
	 * Delete method
	 * 
	 * @param table
	 *            - table name
	 * @param where
	 *            WHERE clause, if pass null, all the rows will be deleted
	 * @throws android.database.SQLException
	 *             sql exception
	 */
	public void delete(String table, String where) throws SQLException {

		mDataBase.delete(table, where, null);

	}

	/**
	 * Update method
	 * 
	 * @param table
	 *            - table name
	 * @param values
	 *            - values to update
	 * @param where
	 *            - WHERE clause, if pass null, all rows will be updated
	 */
	public void update(String table, ContentValues values) {
		mDataBase.update(table, values, null, null);
	}


	/**
	 * Let you make a raw query
	 * 
	 * @param command
	 *            - the sql comand you want to run
	 */
	public void sqlCommand(String command) {
		mDataBase.execSQL(command);
	}

	@Override
	public synchronized void close() {

		if (mDataBase != null)
			mDataBase.close();

		super.close();

	}

	@Override
	public void onCreate(SQLiteDatabase db) {

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
