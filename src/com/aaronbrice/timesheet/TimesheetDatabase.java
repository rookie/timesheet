package com.aaronbrice.timesheet;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/** 
 * Handles the Timesheet Database.
 */
public class TimesheetDatabase extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "Timesheet";
    private static final int DATABASE_VERSION = 1;
    private final Context m_context;

    public TimesheetDatabase(Context ctx) {
        super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
        this.m_context = ctx;
    }

    @Override
    public void onCreate(SQLiteDatabase db) throws SQLException {
        String[] sqls = new String[] {
            "CREATE TABLE tasks (_id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT NOT NULL, billable INTEGER)",
            "CREATE TABLE time_entries (_id INTEGER PRIMARY KEY AUTOINCREMENT, task_id INTEGER, start_time TEXT NOT NULL, end_time TEXT)"
        };
        db.beginTransaction();
        try {
            for( String sql : sqls )
                db.execSQL(sql);
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            Log.e("Error creating Timesheet database tables", e.toString());
            throw e;
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int old_version, int new_version)
    {
        // I guess I only have to worry about this if I get to version 2?
    }

    public Cursor getTasks()
    {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query("tasks", new String[] {"_id", "title", "billable"}, null, null, null, null, "billable DESC, _id ASC");
        c.moveToFirst();
        return c;
    }

    public void newTask(String title, boolean billable)
    {
        ContentValues cv = new ContentValues();
        cv.put("title", title);
        cv.put("billable", billable);
        try {
            getWritableDatabase().insert("tasks", null, cv);
        } catch (SQLException e) {
            Log.e("Error adding new task", e.toString());
        }
    }

    public void deleteTask(long task_id)
    {
        try {
            getWritableDatabase().delete("tasks", "_id=?", new String[] {Long.toString(task_id)});
        } catch (SQLException e) {
            Log.e("Error deleting task", e.toString());
        }
    }
}