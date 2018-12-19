package br.com.viphost.kardenapp.CONTROLLER.mutations;

import com.google.gson.Gson;

import br.com.viphost.kardenapp.CONTROLLER.DeviceInfo;
import br.com.viphost.kardenapp.CONTROLLER.GraphqlClient;
import br.com.viphost.kardenapp.CONTROLLER.GraphqlError;
import br.com.viphost.kardenapp.CONTROLLER.GraphqlResponse;
import br.com.viphost.kardenapp.CONTROLLER.Resposta;

public class CadastrarCategoria extends Mutation {
    public CadastrarCategoria(GraphqlClient graphqlClient) {
        super(graphqlClient);
    }


    /**
     * @return      br.com.viphost.graphql.tipos.Inteiro ou GraphqlError
     */
    public GraphqlResponse run(String nome, String token, String id_dispositivo){
        Gson gson = new Gson();
        String dispositivo = new DeviceInfo().getDeviceName();
        String json = graphqlClient.Query(
                "mutation{" +
                    "cadastrar(token:\"" + token + "\",dispositivo:\"" + dispositivo + "\",id_dispositivo: \"" + id_dispositivo + "\"){" +
                        "categoria(nome:\""+nome+"\")" +
                    "}" +
                "}");
        Resposta resposta = graphqlClient.ResponseOf(json);
        GraphqlError[] errors = resposta.getErrors();
        if(errors!=null && errors.length>0){
            return errors[0];
        }else if(resposta.getData()!=null && resposta.getData().getCadastrar() != null)
            return resposta.getData().getCadastrar().getCategoria();
        return null;
    }
}
