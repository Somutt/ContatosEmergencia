package com.example.contatosemergencia.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.contatosemergencia.R;
import com.example.contatosemergencia.models.Contato;
import com.example.contatosemergencia.models.User;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;

public class AlterarContatos_Activity extends AppCompatActivity {

    boolean primeiraVezUser = true;
    TextView tvLogin;
    EditText edtAdd;
    Button btAdd;
    ListView lvContatos;
    User user;

    @SuppressLint("ClickableViewAccessibility") //Supressão de um erro aí
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alterarcontatos_activity);

        //Recupera o usuário que foi passado pelo método putExtra() na intent anterior
        Intent quemChamou = this.getIntent();
        if(quemChamou != null) {
            Bundle params = quemChamou.getExtras();
            if(params != null) {
                user = (User) params.getSerializable("usuario");
            }
        }

        tvLogin = findViewById(R.id.tvContatoLogin);
        btAdd = findViewById(R.id.btAddContatos);
        edtAdd = findViewById(R.id.edtAddContatos);
        lvContatos = findViewById(R.id.listViewContatos);

        //Excluir o label quando o usuário tocar pela primeira vez no campo de pesquisa p/ buscar os contatos
        edtAdd.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(primeiraVezUser) {
                    primeiraVezUser = false;
                    edtAdd.setText("");
                }

                return false;
            }
        });

        tvLogin.setText("Alterar Contatos de "+user.getNome());

        //Ao apertar no botão de busca, o app irá pedir permissão para acessar os contatos e adicionar um a partir da busca
        btAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Checa se já há uma permissão, se não, chamará a permissão a partir do manifest
                if(ContextCompat.checkSelfPermission(AlterarContatos_Activity.this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_DENIED) {
                    requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, 3333);
                    return;
                }

                //Essa parte aqui imagino que seja para realizar a busca a partir dos contatos buscados do celular, mas nao sei perfeitamente
                ContentResolver cr = getContentResolver();
                String consulta = ContactsContract.Contacts.DISPLAY_NAME + " LIKE ?"; //Aciona a consulta nos contatos?
                String [] argumentosConsulta = {"%"+edtAdd.getText()+"%"}; //Passando o argumento p/ consulta
                Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, consulta, argumentosConsulta, null);
                final String [] nomesContatos = new String[cursor.getCount()];
                final String [] telefonesContatos = new String[cursor.getCount()];

                //Acredito que esta parte continua a busca de contatos e salva o nome e número
                int i=0;
                while (cursor.moveToNext()) {
                    int indiceNome = cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME);
                    String contatoNome = cursor.getString(indiceNome);
                    nomesContatos[i]= contatoNome;
                    int indiceContatoID = cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID);
                    String contactID = cursor.getString(indiceContatoID);
                    String consultaPhone = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactID;
                    Cursor phones = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, consultaPhone, null, null);

                    while (phones.moveToNext()) {
                        @SuppressLint("Range") String number = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)); //Supressão de um erro aí envolvendo o tamanho mínimo
                        telefonesContatos[i]=number; //Salvando só último telefone
                    }
                    i++;
                }

                //Se os contatos achados na pesquisa não for nulo, preenche a lista com os contatos encontrados
                if(nomesContatos != null) {
                    for(int j=0; j<=nomesContatos.length; j++) {
                        ArrayAdapter<String> adaptador;
                        adaptador = new ArrayAdapter<String>(AlterarContatos_Activity.this, R.layout.list_view, nomesContatos);
                        lvContatos.setAdapter(adaptador);
                        lvContatos.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                            @Override
                            //Após inserir o contato na lista, permite que ao ser clicado, adicione na tela da Lista de Contatos
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Contato c = new Contato();
                                c.setNome(nomesContatos[position]);
                                c.setNumero("tel:+"+telefonesContatos[position]);
                                salvarContato(c);
                                //Log.v("Flamengo", "Nome: "+user.getContatos().get(0).getNome()+" Numero: "+c.getNumero()); Teste
                                Intent intent = new Intent(getApplicationContext(), ListaContatos_Activity.class);
                                intent.putExtra("usuario", user);
                                startActivity(intent);
                                finish();
                            }
                        });
                    }
                }
            }
        });
    }

    //Preenche a lista de contatos para o usuário através de uma conversão de tipos, admito q não entendi perfeitamente
    public void salvarContato (Contato w){
        SharedPreferences salvaContatos = getSharedPreferences("contatos", Activity.MODE_PRIVATE);

        int num = salvaContatos.getInt("numContatos", 0); //checando quantos contatos já tem
        SharedPreferences.Editor editor = salvaContatos.edit();
        try {
            ByteArrayOutputStream dt = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(dt);
            dt = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(dt);
            oos.writeObject(w);
            String contatoSerializado= dt.toString(StandardCharsets.ISO_8859_1.name());
            editor.putString("contato"+(num+1), contatoSerializado);
            editor.putInt("numContatos",num+1);
        }catch(Exception e) {
            e.printStackTrace();
        }
        editor.commit();
        user.getContatos().add(w);
    }

}
