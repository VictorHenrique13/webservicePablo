package br.com.viphost.kardenapp.CONTROLLER.mutations;

import com.google.gson.Gson;

import br.com.viphost.kardenapp.CONTROLLER.DeviceInfo;
import br.com.viphost.kardenapp.CONTROLLER.GraphqlClient;
import br.com.viphost.kardenapp.CONTROLLER.GraphqlError;
import br.com.viphost.kardenapp.CONTROLLER.GraphqlResponse;
import br.com.viphost.kardenapp.CONTROLLER.Resposta;

public class AbrirMesa extends Mutation{
    public AbrirMesa(GraphqlClient graphqlClient) {
        super(graphqlClient);
    }

    /**
     * @return      Mesa ou GraphqlError
     */
    public GraphqlResponse run(String token, String id_dispositivo, int numero){
        Gson gson = new Gson();
        String dispositivo = new DeviceInfo().getDeviceName();
        String json = graphqlClient.Query(
                "mutation{" +
                        "abrir(token:\"" + token + "\",dispositivo:\"" + dispositivo + "\",id_dispositivo: \"" + id_dispositivo + "\"){" +
                        "mesa(numero:"+numero+"){" +
                        "comanda,jaAberta" +
                        "}" +
                        "}" +
                        "}");
        Resposta resposta = graphqlClient.ResponseOf(json);
        GraphqlError[] errors = resposta.getErrors();
        if(errors!=null && errors.length>0){
            return errors[0];
        }else if(resposta.getData()!=null && resposta.getData().getAbrir() != null)
            return resposta.getData().getAbrir().getMesa();
        return null;
    }
    /**
     * @return      Mesa ou GraphqlError
     */
    public GraphqlResponse run(String token, String id_dispositivo, String numero){
        Gson gson = new Gson();
        String dispositivo = new DeviceInfo().getDeviceName();
        String json = graphqlClient.Query(
                "mutation{" +
                    "abrir(token:\"" + token + "\",dispositivo:\"" + dispositivo + "\",id_dispositivo: \"" + id_dispositivo + "\"){" +
                        "mesa(numero:"+numero+"){" +
                            "comanda,jaAberta" +
                        "}" +
                    "}" +
                "}");
        Resposta resposta = graphqlClient.ResponseOf(json);
        GraphqlError[] errors = resposta.getErrors();
        if(errors!=null && errors.length>0){
            return errors[0];
        }else if(resposta.getData()!=null && resposta.getData().getAbrir() != null)
            return resposta.getData().getAbrir().getMesa();
        return null;
    }
}
