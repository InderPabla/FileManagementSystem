import java.util.ArrayList;

public class PageHandler{
	static int[] inputs = {0,2,1,3,5,4,6,3,7,4,7,3,3,5,5,3,1,1,1,7,1,3,4,1};
	static ArrayList pageArray;
    public static void main(String[] args){       
    	
    	System.out.println("LRU");
        resetPageArray();    
        int pageIndex = 1;  
        for(int i = 0; i<inputs.length; i++){
        	PageData pageData = LRU((ArrayList<Integer>) pageArray.get(pageIndex-1),inputs[i]);
        	pageArray.set(pageIndex, pageData.pageDataList);
        	
        	System.out.print(inputs[i]+": ");
        	for(int j = 0; j<8; j++){
        		if(j == 4)
        			System.out.print("| ");
	        	if((int)pageData.pageDataList.get(j) == -1)
	        		break;
	        	System.out.print(pageData.pageDataList.get(j)+" ");
	        }
        	System.out.print("-------- ");
        	if(pageData.pageFault)
        		System.out.print("-Page Fault ");
        	System.out.print("-Distance: "+pageData.distance+" ");
        	System.out.print("\n");
        	pageIndex++;
        }
       
        System.out.println("");
        System.out.println("FIFO");
        resetPageArray();
        pageIndex = 1;
        for(int i = 0; i<inputs.length; i++){
        	PageData pageData = FIFO((ArrayList<Integer>) pageArray.get(pageIndex-1),inputs[i]);
        	pageArray.set(pageIndex, pageData.pageDataList);
        	
        	System.out.print(inputs[i]+": ");
        	for(int j = 0; j<8; j++){
        		if(j == 4)
        			System.out.print("| ");
	        	if((int)pageData.pageDataList.get(j) == -1)
	        		break;
	        	System.out.print(pageData.pageDataList.get(j)+" ");
	        }
        	System.out.print("-------- ");
        	if(pageData.pageFault)
        		System.out.print("-Page Fault ");
        	System.out.print("-Distance: "+pageData.distance+" ");
        	System.out.print("\n");
        	pageIndex++;
        }           
    }
    
    public static void resetPageArray(){
    	pageArray = new ArrayList<ArrayList<Integer>>();
        for(int i = 0; i<inputs.length+1; i++){
        	ArrayList pageDataAtInput = new ArrayList<Integer>();
	        for(int j = 0; j<8; j++){
	        	pageDataAtInput.add(-1);
	        }
	        pageArray.add(pageDataAtInput);
        }
    }
    
    public static PageData LRU(ArrayList<Integer> pageDataAtInput, int input){
    	PageData pageData = new PageData(8,pageDataAtInput);
    	
		boolean exist = false;
		int index = 0;
		for(int i = 0; i <8;i++){
			if((int)pageData.pageDataList.get(i) == input){
				exist = true;
				index = i;
				break;
			}
		}
		
		if(exist){
			if((index-0) >= 4)
				pageData.pageFault = true;
			pageData.pageDataList.remove(index);
			pageData.pageDataList.add(0, input);
			
			pageData.distance = index+1;
		}
		else{
			pageData.pageFault = true;
			pageData.pageDataList.add(0, input);
			pageData.pageDataList.remove(8);
			pageData.distance = -1;
		}
    	   		
    	return pageData;
    }
    
    public static PageData FIFO(ArrayList<Integer> pageDataAtInput, int input){
    	PageData pageData = new PageData(8,pageDataAtInput);
    	
    	boolean exist = false;
		int index = 0;
		for(int i = 0; i <8;i++){
			if((int)pageData.pageDataList.get(i) == input){
				exist = true;
				index = i;
				break;
			}
		}
		if(exist && (index-0)>=4){
			pageData.pageDataList.remove(index);
			pageData.pageDataList.add(0, input);
			pageData.pageFault = true;
			pageData.distance = index+1;
		}
		else if(exist){
			pageData.distance = index+1;
		}
		else{
			pageData.pageFault = true;
			pageData.pageDataList.add(0, input);
			pageData.pageDataList.remove(8);
			pageData.distance = -1;
		}
    	return pageData;
    }
    
    public static class PageData {
    	
    	ArrayList pageDataList = new ArrayList<Integer>();
    	boolean pageFault= false;
    	int distance = 0;
    	
    	public PageData(int size, ArrayList<Integer> pageData){
    		for(int i = 0; i<8;i++)
    			pageDataList.add(pageData.get(i));
    	}
    	
    }

}

