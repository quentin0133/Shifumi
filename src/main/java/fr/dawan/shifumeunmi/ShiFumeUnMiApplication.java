package fr.dawan.shifumeunmi;

import fr.dawan.shifumeunmi.discord.ShifumiBot;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ShiFumeUnMiApplication {
	public static void main(String[] args) {
		ShifumiBot shifumiBot = new ShifumiBot(args[0]);
	}
}
