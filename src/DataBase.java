import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public class DataBase {
	private static DataBase instance = new DataBase();
	Map<String, Data> datas;
	List<String> keyMap;
	int id = 0;

	static DataBase getDataBase() {
		return instance;
	}

	public DataBase() {
		datas = new HashMap<>();
		keyMap = new ArrayList<>();
	}

	void insert(String sentence) {
		String[] values = SentenceAnalysis.Analysis(sentence);
		int key = -1;
		if (!keyMap.contains(values[1])) {
			keyMap.add(values[1]);
			key = keyMap.size() - 1;
		} else
			key = keyMap.indexOf(values[1]);
		if (datas.containsKey(values[0])) {
			datas.get(values[0]).add(key, values[2]);
		} else {
			Data data = new Data(values[0]);
			data.add(key, values[2]);
			datas.put(values[0], data);
		}
	}

	boolean remove(String sentence) {
		String[] values = SentenceAnalysis.Analysis(sentence);
		int key = keyMap.indexOf(values[1]);
		if (key != -1) {
			Data data = datas.get(values[0]);
			if (data.remove(key, values[2])) {
				if (data.records.isEmpty())
					datas.remove(values[0]);
				return true;
			}
		}
		return false;
	}

	Data searchByName(String name) {
		return datas.get(name);
	}

	String[] searchByVarb(String varb) {
		List<Integer> keys = new ArrayList<>();
		for (int i = 0; i < keyMap.size(); i++) {
			if((new Matcher()).tokenMatching(varb, keyMap.get(i)))
				keys.add(i);
		}
		if(keys.isEmpty())
			return null;
		List<String> results = new ArrayList<>();
		for (Data data : datas.values()) {
			results.addAll(data.Search(keys, keyMap));
		}
		return (String[]) results.toArray();
	}
	
	String[] searchByValue(String value) {
		List<String> result = new ArrayList<>();
		for (Data data : datas.values()) {
			result.addAll(data.Search(value, keyMap));
		}
		return (String[]) result.toArray();
	}

	public static void main(String[] args) {
		getDataBase().insert("Hanako studies philosophy");
	}
}

class SentenceAnalysis {
	private static String[] preposition = { "to", "for", "from", "up", "down", "in", "on", "at", "of", "by" };

	static String[] Analysis(String sentence) {
		String[] elements = new String[3];
		StringTokenizer tokenizer = new StringTokenizer(sentence);
		elements[0] = tokenizer.nextToken();
		elements[1] = tokenizer.nextToken();
		elements[2] = "";
		while (true) {
			String t = tokenizer.nextToken();
			if (PrepositionCheck(t)) {
				if (elements[2] == "")
					elements[1] += " " + t;
				elements[2] += t;
				elements[1] += " " + elements[2];
				elements[2] = "";
			} else {
				elements[2] += t;
				if (tokenizer.hasMoreTokens())
					elements[2] += " ";
				else
					break;
			}
		}
		System.out.println("name:" + elements[0]);
		System.out.println("varb:" + elements[1]);
		System.out.println("value:" + elements[2]);
		return elements;
	}

	static boolean PrepositionCheck(String s) {
		for (String pre : preposition) {
			if(pre.equals(s))
				return true;
		}
		return false;
	}
}

class Data {
	String name;
	Map<Integer, List<String>> records;

	public Data(String name) {
		this.name = name;
		records = new HashMap<>();
	}

	public void add(int key, String value) {
		if (records.containsKey(key)) {
			records.get(key).add(value);
		} else {
			List<String> list = new ArrayList<>();
			list.add(value);
			records.put(key, list);
		}
	}

	public List<String> Search(List<Integer> keys,List<String> keyMap) {
		List<String> results = new ArrayList<>();
		for (Integer key : keys) {
			if(records.containsKey(key)) {
				for (String string : records.get(key)) {
					results.add(name+" "+keyMap.get(key)+" "+string);
				}
			}
		}
		return results;
	}
	
	
	public List<String> Search(String value,List<String> keyMap){
		List<String> result = new ArrayList<>();
		for (int key : records.keySet()) {
			List<String> valuelist = records.get(key);
			for (String string : valuelist) {
				if(new Matcher().matching(value, string))
					result.add(name+" "+ keyMap.get(key)+" "+string);
			}
		}
		return result;
	}
	

	/**
	 *
	 * @param key
	 * @return 要素が空になったらtrue
	 */
	public boolean remove(int key, String value) {
		boolean result = false;
		List<String> list = records.get(key);
		if (list != null) {
			result = list.remove(value);
			if (list.isEmpty())
				records.remove(key);
		}
		return result;
	}
}