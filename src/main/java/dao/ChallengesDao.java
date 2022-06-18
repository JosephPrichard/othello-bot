package dao;

import dto.Player;
import dto.Game;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChallengesDao
{
    private final Map<Player, Game> challenges = new ConcurrentHashMap<>();


}
