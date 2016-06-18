package br.inf.prismasoft.chat.server;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import br.inf.prismasoft.chat.utils.DimensionUtils;

public class Server {

	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ServerWindow frame = new ServerWindow();
					
					frame.mostraSplash();
					frame.inicializa();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

}
