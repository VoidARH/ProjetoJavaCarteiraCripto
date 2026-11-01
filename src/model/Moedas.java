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

    public String getNome()  { return nome; }
    public double getSaldo() { return saldo; }
    public void setSaldo(double saldo) { this.saldo = saldo; }
}
