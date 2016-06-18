package br.inf.prismasoft.chat.utils;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Window;

public class DimensionUtils {
	
	public static void setBounds(Window context, int width, int height) {
		Dimension tela = Toolkit.getDefaultToolkit().getScreenSize();
		Double largura = (tela.getWidth() - 350) / 2;
		Double altura = (tela.getHeight() - 300) / 2;
		context.setBounds(largura.intValue(), altura.intValue(), width, height);
	}

}
