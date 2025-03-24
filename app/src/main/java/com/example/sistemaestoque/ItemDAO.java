package com.example.sistemaestoque;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class ItemDAO {
    private DatabaseManager dbManager;
    private SQLiteDatabase database;

    public ItemDAO(Context context) {
        dbManager = new DatabaseManager(context);
    }

    public void open() {
        database = dbManager.getWritableDatabase();
    }

    public void close() {
        dbManager.close();
    }

    // Inserir um novo item
    public long inserirItem(String descricao, int quantidade) {
        open();
        ContentValues valores = new ContentValues();
        valores.put(DatabaseManager.COLUMN_DESCRICAO, descricao);
        valores.put(DatabaseManager.COLUMN_QUANTIDADE, quantidade);

        long resultado = database.insert(DatabaseManager.TABLE_ITEM, null, valores);
        close();
        return resultado;
    }

    // Obter todos os itens
    public List<Item> getAllItems() {
        open();
        List<Item> itens = new ArrayList<>();
        String[] colunas = {
                DatabaseManager.COLUMN_ID,
                DatabaseManager.COLUMN_DESCRICAO,
                DatabaseManager.COLUMN_QUANTIDADE
        };

        Cursor cursor = database.query(
                DatabaseManager.TABLE_ITEM,
                colunas,
                null,
                null,
                null,
                null,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseManager.COLUMN_ID));
                String descricao = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseManager.COLUMN_DESCRICAO));
                int quantidade = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseManager.COLUMN_QUANTIDADE));

                Item item = new Item(id, descricao, quantidade);
                itens.add(item);
            } while (cursor.moveToNext());

            cursor.close();
        }

        close();
        return itens;
    }

    // Atualizar um item existente
    public int atualizarItem(Item item) {
        open();
        ContentValues valores = new ContentValues();
        valores.put(DatabaseManager.COLUMN_DESCRICAO, item.getDescricao());
        valores.put(DatabaseManager.COLUMN_QUANTIDADE, item.getQuantidade());

        int resultado = database.update(
                DatabaseManager.TABLE_ITEM,
                valores,
                DatabaseManager.COLUMN_ID + " = ?",
                new String[] { String.valueOf(item.getId()) }
        );

        close();
        return resultado;
    }

    // Excluir um item
    public int excluirItem(int id) {
        open();
        int resultado = database.delete(
                DatabaseManager.TABLE_ITEM,
                DatabaseManager.COLUMN_ID + " = ?",
                new String[] { String.valueOf(id) }
        );

        close();
        return resultado;
    }

    // Buscar um item pelo ID
    public Item getItemById(int id) {
        open();
        Item item = null;

        Cursor cursor = database.query(
                DatabaseManager.TABLE_ITEM,
                null,
                DatabaseManager.COLUMN_ID + " = ?",
                new String[] { String.valueOf(id) },
                null,
                null,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            String descricao = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseManager.COLUMN_DESCRICAO));
            int quantidade = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseManager.COLUMN_QUANTIDADE));
            item = new Item(id, descricao, quantidade);
            cursor.close();
        }

        close();
        return item;
    }
}