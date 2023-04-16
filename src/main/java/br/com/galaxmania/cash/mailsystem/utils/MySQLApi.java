package br.com.galaxmania.cash.mailsystem.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import br.com.galaxmania.cash.mailsystem.MailDelivery;


public class MySQLApi {
	
	private String user;
	private String password;
	private String ip;
	private String database;
	public java.sql.Connection connection;
	
	public MySQLApi(String user, String password, String ip, String database, MailDelivery mailDelivery) {
		this.user = user;
		this.password = password;
		this.ip = ip;
		this.database = database;
		try {
			connection = DriverManager.getConnection("jdbc:mysql://" + this.ip + "/" + this.database, this.user, this.password);
		} catch (SQLException e) {
			mailDelivery.mailLog("Falha ao conectar com o banco de dados");
			e.printStackTrace();
			System.exit(1);
		}
		
	}
	
	public Connection getConnection() {
		return this.connection;
	}
	

}
