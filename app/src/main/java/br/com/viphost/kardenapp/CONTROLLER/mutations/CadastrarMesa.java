package br.com.viphost.kardenapp.CONTROLLER.mutations;

import com.google.gson.Gson;

import br.com.viphost.kardenapp.CONTROLLER.DeviceInfo;
import br.com.viphost.kardenapp.CONTROLLER.GraphqlClient;
import br.com.viphost.kardenapp.CONTROLLER.GraphqlError;
import br.com.viphost.kardenapp.CONTROLLER.GraphqlResponse;
import br.com.viphost.kardenapp.CONTROLLER.Resposta;
import br.com.viphost.kardenapp.CONTROLLER.tipos.Logico;

public class CadastrarMesa extends Mutation {
    public CadastrarMesa(GraphqlClient graphqlClient) {
        super(graphqlClient);
    }

    /**
     * @return      br.com.viphost.graphql.tipos.Logico ou GraphqlError
     */
    public GraphqlResponse run(String numero, String token, String id_dispositivo){
        Gson gson = new Gson();
        String dispositivo = new DeviceInfo().getDeviceName();
        String json = graphqlClient.Query(
                "mutation{" +
                    "cadastrar(token:\"" + token + "\",dispositivo:\"" + dispositivo + "\",id_dispositivo: \"" + id_dispositivo + "\"){" +
                        "mesa(numero:"+numero+")" +
                    "}" +
                "}");
        Resposta resposta = graphqlClient.ResponseOf(json);
        GraphqlError[] errors = resposta.getErrors();
        if(errors!=null && errors.length>0){
            return errors[0];
        }else if(resposta.getData()!=null && resposta.getData().getCadastrar() != null)
            return new Logico(resposta.getData().getCadastrar().getMesa());
        return null;
    }

    /**
     * @return      br.com.viphost.graphql.tipos.Logico ou GraphqlError
     */
    public GraphqlResponse run(int numero, String token, String id_dispositivo){
        Gson gson = new Gson();
        String dispositivo = new DeviceInfo().getDeviceName();
        String json = graphqlClient.Query(
                "mutation{" +
                        "cadastrar(token:\"" + token + "\",dispositivo:\"" + dispositivo + "\",id_dispositivo: \"" + id_dispositivo + "\"){" +
                        "mesa(numero:"+numero+")" +
                        "}" +
                        "}");
        Resposta resposta = graphqlClient.ResponseOf(json);
        GraphqlError[] errors = resposta.getErrors();
        if(errors!=null && errors.length>0){
            return errors[0];
        }else if(resposta.getData()!=null && resposta.getData().getCadastrar() != null)
            return new Logico(resposta.getData().getCadastrar().getMesa());
        return null;
    }
}
