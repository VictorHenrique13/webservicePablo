package br.com.viphost.kardenapp.CONTROLLER;

public class GraphqlError extends GraphqlResponse{
    private String message;
    private String category;
    private int code =-1;
   // private Location[] locations;

    public String getMessage() {
        return message;
    }

    public String getCategory() {
        return category;
    }

    public int getCode(){
        return code;
    }

   // public Location[] getLocations() {
   //     return locations;
   // }

}
