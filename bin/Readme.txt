Game Theory Track: TicTacToe

@author Andy Ratsirarson
@Programming Assignment 1
@class 	EN.605.421.83.FA16 Foundations of Algorithms

@JDK Java SE 1.8
@IDE Eclipse
@Files TicTacToe_3.java cross.GIF not.GIF Readme.txt

Release Note PA3: 
- a custom method AlphaBeta method has been provided as an alternative to bestMove and IntelligentMove and DFS:
  The alphaBetaSearchMove has the following property:
     * it uses depth first search to go down to the leaves and assign scores: white player(computer)
        white player win: 1
        white player lose: -1
        draw: 0
     * As we go down the Tree we keep an extra 2 variables Alpha (maximum best value at the moment) and Beta (min best value at the moment) that will help prune the tree search.
     Alpha is updated on white player only and beta on black player only. They are defaulted to -9 and 9 respectively. 
- a new button has been added AlphaBeta to indicate the use of the Alpha Beta algorithm to be used by the computer

- a custom method MinMax method has been provided as an alternative to bestMove, IntelligentMove, DFS and AlphaBeta :
  This is similar to Depth First Search since it goes down the tree and search for the best possible way.
  The MinMax has the following property:
     * it uses depth first search to go down to the leaves and assign scores: white player(computer)
        white player win: 1
        white player lose: -1
        draw: 0
     * As we go down the Tree we keep an extra 2 variables Alpha (maximum best value at the moment) and Beta (min best value at the moment) that will help prune the tree search.
     Alpha is updated on white player only and beta on black player only. They are defaulted to -9 and 9 respectively. 
       
- a new button has been added MinMax to indicate the use of the Alpha Beta algorithm to be used by the computer

- Due to constraint of time I was not able to finish BFS and BFS heuristics. 
Both are looking in a tree where it looks at all the nodes in each level first before going down the next level.



Release Note PA2: 
- a custom method depthFirstSearchMove method has been provided as an alternative to bestMove and IntelligentMove:
  The depthFirstSearchMove has the following property:
     * it uses depth first search to go down to the leaves and assign scores: white player(computer)
        white player win: 1
        white player lose: -1
        draw: 0
     * for picking the best move among available moves, we use the move with the best score from the white player perspective
       and pick the least score from the black player perspective.
       
- a new button has been added DFS to indicate the use of the Depth first search algorithm to be used by the computer

Release Note PA1: 

- A custom method intelligentMove has been added as an alternative to bestMove provided
	
	The IntelligentMove has the following property:
		 * If a win is available the agent will take it
	     * If the current state shows that black will win, the agent will block that move
	     * Connect 2 pieces if available for a potential win
	     * Choose random move if no optimal move is found

- 2 Buttons have been added to the UI. The user can choose between using either Method IntelligentMove or BestMove. 
  By default BestMove is used.
  Anytime in the game the user can choose between both moves.
  
