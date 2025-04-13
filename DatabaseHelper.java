package com.example.gear_guardian;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    // Database Info
    private static final String DATABASE_NAME = "gear_guardian.db";
    private static final int DATABASE_VERSION = 1;

    // Maintenance Table Info
    public static final String TABLE_MAINTENANCE = "maintenance";
    public static final String COLUMN_MAINTENANCE_ID = "id";
    public static final String COLUMN_SERVICE = "service";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_COST = "cost";

    // Vehicle Table Info
    public static final String TABLE_VEHICLE = "vehicle";
    public static final String COLUMN_VEHICLE_ID = "id";
    public static final String COLUMN_VEHICLE_NAME = "name";
    public static final String COLUMN_VEHICLE_MAKE = "make";
    public static final String COLUMN_VEHICLE_MODEL = "model";
    public static final String COLUMN_VEHICLE_YEAR = "year";
    public static final String COLUMN_SERVICE_INTERVAL = "interval";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create Maintenance Table
        String createMaintenanceTable = "CREATE TABLE " + TABLE_MAINTENANCE + " ("
                + COLUMN_MAINTENANCE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_SERVICE + " TEXT, "
                + COLUMN_DATE + " TEXT, "
                + COLUMN_COST + " REAL)";
        db.execSQL(createMaintenanceTable);

        // Create Vehicle Table
        String createVehicleTable = "CREATE TABLE " + TABLE_VEHICLE + " ("
                + COLUMN_VEHICLE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_VEHICLE_NAME + " TEXT, "
                + COLUMN_VEHICLE_MAKE + " TEXT, "
                + COLUMN_VEHICLE_MODEL + " TEXT, "
                + COLUMN_VEHICLE_YEAR + " INTEGER, "
                + COLUMN_SERVICE_INTERVAL + " TEXT)";
        db.execSQL(createVehicleTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older tables if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MAINTENANCE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VEHICLE);
        onCreate(db);
    }

    // Insert a new maintenance record
    public boolean insertRecord(String service, String date, double cost) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_SERVICE, service);
        values.put(COLUMN_DATE, date);
        values.put(COLUMN_COST, cost);

        long result = db.insert(TABLE_MAINTENANCE, null, values);
        return result != -1;
    }

    // Retrieve all maintenance records
    public Cursor getAllRecords() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_MAINTENANCE, null);
    }

    // Insert a new vehicle record
    public boolean insertVehicle(String name, String make, String model, int year, String serviceInterval) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_VEHICLE_NAME, name);
        values.put(COLUMN_VEHICLE_MAKE, make);
        values.put(COLUMN_VEHICLE_MODEL, model);
        values.put(COLUMN_VEHICLE_YEAR, year);
        values.put(COLUMN_SERVICE_INTERVAL, serviceInterval);

        long result = db.insert(TABLE_VEHICLE, null, values);
        return result != -1;
    }

    // Retrieve all vehicles
    public Cursor getAllVehicles() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_VEHICLE, null);
    }
}
