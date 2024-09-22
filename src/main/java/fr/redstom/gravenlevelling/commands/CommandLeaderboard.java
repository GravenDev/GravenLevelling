package fr.redstom.gravenlevelling.commands;

import fr.redstom.gravenlevelling.jpa.services.GravenGuildService;
import fr.redstom.gravenlevelling.utils.jda.Command;
import fr.redstom.gravenlevelling.utils.jda.CommandExecutor;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.utils.FileUpload;

@Command
@RequiredArgsConstructor
public class CommandLeaderboard implements CommandExecutor {

    private final GravenGuildService guildService;

    @Override
    public SlashCommandData data() {
        return Commands.slash("leaderboard", "Affiche le tableau des scores")
                .addOption(OptionType.INTEGER, "page", "Page du tableau des scores", false, false);
    }

    @Override
    @SneakyThrows
    public void execute(SlashCommandInteractionEvent event) {
        int page = Math.abs(event.getOption("page", 1, OptionMapping::getAsInt));
        InteractionHook hook = event.deferReply(false).complete();

        byte[] data = guildService.getLeaderboardImageFor(event.getGuild(), page, event.getMember());

        if (data == null) {
            hook.editOriginal(":x: Il n'existe pas pas de page n°**" + page + "** !").queue();
            return;
        }

        hook.editOriginalAttachments(FileUpload.fromData(data, "image.png"))
                .setActionRow(
                        Button.of(ButtonStyle.PRIMARY, "lb-previous;" + page, "Précédent", Emoji.fromUnicode("⬅️"))
                                .withDisabled(page == 1),
                        Button.of(ButtonStyle.SUCCESS, "euuuuuuuh", "Page " + page).asDisabled(),
                        Button.of(ButtonStyle.PRIMARY, "lb-next;" + page, "Suivant", Emoji.fromUnicode("➡️"))
                )
                .queue();
    }
}
