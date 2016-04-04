/**
 * This class acts like a simulated file. With array of blocks storing random chunks of data 512 bytes in size.
 * @author Inderpreet Pabla
 */
public class SimulatedFile{

    int fileNumber;
    int fileBlocks;
    Block[] blocks;
    
    public SimulatedFile(int fileNumber, int fileBlocks){
        this.fileNumber = fileNumber;
        this.fileBlocks = fileBlocks;

        blocks = new Block[this.fileBlocks];

        for(int i = 0; i<blocks.length; i++){
            blocks[i] = new Block(512);
            blocks[i].generateRandomBlockData();
        }   
    }
}
