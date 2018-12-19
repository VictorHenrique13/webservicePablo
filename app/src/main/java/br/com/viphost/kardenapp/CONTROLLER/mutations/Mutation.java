package br.com.viphost.kardenapp.CONTROLLER.mutations;

import br.com.viphost.kardenapp.CONTROLLER.GraphqlClient;

public abstract class Mutation {

    protected GraphqlClient graphqlClient;
    public Mutation(GraphqlClient graphqlClient){
        this.graphqlClient=graphqlClient;
    }
}
