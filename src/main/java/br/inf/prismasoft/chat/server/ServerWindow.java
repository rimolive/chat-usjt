package br.inf.prismasoft.chat.server;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import br.inf.prismasoft.chat.utils.DimensionUtils;

public class ServerWindow extends JFrame {
	
	private String logFile = "chat.log";

	private JFileChooser save = new JFileChooser();
	
	private SimpleDateFormat data = new SimpleDateFormat("dd-MM-yyyy");
	
	private Calendar calendario = Calendar.getInstance();
	
	private int port, backlog;
	
	private JScrollPane scroll;
	
	private JLabel lIcon;
	
	public JTextArea log;
	
	private JPanel pLog, pCom, pBody;
	
	private JButton bConecta, bSair, bsalvar;
	
	public void inicializa() {
		setTitle("Pifuze Server v0.4b");
		createGUI();
	}

	private void createGUI() {
		pCom = new JPanel(new GridLayout(1, 3, 5, 5));
		pLog = new JPanel();
		pBody = new JPanel();
		log = new JTextArea();
		lIcon = new JLabel(new ImageIcon(this.getClass().getResource("/images/Server.jpg").getPath()));
		log.setEditable(false);
		scroll = new JScrollPane(log);
		scroll.setAutoscrolls(true);
		scroll.setPreferredSize(new Dimension(420, 400));
		bConecta = new JButton("Conectar");
		bSair = new JButton("Sair");
		bsalvar = new JButton("Salvar Log");

		bConecta.addActionListener(e -> go());
		
		bSair.addActionListener(e -> System.exit(0));
		
		bsalvar.addActionListener(e -> {
			File file = new File("CHAT_log_"
					+ (String) data.format(calendario.getTime()));
			save.setSelectedFile(file);
			int returnVal = save.showSaveDialog(ServerWindow.this); // save
																	// dialog
																	// box
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				try {
					file = save.getSelectedFile();
					FileWriter outputStream = new FileWriter(file.getPath()
							+ ".rtf"); // saves the text into a prasanna file,
										// but you can change it to txt for
										// normal notepad like text files
					outputStream.write(log.getText());
					outputStream.close();
				} catch (IOException ioe) {
					System.out.println("Error");
				}
			}
		});

		pCom.add(bConecta);
		pCom.add(bsalvar);
		pCom.add(bSair);
		add(lIcon, BorderLayout.WEST);
		pLog.add(scroll, BorderLayout.CENTER);
		pBody.add(pLog, BorderLayout.CENTER);
		pBody.add(pCom, BorderLayout.SOUTH);
		add(pBody, BorderLayout.CENTER);

		DimensionUtils.setBounds(this, 640, 505);
		setVisible(true);
	}
	
	public void go() {
		ResourceBundle res = ResourceBundle.getBundle("config");
		port = Integer.parseInt(res.getString("chat.port"));
		backlog = Integer.parseInt(res.getString("chat.backLog"));
		int maxConnections = Integer.parseInt(res.getString("chat.maxConnections"));

		log.append("Config do servidor:\n");
		log.append("Backlogs=" + backlog + "\n");
		log.append("Max Connections=" + maxConnections + "\n");
		log.append("Log File=" + logFile);

		Thread t = new Thread(new ServerComm(port, backlog, log));
		t.start();
	}

	public void mostraSplash() {
		JDialog splashScreen = new JDialog();
		JLabel splash;
		splash = new JLabel(new ImageIcon(this.getClass().getResource("/images/SplashServer.jpg").getPath()));
		Container c = splashScreen.getContentPane();
		c.add(splash);
		DimensionUtils.setBounds(this, 0, 0);
		splashScreen.setUndecorated(true);
		splashScreen.pack();
		splashScreen.setVisible(true);
		try {
			synchronized (splashScreen) {
				splashScreen.wait(2 * 1000);
			}
		} catch (InterruptedException e) {
		}
		splashScreen.setVisible(false);
	}

}