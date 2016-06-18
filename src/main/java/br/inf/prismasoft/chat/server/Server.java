package br.inf.prismasoft.chat.server;

import java.awt.EventQueue;

/**
 * Classe principal que inicializa o Servidor de Chat.
 * 
 * @author Ricardo M. Oliveira <ricardo.martinelli.oliveira@gmail.com>
 */
public class Server {

	/**
	 * Launch the application.
	 */
	public static void main(String... args) {
		EventQueue.invokeLater(() -> {
			ServerWindow frame = new ServerWindow();
			frame.inicializa();
		});
	}

}
