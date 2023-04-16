package br.com.galaxmania.cash.mailsystem;

public class Main {
    /**
     * Main class
     *
     * @author luiz
     * created on 02/05/2018
     */
    public static void main(String[] args) {
        try {
            new MailDelivery();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
