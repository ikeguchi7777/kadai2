import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public class DataBase{
	private static DataBase instance=new DataBase();
	Map<String,Data> datas;
	List<String> keyMap;
	int id=0;

	static DataBase getDataBase() {
		return instance;
	}
	public DataBase() {
		datas=new HashMap<>();
		keyMap = new ArrayList<>();
	}

	void insert(String sentence) {
		String[] values=SentenceAnalysis.Analysis(sentence);
		int key = -1;
		if(!keyMap.contains(values[1]))
		{
			keyMap.add(values[1]);
			key = keyMap.size()-1;
		}
		else
			key = keyMap.indexOf(values[1]);
		if(datas.containsKey(values[0])) {
			datas.get(values[0]).add(key, values[2]);
		}
		else {
			Data data = new Data();
			data.add(key, values[2]);
			datas.put(values[0], data);
		}
	}

	boolean remove(String sentence) {
		String[] values = SentenceAnalysis.Analysis(sentence);
		int key = keyMap.indexOf(values[1]);
		if(key!=-1) {
			Data data = datas.get(values[0]);
			if(data.remove(key, values[2])) {
				if(data.records.isEmpty())
					datas.remove(values[0]);
				return true;
			}
		}
		return false;
	}
}

class SentenceAnalysis{
	private static String preposition = "to for from up down in on at of by";
	static String[] Analysis(String sentence) {
		String[] elements = new String[3];
		StringTokenizer tokenizer = new StringTokenizer(sentence);
		elements[0]=tokenizer.nextToken();
		elements[1]=tokenizer.nextToken();
		elements[2]="";
		while (true) {
			String t=tokenizer.nextToken();
			if(preposition.contains(t)) {
				if(elements[2]=="")
					elements[1]+=" "+t;
				elements[2]+=t;
				elements[1]+=" "+elements[2];
				elements[2]="";
			}
			else {
				elements[2]+=t;
				if(tokenizer.hasMoreTokens())
					elements[2]+=" ";
				else
					break;
			}
		}
		return elements;
	}
}

class Data{
	Map<Integer, List<String>> records;
	public Data() {
		records = new HashMap<>();
	}

	public void add(int key,String value) {
		if(records.containsKey(key))
		{
			records.get(key).add(value);
		}
		else {
			List<String> list = new ArrayList<>();
			list.add(value);
			records.put(key, list);
		}
	}

	/**
	 *
	 * @param key
	 * @return 要素が空になったらtrue
	 */
	public boolean remove(int key,String value ) {
		boolean result=false;
		List<String> list = records.get(key);
		if (list!=null) {
			result = list.remove(value);
			if(list.isEmpty())
				records.remove(key);
		}
		return result;
	}
}