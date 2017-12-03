// package go;
/**
 * GomokuStrategy.java
 */


import java.util.Arrays;

//AI Class

/**
 * The Class GomokuStrategy has the calculation structure for the
 * implementation of MiniMax and ALPHA-BETA algorithms, each think
 * level needs a new GomokuStrategy object.  
 */
public class GomokuStrategy {


	    
	// Added constants MARK
	static final int PLAYER_A = 0;         //piece PLAYER_A
	static final int PLAYER_B = 1;         //piece PLAYER_B
	static final int FREE = 3;          //a free place on the board
	static  int[] EMPTY = {32};    //no lines are found
	static final int INFINIT = 999999;
	
    /**board to be analyzed*/
    GameBoard board;          
    /**next move to be simulated*/
    PlayerPosition move;      
    /**sorted set with all Free valid places*/
    GomokuAvailableField free;         
    /**value of the actual board*/
    int value = 0;          
    /**Intermediate values of a think level for the position*/
    int intValue;           
    /**player that set the last piece*/
    int TDPlayer;          
    /**think level to be used*/
    
    //CHANGE maxTD  TO 5 BEFORE SUBMITTING THE FINAL VERSION
    //AI Thinking Depth
    int maxTD = 3;  //next five potential game states are taken into consideration             
        
    /**for the ALPHA-BETA algorithms*/
    int alpha = -INFINIT;   
    /**for the ALPHA-BETA algorithms*/
    int beta = INFINIT;     
    /** 
     * expPat is a two dimension array to stores for each Player the number
     * of some special patterns. With this information is possible to cancel
     * a calculation if is obviously that with this move the game will be lost
     * expPat[PLAYER_A][TREE] # of six-patterns with tree PLAYER_A pieces
     * expPat[PLAYER_A][FOURFIVE] # of five-patterns with four PLAYER_A pieces
     * expPat[PLAYER_A][FOURSIX] # of six-patterns with four PLAYER_A pieces
     * expPat[PLAYER_A][FIVE] # of six-patterns with tree PLAYER_A pieces
     * there are corresponding fields for PLAYER_B
     */
    int[][] expPat = {{0,0,0,0},{0,0,0,0}};
    /** Structure for line evaluation */
    Eval lineEvalStruc;

    /**
     * Creates a new empty <code>GomokuStrategy</code> structure.
     * @param b             the board to be evaluated
     * @param maxTD         the think depth to use
     * @param strategy      the evaluation method to use
     */
    protected GomokuStrategy(final GameBoard b) {
        //this.maxTD = 7;      
    	//this.maxTD = 1;  //change to 7 later on
        //this.strategy = (int) strategy;
   
        TDPlayer = b.getStatus();
        lineEvalStruc = new Eval(TDPlayer,expPat);
        board = new GameBoard(b);
        free = new GomokuAvailableField(15,15);
        // if the board is not free, initialize some values:
        if (board.getStatus() < FREE) {
            calcFreeList();
            calcBoardValue();
            if (maxTD>1) sortFreeList();
        }
    }
    
    /**
     * Creates a new <code>GomokuStrategy</code> structure as a copy from another.
     * @param es            the evaluation structure to be copied
     */
    protected GomokuStrategy(final GomokuStrategy es) {
        value = es.value;
        alpha = es.alpha;
        beta = es.beta;
        maxTD = es.maxTD;

        TDPlayer = es.TDPlayer;
        board = new GameBoard(es.board);
        free = new GomokuAvailableField(es.free);
        if (es.move != null) 
            move = (PlayerPosition) free.get(es.free.indexOf(es.move));
        for (int i=PLAYER_A;i<=PLAYER_B;i++) 
            System.arraycopy(es.expPat[i],0,expPat[i],0,expPat[i].length);
        lineEvalStruc = new Eval(es.lineEvalStruc.lastPiece,expPat);
    }
    
    /**
     * Copy a <code>GomokuStrategy</code> structure into another. This Method will
     * be called to initialize the GomokuStrategy structure before the calculation
     * for a new think level started and TDPlayer is already set for this
     * new think level
     * @param es            the evaluation structure to be copied
     */
    protected void copyEvaluation(final GomokuStrategy es) {
        value = es.value;
        alpha = es.alpha;
        beta = es.beta;
        maxTD = es.maxTD;
        TDPlayer = (int) ((es.TDPlayer+1)%2);

        board.boardcopy(es.board);
        free = new GomokuAvailableField(es.free);
        if (es.move != null) 
            move = (PlayerPosition) free.get(es.free.indexOf(es.move));
        for (int i=PLAYER_A;i<=PLAYER_B;i++) 
            System.arraycopy(es.expPat[i],0,expPat[i],0,expPat[i].length);
    }

    /**
     * set into free all fields that can be used to set the 
     * next piece.
     */
    protected void calcFreeList() {
        addFreeRound(board.getOccupList());
    }
       
    /**
     * set all free fields, around the position p, into free
     * @param p     the position which should be analyzed
     */
    protected void addFreeRound(final PlayerPosition p) {
        final PlayerPosition[] freeCoord = board.getFreeRound(p);
        for (int i=0; freeCoord != null && i < freeCoord.length;i++ ) {
            free.addLast(freeCoord[i]);
        }
    }
    
