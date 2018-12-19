package br.com.viphost.kardenapp.CONTROLLER;

import android.os.AsyncTask;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HttpsURLConnection;

public class GraphqlClient {
    private String host;
    private String path;
    private Gson gson;
    private boolean secure = false;
    private boolean sandbox = true;

    public GraphqlClient(){
        host  = "app.viphost.com.br";
        path = "graphql/index.php";
        gson = new Gson();
    }
    public GraphqlClient(String host){
        this.host  = host;
        path = "";
        gson = new Gson();
    }
    public GraphqlClient(String host, String path){
        this.host  = host;
        this.path = path;
        gson = new Gson();
    }
    public void setSecure(boolean secure){
        this.secure = secure;
    }
    private String sendData(String data) {
        final String finaldata = data;
        AsyncTask at = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                try {
                    String data = (String)objects[0];
                    String protocol;
                    if (secure) protocol = "https://";
                    else protocol = "http://";
                    String url = protocol + host + "/" + path;
                    if(sandbox){
                        System.out.println("SANDBOX ATIVADO");
                        url = protocol + host + "/sandbox/" + path;
                    }

                    BufferedReader in;
                    if (secure) {
                        HttpsURLConnection con = (HttpsURLConnection) new URL(url).openConnection();
                        con.setRequestMethod("POST");
                        con.setRequestProperty("Content-type", "application/json"); //fala o que vai mandar
                        con.setDoOutput(true); //fala que voce vai enviar algo
                        PrintStream printStream = new PrintStream(con.getOutputStream());
                        printStream.println(data); //seta o que voce vai enviar
                        con.connect(); //envia para o servidor
                        in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    } else {
                        HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
                        con.setRequestMethod("POST");
                        con.setRequestProperty("Content-type", "application/json"); //fala o que vai mandar
                        con.setDoOutput(true); //fala que voce vai enviar algo
                        PrintStream printStream = new PrintStream(con.getOutputStream());
                        printStream.println(data); //seta o que voce vai enviar
                        con.connect(); //envia para o servidor
                        in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    }


                    String json = in.readLine();
                    System.out.println(json);
                    in.close();
                    return json;
                }catch (IOException e){
                    String message = e.getMessage().replace("\"", "\\\"");
                    String retorno = "{"+
                                "\"errors\":["+
                                    "{"+
                                        "\"message\": \""+message+"\","+
                                        "\"category\": \"internal\"," +
                                        "\"code\": -2" +
                                    "}"+
                                "]"+
                            "}";
                    System.out.println(retorno);
                    return retorno;
                }
            }
        };
        at.execute(data);
        try {
            return (String)at.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
            return null;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }
    public Resposta ResponseOf(String json){
        if(json==null || json.isEmpty())
            return new Resposta();
        Resposta resposta;
        try {
            resposta = gson.fromJson(json, Resposta.class);
        }catch (Exception e){
            e.printStackTrace();
            resposta = new Resposta();
        }
        return resposta;
    }
    public String Query(String query){
        query = query.replace("\"", "\\\"");
        if(sandbox){
            System.out.println("{\"query\":\""+query+"\"}");
        }
        String data;
        data = sendData("{\"query\":\""+query+"\"}");
        return data;
    }
    public String Mutation(String mutation){
        mutation  = mutation.replace("\"", "\\\"");
        String data;
        data = sendData("{\"mutation\":\""+mutation+"\"}");
        return data;
    }
    public Subscription Subscription(String subscription, SubscriptionListener subscriptionListener){
        subscription  = subscription.replace("\"", "\\\"");
        Subscription subs;
        try {
            subs = new Subscription(host, path, "{\"subscription\":\""+subscription+"\"}", subscriptionListener, true);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return subs;
    }
}
