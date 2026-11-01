/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author void
 */

/** Interface do UML: toda criptomoeda cobra taxa na compra e na venda. */
public interface Tarifacao {
    /** @return taxa de compra em fração (ex.: 0.02 = 2%) */
    double taxaCompra();

    /** @return taxa de venda em fração */
    double taxaVenda();
}
