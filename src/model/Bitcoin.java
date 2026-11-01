/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author void
 */
public class Bitcoin extends Moedas implements Tarifacao {
    public Bitcoin(double saldo) { super("BTC", saldo); }

    @Override public double taxaCompra() { return 0.02; }
    @Override public double taxaVenda()  { return 0.03; }
}

