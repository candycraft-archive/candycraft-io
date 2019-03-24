import de.candycraft.io.server.models.clan.Clan;
import de.candycraft.io.server.models.clan.ClanMember;
import de.candycraft.io.server.models.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Paul
 * on 24.03.2019
 *
 * @author pauhull
 */
public class JsonGenerator {

    public static void main(String[] args) {
        List<ClanMember> clanMembers = new ArrayList<>();
        clanMembers.add(ClanMember.builder().name("pauhull").uuid(UUID.randomUUID()).id(0).clanId(1).joinedAt(0).clanGroupId(0).build());
        Clan clan = Clan.builder().createdAt(0).exp(0).level(0).fullName("Pauls clan").id(0).members(clanMembers).tag("KKKK").build();
        System.out.println(clan.toJSONString());

        Player player = Player.builder().server(0).id(0).onlineTime(0).uuid(UUID.randomUUID()).name("pauhull").build();
        //System.out.println(player.toJSONString());
    }

}
