/* Copyright (c) 2007-2015 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package minesweeper;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

/**
 * Board represents a minesweeper board such that each entry in the minesweeper is either empty,
 * has a bomb, has a flag. Board is a mutable data type.
 */
public class Board {
    //Representation aka fields
    private final List<List<Tile>> board;

 
    //Abstraction function:
    // Maps a multi-dimensional list (board) of type tile into a 2 dimensional minesweeper board.
    //Representation invariant:
    // board must have the size of a row and a column the same throughout the list.
    //Representation Exposure:
    // The nested list board has a final reference and made up of immutable type Tile. Mutators create
    // new instances of Tile everytime they mutate board. Observers only return primitive types.
    //Thread Safety
    // All mutators and observors are synchronized on the object board. Hence, no concurrency on the rep.
    // The rep relies on an immutable type Tile.
    // Further since all the synchronized blocks rely on one key there doesn't exist a cycle of dependencies.
    // There are no static fields nor static methods that utilize the rep and hence no monitor pattern applied
    // to static methods.
    private void checkRep()
    {
        int xDim=0;
        for(int y=0;y<board.size();y++){
            if(y!=0)
            {
                assert xDim==board.get(y).size();
            }
            else{
                xDim=board.get(y).size();
            }
        }
        
    }
    /**
     * Constructor with no existing file.
     * @param x represents the horizontal size of the minesweeper board.
     * @param y represents the vertical size of the minesweeper board.
     * Notice that the coordinate system has the origin established at the upper-left corner.
     *    (0,0)- - - -> (x-direction)
     *      |
     *      |
     *      |
     *      ^
     *    (y-direction)
     *    
     * Creates a board that has "randomly" generated with 25% probability of a tile having a bomb.
     */
    public Board(int x,int y)
    {
        boolean notDug=false;
        boolean notFlagged=false;
        boolean notTouched=false;
        boolean hasBomb=true;
        boolean noBomb=false;
        double probability=0.25;
        List<List<Tile>> randomBoard= new ArrayList<List<Tile>>();
        List<Tile> partialBoard= new ArrayList<>();
        for (int b=0;b<y;b++) //rows
        {
            if (b>0)
            {
                randomBoard.add(partialBoard);
                partialBoard=new ArrayList<>();
            }
            for (int a=0;a<x;a++) //columns
            {
                if (Math.random()<=probability) //25% chance of producing a bomb for an element
                {
                    Tile tile= new Tile(notDug,notFlagged,notTouched,hasBomb); // not dug, not flagged, not touched, has a bomb
                    partialBoard.add(tile);
                }
                else{
                    Tile tileNoBomb= new Tile(notDug,notFlagged,notTouched,noBomb);//not dug, not flagged,not touched,no bomb
                    partialBoard.add(tileNoBomb);
                }
            }  
            
        }
        randomBoard.add(partialBoard);
        this.board=Collections.synchronizedList(randomBoard);
        checkRep();
    }
    /**
     * Constructor with existing file.
     * @param fileName , fileName represents the text file associated with a previous minesweeper.
     * This constructor will create a board mirroring the contents of that file.
     * @throws FileNotFoundException 
     */
    public Board(File fileName) throws FileNotFoundException
    {
        boolean notFlagged=false;
        boolean notDug=false;
        boolean notTouched=false;
        boolean hasBomb=true;
        boolean noBomb=false;
        RuntimeException error= new RuntimeException(); //If the file is incorrectly formatted
        int xdim;
        int ydim;
        List<List<Tile>> board= new ArrayList<>();
        List<Integer> preBoard= new ArrayList<>();
        Scanner scanner= new Scanner(fileName);
        while (scanner.hasNextInt())
        {
            int entry=scanner.nextInt(); //throws an error if not matched to an integer.
            preBoard.add(entry);
        }
        xdim=preBoard.get(0);
        ydim=preBoard.get(1);
        preBoard.remove(0); //Get rid of dimensions of the minesweeper board.
        preBoard.remove(0);
        if ((preBoard.size())!=xdim*ydim) //Incorrect formatting if the board isn't the dimensions expected.
        {
            throw error;
        }
        List<Tile> row=new ArrayList<>();
        for (int x=0;x<preBoard.size();x++){
            if ((x)%xdim==0 && (x)/xdim>0){
                board.add(row);
                row=new ArrayList<>();
            }
            if( preBoard.get(x)==0){
                Tile tile= new Tile(notDug,notFlagged,notTouched,noBomb);
                row.add(tile);
            }
            if(preBoard.get(x)==1)
            {
                Tile tile= new Tile(notDug,notFlagged,notTouched,hasBomb);
                row.add(tile);
            }
            if (preBoard.get(x)!=0 && preBoard.get(x)!=1)
            {
                throw error;
            }
        }
        board.add(row);
        this.board=Collections.synchronizedList(board);

    }
    

    
    @Override
    public String toString(){
        synchronized(board){
            String result="";
            for (int y=0;y<this.board.size();y++)
            {
                for (int x=0;x<this.board.get(0).size();x++)
                {
                    if (!this.board.get(y).get(x).isTouched())
                    {
                        result=result+"- ";
                    }
                    if(this.board.get(y).get(x).isFlagged())
                    {
                        result=result+"F ";
                    }
                    if (this.board.get(y).get(x).isDug() && this.getNumber(x,y)==0)
                    {
                        result=result+"  ";
                    }
                    if(this.board.get(y).get(x).isDug() && this.getNumber(x,y)!=0){
                        result=result+this.getNumber(x,y)+" ";
                    }
                    //Getting rid of the extra space at the end.
                    if (x==this.board.get(0).size()-1)
                    {
                        result=result.substring(0, result.length()-1);
                    }
                }
              //Since println will be used in server (for consistency), no need for newline at the end.
                if (y!=this.board.size()-1)
                {
                    result=result+"\r\n";
                }
            }
            checkRep();
            return result; 
        }
        
    }
    /**
     * Digs a tile for a bomb. Changes the state of the tile to dug. Boolean will indicate if their was a bomb where dug.
     * @param x represents the x coordinate of the tile that will be dug.
     * @param y represents the y coordinate of the tile that will be dug.
     * @return boolean. Read below.
     * Effects: If x and y are outside of the range of this board, board does not change.
     * If x and y are legal coordinates then:
     * case (1): If the tile is touched, do nothing. Return false.
     * case (2): If the tile is untouched and has a bomb, then delete the bomb at current coordinate and 
     * return true. (Will become a dug state tile)
     * case (3): If the tile is untouched and has no bomb, then change the state of this tile to dug. Return false.
     * case (4): If the tile has adjacent untouched tiles that have no bombs, change
     * the state of this tile and its adjacent tiles to dug. Return false. (recursively).
     */
    public boolean dig(int x, int y){
        boolean notFlagged=false;
        boolean dug=true;
        boolean touched=true;
        boolean noBomb=false;
        synchronized(board){
            boolean wasThereBomb=false;
            if (y>=0 && y<=this.board.size()-1 && x>=0 && x<=this.board.get(0).size()-1 && !this.board.get(y).get(x).isTouched())
            {
                //Notice that this code can be condensed, but I found it useful for dig to return
                //boolean of the bomb state in to which it dug. This requires different if statements.
                if(this.getNumber(x,y)!=0 && this.board.get(y).get(x).hasBomb()) //case 2.
                {
                    Tile tile= new Tile(dug,notFlagged,touched,noBomb);
                    this.board.get(y).remove(x);
                    this.board.get(y).add(x,tile);
                    wasThereBomb=true;
                    checkRep();
                    return wasThereBomb;
                }
                if (this.getNumber(x,y)!=0 && !this.board.get(y).get(x).hasBomb())
                {
                    Tile tile= new Tile(dug,notFlagged,touched,noBomb);
                    this.board.get(y).remove(x);
                    this.board.get(y).add(x,tile);
                    wasThereBomb=false;
                    checkRep();
                    return wasThereBomb;
                }
                if (this.getNumber(x,y)==0 && !this.board.get(y).get(x).hasBomb()) //case 4
                {
                    this.helperDig(x, y); //recursive helper method
                    wasThereBomb=false;
                    checkRep();
                    return wasThereBomb;
                }
                // Notice that since bomb tiles are going to be cleared in the end, we might as well consider it as tile without bomb.
                if (this.getNumber(x,y)==0 && this.board.get(y).get(x).hasBomb()) //case 4
                {
                    Tile pseudoTile= new Tile(false,false,false,false); //pretend that it had no bomb and untouched. This allows recursive function to run.
                    this.board.get(y).remove(x);
                    this.board.get(y).add(x, pseudoTile);
                    
                    this.helperDig(x, y); //recurisve helper method
                    wasThereBomb=true;
                    checkRep();
                    return wasThereBomb;
                }
            }
            else{ //case 1
                checkRep();
                return false;
            }
            checkRep();
            return wasThereBomb;
        }
    }
    //Recursively digs when no neighbors have mines.
    private void helperDig(int x, int y){
            if (y>=0 && y<=this.board.size()-1 && x>=0 && x<=this.board.get(0).size()-1 && !this.board.get(y).get(x).isTouched())
            {
                if (!this.board.get(y).get(x).hasBomb())
                {
                    Tile tile= new Tile(true,false,true,false);
                    this.board.get(y).remove(x);
                    this.board.get(y).add(x,tile);
                }
                if (this.getNumber(x,y)==0)
                {
                    helperDig(x+1,y);
                    helperDig(x+1,y-1);
                    helperDig(x-1,y);
                    helperDig(x-1,y+1);
                    helperDig(x,y+1);
                    helperDig(x,y-1);
                    helperDig(x-1,y-1);
                    helperDig(x+1,y+1);
                }

            }
        
    }
    /**
     * Flags a current tile for Bomb.
     * @param x represents the x-coordinate of the tile that will be flagged.
     * @param y represents the y-coordinate of the tile that will be flagged.
     * Effects: If x and y coordinates are legal in an untouched state then flag this coordinate, otherwise do nothing.
     */
    public void flag(int x, int y){
        synchronized(board){
            if (y>=0 && y<=this.board.size()-1 && x>=0 && x<=this.board.get(0).size()-1 && !this.board.get(y).get(x).isTouched())
            {
                if (this.board.get(y).get(x).hasBomb()){
                    Tile tile= new Tile (false,true,true,true);
                    this.board.get(y).remove(x);
                    this.board.get(y).add(x,tile);
                }
                else{
                    Tile tile= new Tile(false,true,true,false);
                    this.board.get(y).remove(x);
                    this.board.get(y).add(x,tile); 
                }
                
            } 
        }
        
    }
    /**
     * Remove a flag from current tile.
     * @param x represents the x-coordinate of the tile that will be deflagged.
     * @param y represents the y-coordinate of the tile that will be deflagged.
     * Effects: If x and y are legal and the tile is a flagged state, turn the tile to untouched state.
     */
    public void deflag(int x, int y){
        synchronized(board){
            if (y>=0 && y<=this.board.size()-1 && x>=0 && x<=this.board.get(0).size()-1 && this.board.get(y).get(x).isFlagged())
            {
                if (this.board.get(y).get(x).hasBomb())
                {
                    Tile tile = new Tile(false,false,false,true);
                    this.board.get(y).remove(x);
                    this.board.get(y).add(x, tile);
                }
                else{
                    Tile tile= new Tile(false,false,false,false);
                    this.board.get(y).remove(x);
                    this.board.get(y).add(x,tile);
                }
            } 
        }
        
    }
    /**
     * Returns a number that indicates how many adjacent tiles have mines, tile must have been already dug.
     * @param x represents the x-coordinate of the tile. Must be valid x-coordinates.
     * @param y represents the y-coordinate of the tile. Must be valid y-coordinates.
     * @return an integer that represents the number of adjacent mines. The integer is between 0<=x<=8.
     */
    public int getNumber(int x, int y){
        synchronized(board){
            int count=0;
            if ((y+1)<=board.size()-1 && board.get(y+1).get(x).hasBomb())
            {
                count++;
            }
            if ((y+1)<=board.size()-1 && (x-1)>=0 && board.get(y+1).get(x-1).hasBomb())
            {
                count++;
            }
            
            if ((y-1)>=0 && board.get(y-1).get(x).hasBomb())
            {
                count++;
            }
            if ((x+1)<=board.get(0).size()-1 && board.get(y).get(x+1).hasBomb())
            {
                count++;
            }
            if ((x+1)<=board.get(0).size()-1 && (y-1)>=0 && board.get(y-1).get(x+1).hasBomb())
            {
                count++;
            }
            if ((x-1)>=0 && board.get(y).get(x-1).hasBomb())
            {
                count++;
            }
            if ((x-1)>=0 && (y-1)>=0 && board.get(y-1).get(x-1).hasBomb())
            {
                count++;
            }
            if ((x+1)<=board.get(0).size()-1 && (y+1)<=board.size()-1 && board.get(y+1).get(x+1).hasBomb())
            {
                count++;
            }
            checkRep();
            return count; 
        }
        
    }
    /** Checks if the tile is touched.
     * @param x represents the x-coordinate of the tile. Must be valid x-coordinates.
     * @param y represents the y-coordinate of the tile. Must be valid y-coordinates.
     * @return true if this tile is touched. false if the tile is untouched.
     * 
     */
    public boolean isTouched(int x,int y){
        synchronized(board){
            if (y>=0 && y<=this.board.size()-1 && x>=0 && x<=this.board.get(0).size()-1){
                checkRep();
                return (board.get(y).get(x).isTouched());
            }
            else{
                checkRep();
                return false;
            }  
        }
        
    }
    /** Checks if the tile is dug.
     * @param x represents the x-coordinate of the tile. Must be valid x-coordinates.
     * @param y represents the y-coordinate of the tile. Must be valid y-coordinates.
     * @return true if the tile is dug. False if the tile has not been dug.
     */
    public boolean isDug(int x,int y)
    {
        synchronized(board){
            if (y>=0 && y<=this.board.size()-1 && x>=0 && x<=this.board.get(0).size()-1)
            {
                checkRep();
                return this.board.get(y).get(x).isDug(); 
            }
            else{
                checkRep();
                return false;
            }  
        }
        
        
    }
    /** Checks if the tile has been flagged.
     * @param x represents the x-coordinate of the tile. Must be valid x-coordinates.
     * @param y represents the y-coordinate of the tile. Must be valid y-coordinates.
     * @return true if this tile has been flagged. False otherwise.
     */
    public boolean isFlagged(int x, int y)
    {
        synchronized(board){
            if (y>=0 && y<=this.board.size()-1 && x>=0 && x<=this.board.get(0).size()-1)
            {
                checkRep();
                return this.board.get(y).get(x).isFlagged();  
            }
            else{
                checkRep();
                return false;
            } 
        }
        
        
    }
    /**
     * Checks if the tile at x,y has a bomb
     * @param x represents the x-coordinate of the tile. Must be valid x-coordinates.
     * @param y represents the y-coordinate of the tile. Must be valid y-coordinates.
     * @return true if the tile has a bomb, false otherwise.
     */
    public boolean hasBomb(int x,int y)
    {
        synchronized(board){
            if (y>=0 && y<=this.board.size()-1 && x>=0 && x<=this.board.get(0).size()-1){
                checkRep();
                return this.board.get(y).get(x).hasBomb(); 
            }
            else{
                checkRep();
                return false;
            }  
        }
        
         
    }
    /**
     * Gets the y dimension of the minesweeper board.
     * @return an int that represents the y dimension.
     */
    public int getY()
    {
        synchronized (board)
        {
            return board.size();  
        }
        
    }
    /**
     * Gets the x dimension of the minesweeper board.
     * @return an int that represents the x Dimension.
     */
    public int getX(){
        synchronized (board)
        {
            return board.get(0).size();  
        }
        
    }

}