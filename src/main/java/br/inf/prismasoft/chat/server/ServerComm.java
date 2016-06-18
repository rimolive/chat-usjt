package br.inf.prismasoft.chat.server;

import java.io.BufferedReader;
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
import java.util.Vector;

import javax.swing.JTextArea;

public class ServerComm implements Runnable {
	
	private String usConected, msg, magic = "bobo", logfile = "Chat.log";
	
	private int port, backlog, maxConnections, numConnections = 0;
	
	private SimpleDateFormat hora = new SimpleDateFormat("HH:mm:ss");
	
	private Vector<Connection> connections = null;
	
	private JTextArea log;
	
	private ObjectInputStream entrada;
	
	public ServerComm(int port, int backlog, JTextArea log) {
		this.port = port;
		this.backlog = backlog;
		this.log = log;
	}

	@Override
	public void run() {
		ServerSocket serverSocket = null;
		Socket communicationSocket;

		try {
			log.append("\n\nTentando iniciar o servidor...");
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
				handleConnection(communicationSocket);
			} catch (Exception e) {
				log.append("\nUnable to spawn child socket.");
				e.printStackTrace();
			}
		}
	}
	
	public void handleConnection(Socket connection) {
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
		connections.forEach(c -> c.sendMessage(message));
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
				ServerComm.this.sendToAllClients(welcome);
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
						ServerComm.this.sendToAllClients(input + "\n");
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
				ServerComm.this.connectionClosed(this, address.toString());
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

}