import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.Semaphore;
import java.util.Hashtable;
import java.util.Random;
import java.util.Enumeration;

/**
 * This class handles all file and disk related operation, such as
 * disk operations of read, write, delete and file operations of
 * list, create, rename and delete.
 * @author Inderpreet Pabla
 */
public class FileHandler{

    String fileListName = "fileList.txt"; //file contains, file names and number of blocks

    FileOperationMaker fileOperationMaker; //makes file operations of read, write and delete

    Hashtable<Integer, SimulatedFile> simFileHash = new Hashtable<Integer, SimulatedFile>(); //Integer corresponds to the file number, with a value of simulated file 

    LinkedBlock[] contiguousBlocks = new LinkedBlock[400]; //400 blocks of LinkedBlocks, which will act like contiguous blocks
    
    
    LinkedBlock[] linkedBlocks = new LinkedBlock[400]; //400 blocks of LinkedBlocks, which will act like a linked blocks
    ArrayList<Integer> linkedNotUsedIndices = new ArrayList(); //List of unused indices
    
    Semaphore writing; //write sem
    Semaphore reading; //read sem
    Semaphore deleting; //delete sem
    Semaphore printing; //print sem
    Semaphore finished; //finished sem
    
    
    /**
	 * Class constructor retrieves data from fileList.txt if it exists
	 * else it creates a new fileList.txt.
	 */
    public FileHandler(){
        File file = new File(fileListName); //file object for fileList.txt
        
		if(file.exists()){
			retrieveData(); //if file exists, get data
		}
		else{
            makeListFile();//if file does not exist, make a new list file
		}
    }

    /**
	 * Method deletes the old fileList.txt and creates a new one.
	 */
    public void makeListFile(){
        File file = new File(fileListName); //file object for fileList.txt
        file.delete(); //delete file
        try {
            file.createNewFile(); //create new file
        } catch (IOException e) {
            e.printStackTrace(); //if IO exception print error
        }
    }

    /**
	 * Method retrieves data of file and block numbers from fileList.txt
	 */
    public void retrieveData(){
        try{
            Scanner scanner = new Scanner(new FileReader(fileListName)); //scanner to read fileList.txt

            //while scanner has more values
            while(scanner.hasNext()){
                int fileNumber = scanner.nextInt(); //get next integer as file number
                int fileBlocks = scanner.nextInt(); //get another intger as file blocks

                SimulatedFile simFile = new SimulatedFile(fileNumber, fileBlocks); //create simulated file with file number and number of blocks
                simFileHash.put(fileNumber,simFile); //create new hash, with file number as key, and simulated file as the value
            }
            
            scanner.close(); //close scanner
        }
        catch (IOException e) {
            e.printStackTrace(); //if IO exception print error
        }
    }

    /**
	 * Method creates a file with given file number and number of blocks.
	 * Method also checks if file already exists before inserting the file into the
	 * file hash.
	 * @param fileNumber unique number of the file
	 * @param blockNumber number of blocks allocated to this file
	 */
    public void createFile(int fileNumber, int blockNumber){
        SimulatedFile simFile = new SimulatedFile(fileNumber, blockNumber); //simulated file with file number and block number

        boolean exists = simFileHash.containsKey(fileNumber); //check if file already exists
        
        //if file exists
        if(exists){
            System.out.println("File Already Exists."); //print file already exists
        }
        //if file does not exist
        else{
            simFileHash.put(fileNumber,simFile); //insert file into hash with given file number as key and simulated file as value
            try {
                FileWriter fileWriter = new FileWriter(fileListName,true); //file writer to append to fileList.txt
                fileWriter.write(simFile.fileNumber+" "+simFile.fileBlocks+"\n"); //append file number and number of blocks
                fileWriter.close(); //close file writer
                
                //create new file with the given file number and load file with block data
                File newFile = new File(fileNumber+"");
                newFile.createNewFile();
                loadFile(fileNumber, simFile);
                System.out.println("File Created Scuessfully.");
            }
            catch (IOException e) {
                e.printStackTrace();  //if IO exception print error
            }
        }
    }
    
    /**
	 * Method lists all simulated file data currently stored in the hash.
	 */
    public void listFiles(){
        boolean found = false; //if a file was found
        Enumeration keys = simFileHash.keys(); //get all keys in the hash
        
        //enumerate through all keys in the hash
        while ( keys.hasMoreElements() ){
           Integer key = (Integer)keys.nextElement(); //get key, which is the file number
           SimulatedFile value = (SimulatedFile)simFileHash.get(key); //get value with the given key, which is the simulated file
           System.out.println("Unique File Number: "+key + ", Blocks: " + value.fileBlocks); //print file number and file blocks
           found = true; //a file was found
        }

        if(!found){
            System.out.println("No Files."); //if no file was found, then the hash is empty
        }
    }

