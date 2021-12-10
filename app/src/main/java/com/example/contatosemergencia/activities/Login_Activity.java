package com.example.contatosemergencia.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.os.Bundle;
import android.widget.Toast;

import com.example.contatosemergencia.R;
import com.example.contatosemergencia.models.Contato;
import com.example.contatosemergencia.models.User;

import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class Login_Activity extends AppCompatActivity {
    boolean primeiraVezLogin = true;
    boolean primeiraVezSenha = true;

    EditText edLogin;
    EditText edSenha;
    Button btLogar;
    Button btNovo;

    @SuppressLint("ClickableViewAccessibility") //Supressão de um erro aí
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        //Buscando o usuário no SharedPreference p/ checar se há um usuário padrão
        SharedPreferences temUser = getSharedPreferences("usuarioPadrao", Activity.MODE_PRIVATE);
        boolean manterLogado = temUser.getBoolean("manterLogado", false);
        //Log.v("Palmeiras", "Manter Logado? "+manterLogado); Teste

        //Montando o usuário a partir do SharedPreference e mudando a activity p/ a lista de contatos caso manterLogado seja True
        if (montarObjetoSemLogar()) {
            //Log.v("Palmeiras","Oi"); Teste
            User user = montarObjeto();
            preencherListaDeContatos(user);
            Intent intent = new Intent(Login_Activity.this, ListaContatos_Activity.class);
            intent.putExtra("usuario", user);
            startActivity(intent);
            finish();
        } else { //Montando a tela caso não haja um usuário padrão
            //Log.v("Palmeiras","Olá"); Teste
            btLogar = findViewById(R.id.btEntrarLogin);
            btNovo = findViewById(R.id.btNovoLogin);
            edLogin = findViewById(R.id.edtLoginLogin);
            edSenha = findViewById(R.id.edtSenhaLogin);

            //Excluir o label quando o usuário tocar pela primeira vez no campo de Login
            edLogin.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (primeiraVezLogin) {
                        primeiraVezLogin = false;
                        edLogin.setText("");
                    }
                    return false;
                }
            });

            //Excluir o label quando o usuário tocar pela primeira vez no campo de Senha e alterando a tipo de input
            edSenha.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (primeiraVezSenha) {
                        primeiraVezSenha = false;
                        edSenha.setText("");
                        edSenha.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    }
                    return false;
                }
            });

            //Ao clicar em login, irá comparar as informações com a senha e login salvos no usuário padrão
            btLogar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //String login = edLogin.getText().toString();
                    //String senha = edSenha.getText().toString();

                    //Busca o usuario padrao salvo com SharedPreference
                    SharedPreferences temUser = getSharedPreferences("usuarioPadrao", Activity.MODE_PRIVATE);
                    String loginSalvo = temUser.getString("login", "");
                    String senhaSalva = temUser.getString("senha", "");

                    //Checa se há algum login e senha no SharedPreference e os recupera
                    if ((loginSalvo != null) && (senhaSalva != null)) {
                        String login = edLogin.getText().toString();
                        String senha = edSenha.getText().toString();

                        //Compara login e senha, e, caso estejam corretos, muda a activity com o usuário passado
                        if ((loginSalvo.compareTo(login) == 0) && (senhaSalva.compareTo(senha) == 0)) {
                            User user = montarObjeto();
                            preencherListaDeContatos(user);
                            Intent intent = new Intent(Login_Activity.this, AlterarContatos_Activity.class);
                            intent.putExtra("usuario", user);
                            startActivity(intent);
                        } else { //Mensagem de erro caso a senha não bata
                            Toast.makeText(Login_Activity.this, "Login e Senha Incorretos", Toast.LENGTH_LONG).show();
                        }
                    } else { //Mensagem de erro caso não haja login e senha
                        Toast.makeText(Login_Activity.this, "Login e Senha nulos", Toast.LENGTH_LONG).show();
                    }

                    //Log.v("PDM","Login: "+login+", Senha: "+senha);
                }
            });

            //Mudança de activity para a tela de registro de usuário
            btNovo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Login_Activity.this, RegistroUsuario_Activity.class);
                    startActivity(intent);
                }
            });

        }
    }

    //Monta o usuário a partir do usuaário padrão salvo no SharedPreferences
    private User montarObjeto(){
        User user = null;
        SharedPreferences temUser = getSharedPreferences("usuarioPadrao", Activity.MODE_PRIVATE);
        String nomeSalvo = temUser.getString("nome", "");
        String loginSalvo = temUser.getString("login", "");
        String senhaSalva = temUser.getString("senha", "");
        String emailSalvo = temUser.getString("email", "");
        boolean manterLogado = temUser.getBoolean("manterLogado", false);

        user = new User(nomeSalvo, loginSalvo, senhaSalva, emailSalvo, manterLogado);
        return user;
    }

    //Caso já haja um usuário padrão salvo com manterLogado True, monta a partir das informações salvas
    private boolean montarObjetoSemLogar() {
        SharedPreferences temUser = getSharedPreferences("usuarioPadrao", Activity.MODE_PRIVATE);
        boolean manterLogado = temUser.getBoolean("manterLogado", false);
        return manterLogado;
    }

    //Preenche a lista de contatos para o usuário através de uma conversão de tipos, admito q não entendi perfeitamente
    protected void preencherListaDeContatos(User user) {

        SharedPreferences recuperarContatos = getSharedPreferences("contatos", Activity.MODE_PRIVATE);

        int num = recuperarContatos.getInt("numContatos", 0);
        ArrayList<Contato> contatos = new ArrayList<Contato>();

        Contato contato;


        for (int i = 1; i <= num; i++) {
            String objSel = recuperarContatos.getString("contato" + i, "");
            if (objSel.compareTo("") != 0) {
                try {
                    ByteArrayInputStream bis =
                            new ByteArrayInputStream(objSel.getBytes(StandardCharsets.ISO_8859_1.name()));
                    ObjectInputStream oos = new ObjectInputStream(bis);
                    contato = (Contato) oos.readObject();

                    if (contato != null) {
                        contatos.add(contato);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
        user.setContatos(contatos);
    }

}
