package br.com.galaxmania.cash.mailsystem;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import br.com.galaxmania.cash.mailsystem.utils.RcHttp;

public class OrderObject {
	
	private String email;
	private String key;
	private String date;
	private int orderId;
	private boolean readyToSendEmail;
	private int amountCash;
	
	private JSONArray processedData;
	private String url;
	private List<String> EMAILS;
	
	
	private MailDelivery mailDelivery;
	
	public OrderObject(String email, int amountCash, MailDelivery mailDelivery, int orderId, String url, JSONArray data, List<String> EMAILS) {
		this.email = email;
		this.readyToSendEmail = false;
		this.amountCash = amountCash;
		this.mailDelivery = mailDelivery;
		this.key = generateKey();
		this.orderId = orderId;
		
	    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
		LocalDateTime now = LocalDateTime.now();
        String date = dtf.format(now);
        this.date = date;
        
        this.url = url;
        this.processedData = data;
        this.EMAILS = EMAILS;
	}
	
	public void increaseCash(int amount) {
		this.amountCash += amount;
	}
	
	public void processData() throws Exception {
		if (1 > processedData.length() ) {
			mailDelivery.mailLog("Nenhum dado processado.");
			return ;
		}
		
		RcHttp rcHttp = new RcHttp();

		mailDelivery.mailLog( "Enviando dados processados para o servidor." );

		HashMap< String, String > postData = new HashMap<>();
		postData.put( "processedOrders", processedData.toString() );

		String updatedCommandSet = rcHttp.send( url, postData );
		JSONObject updatedResponse = new JSONObject( updatedCommandSet );
		boolean status = updatedResponse.getBoolean( "success" );

		if ( ! status ) {
			Object dataSet = updatedResponse.get( "data" );
			if ( dataSet instanceof JSONObject ) {
				String message = ( ( JSONObject ) dataSet ).getString( "msg" );
				throw new Exception( message );
			}
			throw new Exception( "Falha ao enviar os dados para o servidor. :" + updatedCommandSet );
		}else {
			mailDelivery.getListOfOrder().stream().filter(oo -> EMAILS.contains(oo.getEmail())).forEach(oo-> oo.setReadyToSendEmail(true));
			mailDelivery.mailLog( "Dados processados com sucesso!!!" );

		}
	}
	
	private String generateKey() {
		Date date = new Date();
		SimpleDateFormat yourDateFormat = new SimpleDateFormat("dd");
		String dd = yourDateFormat.format(date);
		StringBuilder sb = new StringBuilder("MMC"+dd);
		for (int i =0; i< 3; i++) {
			sb.append("-").append(i == 2 ? generateKeyFragment(8) : generateKeyFragment(5));
		}
		
		return sb.toString();
	}
	
	private String generateKeyFragment(int length) {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i<length; i++){
			sb.append(this.mailDelivery.A_Z_0_9[this.mailDelivery.random.nextInt(this.mailDelivery.A_Z_0_9.length)]);
		}
		return sb.toString();
	}

	public String getEmail() {
		return email;
	}
	
	public int getOrderId() {
		return this.orderId;
	}
	
	public String getKey() {
		return this.key;
	}
	
	public String getDate() {
		return this.date;
	}

	public boolean isReadyToSendEmail() {
		return readyToSendEmail;
	}

	public int getAmountCash() {
		return amountCash;
	}
	
	public void setReadyToSendEmail(boolean value) {
		this.readyToSendEmail = value;
	}
	

}
