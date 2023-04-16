package br.com.galaxmania.cash.mailsystem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.Scanner;
import java.util.Timer;

import org.apache.commons.io.FileUtils;

import br.com.galaxmania.cash.mailsystem.services.MailService;
import br.com.galaxmania.cash.mailsystem.services.MySQLService;
import br.com.galaxmania.cash.mailsystem.services.OrderService;
import br.com.galaxmania.cash.mailsystem.utils.FileUtil;
import br.com.galaxmania.cash.mailsystem.utils.TeePrintStream;

/**
 * Delivery class
 * @author luiz
 * created on 02/05/2018
 */
public class MailDelivery {
    private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
	public Random random;
	public final String[] A_Z_0_9 ={"0","1","2","3","4","5","6","7","8","9",
			"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
	private String EMAIL;
	private String USER_NAME;
	private String PASSWORD;
	private String HOST;
	private String PORT;
	
	private String DB_USER_NAME;
	private String DB_PASSWORD;
	private String DB_HOST;
	private String DB_DATABASE;
	
	private String SITE_URL;
	private String WMC_KEY;
	
	private List<OrderObject> list;

	
	private OrderService ORDER_SERVICE;
	private MailService MAIL_SERVICE;
	private MySQLService SQL_SERVICE;

	public MailDelivery() throws Exception {
		random = new Random();
		setupFiles();
		this.list = new ArrayList<>();
		ORDER_SERVICE = new OrderService(this);
		MAIL_SERVICE = new MailService(this);
		SQL_SERVICE = new MySQLService(this);

		MAIL_SERVICE.runService();
		SQL_SERVICE.runService();
		
		Timer timer = new Timer();
		timer.schedule(new SchedulerOrderCheck(this), 0, 10000);

		mailLog("Sucesso!");
		mailLog("Os pedidos serão verificados a cada 10 segundos!");
		
		waitStopCommand();
	}
	
	private void setupLogFile() {
	    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd_MM_yyyy HH-mm-ss");
        LocalDateTime now = LocalDateTime.now();
        String date = dtf.format(now);
        File f = new File("logs" + File.separator + date + ".log");
        try {
            FileUtils.touch(f);

            FileOutputStream file = new FileOutputStream(f);
            TeePrintStream tee = new TeePrintStream(file, System.out);

            System.setOut(tee);
            System.setErr(tee);
        } catch (IOException ex) {
        	ex.printStackTrace();
            System.exit(1);
        }
    }
	
	private void setupPropsfile() {
		Properties prop = new Properties();

        try {
            InputStream input = new FileInputStream("server.properties");
            prop.load(input);
            
            this.EMAIL = prop.getProperty("email");
            this.USER_NAME = prop.getProperty("usuario");
            this.PASSWORD = prop.getProperty("senha");

            this.HOST = prop.getProperty("host");
            this.PORT = prop.getProperty("porta");
            
            this.SITE_URL = prop.getProperty("loja_url");
            this.WMC_KEY = prop.getProperty("wmc_key");
            
            this.DB_HOST = prop.getProperty("db_host");
            this.DB_DATABASE = prop.getProperty("db_banco");
            this.DB_USER_NAME = prop.getProperty("db_usuario");
            this.DB_PASSWORD = prop.getProperty("db_senha");

            
        } catch (IOException ex) {
        	ex.printStackTrace();
            System.exit(1);
        }
	}
	
	public void mailLog(String message){
		LocalDateTime now = LocalDateTime.now();
        String date = dtf.format(now);
        System.out.println("["+date+"] "+message);
	}
	
	private void setupFiles() throws Exception {
		setupLogFile();
		saveResource("/server.properties");
		saveResource("/email.html");
		setupPropsfile();
		FileUtil.loadEmail(new File("email.html"));
	}
	
	public void saveResource(String name)
		       throws Exception {
		    File out = new File(name);
		    if (out.exists()) return;
		    FileUtil.ExportResource(name);
		}
	
	private void waitStopCommand() {
		Thread t = new Thread(() -> {
				Scanner s = new Scanner(System.in);
				if (s.next().equalsIgnoreCase("stop")) {
					mailLog("Goodbye...");
					s.close();
					System.exit(0);
				}
		});
		t.start();
	}
	
	public List<OrderObject> getListOfOrder() {
		return this.list;
	}
	
	public String getEMAIL() {
		return EMAIL;
	}

	public String getUSER_NAME() {
		return USER_NAME;
	}

	public String getPASSWORD() {
		return PASSWORD;
	}

	public String getHOST() {
		return HOST;
	}

	public String getPORT() {
		return PORT;
	}
	
	public String getSITE_URL() {
		return this.SITE_URL;
	}
	
	public String getWMC_KEY() {
		return this.WMC_KEY;
	}
	
	public String getDB_USER_NAME() {
		return DB_USER_NAME;
	}

	public String getDB_PASSWORD() {
		return DB_PASSWORD;
	}

	public String getDB_HOST() {
		return DB_HOST;
	}

	public String getDB_DATABASE() {
		return DB_DATABASE;
	}

	public List<OrderObject> getList() {
		return list;
	}
	
	public OrderService getOrderServce() {
		return this.ORDER_SERVICE;
	}
	
	public MailService getMailService() {
		return this.MAIL_SERVICE;
	}
	
	public MySQLService getSQLService() {
		return this.SQL_SERVICE;
	}
}