    /**
	 * Method deletes a given file number (key), if it exists in the hash.
	 */
    public void deleteFile(int fileNumber){
        boolean exists = simFileHash.containsKey(fileNumber); //if key exists

        //if exists
        if(exists){
            simFileHash.remove(fileNumber); //remove key and value
            remakeFileList(); //re-make fileList.txt given the updated hash 

            //delete file with the same file number
            File newFile = new File(fileNumber+"");
            newFile.delete();

            System.out.println("File Deleted Successfully."); //success
        }
        //if does not exist
        else{
            System.out.println("File Does Not Exist.");
        }
    }

    /**
	 * Method renames a file given, the old and new file number.
	 * @param oldFileNumber this is current file number 
	 * @param newFileNumber this is the file number to be changed to
	 */
    public void renameFile(int oldFileNumber, int newFileNumber){
        boolean exists = simFileHash.containsKey(oldFileNumber); //if current file exists
        
        //if does not exist
        if(!exists){
            System.out.println("File to be re-named does not exist."); //file does not exist
        }
        //if file does exist
        else{
            exists = simFileHash.containsKey(newFileNumber); //check if the new file exists

            //if eixsts 
            if(exists){
                System.out.println("New file already exists."); //new file already exists
            }
            //current file eixsts and new file does not exist
            else{
                SimulatedFile file = (SimulatedFile)simFileHash.get(oldFileNumber); //get simulated file value from the hash of current file number
                file.fileNumber = newFileNumber; //change current file number to new file number
                simFileHash.remove(oldFileNumber); //remove hash of current file number
                simFileHash.put(newFileNumber,file); //replace it with new file number
                
                remakeFileList(); //re-make fileList.txt given the updated hash

                //delete current file
                File oldFile = new File(oldFileNumber+"");
                oldFile.delete();

                //create new file
                try{
                    File newFile = new File(newFileNumber+"");
                    newFile.createNewFile(); 
                    loadFile(newFileNumber, file); //load new file with simulated file data
                }
                catch (IOException e) {
                    e.printStackTrace(); //if IO exception print error
                }
                System.out.println("File re-named successfully."); //success
            }
        }
    }

    /**
	 * Method remakes fileList.txt with the updated hash
	 */
    public void remakeFileList(){
        makeListFile(); //delete and remake fileList.txt
        
        try {
            Enumeration keys = simFileHash.keys(); //get all keys in the hash
            FileWriter fileWriter = new FileWriter(fileListName,true); //append to fileList.txt
            
          //enumerate through all keys in the hash
            while ( keys.hasMoreElements() ){
               Integer key = (Integer)keys.nextElement(); //get key which is the file number
               SimulatedFile value = (SimulatedFile)simFileHash.get(key); //get simulated file with given key
               fileWriter.write(value.fileNumber+" "+value.fileBlocks+"\n"); //append to file, file number and blocks 
            }
            fileWriter.close(); //close file writer
        }
        catch (IOException e) {
            e.printStackTrace(); //if IO exception print error
        }
    }

    /**
	 * Method loads files with random data.
	 * @param fileNumber the file to loaded with data
	 * @param simFile simulated file contains blocks, with random data to be copied onto the given file number 
	 */
    public void loadFile(int fileNumber, SimulatedFile simFile){
        try{
            FileWriter fileWriter = new FileWriter(fileNumber+"",true);  //append to fileNumber.txt
            for(int i = 0; i < simFile.blocks.length;i++){
                fileWriter.write(simFile.blocks[i].blockData.toString()); //append all block data in the simulated file
            }
            fileWriter.close(); //close file writer
        }
        catch (IOException e) {
            e.printStackTrace(); //if IO exception print error
        }
    }
    
