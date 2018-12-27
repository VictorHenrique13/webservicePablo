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
        db.execSQL("create table if not exists mesa(id integer primary key autoincrement, numMesa text not null)");
        db.execSQL("create table if not exists produto(id integer primary key,nome text,valor double, nomeCategoria text)");
        db.execSQL("CREATE TABLE if not exists login(id INTEGER primary key autoincrement,token text,permissao INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void setLogin(String token, int permissao){
        db = getReadableDatabase();
        Cursor c = db.rawQuery("select * from login",null);
        try {
            ContentValues values = new ContentValues();
            values.put("token",token);
            values.put("permissao",permissao);
            if(c.moveToFirst()){
                int id = c.getInt(c.getColumnIndex("id"));
                db.update("login",values,"id="+id, null);
            }else{
                db.insert("login","",values);
            }
        }finally {
            db.close();
        }
    }
    public void deleteLogin(){
        db = getReadableDatabase();
        try {
            db.delete("login",null,null);
        }finally {
            db.close();
        }
    }
    public void setPermissao(int permissao){
        db = getReadableDatabase();
        Cursor c = db.rawQuery("select * from login",null);
        try {
            ContentValues values = new ContentValues();
            values.put("permissao",permissao);
            if(c.moveToFirst()){
                int id = c.getInt(c.getColumnIndex("id"));
                db.update("login",values,"id="+id, null);
            }else{
                db.insert("login","",values);
            }
        }finally {
            db.close();
        }
    }
    public void insertMesa(String mesa){
            db = getReadableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put("numMesa",mesa);
            db.insert("mesa","",values);

        }finally {
            db.close();
        }
    }
    public void insertProduto(Produto prod){
        db = getReadableDatabase();
        try{
            ContentValues values = new ContentValues();
            values.put("id",prod.getId());
            values.put("nome",prod.getNome());
            values.put("valor",prod.getDoublePreco());
            values.put("nomeCategoria",prod.getNameCategoria());
            db.insert("produto","",values);
        }finally {
            db.close();
        }
    }
    public void insertCategoria(String nomeCategoria){
        db = getReadableDatabase();
        try{
            ContentValues values = new ContentValues();
            values.put("nomeCategoria",nomeCategoria);
            db.insert("categoria","",values);
        }finally {
            db.close();
        }
    }
    public String getToken(){
        db = getReadableDatabase();
        Cursor c = db.rawQuery("select * from login",null);
        String retorno = "vazio";
        if(c.moveToFirst()){
            retorno = c.getString(c.getColumnIndex("token"));
        }
        db.close();
        return retorno;
    }
    public int getPermissao(){
        db = getReadableDatabase();
        Cursor c = db.rawQuery("select * from login",null);
        int retorno = -1;
        if(c.moveToFirst()){
            retorno = c.getInt(c.getColumnIndex("permissao"));
        }
        db.close();
        return retorno;
    }

    public ArrayList<String> getListaCategoria(){
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
         Cursor c = db.rawQuery("select * from mesa",null);
         if(c.moveToFirst()){
             do{
                mesas.add(c.getString(c.getColumnIndex("numMesa")));
             }while(c.moveToNext());
         }
         db.close();
         return mesas;
    }
    public ArrayList<Produto> getListaProdutos(String categoriaNome){
        db = getReadableDatabase();
        ArrayList<Produto> produtos = new ArrayList<>();
        //Cursor c = db.rawQuery("select * from produto where nomeCategoria = '"+categoriaNome+"'",null);
        Cursor c = db.rawQuery("select * from produto",null);
        if(c.moveToFirst()){
            do{
                int id = c.getInt(c.getColumnIndex("id"));
                String nome = c.getString(c.getColumnIndex("nome"));
                double valor = c.getDouble(c.getColumnIndex("valor"));
                String categoria = c.getString(c.getColumnIndex("nomeCategoria"));
                Produto p = new Produto(id,nome,0,valor,categoria);
                produtos.add(p);
            }while (c.moveToNext());
        }
        return produtos;
    }

    public void updateMesa(String oldMesa, String newMesa){
        db = getReadableDatabase();
        Cursor c = db.rawQuery("select * from mesa where numMesa = '"+oldMesa+"'",null);
        try {
            ContentValues values = new ContentValues();
            values.put("numMesa",newMesa);
            if(c.moveToFirst()){
                int id = c.getInt(c.getColumnIndex("id"));
                db.update("mesa",values,"id="+id, null);
            }else{
                db.insert("mesa","",values);
            }
        }finally {
            db.close();
        }
    }
    public void updateCategoria(String oldCategoria, String newCategoria){
        db = getReadableDatabase();
        Cursor c = db.rawQuery("select * from categoria where nomeCategoria = '"+oldCategoria+"'",null);
        try {
            ContentValues values = new ContentValues();
            values.put("nomeCategoria",newCategoria);
            if(c.moveToFirst()){
                int id = c.getInt(c.getColumnIndex("id"));
                db.update("categoria",values,"id="+id, null);
            }else{
                db.insert("categoria","",values);
            }
        }finally {
            db.close();
        }
    }
    public void updateProduto(Produto oldProduto, Produto newProduto){
        db = getReadableDatabase();
        Cursor c = db.rawQuery("select * from produto where id = '"+oldProduto.getId()+"'",null);
        try {
            ContentValues values = new ContentValues();
            values.put("id",newProduto.getId());
            values.put("nome",newProduto.getNome());
            values.put("valor",newProduto.getDoublePreco());
            values.put("nomeCategoria",newProduto.getNameCategoria());
            if(c.moveToFirst()){
                int id = c.getInt(c.getColumnIndex("id"));
                db.update("produto",values,"id="+id, null);
            }else{
                db.insert("produto","",values);
            }
        }finally {
            db.close();
        }
    }
    public void removeMesa(String numMesa){
        db = getReadableDatabase();
        Cursor c = db.rawQuery("select * from mesa where numMesa = '"+numMesa+"'",null);
        try {
            if(c.moveToFirst()){
                int id = c.getInt(c.getColumnIndex("id"));
                db.delete("mesa","id="+id,null);
            }
        }finally {
            db.close();
        }
    }
    public void removeCategoria(String nomeCategoria){
        db = getReadableDatabase();
        Cursor c = db.rawQuery("select * from categoria where nomeCategoria = '"+nomeCategoria+"'",null);
        try {
            if(c.moveToFirst()){
                int id = c.getInt(c.getColumnIndex("id"));
                db.delete("categoria","id="+id,null);
            }
        }finally {
            db.close();
        }
    }
    public void removeProduto(int idProduto){
        db = getReadableDatabase();
        Cursor c = db.rawQuery("select * from produto where id = '"+idProduto+"'",null);
        try {
            if(c.moveToFirst()){
                int id = c.getInt(c.getColumnIndex("id"));
                db.delete("produto","id="+id,null);
            }
        }finally {
            db.close();
        }
    }
    public void removeAllProdutoFromCategoria(String categoriaNome){
        db = getReadableDatabase();
        try {
            db.delete("produto","nomeCategoria='"+categoriaNome+"'",null);
        }finally {
            db.close();
        }
    }
    public void removeAllCategoria(){
        db = getReadableDatabase();
        try {
            db.delete("categoria",null,null);
        }finally {
            db.close();
        }
    }
    public void removeAllMesa(){
        db = getReadableDatabase();
        try {
            db.delete("mesa",null,null);
        }finally {
            db.close();
        }
    }
    public void recreateAllTables(){
        db = getReadableDatabase();
        try {
            db.execSQL("DROP TABLE IF EXISTS login");
            db.execSQL("DROP TABLE IF EXISTS mesa");
            db.execSQL("DROP TABLE IF EXISTS categoria");
            db.execSQL("DROP TABLE IF EXISTS produto");
            onCreate(db);
        }finally {
            db.close();
        }
    }
}
