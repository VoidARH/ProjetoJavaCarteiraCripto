/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main;

import view.Login;

/**
 *
 * @author void
 */
public class main {

    public static void main(String[] args) {
        // a tela precisa ser criada na thread própria do Swing
        java.awt.EventQueue.invokeLater(() -> new Login().setVisible(true));
    }
}
