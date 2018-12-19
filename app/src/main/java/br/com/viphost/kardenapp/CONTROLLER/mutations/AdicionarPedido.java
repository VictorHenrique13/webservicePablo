package br.com.viphost.kardenapp.CONTROLLER.mutations;

import com.google.gson.Gson;

import br.com.viphost.kardenapp.CONTROLLER.DeviceInfo;
import br.com.viphost.kardenapp.CONTROLLER.GraphqlClient;
import br.com.viphost.kardenapp.CONTROLLER.GraphqlError;
import br.com.viphost.kardenapp.CONTROLLER.GraphqlResponse;
import br.com.viphost.kardenapp.CONTROLLER.Resposta;
import br.com.viphost.kardenapp.CONTROLLER.tipos.Logico;

public class AdicionarPedido extends Mutation {
    public AdicionarPedido(GraphqlClient graphqlClient) {
        super(graphqlClient);
    }

    /**
     * @return      import br.com.viphost.graphql.tipos.Logico ou GraphqlError
     */
    public GraphqlResponse run(int id_comanda, String[][] itens, String token, String id_dispositivo) {
        Gson gson = new Gson();
        String dispositivo = new DeviceInfo().getDeviceName();
        String itensStr = "[";
        for(String[] item : itens){
            itensStr += "[\""+item[0]+"\",\""+item[1]+"\",\""+item[2]+"\",\""+item[3]+"\"]";//["idproduto","quantidade","anotacoes","acrescimo/desconto"]
        }
        itensStr += "]";
        String json = graphqlClient.Query(
                "mutation{" +
                    "adicionar(token:\"" + token + "\",dispositivo:\"" + dispositivo + "\",id_dispositivo: \"" + id_dispositivo + "\"){" +
                        "pedido(comanda:"+id_comanda+",itens:"+itensStr+")"+
                    "}" +
                "}");
        Resposta resposta = graphqlClient.ResponseOf(json);
        GraphqlError[] errors = resposta.getErrors();
        if(errors!=null && errors.length>0){
            return errors[0];
        }else if(resposta.getData()!=null && resposta.getData().getAdicionar() != null)
            return new Logico(resposta.getData().getAdicionar().getPedido());
        return null;
    }
}
