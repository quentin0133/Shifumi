package fr.dawan.shifumeunmi.shifumi.enums;

import java.util.*;

public enum PlayerGameState {
    UNDEFINED, WIN, LOOSE, DRAW;

    private static final EnumMap<ShifumiAction, ShifumiAction> winningMap = new EnumMap<>(Map.of(
            ShifumiAction.ROCK, ShifumiAction.SCISSOR,
            ShifumiAction.SCISSOR, ShifumiAction.PAPER,
            ShifumiAction.PAPER, ShifumiAction.ROCK
    ));

    public static PlayerGameState getState(ShifumiAction action1, ShifumiAction action2) {
        if (action1 == action2) return DRAW;
        return winningMap.entrySet().stream()
                .anyMatch(entry -> action1 == entry.getKey() && action2 == entry.getValue())
                ? WIN : LOOSE;
    }
}