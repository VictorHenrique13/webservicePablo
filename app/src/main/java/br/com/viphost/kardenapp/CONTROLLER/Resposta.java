package br.com.viphost.kardenapp.CONTROLLER;

public class Resposta {
    private Data data;
    private GraphqlError[] errors;
    public Data getData(){
        return data;
    }
    public GraphqlError[] getErrors(){
        return errors;
    }
}
