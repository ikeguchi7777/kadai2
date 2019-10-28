import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.StringTokenizer;

public class DataBase {
	private static DataBase instance = new DataBase();
	Map<String, Data> datas;
	Map<String,Integer> keyMap;
	static int id = 0;

	static DataBase getDataBase() {
		return instance;
	}

	private DataBase() {
		datas = new HashMap<>();
		keyMap = new HashMap<>();
	}

	boolean insert(String sentence) {
		String[] values = null;
		if(sentence.contains("?"))
			return false;
		try {
			values = SentenceAnalysis.Analysis(sentence);
		} catch (Exception e) {
			return false;
		}
		int key = -1;
		if (!keyMap.containsKey(values[1])) {
			keyMap.put(values[1],id++);
			key = keyMap.size() - 1;
		} else
			key = keyMap.get(values[1]);
		if (datas.containsKey(values[0])) {
			datas.get(values[0]).insert(key, values[2]);
		} else {
			Data data = new Data(values[0]);
			data.insert(key, values[2]);
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

		int key = keyMap.get(values[1]);
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
		Set<Integer> keyset =d.records.keySet();
		for (String s : keyMap.keySet()) {
			if(keyset.contains(keyMap.get(s)))
				dataBase.keyMap.put(s, keyMap.get(s));
		}
		dataBase.datas.put(name, d);
		return dataBase;
	}

	DataBase searchByVarb(String varb) {
		DataBase dataBase = new DataBase();
		for (String key : keyMap.keySet()) {
			if (key.equals(varb))
				dataBase.keyMap.put(key, keyMap.get(key));
		}
		if (dataBase.keyMap.isEmpty())
			return null;
		for (Data d : datas.values()) {
			if (d.Search(dataBase.keyMap.values())) {
				dataBase.datas.put(d.name, d);
			}
		}
		if(dataBase.datas.isEmpty())
			return null;
		return dataBase;
	}

	DataBase searchByValue(String value) {
		DataBase dataBase = new DataBase();
		dataBase.keyMap = keyMap;
		for (Data d : datas.values()) {
			if (d.Search(value)) {
				dataBase.datas.put(d.name, d);
			}
		}
		if (dataBase.datas.isEmpty())
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
		if (dataBase!=null&&!isContainVar(values[1]))
			dataBase = dataBase.searchByVarb(values[1]);
		if (dataBase!=null&&!isContainVar(values[2]))
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
	
	public static void Save(File file) {
		try {
            PrintWriter p_writer = new PrintWriter
                    (new BufferedWriter(new OutputStreamWriter
                    (new FileOutputStream(file),"UTF-8")));
            for (String s : instance.GetResult()) {
    			p_writer.println(s);
    		}
            
            //ファイルをクローズする
            p_writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
		
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

	public void insert(int key, String value) {
		if (records.containsKey(key)) {
			records.get(key).add(value);
		} else {
			List<String> list = new ArrayList<>();
			list.add(value);
			records.put(key, list);
		}
	}

	public boolean Search(Collection<Integer> collection) {
		for (Integer integer : collection) {
			if (records.containsKey(integer))
				return true;
		}
		return false;
	}

	public boolean Search(String value) {
		for (List<String> list : records.values()) {
			for (String s : list) {
				if (s.equals(value))
					return true;
			}
		}
		return false;
	}

	public List<String> GetAllSentence(Map<String,Integer> keyMap) {
		List<String> list = new ArrayList<>();
		Set<Integer> sets = records.keySet();
		for (String s : keyMap.keySet()) {
			Integer t =keyMap.get(s);
			if(sets.contains(t)) {
				for (String string : records.get(t)) {
					list.add(name + " " + s + " " + string);
				}
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