import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
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

	boolean insert(String sentence) {
		String[] values = null;
		try {
			values = SentenceAnalysis.Analysis(sentence);
		} catch (Exception e) {
			return false;
		}
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
		return true;
	}

	boolean remove(String sentence) {
		String[] values = null;
		try {
			values = SentenceAnalysis.Analysis(sentence);
		} catch (Exception e) {
			return false;
		}

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

	DataBase searchByName(String name) {
		DataBase dataBase = new DataBase();
		Data d = datas.get(name);
		if (d == null)
			return null;
		Map<Integer, List<String>> records = new HashMap<Integer, List<String>>();
		for (int key : d.records.keySet()) {
			dataBase.keyMap.add(keyMap.get(key));
			records.put(dataBase.keyMap.size() - 1, d.records.get(key));
		}
		d.records = records;
		dataBase.datas.put(name, d);
		return dataBase;
	}

	DataBase searchByVarb(String varb) {
		List<Integer> keys = new ArrayList<>();
		for (int i = 0; i < keyMap.size(); i++) {
			if ((new Matcher()).tokenMatching(varb, keyMap.get(i)))
				keys.add(i);
		}
		if (keys.isEmpty())
			return null;
		/*List<String> results = new ArrayList<>();
		for (Data data : datas.values()) {
			results.addAll(data.Search(keys, keyMap));
		}
		return (String[]) results.toArray();*/
		DataBase dataBase = new DataBase();
		for (Data d : datas.values()) {
			if (d.Search(keys)) {
				dataBase.datas.put(d.name, d);
			}
		}
		dataBase.keyMap = keyMap;
		return dataBase;
	}

	DataBase searchByValue(String value) {
		DataBase dataBase = new DataBase();
		dataBase.keyMap = keyMap;
		for (Data d : datas.values()) {
			if (d.Search(value))
				dataBase.datas.put(d.name, d);
		}
		if (dataBase.datas.size() == 0)
			return null;
		return dataBase;
	}

	DataBase Search(String sentence) {
		DataBase dataBase = this;
		String[] values = null;
		try {
			values = SentenceAnalysis.Analysis(sentence);
		} catch (Exception e) {
			return null;
		}
		if (!isVar(values[0]))
			dataBase = dataBase.searchByName(values[0]);
		if (!isContainVar(values[1]))
			dataBase = dataBase.searchByVarb(values[1]);
		if (!isContainVar(values[2]))
			dataBase = dataBase.searchByValue(values[2]);
		return dataBase;
	}

	boolean isVar(String s) {
		return s.startsWith("?");
	}

	boolean isContainVar(String s) {
		return s.contains("?");
	}

	public List<String> GetResult() {
		List<String> list = new ArrayList<>();
		for (Data d : datas.values()) {
			list.addAll(d.GetAllSentence(keyMap));
		}
		return list;
	}

	public static void main(String[] args) {
		getDataBase().insert("Hanako studies philosophy");
	}
}

class SentenceAnalysis {
	private static String[] preposition = { "to", "for", "from", "up", "down", "in", "on", "at", "of", "by" };

	static String[] Analysis(String sentence) throws NoSuchElementException {
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
			if (pre.equals(s))
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

	public List<String> Search(List<Integer> keys, List<String> keyMap) {
		List<String> results = new ArrayList<>();
		for (Integer key : keys) {
			if (records.containsKey(key)) {
				for (String string : records.get(key)) {
					results.add(name + " " + keyMap.get(key) + " " + string);
				}
			}
		}
		return results;
	}

	public boolean Search(List<Integer> keys) {
		for (Integer integer : keys) {
			if (records.containsKey(integer))
				return true;
		}
		return false;
	}

	public boolean Search(String value) {
		for (List<String> list : records.values()) {
			for (String s : list) {
				if (new Matcher().tokenMatching(s, value))
					return true;
			}
		}
		return false;
	}

	public List<String> Search(String value, List<String> keyMap) {
		List<String> result = new ArrayList<>();
		for (int key : records.keySet()) {
			List<String> valuelist = records.get(key);
			for (String string : valuelist) {
				if (new Matcher().matching(value, string))
					result.add(name + " " + keyMap.get(key) + " " + string);
			}
		}
		return result;
	}

	public List<String> GetAllSentence(List<String> keyMap) {
		List<String> list = new ArrayList<>();
		for (int key : records.keySet()) {
			for (String string : records.get(key)) {
				list.add(name + " " + keyMap.get(key) + " " + string);
			}
		}
		return list;
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