package br.inf.prismasoft.chat.client;

import java.awt.Container;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;

import br.inf.prismasoft.chat.utils.DimensionUtils;

public class SplashScreen extends JDialog {
	
	private static final String SPLASH_IMAGE = "/images/splash.jpg";

	public SplashScreen() {
		JLabel splash = new JLabel(new ImageIcon(this.getClass().getResource(SPLASH_IMAGE).getPath()));
		
		Container c = getContentPane();
		
		c.add(splash);
		
		DimensionUtils.setBounds(this, 0, 0);
		setUndecorated(true);
		pack();
	}

}
