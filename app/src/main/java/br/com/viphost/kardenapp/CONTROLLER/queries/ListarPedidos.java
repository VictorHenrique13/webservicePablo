package br.com.viphost.kardenapp.CONTROLLER.queries;

import com.google.gson.Gson;

import br.com.viphost.kardenapp.CONTROLLER.DeviceInfo;
import br.com.viphost.kardenapp.CONTROLLER.GraphqlClient;
import br.com.viphost.kardenapp.CONTROLLER.GraphqlError;
import br.com.viphost.kardenapp.CONTROLLER.GraphqlResponse;
import br.com.viphost.kardenapp.CONTROLLER.Resposta;

public class ListarPedidos extends Query{
    public ListarPedidos(GraphqlClient graphqlClient) {
        super(graphqlClient);
    }

    /**
     * @return      ListaPedidos ou GraphqlError
     */
    public GraphqlResponse run(int mesa, String token, String id_dispositivo){
        Gson gson = new Gson();
        String dispositivo = new DeviceInfo().getDeviceName();
        String json = graphqlClient.Query(
                "query{" +
                    "listar(token:\"" + token + "\",dispositivo:\"" + dispositivo + "\",id_dispositivo: \"" + id_dispositivo + "\"){" +
                        "pedidos(mesa: "+mesa+"){" +
                            "itens{id,produto{id,nome},quantidade,valor,acrescimoDesconto,comentario}," +
                            "comanda," +
                            "mesa," +
                            "timestamp" +
                        "}" +
                    "}" +
                "}");
        Resposta resposta = graphqlClient.ResponseOf(json);
        GraphqlError[] errors = resposta.getErrors();
        if(errors!=null && errors.length>0){
            return errors[0];
        }else if(resposta.getData()!=null && resposta.getData().getListar() != null)
            return resposta.getData().getListar().getPedidos();
        return null;
    }
}
