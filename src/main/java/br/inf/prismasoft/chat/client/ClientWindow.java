package br.inf.prismasoft.chat.client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import br.inf.prismasoft.chat.client.types.FormatoFonte;
import br.inf.prismasoft.chat.i18n.Message;
import br.inf.prismasoft.chat.utils.DimensionUtils;

public class ClientWindow extends JFrame {
	public ClientWindow() {
	}
	
	private final String DEFAULT_SERVER_IP = "localhost";

	private final Integer DEFAULT_SERVER_PORT = 1234;

	private String serverIP = DEFAULT_SERVER_IP;

	private int serverPort = DEFAULT_SERVER_PORT;

	private Socket connection = null;

	private BufferedReader serverIn = null;

	private PrintStream serverOut = null;

	ObjectOutputStream saida;

	private JTextField wiz = new JTextField();

	private JDialog telaConect = new JDialog();

	private JLabel topimage = new JLabel(new ImageIcon(this.getClass().getResource("/images/WizardTop.jpg").getPath()));

	private JLabel leftimage = new JLabel(new ImageIcon(this.getClass().getResource("/images/WizardLeft.gif").getPath()));

	private JLabel spacer = new JLabel("");

	private JLabel inst1 = new JLabel("Bem Vindo ao Pifuze Chat v0.8b!");

	private JLabel inst2 = new JLabel("Aqui voce vai configurar sua conexao com o servidor do chat.");

	private JLabel inst3 = new JLabel("Clique em avancar para continuar a configuracao.");

	private JLabel spacer2 = new JLabel("");

	private JPanel panel = new JPanel(new BorderLayout());

	private JPanel butons = new JPanel(new GridLayout(1, 4, 5, 5));

	private JPanel body = new JPanel(new GridLayout(10, 2, 3, 3));

	private JButton next = new JButton("Avancar >>>");

	private JButton back = new JButton("<<< Voltar");

	private int ind = 0;

	private JFileChooser save = new JFileChooser();

	private SimpleDateFormat hora = new SimpleDateFormat("HH:mm:ss");

	private Object size[] = { new Integer(12), new Integer(14), new Integer(16), new Integer(18), new Integer(20),
			new Integer(22), new Integer(24), new Integer(26), new Integer(28), new Integer(30), new Integer(32),
			new Integer(36), new Integer(38), new Integer(42) };

	private GregorianCalendar calendario = new GregorianCalendar();

	private SimpleDateFormat data = new SimpleDateFormat("dd-MM-yyyy");

	private int fontTam = 10, k = 0;

	private String cabecalho;

	private String fontTip = "Times New Roman";

	private Font fonte;

	private boolean neg = false, ita = false;

	private String font[] = { "Arial", "Arial Black", "Times New Roman", "Comic Sans MS", "Lucida Sans Unicode", "Impact", "Verdana" };

	private FormatoFonte formatoFonte;

	private String forma[] = { "Sem formatacao", "Negrito", "Italico", "Negrito e Italico" };
	
	private JComboBox fontName = new JComboBox(font), fontSize = new JComboBox(size), formatacao = new JComboBox(forma);

	private JTextField tenvia;

	private JButton benvia;

	private JTextArea tMensagens;

	private String nick = "";

	private JList pessoas;

	private JMenuBar menu;

	private JMenuItem m1, m2, m6, msalvar, mconect;

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

