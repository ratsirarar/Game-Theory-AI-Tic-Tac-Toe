/*
 * @(#)TicTacToe.java	1.4 98/06/29
 *
 * Copyright (c) 1997, 1998 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Sun grants you ("Licensee") a non-exclusive, royalty free, license to use,
 * modify and redistribute this software in source and binary code form,
 * provided that i) this copyright notice and license appear on all copies of
 * the software; and ii) Licensee does not utilize the software in a manner
 * which is disparaging to Sun.
 *
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY
 * IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR
 * NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN AND ITS LICENSORS SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING
 * OR DISTRIBUTING THE SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL SUN OR ITS
 * LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT,
 * INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER
 * CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF
 * OR INABILITY TO USE SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES.
 *
 * This software is not designed or intended for use in on-line control of
 * aircraft, air traffic, aircraft navigation or aircraft communications; or in
 * the design, construction, operation or maintenance of any nuclear
 * facility. Licensee represents and warrants that it will not use or
 * redistribute the Software for such purposes.
 */

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.net.*;
import java.util.ArrayList;
import java.applet.*;
import java.util.Collections;
import java.util.Random;


/**
 * A TicTacToe applet. A very simple, and mostly brain-dead
 * implementation of your favorite game! <p>
 *
 * In this game a position is represented by a white and black
 * bitmask. A bit is set if a position is ocupied. There are
 * 9 squares so there are 1<<9 possible positions for each
 * side. An array of 1<<9 booleans is created, it marks
 * all the winning positions.
 *
 * @version 	1.2, 13 Oct 1995
 * @author Arthur van Hoff
 * @modified 04/23/96 Jim Hagen : winning sounds
 * @modified 02/10/98 Mike McCloskey : added destroy()
 * @modified 16/09/16 Andy Ratsirarson: Added intelligentMove()
 */


public
class TicTacToe_3 extends Applet implements MouseListener, ActionListener {
    /**
     * White's current position. The computer is white.
     */
    int white;

    /**
     * Black's current position. The user is black.
     */
    int black;
    
    /**
     * Best Move for DFS search
     */
    int bestMoveDFS;
    
    /**
     * Best Move for MinMax
     */
    int bestMoveMinMax;
    
    
    /**
     * The squares in order of importance...
     */
    final static int moves[] = {4, 0, 2, 6, 8, 1, 3, 5, 7};

    /**
     * The winning positions.
     */
    static boolean won[] = new boolean[1 << 9];
    static final int DONE = (1 << 9) - 1;
    static final int OK = 0;
    static final int WIN = 1;
    static final int LOSE = 2;
    static final int STALEMATE = 3;

    /**
     * Mark all positions with these bits set as winning.
     */
    static void isWon(int pos) {
		for (int i = 0 ; i < DONE ; i++) {
		    if ((i & pos) == pos) {
			won[i] = true;
		    }
		}
    }

    /**
     * Initialize all winning positions.
     */
    static {
	isWon((1 << 0) | (1 << 1) | (1 << 2));
	isWon((1 << 3) | (1 << 4) | (1 << 5));
	isWon((1 << 6) | (1 << 7) | (1 << 8));
	isWon((1 << 0) | (1 << 3) | (1 << 6));
	isWon((1 << 1) | (1 << 4) | (1 << 7));
	isWon((1 << 2) | (1 << 5) | (1 << 8));
	isWon((1 << 0) | (1 << 4) | (1 << 8));
	isWon((1 << 2) | (1 << 4) | (1 << 6));
    }

