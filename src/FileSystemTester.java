import java.io.File;
import java.util.Scanner;
import java.util.Random;

/**
 * This class tests file and disk operations by user's command. 
 * @author Inderpreet Pabla
 */
public class FileSystemTester {

    static FileHandler fileHandler;
	static Scanner scanner;
	
	static boolean[] open = new boolean[100];
	public static void main(String[] args) {
		scanner = new Scanner(System.in);
		fileHandler = new FileHandler();

		getInputs();
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
			else if(input.equalsIgnoreCase("list")){
				list();
			}
			else if(input.equalsIgnoreCase("rename")){
				rename();
			}
			else if(input.equalsIgnoreCase("delete")){
				delete();
			}
			else if(input.equalsIgnoreCase("create")){
				create();
			}
			else if(input.equalsIgnoreCase("disk")){
				diskOperation();
			}
		}
	}

	public static void delete(){
		System.out.print("File Number: ");
        int fileNumber = scanner.nextInt();
        fileHandler.deleteFile(fileNumber);
		System.out.print("\n");
	}

	public static void rename(){
		System.out.print("Current File Number: ");
		int oldFileNumber = scanner.nextInt();
		System.out.print("\nNew File Number: ");
		int newFileNumber = scanner.nextInt();
		fileHandler.renameFile(oldFileNumber,newFileNumber);
		System.out.print("\n");
	}
	
	public static void random(){
        for(int i = 0; i < 10; i++){
            int fileNumber  = i+1;
            int randomBlocks = randInt(1,40);
            fileHandler.createFile(fileNumber,randomBlocks);
        }
		System.out.print("\n");
	}

	public static void list(){
	    fileHandler.listFiles();
		System.out.print("\n");

	}

    public static void create(){
    	System.out.print("File Number: ");
        int fileNumber = scanner.nextInt();
        System.out.print("\nNumber Of Blocks: ");
        int fileBlocks = scanner.nextInt();
        fileHandler.createFile(fileNumber,fileBlocks);
		System.out.print("\n");
	}

    public static void diskOperation(){
        fileHandler.diskOperation();
		System.out.print("\n");
	}
    
	public static void help(){
		System.out.println("Type \"random\"	to make 10 random files.");
		System.out.println("Type \"list\" 	to list all files.");
		System.out.println("Type \"delete\"	to delete a file.");
		System.out.println("Type \"rename\"	to rename a file.");
		System.out.println("Type \"create\"	to create a file.");
		System.out.println("Type \"disk\"	to do disk operations.");
		System.out.print("\n");
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
