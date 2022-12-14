/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.mario.bancos.Ventanas.Paneles;

import com.mario.bancos.Clases.Hash;
import com.mario.bancos.Clases.Usuario;
import com.mario.bancos.Cliente;

import java.awt.BorderLayout;
import java.security.NoSuchAlgorithmException;

/**
 *
 * @author MarioZatonToledo
 */
public class Panel_inicioSesion extends javax.swing.JPanel {

    /**
     * Creates new form Panel_inicioSesion
     */
    public Panel_inicioSesion() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        t_usuario = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        t_pass = new javax.swing.JPasswordField();
        b_inicioSesion = new javax.swing.JButton();

        setPreferredSize(new java.awt.Dimension(622, 380));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setText("Iniciar sesión");

        jLabel2.setText("Usuario:");

        jLabel3.setText("Contraseña:");

        b_inicioSesion.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        b_inicioSesion.setText("Entrar");
        b_inicioSesion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                try {
                    b_inicioSesionActionPerformed(evt);
                } catch (NoSuchAlgorithmException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(232, 232, 232)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(23, 23, 23)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(54, 54, 54)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(49, 49, 49)
                        .addComponent(jLabel3))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(t_usuario)
                        .addComponent(t_pass, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(38, 38, 38)
                        .addComponent(b_inicioSesion)))
                .addContainerGap(234, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addComponent(jLabel1)
                .addGap(37, 37, 37)
                .addComponent(jLabel2)
                .addGap(18, 18, 18)
                .addComponent(t_usuario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(29, 29, 29)
                .addComponent(jLabel3)
                .addGap(18, 18, 18)
                .addComponent(t_pass, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(50, 50, 50)
                .addComponent(b_inicioSesion)
                .addContainerGap(55, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void b_inicioSesionActionPerformed(java.awt.event.ActionEvent evt) throws NoSuchAlgorithmException {//GEN-FIRST:event_b_inicioSesionActionPerformed

        String usuario = t_usuario.getText();
        String pass = t_pass.getText();

        if(!usuario.isEmpty() && !pass.isEmpty()){
            Usuario u = new Usuario();
            u.setUsuario(usuario);

            Hash md = new Hash();
            pass = md.hashear(t_pass.getText());
            u.setContrasena(pass);
            u.setMetodo("loginUsuario");
            Cliente.login(u);
        }

    }//GEN-LAST:event_b_inicioSesionActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton b_inicioSesion;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPasswordField t_pass;
    private javax.swing.JTextField t_usuario;
    // End of variables declaration//GEN-END:variables
}