    /**
     * Compute the best move for white.
     * @return the square to take
     */
    int bestMove(int white, int black) {
    	int bestmove = -1;
    	loop:
    		for (int i = 0 ; i < 9 ; i++) {
    			int mw = moves[i];
    			if (((white & (1 << mw)) == 0) && ((black & (1 << mw)) == 0)) {
    				int pw = white | (1 << mw);
    				if (won[pw]) {
    					// white wins, take it!
    					return mw;
    				}
    				for (int mb = 0 ; mb < 9 ; mb++) {
    					//Thinking ahead: if black can take this position will he win. #black not already in this position (black & (1 << mb)) 0
    					if (((pw & (1 << mb)) == 0) && ((black & (1 << mb)) == 0)) {
    						int pb = black | (1 << mb);
    						if (won[pb]) {
    							// black wins, take another
    							continue loop;
    						}
    					}
    				}
    				// Neither white nor black can win in one move, this will do.
    				if (bestmove == -1) {
    					bestmove = mw;
    				}
    			}
    		}
    	if (bestmove != -1) {
    		return bestmove;
    	}
    	// No move is totally satisfactory, try the first one that is open
    	for (int i = 0 ; i < 9 ; i++) {
    		int mw = moves[i];
    		if (((white & (1 << mw)) == 0) && ((black & (1 << mw)) == 0)) {
    			return mw;
    		}
    	}
    	// No more moves
    	return -1;
    }
    
    /**
     * DFS best move for white
     * 
     * - use depth first search to explore the game state tree
     * - when the search reaches a leaf (win, lost, draw state), assign a score:
     *    white win: 1 
     *    white loss: -1
     *    draw: 0 
     * - backtrack and pick the best move for white (the maximum score)
     * - backtrack and pick the best move for black (the minimum score)
     */
    int depthFirstSearchMove(int current_player, int other_player){
    	if (won[current_player] || isGameOver(current_player, other_player)){
    		return getScore(current_player);
    	}
    	ArrayList<Integer> available_move = new ArrayList<Integer>();
    	ArrayList<Integer> score = new ArrayList<Integer>();
		
    	for (int i = 0 ; i < 9 ; i++) {
			int mw = moves[i];
			if (((current_player & (1 << mw)) == 0) && ((other_player & (1 << mw)) == 0)) {
				available_move.add(mw);
				int pw = current_player | (1 << mw);
				score.add(depthFirstSearchMove(other_player, pw));
			}
		}
    	//pick best move for white player
    	if (isWhitePlayer(current_player)){
    		int max_index_score = score.indexOf(Collections.max(score));
    		bestMoveDFS = available_move.get(max_index_score) ;
    		return score.get(max_index_score);
    	}
    	//pick best move for black player
    	else{
    		int min_index_score = score.indexOf(Collections.min(score));
    		bestMoveDFS = available_move.get(min_index_score) ;
    		return score.get(min_index_score);
    	}
    }
  
    /**
     * Alpha-Beta best move for white
     * 
     * - use alphabeta pruning to shrink the tree space
     * - alpha represents the best possible value at the moment for white player. Default is -9
     * - beta represents the best possible value for black player at the moment. Default is 9
     * 
     * - when the search reaches a leaf (win, lost, draw state), assign a score:
     *    white win: 1 
     *    white loss: -1
     *    draw: 0 
     *    
     * - backtrack and assign the best possible value for Alpha for the current white node (the maximum score)
     *   if alpha >= beta then no need to further look in the current node's non-visited children
     * - backtrack and assign the best possible value for Beta for the current black node (the minimum score)
     *   if alpha >= beta then no need to further look in the current node's non-visited children
     */
    int[] alphBetaSearchMove(int current_player, int other_player, int alpha, int beta, int mw){
    	if (won[current_player] || isGameOver(current_player, other_player)){
    		int[] ret = {mw, getScore(current_player)};
    		return ret;
    	}
    	ArrayList<Integer> available_move = new ArrayList<Integer>();
    	ArrayList<Integer> score = new ArrayList<Integer>();
    	int v;
    	//Assign default value for the current player
    	if (isWhitePlayer(current_player)){
    		v = -9;
    	}
    	//Assign default value for the current player
    	else{
    		v = 9;
    	}  
		int best_mv = mw;
		
    	for (int i = 0 ; i < 9 ; i++) {
			int try_mw = moves[i];
			if (((current_player & (1 << mw)) == 0) && ((other_player & (1 << mw)) == 0)) {
				int pw = current_player | (1 << mw);
				int[] tmp = alphBetaSearchMove(other_player, current_player, alpha, beta, try_mw);
				int child_move = tmp[0];
				int child_score = tmp[1];
		    	//pick best move for white player
		    	if (isWhitePlayer(current_player)){
		    		if (v < child_score){
		    			v = child_score;
		    			best_mv = child_move;
		    		}
		    		
		    		alpha = Math.max(alpha, v);
		    		if (beta <= alpha){
		    			break;
		    		}
		    	}
		    	//pick best move for black player
		    	else{
		    		if (v > child_score){
		    			v = child_score;
		    			best_mv = child_move;
		    		}
		    		beta = Math.min(beta, v);
		    		if (beta <= alpha){
		    			break;
		    		}
		    	}   
			}
		}
    	int[] result = {best_mv, v};
    	return result;
 
    }
    
