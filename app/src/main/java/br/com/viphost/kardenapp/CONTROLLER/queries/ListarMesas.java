package br.com.viphost.kardenapp.CONTROLLER.queries;

import com.google.gson.Gson;

import br.com.viphost.kardenapp.CONTROLLER.DeviceInfo;
import br.com.viphost.kardenapp.CONTROLLER.GraphqlClient;
import br.com.viphost.kardenapp.CONTROLLER.GraphqlError;
import br.com.viphost.kardenapp.CONTROLLER.GraphqlResponse;
import br.com.viphost.kardenapp.CONTROLLER.Resposta;
import br.com.viphost.kardenapp.CONTROLLER.tipos.IntArray;

public class ListarMesas extends Query {
    public ListarMesas(GraphqlClient graphqlClient) {
        super(graphqlClient);
    }


    /**
     * @return      IntArray ou GraphqlError
     */
    public GraphqlResponse run(String token, String id_dispositivo){
        Gson gson = new Gson();
        String dispositivo = new DeviceInfo().getDeviceName();
        String json = graphqlClient.Query(
                "query{" +
                    "listar(token:\"" + token + "\",dispositivo:\"" + dispositivo + "\",id_dispositivo: \"" + id_dispositivo + "\"){" +
                        "mesas" +
                    "}" +
                "}");
        Resposta resposta = graphqlClient.ResponseOf(json);
        GraphqlError[] errors = resposta.getErrors();
        if(errors!=null && errors.length>0){
            return errors[0];
        }else if(resposta.getData()!=null && resposta.getData().getListar() != null)
            return new IntArray(resposta.getData().getListar().getMesas());
        return null;
    }

    /**
     * @return      IntArray ou GraphqlError
     */
    public GraphqlResponse run(String token, String id_dispositivo, boolean aberta){
        Gson gson = new Gson();
        String dispositivo = new DeviceInfo().getDeviceName();
        String strAberta = "false";
        if(aberta)
            strAberta="true";
        String json = graphqlClient.Query(
                "query{" +
                    "listar(token:\"" + token + "\",dispositivo:\"" + dispositivo + "\",id_dispositivo: \"" + id_dispositivo + "\"){" +
                        "mesas(aberta: "+strAberta+")" +
                    "}" +
                "}");
        Resposta resposta = graphqlClient.ResponseOf(json);
        GraphqlError[] errors = resposta.getErrors();
        if(errors!=null && errors.length>0){
            return errors[0];
        }else if(resposta.getData()!=null && resposta.getData().getListar() != null)
            return new IntArray(resposta.getData().getListar().getMesas());
        return null;
    }
}
