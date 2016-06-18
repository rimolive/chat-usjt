package br.inf.prismasoft.chat.client;

import static javax.swing.JFrame.EXIT_ON_CLOSE;

public class Client {
	
	public static void main(String[] args) {
		ClientWindow application = new ClientWindow();

		application.setDefaultCloseOperation(EXIT_ON_CLOSE);
		application.mostraSplash();
		application.inicializa();
	}

}