    /**
     * minMaxsearch Move
     * 
     * Use MinMax Algorithm to search the best move for the user
     * @param current_player
     * @param other_player
     * @return
     */
    int minMaxSearchMove(int current_player, int other_player){
    	if (won[current_player] || isGameOver(current_player, other_player)){
    		return getScore(current_player);
    	}
    	ArrayList<Integer> available_move = new ArrayList<Integer>();
    	ArrayList<Integer> score = new ArrayList<Integer>();
		
    	for (int i = 0 ; i < 9 ; i++) {
			int mw = moves[i];
			if (((current_player & (1 << mw)) == 0) && ((other_player & (1 << mw)) == 0)) {
				available_move.add(mw);
				int pw = current_player | (1 << mw);
				score.add(minMaxSearchMove(other_player, pw));
			}
		}
    	//pick best move for white player
    	if (isWhitePlayer(current_player)){
    		int max_index_score = score.indexOf(Collections.max(score));
    		bestMoveMinMax = available_move.get(max_index_score) ;
    		return score.get(max_index_score);
    	}
    	//pick best move for black player
    	else{
    		int min_index_score = score.indexOf(Collections.min(score));
    		bestMoveMinMax = available_move.get(min_index_score) ;
    		return score.get(min_index_score);
    	}
    }
    
    /**
     * Check if the game is over
     * 
     * @param white
     * @param black
     * @return
     */
    boolean isGameOver(int white, int black){
    	if ((white | black) == DONE){
    		return true;
    	}
    	return false;
    }
    
    /**
     * check if the level the DFS tree is at is for black or white player
     * @param current_player
     * @return
     */
    boolean isWhitePlayer(int current_player){
    	if ((white & current_player) == white) {
    		return true;
    	}
    	return false;
    }
    
