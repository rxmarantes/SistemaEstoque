package com.example.sistemaestoque;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class AddEditItemActivity extends AppCompatActivity {

    private TextView tvTitulo;
    private EditText etDescricao;
    private EditText etQuantidade;
    private Button btnSalvar;
    private Button btnCancelar;

    private ItemDAO itemDAO;
    private Item itemAtual = null;
    private int itemId = -1;
    private boolean modoEdicao = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_item);

        // Inicializa os componentes de UI
        tvTitulo = findViewById(R.id.tvTitulo);
        etDescricao = findViewById(R.id.etDescricao);
        etQuantidade = findViewById(R.id.etQuantidade);
        btnSalvar = findViewById(R.id.btnSalvar);
        btnCancelar = findViewById(R.id.btnCancelar);

        // Inicializa o DAO
        itemDAO = new ItemDAO(this);

        // Verifica se estamos no modo de edição (foi passado um ID)
        if (getIntent().hasExtra("ITEM_ID")) {
            modoEdicao = true;
            itemId = getIntent().getIntExtra("ITEM_ID", -1);

            // Busca os dados do item a ser editado
            if (itemId != -1) {
                carregarDadosItem();
            }
        }

        // Configura o título da tela
        tvTitulo.setText(modoEdicao ? "Editar Item" : "Adicionar Item");

        // Configura os botões
        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                salvarItem();
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }

    private void carregarDadosItem() {
        itemAtual = itemDAO.getItemById(itemId);
        if (itemAtual != null) {
            etDescricao.setText(itemAtual.getDescricao());
            etQuantidade.setText(String.valueOf(itemAtual.getQuantidade()));
        } else {
            Toast.makeText(this, "Item não encontrado!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void salvarItem() {
        // Validar dados
        String descricao = etDescricao.getText().toString().trim();
        String quantidadeStr = etQuantidade.getText().toString().trim();

        if (descricao.isEmpty()) {
            etDescricao.setError("A descrição é obrigatória");
            return;
        }

        if (quantidadeStr.isEmpty()) {
            etQuantidade.setError("A quantidade é obrigatória");
            return;
        }

        int quantidade;
        try {
            quantidade = Integer.parseInt(quantidadeStr);
        } catch (NumberFormatException e) {
            etQuantidade.setError("Quantidade inválida");
            return;
        }

        // Salvar no banco de dados
        long resultado;

        if (modoEdicao) {
            // Atualiza o item existente
            itemAtual.setDescricao(descricao);
            itemAtual.setQuantidade(quantidade);
            resultado = itemDAO.atualizarItem(itemAtual);

            if (resultado > 0) {
                Toast.makeText(this, "Item atualizado com sucesso!", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            } else {
                Toast.makeText(this, "Erro ao atualizar o item", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Cria um novo item
            resultado = itemDAO.inserirItem(descricao, quantidade);

            if (resultado != -1) {
                Toast.makeText(this, "Item adicionado com sucesso!", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            } else {
                Toast.makeText(this, "Erro ao adicionar o item", Toast.LENGTH_SHORT).show();
            }
        }
    }
}