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

public class FileHandler{

    String fileListName = "fileList.txt";

    FileOperationMaker fileOperationMaker;

    Hashtable<Integer, SimulatedFile> simFileHash = new Hashtable<Integer, SimulatedFile>();

    LinkedBlock[] contiguousBlocks = new LinkedBlock[400];
    
    
    LinkedBlock[] linkedBlocks = new LinkedBlock[400];
    ArrayList<Integer> linkedNotUsedIndicies = new ArrayList();
    
    Semaphore writing = new Semaphore(0);
    Semaphore reading = new Semaphore(0);
    Semaphore deleting = new Semaphore(0);
    
    public FileHandler(){
        File file = new File(fileListName);
		if(file.exists()){
			retrieveData();
		}
		else{
            makeListFile();
		}
    }

    public void makeListFile(){
        File file = new File(fileListName);
        file.delete();
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void retrieveData(){
        try{
            Scanner scanner = new Scanner(new FileReader(fileListName));

            while(scanner.hasNext()){
                int fileNumber = scanner.nextInt();
                int fileBlocks = scanner.nextInt();

                SimulatedFile simFile = new SimulatedFile(fileNumber, fileBlocks);
                simFileHash.put(fileNumber,simFile);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createFile(int fileNumber, int randomBlocks){
        SimulatedFile simFile = new SimulatedFile(fileNumber, randomBlocks);

        boolean exists = simFileHash.containsKey(fileNumber);

        if(exists){
            System.out.println("File Already Exists.");
        }
        else{
            simFileHash.put(fileNumber,simFile);
            try {
                FileWriter fileWriter = new FileWriter(fileListName,true);
                fileWriter.write(simFile.fileNumber+" "+simFile.fileBlocks+"\n");
                fileWriter.close();

                File newFile = new File(fileNumber+"");
                newFile.createNewFile();
                loadFile(fileNumber, simFile);
                System.out.println("File Created Scuessfully.");


            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void listFiles(){
        boolean found = false;
        Enumeration keys = simFileHash.keys();
        while ( keys.hasMoreElements() ){
           Integer key = (Integer)keys.nextElement();
           SimulatedFile value = (SimulatedFile)simFileHash.get(key);
           System.out.println(key + " " + value.fileBlocks);
           found = true;
        }

        if(!found)
            System.out.println("No Files.");
    }

    public void deleteFile(int fileNumber){
        boolean exists = simFileHash.containsKey(fileNumber);

        if(exists){
            simFileHash.remove(fileNumber);
            remakeFileList();

            File newFile = new File(fileNumber+"");
            newFile.delete();

            System.out.println("File Deleted Scuessfully.");
        }
        else{
            System.out.println("File Does Not Exist.");
        }
    }

    public void renameFile(int oldFileNumber, int newFileNumber){
        boolean exists = simFileHash.containsKey(oldFileNumber);
        if(!exists){
            System.out.println("File to be re-named does not exist.");
        }
        else{
            exists = simFileHash.containsKey(newFileNumber);

            if(exists){
                System.out.println("New file already exists.");
            }
            else{
                SimulatedFile file = (SimulatedFile)simFileHash.get(oldFileNumber);
                file.fileNumber = newFileNumber;
                simFileHash.remove(oldFileNumber);
                simFileHash.put(newFileNumber,file);
                remakeFileList();

                File oldFile = new File(oldFileNumber+"");
                oldFile.delete();

                try{
                    File newFile = new File(newFileNumber+"");
                    newFile.createNewFile();
                    loadFile(newFileNumber, file);
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("File re-named scuessfully.");
            }
        }
    }

    public void remakeFileList(){
        makeListFile();
        try {
            Enumeration keys = simFileHash.keys();
            FileWriter fileWriter = new FileWriter(fileListName,true);
            while ( keys.hasMoreElements() ){
               Integer key = (Integer)keys.nextElement();
               SimulatedFile value = (SimulatedFile)simFileHash.get(key);
               fileWriter.write(value.fileNumber+" "+value.fileBlocks+"\n");
            }
            fileWriter.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadFile(int fileNumber, SimulatedFile simFile){
        try{
            FileWriter fileWriter = new FileWriter(fileNumber+"",true);
            for(int i = 0; i < simFile.blocks.length;i++){
                fileWriter.write(simFile.blocks[i].blockData.toString());
            }
            fileWriter.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void diskOperation(){
    	/*contiguousBlocksLoad();
    	linkedBlocksLoad();*/
    	
    	fileOperationMaker = new FileOperationMaker();
    	System.out.println("addd"+simFileHash.size());
        fileOperationMaker.makeRandomOperations(simFileHash.size());
    	
    	for(int i = 0;i<contiguousBlocks.length;i++){
        	contiguousBlocks[i] = new LinkedBlock();
        }
    	
    	for(int i = 0;i<linkedBlocks.length;i++){
    		linkedBlocks[i] = new LinkedBlock();
        }
    	linkedNotUsedIndicies = new ArrayList();
    	for(int i = 0;i<400;i++){
    		linkedNotUsedIndicies.add(i);
        }
    	
    	while(fileOperationMaker.hasNext()){
    		OperationData operationData = fileOperationMaker.next();
    		switch(operationData.operation){
    			case "R":
    				readContiguous(operationData.fileIndex);
    				readLinked(operationData.fileIndex);
    				System.out.println();
    				break;
    				
    			case "W":writeContiguous(operationData.fileIndex);
    				writeLinked(operationData.fileIndex);
    				System.out.println();
    				break;
    				
    			case "D":deleteContiguous(operationData.fileIndex);
    				deleteLinked(operationData.fileIndex);
    				System.out.println();
    				break;
    		}
    	}
    	
    	printFreeBlocks();
    }
    
    public void printFreeBlocks(){
    	System.out.print("Free Contiguous Blocks: ");
    	for(int i = 0; i<contiguousBlocks.length; i++){
    		if(contiguousBlocks[i].fileNumber == -1){
    			System.out.print(i+" ");
    		}
    	}
    	
    	System.out.println();
    	
    	System.out.print("Free Linked Blocks: ");
    	for(int i = 0; i<linkedBlocks.length; i++){
    		if(linkedBlocks[i].fileNumber == -1){
    			System.out.print(i+" ");
    		}
    	}
    	
    	System.out.println();
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
    		System.out.printf("%-12s%-12s%-12s%-12s\n","Requested File","Operation","Status","Blocks(Contiguous)");
    		System.out.printf("%-12s%-14s%-12s%-12s\n",file.fileNumber,"W","Exists",blocks);
    	}
    	else if(location == -1){   		
    		System.out.printf("%-12s%-12s%-12s\n","Requested File","Operation","Status");
    		System.out.printf("%-12s%-14s%-12s\n",file.fileNumber,"W","No Space");
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
    		System.out.printf("%-12s%-12s%-12s%-12s\n","Requested File","Operation","Status","Blocks(Contiguous)");
    		System.out.printf("%-12s%-14s%-12s%-12s\n",file.fileNumber,"W","Sucess",blocks);
    		
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
    		System.out.printf("%-12s%-12s%-12s%-12s\n","Requested File","Operation","Status","Blocks(Contiguous)");
    		System.out.printf("%-12s%-14s%-12s%-12s\n",file.fileNumber,"R","Sucess",blocks);
    	}
    	else{
    		System.out.printf("%-12s%-12s%-12s\n","Requested File","Operation","Status");
    		System.out.printf("%-12s%-14s%-12s\n",file.fileNumber,"R","Not Found");
    	}
    }
    
    public void deleteContiguous(int fileIndex){
    	SimulatedFile file = getHashFileFromIndex(fileIndex);
    	int existLocation = contiguousFileExists(file.fileNumber);
    	
    	if(existLocation == -1){
    		System.out.printf("%-12s%-12s%-12s\n","Requested File","Operation","Status");
    		System.out.printf("%-12s%-14s%-12s\n",file.fileNumber,"D","Not Found");
    	}
    	else {
    		for(int i = existLocation; i< existLocation+file.fileBlocks;i++){
        		contiguousBlocks[i]= new LinkedBlock();
        	}
    		System.out.printf("%-12s%-12s%-12s\n","Requested File","Operation","Status");
    		System.out.printf("%-12s%-14s%-12s\n",file.fileNumber,"D","Sucess");
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
    		
    		System.out.printf("%-12s%-12s%-12s%-12s\n","Requested File","Operation","Status","Blocks(Linked)");
    		System.out.printf("%-12s%-14s%-12s%-12s\n",file.fileNumber,"W","Exsits",blocks);
    	}
    	else if(location == -1){   		
    		System.out.printf("%-12s%-12s%-12s\n","Requested File","Operation","Status");
    		System.out.printf("%-12s%-14s%-12s\n",file.fileNumber,"W","No Space");
    	}
    	else{
    		int previousLinkIndex = -1;
    		for(int i = 0;i<file.blocks.length;i++){
    			int randomNotUsedIndex = randInt(0,linkedNotUsedIndicies.size()-1);
    			int randomLinkIndex = linkedNotUsedIndicies.get(randomNotUsedIndex);
    			linkedNotUsedIndicies.remove(randomNotUsedIndex);
    			  			
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
    		
    		System.out.printf("%-12s%-12s%-12s%-12s\n","Requested File","Operation","Status","Blocks(Linked)");
    		System.out.printf("%-12s%-14s%-12s%-12s\n",file.fileNumber,"W","Sucess",blocks);
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
    		
    		System.out.printf("%-12s%-12s%-12s%-12s\n","Requested File","Operation","Status","Blocks(Linked)");
    		System.out.printf("%-12s%-14s%-12s%-12s\n",file.fileNumber,"R","Sucess",blocks);
    	}
    	else{
    		System.out.printf("%-12s%-12s%-12s\n","Requested File","Operation","Status");
    		System.out.printf("%-12s%-14s%-12s\n",file.fileNumber,"R","Not Found");
    	}
    }

	public void deleteLinked(int fileIndex){
		SimulatedFile file = getHashFileFromIndex(fileIndex);
		int existLocation = linkedFileExists(file.fileNumber);
    	
    	if(existLocation == -1){
    		System.out.printf("%-12s%-12s%-12s\n","Requested File","Operation","Status");
    		System.out.printf("%-12s%-14s%-12s\n",file.fileNumber,"D","Not Found");
    	}
    	else {
    		
    		boolean done = false;
    		int index = existLocation;
    		while(!done){
    			linkedNotUsedIndicies.add(linkedBlocks[index].index);
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
    		
    		System.out.printf("%-12s%-12s%-12s\n","Requested File","Operation","Status");
    		System.out.printf("%-12s%-14s%-12s\n",file.fileNumber,"D","Sucess");
    	}
	}
	
    public int contiguousFileExists(int fileNumber){
    	for(int i = 0; i<contiguousBlocks.length;i++){
    		if(contiguousBlocks[i].fileNumber == fileNumber)
    			return i;
    	}
    	return -1;
    }
    
    public int availableContiguousLocation(int numberOfBlocks){
    	int location = -1;
    	int blocksFree = 0;
    	
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
    			return location;
        	}
    		
    	}
    
    	return -1;
    }
    
    public int availableLinkedLocation(int numberOfBlocks){
    	if(linkedNotUsedIndicies.size()>=numberOfBlocks){
    		return 1;
    	}
    	return -1;
    }
    
    public int linkedFileExists(int fileNumber){
    	for(int i = 0; i<linkedBlocks.length;i++){
    		if(linkedBlocks[i].fileNumber == fileNumber && linkedBlocks[i].previous == null){
    			return i;
    		}
    	}
    	return -1;
    }
    
    public SimulatedFile getHashFileFromIndex(int fileIndex){
    	int indexCounter = 0;
    	
    	Enumeration keys = simFileHash.keys();
    	while ( keys.hasMoreElements() ){
    		Integer key = (Integer)keys.nextElement();
    		SimulatedFile value = (SimulatedFile)simFileHash.get(key);
    		
    		if(indexCounter == fileIndex){
    			return value;
    		}
    		
    		indexCounter++;
    	}
    	
    	return null;
    }  
    
	public static int randInt(int min, int max) {
	    Random rand = new Random();
	    int randomNum = rand.nextInt((max - min) + 1) + min;
	    return randomNum;
	}
}
