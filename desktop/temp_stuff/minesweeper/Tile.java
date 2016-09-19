package minesweeper;

//Represents an immutable tile in a minesweeper game.
public class Tile {
    private final boolean bomb;
    private final boolean dug;
    private final boolean flagged;
    private final boolean isTouched;
    
    //Abstraction function:
    // Represents possible state bomb,dug,flagged,istouched, to a state of a tile in a minesweeper game.
    //Representation Invariant:
    //  Dug state cannot be also a flagged state. Flagged state cannot be a dug state. If dug or flagged it has to be touched true.
    //  Cannot have a dug and bomb state.
    //Representation Exposure:
    // No mutator methods and representation fields are private final primitive objects.
    //ThreadSafety argument:
    // Datatype is immutable (no beneficient mutations, no mutators, no rep exposure) hence thread-safe.
    
    /**
     * Constructor that takes states of a tile.
     * @param isFlagged boolean
     * @param isTouched boolean
     * @param bomb boolean
     * @param isDug boolean
     */
    public Tile(boolean isDug,boolean isFlagged,boolean isTouched,boolean bomb)
    {
        this.dug=isDug;
        this.flagged=isFlagged;
        this.isTouched=isTouched;
        this.bomb=bomb;
    }
    private void checkRep(){
        if (dug)
        {
            assert isTouched;
            assert !flagged;
            assert !bomb;
        }
        if (flagged)
        {
            assert isTouched;
            assert !dug;
        }
        
        
    }
    /**
     * @return true if this tile is flagged. False otherwise.
     * 
     */
    public boolean isFlagged(){

        
        checkRep();
        return flagged;
        
    }
    
    /**
     * @return true if this tile is touched. False otherwise.
     */
    public boolean isTouched(){

        checkRep();
        return isTouched;
        
    }
    
    /**
     * @return true if this tile has been dug. False otherwise.
     */
    public boolean isDug(){
       
        checkRep();
        return dug;
    
    }
    /**
     * @return true is the tile has a bomb. False otherwise.
     */
    public boolean hasBomb(){
       
        checkRep();
        return bomb;
    
    }
    @Override
    public String toString(){

        String result="";
        if(!this.isTouched())
        {
            result="-";
        }
        if (this.isFlagged())
        {
            result="F";
        }
        if (this.hasBomb())
        {
            result="B";
        }
        checkRep();
        return result;
    
    }
}
