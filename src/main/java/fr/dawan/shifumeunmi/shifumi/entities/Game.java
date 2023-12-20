package fr.dawan.shifumeunmi.shifumi.entities;

import fr.dawan.shifumeunmi.shifumi.enums.PlayerGameState;
import fr.dawan.shifumeunmi.shifumi.enums.ShifumiAction;
import fr.dawan.shifumeunmi.shifumi.tools.RandomTool;

import lombok.Data;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.api.entities.Message;

@Data
@NoArgsConstructor
public class Game {
    private String playerEffectiveName;
    private ShifumiAction playerAction;
    private ShifumiAction botAction;
    private PlayerGameState state;
    private long messageId;
    private long channelId;
    private long guildId;

    public Game(String playerEffectiveName, Message message) {
        this.playerEffectiveName = playerEffectiveName;
        this.state = PlayerGameState.UNDEFINED;
        messageId = message.getIdLong();
        channelId = message.getChannel().getIdLong();
        guildId = message.getGuild().getIdLong();
    }

    public Game startGame(ShifumiAction playerAction) {
        this.playerAction = playerAction;
        botAction = RandomTool.getAction();
        state = PlayerGameState.getState(this.playerAction, botAction);
        return this;
    }

    @Override
    public String toString() {
        if (state == PlayerGameState.UNDEFINED)
            return "La partie de %s est toujours en cours".formatted(playerEffectiveName);

        String responseState = (switch (state) {
            case WIN -> "Vous avez gagné :smiling_face_with_tear:";
            case LOOSE -> "Vous avez perdu :smirk:";
            case DRAW -> "C'est une égalité  :disappointed:";
            default -> throw new IllegalStateException("Unexpected value : " + state);
        });

        return "%s a choisi %s%nJ'ai choisi %s%n%s"
                .formatted(playerEffectiveName,
                        playerAction.getEmoji().getAsReactionCode(),
                        botAction.getEmoji().getAsReactionCode(), responseState);
    }
}
