package com.example.gearguardian;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";

    private static final String DATABASE_NAME    = "gear_guardian.db";
    private static final int    DATABASE_VERSION = 4;

    // VEHICLE table
    public static final String TABLE_VEHICLE           = "vehicle";
    public static final String COLUMN_VEHICLE_ID       = "id";
    public static final String COLUMN_VEHICLE_NAME     = "name";
    public static final String COLUMN_VEHICLE_MAKE     = "make";
    public static final String COLUMN_VEHICLE_MODEL    = "model";
    public static final String COLUMN_VEHICLE_YEAR     = "year";
    public static final String COLUMN_SERVICE_INTERVAL = "service_interval";

    // MAINTENANCE table
    public static final String TABLE_MAINTENANCE     = "maintenance";
    public static final String COLUMN_MAINT_ID       = "id";
    public static final String COLUMN_MAINT_VEHICLE  = "vehicle_id";
    public static final String COLUMN_MAINT_SERVICE  = "service";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
        Log.d(TAG, "Foreign keys enabled.");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE " + TABLE_VEHICLE + " (" +
                        COLUMN_VEHICLE_ID   + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_VEHICLE_NAME + " TEXT NOT NULL, " +
                        COLUMN_VEHICLE_MAKE + " TEXT, " +
                        COLUMN_VEHICLE_MODEL+ " TEXT, " +
                        COLUMN_VEHICLE_YEAR + " INTEGER, " +
                        COLUMN_SERVICE_INTERVAL + " TEXT" +
                        ")"
        );

        db.execSQL(
                "CREATE TABLE " + TABLE_MAINTENANCE + " (" +
                        COLUMN_MAINT_ID      + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_MAINT_VEHICLE + " INTEGER NOT NULL, " +
                        COLUMN_MAINT_SERVICE + " TEXT NOT NULL, " +
                        "FOREIGN KEY(" + COLUMN_MAINT_VEHICLE + ") REFERENCES " +
                        TABLE_VEHICLE + "(" + COLUMN_VEHICLE_ID + ") ON DELETE CASCADE" +
                        ")"
        );

        Log.d(TAG, "Tables created.");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MAINTENANCE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VEHICLE);
        onCreate(db);
    }

    public long insertVehicleReturnId(String name,
                                      String make,
                                      String model,
                                      int year,
                                      String serviceInterval) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_VEHICLE_NAME,     name);
        cv.put(COLUMN_VEHICLE_MAKE,     make);
        cv.put(COLUMN_VEHICLE_MODEL,    model);
        cv.put(COLUMN_VEHICLE_YEAR,     year);
        cv.put(COLUMN_SERVICE_INTERVAL, serviceInterval);
        long id = db.insert(TABLE_VEHICLE, null, cv);
        Log.d(TAG, "Inserted vehicle with ID: " + id);
        return id;
    }

    public long insertMaintenance(int vehicleId, String service) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_MAINT_VEHICLE, vehicleId);
        cv.put(COLUMN_MAINT_SERVICE, service);
        long result = db.insert(TABLE_MAINTENANCE, null, cv);
        if (result == -1) {
            Log.e(TAG, "Failed to insert maintenance for vehicle ID " + vehicleId + ". Possibly invalid foreign key.");
        } else {
            Log.d(TAG, "Inserted maintenance with ID: " + result);
        }
        return result;
    }

    public Cursor getAllVehicles() {
        return getReadableDatabase().query(
                TABLE_VEHICLE,
                new String[]{ COLUMN_VEHICLE_ID, COLUMN_VEHICLE_NAME, COLUMN_SERVICE_INTERVAL },
                null, null, null, null, COLUMN_VEHICLE_NAME + " COLLATE NOCASE"
        );
    }

    public Cursor getMaintenanceForVehicle(int vehicleId) {
        return getReadableDatabase().query(
                TABLE_MAINTENANCE,
                new String[]{ COLUMN_MAINT_ID, COLUMN_MAINT_SERVICE },
                COLUMN_MAINT_VEHICLE + " = ?",
                new String[]{ String.valueOf(vehicleId) },
                null, null,
                COLUMN_MAINT_ID + " DESC"
        );
    }

    public void clearAllData() {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            db.delete(TABLE_MAINTENANCE, null, null);
            db.delete(TABLE_VEHICLE, null, null);
            db.setTransactionSuccessful();
            Log.d(TAG, "All data cleared.");
        } finally {
            db.endTransaction();
        }
    }
}
