package br.com.viphost.kardenapp.VIEW;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import br.com.viphost.kardenapp.CONTROLLER.DAO.DbOpenhelper;
import br.com.viphost.kardenapp.CONTROLLER.DeviceInfo;
import br.com.viphost.kardenapp.CONTROLLER.GraphqlClient;
import br.com.viphost.kardenapp.CONTROLLER.GraphqlError;
import br.com.viphost.kardenapp.CONTROLLER.GraphqlResponse;
import br.com.viphost.kardenapp.CONTROLLER.connections.login.AtualizarCategorias;
import br.com.viphost.kardenapp.CONTROLLER.utils.Balao;
import br.com.viphost.kardenapp.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;


public class Login extends Fragment {

    private Button btnGo;
    private TextInputLayout layEmail;
    private TextInputLayout laySenha;
    private TextInputEditText edtSenha;
    private TextInputEditText edtEmail;
    private AlertDialog erro;
    private DbOpenhelper DB;
    public Login() {

    }





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_login, container, false);
        btnGo = v.findViewById(R.id.btnG);
        layEmail = v.findViewById(R.id.layEmail);
        laySenha = v.findViewById(R.id.laySenha);
        edtEmail = v.findViewById(R.id.edtEmail);
        edtSenha = v.findViewById(R.id.edtSenha);
        // dados do login em formato de string
        String email = edtEmail.getText().toString();
        String senha = edtSenha.getText().toString();
        //--------------------------------------------
        DB = new DbOpenhelper(getActivity());


        //verificação de campos - inicio
        btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              if(validate()){
                  //conexao de login realizada aqui
                  final String deviceID = new DeviceInfo().getDeviceID(getContext());
                  final GraphqlClient graphqlClient = new GraphqlClient();
                  final br.com.viphost.kardenapp.CONTROLLER.mutations.Login loginMutation = new br.com.viphost.kardenapp.CONTROLLER.mutations.Login(graphqlClient);
                  final String
                          email = edtEmail.getText().toString(),
                          senha = edtSenha.getText().toString();
                  final ProgressDialog progressDialog= ProgressDialog.show(getContext(), "Logando...","Fazendo Login, aguarde...", true);
                  Runnable runnable = new Runnable() {
                      @Override
                      public void run() {
                          GraphqlResponse resposta = loginMutation.run(email,senha,deviceID);
                          if(resposta instanceof br.com.viphost.kardenapp.CONTROLLER.tipos.Login){
                              br.com.viphost.kardenapp.CONTROLLER.tipos.Login login =(br.com.viphost.kardenapp.CONTROLLER.tipos.Login) resposta;
                              //Memoria.setToken(getContext(), login.getToken());
                              //Memoria.setPermission(getContext(),login.getPermissao());
                              DB.setLogin(login.getToken(),login.getPermissao());
                              DB.setDadosPessoais(login.getNome(),email,login.getTelefone());
                              new Balao(getActivity(),"Login Realizado",Toast.LENGTH_SHORT).show();
                              new AtualizarCategorias(getActivity()).run(true);
                              Intent m = new Intent(getActivity(),Mesas.class);
                              m.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                              startActivity(m);
                              getActivity().finish();
                          }else if(resposta instanceof GraphqlError){
                              final GraphqlError error = (GraphqlError) resposta;
                              new Balao(getActivity(), error.getMessage() + ". " + error.getCategory() + "[" + error.getCode() + "]", Toast.LENGTH_LONG).show();
                          } else{
                              new Balao(getActivity(),"Erro desconhecido",Toast.LENGTH_LONG).show();
                          }
                          progressDialog.dismiss();
                      }
                  };
                  new Thread(runnable).start();
                  ///----------------------
                  //Intent m = new Intent(getActivity(),Mesas.class);
                  //m.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                  //startActivity(m);
              }else{
                    AlertDialog.Builder c = new AlertDialog.Builder(getActivity());
                    c.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            erro.dismiss();
                        }
                    });
                    c.setMessage("Usuário ou senha inválido!");
                    erro = c.create();
                    erro.show();
              }
            }
        });
        //fim
        return v;
    }

//função que valida campos caso queira pode fazer outro validadeou complementar este
    public boolean validate(){
        Boolean ver;
        if(edtEmail.getText().toString().isEmpty()){
            ver = false;
            return ver;
        }else{
            ver = true;
        }
        if(edtSenha.getText().toString().isEmpty()){
            ver = false;
            return ver;
        }else{
            ver = true;
        }
        return ver;
    }
//fim validade

}
