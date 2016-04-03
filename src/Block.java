import java.util.Random;

public class Block {
	int blockSize;
	StringBuilder blockData = new StringBuilder();
	public Block(int blockSize){
		this.blockSize = blockSize;
		this.blockData.setLength(blockSize);
	}

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
