package fr.redstom.gravenlevelling.jda.services;

import fr.redstom.gravenlevelling.jda.entities.GravenGuild;
import fr.redstom.gravenlevelling.jda.entities.GravenMember;
import fr.redstom.gravenlevelling.jda.repositories.GravenGuildRepository;
import fr.redstom.gravenlevelling.jda.repositories.GravenMemberRepository;
import fr.redstom.gravenlevelling.utils.ImageGenerator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.entities.Guild;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

@Service
@RequiredArgsConstructor(onConstructor_ = @__(@Lazy))
public class GravenGuildService {

    private final GravenGuildRepository guildRepository;
    private final GravenMemberRepository memberRepository;

    private final GravenMemberService memberService;

    private final ImageGenerator imageGenerator;

    public GravenGuild getOrCreateByDiscordGuild(Guild guild) {
        return guildRepository
                .findById(guild.getIdLong())
                .orElseGet(() -> guildRepository.save(
                        GravenGuild.builder()
                                .id(guild.getIdLong())
                                .build()));
    }

    @Transactional
    public Page<GravenMember> getLeaderboardOf(Guild guild, int page) {
        GravenGuild gGuild = getOrCreateByDiscordGuild(guild);

        return memberRepository.findAllByGuild(gGuild, PageRequest.of(page - 1, 10));
    }

    @Transactional
    @SneakyThrows
    public byte[] getLeaderboardImageFor(Guild guild, int page) {
        Page<GravenMember> members = getLeaderboardOf(guild, page);
        if (members.isEmpty()) {
            return null;
        }

        BufferedImage image = imageGenerator.generateLeaderboardImage(page, members.getContent(), memberService::getDiscordMemberByMember);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        ImageIO.write(image, "png", stream);
        stream.flush();

        return stream.toByteArray();
    }
}
