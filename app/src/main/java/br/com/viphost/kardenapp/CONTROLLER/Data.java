package br.com.viphost.kardenapp.CONTROLLER;

import br.com.viphost.kardenapp.CONTROLLER.tipos.Cadastro;
import br.com.viphost.kardenapp.CONTROLLER.tipos.Abertura;
import br.com.viphost.kardenapp.CONTROLLER.tipos.Adicao;
import br.com.viphost.kardenapp.CONTROLLER.tipos.Fechamento;
import br.com.viphost.kardenapp.CONTROLLER.tipos.Listagem;
import br.com.viphost.kardenapp.CONTROLLER.tipos.Login;
import br.com.viphost.kardenapp.CONTROLLER.tipos.Registro;
import br.com.viphost.kardenapp.CONTROLLER.tipos.Verificacao;

public class Data {
    private Login login = null;
    private Registro registrar = null;
    private Boolean logout = false;
    private Abertura abrir = null;
    private Fechamento fechar = null;
    private Adicao adicionar = null;
    private Listagem listar = null;
    private Cadastro cadastrar = null;
    private Verificacao verificar = null;

    public Login getLogin() {
        return login;
    }

    public boolean getLogout() {
        return logout;
    }

    public Abertura getAbrir() {
        return abrir;
    }

    public Adicao getAdicionar() {
        return adicionar;
    }

    public Fechamento getFechar() {
        return fechar;
    }

    public Listagem getListar() {
        return listar;
    }

    public Registro getRegistrar() {
        return registrar;
    }

    public Cadastro getCadastrar() {
        return cadastrar;
    }

    public Verificacao getVerificar() {
        return verificar;
    }
}
