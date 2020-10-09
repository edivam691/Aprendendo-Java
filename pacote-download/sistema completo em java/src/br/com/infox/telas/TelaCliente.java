package br.com.infox.telas;

import br.com.infox.dal.ModuloConexao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import net.proteanit.sql.DbUtils;

public class TelaCliente extends javax.swing.JInternalFrame {

    private Connection conexao;
    private PreparedStatement pst;
    private ResultSet rs;

    public TelaCliente() {
        initComponents();
        conexao = ModuloConexao.conector();
    }

    private void adicionar() {
        //a estrtura abaixo testa se os campos  obrigatórios estão preechidos
        if (txtCliNome.getText().isEmpty() || txtCliEndereco.getText().isEmpty() || txtCliEmail.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, " ( * ) Preencha os campos obrigatorios");
        } else {//a estrutura abaixo pede comfirmação para adicionar
            int alterar = JOptionPane.showConfirmDialog(null, "Tem certeza que deseja adicionar o /ou a  cliente " + txtCliNome.getText() + " ?", "ATENÇÃO", JOptionPane.YES_NO_OPTION);
            if (alterar == JOptionPane.YES_OPTION) {
                //as linhas abaixo adicionam um novo cliente
                String sql = "insert into tb_clientes values(?,?,?,?,?)";
                try {
                    pst = conexao.prepareStatement(sql);
                    pst.setNull(1, 1);
                    pst.setString(2, txtCliNome.getText());
                    pst.setString(3, txtCliEndereco.getText());
                    pst.setString(4, txtCliTelefone.getText());
                    pst.setString(5, txtCliEmail.getText());
                    int adicionar = pst.executeUpdate();
                    if (adicionar > 0) {//as linhas abaixo comfirma o cadastro e lipa os campos
                        JOptionPane.showMessageDialog(null, "Cliente adicionado com sucesso");
                        txtCliNome.setText(null);
                        txtCliEndereco.setText(null);
                        txtCliTelefone.setText(null);
                        txtCliEmail.setText(null);
                    }
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(null, e);
                }
            }
        }
    }

    private void pesquisar_Cliente() {

        String sql = "select *  from  tb_clientes where  nome_cli   like ?";
        //a estrutura abaixo faz a pesquisa dos clientes 
        try {
            pst = conexao.prepareStatement(sql);
            pst.setString(1, txtCliPesquisar.getText() + "%");
            rs = pst.executeQuery();
            //a linha abaixo seta os clientes pesquisados na tabela
            tblCliente.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    private void preencherCamposCliente() {
        //as linhas abaixo preenche os campos do cliente  quando dá um clik na tabela e tambem lança uma exeção se dé um clik na tabela vázia.
        try {
            int campo = tblCliente.getSelectedRow();
            txtCliNome.setText(tblCliente.getModel().getValueAt(campo, 1).toString());
            txtCliEndereco.setText(tblCliente.getModel().getValueAt(campo, 2).toString());
            txtCliTelefone.setText(tblCliente.getModel().getValueAt(campo, 3).toString());
            txtCliEmail.setText(tblCliente.getModel().getValueAt(campo, 4).toString());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Preecha o campo pesquisar para preencher a tabela.");
        }
    }

    private void alterar() {
        //a estrutura abaixo testa se os campos estão vázios para alterar() 
        if (txtCliNome.getText().isEmpty() || txtCliEndereco.getText().isEmpty() || txtCliEmail.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, " ( * ) Preencha os campos obrigatorios");
        } else {//as linhas abaixo altera os dados
            String sql = "update tb_clientes set nome_cli = ?, end_cli = ?, fone_cli = ?, email_cli = ? where  id_cli = ?";
            int campo = tblCliente.getSelectedRow();
            //a estrutura abaixo pede comfirmação do cliente a ser alterado
            if (campo < 0) {
                JOptionPane.showMessageDialog(null, "clik na tabela o nome do /ou da cliente que deseja alterar.");
            } else {//a estrutura abaixo pede comfirmação para alterar
                int alterar = JOptionPane.showConfirmDialog(null, "Tem certeza que deseja alterar o ou/a  cliente " + tblCliente.getModel().getValueAt(campo, 1).toString() + " ?", "ATENÇÃO", JOptionPane.YES_NO_OPTION);
                if (alterar == JOptionPane.YES_OPTION) {
                    //as linhas abaixo alteram os dados 
                    try {
                        pst = conexao.prepareStatement(sql);
                        pst.setString(1, txtCliNome.getText());
                        pst.setString(2, txtCliEndereco.getText());
                        pst.setString(3, txtCliTelefone.getText());
                        pst.setString(4, txtCliEmail.getText());
                        pst.setString(5, tblCliente.getModel().getValueAt(campo, 0).toString());
                        pst.executeUpdate();
                        JOptionPane.showMessageDialog(null, "dados alterados com sucesso");//as linhas abaixo  limpa os campos
                        txtCliNome.setText(null);
                        txtCliEndereco.setText(null);
                        txtCliTelefone.setText(null);
                        txtCliEmail.setText(null);
                    } catch (SQLException e) {
                        JOptionPane.showMessageDialog(null, e);
                    }
                }
            }
        }
    }

    private void remover() {       
        //a estrutura abaixo testa se os campos estão vázios para alterar() 
        if (txtCliNome.getText().isEmpty() || txtCliEndereco.getText().isEmpty() || txtCliEmail.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, " ( * ) Preencha os campos obrigatorios");
        } else {//as linhas abaixo pede para selecionar o cliente a ser apagado  caso o campo pesquisar  esteja vázio
            int campo = tblCliente.getSelectedRow();
            if (campo < 0) {
                JOptionPane.showMessageDialog(null, "clik na tabela o nome do/ou da cliente que deseja excluir.");
            } else {

                //as linhas abaixo comfirmam  e  informam que o cliente está vínculado a uma ordem de serviço
                String sql2 = "select  id_cli  from tb_os where id_cli = ?";
                try {
                    pst = conexao.prepareStatement(sql2);
                    pst.setString(1, tblCliente.getModel().getValueAt(campo, 0).toString());
                    rs = pst.executeQuery();

                    if (rs.next()) {
                        JOptionPane.showMessageDialog(null, "Este cliente está relacionado a uma ordem de serviço, para  removê-lo, voçê terá que remover primeiro, a ordem de serviço.");
                    } else {
                        //a estrutura abaixo pede comfirmação para apagar o cliente caso ele não esteja vínculado a uma ordem de serviço
                        int alterar = JOptionPane.showConfirmDialog(null, "Tem certeza que deseja excluir o/ou a cliente " + tblCliente.getModel().getValueAt(campo, 1).toString() + " ?", "ATENÇÃO", JOptionPane.YES_NO_OPTION);
                        if (alterar == JOptionPane.YES_OPTION) {
                            //as linhas abaixo apaga o cliente
                            String sql = "delete from tb_clientes  where id_cli = ?";

                            try {
                                pst = conexao.prepareStatement(sql);
                                pst.setString(1, tblCliente.getModel().getValueAt(campo, 0).toString());
                                int excluidos = pst.executeUpdate();
                                //a estrutura abaixo cofirma que os dados  foi excluidos 
                                if (excluidos > 0) {
                                    JOptionPane.showMessageDialog(null, "dados excluindos com sucesso");//as linhas abaixo lipa os campos depois de apagados
                                    txtCliNome.setText(null);
                                    txtCliEndereco.setText(null);
                                    txtCliTelefone.setText(null);
                                    txtCliEmail.setText(null);
                                } else {
                                    JOptionPane.showMessageDialog(null, "Não foi posivel apagar este cliente");
                                }
                            } catch (SQLException e) {
                                JOptionPane.showMessageDialog(null, e);
                            }
                        }
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, ex);
                }
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblCliente = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        txtCliEndereco = new javax.swing.JTextField();
        txtCliTelefone = new javax.swing.JTextField();
        txtCliNome = new javax.swing.JTextField();
        txtCliEmail = new javax.swing.JTextField();
        btnCliAdicionar = new javax.swing.JButton();
        btnCliAlterar = new javax.swing.JButton();
        btnCliExcluir = new javax.swing.JButton();
        txtCliPesquisar = new javax.swing.JTextField();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setTitle("Cliente");
        setPreferredSize(new java.awt.Dimension(671, 536));

        tblCliente.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "", "", "", ""
            }
        ));
        tblCliente.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblClienteMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblCliente);

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/infox/icones/pesquisar.png"))); // NOI18N

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 15)); // NOI18N
        jLabel2.setText("(*)Campo Obrigatório");

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel3.setText("* nome");

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel4.setText("* Endereço");

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel5.setText("Telefone");

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel6.setText("* Email");

        btnCliAdicionar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/infox/icones/create.png"))); // NOI18N
        btnCliAdicionar.setToolTipText("Adicionar");
        btnCliAdicionar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnCliAdicionar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCliAdicionarActionPerformed(evt);
            }
        });

        btnCliAlterar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/infox/icones/update.png"))); // NOI18N
        btnCliAlterar.setToolTipText("Alterar");
        btnCliAlterar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnCliAlterar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCliAlterarActionPerformed(evt);
            }
        });

        btnCliExcluir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/infox/icones/delete.png"))); // NOI18N
        btnCliExcluir.setToolTipText("Excluir");
        btnCliExcluir.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnCliExcluir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCliExcluirActionPerformed(evt);
            }
        });

        txtCliPesquisar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCliPesquisarActionPerformed(evt);
            }
        });
        txtCliPesquisar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtCliPesquisarKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 101, Short.MAX_VALUE)
                        .addComponent(btnCliAdicionar)
                        .addGap(74, 74, 74)
                        .addComponent(btnCliAlterar)
                        .addGap(77, 77, 77)
                        .addComponent(btnCliExcluir)
                        .addGap(102, 102, 102))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(txtCliPesquisar)
                        .addGap(10, 10, 10)
                        .addComponent(jLabel1)
                        .addGap(55, 55, 55)
                        .addComponent(jLabel2)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel4)
                                    .addComponent(jLabel6))
                                .addGap(18, 18, 18)
                                .addComponent(txtCliEndereco))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel5)
                                    .addComponent(jLabel3))
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(18, 18, 18)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(txtCliEmail)
                                            .addComponent(txtCliTelefone)))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(9, 9, 9)
                                        .addComponent(txtCliNome))))
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING))
                        .addGap(30, 30, 30))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jLabel1)
                    .addComponent(txtCliPesquisar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(33, 33, 33)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(36, 36, 36)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtCliNome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(26, 26, 26)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(txtCliEndereco, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(28, 28, 28)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtCliTelefone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(28, 28, 28)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(txtCliEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(27, 27, 27)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnCliAlterar)
                    .addComponent(btnCliExcluir)
                    .addComponent(btnCliAdicionar))
                .addContainerGap(28, Short.MAX_VALUE))
        );

        setBounds(0, 0, 671, 536);
    }// </editor-fold>//GEN-END:initComponents

    private void btnCliAdicionarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCliAdicionarActionPerformed
        //a linha abaixo chama o metódo adicionar
        adicionar();
    }//GEN-LAST:event_btnCliAdicionarActionPerformed

    private void txtCliPesquisarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCliPesquisarActionPerformed

    }//GEN-LAST:event_txtCliPesquisarActionPerformed

    private void btnCliAlterarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCliAlterarActionPerformed
        //a linha abaixo chama o metódo alterar
        alterar();
    }//GEN-LAST:event_btnCliAlterarActionPerformed

    private void txtCliPesquisarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCliPesquisarKeyReleased
        //a linha abaixo  chama o metódo pesquisar_cliente
        pesquisar_Cliente();
    }//GEN-LAST:event_txtCliPesquisarKeyReleased

    private void tblClienteMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblClienteMouseClicked
        //a linha abaixo chama o matódo preencherCamposCliente
        preencherCamposCliente();
    }//GEN-LAST:event_tblClienteMouseClicked

    private void btnCliExcluirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCliExcluirActionPerformed
        //a linha abaixo chama o metódo remover
        remover();
    }//GEN-LAST:event_btnCliExcluirActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCliAdicionar;
    private javax.swing.JButton btnCliAlterar;
    private javax.swing.JButton btnCliExcluir;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblCliente;
    private javax.swing.JTextField txtCliEmail;
    private javax.swing.JTextField txtCliEndereco;
    private javax.swing.JTextField txtCliNome;
    private javax.swing.JTextField txtCliPesquisar;
    private javax.swing.JTextField txtCliTelefone;
    // End of variables declaration//GEN-END:variables
}
