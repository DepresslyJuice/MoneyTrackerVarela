package com.example.moneytrackervarela.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.moneytrackervarela.models.Categoria;
import com.example.moneytrackervarela.models.Transaccion;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "money_tracker.db";
    private static final int DATABASE_VERSION = 1;

    // Table Names
    private static final String TABLE_CATEGORIES = "categories";
    private static final String TABLE_TRANSACTIONS = "transactions";

    // Common column names
    private static final String KEY_ID = "id";
    private static final String KEY_TIPO = "tipo";
    private static final String KEY_CREATED_AT = "created_at";

    // CATEGORIES Table - column names
    private static final String KEY_CATEGORY_NAME = "nombre";
    private static final String KEY_CATEGORY_ICON = "icon";
    private static final String KEY_CATEGORY_COLOR = "color";

    // TRANSACTIONS Table - column names
    private static final String KEY_TRANSACTION_MONTO = "monto";
    private static final String KEY_TRANSACTION_ID_CATEGORIA = "id_categoria";
    private static final String KEY_TRANSACTION_DESCRIPCION = "descripcion";
    private static final String KEY_TRANSACTION_FECHA = "fecha";
    private static final String KEY_TRANSACTION_PAYMENT_METHOD = "payment_method";


    // Table Create Statements
    private static final String CREATE_TABLE_CATEGORIES = "CREATE TABLE "
            + TABLE_CATEGORIES + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_CATEGORY_NAME + " TEXT," + KEY_TIPO + " TEXT," + KEY_CATEGORY_ICON
            + " TEXT," + KEY_CATEGORY_COLOR + " TEXT" + ")";


    private static final String CREATE_TABLE_TRANSACTIONS = "CREATE TABLE "
            + TABLE_TRANSACTIONS + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_TIPO + " TEXT," + KEY_TRANSACTION_MONTO + " REAL,"
            + KEY_TRANSACTION_ID_CATEGORIA + " INTEGER," + KEY_TRANSACTION_DESCRIPCION
            + " TEXT," + KEY_TRANSACTION_FECHA + " TEXT," + KEY_TRANSACTION_PAYMENT_METHOD
            + " TEXT," + KEY_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP,"
            + " FOREIGN KEY(" + KEY_TRANSACTION_ID_CATEGORIA + ") REFERENCES " + TABLE_CATEGORIES + "(" + KEY_ID + ")" + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        db.execSQL("PRAGMA foreign_keys=ON;");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_CATEGORIES);
        db.execSQL(CREATE_TABLE_TRANSACTIONS);
        populateInitialCategories(db);
    }

    private void populateInitialCategories(SQLiteDatabase db) {
        String[] incomeCategories = {"Salario", "Freelance", "Beca", "Otros"};
        String[] expenseCategories = {"Alimentación", "Transporte", "Educación", "Entretenimiento", "Salud", "Otros"};

        for (String name : incomeCategories) {
            ContentValues values = new ContentValues();
            values.put(KEY_CATEGORY_NAME, name);
            values.put(KEY_TIPO, "INCOME");
            values.put(KEY_CATEGORY_ICON, "ic_income_default");
            values.put(KEY_CATEGORY_COLOR, "#4CAF50"); // Green
            db.insert(TABLE_CATEGORIES, null, values);
        }
        for (String name : expenseCategories) {
            ContentValues values = new ContentValues();
            values.put(KEY_CATEGORY_NAME, name);
            values.put(KEY_TIPO, "EXPENSE");
            values.put(KEY_CATEGORY_ICON, "ic_expense_default");
            values.put(KEY_CATEGORY_COLOR, "#F44336"); // Red
            db.insert(TABLE_CATEGORIES, null, values);
        }
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSACTIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIES);
        onCreate(db);
    }

    // Categoria CRUD
    public long addCategoria(Categoria categoria) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_CATEGORY_NAME, categoria.getNombre());
        values.put(KEY_TIPO, categoria.getTipo());
        values.put(KEY_CATEGORY_ICON, categoria.getIcon());
        values.put(KEY_CATEGORY_COLOR, categoria.getColor());
        long id = db.insert(TABLE_CATEGORIES, null, values);
        db.close();
        return id;
    }

    public Categoria getCategoria(long categoria_id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_CATEGORIES + " WHERE "
                + KEY_ID + " = " + categoria_id;
        Cursor c = db.rawQuery(selectQuery, null);
        Categoria categoria = null;
        if (c != null && c.moveToFirst()) {
            categoria = new Categoria();
            categoria.setId(c.getInt(c.getColumnIndexOrThrow(KEY_ID)));
            categoria.setNombre(c.getString(c.getColumnIndexOrThrow(KEY_CATEGORY_NAME)));
            categoria.setTipo(c.getString(c.getColumnIndexOrThrow(KEY_TIPO)));
            categoria.setIcon(c.getString(c.getColumnIndexOrThrow(KEY_CATEGORY_ICON)));
            categoria.setColor(c.getString(c.getColumnIndexOrThrow(KEY_CATEGORY_COLOR)));
        }
        if(c != null) c.close();
        db.close();
        return categoria;
    }

    public List<Categoria> getAllCategorias() {
        List<Categoria> categorias = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_CATEGORIES;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            do {
                Categoria categoria = new Categoria();
                categoria.setId(c.getInt(c.getColumnIndexOrThrow(KEY_ID)));
                categoria.setNombre(c.getString(c.getColumnIndexOrThrow(KEY_CATEGORY_NAME)));
                categoria.setTipo(c.getString(c.getColumnIndexOrThrow(KEY_TIPO)));
                categoria.setIcon(c.getString(c.getColumnIndexOrThrow(KEY_CATEGORY_ICON)));
                categoria.setColor(c.getString(c.getColumnIndexOrThrow(KEY_CATEGORY_COLOR)));
                categorias.add(categoria);
            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return categorias;
    }

    public int updateCategoria(Categoria categoria) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_CATEGORY_NAME, categoria.getNombre());
        values.put(KEY_TIPO, categoria.getTipo());
        values.put(KEY_CATEGORY_ICON, categoria.getIcon());
        values.put(KEY_CATEGORY_COLOR, categoria.getColor());
        int rows = db.update(TABLE_CATEGORIES, values, KEY_ID + " = ?",
                new String[]{String.valueOf(categoria.getId())});
        db.close();
        return rows;
    }

    public void deleteCategoria(long categoria_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CATEGORIES, KEY_ID + " = ?",
                new String[]{String.valueOf(categoria_id)});
        db.close();
    }

    // Transaccion CRUD
    public long addTransaccion(Transaccion transaccion) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_TIPO, transaccion.getTipo());
        values.put(KEY_TRANSACTION_MONTO, transaccion.getMonto());
        values.put(KEY_TRANSACTION_ID_CATEGORIA, transaccion.getIdCategoria());
        values.put(KEY_TRANSACTION_DESCRIPCION, transaccion.getDescripcion());
        values.put(KEY_TRANSACTION_FECHA, transaccion.getFecha());
        values.put(KEY_TRANSACTION_PAYMENT_METHOD, transaccion.getPaymentMethod());
        long id = db.insert(TABLE_TRANSACTIONS, null, values);
        db.close();
        return id;
    }

    public Transaccion getTransaccion(long transaccion_id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_TRANSACTIONS + " WHERE "
                + KEY_ID + " = " + transaccion_id;

        Cursor c = db.rawQuery(selectQuery, null);
        Transaccion transaccion = null;

        if (c != null && c.moveToFirst()) {
            transaccion = new Transaccion();
            transaccion.setId(c.getInt(c.getColumnIndexOrThrow(KEY_ID)));
            transaccion.setTipo(c.getString(c.getColumnIndexOrThrow(KEY_TIPO)));
            transaccion.setMonto(c.getDouble(c.getColumnIndexOrThrow(KEY_TRANSACTION_MONTO)));
            transaccion.setIdCategoria(c.getInt(c.getColumnIndexOrThrow(KEY_TRANSACTION_ID_CATEGORIA)));
            transaccion.setDescripcion(c.getString(c.getColumnIndexOrThrow(KEY_TRANSACTION_DESCRIPCION)));
            transaccion.setFecha(c.getString(c.getColumnIndexOrThrow(KEY_TRANSACTION_FECHA)));
            transaccion.setPaymentMethod(c.getString(c.getColumnIndexOrThrow(KEY_TRANSACTION_PAYMENT_METHOD)));
            transaccion.setCreated_at(c.getString(c.getColumnIndexOrThrow(KEY_CREATED_AT)));
        }

        if (c != null) c.close();
        db.close();
        return transaccion;
    }

    public List<Transaccion> getFilteredTransactions(String startDate, String endDate, Long categoryId) {
        List<Transaccion> transacciones = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        StringBuilder queryBuilder = new StringBuilder("SELECT * FROM " + TABLE_TRANSACTIONS + " WHERE 1=1");
        ArrayList<String> selectionArgs = new ArrayList<>();

        if (startDate != null && !startDate.isEmpty() && endDate != null && !endDate.isEmpty()) {
            queryBuilder.append(" AND " + KEY_TRANSACTION_FECHA + " BETWEEN ? AND ?");
            selectionArgs.add(startDate);
            selectionArgs.add(endDate);
        }
        if (categoryId != null && categoryId > 0) {
            queryBuilder.append(" AND " + KEY_TRANSACTION_ID_CATEGORIA + " = ?");
            selectionArgs.add(String.valueOf(categoryId));
        }
        queryBuilder.append(" ORDER BY " + KEY_TRANSACTION_FECHA + " DESC");

        Cursor c = db.rawQuery(queryBuilder.toString(), selectionArgs.toArray(new String[0]));

        if (c.moveToFirst()) {
            do {
                Transaccion transaccion = new Transaccion();
                transaccion.setId(c.getInt(c.getColumnIndexOrThrow(KEY_ID)));
                transaccion.setTipo(c.getString(c.getColumnIndexOrThrow(KEY_TIPO)));
                transaccion.setMonto(c.getDouble(c.getColumnIndexOrThrow(KEY_TRANSACTION_MONTO)));
                transaccion.setIdCategoria(c.getInt(c.getColumnIndexOrThrow(KEY_TRANSACTION_ID_CATEGORIA)));
                transaccion.setDescripcion(c.getString(c.getColumnIndexOrThrow(KEY_TRANSACTION_DESCRIPCION)));
                transaccion.setFecha(c.getString(c.getColumnIndexOrThrow(KEY_TRANSACTION_FECHA)));
                transaccion.setPaymentMethod(c.getString(c.getColumnIndexOrThrow(KEY_TRANSACTION_PAYMENT_METHOD)));
                transaccion.setCreated_at(c.getString(c.getColumnIndexOrThrow(KEY_CREATED_AT)));
                transacciones.add(transaccion);
            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return transacciones;
    }

    public double getSumByType(String tipo, String startDate, String endDate) {
        SQLiteDatabase db = this.getReadableDatabase();
        double sum = 0;

        StringBuilder queryBuilder = new StringBuilder("SELECT SUM(" + KEY_TRANSACTION_MONTO + ") FROM " + TABLE_TRANSACTIONS + " WHERE " + KEY_TIPO + " = ?");
        ArrayList<String> selectionArgs = new ArrayList<>();
        selectionArgs.add(tipo);

        if (startDate != null && !startDate.isEmpty() && endDate != null && !endDate.isEmpty()) {
            queryBuilder.append(" AND " + KEY_TRANSACTION_FECHA + " BETWEEN ? AND ?");
            selectionArgs.add(startDate);
            selectionArgs.add(endDate);
        }

        Cursor c = db.rawQuery(queryBuilder.toString(), selectionArgs.toArray(new String[0]));

        if (c.moveToFirst()) {
            sum = c.getDouble(0);
        }
        c.close();
        db.close();
        return sum;
    }

    public Map<String, Double> getCategoryStatistics(String tipo, String startDate, String endDate) {
        Map<String, Double> stats = new LinkedHashMap<>();
        SQLiteDatabase db = this.getReadableDatabase();

        StringBuilder queryBuilder = new StringBuilder(
                "SELECT C." + KEY_CATEGORY_NAME + ", SUM(T." + KEY_TRANSACTION_MONTO + ") as total " +
                "FROM " + TABLE_TRANSACTIONS + " T JOIN " + TABLE_CATEGORIES + " C ON T." + KEY_TRANSACTION_ID_CATEGORIA + " = C." + KEY_ID + " " +
                "WHERE T." + KEY_TIPO + " = ?");

        ArrayList<String> selectionArgs = new ArrayList<>();
        selectionArgs.add(tipo);

        if (startDate != null && !startDate.isEmpty() && endDate != null && !endDate.isEmpty()) {
            queryBuilder.append(" AND T." + KEY_TRANSACTION_FECHA + " BETWEEN ? AND ?");
            selectionArgs.add(startDate);
            selectionArgs.add(endDate);
        }

        queryBuilder.append(" GROUP BY C." + KEY_CATEGORY_NAME + " ORDER BY total DESC");

        Cursor c = db.rawQuery(queryBuilder.toString(), selectionArgs.toArray(new String[0]));

        if (c.moveToFirst()) {
            do {
                stats.put(c.getString(0), c.getDouble(1));
            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return stats;
    }
    
    public double getDailyAverage(String tipo, String startDate, String endDate) {
        SQLiteDatabase db = this.getReadableDatabase();
        double total = 0;
        long days = 1;

        StringBuilder queryBuilder = new StringBuilder(
            "SELECT SUM(" + KEY_TRANSACTION_MONTO + "), COUNT(DISTINCT " + KEY_TRANSACTION_FECHA + ") " +
            "FROM " + TABLE_TRANSACTIONS + " WHERE " + KEY_TIPO + " = ?");

        ArrayList<String> selectionArgs = new ArrayList<>();
        selectionArgs.add(tipo);

        if (startDate != null && !startDate.isEmpty() && endDate != null && !endDate.isEmpty()) {
            queryBuilder.append(" AND " + KEY_TRANSACTION_FECHA + " BETWEEN ? AND ?");
            selectionArgs.add(startDate);
            selectionArgs.add(endDate);
        }

        Cursor c = db.rawQuery(queryBuilder.toString(), selectionArgs.toArray(new String[0]));

        if (c.moveToFirst()) {
            total = c.getDouble(0);
            days = c.getLong(1);
            if (days == 0) { // Avoid division by zero
                days = 1;
            }
        }
        c.close();
        db.close();

        return total / days;
    }

    public int updateTransaccion(Transaccion transaccion) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TIPO, transaccion.getTipo());
        values.put(KEY_TRANSACTION_MONTO, transaccion.getMonto());
        values.put(KEY_TRANSACTION_ID_CATEGORIA, transaccion.getIdCategoria());
        values.put(KEY_TRANSACTION_DESCRIPCION, transaccion.getDescripcion());
        values.put(KEY_TRANSACTION_FECHA, transaccion.getFecha());
        values.put(KEY_TRANSACTION_PAYMENT_METHOD, transaccion.getPaymentMethod());

        int rows = db.update(TABLE_TRANSACTIONS, values, KEY_ID + " = ?",
                new String[]{String.valueOf(transaccion.getId())});
        db.close();
        return rows;
    }

    public void deleteTransaccion(long transaccion_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TRANSACTIONS, KEY_ID + " = ?",
                new String[]{String.valueOf(transaccion_id)});
        db.close();
    }
}
