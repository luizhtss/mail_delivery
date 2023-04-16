package br.com.galaxmania.cash.mailsystem.services;

import java.sql.SQLException;

import com.mysql.jdbc.PreparedStatement;

import br.com.galaxmania.cash.mailsystem.MailDelivery;
import br.com.galaxmania.cash.mailsystem.OrderObject;
import br.com.galaxmania.cash.mailsystem.utils.MySQLApi;

public class MySQLService implements IService {

    private MailDelivery mailDelivery;

    private OrderObject oo;

    private MySQLApi SQL_API;

    public MySQLService(MailDelivery mailDelivery) {
        this.mailDelivery = mailDelivery;
    }


    @Override
    public void runService() throws Exception {
        // TODO Auto-generated method stub
        mailDelivery.mailLog("Conectando ao banco de dados...");
        SQL_API = new MySQLApi(mailDelivery.getDB_USER_NAME(), mailDelivery.getDB_PASSWORD(), mailDelivery.getDB_HOST(), mailDelivery.getDB_DATABASE(), mailDelivery);

    }

    public void setOrderObject(OrderObject oo) {
        this.oo = oo;
    }

    public void insertTable() throws SQLException {
        // TO-DO: Implementar hikari-cp.
        if (this.SQL_API.getConnection().isClosed()) {
            SQL_API = new MySQLApi(mailDelivery.getDB_USER_NAME(), mailDelivery.getDB_PASSWORD(), mailDelivery.getDB_HOST(), mailDelivery.getDB_DATABASE(), mailDelivery);

        }

        PreparedStatement ps = (PreparedStatement) this.SQL_API.getConnection().prepareStatement("INSERT INTO `cash_keys` (`chave`, `valor`, `usada`, `jogador_ativou`, `data_ativacao`, `data_gerada`, `id_pedido`, `email`) VALUES (?, ?, 0, '', '', ?, ?, ?)");
        ps.setString(1, oo.getKey());
        ps.setInt(2, oo.getAmountCash());
        ps.setString(3, oo.getDate());
        ps.setInt(4, oo.getOrderId());
        ps.setString(5, oo.getEmail());
        ps.execute();
    }

}
