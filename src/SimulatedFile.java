/**
 * This class acts like a simulated file. With array of blocks storing random chunks of data 512 bytes in size.
 * @author Inderpreet Pabla
 */
public class SimulatedFile{

    int fileNumber;
    int fileBlocks;
	int defaultBlockSize = 512;
    Block[] blocks;
	
	/**
	 * Class constructor initilizes a Simulated file with given number of blocks
	 * @param fileNumber Unique number this file belongs to. 
	 * @param fielBlock Number of blocks that are contained within this file
	 */
    public SimulatedFile(int fileNumber, int fileBlocks){
        this.fileNumber = fileNumber;
        this.fileBlocks = fileBlocks;

        blocks = new Block[this.fileBlocks];

        for(int i = 0; i<blocks.length; i++){
            blocks[i] = new Block(defaultBlockSize);
            blocks[i].generateRandomBlockData();
        }   
    }
}
