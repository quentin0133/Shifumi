package fr.dawan.shifumeunmi.shifumi.tools;

import fr.dawan.shifumeunmi.shifumi.enums.ShifumiAction;

import java.util.Random;

public class RandomTool {
    private static final Random RANDOM = new Random();
    public static ShifumiAction getAction() {
        return ShifumiAction.values()[RANDOM.nextInt(0, 3)];
    }
}