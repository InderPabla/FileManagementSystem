import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

public class SimulatedFile {
	
	String fileName = "";
	int numberOfBlocks = 0;
	Block blocks[];
	
	public SimulatedFile(String fileName){
		this.fileName = fileName;
		
	}
	
	public void makeRandomFile(int minBlocks, int maxBlocks){
		this.numberOfBlocks = randInt(minBlocks, maxBlocks);
		this.blocks = new Block[numberOfBlocks];
		
		for(int i = 0;i<numberOfBlocks;i++){
			blocks[i] = new Block(512);
			blocks[i].generateRandomBlockData();
		}
	}
	
	public void createFile(){
		File file = new File(fileName);
		try {
			file.createNewFile();
			
			PrintWriter printWriter= new PrintWriter(fileName);
			
			for(int i = 0;i<numberOfBlocks;i++){
				Block block = blocks[i];
				printWriter.write(block.blockData.toString());
			}
			//System.out.println(fileName+" "+numberOfBlocks);
			printWriter.close(); 
		} catch (IOException e) {
			e.printStackTrace();
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