	private void changeTheme(int valor) {
		try {
			UIManager.setLookAndFeel(looks[valor].getClassName());
			SwingUtilities.updateComponentTreeUI(this);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

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

	private void copyText() {
		if (!"".equals(tenvia.getText())) {
			String a = nick + " " + action.getSelectedItem() + " " + receiver.getText() + ": " + tenvia.getText() + "";
			serverOut.println(a);
			tenvia.setText("");
		}
	}

	private class RemoteReader implements Runnable {
		private boolean keepListening = true;

		public void run() {
			while (keepListening) {
				try {
					String nextLine = serverIn.readLine();
					tMensagens.append(nextLine + "\n");
				} catch (Exception e) {
					keepListening = false;
					System.out.println("Erra enquanto lia o servidor.");
					e.printStackTrace();
				}
			}
		}
	}

	public void inicializa() {
		setTitle(Message.CLIENT_TITLE.content());

		cabecalho = "Pifuze Chat v0.9b\n\nHistorico do usuario " + nick + " no dia " + data.format(calendario.getTime()) + "\n\n\n";

		if (nick == null)
			System.exit(0);

		Container cp = getContentPane();
		cp.setLayout(new BorderLayout());

		Icon iEnvia = new ImageIcon(this.getClass().getResource("/images/icon.gif").getPath());

		p1 = new JPanel(new GridLayout(2, 1, 5, 5));
		p2 = new JPanel(new BorderLayout(2, 2));
		p3 = new JPanel(new BorderLayout());
		p4 = new JPanel(new BorderLayout());
		p5 = new JPanel(new GridLayout(1, 6, 5, 5));
		
		fontName.addActionListener(e -> {
			Font font = tMensagens.getFont();
			if (!neg && !ita) {
				Font X = new Font(fontName.getSelectedItem() + "", Font.PLAIN, fontTam);
				tMensagens.setFont(X);
				fontTip = "" + fontName.getSelectedItem();
			} else if (neg && !ita) {
				Font X = new Font(fontName.getSelectedItem() + "", Font.BOLD, fontTam);
				tMensagens.setFont(X);
				fontTip = "" + fontName.getSelectedItem();
			}
			if (!neg && ita) {
				Font X = new Font(fontName.getSelectedItem() + "", Font.ITALIC, fontTam);
				tMensagens.setFont(X);
				fontTip = "" + fontName.getSelectedItem();
			}
			if (neg && ita) {
				Font X = new Font(fontName.getSelectedItem() + "", Font.BOLD + Font.ITALIC, fontTam);
				tMensagens.setFont(X);
				fontTip = "" + fontName.getSelectedItem();
			}
		});
		
		fontSize.addActionListener(e -> {
			Font font = tMensagens.getFont().deriveFont(fontTam);
			tMensagens.setFont(font);
		});
		
		p5.add(efont = new JLabel("Fonte:"));
		p5.add(fontName);
		p5.add(esize = new JLabel("Tamanho:"));
		p5.add(fontSize);
		p5.add(format);
		p5.add(formatacao);
		p4.setBorder(BorderFactory.createLoweredBevelBorder());
		p3.add("Center", tenvia = new JTextField());
		p3.add("East", benvia = new JButton("", iEnvia));

		String x[] = new String[20];
		x[cont] = "Todos";
		cont++;
		x[cont] = nick;
		cont++;
		sender = new JLabel(nick + " ");

		plainFont = new Font(fontTip + "", Font.PLAIN, fontTam);
		boldFont = new Font("Comic Sans MS", Font.BOLD, 14);

		tMensagens = new JTextArea();
		scroll = new JScrollPane(tMensagens);

		tenvia.setFont(boldFont);
		tMensagens.setFont(plainFont);

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

		Container cop = telaConect.getContentPane();
		cop.setLayout(new BorderLayout());
		cop.add(topimage, BorderLayout.NORTH);
		cop.add(leftimage, BorderLayout.WEST);
		cop.add(panel, BorderLayout.CENTER);
		
		back.addActionListener(e -> {
			switch(ind) {
				case 1:
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
					break;
				case 2:
					inst3.setText("Insira no campo abaixo o IP do servidor:");
					wiz.setText("");
					ind--;
					break;
				case 3:
					inst3.setText("Insira no campo abaixo a porta de conexão com o servidor:");
					wiz.setText("");
					ind--;
			}
		});
		
		next.addActionListener(e -> {
			switch (ind) {
			case 0:
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

				break;
			case 1:
				if (wiz.getText().equals("")) {
					JOptionPane.showMessageDialog(null, "Digite um IP valido.", "ERRO", JOptionPane.ERROR_MESSAGE);
				} else {
					serverIP = wiz.getText();
					inst3.setText("Insira no campo abaixo a porta de conex�o com o servidor:");
					wiz.setText("");
					ind++;
				}
				break;
			case 2:
				if (wiz.getText().equals("")) {
					JOptionPane.showMessageDialog(null, "Digite uma porta v�lida.", "ERRO", JOptionPane.ERROR_MESSAGE);
				} else {
					serverPort = Integer.parseInt(wiz.getText());
					inst3.setText("Digite o nome que voc� deseja usar no chat:");
					wiz.setText("");
					ind++;
				}
				break;
			case 3:
				if (wiz.getText().equals("")) {
					JOptionPane.showMessageDialog(null, "Digite um nick para acessar o chat.", "ERRO",
							JOptionPane.ERROR_MESSAGE);
				} else {
					nick = wiz.getText();
					k = nick.length();
					if (k > 15) {
						JOptionPane.showMessageDialog(null, "Seu nick deve conter menos de 15 caracteres.", "Erro",
								JOptionPane.ERROR_MESSAGE);

					} else {
						String serverIP = System.getProperty("serverip");
						if (serverIP != null)
							this.serverIP = serverIP;
						String serverPort = System.getProperty("serverport");
						if (serverPort != null)
							this.serverPort = Integer.parseInt(serverPort);
						tMensagens.setText("");
						makeConnection();
						inst3.setText("Voc� j� est� conectado!");
						body.remove(wiz);
						inst1.setText("Clique em Fechar para fechar essa janela.");
						body.add(inst1);
						back.setVisible(false);
						next.setText("Fechar");
						sender.setText("  " + nick + "  ");
						ind++;
					}
				}
				break;
			case 4:
				telaConect.setVisible(false);
			}
		});
		
		
		telaConect.pack();

		m1.addActionListener(e -> System.exit(0));
		
		m2.addActionListener(e -> {
			color = JColorChooser.showDialog(ClientWindow.this, "Choose a color", color);
			if (color == null) {
				color = Color.white;
			}
			tMensagens.setBackground(color);
			pessoas.setBackground(color);
			tenvia.setBackground(color);
		});
		
		m3.addActionListener(e -> {
			m3.setSelected(true);
			m4.setSelected(false);
			m5.setSelected(false);
			changeTheme(0);
		});
		
		m4.addActionListener(e -> {
			m4.setSelected(true);
			m3.setSelected(false);
			m5.setSelected(false);
			changeTheme(1);
		});
		
		m5.addActionListener(e -> {
			m5.setSelected(true);
			m3.setSelected(false);
			m4.setSelected(false);
			changeTheme(2);
		});
		
		m6.addActionListener(e -> JOptionPane.showMessageDialog(null, "!!!", "Sobre o Pifuze", JOptionPane.INFORMATION_MESSAGE));
		
		mconect.addActionListener(e -> {
			DimensionUtils.setBounds(telaConect, 0, 0);
			telaConect.setVisible(true);
		});
		
		msalvar.addActionListener(e -> {
			File file = new File("log_" + (String) data.format(calendario.getTime()));
			
			save.setSelectedFile(file);
			
			int returnVal = save.showSaveDialog(ClientWindow.this); // save dialog box
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				try {
					file = save.getSelectedFile();
					// saves the text into a prasanna file, but you can change it to txt for normal
					// notepad like text files
					FileWriter outputStream = new FileWriter(file.getPath() + ".rtf");
					outputStream.write(cabecalho + "" + tMensagens.getText());
					outputStream.close();
				} catch (IOException ioe) {
					System.out.println("Error");
				}
			}
		});
		
		benvia.addActionListener(e -> copyText());
		tenvia.addActionListener(e -> copyText());
		pessoas.addListSelectionListener(e -> receiver.setText("" + pessoas.getSelectedValue()));
		
		formatacao.addActionListener(e -> {
			Font font = tMensagens.getFont();
			switch(formatacao.getSelectedItem().toString()) {
				case "Sem formatação":
					font = font.deriveFont(Font.PLAIN);
					break;
				case "Negrito":
					font = font.deriveFont(Font.BOLD);
					break;
				case "Itálico":
					font = font.deriveFont(Font.ITALIC);
					break;
				case "Negrito e Itálico":
					font = font.deriveFont(Font.BOLD + Font.ITALIC);
			}
			tMensagens.setFont(font);
		});
		
		tMensagens.setEditable(false);
		scroll.setAutoscrolls(true);

		looks = UIManager.getInstalledLookAndFeels();
		tMensagens.append("Voce nao esta conectado!!!\nClique em File > Conectar para estabelecer a conexao!");
		DimensionUtils.setBounds(this, 780, 550);
		setVisible(true);
		JOptionPane.showMessageDialog(null,
				"Atencao!!!\n\nVoce nao esta conectado!\nClique em File > Conectar para estabelecer uma conexao.",
				"Pifuze Chat v0.8b", JOptionPane.INFORMATION_MESSAGE);
	}
	
	public void mostraSplash() {
		JDialog splashScreen = new SplashScreen();

		splashScreen.setVisible(true);
		try {
			synchronized (splashScreen) {
				splashScreen.wait(3 * 1000);
			}
		} catch (InterruptedException e) {
		}
		splashScreen.setVisible(false);
	}
	
}