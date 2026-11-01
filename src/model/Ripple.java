/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author void
 */

public class Ripple extends Moedas implements Tarifacao {
    public Ripple(double saldo) { super("XRP", saldo); }

    @Override public double taxaCompra() { return 0.01; }
    @Override public double taxaVenda()  { return 0.01; }
}