    /**
   	 * Method does random disk operation on two types of disk storage, 
   	 * linked and contiguous. Operation can be read, write or delete and can 
   	 * be done on any given file in the hash.
   	 */
    public void diskOperation(){
    	//create random operations to be done
    	fileOperationMaker = new FileOperationMaker(); 
        fileOperationMaker.makeRandomOperations(simFileHash.size());
    	
        //empty contiguous blocks
    	for(int i = 0;i<contiguousBlocks.length;i++){
        	contiguousBlocks[i] = new LinkedBlock();
        }
    	
    	 //empty linked blocks
    	for(int i = 0;i<linkedBlocks.length;i++){
    		linkedBlocks[i] = new LinkedBlock();
        }
    	
    	 //empty unused indices and re-add integers from 0 to 399
    	linkedNotUsedIndices = new ArrayList();
    	for(int i = 0;i<400;i++){
    		linkedNotUsedIndices.add(i);
        }
    	
    	//semaphores to handle mutual exclusion
    	finished = new Semaphore(-((simFileHash.size()*3)-1)); //-(hash size * 3) operations need to be done before method is finished
    	reading = new Semaphore(1); //reading semaphore, unlocked
    	deleting = new Semaphore(1); //deleting semaphore, unlocked
    	writing = new Semaphore(1); //writing semaphore, unlocked
    	printing = new Semaphore(1); //printing semaphore, unlocked
    	 
    	//while there is another operation left to do
    	while(fileOperationMaker.hasNext()){
    		OperationData operationData = fileOperationMaker.next(); //get next operation
    		
    		/**
    	   	 * Class runs a thread which updates linked and contiguous blocks given read, write or delete operation
    	   	 */
    		class OperationTask implements Runnable {
    	        OperationData data; //operation data
    	        
    	        OperationTask(OperationData data) { 
    	        	this.data = data; //Initialize data
    	        }
    	        
    	        /**
        	   	 * Method runs the thread operation
        	   	 */
    	        public void run() {
    	        	//Switch statement on 3 possible operations
    	        	switch(data.operation){
	        			case "R": //read operation
	        				
	        				//Acquire read and delete exclusion 
	        				readAcquire();
	        				deleteAcquire();
	        				
	        				readContiguous(data.fileIndex); //read data from contiguous blocks at given index
	        				readLinked(data.fileIndex); //read data from linked blocks at given index
	        				
	        				//release read and delete exclusion
	        				readRelease();
	        				deleteRelease();
	        				break; //break out of switch
	        				
	        			case "W": //write operation
	        				
	        				//Acquire write and delete exclusion 
		        			writeAcquire();
		        			deleteAcquire();
		        			
		        			writeContiguous(data.fileIndex);  //write data to contiguous blocks at given index
	        				writeLinked(data.fileIndex); //write data to linked blocks at given index
	        				
	        				//release write and delete exclusion 
	        				writeRelease();
	        				deleteRelease();
	        				break; //break out of switch
	        				
	        			case "D": //delete operation
	        				
	        				//Acquire read, write and delete exclusion 
	        				writeAcquire();
		        			readAcquire();
		        			deleteAcquire();
		        			
		        			deleteContiguous(data.fileIndex); //delete data from contiguous blocks at given index
	        				deleteLinked(data.fileIndex); //delete data from linked blocks at given index
	        				
	        				//release read, write and delete
	        				writeRelease();
	        				readRelease();
	        				deleteRelease();
	        				break; //break out of switch
    	        	}
    	        	
    	        	finishRelease(); //release finished
    	        }
    	    }
    		
    	    Thread operationThread = new Thread(new OperationTask(operationData)); //create thread with OperationTask class and operation data
    	    operationThread.start(); //start thread
    	}
    	  	
    	finishAcquire(); //acquire finish (if able to acquire, it means all operations are done)
    	printFreeBlocks(); //print free blocks
    	finishRelease(); //release finish 
    	
    	fileOperationMaker.closeAll(); // close all scanners in file operation
 	  	
    }

    /**
   	 * Method prints all unused blocks in contiguous and linked.
   	 */
    public void printFreeBlocks(){
    	int contiguousCounter = 0;
    	int linkedCounter = 0;
    	
    	System.out.println("-----------------------------------------------------------------------------");
    	
    	//print free contiguous blocks. 
    	System.out.print("Free Contiguous Blocks: ");
    	for(int i = 0; i<contiguousBlocks.length; i++){
    		// if file number is -1, it means it's not in use.
    		if(contiguousBlocks[i].fileNumber == -1){
    			System.out.print(i+" ");
    			contiguousCounter++;
    		}
    	}
    	
    	System.out.println();
    	
    	//print free linked blocks. 
    	System.out.print("Free Linked Blocks: ");
    	for(int i = 0; i<linkedBlocks.length; i++){
    		// if file number is -1, it means it's not in use.
    		if(linkedBlocks[i].fileNumber == -1){
    			System.out.print(i+" ");
    			linkedCounter++;
    		}
    	}
    	
    	//print information
    	System.out.println();
    	System.out.println("Total Free Contiguous Blocks: "+contiguousCounter);
    	System.out.println("Total Free Linked Blocks: "+linkedCounter);
    	System.out.println("-----------------------------------------------------------------------------");
    }
    
