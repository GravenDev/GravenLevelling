package fr.redstom.gravenlevelling.utils;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public interface CommandExecutor {

    SlashCommandData data();

    void execute(SlashCommandInteractionEvent event);

}
