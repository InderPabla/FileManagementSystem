import java.util.ArrayList;

public class PageData {
	
	ArrayList pageDataList = new ArrayList<Integer>();
	boolean pageFault= false;
	int distance = 0;
	
	public PageData(int size, ArrayList<Integer> pageData){
		for(int i = 0; i<8;i++)
			pageDataList.add(pageData.get(i));
	}
	
}