    /**
     * Assign score to final state
     *    white win: 1 
     *    white loss: -1
     *    draw: 0 
     * 
     * @param current_player
     * @return
     */
    int getScore(int current_player){
    	if (isWhitePlayer(current_player) && won[current_player]){
    			return 1;
    		}
    	else if (!isWhitePlayer(current_player) && won[current_player]){
    		return -1;
    	}
    	return 0;
    }
    	
    
    /**
     * Compute the best move for white
     * 
     * - If a win is available take it
     * - If the current state shows that black will win, stop him
     * - Connect 2 pieces if available for a potential win
     * - Choose randomly if nothing is found
     */
    int intelligentMove(int white, int black) {
    	//available square left
    	ArrayList<Integer> available_move = new ArrayList<Integer>();
    	
    	//check if win available, take it
    	for (int i=0; i<9; i++){
    		
    		if (((white & (1 << i)) == 0)  && ((black & (1 << i)) == 0)){
    			available_move.add(i);
    			int wm = white | (1 << i);
    			if (won[wm]){
    				System.out.println("Check if win white Called");
    				return i;
    			}
    			int bm = black | (1 << i);
    			//check if black will win, block it
    			if (won[bm]){
    				System.out.println("Check if win black Called");
    				return i;
    			}    				
    		}
    	}
    	
		 //check if connecting move available
    	for (int i: available_move){
    		//For each available move check if there is no black 
    		// on the corresponding row, column and diagonal (if available)
    		//and at least have a white already placed
    		 for (int j=0; j<3; j++ ){
    		 
    			 int col = j;
    			 int row = j*3;
    			//check for each corresponding columns there is no black and at least 1 white
				 if ((i == col || i == col+3 || i == col+6)
				   && ((white & ((1 << col) | (1 << col+3) | (1 << col+6))) != 0)
				   && ((black & ((1 << col) | (1 << col+3) | (1 << col+6))) == 0)){
					  return i;
				 }
				//check for each corresponding rows there is no black and at least 1 white
				 if ((i == row || i == row+1 || i == row+2)
				   && ((white & ((1 << row) | (1 << row+1) | (1 << row+2))) != 0)
				   && ((black & ((1 << row) | (1 << row+1) | (1 << row+2))) == 0)){
					  return i;
				 }
    		 }
    		 //check for diagonals for no blacks and at least 1 white
			 if ((i == 0 || i == 4 || i == 8)
			   && ((white | ((1 << 0) | (1 << 4) | (1 << 8))) != 0)
			   && ((black | ((1 << 0) | (1 << 4) | (1 << 8))) == 0)){
				 System.out.println("available on diagonal"+i);
				  return i;
			 }
			 if ((i == 2 || i == 4 || i == 6)
				   && ((white | ((1 << 2) | (1 << 4) | (1 << 6))) != 0)
				   && ((black | ((1 << 2) | (1 << 4) | (1 << 6))) == 0)){
				 	  System.out.println("available on diagonal"+i);
					  return i;
			}
    	}
    	
    	//Select random if no optiomal solution is found at this time. 
    	if (!available_move.isEmpty()){
    		Random r = new Random();
    		int index = r.nextInt(available_move.size());
    		System.out.println("Random chosen");
    		return available_move.get(index);
    	}
    		
    	
    	return -1;
    }

    
    /**
     * User move.
     * @return true if legal
     */
    boolean yourMove(int m) {
    	if ((m < 0) || (m > 8)) {
	    	return false;
		}
		if (((black | white) & (1 << m)) != 0) {
	    	return false;
		}
		black |= 1 << m;
		return true;
    }

    /**
     * Computer move.
     * @return true if legal
     */
    boolean myMove() {
    	if ((black | white) == DONE) {
	    	return false;
		}
    	int best;
    	if (useMethod == useAIMove.getName()){
    		best = intelligentMove(white, black);
    	}
    	else if(useMethod == useDFSMove.getName()){
    		depthFirstSearchMove(white, black);
    		best = bestMoveDFS;
    	}
    	else if(useMethod == useMinMaxMove.getName()){
    		minMaxSearchMove(white, black);
    		best = bestMoveMinMax;
    	}
    	else if(useMethod == useAlphaBetaMove.getName()){
    		int[] result = alphBetaSearchMove(white, black, -9, 9, -1);
    		best = result[0];
    	}
    	else{
    		best = bestMove(white, black);
    	}
		
		white |= 1 << best;
		return true;
    }

    /**
     * Figure what the status of the game is.
     */
    int status() {
	if (won[white]) {
	    return WIN;
	}
	if (won[black]) {
	    return LOSE;
	}
	if ((black | white) == DONE) {
	    return STALEMATE;
	}
	return OK;
    }
    
    /**
     * Method to use
     */
    String useMethod;
    
    /**
     * Button to use Best move method
     */
    Button useBestMove;
    
    /**
     * Button to use AI move method
     */
    Button useAIMove;
    
    /**
     * Button to use DFS move method
     */
    Button useDFSMove;
    
    /**
     * Button to use MinMax move method
     */
    Button useMinMaxMove;
    
    /**
     * Button to use AlphaBeta move method
     */
    Button useAlphaBetaMove;
    

    /**
     * Who goes first in the next game?
     */
    boolean first = true;

    /**
     * The image for white.
     */
    Image notImage;

    /**
     * The image for black.
     */
    Image crossImage;

