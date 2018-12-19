package br.com.viphost.kardenapp.CONTROLLER.mutations;

import com.google.gson.Gson;

import br.com.viphost.kardenapp.CONTROLLER.DeviceInfo;
import br.com.viphost.kardenapp.CONTROLLER.GraphqlClient;
import br.com.viphost.kardenapp.CONTROLLER.GraphqlError;
import br.com.viphost.kardenapp.CONTROLLER.GraphqlResponse;
import br.com.viphost.kardenapp.CONTROLLER.Resposta;
import br.com.viphost.kardenapp.CONTROLLER.tipos.Logico;

public class FecharMesa extends Mutation{
    public FecharMesa(GraphqlClient graphqlClient) {
        super(graphqlClient);
    }

    /**
     * @return      import br.com.viphost.graphql.tipos.Logico ou GraphqlError
     */
    public GraphqlResponse run(String token, String id_dispositivo, int numero){
        Gson gson = new Gson();
        String dispositivo = new DeviceInfo().getDeviceName();
        String json = graphqlClient.Query(
                "mutation{" +
                    "fechar(token:\"" + token + "\",dispositivo:\"" + dispositivo + "\",id_dispositivo: \"" + id_dispositivo + "\"){" +
                        "mesa(numero:"+numero+")" +
                    "}" +
                "}");
        Resposta resposta = graphqlClient.ResponseOf(json);
        GraphqlError[] errors = resposta.getErrors();
        if(errors!=null && errors.length>0){
            return errors[0];
        }else if(resposta.getData()!=null && resposta.getData().getFechar() != null)
            return new Logico(resposta.getData().getFechar().getMesa());
        return null;
    }
}
