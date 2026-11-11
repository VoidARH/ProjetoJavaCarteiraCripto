/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author void
 */

/** Classe abstrata de qualquer moeda guardada na carteira. **/
public abstract class Moedas {
    private final String nome;
    private double saldo;

    public Moedas(String nome, double saldo) {
        this.nome = nome;
        this.saldo = saldo;
    }

    public String getNome()  { return this.nome; }
    public double getSaldo() { return this.saldo; }
    /**
     * Sem "public" de propósito: só as classes do pacote model (a carteira) podem
     * alterar o saldo. Quem está fora (controller, view) precisa usar os métodos da
     * carteira, que validam a operação antes de mexer no saldo.
     */
    void setSaldo(double saldo) { this.saldo = saldo; }
}
