package br.com.viphost.kardenapp.CONTROLLER.utils;

public class BinaryTool {
    public static boolean BitValueOfInt(int inteiro, int posicao){
        int resto = -1;
        for(int x = 0 ; x<posicao;x++){
            resto = inteiro%2;
            inteiro = inteiro/2;
        }
        if(resto==1){
            return true;
        }else
            return false;
    }
}
