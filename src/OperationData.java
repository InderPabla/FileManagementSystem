/**
 * This class stores operation data of a given file index 
 * and file operation type.
 * @author Inderpreet Pabla
 */
public class OperationData {
	
	int fileIndex; //file index 
	String operation; //file operation (R, W, D)
	
	/**
	 * Class constructor initializes file index and operation 
	 */
	public OperationData (int fileIndex, String operation){
		this.fileIndex = fileIndex; 
		this.operation = operation;
	}
}
