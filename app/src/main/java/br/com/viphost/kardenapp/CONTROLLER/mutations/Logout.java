package br.com.viphost.kardenapp.CONTROLLER.mutations;

import com.google.gson.Gson;

import br.com.viphost.kardenapp.CONTROLLER.GraphqlClient;
import br.com.viphost.kardenapp.CONTROLLER.GraphqlError;
import br.com.viphost.kardenapp.CONTROLLER.GraphqlResponse;
import br.com.viphost.kardenapp.CONTROLLER.Resposta;
import br.com.viphost.kardenapp.CONTROLLER.tipos.Logico;

public class Logout extends Mutation{
    public Logout(GraphqlClient graphqlClient) {
        super(graphqlClient);
    }

    /**
     * @return      import br.com.viphost.graphql.tipos.Logico ou GraphqlError
     */
    public GraphqlResponse run(String token, String id_dispositivo){
        Gson gson = new Gson();
        String dispositivo = "Android";//Aq vai ter q usar um codigo para conseguir obter o modelo do aparelho
        String json = graphqlClient.Query("mutation{logout(token:\""+token+"\",dispositivo:\""+dispositivo+"\",id_dispositivo: \""+id_dispositivo+"\")}");
        Resposta resposta = graphqlClient.ResponseOf(json);
        GraphqlError[] errors = resposta.getErrors();
        if(errors!=null && errors.length>0){
            return errors[0];
        }else if(resposta.getData()!=null)
            return new Logico(resposta.getData().getLogout());
        return null;
    }
}
