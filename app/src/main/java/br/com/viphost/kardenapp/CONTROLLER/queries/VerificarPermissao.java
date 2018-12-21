package br.com.viphost.kardenapp.CONTROLLER.queries;

import com.google.gson.Gson;

import br.com.viphost.kardenapp.CONTROLLER.DeviceInfo;
import br.com.viphost.kardenapp.CONTROLLER.GraphqlClient;
import br.com.viphost.kardenapp.CONTROLLER.GraphqlError;
import br.com.viphost.kardenapp.CONTROLLER.GraphqlResponse;
import br.com.viphost.kardenapp.CONTROLLER.Resposta;

public class VerificarPermissao extends Query {
    public VerificarPermissao(GraphqlClient graphqlClient) {
        super(graphqlClient);
    }

    /**
     * @return      Verificacao ou GraphqlError
     */
    public GraphqlResponse run(String token, String id_dispositivo){
        Gson gson = new Gson();
        String dispositivo = new DeviceInfo().getDeviceName();
        String json = graphqlClient.Query(
                "query{" +
                    "verificar(token:\"" + token + "\",dispositivo:\"" + dispositivo + "\",id_dispositivo: \"" + id_dispositivo + "\"){" +
                        "permissao" +
                    "}" +
                "}");
        Resposta resposta = graphqlClient.ResponseOf(json);
        GraphqlError[] errors = resposta.getErrors();
        if(errors!=null && errors.length>0){
            return errors[0];
        }else if(resposta.getData()!=null && resposta.getData().getVerificar() != null)
            return resposta.getData().getVerificar().getPermissao();
        return null;
    }
}