    /**
     * set all free fields, around all position of the list p, into free
     * @param p     the position list which should be analyzed
     */
    protected void addFreeRound(final PlayerPosition[] p) {
        for (int i=0; i < p.length; i++ ) addFreeRound(p[i]);
    }

    /**
     * Sort free with the best moves at first, to accelerate the algorithm to 
     * find the next best move. With a sorted free the ALPHA-BETA algorithm will
     * cut early a calculation.
     * This method use think depth 1 to sort free.
     **/
    protected void sortFreeList() {
        GomokuStrategy hlpEval = new GomokuStrategy(this);  // create hlpeval as copy
        hlpEval.maxTD = (int) 1;                   // change think depth to 1
        AI hlpMM = new AI(hlpEval);         // calculate the moves
        free = new GomokuAvailableField(hlpMM.getFreeSorted(), 15, 15);
    }
    
   

    /**
     * set a piece into a board, calculate the new board value and add into 
     * free the new free fields.
     * @param lp        player that set the last piece before the calculations
     */
    protected void setPiece(){
        // expPat will be changed into this method
        value += lineEvalStruc.subLineValue(board.getRow(move));
        value += lineEvalStruc.subLineValue(board.getColumn(move));
        value += lineEvalStruc.subLineValue(board.getDiagUp(move));
        value += lineEvalStruc.subLineValue(board.getDiagDown(move));
        board.placeAIStone(move,TDPlayer);
        addFreeRound(move);
        value += lineEvalStruc.addLineValue(board.getRow(move));
        value += lineEvalStruc.addLineValue(board.getColumn(move));
        value += lineEvalStruc.addLineValue(board.getDiagUp(move));
        value += lineEvalStruc.addLineValue(board.getDiagDown(move));
        intValue = (1-2*TDPlayer)*INFINIT;
    }
    
    /**
     * Calculates the value of the game situation. This is only used to
     * Initialize a new GomokuStrategy.
     */
    protected void calcBoardValue(){
        int i;
        for ( i=1; i <= 15; i++) //board size is 15
            value += lineEvalStruc.addLineValue(board.getRow(i));
        for ( i=1; i <= 15; i++) 
            value += lineEvalStruc.addLineValue(board.getColumn(i));
        for ( i=5; i < 15 + 15 -4; i++) 
            value += lineEvalStruc.addLineValue(board.getDiagUp(i));
        for ( i=5; i < 15+ 15-4; i++) //board size is 15
            value += lineEvalStruc.addLineValue(board.getDiagDown(i));
    }
    
    /*
     * The subclass Eval has a structure and methods to calculate the value
     * of each line on a board
     */
    private final class Eval {
        /**player that set the last piece before the calculations*/
        private int lastPiece;         
        /**max intermediate value. Is reseted if a pattern is recognized*/
        private int intValMax;  
        /**intermediate value. Is reseted on each new analyzed position*/
        private int intVal;     
        /**store the pattern that are found*/
        private int pattern;
        /**the length of the pattern*/
        private int count;      
        /**color of the pattern*/
        private int color;
        /**recognized value for PLAYER_A and PLAYER_B*/
        private int[] value = new int[2];  
        /**Intermediate value for eP (extPat)*/
        private int[] ss = new int[4];  
        /**how extPat*/
        private int[][] eP;            
        
        /**
         * crates a new Eval structure
         * @param lastPiece     player that set the last piece before the calculations
         * @param extPat        Extended Patterns
         **/
        protected Eval(final int lastPiece, int[][] extPat) {
            this.lastPiece=lastPiece;
            eP=extPat;
        }
        
        /*
         * accept values of patterns and counts the number of special patterns
         */
        private void recognize() {
            int i;
            if (color!=FREE) {
                if ( maxTD == 1 || lastPiece==PLAYER_A ) {
                    /**
                     * If the pattern belongs the last player the pattern value will
                     * be doubled if the think level is odd or for strategy 1
                     * for the move generation for PLAYER_B
                     */
                    if ( lastPiece == color ) {
                        intValMax <<= 1;
                    }
                }
                value[color] += intValMax;
                //the number of special patterns for extPat will be counted
                for (i=3; i>=0; i--) {
                    if (ss[i]>0) {
                        eP[color][i]++;
                        break;
                    }
                }
                for (i=0;i<ss.length;i++) ss[i]=0;
                intValMax = 0;
            }
        }
        
        /**
         * calculates a negative value of a move
         * @param    js to calculate
         * @return   negative value of line
         */
        protected int subLineValue( final int[] js ) {
            for (int i=0; i<eP[0].length; i++) eP[PLAYER_A][i] *= (-1);
            for (int i=0; i<eP[1].length; i++) eP[PLAYER_B][i] *= (-1);
            final int ret = addLineValue(js);
            for (int i=0; i<eP[0].length; i++) eP[PLAYER_A][i] *= (-1);
            for (int i=0; i<eP[1].length; i++) eP[PLAYER_B][i] *= (-1);
            return -ret;
        }            
        