    /**
     * Initialize the applet. Resize and load images.
     */
    public void init() {
	    useBestMove = new Button("Best Move Method");
	    this.add(useBestMove);
	    useBestMove.addActionListener(this);
	    
	    useAIMove = new Button("AI Method");
	    this.add(useAIMove);
	    useAIMove.addActionListener(this);
	    
	    useDFSMove = new Button("DFS Method");
	    this.add(useDFSMove);
	    useDFSMove.addActionListener(this);
	    
	    useMinMaxMove = new Button("MinMax Method");
	    this.add(useMinMaxMove);
	    useMinMaxMove.addActionListener(this);
	    
	    useAlphaBetaMove = new Button("AlphaBeta Method");
	    this.add(useAlphaBetaMove);
	    useAlphaBetaMove.addActionListener(this);
	    
		notImage = getImage(getCodeBase(), "not.gif");
		crossImage = getImage(getCodeBase(), "cross.gif");
		addMouseListener(this);
    }

    public void destroy() {
        removeMouseListener(this);
    }

    /**
     * Button listener 
     */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == useBestMove) {
			System.out.println("Using Best Move Method for solving Tic Tac Toe");
			useMethod = useBestMove.getName();
		}
		else if (e.getSource() == useDFSMove){
			System.out.println("Using DFS method for solving Tic Tac Toe");
			useMethod = useDFSMove.getName();
		}
		else if (e.getSource() == useMinMaxMove){
			System.out.println("Using MinMax method for solving Tic Tac Toe");
			useMethod = useMinMaxMove.getName();
		} 
		else if (e.getSource() == useAlphaBetaMove){
			System.out.println("Using Alpha Beta method for solving Tic Tac Toe");
			useMethod = useDFSMove.getName();
		} 
		else{
			System.out.println("Using AI method for solving Tic Tac Toe");
			useMethod = useAIMove.getName();
		}
	}
    
    /**
     * Paint it.
     */
    public void paint(Graphics g) {
    	Dimension d = getSize();
    	g.setColor(Color.black);
    	int xoff = d.width / 3;
    	int yoff = d.height / 3;
    	g.drawLine(xoff, 0, xoff, d.height);
    	g.drawLine(2*xoff, 0, 2*xoff, d.height);
    	g.drawLine(0, yoff, d.width, yoff);
    	g.drawLine(0, 2*yoff, d.width, 2*yoff);

		int i = 0;
		for (int r = 0 ; r < 3 ; r++) {
			for (int c = 0 ; c < 3 ; c++, i++) {
				if ((white & (1 << i)) != 0) {
					g.drawImage(notImage, c*xoff + 1, r*yoff + 1, this);
				} else if ((black & (1 << i)) != 0) {
					g.drawImage(crossImage, c*xoff + 1, r*yoff + 1, this);
				}
			}
		}
    }

    /**
     * The user has clicked in the applet. Figure out where
     * and see if a legal move is possible. If it is a legal
     * move, respond with a legal move (if possible).
     */
    public void mouseReleased(MouseEvent e) {
	int x = e.getX();
	int y = e.getY();

	switch (status()) {
	  case WIN:
	  case LOSE:
	  case STALEMATE:
	    //play(getCodeBase(), "audio/return.au");
	    white = black = 0;
	    bestMoveDFS = -1;
	    if (first) {
		white |= 1 << (int)(Math.random() * 9);
	    }
	    first = !first;
	    repaint();
	    return;
	}

	// Figure out the row/column
	Dimension d = getSize();
	int c = (x * 3) / d.width;
	int r = (y * 3) / d.height;
	if (yourMove(c + r * 3)) {
	    repaint();

	    switch (status()) {
	      case WIN:
		//play(getCodeBase(), "audio/yahoo1.au");
		break;
	      case LOSE:
		//play(getCodeBase(), "audio/yahoo2.au");
		break;
	      case STALEMATE:
		break;
	      default:
		if (myMove()) {
		    repaint();
		    switch (status()) {
		      case WIN:
			//play(getCodeBase(), "audio/yahoo1.au");
			break;
		      case LOSE:
			//play(getCodeBase(), "audio/yahoo2.au");
			break;
		      case STALEMATE:
			break;
		      default:
			//play(getCodeBase(), "audio/ding.au");
		    }
		} else {
		    play(getCodeBase(), "audio/beep.au");
		}
	    }
	} else {
	    play(getCodeBase(), "audio/beep.au");
	}
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public String getAppletInfo() {
	return "TicTacToe by Arthur van Hoff";
    }
}