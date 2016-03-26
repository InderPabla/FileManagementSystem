import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class FileHandler {
	
	ArrayList fileList = new ArrayList<String>();
	String fileHandlerFileName = "fileHandlerFile.txt";
	
	
	public FileHandler(){
		File file = new File(fileHandlerFileName);
		if(file.exists()){
			populateFileList();
		}
		else{
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void generateRandomFiles(int numberOfFiles, int minBlocks, int maxBlocks){
		String tempFileName = "file";
		int nextNumber = fileList.size();
		for(int i = 0; i<numberOfFiles;i++){
			SimulatedFile file = new SimulatedFile(tempFileName+nextNumber+".txt");
			file.makeRandomFile(minBlocks, maxBlocks);
			file.createFile();
			fileList.add(file.fileName);
			
			try {
				FileWriter fileWriter = new FileWriter(fileHandlerFileName,true);
				fileWriter.write(file.fileName+"\n");
				fileWriter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			nextNumber++;
		}
		
		//SimulatedFile file = new SimulatedFile();
	}
	
	public void populateFileList(){
		try {
			Scanner scanner = new Scanner(new FileReader(fileHandlerFileName));
			while(scanner.hasNext()){
				fileList.add(scanner.next());
			}
			scanner.close(); 
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	
	public void deleteAllFiles(){
		File file = new File(fileHandlerFileName);
		if(file.exists())
			file.delete();
		for(int i = 0 ; i<fileList.size();i++){
			String fileName = (String) fileList.get(i);
			file = new File(fileName);
			if(file.exists())
				file.delete();
		}
		fileList = new ArrayList<String>();				
	}
	
	public void deleteFile(int fileNumber){
		fileList.remove(fileNumber-1);
		remakeFileHandlerFile();
	}
	
	public void renameFile(int fileNumber, String newFileName){
		fileList.set(fileNumber-1, newFileName);
		remakeFileHandlerFile();
		File oldFile = new File((String)fileList.get(fileNumber-1));
		File newFile = new File(newFileName);	
		oldFile.renameTo(newFile);
	}
	
	public void remakeFileHandlerFile(){
		File file = new File(fileHandlerFileName);
		file.delete();
		
		try {
			file.createNewFile();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		for(int i = 0; i<fileList.size();i++){
			try {
				FileWriter fileWriter = new FileWriter(fileHandlerFileName,true);
				fileWriter.write(fileList.get(i)+"\n");
				fileWriter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
}