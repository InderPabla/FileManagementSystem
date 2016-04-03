
public class LinkedBlock {
	int fileNumber ;
	Block block;
	LinkedBlock previous;
	LinkedBlock next;
	int index;
	
	public  LinkedBlock(){
		this.fileNumber = -1;
		this.index = -1;
		this.block = null;
		this.previous = null;
		this.next = null;
	}
	
	public  LinkedBlock(int fileNumber, Block block){
		this.fileNumber = fileNumber;
		this.index = -1;
		this.block = block;
		this.previous = null;
		this.next = null;
	}
}
