
import java.io.File;
import java.util.Scanner;

public class CPS801ProjectTester {
	static FileHandler fileHandler;
	static Scanner scanner;
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		fileHandler = new FileHandler();
		scanner = new Scanner(System.in);
		getInputs();
		//fileHandler.generateRandomFiles(10,1,40);
		//fileHandler.deleteAllFiles();
	}
	
	public static void getInputs(){
		while(true){
			System.out.print("Enter (Type: \"help\" for more): ");
			String input = scanner.next();
			if(input.equalsIgnoreCase("help")){
				help();
			}
			else if(input.equalsIgnoreCase("random")){
				random();
			}
			else if(input.equalsIgnoreCase("list1")){
				listType1();
			}
			else if(input.equalsIgnoreCase("list2")){
				listType2();
			}
			else if(input.equalsIgnoreCase("rename")){
				rename();
			}
			else if(input.equalsIgnoreCase("delete")){
				delete();
			}
		}
	}
	
	public static void delete(){
		System.out.print("Enter File Number: ");
		int fileNumber = scanner.nextInt();
		
		if(fileHandler.fileList.size() == 0){
			System.out.println("No files exist.");
		}
		else if(fileNumber < 1 || fileNumber>fileHandler.fileList.size()){
			System.out.println("Invalid File Number.");
		}
		else{
			fileHandler.deleteFile(fileNumber);
			System.out.println("File Deleted.");
			System.out.print("\n");
		}
	}
	
	public static void rename(){
		System.out.print("Enter File Number: ");
		int fileNumber = scanner.nextInt();
		System.out.print("Enter New File Name: ");
		String newFileName = scanner.next();
		
		
		if(fileHandler.fileList.size() == 0){
			System.out.println("No files exist.");
		}
		else if(fileNumber < 1 || fileNumber>fileHandler.fileList.size()){
			System.out.println("Invalid File Number.");
		}
		else if (new File(newFileName).exists()){
			System.out.println("Invalid file name.");
		}
		else{
			fileHandler.renameFile(fileNumber,newFileName);
			System.out.println("File renamed.");
			System.out.print("\n");
		}
		
	}	
	
	public static void random(){
		fileHandler.generateRandomFiles(10, 1, 40);
		System.out.println("10 random files created.");
		System.out.print("\n");
	}	
		
	public static void listType1(){
		if(fileHandler.fileList.size() == 0){
			System.out.println("No files exist.");
		}
		else{
			for(int i = 0; i <fileHandler.fileList.size();i++){
				System.out.println("File "+(i+1)+": "+fileHandler.fileList.get(i));
			}
		}
		System.out.print("\n");
	}
	
	public static void listType2(){
		if(fileHandler.fileList.size() == 0){
			System.out.println("No files.");
		}
		else{
			for(int i = 0; i <fileHandler.fileList.size();i++){
				File file = new File((String) fileHandler.fileList.get(i)); 
				long fileSize = file.length();
				System.out.println("File "+(i+1)+": "+file.getName()+",	Size: "+fileSize+",	Blocks: "+(fileSize/512));
			}
		}
		System.out.print("\n");
	}
	
	public static void help(){
		System.out.println("Type \"random\"	to make 10 random files.");
		System.out.println("Type \"list1\" 	to list all files.");
		System.out.println("Type \"list2\" 	to list all files with more information.");
		System.out.println("Type \"delete\"	to delete a file.");
		System.out.println("Type \"rename\"	to rename a file.");
		System.out.print("\n");
	}
	
	

}
