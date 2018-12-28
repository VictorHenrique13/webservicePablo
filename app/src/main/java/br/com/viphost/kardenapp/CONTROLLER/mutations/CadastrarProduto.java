package br.com.viphost.kardenapp.CONTROLLER.mutations;

import com.google.gson.Gson;

import br.com.viphost.kardenapp.CONTROLLER.DeviceInfo;
import br.com.viphost.kardenapp.CONTROLLER.GraphqlClient;
import br.com.viphost.kardenapp.CONTROLLER.GraphqlError;
import br.com.viphost.kardenapp.CONTROLLER.GraphqlResponse;
import br.com.viphost.kardenapp.CONTROLLER.Resposta;

public class CadastrarProduto extends Mutation {
    public CadastrarProduto(GraphqlClient graphqlClient) {
        super(graphqlClient);
    }


    /**
     * @return      br.com.viphost.graphql.tipos.Inteiro ou GraphqlError
     */
    public GraphqlResponse run(String nome, double valor, int idCategoria, String token, String id_dispositivo){
        Gson gson = new Gson();
        String dispositivo = new DeviceInfo().getDeviceName();
        String json = graphqlClient.Query(
                "mutation{" +
                    "cadastrar(token:\"" + token + "\",dispositivo:\"" + dispositivo + "\",id_dispositivo: \"" + id_dispositivo + "\"){" +
                        "produto(nome:\""+nome+"\", valor:"+valor+", categoria:"+idCategoria+")" +
                    "}" +
                "}");
        Resposta resposta = graphqlClient.ResponseOf(json);
        GraphqlError[] errors = resposta.getErrors();
        if(errors!=null && errors.length>0){
            return errors[0];
        }else if(resposta.getData()!=null && resposta.getData().getCadastrar() != null)
            return resposta.getData().getCadastrar().getProduto();
        return null;
    }
    /**
     * @return      br.com.viphost.graphql.tipos.Inteiro ou GraphqlError
     */
    public GraphqlResponse run(String nome, double valor, String nomeCategoria, String token, String id_dispositivo){
        Gson gson = new Gson();
        String dispositivo = new DeviceInfo().getDeviceName();
        String json = graphqlClient.Query(
                "mutation{" +
                    "cadastrar(token:\"" + token + "\",dispositivo:\"" + dispositivo + "\",id_dispositivo: \"" + id_dispositivo + "\"){" +
                        "produto(nome:\""+nome+"\", valor:"+valor+", categoriaStr:\""+nomeCategoria+"\", categoria:-1)" +
                    "}" +
                "}");
        Resposta resposta = graphqlClient.ResponseOf(json);
        GraphqlError[] errors = resposta.getErrors();
        if(errors!=null && errors.length>0){
            return errors[0];
        }else if(resposta.getData()!=null && resposta.getData().getCadastrar() != null)
            return resposta.getData().getCadastrar().getProduto();
        return null;
    }
}
