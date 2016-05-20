package br.inf.prismasoft.chat;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;

public class janelinha extends JFrame implements ActionListener {

	// INICIO DAS VARIAVEIS DE CONTROLE DE CONEXAO

	private final String DEFAULT_SERVER_IP = "localhost";
	private final int DEFAULT_SERVER_PORT = 1234;

	private String serverIP = DEFAULT_SERVER_IP;
	private int serverPort = DEFAULT_SERVER_PORT;

	private Socket connection = null;
	private BufferedReader serverIn = null;
	private PrintStream serverOut = null;
	ObjectOutputStream saida;

	// FIM DAS VARIAVEIS DE CONEXAO

	// VARIAVEIS DA TELA DE CONEXAO

	private JTextField wiz = new JTextField();
	private JDialog TelaConect = new JDialog();
	private JLabel topimage = new JLabel(new ImageIcon("images/WizardTop.jpg"));
	private JLabel leftimage = new JLabel(new ImageIcon("images/WizardLeft.gif"));
	private JLabel spacer = new JLabel("");
	private JLabel inst1 = new JLabel("Bem Vindo ao Pifuze Chat v0.8b!");
	private JLabel inst2 = new JLabel(
			"Aqui voce vai configurar sua conexao com o servidor do chat.");
	private JLabel inst3 = new JLabel(
			"Clique em avancar para continuar a configuracao.");
	private JLabel spacer2 = new JLabel("");
	private JPanel panel = new JPanel(new BorderLayout());
	private JPanel butons = new JPanel(new GridLayout(1, 4, 5, 5));
	private JPanel body = new JPanel(new GridLayout(10, 2, 3, 3));
	private JButton next = new JButton("Avancar >>>");
	private JButton back = new JButton("<<< Voltar");
	private int ind = 0;

	// FIM DAS VARI�VEIS DA TELA DE CONEX�O

	private JFileChooser save = new JFileChooser();
	private SimpleDateFormat hora = new SimpleDateFormat("HH:mm:ss");
	private Object size[] = { new Integer(12), new Integer(14),
			new Integer(16), new Integer(18), new Integer(20), new Integer(22),
			new Integer(24), new Integer(26), new Integer(28), new Integer(30),
			new Integer(32), new Integer(36), new Integer(38), new Integer(42) };
	private GregorianCalendar calendario = new GregorianCalendar();
	private SimpleDateFormat data = new SimpleDateFormat("dd-MM-yyyy");
	private int fontTam = 10, k = 0;
	private String cabecalho;
	private String fontTip = "Times New Roman";
	private Font fonte;
	private boolean neg = false, ita = false;
	private String font[] = { "Arial", "Arial Black", "Times New Roman",
			"Comic Sans MS", "Lucida Sans Unicode", "Impact", "Verdana" };
	private String forma[] = { "Sem formatacao", "Negrito", "Italico",
			"Negrito e Italico"

	};
	private JComboBox fontName = new JComboBox(font), fontSize = new JComboBox(
			size), formatacao = new JComboBox(forma);
	private JTextField tenvia;
	private JButton benvia;
	private JTextArea lista;
	private String nick = "";
	private JList pessoas;
	private JMenuBar menu;
	private JMenuItem m2, m1, m6, msalvar, mconect;
	private JRadioButtonMenuItem m3, m4, m5;
	private JPanel p1, p2, p3, p4, p5, about;
	private Color color = Color.white;
	private int cont = 0;
	private JLabel sender, receiver, efont, esize;
	private JComboBox action;
	private JScrollPane scroll;
	private Font plainFont, boldFont;
	private String ac[] = { "fala para", "grita com", "fica nervoso com" };
	private UIManager.LookAndFeelInfo looks[];
	JCheckBox negrito = new JCheckBox("Negrito");
	JCheckBox italico = new JCheckBox("Italico");
	private JLabel format = new JLabel("Formatacao:");

