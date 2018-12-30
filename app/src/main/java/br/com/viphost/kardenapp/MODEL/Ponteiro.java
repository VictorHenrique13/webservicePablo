package br.com.viphost.kardenapp.MODEL;

public class Ponteiro {
    private Object obj;
    public Ponteiro(Object obj){
        this.obj = obj;
    }

    public void setValue(Object obj){
        this.obj=obj;
    }
    public Object getValue(){
        return obj;
    }
}
