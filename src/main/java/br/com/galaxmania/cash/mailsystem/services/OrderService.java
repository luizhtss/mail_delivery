package br.com.galaxmania.cash.mailsystem.services;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.http.client.utils.URIBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

import br.com.galaxmania.cash.mailsystem.MailDelivery;
import br.com.galaxmania.cash.mailsystem.OrderObject;
import br.com.galaxmania.cash.mailsystem.utils.RcHttp;

public class OrderService implements IService{
	
	private MailDelivery mailDelivery;
	
	public OrderService(MailDelivery mailDelivery) {
		this.mailDelivery = mailDelivery;
	}
	
	@Override
	public void runService() throws Exception {
		URIBuilder uriBuilder = new URIBuilder(mailDelivery.getSITE_URL());
		uriBuilder.addParameter( "wmc_key",  mailDelivery.getWMC_KEY() );

		String url = uriBuilder.toString();
		if ( url.equals( "" ) ) {
			throw new Exception( "WMC URL is empty for some reason" );
		}

		RcHttp rcHttp = new RcHttp();
		String httpResponse = rcHttp.request( url );

		// No response, kill out here.
		if ( httpResponse.equals( "" ) ) {
			return;
		}

		JSONObject pendingCommands = new JSONObject( httpResponse );

		if ( ! pendingCommands.getBoolean( "success" ) ) {

			Object dataCheck = pendingCommands.get( "data" );
			if ( dataCheck instanceof JSONObject ) {
				JSONObject errors = pendingCommands.getJSONObject( "data" );
				String msg = errors.getString( "msg" );
				
				throw new Exception( msg );
			}

			return ;
		}

		Object dataCheck = pendingCommands.get( "data" );
		if ( !( dataCheck instanceof JSONObject ) ) {
			return ;
		}

		JSONObject data = pendingCommands.getJSONObject( "data" );
		Iterator<String> playerNames = data.keys();
		JSONArray processedData = new JSONArray();
		List<String> EMAILS = new ArrayList<>();
		while ( playerNames.hasNext() ) {
			String playerName = playerNames.next();
			JSONObject playerOrders = data.getJSONObject( playerName );
			Iterator<String> orderIDs = playerOrders.keys();
			while ( orderIDs.hasNext() ) {
				String orderID = orderIDs.next();
				processedData.put(Integer.parseInt(orderID));
				JSONObject commands = playerOrders.getJSONObject( orderID );
				Iterator<String> wow = commands.keys();
				while (wow.hasNext()) {
					String email = wow.next();
					EMAILS.add(email);
					if (!(commands.get(email) instanceof JSONArray))continue;
					JSONArray jsonArrayrray = commands.getJSONArray(email);
					if (mailDelivery.getListOfOrder().stream().filter(ml -> ml.getEmail().equalsIgnoreCase(email)).findFirst().isPresent()) {
						OrderObject os = mailDelivery.getListOfOrder().stream().filter(ml -> ml.getEmail().equalsIgnoreCase(email)).findFirst().get();
						jsonArrayrray.forEach(cashs -> os.increaseCash(Integer.parseInt((String) cashs)));
					}else {
						OrderObject os = new OrderObject(email, 0, mailDelivery, Integer.parseInt(orderID), url, processedData, EMAILS);
						jsonArrayrray.forEach(cashs -> os.increaseCash(Integer.parseInt((String) cashs)));
						mailDelivery.mailLog("O pedido "+orderID+" já está pronto para ser enviado ao cliente!");
						mailDelivery.getListOfOrder().add(os);
					}
				}
			}
		}
	}
}
