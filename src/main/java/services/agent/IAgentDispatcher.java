/*
 * Copyright (c) Joseph Prichard 2023.
 */

package services.agent;

import othello.Move;
import othello.OthelloBoard;

import java.util.List;
import java.util.concurrent.Future;

public interface IAgentDispatcher {

    Future<List<Move>> findMoves(OthelloBoard board, int depth);

    Future<Move> findMove(OthelloBoard board, int depth);
}
