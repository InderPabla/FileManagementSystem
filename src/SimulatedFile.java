public class SimulatedFile{

    int fileNumber;
    int fileBlocks;
    Block[] blocks;
    
    //int linkedStartingIndex;
    
    public SimulatedFile(int fileNumber, int fileBlocks){
        this.fileNumber = fileNumber;
        this.fileBlocks = fileBlocks;

        blocks = new Block[this.fileBlocks];

        for(int i = 0; i<blocks.length; i++){
            blocks[i] = new Block(512);
            blocks[i].generateRandomBlockData();
        }
        
        //linkedStartingIndex = -1;
    }
}