    public void writeContiguous(int fileIndex){
    	SimulatedFile file = getHashFileFromIndex(fileIndex);
    	int location = availableContiguousLocation(file.fileBlocks);
    	int existLocation = contiguousFileExists(file.fileNumber);
    	
    	if(existLocation != -1){	
    		String blocks = "";
			for(int i = existLocation; i< existLocation+file.fileBlocks;i++){
				blocks+=i+" ";
        	}
			
			//print information
			printAcquire();
    		System.out.printf("%-12s%-12s%-12s%-12s\n","Requested File","Operation","Status","Blocks(Contiguous)");
    		System.out.printf("%-12s%-14s%-12s%-12s\n",file.fileNumber,"W","Exists",blocks);
    		printRelease();
    	}
    	else if(location == -1){ 
    		//print information
    		printAcquire();
    		System.out.printf("%-12s%-12s%-12s\n","Requested File","Operation","Status");
    		System.out.printf("%-12s%-14s%-12s\n",file.fileNumber,"W","No Space");
    		printRelease();
    	}
    	else{
    		for(int i = location; i< location+file.fileBlocks;i++){
        		contiguousBlocks[i].block = file.blocks[i-location];
        		contiguousBlocks[i].fileNumber = file.fileNumber;
        		contiguousBlocks[i].index = location;
        	}
    		
    		String blocks = "";
			for(int i = location; i< location+file.fileBlocks;i++){
				blocks+=i+" ";
        	}
			
			//print information
			printAcquire();
    		System.out.printf("%-12s%-12s%-12s%-12s\n","Requested File","Operation","Status","Blocks(Contiguous)");
    		System.out.printf("%-12s%-14s%-12s%-12s\n",file.fileNumber,"W","Sucess",blocks);
    		printRelease();
    		
    	}
    	
    	
    }
   
    public void readContiguous(int fileIndex){
    	SimulatedFile file = getHashFileFromIndex(fileIndex);
    	int existLocation = contiguousFileExists(file.fileNumber);
    	if(existLocation != -1){			
    		String blocks = "";
			for(int i = existLocation; i< existLocation+file.fileBlocks;i++){
				blocks+=i+" ";
        	}
			
			//print information
			printAcquire();
    		System.out.printf("%-12s%-12s%-12s%-12s\n","Requested File","Operation","Status","Blocks(Contiguous)");
    		System.out.printf("%-12s%-14s%-12s%-12s\n",file.fileNumber,"R","Sucess",blocks);
    		printRelease();
    	}
    	else{
    		//print information
    		printAcquire();
    		System.out.printf("%-12s%-12s%-12s\n","Requested File","Operation","Status");
    		System.out.printf("%-12s%-14s%-12s\n",file.fileNumber,"R","Not Found");
    		printRelease();
    	}
    }
    
    public void deleteContiguous(int fileIndex){
    	SimulatedFile file = getHashFileFromIndex(fileIndex);
    	int existLocation = contiguousFileExists(file.fileNumber);
    	
    	if(existLocation == -1){
    		//print information
    		printAcquire();
    		System.out.printf("%-12s%-12s%-12s\n","Requested File","Operation","Status");
    		System.out.printf("%-12s%-14s%-12s\n",file.fileNumber,"D","Not Found");
    		printRelease();
    	}
    	else {
    		for(int i = existLocation; i< existLocation+file.fileBlocks;i++){
        		contiguousBlocks[i]= new LinkedBlock();
        	}
    		
    		//print information
    		printAcquire();
    		System.out.printf("%-12s%-12s%-12s\n","Requested File","Operation","Status");
    		System.out.printf("%-12s%-14s%-12s\n",file.fileNumber,"D","Sucess");
    		printRelease();
    	}
    	
    	
    }
    
