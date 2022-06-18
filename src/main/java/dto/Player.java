package dto;

import net.dv8tion.jda.api.entities.User;
import utils.DiscordUtils;

public class Player
{
    private long id;
    private String name;

    public Player(User user) {
        this.id = DiscordUtils.toLongId(user.getId());
        this.name = user.getAsTag();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return id == player.getId();
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }

    @Override
    public String toString() {
        return Long.toString(id);
    }
}
