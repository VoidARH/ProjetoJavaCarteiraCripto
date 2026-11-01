/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author void
 */

/** Classe base de quem usa o sistema (diagrama UML: Pessoa). **/
public abstract class Pessoa {
    private final String nome;
    private final String cpf;
    private final String senha;

    public Pessoa(String nome, String cpf, String senha) {
        this.nome = nome;
        this.cpf = cpf;
        this.senha = senha;
    }

    public String getNome() { return nome; }
    public String getCpf()  { return cpf; }

    /** true se a senha informada e igual z senha da pessoa **/
    public boolean senhaCorreta(String tentativa) {
        return senha.equals(tentativa);
    }
}