    public void writeLinked(int fileIndex){
    	SimulatedFile file = getHashFileFromIndex(fileIndex);
    	int location = availableLinkedLocation(file.fileBlocks);
    	int existLocation = linkedFileExists(file.fileNumber);
    	
    	if(existLocation != -1){	
    		boolean done = false;
    		String blocks = "";
    		LinkedBlock current = linkedBlocks[existLocation];
    		while(!done){
				blocks+=current.index+" ";	
				
				if(current.next!=null)
					current = current.next;
				else
					done = true;
    		}
    		
    		//print information
    		printAcquire();
    		System.out.printf("%-12s%-12s%-12s%-12s\n","Requested File","Operation","Status","Blocks(Linked)");
    		System.out.printf("%-12s%-14s%-12s%-12s\n",file.fileNumber,"W","Exsits",blocks);
    		printRelease();
    	}
    	else if(location == -1){   
    		//print information
    		printAcquire();
    		System.out.printf("%-12s%-12s%-12s\n","Requested File","Operation","Status");
    		System.out.printf("%-12s%-14s%-12s\n",file.fileNumber,"W","No Space");
    		printRelease();
    	}
    	else{
    		int previousLinkIndex = -1;
    		for(int i = 0;i<file.blocks.length;i++){
    			int randomNotUsedIndex = randInt(0,linkedNotUsedIndices.size()-1);
    			int randomLinkIndex = linkedNotUsedIndices.get(randomNotUsedIndex);
    			linkedNotUsedIndices.remove(randomNotUsedIndex);
    			  			
    			linkedBlocks[randomLinkIndex] = new LinkedBlock(file.fileNumber,file.blocks[i]);
    			linkedBlocks[randomLinkIndex].index = randomLinkIndex;
    			if(previousLinkIndex != -1){
    				linkedBlocks[randomLinkIndex].previous = linkedBlocks[previousLinkIndex];
    				linkedBlocks[previousLinkIndex].next = linkedBlocks[randomLinkIndex];
    			}
    			previousLinkIndex = randomLinkIndex;	
    			
    			if(i==0)
    				location = randomLinkIndex;
    		}
    		
    		boolean done = false;
    		String blocks = "";
    		LinkedBlock current = linkedBlocks[location];
    		while(!done){
				blocks+=current.index+" ";	
				
				if(current.next!=null)
					current = current.next;
				else
					done = true;
    		}
    		
    		//print information
    		printAcquire();
    		System.out.printf("%-12s%-12s%-12s%-12s\n","Requested File","Operation","Status","Blocks(Linked)");
    		System.out.printf("%-12s%-14s%-12s%-12s\n",file.fileNumber,"W","Sucess",blocks);
    		printRelease();
    	}
    }
       
    public void readLinked(int fileIndex){
    	SimulatedFile file = getHashFileFromIndex(fileIndex);
    	int existLocation = linkedFileExists(file.fileNumber);
    	if(existLocation != -1){
    		boolean done = false;
    		String blocks = "";
    		LinkedBlock current = linkedBlocks[existLocation];
    		while(!done){
				blocks+=current.index+" ";	
				
				if(current.next!=null)
					current = current.next;
				else
					done = true;
    		}
    		
    		//print information
    		printAcquire();
    		System.out.printf("%-12s%-12s%-12s%-12s\n","Requested File","Operation","Status","Blocks(Linked)");
    		System.out.printf("%-12s%-14s%-12s%-12s\n",file.fileNumber,"R","Sucess",blocks);
    		printRelease();
    	}
    	else{
    		//print information
    		printAcquire();
    		System.out.printf("%-12s%-12s%-12s\n","Requested File","Operation","Status");
    		System.out.printf("%-12s%-14s%-12s\n",file.fileNumber,"R","Not Found");
    		printRelease();
    	}
    }

	public void deleteLinked(int fileIndex){
		SimulatedFile file = getHashFileFromIndex(fileIndex);
		int existLocation = linkedFileExists(file.fileNumber);
    	
    	if(existLocation == -1){
    		printAcquire();
    		System.out.printf("%-12s%-12s%-12s\n","Requested File","Operation","Status");
    		System.out.printf("%-12s%-14s%-12s\n",file.fileNumber,"D","Not Found");
    		printRelease();
    	}
    	else {
    		
    		boolean done = false;
    		int index = existLocation;
    		while(!done){
    			linkedNotUsedIndices.add(linkedBlocks[index].index);
    			if(linkedBlocks[index].next==null){
    				linkedBlocks[index] = new LinkedBlock();
    				done = true;
    			}
    			else{
    				int tempIndex = index;
    				index = linkedBlocks[index].next.index;
    				linkedBlocks[tempIndex] = new LinkedBlock();
    			}
    			
    		}
    		
    		//print information
    		printAcquire();
    		System.out.printf("%-12s%-12s%-12s\n","Requested File","Operation","Status");
    		System.out.printf("%-12s%-14s%-12s\n",file.fileNumber,"D","Sucess");
    		printRelease();
    	}
	}
	
