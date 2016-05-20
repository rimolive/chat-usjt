package br.inf.prismasoft.chat.server;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

public class ChatServer extends JFrame implements Runnable, ActionListener {

	private static final long serialVersionUID = 1L;
	
	private String magic = "bobo";
	
	private JFileChooser save = new JFileChooser();
	
	private SimpleDateFormat hora = new SimpleDateFormat("HH:mm:ss");
	
	private SimpleDateFormat data = new SimpleDateFormat("dd-MM-yyyy");
	
	private Calendar calendario2 = Calendar.getInstance();
	
	private int port;
	
	private int backlog;
	
	private int numConnections = 0;
	
	private int maxConnections;
	
	private String logfile = "Chat.log";
	
	private Vector<Connection> connections = null;
	
	private JScrollPane scroll;
	
	private JLabel lIcon;
	
	public JTextArea log;
	
	private JPanel pLog, pCom, pBody;
	
	private JButton bConecta, bSair, bsalvar;
	
	String usConected, msg;
	
	private ObjectInputStream entrada;
	
	public ChatServer() {
		super("Pifuze Server v0.4b");
		
		createGUI();
	}

	private void createGUI() {
		pLog = new JPanel();
		pBody = new JPanel();
		log = new JTextArea();
		lIcon = new JLabel(new ImageIcon("images/Server.jpg"));
		log.setEditable(false);
		scroll = new JScrollPane(log);
		scroll.setAutoscrolls(true);
		pCom = new JPanel(new GridLayout(1, 3, 5, 5));
		bConecta = new JButton("Conectar");
		bSair = new JButton("Sair");
		bsalvar = new JButton("Salvar Log");

		bConecta.addActionListener(this);
		bSair.addActionListener(this);
		bsalvar.addActionListener(this);

		pCom.add(bConecta);
		pCom.add(bsalvar);
		pCom.add(bSair);
		add(lIcon, BorderLayout.WEST);
		pLog.add(scroll, BorderLayout.CENTER);
		pBody.add(pLog, BorderLayout.CENTER);
		pBody.add(pCom, BorderLayout.SOUTH);
		add(pBody, BorderLayout.CENTER);

		Dimension tela = Toolkit.getDefaultToolkit().getScreenSize();
		int largura = (int) (tela.getWidth() - 640) / 2;
		int altura = (int) (tela.getHeight() - 535) / 2;
		setSize(640, 505);
		
		setLocation(largura, altura);
		setVisible(true);
	}
	
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == bsalvar) {

			File file = new File("CHAT_log_"
					+ (String) data.format(calendario2.getTime()));
			save.setSelectedFile(file);
			int returnVal = save.showSaveDialog(ChatServer.this); // save
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
		}

		if (e.getSource() == bSair) {
			System.exit(0);
		}
		if (e.getSource() == bConecta) {
			go();
		}
	}

	public void go() {
		ResourceBundle res = ResourceBundle.getBundle("config");
		this.port = Integer.parseInt(res.getString("chat.port"));
		this.backlog = Integer.parseInt(res.getString("chat.backLog"));
		this.maxConnections = Integer.parseInt(res.getString("chat.maxConnections"));
		
		this.connections = new Vector<Connection>(maxConnections);

		log.append("Config do servidor:\nBacklogs=" + backlog
				+ "\nMax Connections=" + maxConnections + "\n\tLog File="
				+ logfile);

		Thread t = new Thread(this);
		t.start();
	}

	public void run() {
		ServerSocket serverSocket = null;
		Socket communicationSocket;

		try {
			log.append("\nTentando iniciar o servidor...");
			serverSocket = new ServerSocket(port, backlog);
		} catch (IOException e) {
			log.append("\nErro iniciando o servidor: Nao pude abrir a porta "
					+ port);
			e.printStackTrace();
			System.exit(1);
		}
		log.append("\nServidor iniciado na porta " + port);

		// Run the listen/accept loop forever
		while (true) {
			try {
				// Wait here and listen for a connection
				communicationSocket = serverSocket.accept();
				HandleConnection(communicationSocket);
			} catch (Exception e) {
				log.append("\nUnable to spawn child socket.");
				e.printStackTrace();
			}
		}
	}

	public void HandleConnection(Socket connection) {
		synchronized (this) {
			while (numConnections == maxConnections) {
				try {
					wait();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		numConnections++;
		Connection con = new Connection(connection);
		Thread t = new Thread(con);
		t.start();
		connections.addElement(con);
	}

	public synchronized void connectionClosed(Connection connection, String user) {
		connections.removeElement(connection);
		numConnections--;
		log.append("\nFOI EMBORA " + user);
		log.append("\nNumero de conex�es " + numConnections);

		notify();
	}

	public void sendToAllClients(String message) {
		for(Connection c: connections) {
			c.sendMessage(message);
		}
	}

	class Connection implements Runnable {
		private Socket communicationSocket = null;
		private OutputStreamWriter out = null;
		private BufferedReader in = null;
		
		public Connection(Socket s) {
			communicationSocket = s;
		}

		public void run() {
			OutputStream socketOutput = null;
			InputStream socketInput = null;
			try {
				socketOutput = communicationSocket.getOutputStream();
				out = new OutputStreamWriter(socketOutput);
				socketInput = communicationSocket.getInputStream();
				in = new BufferedReader(new InputStreamReader(socketInput));

				entrada = new ObjectInputStream(communicationSocket.getInputStream());
				msg = (String) entrada.readObject();

				InetAddress address = communicationSocket.getInetAddress();
				String hostname = address.getHostName();

				String welcome = "Conexao feita pelo host: " + hostname + " "
						+ msg;
				usConected = hostname;
				if (usConected != null)
					welcome += " para " + usConected + "123" + msg;
				welcome += "!\n";
				ChatServer.this.sendToAllClients(welcome);
				log.append("\nConexao feita " + usConected + "@" + hostname
						+ " " + msg);
				sendMessage("Bem vindo " + usConected + " a frase chave eh "
						+ magic + "\n");
				String input = null;

				while ((input = in.readLine()) != null) {
					if (input.indexOf(magic) != -1) {
						sendMessage("SEU PODRE " + usConected
								+ " vc enviou a frase chave\n");
						log
								.append("\n" + usConected
										+ " enviou a frase chave!");
					} else
						ChatServer.this.sendToAllClients(input + "\n");
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					// if(in != null) in.close();
					// if(out != null) out.close();
					// communicationSocket.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				InetAddress address = this.communicationSocket.getInetAddress();
				ChatServer.this.connectionClosed(this, address.toString());
			}
		}

		public void sendMessage(String message) {
			try {
				Calendar calendario = Calendar.getInstance();
				StringBuffer sbLog = new StringBuffer();
				
				sbLog.append(hora.format(calendario.getTime()));
				sbLog.append(" | ");
				sbLog.append(message);
				
				out.write(sbLog.toString());
				out.flush();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String... args) {
		JDialog splashScreen = new JDialog();
		JLabel splash;
		splash = new JLabel(new ImageIcon("images/splashserver.jpg"));
		Container c = splashScreen.getContentPane();
		c.add(splash);
		Dimension tela = Toolkit.getDefaultToolkit().getScreenSize();
		int largura = (int) (tela.getWidth() - 350) / 2;
		int altura = (int) (tela.getHeight() - 300) / 2;
		splashScreen.setBounds(largura, altura, 0, 0);
		splashScreen.setUndecorated(true);
		splashScreen.setUndecorated(true);
		splashScreen.pack();
		splashScreen.setVisible(true);
		try {
			synchronized (splashScreen) {
				splashScreen.wait(2 * 1000);
			}
		} catch (InterruptedException e) {
			// o q fazer se der uma exception?
		}

		splashScreen.setVisible(false);
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					new ChatServer().setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
	}

}