	public janelinha() {
		super("Pifuze Chat v0.8b");

		cabecalho = "Pifuze Chat v0.8b\n\nHistorico do usuario " + nick
				+ " no dia " + data.format(calendario.getTime()) + "\n\n\n";
		if (nick == null)
			System.exit(0);
		Container cp = getContentPane();
		cp.setLayout(new BorderLayout());

		Icon Ienvia = new ImageIcon("images/icon.gif");

		p1 = new JPanel(new GridLayout(2, 1, 5, 5));
		p2 = new JPanel(new BorderLayout(2, 2));
		p3 = new JPanel(new BorderLayout());
		p4 = new JPanel(new BorderLayout());
		p5 = new JPanel(new GridLayout(1, 6, 5, 5));
		fontName.addActionListener(this);
		fontSize.addActionListener(this);
		p5.add(efont = new JLabel("Fonte:"));
		p5.add(fontName);
		p5.add(esize = new JLabel("Tamanho:"));
		p5.add(fontSize);
		p5.add(format);
		p5.add(formatacao);
		p4.setBorder(BorderFactory.createLoweredBevelBorder());
		p3.add("Center", tenvia = new JTextField());
		p3.add("East", benvia = new JButton("", Ienvia));

		String x[] = new String[20];
		x[cont] = "Todos";
		cont++;
		x[cont] = nick;
		cont++;
		sender = new JLabel(nick + " ");

		// Pegando as informa��es de IP e porta

		// XD

		plainFont = new Font(fontTip + "", Font.PLAIN, fontTam);
		boldFont = new Font("Comic Sans MS", Font.BOLD, 14);

		lista = new JTextArea();
		scroll = new JScrollPane(lista);

		tenvia.setFont(boldFont);
		lista.setFont(plainFont);

		p4.add("West", sender);
		p4.add("Center", action = new JComboBox(ac));
		p4.add("East", receiver = new JLabel("Todos"));
		p2.add("West", pessoas = new JList(x));
		p2.add("Center", scroll);
		p1.add(p4);
		p1.add(p3);
		cp.add(p1, BorderLayout.SOUTH);
		cp.add(p5, BorderLayout.NORTH);
		cp.add(p2, BorderLayout.CENTER);

		JMenu menu1 = new JMenu("File");
		JMenu menu2 = new JMenu("Edit");
		JMenu menu4 = new JMenu("Themes");
		JMenu menu3 = new JMenu("Help");
		menu = new JMenuBar();
		menu.add(menu1);
		menu.add(menu2);
		menu.add(menu4);
		menu.add(menu3);

		m2 = new JMenuItem("Color");
		m1 = new JMenuItem("Quit");
		mconect = new JMenuItem("Conectar");
		m3 = new JRadioButtonMenuItem("Metal");
		m4 = new JRadioButtonMenuItem("Motif");
		m5 = new JRadioButtonMenuItem("Windows");
		m6 = new JMenuItem("About");
		msalvar = new JMenuItem("Salvar Hist�rico");
		menu2.add(m2);
		menu1.add(mconect);
		menu1.add(msalvar);
		menu1.add(m1);
		menu4.add(m3);
		menu4.add(m4);
		menu4.add(m5);
		menu3.add(m6);
		setJMenuBar(menu);
		m3.setSelected(true);

		// PARTE RELACIONADA COM O WIZARD DE CONEX�O

		back.setVisible(false);
		butons.add(back);
		butons.add(next);
		body.add(spacer2);
		body.add(inst1);
		body.add(spacer);
		body.add(inst2);
		body.add(inst3);

		panel.add(butons, BorderLayout.SOUTH);
		panel.add(body, BorderLayout.CENTER);

		Container cop = TelaConect.getContentPane();
		cop.setLayout(new BorderLayout());
		cop.add(topimage, BorderLayout.NORTH);
		cop.add(leftimage, BorderLayout.WEST);
		cop.add(panel, BorderLayout.CENTER);
		back.addActionListener(this);
		next.addActionListener(this);
		TelaConect.pack();

		// FIM

		m2.addActionListener(this);
		m1.addActionListener(this);
		m3.addActionListener(this);
		m4.addActionListener(this);
		m5.addActionListener(this);
		m6.addActionListener(this);
		mconect.addActionListener(this);
		msalvar.addActionListener(this);
		benvia.addActionListener(this);
		tenvia.addActionListener(new teclaenter());
		pessoas.addListSelectionListener(new selectlist());
		formatacao.addActionListener(this);
		lista.setEditable(false);
		scroll.setAutoscrolls(true);

		looks = UIManager.getInstalledLookAndFeels();
		lista
				.append("Voce nao esta conectado!!!\nClique em File > Conectar para estabelecer a conexao!");
		Dimension tela = Toolkit.getDefaultToolkit().getScreenSize();
		int largura = (int) (tela.getWidth() - 780) / 2;
		int altura = (int) (tela.getHeight() - 580) / 2;
		setLocation(largura, altura);
		setSize(780, 550);
		show();
		JOptionPane
				.showMessageDialog(
						null,
						"Atencao!!!\n\nVoce nao esta conectado!\nClique em File > Conectar para estabelecer uma conexao.",
						"Pifuze Chat v0.8b", JOptionPane.INFORMATION_MESSAGE);

	}

