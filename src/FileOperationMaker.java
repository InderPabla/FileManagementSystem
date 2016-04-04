import java.io.File;
import java.io.FileReader;
import java.util.Scanner;
import java.util.Random;
import java.io.IOException;
import java.io.FileWriter;

/**
 * This class creates disk operations to be done on random file indices with random operation types
 * @author Inderpreet Pabla
 */
public class FileOperationMaker{

    String operationFile = "fileoperations.txt";
    String[] operations = {"R","W","D"};
    Scanner scannerLine;
    Scanner scannerData;
    
    public FileOperationMaker(){
    	makeOperationFile();
    }

    public void makeRandomOperations(int numberOfFiles){
    	
        File file = new File(operationFile);
        
        try{
            FileWriter fileWriter = new FileWriter(operationFile,true);
            for(int i = 0; i<numberOfFiles*3;i++){
                int fileEffected = randInt(0,numberOfFiles-1);
                int operationIndex = randInt(0,operations.length - 1);
                fileWriter.write(fileEffected+" "+operations[operationIndex]+"\n");
            }
            fileWriter.close();
            scannerLine = new Scanner(new FileReader(operationFile));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void makeOperationFile() {
        File file = new File(operationFile);
        file.delete();
       
        file = new File(operationFile);
        try {
            file.createNewFile();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public boolean hasNext(){
    	if(scannerLine.hasNext()){
    		return true;
    	}
    	else{
    		return false;
    	}
    }
    
    public OperationData next(){
    	OperationData operationData = null;
    	if(scannerLine.hasNext()){
    		scannerData = new Scanner(scannerLine.nextLine());
    		operationData = new OperationData(scannerData.nextInt(),scannerData.next());
    	}
    	return operationData;
    }
    
    public void closeAll(){
    	scannerLine.close();
    	scannerData.close();
    	scannerLine = null;
    	scannerData = null;
    }
    
    /**
	 * Returns a psuedo-random number between min and max, inclusive.
	 * The difference between min and max can be at most
	 * @param min Minimim value
	 * @param max Maximim value.  Must be greater than min.
	 * @return Integer between min and max, inclusive.
	 */
    public static int randInt(int min, int max) {
	    Random rand = new Random();
	    int randomNum = rand.nextInt((max - min) + 1) + min;
	    return randomNum;
	}

}
