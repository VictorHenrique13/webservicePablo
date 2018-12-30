package br.com.viphost.kardenapp.VIEW;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

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
import br.com.viphost.kardenapp.CONTROLLER.mutations.RegistrarUsuario;
import br.com.viphost.kardenapp.CONTROLLER.tipos.Login;
import br.com.viphost.kardenapp.CONTROLLER.utils.Balao;
import br.com.viphost.kardenapp.R;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;


public class Cadastro extends Fragment {
    private TextInputLayout layNome;
    private TextInputEditText edtNome;
    private TextInputLayout layTelefone;
    private TextInputEditText edtTelefone;
    private TextInputLayout layEmailC;
    private TextInputEditText edtEmailC;
    private TextInputLayout laySenhaC;
    private TextInputEditText edtSenhaC;
    private TextInputLayout laySenhaCf;
    private TextInputEditText edtSenhaCf;
    private Button btnCadastrar;
    AlertDialog erro;
    AlertDialog erroSenha;
    private DbOpenhelper DB;


    public Cadastro() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_cadastro, container, false);
        layNome = v.findViewById(R.id.layNome);
        edtNome = v.findViewById(R.id.edtNome);
        layTelefone = v.findViewById(R.id.layTelefone);
        edtTelefone = v.findViewById(R.id.edtTelefone);
        layEmailC = v.findViewById(R.id.layEmailC);
        edtEmailC = v.findViewById(R.id.edtEmailC);
        laySenhaC = v.findViewById(R.id.laySenhaC);
        edtSenhaC = v.findViewById(R.id.edtSenhaC);
        // dados cliente em string aqui//Precisa ser atualizado a cada vez que aperta o botao
        //é necessario uma contante para Thread e não uma variavel
        String nome = edtNome.getText().toString();
        String telefone = edtTelefone.getText().toString();
        String email = edtEmailC.getText().toString();
        String senha = edtSenhaC.getText().toString();
        btnCadastrar = v.findViewById(R.id.btnCadastrar);
        //----------------------------------------------
        DB = new DbOpenhelper(getActivity());
        //Device ID. Necessario para todas as requisições e só consigo puxar no onCreate
        final String deviceID = new DeviceInfo().getDeviceID(getContext());
        btnCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()){
                    //conexao de casdatro realizada aqui
                    final GraphqlClient graphqlClient = new GraphqlClient();
                    final RegistrarUsuario registro = new RegistrarUsuario(graphqlClient);
                    final String nome = edtNome.getText().toString(),
                            telefone = edtTelefone.getText().toString(),
                            email = edtEmailC.getText().toString(),
                            senha = edtSenhaC.getText().toString();
                    final ProgressDialog progressDialog= ProgressDialog.show(getContext(), "Registrando...","Fazendo Cadastro, aguarde...", true);
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            GraphqlResponse resposta = registro.run(nome,telefone,email,email,senha,deviceID);
                            if(resposta instanceof Login){
                                Login login =(Login) resposta;
                                //Memoria.setToken(getContext(), login.getToken());
                                //Memoria.setPermission(getContext(),login.getPermissao());
                                DB.setLogin(login.getToken(),login.getPermissao());
                                DB.setDadosPessoais(login.getNome(),email,login.getTelefone());
                                new Balao(getActivity(),"Cadastro Realizado",Toast.LENGTH_SHORT).show();
                                Intent m = new Intent(getActivity(),MainActivity.class);
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


                }else{

                    Toast.makeText(getActivity(),"erro",Toast.LENGTH_LONG).show();
                }
            }
        });
        return v;
    }


public boolean validate(){
        boolean ver;
        if(edtNome.getText().toString().isEmpty()){
            ver = false;
        }else{
            ver = true;
        }
        if(edtTelefone.getText().toString().isEmpty()){
            ver = false;
        }else{
            ver = true;
        }
        if(edtEmailC.getText().toString().isEmpty()){
            ver = false;
        }else{
            ver = true;
        }
        if(edtSenhaC.getText().toString().isEmpty()){
            ver = false;
        }else{
            ver = true;
        }
        return ver;
}
    public boolean validateSenha(){
        boolean vers;

        if(edtSenhaC.getText().toString() == edtSenhaCf.getText().toString()){
            vers = true;
        }else{
            vers = false;
        }
        return vers;
    }


}
