/*
 * Copyright (c) Joseph Prichard 2023.
 */

package services;

import othello.Move;
import othello.OthelloAgent;

import java.util.List;
import java.util.concurrent.*;
import java.util.logging.Logger;

public class AgentService
{
    private final Logger logger = Logger.getLogger("service.agent");
    private final ExecutorService executorService = Executors.newFixedThreadPool(4);

    public void findRankedMoves(AgentRequest<List<Move>> agentRequest) {
        executorService.submit(() -> {
            OthelloAgent agent = new OthelloAgent(agentRequest.getGame().getBoard(), agentRequest.getDepth());
            List<Move> moves = agent.findRankedMoves();
            agentRequest.getOnComplete().accept(moves);
        });
        logger.info("Started agent ranked moves calculation of depth " + agentRequest.getDepth());
    }

    public void findBestMove(AgentRequest<Move> agentRequest) {
        executorService.submit(() -> {
            OthelloAgent agent = new OthelloAgent(agentRequest.getGame().getBoard(), agentRequest.getDepth());
            Move move = agent.findBestMove();
            agentRequest.getOnComplete().accept(move);
        });
        logger.info("Started agent best move calculation of depth " + agentRequest.getDepth());
    }
}