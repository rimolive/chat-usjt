package br.inf.prismasoft.chat.i18n;

public enum Message {
	CLIENT_TITLE("Pifuze Chat v0.8b"),
	SERVER_TITLE("");

	String msg;
	
	private Message(String msg) {
		this.msg = msg;
	}
	
	public String content() {
		return this.msg;
	}
	
}