	/**
   	 * Acquires printing semaphore
   	 */
	public void printAcquire(){
		try {
			printing.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
   	 * Releases printing semaphore
   	 */
	public void printRelease(){
		printing.release();
	}
	
	/**
   	 * Acquires writing semaphore
   	 */
	public void writeAcquire(){
		try {
			writing.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
   	 * Releases writing semaphore
   	 */
	public void writeRelease(){
		writing.release();
	}
	
	/**
   	 * Acquires reading semaphore
   	 */
	public void readAcquire(){
		try {
			reading.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
   	 * Releases reading semaphore
   	 */
	public void readRelease(){
		reading.release();
	}
	
	/**
   	 * Acquires deleting semaphore
   	 */
	public void deleteAcquire(){
		try {
			deleting.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
   	 * Releases deleting semaphore
   	 */
	public void deleteRelease(){
		deleting.release();
	}
	
	/**
   	 * Acquires finished semaphore
   	 */
	public void finishAcquire(){
		try {
			finished.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
   	 * Releases finished semaphore
   	 */
	public void finishRelease(){
		finished.release();
	}
	
	/**
   	 * Checks  to see if the file number exists in contiguous blocks
   	 * @param fileNumber file number to check
   	 * @return -1 if does not exist, else the location where first index of the block is located. 
   	 */
    public int contiguousFileExists(int fileNumber){
    	for(int i = 0; i<contiguousBlocks.length;i++){
    		if(contiguousBlocks[i].fileNumber == fileNumber)
    			return i; //file number eixsts, return location
    	}
    	return -1; //does not exist
    }
    
    /**
   	 * Checks  to see if number of blocks can fit in the contiguous blocks
   	 * @param numberOfBlocks number of blocks of space
   	 * @return -1 if cannot fit, else the location where it can fit
   	 */
    public int availableContiguousLocation(int numberOfBlocks){
    	int location = -1;
    	int blocksFree = 0;
    	
    	//run through contiguous blocks and check number of free slots. If contiguous free slots available then we can fit the number of blocks
    	for(int i = 0; i<contiguousBlocks.length;i++){
    		if(contiguousBlocks[i].fileNumber == -1 && blocksFree == 0){
    			blocksFree++;
    			location = i;
    		}
    		else if(contiguousBlocks[i].fileNumber == -1){
    			blocksFree++;
    		}
    		else{
    			location = -1;
    			blocksFree = 0;
    		}
    		
    		if(numberOfBlocks == blocksFree){
    			return location; //return the starting location of fit
        	}
    		
    	}
    
    	return -1;
    }
    
    /**
   	 * Checks  to see if number of blocks can fit in the linked blocks
   	 * @param numberOfBlocks number of blocks of space
   	 * @return 1 if space is available, -1 if not available
   	 */
    public int availableLinkedLocation(int numberOfBlocks){
    	//if number of empty indices greater than or equal to number of blocks required
    	if(linkedNotUsedIndices.size()>=numberOfBlocks){
    		return 1; //can fit
    	}
    	return -1; //cannot fit
    }
    
    /**
   	 * Checks to see if file number exists within the linked blocks.
   	 * @param fileNumber file number to check 
   	 * @return the location of the starting link for this file number
   	 */
    public int linkedFileExists(int fileNumber){
    	for(int i = 0; i<linkedBlocks.length;i++){
    		//if file number matches and linked block has no previous link 
    		if(linkedBlocks[i].fileNumber == fileNumber && linkedBlocks[i].previous == null){
    			return i; //this is the starting index 
    		}
    	}
    	return -1; //cannot find the file number
    }
    
    /**
   	 * Gets simulated file from hash given index. Method runs through all
   	 * keys to the point of index, and returns the file at that key.
   	 * @param fileIndex get the simulated file at this index in the hash 
   	 * @return the found simulated file
   	 */
    public SimulatedFile getHashFileFromIndex(int fileIndex){
    	int indexCounter = 0;
    	
    	Enumeration keys = simFileHash.keys(); //get all keys
    	
    	//run through keys
    	while ( keys.hasMoreElements() ){
    		Integer key = (Integer)keys.nextElement(); //get key
    		SimulatedFile value = (SimulatedFile)simFileHash.get(key); //get value with the key
    		
    		//if index counter matches given index, return value 
    		if(indexCounter == fileIndex){
    			return value; //return value
    		}
    		
    		indexCounter++; //increment index
    	}
    	
    	return null; //if not found, then return null
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
