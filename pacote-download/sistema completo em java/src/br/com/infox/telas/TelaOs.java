package br.com.infox.telas;

import br.com.infox.dal.ModuloConexao;
import java.awt.Color;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import javax.swing.JOptionPane;
import net.proteanit.sql.DbUtils;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JasperViewer;

public class TelaOs extends javax.swing.JInternalFrame {

    private Connection conexao;
    private PreparedStatement pst;
    private ResultSet rs;
    private String tipo;

    public TelaOs() {
        initComponents();
        conexao = ModuloConexao.conector();
    }

    private void pesquisar_Cliente() {
        //as linhas abaixo seleciona o nome, fone e o id do cliente
        String sql = "select id_cli,nome_cli, fone_cli  from tb_clientes where nome_cli  like ?";
        try {
            pst = conexao.prepareStatement(sql);
            pst.setString(1, txtCliPesquisar.getText() + "%");
            rs = pst.executeQuery();
            //a linha abaixo preenche a tabela com o  cliente selecionado
            tblCliente.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    private void preencherCamposOs() {
//as linhas abaixo limpa os campos se eles tiverem ocupados com uma ordem de serviço anterior
        txtOs.setText(null);
        txtData.setText(null);
        rbtOs.setForeground(Color.BLACK);
        rbtOrcamento.setForeground(Color.BLACK);
        txtEquipamento.setText(null);
        txtDefeito.setText(null);
        txtServico.setText(null);
        txtTecnico.setText(null);
        txtValorTotal.setText(null);

        //as linhas abaixo preenche o campo txtCliId
        try {
            int campo = tblCliente.getSelectedRow();
            txtCliId.setText(tblCliente.getModel().getValueAt(campo, 0).toString());
            //as linhas abaixo lança uma exerção caso haja um clik na tabela vázia
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "preencha o campo pesquisar cliente para preencher a tabela");
        }
        //as linhas abaixo preenchem os campos  da ordem de serviço, ligando ao cliente selecionado na tabela
        String sql = "select * from tb_os where id_cli = ?";
        try {

            pst = conexao.prepareStatement(sql);
            pst.setString(1, txtCliId.getText());
            rs = pst.executeQuery();

            if (rs.next()) {
                txtOs.setText(rs.getString(1));
                txtData.setText(rs.getString(2));
                tipo = rs.getString(3);
                if ("Orçamento".equals(tipo)) {
                    rbtOs.setForeground(Color.BLACK);
                    rbtOrcamento.setForeground(Color.red);
                } else {
                    rbtOrcamento.setForeground(Color.BLACK);
                    rbtOs.setForeground(Color.red);
                }
                txtEquipamento.setText(rs.getString(4));
                txtDefeito.setText(rs.getString(5));
                cboOsSituacao.setSelectedItem(rs.getString(6));
                txtServico.setText(rs.getString(7));
                txtTecnico.setText(rs.getString(8));
                txtValorTotal.setText(rs.getString(9).replace(".", ","));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        }
        //as linhas abaixo informam que não existe ordem  de serviço vículado ao cliente selecionado nas linhas acima.
        String sql1 = "select id_os  from tb_os where id_os = ?";

        try {
            pst = conexao.prepareStatement(sql1);
            pst.setString(1, txtOs.getText());
            rs = pst.executeQuery();

            if (rs.next()) {

            } else {
                JOptionPane.showMessageDialog(null, "Este e/ou esta cliente não possui ordem de serviço");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    private boolean seExisteCliente() {
        //as linhas abaixo verifícam se existe cliente vinculado a uma ordem de serviço
        String sql = "select id_cli  from tb_os where id_cli = ? ";
        try {
            pst = conexao.prepareStatement(sql);
            pst.setString(1, txtCliId.getText());
            rs = pst.executeQuery();
            if (rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        }
        return false;
    }

    private void emitirOs() {
        //a linha abaixo chama o metódo que verifíca se existe cliente  vinculado a uma ordem de serviço
        boolean existe = seExisteCliente();
        //a estrutura abaixo permite ou nega que uma ordem de serviço sejá emitida
        if (existe) {
            JOptionPane.showMessageDialog(null, " Ordem de serviço já emitida para esse e/ou essa cliente");
        } else {
            Date data = new Date();
            DateFormat formato = DateFormat.getDateInstance(DateFormat.SHORT);
            //a linha abaixo seta a data atual no campo txtData
            txtData.setText(formato.format(data));
            //a estrutura abaixo verifíca se os campos obrigatórios estão vázios
            if (tipo == null || txtEquipamento.getText().isEmpty() || txtDefeito.getText().isEmpty() || txtServico.getText().isEmpty() || txtTecnico.getText().isEmpty() || txtValorTotal.getText().isEmpty() || txtCliId.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "preencha  os  campos obrigatórios");
            } else {
                //as linhas abaixo pedem comfirmação para emitir uma ordem de serviço
                int emitir = JOptionPane.showConfirmDialog(null, "Tem certeza que deseja emitir esta ordem de serviço ? ", "ATENÇAÕ", JOptionPane.YES_NO_OPTION);
                if (emitir == JOptionPane.YES_OPTION) {
                    //as linhas abaixo  guarda no banco os dados da ordem de serviço
                    try {
                        String sql = "insert into tb_os values(?,?,?,?,?,?,?,?,?,?)";
                        pst = conexao.prepareStatement(sql);
                        pst.setNull(1, 1);
                        pst.setString(2, formato.format(data));
                        pst.setString(3, tipo);
                        pst.setString(4, txtEquipamento.getText());
                        pst.setString(5, txtDefeito.getText());
                        pst.setString(6, cboOsSituacao.getSelectedItem().toString());
                        pst.setString(7, txtServico.getText());
                        pst.setString(8, txtTecnico.getText());
                        pst.setString(9, txtValorTotal.getText().replace(",", "."));
                        pst.setString(10, txtCliId.getText());
                        pst.executeUpdate();
                        //a linha abaixo informa ao usuário que á ordem de serviço foi emitida com sucesso
                        JOptionPane.showMessageDialog(null, "Ordem de Serviço emititida com sucesso");
                        //as linhas abaixo limpa os campos depois que os dados foram guardados no banco
                        txtEquipamento.setText(null);
                        txtDefeito.setText(null);
                        txtServico.setText(null);
                        txtTecnico.setText(null);;
                        txtValorTotal.setText(null);
                        txtCliId.setText(null);
                    } catch (SQLException e) {
                        JOptionPane.showMessageDialog(null, e);
                    }
                }
            }
        }
    }

    private boolean seExisteOs() {
        //as linhas abaixo verifícam se existe ordem de serviço cadastrada no banco
        String sql = "select id_os  from tb_os where id_os = ?";

        try {
            pst = conexao.prepareStatement(sql);
            pst.setString(1, txtOs.getText());
            rs = pst.executeQuery();
            if (rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        }

        return false;
    }

    private void pesquisarOs() {
//a linha abaixo chama o metódo que verifíca se existe ordem de serviço
        boolean existe = seExisteOs();
//a estrutura abaixo pesquisa a ordem de serviço vínculada ao  cliente
        if (existe) {
            String sql = " select  c.id_cli,  c.nome_cli, o.data_os, o.tipo, o.equipamento, o.defeito, o.situação, o.servico, o.tecnico, o.valor  from tb_clientes c  inner join  tb_os o  on  c.id_cli = o.id_cli  where  id_os = ?";

            try {
                pst = conexao.prepareStatement(sql);
                pst.setString(1, txtOs.getText());
                rs = pst.executeQuery();

                if (rs.next()) {
                    txtCliId.setText(rs.getString(1));
                    txtCliPesquisar.setText(rs.getString(2));
                    txtData.setText(rs.getString(3));
                    tipo = rs.getString(4);

                    // estrutura  abaixo  alterna a cor do rádio buttom -> ->
                    if ("Orçamento".equals(tipo)) {
                        rbtOs.setForeground(Color.BLACK);
                        rbtOrcamento.setForeground(Color.red);
                    } else {
                        rbtOrcamento.setForeground(Color.BLACK);
                        rbtOs.setForeground(Color.red);
                    }
                    // -> -> de acordo com  cada ordem de serviço

                    txtEquipamento.setText(rs.getString(5));
                    txtDefeito.setText(rs.getString(6));
                    cboOsSituacao.setSelectedItem(rs.getString(7));
                    txtServico.setText(rs.getString(8));
                    txtTecnico.setText(rs.getString(9));
                    txtValorTotal.setText(rs.getString(10).replace(".", ","));
                }
            } catch (SQLException e) {
                JOptionPane.showConfirmDialog(null, e);
            }

            //as linhas abaixo seleciona o nome, fone e o id do cliente
            String sql2 = "select id_cli,nome_cli, fone_cli  from tb_clientes where nome_cli  like ?";
            try {
                pst = conexao.prepareStatement(sql2);
                pst.setString(1, txtCliPesquisar.getText() + "%");
                rs = pst.executeQuery();
                //a linha abaixo preenche a tabela com o  cliente selecionado
                tblCliente.setModel(DbUtils.resultSetToTableModel(rs));
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, e);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Ordem de serviço não existe");
            //as linhas abaixo limpa os campos se eles tiverem ocupados com uma ordem de serviço anterior
            txtOs.setText(null);
            txtData.setText(null);
            rbtOs.setForeground(Color.BLACK);
            rbtOrcamento.setForeground(Color.BLACK);
            txtEquipamento.setText(null);
            txtDefeito.setText(null);
            txtServico.setText(null);
            txtTecnico.setText(null);
            txtValorTotal.setText(null);
            txtCliPesquisar.setText(null);
            txtCliId.setText(null);
            tblCliente.setValueAt(null, 0, 0);
            tblCliente.setValueAt(null, 0, 1);
            tblCliente.setValueAt(null, 0, 2);
        }
    }

    private void alterar() {

        //a estrutura abaixo verifíca se os campos obrigatórios estão vázios
        if (tipo == null || txtEquipamento.getText().isEmpty() || txtDefeito.getText().isEmpty() || txtServico.getText().isEmpty() || txtTecnico.getText().isEmpty() || txtValorTotal.getText().isEmpty() || txtCliId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "preencha  os  campos obrigatórios");
        } else {
            //as linhas abaixo pedem comfirmação para alterar ordem de serviço
            int alterar = JOptionPane.showConfirmDialog(null, "Tem certeza que deseja alterar ordem de serviço ", "ATENÇÃO", JOptionPane.YES_NO_OPTION);
            if (alterar == JOptionPane.YES_OPTION) {
                //as linhas abaixo altera a ordem de serviço 
                String sql = "update  tb_os  set   tipo = ?, equipamento = ?, defeito = ?, situação = ?, servico = ?, tecnico = ?, valor = ?  where  id_os = ? ";

                try {
                    pst = conexao.prepareStatement(sql);
                    pst.setString(1, tipo);
                    pst.setString(2, txtEquipamento.getText());
                    pst.setString(3, txtDefeito.getText());
                    pst.setString(4, cboOsSituacao.getSelectedItem().toString());
                    pst.setString(5, txtServico.getText());
                    pst.setString(6, txtTecnico.getText());
                    pst.setString(7, txtValorTotal.getText().replace(",", "."));
                    pst.setString(8, txtOs.getText());
                    pst.executeUpdate();
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(null, e);
                }
                //a linha abaixo comfirma a alteração na ordem de serviço
                JOptionPane.showMessageDialog(null, "Ordem de serviço alterada com sucesso");
                //as linhas abaixo limpa os campos se eles tiverem ocupados com uma ordem de serviço anterior
                txtOs.setText(null);
                txtData.setText(null);
                rbtOs.setForeground(Color.BLACK);
                rbtOrcamento.setForeground(Color.BLACK);
                txtEquipamento.setText(null);
                txtDefeito.setText(null);
                txtServico.setText(null);
                txtTecnico.setText(null);
                txtValorTotal.setText(null);
                txtCliPesquisar.setText(null);
                txtCliId.setText(null);
                tblCliente.setValueAt(null, 0, 0);
                tblCliente.setValueAt(null, 0, 1);
                tblCliente.setValueAt(null, 0, 2);
            }
        }
    }

    private void remover() {
        //a estrutura abaixo verifíca se os campos obrigatórios estão vázios
        if (tipo == null || txtEquipamento.getText().isEmpty() || txtDefeito.getText().isEmpty() || txtServico.getText().isEmpty() || txtTecnico.getText().isEmpty() || txtValorTotal.getText().isEmpty() || txtCliId.getText().isEmpty() || txtOs.getText().isEmpty() || txtData.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "preencha  os  campos obrigatórios");
        } else {
            //a estrutura  abaixo pede comfirmação para excluir ordem de serviço
            int excluir = JOptionPane.showConfirmDialog(null, "Tem certeza que deseja excluir  ordem de serviço " + txtOs.getText().toUpperCase(), "ATENÇÃO", JOptionPane.YES_NO_OPTION);

            if (excluir == JOptionPane.YES_OPTION) {
                //as linhas abaixo apagam ordem de serviço
                String sql = "delete  from tb_os where id_os = ?";

                try {
                    pst = conexao.prepareStatement(sql);
                    pst.setString(1, txtOs.getText());
                    int apagar = pst.executeUpdate();
                    //a estrutura abaixo comfirma a exclusão dos dados 
                    if (apagar > 0) {
                        JOptionPane.showMessageDialog(null, "Dados apagados com sucesso");
                        //as linhas abaixo limpa os campos depois da exclusão dos dados
                        txtOs.setText(null);
                        txtData.setText(null);
                        rbtOs.setForeground(Color.BLACK);
                        rbtOrcamento.setForeground(Color.BLACK);
                        txtEquipamento.setText(null);
                        txtDefeito.setText(null);
                        txtServico.setText(null);
                        txtTecnico.setText(null);
                        txtValorTotal.setText(null);
                    } else {
                        JOptionPane.showMessageDialog(null, "Não foi possivel apagar este cliente");
                    }
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(null, e);
                }
            }
        }
    }

    private void imprimirOrdemDeServico() {

        if (tipo == null || txtEquipamento.getText().isEmpty() || txtDefeito.getText().isEmpty() || txtServico.getText().isEmpty() || txtTecnico.getText().isEmpty() || txtValorTotal.getText().isEmpty() || txtCliId.getText().isEmpty()) {

            JOptionPane.showMessageDialog(null, "preencha  os  campos obrigatórios");

        } else {

            int comfirma = JOptionPane.showConfirmDialog(null, "Comfirma impressão de ordem de serviço", "ATENÇÃO", JOptionPane.YES_NO_OPTION);

            if (comfirma == JOptionPane.YES_OPTION) {

                try {
                    HashMap paramento = new HashMap();

                    paramento.put("id_cliente", Integer.parseInt(txtCliId.getText()));

                    JasperPrint print = JasperFillManager.fillReport("C:/Report/MyReports/imprimir_ordem_de_servico.jasper", paramento, conexao);

                    JasperViewer.viewReport(print, true);

                } catch (JRException e) {

                    JOptionPane.showMessageDialog(null, e);

                }
            }
        }
        txtOs.setText(null);
        txtData.setText(null);
        rbtOs.setForeground(Color.BLACK);
        rbtOrcamento.setForeground(Color.BLACK);
        txtEquipamento.setText(null);
        txtDefeito.setText(null);
        txtServico.setText(null);
        txtTecnico.setText(null);
        txtValorTotal.setText(null);
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
        buttonGroup2 = new javax.swing.ButtonGroup();
        buttonGroup3 = new javax.swing.ButtonGroup();
        buttonGroup4 = new javax.swing.ButtonGroup();
        buttonGroup5 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtOs = new javax.swing.JTextField();
        rbtOrcamento = new javax.swing.JRadioButton();
        rbtOs = new javax.swing.JRadioButton();
        txtData = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblCliente = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtCliPesquisar = new javax.swing.JTextField();
        txtCliId = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        cboOsSituacao = new javax.swing.JComboBox<>();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        txtEquipamento = new javax.swing.JTextField();
        txtServico = new javax.swing.JTextField();
        txtValorTotal = new javax.swing.JTextField();
        btnAdicionar = new javax.swing.JButton();
        btnPesquisar = new javax.swing.JButton();
        btnAlterar = new javax.swing.JButton();
        btnExcluir = new javax.swing.JButton();
        btnImprimir = new javax.swing.JButton();
        txtTecnico = new javax.swing.JTextField();
        txtDefeito = new javax.swing.JTextField();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setTitle("Tela Ordem de Serviço");
        setPreferredSize(new java.awt.Dimension(671, 536));

        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel4.setText("Nº OS");

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel5.setText("Data");

        buttonGroup1.add(rbtOrcamento);
        rbtOrcamento.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        rbtOrcamento.setText("Orçamento *");
        rbtOrcamento.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbtOrcamentoActionPerformed(evt);
            }
        });

        buttonGroup1.add(rbtOs);
        rbtOs.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        rbtOs.setText("Ordem de serviço *");
        rbtOs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbtOsActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(28, 28, 28)
                                .addComponent(jLabel4))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addComponent(txtOs, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(45, 45, 45)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(rbtOs)
                                    .addComponent(txtData, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(81, 81, 81)
                                .addComponent(jLabel5))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(rbtOrcamento)))
                .addContainerGap(44, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtData, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtOs, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rbtOrcamento)
                    .addComponent(rbtOs))
                .addContainerGap(22, Short.MAX_VALUE))
        );

        tblCliente.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        tblCliente.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "id_cliente", "nome_cliente", "fone_cliente"
            }
        ));
        tblCliente.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblClienteMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblCliente);

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel1.setText("* Cliente");

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel2.setText("*Id");

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/infox/icones/pesquisar.png"))); // NOI18N

        txtCliPesquisar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtCliPesquisarKeyReleased(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel6.setText("* Campo Obrigatório");

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel7.setText("Situação");

        cboOsSituacao.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        cboOsSituacao.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Entrega OK", "Orsamento Reprovado", "Aguardando Aprovação", "Aguardando Peças", "Abandonado p/ Cliente", "Na Bancada", "Retornou", " " }));

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel8.setText("* Defeito");

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel9.setText("* Técnico");

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel10.setText("* Equipamento");

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel11.setText("* Serviço");

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel12.setText("* Valor Total");

        txtValorTotal.setText("0");

        btnAdicionar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/infox/icones/create.png"))); // NOI18N
        btnAdicionar.setToolTipText("Emitir Os");
        btnAdicionar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAdicionar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAdicionarActionPerformed(evt);
            }
        });

        btnPesquisar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/infox/icones/read.png"))); // NOI18N
        btnPesquisar.setToolTipText("Pesquisar Os");
        btnPesquisar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnPesquisar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPesquisarActionPerformed(evt);
            }
        });

        btnAlterar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/infox/icones/update.png"))); // NOI18N
        btnAlterar.setToolTipText("Alterar Os");
        btnAlterar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAlterarActionPerformed(evt);
            }
        });

        btnExcluir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/infox/icones/delete.png"))); // NOI18N
        btnExcluir.setToolTipText("Excluir Os");
        btnExcluir.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnExcluir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExcluirActionPerformed(evt);
            }
        });

        btnImprimir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/infox/icones/print.png"))); // NOI18N
        btnImprimir.setToolTipText("Imprimir Os");
        btnImprimir.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnImprimir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImprimirActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(39, 39, 39)
                        .addComponent(jLabel7)
                        .addGap(18, 18, 18)
                        .addComponent(cboOsSituacao, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtCliPesquisar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel3)
                        .addGap(41, 41, 41)
                        .addComponent(jLabel2)
                        .addGap(18, 18, 18)
                        .addComponent(txtCliId, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(66, 66, 66))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addGap(35, 35, 35)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel6)
                        .addGap(34, 34, 34))))
            .addGroup(layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addComponent(jLabel10)
                                .addGap(18, 18, 18)
                                .addComponent(txtEquipamento))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(txtValorTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(layout.createSequentialGroup()
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(jLabel8)
                                        .addComponent(jLabel11))
                                    .addGap(24, 24, 24)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(txtServico)
                                        .addComponent(txtDefeito, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 538, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                        .addGap(0, 8, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btnAdicionar)
                                .addGap(24, 24, 24)
                                .addComponent(btnPesquisar)
                                .addGap(27, 27, 27)
                                .addComponent(btnAlterar)
                                .addGap(36, 36, 36)
                                .addComponent(btnExcluir)
                                .addGap(36, 36, 36)
                                .addComponent(btnImprimir))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel9)
                                .addGap(24, 24, 24)
                                .addComponent(txtTecnico, javax.swing.GroupLayout.PREFERRED_SIZE, 309, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel12)))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 20, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7)
                            .addComponent(cboOsSituacao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel6))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtCliPesquisar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel2)
                                .addComponent(txtCliId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 19, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(txtEquipamento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8)
                    .addComponent(txtDefeito, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtServico, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnExcluir, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel9)
                            .addComponent(txtValorTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel12)
                            .addComponent(txtTecnico, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(63, 63, 63)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(btnAdicionar, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(btnPesquisar, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(btnAlterar, javax.swing.GroupLayout.Alignment.TRAILING)))
                            .addComponent(btnImprimir, javax.swing.GroupLayout.Alignment.TRAILING))))
                .addGap(33, 33, 33))
        );

        setBounds(0, 0, 671, 536);
    }// </editor-fold>//GEN-END:initComponents

    private void btnAdicionarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAdicionarActionPerformed
        emitirOs();
    }//GEN-LAST:event_btnAdicionarActionPerformed

    private void txtCliPesquisarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCliPesquisarKeyReleased
        pesquisar_Cliente();
    }//GEN-LAST:event_txtCliPesquisarKeyReleased

    private void tblClienteMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblClienteMouseClicked
        preencherCamposOs();
    }//GEN-LAST:event_tblClienteMouseClicked

    private void rbtOrcamentoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbtOrcamentoActionPerformed
        tipo = "Orçamento";
    }//GEN-LAST:event_rbtOrcamentoActionPerformed

    private void rbtOsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbtOsActionPerformed
        tipo = "Ordem de Seriço";
    }//GEN-LAST:event_rbtOsActionPerformed

    private void btnPesquisarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPesquisarActionPerformed
        pesquisarOs();
    }//GEN-LAST:event_btnPesquisarActionPerformed

    private void btnAlterarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAlterarActionPerformed
        alterar();
    }//GEN-LAST:event_btnAlterarActionPerformed

    private void btnExcluirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExcluirActionPerformed
        remover();
    }//GEN-LAST:event_btnExcluirActionPerformed

    private void btnImprimirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImprimirActionPerformed
        imprimirOrdemDeServico();
    }//GEN-LAST:event_btnImprimirActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdicionar;
    private javax.swing.JButton btnAlterar;
    private javax.swing.JButton btnExcluir;
    private javax.swing.JButton btnImprimir;
    private javax.swing.JButton btnPesquisar;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.ButtonGroup buttonGroup3;
    private javax.swing.ButtonGroup buttonGroup4;
    private javax.swing.ButtonGroup buttonGroup5;
    private javax.swing.JComboBox<String> cboOsSituacao;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JRadioButton rbtOrcamento;
    private javax.swing.JRadioButton rbtOs;
    private javax.swing.JTable tblCliente;
    private javax.swing.JTextField txtCliId;
    private javax.swing.JTextField txtCliPesquisar;
    private javax.swing.JTextField txtData;
    private javax.swing.JTextField txtDefeito;
    private javax.swing.JTextField txtEquipamento;
    private javax.swing.JTextField txtOs;
    private javax.swing.JTextField txtServico;
    private javax.swing.JTextField txtTecnico;
    private javax.swing.JTextField txtValorTotal;
    // End of variables declaration//GEN-END:variables
}
