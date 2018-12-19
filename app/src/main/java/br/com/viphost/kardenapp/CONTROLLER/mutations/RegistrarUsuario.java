package br.com.viphost.kardenapp.CONTROLLER.mutations;

import com.google.gson.Gson;

import br.com.viphost.kardenapp.CONTROLLER.DeviceInfo;
import br.com.viphost.kardenapp.CONTROLLER.GraphqlClient;
import br.com.viphost.kardenapp.CONTROLLER.GraphqlError;
import br.com.viphost.kardenapp.CONTROLLER.GraphqlResponse;
import br.com.viphost.kardenapp.CONTROLLER.Resposta;

public class RegistrarUsuario extends Mutation{
    public RegistrarUsuario(GraphqlClient graphqlClient) {
        super(graphqlClient);
    }

    /**
     * @return      Login ou GraphqlError
     */
    public GraphqlResponse run(String nome, String telefone, String email, String usuario, String senha, String id_dispositivo){
        Gson gson = new Gson();
        String dispositivo = new DeviceInfo().getDeviceName();//Aq vai ter q usar um codigo para conseguir obter o modelo do aparelho
        String json = graphqlClient.Query(
                "mutation{" +
                    "registrar(dispositivo:\""+dispositivo+"\",id_dispositivo: \""+id_dispositivo+"\"){" +
                        "usuario(nome:\""+nome+"\",telefone:\""+telefone+"\",email:\""+email+"\",usuario: \""+usuario+"\",senha: \""+senha+"\"){" +
                            "token,permissao" +
                        "}" +
                    "}" +
                "}"
        );
        Resposta resposta = graphqlClient.ResponseOf(json);
        GraphqlError[] errors = resposta.getErrors();
        if(errors!=null && errors.length>0){
            return errors[0];
        }else if(resposta.getData()!=null && resposta.getData().getRegistrar() != null)
            return resposta.getData().getRegistrar().getUsuario();
        return null;
    }
}