/**
 * This class acts like a double linked list and a contiguous array. 
 * @author Inderpreet Pabla
 */
public class LinkedBlock {
	int fileNumber ; 
	Block block;
	LinkedBlock previous;
	LinkedBlock next;
	int index;
	
	/**
	 * Class constructor initilizes linked block with no existant file index
	 */
	public  LinkedBlock(){
		this.fileNumber = -1;
		this.index = -1;
		this.block = null;
		this.previous = null;
		this.next = null;
	}
	
	/**
	 * Class constructor initilizes linked block with file number and block data
	 * @param fileNumber Unique number this file belongs to. 
	 * @param block Block data to be saved as a reference
	 */
	public  LinkedBlock(int fileNumber, Block block){
		this.fileNumber = fileNumber;
		this.index = -1;
		this.block = block;
		this.previous = null;
		this.next = null;
	}
}
