package br.com.viphost.kardenapp.CONTROLLER.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import br.com.viphost.kardenapp.MODEL.Produto;

public class DbOpenhelper extends SQLiteOpenHelper {
    private  Context context;
    private static final String DB_NAME = "karden.sqlite";
    private static final int VERSION = 1;
    private SQLiteDatabase db ;
    private static final String TAG  = "sql";
    public DbOpenhelper(@Nullable Context context) {
        super(context,DB_NAME,null,VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE if not exists categoria(id INTEGER primary key autoincrement,nomeCategoria text,img varchar)");
        Log.i(TAG,"Categoria criada");
        db.execSQL("create table if not exists mesas(id integer primary key autoincrement, numMesa text not null)");
        db.execSQL("create table if not exists produtos(id integer primary key autoincrement,nome text,valor double, quantidade INTEGER,nomeCategoria text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void salvarMesa(int mesa){
            db = getReadableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put("numMesa",mesa);
            db.insert("mesas","",values);

        }finally {
            db.close();
        }
    }
    public void salvarProduto(Produto prod){
        db = getReadableDatabase();
        try{
            ContentValues values = new ContentValues();
            values.put("id",prod.getId());
            values.put("nome",prod.getNome());
            values.put("nomeCategoria",prod.getNameCategoria());
            values.put("valor",prod.getPreco());
            values.put("quantidade",prod.getIntQuantidade());
            db.insert("produtos","",values);
        }finally {
            db.close();
        }
    }
    public void salvarCategoria(String nomeCategoria){
        db = getReadableDatabase();
        try{
            ContentValues values = new ContentValues();
            values.put("nomeCategoria",nomeCategoria);
            db.insert("categoria","",values);
        }finally {
            db.close();
        }
    }

    public ArrayList<String> getListaAtegoria(){
        db = getReadableDatabase();
        ArrayList<String> nameCategoria = new ArrayList<>();
        Cursor c = db.rawQuery("select * from categoria",null);

        if(c.moveToFirst()){
            do{
                nameCategoria.add(c.getString(c.getColumnIndex("nomeCategoria")));

            }while(c.moveToNext());
        }
        db.close();
        return  nameCategoria;
    }
    public ArrayList<String> getListaMesas(){
        db = getReadableDatabase();
         ArrayList<String> mesas = new ArrayList<>();
         Cursor c = db.rawQuery("select * from mesas",null);
         if(c.moveToFirst()){
             do{
                mesas.add(c.getString(c.getColumnIndex("numMesa")));
             }while(c.moveToNext());
         }
         db.close();
         return mesas;
    }
    public ArrayList<Produto> getListProdutos(){
        db = getReadableDatabase();
        ArrayList<Produto> produtos = new ArrayList<>();
        Cursor c = db.rawQuery("select * from produtos",null);
        if(c.moveToFirst()){
            do{
                int id = c.getInt(c.getColumnIndex("id"));
                String nome = c.getString(c.getColumnIndex("nome"));
                int quantidade = c.getInt(c.getColumnIndex("quantidade"));
                float valor = c.getFloat(c.getColumnIndex("valor"));
                String categoria = c.getString(c.getColumnIndex("nomeCategoria"));
                Produto p = new Produto(id,nome,quantidade,valor,categoria);
                produtos.add(p);
            }while (c.moveToNext());
        }
        return produtos;
    }
}
