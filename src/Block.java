import java.util.Random;

/**
 * This class creates string of size 512, with random characters between ASCII 36-126.
 * @author Inderpreet Pabla
 */
public class Block {
	int blockSize;
	StringBuilder blockData;
	
	/**
	* Class constructor initializes block size and a string builder with the given block size.
	* @param blockSize Size of the string builder
	*/
	public Block(int blockSize){
		this.blockSize = blockSize;
		
		blockData  = new StringBuilder();
		blockData.setLength(blockSize);
	}
	
	/**
	* Generate random block data to simulate saved data.
	*/
	public void generateRandomBlockData(){
		for(int i = 0; i<blockSize;i++){
			char randomChar = (char)randInt(32, 126);
			blockData.setCharAt(i,randomChar);

		}
	}

	/**
	 * Returns a psuedo-random number between min and max, inclusive.
	 * The difference between min and max can be at most
	 * @param min Minimim value
	 * @param max Maximim value.  Must be greater than min.
	 * @return Integer between min and max, inclusive.
	 */
	public int randInt(int min, int max) {
	    Random rand = new Random();
	    int randomNum = rand.nextInt((max - min) + 1) + min;
	    return randomNum;
	}
}