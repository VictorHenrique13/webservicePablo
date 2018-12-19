package br.com.viphost.kardenapp.CONTROLLER.mutations;

import com.google.gson.Gson;

import br.com.viphost.kardenapp.CONTROLLER.DeviceInfo;
import br.com.viphost.kardenapp.CONTROLLER.GraphqlClient;
import br.com.viphost.kardenapp.CONTROLLER.GraphqlError;
import br.com.viphost.kardenapp.CONTROLLER.GraphqlResponse;
import br.com.viphost.kardenapp.CONTROLLER.Resposta;
import br.com.viphost.kardenapp.CONTROLLER.tipos.Inteiro;

public class AbrirComanda extends Mutation {
    public AbrirComanda(GraphqlClient graphqlClient) {
        super(graphqlClient);
    }

    /**
     * @return      br.com.viphost.graphql.tipos.Inteiro ou GraphqlError
     */
    public GraphqlResponse run(String token,String id_dispositivo, int mesa) {
        Gson gson = new Gson();
        String dispositivo = new DeviceInfo().getDeviceName();
        String json = graphqlClient.Query(
                "mutation{" +
                        "abrir(token:\"" + token + "\",dispositivo:\"" + dispositivo + "\",id_dispositivo: \"" + id_dispositivo + "\"){" +
                            "comanda(mesa:" + mesa + ")" +
                        "}" +
                "}");
        Resposta resposta = graphqlClient.ResponseOf(json);
        GraphqlError[] errors = resposta.getErrors();
        if (errors != null && errors.length > 0) {
            return errors[0];
        } else if (resposta.getData() != null && resposta.getData().getAbrir() != null)
            return new Inteiro(resposta.getData().getAbrir().getComanda());
        return null;
    }
}
