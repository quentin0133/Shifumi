package fr.dawan.shifumeunmi.shifumi.enums;

import net.dv8tion.jda.api.entities.emoji.Emoji;

import java.util.Arrays;

public enum ShifumiAction {
    ROCK("U+1FAA8"), PAPER("U+1F4F0"), SCISSOR("U+2702");

    private final Emoji emoji;

    ShifumiAction(String unicode) {
        this.emoji = Emoji.fromUnicode(unicode);
    }

    public static ShifumiAction getActionFromUnicode(String unicode) {
        return Arrays.stream(ShifumiAction.values())
                .filter(action -> action.emoji.equals(Emoji.fromUnicode(unicode)))
                .findFirst().orElse(null);
    }

    public static boolean hasReaction(String unicode) {
        return Arrays.stream(ShifumiAction.values())
                .anyMatch(action -> action.emoji.equals(Emoji.fromUnicode(unicode)));
    }

    public Emoji getEmoji() {
        return emoji;
    }
}