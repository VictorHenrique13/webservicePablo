package br.com.viphost.kardenapp.CONTROLLER.queries;

import br.com.viphost.kardenapp.CONTROLLER.GraphqlClient;

public abstract class Query {

    protected GraphqlClient graphqlClient;
    public Query(GraphqlClient graphqlClient){
        this.graphqlClient=graphqlClient;
    }
}