	private void changeTheme(int valor) {
		try {
			UIManager.setLookAndFeel(looks[valor].getClassName());
			SwingUtilities.updateComponentTreeUI(this);
		}

		catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	// FAZENDO O METODO DA PORQUERA DA CONEXAO

	private void makeConnection() {
		try {
			connection = new Socket(serverIP, serverPort);
			serverIn = new BufferedReader(new InputStreamReader(connection.getInputStream()));

			saida = new ObjectOutputStream(connection.getOutputStream());
			saida.writeObject(nick);
			saida.flush();

			serverOut = new PrintStream(connection.getOutputStream());
			Thread t = new Thread(new RemoteReader());
			t.start();
		} catch (Exception e) {
			System.out.println("Incapaz de conectar com o servidor!");
			e.printStackTrace();
		}
	}

	// Terminou =D

	private void copyText() {
		if (!tenvia.getText().equals("")) {

			String a = nick + " " + action.getSelectedItem() + " "
					+ receiver.getText() + ": " + tenvia.getText() + "";
			serverOut.println(a);
			tenvia.setText("");
		}

	}

	public class selectlist implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent e) {
			receiver.setText("" + pessoas.getSelectedValue());
		}
	}

	public class teclaenter implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			copyText();
		}
	}

	public void actionPerformed(ActionEvent e) {
		// PARTE RELACIONADA AO WIZARD DE CONEX�O

		if (e.getSource() == next) {
			if (ind == 0) {

				body.remove(inst1);
				body.remove(spacer);
				body.remove(inst2);
				body.remove(inst3);
				inst3.setText("Insira no campo abaixo o IP do servidor:");
				body.add(inst3);
				body.add(spacer);
				body.add(wiz);
				back.setVisible(true);
				ind++;
			} else if (ind == 1) {
				if (wiz.getText().equals("")) {
					JOptionPane.showMessageDialog(null, "Digite um IP valido.",
							"ERRO", JOptionPane.ERROR_MESSAGE);
				} else {
					serverIP = wiz.getText();
					inst3
							.setText("Insira no campo abaixo a porta de conex�o com o servidor:");
					wiz.setText("");
					ind++;
				}
			} else if (ind == 2) {
				if (wiz.getText().equals("")) {
					JOptionPane.showMessageDialog(null,
							"Digite uma porta v�lida.", "ERRO",
							JOptionPane.ERROR_MESSAGE);
				} else {
					serverPort = Integer.parseInt(wiz.getText());
					inst3
							.setText("Digite o nome que voc� deseja usar no chat:");
					wiz.setText("");
					ind++;
				}
			} else if (ind == 3) {
				if (wiz.getText().equals("")) {
					JOptionPane.showMessageDialog(null,
							"Digite um nick para acessar o chat.", "ERRO",
							JOptionPane.ERROR_MESSAGE);
				} else {
					nick = wiz.getText();
					k = nick.length();
					if (k > 15) {
						JOptionPane.showMessageDialog(null,
								"Seu nick deve conter menos de 15 caracteres.",
								"Erro", JOptionPane.ERROR_MESSAGE);

					} else {
						String serverIP = System.getProperty("serverip");
						if (serverIP != null)
							this.serverIP = serverIP;
						String serverPort = System.getProperty("serverport");
						if (serverPort != null)
							this.serverPort = Integer.parseInt(serverPort);
						lista.setText("");
						makeConnection();
						inst3.setText("Voc� j� est� conectado!");
						body.remove(wiz);
						inst1
								.setText("Clique em Fechar para fechar essa janela.");
						body.add(inst1);
						back.setVisible(false);
						next.setText("Fechar");
						sender.setText("  " + nick + "  ");
						ind++;
					}
				}

			} else if (ind == 4) {
				TelaConect.setVisible(false);

			}
		}

		if (e.getSource() == back) {
			if (ind == 1) {
				body.remove(inst3);
				body.remove(wiz);
				body.add(spacer2);
				body.add(inst1);
				body.add(spacer);
				body.add(inst2);
				body.add(inst3);
				wiz.setText("");
				back.setVisible(false);
				ind--;
			} else if (ind == 2) {
				inst3.setText("Insira no campo abaixo o IP do servidor:");
				wiz.setText("");
				ind--;
			} else if (ind == 3) {
				inst3
						.setText("Insira no campo abaixo a porta de conex�o com o servidor:");
				wiz.setText("");
				ind--;
			}
		}

		if (e.getSource() == mconect) {
			Dimension tela = Toolkit.getDefaultToolkit().getScreenSize();
			int largura = (int) (tela.getWidth() - 500) / 2;
			int altura = (int) (tela.getHeight() - 470) / 2;
			TelaConect.setLocation(largura, altura);
			TelaConect.show();
		}

		// FIM

		if (e.getSource() == msalvar) {
			File file = new File("log_"
					+ (String) data.format(calendario.getTime()));
			save.setSelectedFile(file);
			int returnVal = save.showSaveDialog(janelinha.this); // save
																	// dialog
																	// box
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				try {
					file = save.getSelectedFile();
					FileWriter outputStream = new FileWriter(file.getPath()
							+ ".rtf"); // saves the text into a prasanna file,
										// but you can change it to txt for
										// normal notepad like text files
					outputStream.write(cabecalho + "" + lista.getText());
					outputStream.close();
				} catch (IOException ioe) {
					System.out.println("Error");
				}
			}

		}

		if (e.getSource() == formatacao) {
			String x = formatacao.getSelectedItem() + "";
			if (x.equals("Sem formata��o")) {
				Font X = new Font(fontTip, Font.PLAIN, fontTam);
				lista.setFont(X);
				neg = false;
				ita = false;
			} else if (x.equals("Negrito")) {
				Font X = new Font(fontTip, Font.BOLD, fontTam);
				lista.setFont(X);
				neg = true;
				ita = false;
			} else if (x.equals("It�lico")) {
				Font X = new Font(fontTip, Font.ITALIC, fontTam);
				lista.setFont(X);
				neg = false;
				ita = true;
			} else if (x.equals("Negrito e It�lico")) {
				Font X = new Font(fontTip, Font.BOLD + Font.ITALIC, fontTam);
				lista.setFont(X);
				neg = true;
				ita = true;
			}
		}
		if (e.getSource() == fontName) {
			if (!neg && !ita) {

				Font X = new Font(fontName.getSelectedItem() + "", Font.PLAIN,
						fontTam);
				lista.setFont(X);
				fontTip = "" + fontName.getSelectedItem();

			} else if (neg && !ita) {

				Font X = new Font(fontName.getSelectedItem() + "", Font.BOLD,
						fontTam);
				lista.setFont(X);
				fontTip = "" + fontName.getSelectedItem();

			}
			if (!neg && ita) {

				Font X = new Font(fontName.getSelectedItem() + "", Font.ITALIC,
						fontTam);
				lista.setFont(X);
				fontTip = "" + fontName.getSelectedItem();

			}
			if (neg && ita) {

				Font X = new Font(fontName.getSelectedItem() + "", Font.BOLD
						+ Font.ITALIC, fontTam);
				lista.setFont(X);
				fontTip = "" + fontName.getSelectedItem();

			}
		}

		if (e.getSource() == fontSize) {
			if (!neg && !ita) {

				fontTam = Integer.parseInt(fontSize.getSelectedItem() + "");
				Font X = new Font(fontTip + "", Font.PLAIN, fontTam);
				lista.setFont(X);

			} else if (neg && !ita) {

				fontTam = Integer.parseInt(fontSize.getSelectedItem() + "");
				Font X = new Font(fontTip + "", Font.BOLD, fontTam);
				lista.setFont(X);

			}
			if (!neg && ita) {

				fontTam = Integer.parseInt(fontSize.getSelectedItem() + "");
				Font X = new Font(fontTip + "", Font.ITALIC, fontTam);
				lista.setFont(X);

			}
			if (neg && ita) {

				fontTam = Integer.parseInt(fontSize.getSelectedItem() + "");
				Font X = new Font(fontTip + "", Font.BOLD + Font.ITALIC,
						fontTam);
				lista.setFont(X);

			}

		}
		if (e.getSource() == benvia) {
			copyText();
		}
		if (m3 == e.getSource()) {
			m3.setSelected(true);
			m4.setSelected(false);
			m5.setSelected(false);
			changeTheme(0);

		}
		if (m4 == e.getSource()) {
			m4.setSelected(true);
			m3.setSelected(false);
			m5.setSelected(false);

			changeTheme(1);

		}
		if (m5 == e.getSource()) {
			m5.setSelected(true);
			m3.setSelected(false);
			m4.setSelected(false);

			changeTheme(2);

		}
		if (e.getSource() == m6) {
			JOptionPane.showMessageDialog(null, "!!!", "Sobre o Pifuze",
					JOptionPane.INFORMATION_MESSAGE);
		}

		if (e.getSource() == m2) {
			color = JColorChooser.showDialog(janelinha.this, "Choose a color",
					color);
			if (color == null) {
				color = Color.white;
			}
			lista.setBackground(color);
			pessoas.setBackground(color);
			tenvia.setBackground(color);
		}

		if (e.getSource() == m1) {
			System.gc();
			System.exit(0);
		}
	}

	private class RemoteReader implements Runnable {
		private boolean keepListening = true;

		public void run() {
			while (keepListening == true) {
				try {
					String nextLine = serverIn.readLine();
					lista.append(nextLine + "\n");
				} catch (Exception e) {
					keepListening = false;
					System.out.println("Erra enquanto lia o servidor.");
					e.printStackTrace();
				}
			}
		}
	}

	public static void main(String[] args) {
		JDialog splashScreen = new JDialog();
		JLabel splash = new JLabel(new ImageIcon("images/splash.jpg"));
		Container c = splashScreen.getContentPane();
		c.add(splash);
		Dimension tela = Toolkit.getDefaultToolkit().getScreenSize();
		int largura = (int) (tela.getWidth() - 350) / 2;
		int altura = (int) (tela.getHeight() - 300) / 2;
		splashScreen.setBounds(largura, altura, 0, 0);
		splashScreen.setUndecorated(true);
		splashScreen.pack();
		splashScreen.show();
		try {
			synchronized (splashScreen) {
				splashScreen.wait(3 * 1000);
			}
		} catch (InterruptedException e) {
			// o q fazer se der uma exception?
			// chorar?
			// pedir para algum certificado ajuda?
			// ou simplesmente entrar em colapso nervoso?
			// o apocalipse esta proximo!
		}

		splashScreen.hide();
		janelinha application = new janelinha();
		application.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	}

}