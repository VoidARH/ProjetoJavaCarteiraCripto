/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author void
 */

/** Investidor: uma Pessoa que possui uma Carteira (composicao no UML). **/
public class Investidor extends Pessoa {
    private final carteira carteira;

    public Investidor(String nome, String cpf, String senha, carteira carteira) {
        super(nome, cpf, senha);
        this.carteira = carteira;
    }

    public carteira getCarteira() { return carteira; }
}
