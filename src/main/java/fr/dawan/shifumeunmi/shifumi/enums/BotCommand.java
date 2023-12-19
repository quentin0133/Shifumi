package fr.dawan.shifumeunmi.shifumi.enums;

import fr.dawan.shifumeunmi.discord.commands.Command;

public enum BotCommand implements Command {
    HELP("Je te donne toutes les commandes que je peux réaliser ! :smiling_face_with_3_hearts:"),
    PLAY("Je joue au Shifumi avec toi, promis je ne triche pas :sunglasses:"),
    LIST_ALL_GAME("Liste les 20 dernières parties que j'ai joué avec tout le monde :point_up::nerd:"),
    LIST_GAME_ME("Liste les 20 dernières parties que j'ai joué a joué avec toi :point_up::nerd:");

    private String description;

    BotCommand(String description) {
        this.description = description;
    }

    @Override
    public String getActionName() {
        // Convert the _ as a separator and use and upper camel case to name the variable
        return name().toLowerCase();
    }

    @Override
    public String getDescription() {
        return this.description;
    }
}