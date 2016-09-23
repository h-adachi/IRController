package hadachi.com.ircontroller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by hide on 2016/09/23.
 */

public class SQLHelper extends SQLiteOpenHelper
{
    public static final String FIELD_ID = "_id";
    public static final String FIELD_TYPE = "type";
    public static final String FIELD_NAME = "name";
    public static final String FIELD_CUSTOM = "custom";
    public static final String FIELD_DEVICE = "device_id";
    public static final String FIELD_DATA = "data";

    public static final String TABLE_DEVICE = "devices";
    public static final String TABLE_COMMAND = "commands";

    private static final String DB_NAME = "ir.db";
    private static final int VER = 1;
    private static final String CreateDeviceTableString = "create table devices(_id integer primary key autoincrement, type text, custom text, name text)";
    private static final String CreateCommandTableString = "create table commands(_id integer primary key autoincrement, device_id integer, name text, data text)";

    public SQLHelper(Context context)
    {
        super(context, DB_NAME, null, VER);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(CreateDeviceTableString);
        db.execSQL(CreateCommandTableString);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {

    }

    public void CommitDevice(DeviceInfo di)
    {
        if(di.id == -1)AppendDevice(di);
        else UpdateDevice(di);
    }

    private void AppendDevice(DeviceInfo di)
    {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FIELD_TYPE, di.Type);
        values.put(FIELD_CUSTOM, di.Costomer);
        values.put(FIELD_NAME, di.Name);
        db.insert(TABLE_DEVICE, null, values);
    }

    private void UpdateDevice(DeviceInfo di)
    {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FIELD_TYPE, di.Type);
        values.put(FIELD_CUSTOM, di.Costomer);
        values.put(FIELD_NAME, di.Name);
        db.update(TABLE_DEVICE, values, FIELD_ID + " = ?", new String[]{String.valueOf(di.id)});
    }

    public void DeleteDevice(int id)
    {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_COMMAND, FIELD_DEVICE + " = ?", new String[]{String.valueOf(id)});
        db.delete(TABLE_DEVICE, FIELD_ID + " = ?", new String[]{String.valueOf(id)});
    }

    public void CommitCommand(CommandInfo ci)
    {
        if(ci.id == -1)AppendCommand(ci);
        else UpdateCommand(ci);
    }

    private void AppendCommand(CommandInfo ci)
    {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FIELD_DEVICE, ci.Device);
        values.put(FIELD_NAME, ci.Name);
        values.put(FIELD_DATA, ci.Data);
        db.insert(TABLE_COMMAND, null, values);
    }

    private void UpdateCommand(CommandInfo ci)
    {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FIELD_DEVICE, ci.Device);
        values.put(FIELD_NAME, ci.Name);
        values.put(FIELD_DATA, ci.Data);
        db.update(TABLE_COMMAND, values, FIELD_ID + " = ?", new String[]{String.valueOf(ci.id)});
    }

    public void DeleteCommand(int id)
    {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_COMMAND, FIELD_ID + " = ?", new String[]{String.valueOf(id)});
    }

    public ArrayList<DeviceInfo> GetDevice()
    {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_DEVICE, new String[] {FIELD_ID, FIELD_TYPE, FIELD_CUSTOM, FIELD_NAME}, null, null, null, null, null);
        if(cursor == null) return null;
        if(cursor.moveToFirst() == false) return null;

        ArrayList<DeviceInfo> result = new ArrayList<DeviceInfo>();
        do
        {
            DeviceInfo di = new DeviceInfo();
            di.id = cursor.getInt(cursor.getColumnIndex(FIELD_ID));
            di.Type = cursor.getString(cursor.getColumnIndex(FIELD_TYPE));
            di.Costomer = cursor.getString(cursor.getColumnIndex(FIELD_CUSTOM));
            di.Name = cursor.getString(cursor.getColumnIndex(FIELD_NAME));
            result.add(di);
        }while(cursor.moveToNext());
        return  result;
    }

    public ArrayList<CommandInfo> GetCommand(int device)
    {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_COMMAND, new String[] {FIELD_ID, FIELD_DEVICE, FIELD_NAME, FIELD_DATA}, FIELD_DEVICE + " = ?", new String[]{String.valueOf(device)}, null, null, null);
        if(cursor == null) return null;
        if(cursor.moveToFirst() == false) return null;

        ArrayList<CommandInfo> result = new ArrayList<CommandInfo>();
        do
        {
            CommandInfo ci = new CommandInfo();
            ci.id = cursor.getInt(cursor.getColumnIndex(FIELD_ID));
            ci.Device = cursor.getInt(cursor.getColumnIndex(FIELD_DEVICE));
            ci.Name = cursor.getString(cursor.getColumnIndex(FIELD_NAME));
            ci.Data = cursor.getString(cursor.getColumnIndex(FIELD_DATA));
            result.add(ci);
        }while(cursor.moveToNext());
        return  result;
    }
}
