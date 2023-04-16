package br.com.galaxmania.cash.mailsystem;

import java.util.TimerTask;

public class SchedulerOrderCheck extends TimerTask{
	
	private MailDelivery mailDelivery;
	
	public SchedulerOrderCheck(MailDelivery mailDelivery) {
		this.mailDelivery = mailDelivery;
	}

	@Override
	public void run() {

		try {
			mailDelivery.getOrderServce().runService();
		
			mailDelivery.getListOfOrder().forEach(oo->{

			mailDelivery.getMailService().setOrderObject(oo);
			mailDelivery.getSQLService().setOrderObject(oo);
			try {
				mailDelivery.getSQLService().insertTable();
				oo.processData();
			} catch (Exception e) {
				oo.setReadyToSendEmail(false);
				mailDelivery.mailLog("Falha ao enviar os dados para o servidor!");
				e.printStackTrace();
				return;
			}
			mailDelivery.getMailService().sendMail();
		});
		
		} catch (Exception e) {
			mailDelivery.mailLog("Falha ao executar o servi�o de pedidos. "+ e.getMessage());
			e.printStackTrace();
			return;
		}
		mailDelivery.getListOfOrder().clear();
	}
	
	

}