        /**
         * Calculate the value of a line. Pattern with length 6 and 5 are analyzed
         * to evaluate a line.
         * @param js          line which will be evaluated
         * @param TDPlayer    color from last player
         * @param maxthinkDepth think depth to be used
         * @param strategy      strategy to be used
         * @return  line value.
         * @return  if line value > 0, PLAYER_A is in advantage.
         * @return  if line value < 0, PLAYER_B is in advantage.
         */
        protected int addLineValue( final int[] js ) {
            Arrays.fill(value,(int) 0);
            if ( js.length > 15 ) {  // Check if array is empty
                //Initialization of EVAL-Variables
                intValMax = 0;
                pattern = 0;
                color = FREE;
                Arrays.fill(ss,(int) 0);
                int pos;     //actual position into line
                //seek the first piece into line
                for ( pos=1 ; js[pos] == FREE && pos < js.length-1 ; pos++);
                count = java.lang.Math.min(pos,6)-1;
                for ( ; pos < js.length-1 ; pos++){
                    intVal = 0;
                    if ( js[pos] == FREE ) {
                        pattern <<= 1;
                        if (count<6) count ++;
                    } else {
                        if ( color == FREE ) color=js[pos];
                        if ( color == js[pos] ) {
                            pattern <<= 1;
                            pattern |= 1;
                            count ++;
                        } else {
                            // here a color change is recognized, therefore the intMaxValue
                            // must be saved and a new pattern started.
                            recognize();
                            color = js[pos];
                            // Pattern rollback. The FREE cells on pattern ending should be added to the next pattern
                            for ( count = 0; pattern % 2 == 0 ; count++ ) pattern >>= 1;
                            pattern = 1;
                            count++;
                        }
                    }
                    if ( count == 6 ) {
                        if ( pattern != 0) {
                        	intVal = getValue61(pattern);
 
                            if ( intVal != 0 && intValMax < intVal ){
                                intValMax = intVal;
                            }
                        }
                        // reduces the pattern to the least four bits
                        pattern &= 31;
                        count--;
                        if ( pattern == 0 ) {
                            // the line is finished or five free cells are recognized.
                            recognize();
                            color = js[pos];
                        }
                    }
                    if ( count == 5 && intVal == 0 && pattern != 0) {
                    	intVal = getValue51(pattern);

                        if ( intVal != 0 && intValMax < intVal ) {
                            intValMax = intVal;
                        }
                    }
                    if ( pos==(js.length-2)) {
                        // the line is finished or five free cells are recognized.
                        recognize();
                    }
                } 
            } // If line is empty is nothing to do
            return (value[PLAYER_A] - value[PLAYER_B]);
        }

        /**
         * Gets the value a pattern with five positions.
         * @param  a pattern to evaluate
         * @return  the pattern value
         */
        private int getValue51(final int pattern) {
        
        		int val = 0;
        	
        		switch (pattern){
                case 0 : val = 0;
                case 1 : val = 1;
                case 2 : val = 1;
                case 3 : val = 9;
                case 4 : val = 1;
                case 5 : val = 8;
                case 6 : val = 10;
                case 7 : val = 90;
                case 8 : val = 1;
                case 9 : val = 7;
                case 10 : val = 9;
                case 11 : val= 80;
                case 12 : val = 10;
                case 13 : val = 80;
                case 14 : val = 100;
                case 15 : 
                		ss[1]++;
                		val = 450;
                case 16 : val = 1;
                case 17 : val = 5;
                case 18 : val = 7;
                case 19 : val = 60;
                case 20 : val = 8;
                case 21 : val = 60;
                case 22 : val = 80;
                case 23 : 
                		ss[1]++;
                		val = 350;
                case 24 : val = 9;
                case 25 : val = 60;
                case 26 : val = 80;
                case 27 : 
                		ss[1]++;
                		val = 350;
                case 28 : val = 90;
                case 29 : 
                		ss[1]++;
                		val = 350;
                case 30 : 
                		ss[1]++;
                		val = 450;
                case 31 : 
                		ss[3]++;
                		val = 200000;
                default : val = 0;
            }
        		
        		return val;
        }


        /**
         * Gets the value a pattern with six positions.
         * @param  a pattern to evaluate
         * @return  the pattern value
         */
        private int getValue61(final int pattern) {
            
        		int val = 0;
        		
        		switch (pattern){
                case 2 : val = 20;
                case 4 : val = 20;
                case 6 : val = 200;
                case 8 : val = 20;
                case 10 : val = 180;
                case 12 : val = 200;
                case 14 : 
                		ss[0]++;
                		val = 1000;
                case 16 : val = 20;
                case 18 : val = 160;
                case 20 : val = 180;
                case 22 : 
                		ss[0]++;
                		val = 900;
                case 24 : val = 200;
                case 26 : 
                		ss[0]++;
                		val = 900;
                case 28 : 
                		ss[0]++;
                		val = 1000;
                case 30 : 
                		ss[2]++;
                		val = 50000;
                default : val = 0;
            }
        		
        		return val;
        }

    
    
    } //inner class Eval
}
