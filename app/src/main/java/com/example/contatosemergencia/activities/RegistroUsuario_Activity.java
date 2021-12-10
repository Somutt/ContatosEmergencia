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
import android.widget.Switch;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.contatosemergencia.models.User;
import com.example.contatosemergencia.R;

@SuppressLint("UseSwitchCompatOrMaterialCode")
public class RegistroUsuario_Activity extends AppCompatActivity {

    boolean primeiraVezNome = true;
    boolean primeiraVezLogin = true;
    boolean primeiraVezSenha = true;
    boolean primeiraVezEmail = true;

    EditText edNome;
    EditText edLogin;
    EditText edSenha;
    EditText edEmail;
    Switch swUsrPadrao;
    Button btCriar;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registro_activity);

        btCriar = findViewById(R.id.btCriar);
        edNome = findViewById(R.id.edtNome);
        edLogin = findViewById(R.id.edtLogin);
        edSenha = findViewById(R.id.edtSenha);
        edEmail = findViewById(R.id.edtEmail);
        swUsrPadrao = findViewById(R.id.swUsrPadrao);

        //Excluir o label quando o usuário tocar pela primeira vez no campo de Nome
        edNome.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(primeiraVezNome) {
                    primeiraVezNome = false;
                    edNome.setText("");
                }
                return false;
            }
        });

        //Excluir o label quando o usuário tocar pela primeira vez no campo de Login
        edLogin.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(primeiraVezLogin) {
                    primeiraVezLogin = false;
                    edLogin.setText("");
                }
                return false;
            }
        });

        //Excluir o label quando o usuário tocar pela primeira vez no campo de Senha, incluindo a mudaça de tipo de input
        edSenha.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(primeiraVezSenha) {
                    primeiraVezSenha = false;
                    edSenha.setText("");
                    edSenha.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
                return false;
            }
        });

        //Excluir o label quando o usuário tocar pela primeira vez no campo de Email
        edEmail.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(primeiraVezEmail) {
                    primeiraVezEmail = false;
                    edEmail.setText("");
                }
                return false;
            }
        });

        //Salvando o usuário padrão no armazenamento interno do smartphone com SharedPreferences ao clicar no botão
        btCriar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nome = edNome.getText().toString();
                String login = edLogin.getText().toString();
                String senha = edSenha.getText().toString();
                String email = edEmail.getText().toString();
                boolean manterLogado = swUsrPadrao.isChecked();

                //Cria o SharedPreference com o nome de usuarioPadrao
                SharedPreferences salvarUser = getSharedPreferences("usuarioPadrao", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = salvarUser.edit();

                //Salvando as informações com o método putString
                editor.putString("nome", nome);
                editor.putString("login", login);
                editor.putString("senha", senha);
                editor.putString("email", email);
                editor.putBoolean("manterLogado", manterLogado);

                editor.commit();

                User user = new User(nome, login, senha, email, manterLogado);

                //Mudando de intent para a tela de adicionar contato com as informações do usuario
                Intent intent = new Intent(RegistroUsuario_Activity.this, AlterarContatos_Activity.class);
                intent.putExtra("usuario", user);
                startActivity(intent);

                finish();

                //Log.v("PDM", "Nome: "+nome+", Login: "+login+", Manter Logado? "+manterLogado); Teste
            }
        });

    }
}