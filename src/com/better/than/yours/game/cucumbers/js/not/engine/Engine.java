package com.better.than.yours.game.cucumbers.js.not.engine;
import com.better.than.yours.game.cucumbers.js.not.controllers.GameViewController;
import com.better.than.yours.game.cucumbers.js.not.models.Board;
import com.better.than.yours.game.cucumbers.js.not.models.BoardObserver;
import com.better.than.yours.game.cucumbers.js.not.models.Cell;
import com.better.than.yours.game.cucumbers.js.not.models.Position;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by mati on 2016-05-10.
 */
public class Engine {
    Board board;
    BoardObserver observer;
    Rules rules;
    Thread gameThread;
    GameViewController view;
    private int livePopulation;
    boolean isRunning = false;
    public Engine(Board board, Rules rules){
        this.board = board;
        this.rules = rules;
        this.observer = new BoardObserver(board);
        this.gameThread = new Thread(new GameThread(this), this.toString());
        System.out.println(this.toString());
        board.passObserver(observer);
    }

    public void passView(GameViewController gameViewController) {
        this.view = gameViewController;
    }

    public void startGame(int startPopulation){
        for (int i = 0; i < startPopulation; i++){
            int x = (int) (Math.random() * board.getWidth());
            int y = (int) (Math.random() * board.getHeight());
            Position position = new Position(x, y, board);
            board.createCell(position, true);
        }
        observer.push();
        isRunning = true;
        gameThread.start();

    }
    public void startGame(Position[] positions){
        for (int i = 0; i < positions.length; i++){
            board.createCell(positions[i], true);
        }
        observer.push();
        isRunning = true;
        gameThread.start();
    }
    public void endGame() {
        System.out.println("call");
        isRunning = false;
    }
    public void continueGame(){
        isRunning = true;
    }

    void checkEach(){
        livePopulation = 0;
        HashMap<Integer, Cell> cellsInBoard = new HashMap<>(board.getCells());
        for (Map.Entry<Integer, Cell> entry : cellsInBoard.entrySet()){
            Cell cell = entry.getValue();
            int neighbors = cell.getLivingNeighbours();
            if (cell.isAlive()){
                livePopulation += 1;
                //System.out.println("Cell x: " + cell.position.getX() + ", y:" + cell.position.getY() + "have: " + cell.getLivingNeighbours());
                //System.out.println("Coords: x: " + cell.position.getX() + " y: " + cell.position.getY());
                aliveOracle(cell, neighbors);
            } else {
                deadOracle(cell, neighbors);
            }
        }
    }
    void aliveOracle(Cell cell, int neighbors){
        if (neighbors <= rules.tooAloneLimit){
            cell.kill();
        } else if (neighbors >= rules.overPopulationLimit) {
            cell.kill();
        } else if (view != null){
            view.add(cell.position, cell.getId());
        }
    }
    void deadOracle(Cell cell, int neighbors){
        if (neighbors == rules.bornTime){
            if (view != null){
                view.add(cell.position, cell.getId());
            }
            cell.revive();
        }
    }
    int getPopulation(){

        return livePopulation;
    }
}
