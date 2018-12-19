package br.com.viphost.kardenapp.CONTROLLER;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.TimerTask;

import javax.net.ssl.HttpsURLConnection;

public class Subscription extends TimerTask {
    BufferedReader in;
    SubscriptionListener subscriptionListener;

    public Subscription(String host, String path, String subscription, SubscriptionListener sl, boolean secure) throws IOException {
        subscriptionListener = sl;
        String protocol;
        if(secure) protocol="https://";
        else protocol="http://";
        String url = protocol+host+"/"+path;

        if (secure) {
            HttpsURLConnection con = (HttpsURLConnection) new URL("https://" + url).openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-type", "application/json"); //fala o que vai mandar
            con.setDoOutput(true); //fala que voce vai enviar algo
            PrintStream printStream = new PrintStream(con.getOutputStream());
            printStream.println(subscription); //seta o que voce vai enviar
            con.connect(); //envia para o servidor
            in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        } else {
            HttpURLConnection con = (HttpURLConnection) new URL("http://" + url).openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-type", "application/json"); //fala o que vai mandar
            con.setDoOutput(true); //fala que voce vai enviar algo
            PrintStream printStream = new PrintStream(con.getOutputStream());
            printStream.println(subscription); //seta o que voce vai enviar
            con.connect(); //envia para o servidor
            in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        }
    }
    public void cancelar(){
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.cancel();

    }

    @Override
    public void run() {
        while(true) {
            String json = null;
            try {
                json = in.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(json!=null){
                subscriptionListener.updateEvent(json);
            }
        }
    }
}
