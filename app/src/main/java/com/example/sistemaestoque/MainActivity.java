package com.example.sistemaestoque;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView listViewItens;
    private ItemDAO itemDAO;
    private List<Item> listaItens;
    private ItemAdapter adapter;

    // Constantes para os resultados das activities
    private static final int REQUEST_ADD_ITEM = 1;
    private static final int REQUEST_EDIT_ITEM = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializa o DAO
        itemDAO = new ItemDAO(this);

        // Inicializa a lista para exibir os itens
        listViewItens = findViewById(R.id.listViewItens);

        // Configura o botão para adicionar novos itens
        Button btnAddItem = findViewById(R.id.btnAdicionarItem);
        btnAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddEditItemActivity.class);
                startActivityForResult(intent, REQUEST_ADD_ITEM);
            }
        });

        // Adiciona alguns dados de exemplo se o banco estiver vazio
        adicionarDadosDeExemplo();

        // Carrega e exibe os itens iniciais do banco de dados
        carregarItens();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_listar) {
            // Navegar para a tela de listagem
            Intent intent = new Intent(this, ListagemActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.menu_cadastrar) {
            // Navegar para a tela de cadastro
            Intent intent = new Intent(this, CadastroActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void carregarItens() {
        // Obtém todos os itens do banco de dados
        listaItens = itemDAO.getAllItems();

        // Cria o adaptador personalizado
        adapter = new ItemAdapter();
        listViewItens.setAdapter(adapter);
    }

    // Adaptador personalizado para a ListView
    private class ItemAdapter extends ArrayAdapter<Item> {

        public ItemAdapter() {
            super(MainActivity.this, R.layout.item_lista, listaItens);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Inflando o layout se necessário
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_lista, parent, false);
            }

            // Obtém o item atual
            final Item itemAtual = listaItens.get(position);

            // Configura os TextViews
            TextView tvDescricao = convertView.findViewById(R.id.tvDescricao);
            TextView tvQuantidade = convertView.findViewById(R.id.tvQuantidade);

            tvDescricao.setText(itemAtual.getDescricao());
            tvQuantidade.setText("Quantidade: " + itemAtual.getQuantidade());

            // Configura o botão de editar
            Button btnEditar = convertView.findViewById(R.id.btnEditar);
            btnEditar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, AddEditItemActivity.class);
                    intent.putExtra("ITEM_ID", itemAtual.getId());
                    startActivityForResult(intent, REQUEST_EDIT_ITEM);
                }
            });

            // Configura o botão de excluir
            Button btnExcluir = convertView.findViewById(R.id.btnExcluir);
            btnExcluir.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Mostra um diálogo de confirmação
                    mostrarDialogoConfirmacaoExclusao(itemAtual);
                }
            });

            return convertView;
        }
    }

    private void mostrarDialogoConfirmacaoExclusao(final Item item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_confirma_exclusao, null);
        builder.setView(view);

        // Configura a mensagem personalizada
        TextView tvMensagem = view.findViewById(R.id.tvMensagemExclusao);
        tvMensagem.setText("Tem certeza que deseja excluir o item: " + item.getDescricao() + "?");

        // Cria o diálogo
        final AlertDialog dialog = builder.create();
        dialog.show();

        // Configura os botões
        Button btnConfirmar = view.findViewById(R.id.btnConfirmarExclusao);
        btnConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Exclui o item do banco de dados
                int resultado = itemDAO.excluirItem(item.getId());
                if (resultado > 0) {
                    Toast.makeText(MainActivity.this, "Item excluído com sucesso!", Toast.LENGTH_SHORT).show();
                    // Recarrega a lista
                    carregarItens();
                } else {
                    Toast.makeText(MainActivity.this, "Erro ao excluir o item", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }
        });

        Button btnCancelar = view.findViewById(R.id.btnCancelarExclusao);
        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Se o resultado for OK, recarrega os itens
        if (resultCode == RESULT_OK && (requestCode == REQUEST_ADD_ITEM || requestCode == REQUEST_EDIT_ITEM)) {
            carregarItens();
        }
    }

    private void adicionarDadosDeExemplo() {
        // Verifica se o banco de dados já possui itens
        if (itemDAO.getAllItems().size() == 0) {
            // Adiciona alguns itens de exemplo
            itemDAO.inserirItem("Caneta Azul", 25);
            itemDAO.inserirItem("Caderno Espiral", 10);
            itemDAO.inserirItem("Post-it Colorido", 15);
            itemDAO.inserirItem("Grampeador", 5);
            itemDAO.inserirItem("Caixa de Clipes", 30);
            Toast.makeText(this, "Dados de exemplo adicionados!", Toast.LENGTH_SHORT).show();
        }
    }

}