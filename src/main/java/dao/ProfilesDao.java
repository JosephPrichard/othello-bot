package dao;

import dto.Player;
import dto.Profile;
import org.jetbrains.annotations.NotNull;
import utils.EloUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ProfilesDao
{
    private final Map<Player, Profile> profiles = new ConcurrentHashMap<>();

    private void insertProfile(Profile profile) {
        profiles.put(profile.getPlayer(), profile);
    }

    @NotNull
    public Profile retrieveProfile(Player player) {
        Profile profile = profiles.get(player);
        if (profile == null) {
            Profile newProfile = new Profile(player);
            insertProfile(newProfile);
            return newProfile;
        } else {
            return profile;
        }
    }

    public void updateProfiles(Player won, Player lost) {
        Profile wonProfile = retrieveProfile(won);
        Profile lostProfile = retrieveProfile(lost);

        wonProfile.setWon(wonProfile.getWon() + 1);
        lostProfile.setLost(lostProfile.getLost() + 1);

        float probWin = EloUtils.probability(lostProfile.getElo(), wonProfile.getElo());
        float probLost = EloUtils.probability(wonProfile.getElo(), lostProfile.getElo());

        wonProfile.setElo(EloUtils.ratingWon(wonProfile.getElo(), probWin));
        wonProfile.setElo(EloUtils.ratingLost(lostProfile.getElo(), probLost));
    }
}
