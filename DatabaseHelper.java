package com.example.gear_guardian;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME    = "gear_guardian.db";
    private static final int    DATABASE_VERSION = 4;  // bump when schema changes

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        // Enable foreign-key constraints for cascade deletes
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 1. Vehicle table
        db.execSQL(
                "CREATE TABLE vehicle (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "name TEXT NOT NULL, " +
                        "make TEXT, " +
                        "model TEXT, " +
                        "year INTEGER, " +
                        "service_interval TEXT)"
        );
        // 2. Maintenance table with FK → vehicle.id
        db.execSQL(
                "CREATE TABLE maintenance (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "vehicle_id INTEGER NOT NULL, " +
                        "service TEXT NOT NULL, " +
                        "FOREIGN KEY(vehicle_id) REFERENCES vehicle(id) ON DELETE CASCADE)"
        );

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop both tables and recreate (simple migration strategy)
        db.execSQL("DROP TABLE IF EXISTS maintenance");
        db.execSQL("DROP TABLE IF EXISTS vehicle");
        onCreate(db);
    }

    /** Inserts a new vehicle into the database. */
    public boolean insertVehicle(String name, String make,
                                 String model, int year,
                                 String serviceInterval) {
        SQLiteDatabase db = getWritableDatabase();  // opens or creates DB :contentReference[oaicite:0]{index=0}
        ContentValues cv = new ContentValues();
        cv.put("name", name);
        cv.put("make", make);
        cv.put("model", model);
        cv.put("year", year);
        cv.put("service_interval", serviceInterval);
        long id = db.insert("vehicle", null, cv);
        return id != -1;
    }

    /** Retrieves all vehicles. */
    public Cursor getAllVehicles() {
        return getReadableDatabase().rawQuery(
                "SELECT id, name, make, model, year, service_interval FROM vehicle",
                null
        );
    }

    /** Inserts a maintenance record tied to a vehicle. */
    public boolean insertMaintenance(int vehicleId, String service) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("vehicle_id", vehicleId);
        cv.put("service", service);
        long id = db.insert("maintenance", null, cv);
        return id != -1;
    }

    /** Retrieves all maintenance entries for the given vehicle. */
    public Cursor getMaintenanceForVehicle(int vehicleId) {
        return getReadableDatabase().rawQuery(
                "SELECT id, service FROM maintenance WHERE vehicle_id = ?",
                new String[]{ String.valueOf(vehicleId) }
        );
    }
}
