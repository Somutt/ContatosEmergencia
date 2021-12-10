package com.example.contatosemergencia.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.os.Bundle;

import com.example.contatosemergencia.R;
import com.example.contatosemergencia.models.Contato;
import com.example.contatosemergencia.models.User;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

public class ListaContatos_Activity extends AppCompatActivity {

    ListView lvContatos;
    TextView tvTitulo;
    Button btAlterar;
    User user;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.listacontatos_activity);

        //Importante inicializar isso antes de chamar a intent porque ela vai usar a lista pra preencher com os contatos
        lvContatos = findViewById(R.id.listViewContatos);

        //Recupera o usuário a partir do método putExtra passado na intent da activity anterior
        Intent quemChamou = this.getIntent();
        if(quemChamou != null) {
            Bundle params = quemChamou.getExtras();
            if(params != null) {
                user = (User) params.getSerializable("usuario");
                preencherListView(user);
            }
        }

        btAlterar = findViewById(R.id.btAlterar);
        tvTitulo = findViewById(R.id.tvTituloContatos);
        tvTitulo.setText("Lista de Contatos de"+user.getNome());

        //Entra na tela de alterar contatos passando o usuário de parâmetro
        btAlterar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListaContatos_Activity.this, AlterarContatos_Activity.class);
                intent.putExtra("usuario", user);
                startActivity(intent);
            }
        });

    }

    //Preenche a lista com os contatos salvos
    protected void preencherListView(User user) {

        final ArrayList<Contato> contatos = user.getContatos();

        if(contatos != null) {
            final String [] nomesSP;
            nomesSP = new String[contatos.size()];
            for (int j =0; j < contatos.size(); j++){
                nomesSP[j] = contatos.get(j).getNome();
            }

            ArrayAdapter<String> adaptador;
            adaptador = new ArrayAdapter<String>(this, R.layout.list_view, nomesSP);

            lvContatos.setAdapter(adaptador);

            //Ao clicar em algum item na lista, realiza uma ligação após checar a permissão.
            lvContatos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (checarPermissaoPhone()) {

                        Uri uri = Uri.parse(contatos.get(position).getNumero());
                        Intent itLigar = new Intent(Intent.ACTION_CALL, uri);
                        startActivity(itLigar);
                    }
                    //Log.v("Corinthians", "Teste");
                }
            });
        }
    }

    protected boolean checarPermissaoPhone(){

        if(ContextCompat.checkSelfPermission(ListaContatos_Activity.this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_DENIED) {
                requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 2222);
                return true;
        }
        return false;
    }
}
