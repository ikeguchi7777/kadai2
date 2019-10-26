
/***
  Unify Program written



  変数:前に？をつける．

  Examle:
  % Unify "Takayuki" "Takayuki"
  true

  % Unify "Takayuki" "Takoyuki"
  false

  % Unify "?x am Takayuki" "I am Takayuki"
  ?x = I .

  % Unify "?x is ?x" "a is b"
  false

  % Unify "?x is ?x" "a is a"
  ?x = a .

  % Unify "?x is a" "b is ?y"
  ?x = b.
  ?y = a.

  % Unify "?x is a" "?y is ?x"
  ?x = a.
  ?y = a.

  Unify は，ユニフィケーション照合アルゴリズムを実現し，
  パターン表現を比較して矛盾のない代入によって同一と判断
  できるかどうかを調べる．

  ポイント！
  ここでは，ストリング同士の単一化であるから，出現検査を行う必要はない．
  しかし，"?x is a"という表記を"is(?x,a)"とするなど，構造を使うならば，
  単一化において出現検査を行う必要がある．
  例えば，"a(?x)"と"?x"を単一化すると ?x = a(a(a(...))) となり，
  無限ループに陥ってしまう．

  ***/

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;

class Unify {
    static DataBase[] results;
    static String[] terms;

    public Unify(String next) {
        terms = next.split(";");
        results = new DataBase[terms.length];
        for (int i = 0; i < terms.length; i++) {
            results[i] = DataBase.getDataBase().Search(terms[i]);
        }
    }

    public static void main(String arg[]) {
        // For CUI Test
        /*
         * if (arg.length != 2) {
         * System.out.println("Usgae : % Unify [string1] [string2]"); } else {
         * System.out.println((new Unifier()).unify(arg[0], arg[1])); }
         */
        // Unifier searcher = new Unifier();
        Scanner stdin = new Scanner(System.in);
        for (int i = 0; i < arg.length; i++) {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(arg[i]), "UTF-8"));
                String s = reader.readLine();
                while (s != null) {
                    if (!s.equals("")) {
                        // searcher.addLine(s);
                        DataBase.getDataBase().insert(s);
                    }
                    s = reader.readLine();
                }
                reader.close();
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
        while (true) {
            System.out.println("Enter Search Pattern:");
            String scan = stdin.nextLine();
            if (scan.equals("exit"))
                break;
            (new Unifier()).search(scan);
            for (String string : Unifier.getVarSets()) {
                System.out.println(string);
            }
        }
        stdin.close();
    }
}

class Unifier {
    StringTokenizer st1;
    String buffer1[];
    StringTokenizer st2;
    String buffer2[];
    HashMap<String, String> vars;
    LinkedList<String> lines;
    private static DataBase[] results;
    private static String[] terms;
    private static List<String> varSets;

    Unifier() {
        vars = new HashMap<String, String>();
        lines = new LinkedList<>();
    }

    Unifier(List<String> list) {
        this();
        for (String string : list) {
            addLine(string);
        }
    }

    Unifier(List<String> list, HashMap<String, String> vars) {
        this(list);
        this.vars = new HashMap<String, String>(vars);
    }

    public static void setByTerms(String str) {
        String[] terms = str.split(";");
        Unifier.terms = terms;
        DataBase[] results = new DataBase[terms.length];
        for (int i = 0; i < results.length; i++) {
            results[i] = DataBase.getDataBase().Search(terms[i]);
        }
        Unifier.results = results;
    }

    public static ArrayList<String> getVarSets(){
        return new ArrayList<String>(Unifier.varSets);
    }
    private static void cleanVarSets(){
        Unifier.varSets = new ArrayList<String>();
    }

    private static void addVarSets(String string){
        Unifier.varSets.add(string);
    }

    public boolean search(String str) {
        cleanVarSets();
        setByTerms(str);
        return search(new HashMap<>(), 0);
    }

    boolean search(HashMap<String, String> vars, int layer) {
        boolean match = false;
        if (layer < terms.length) {
            if(Unifier.results[layer]==null)
                return false;
            Unifier unifier = new Unifier(Unifier.results[layer].GetResult());
            for (String string : unifier.lines) {
                unifier.setVars(vars);
                if (unifier.unify(string, terms[layer])) {
                    match = search(unifier.vars, layer + 1) || match;
                }
            }
        } else {
            String varset = vars.toString();
            if(varset.equals("{}"))
                addVarSets("true.");
            else
                addVarSets(varset);
            return true;
        }
        return match;
    }

    private void setVars(HashMap<String, String> vars2) {
        vars = new HashMap<>(vars2);
    }

    public void addLine(String s) {
        lines.add(s);
    }

    public boolean unify(String string1, String string2, HashMap<String, String> bindings) {
        this.vars = bindings;
        return unify(string1, string2);
    }

    public boolean unify(String string1, String string2) {
        // System.out.println(string1);
        // System.out.println(string2);

        // 同じなら成功
        if (string1.equals(string2)) {
            //System.out.println(string1);
            return true;
        }

        // 各々トークンに分ける
        st1 = new StringTokenizer(string1);
        st2 = new StringTokenizer(string2);

        // 数が異なったら失敗
        if (st1.countTokens() != st2.countTokens())
            return false;

        // 定数同士
        int length = st1.countTokens();
        buffer1 = new String[length];
        buffer2 = new String[length];
        for (int i = 0; i < length; i++) {
            buffer1[i] = st1.nextToken();
            buffer2[i] = st2.nextToken();
        }
        for (int i = 0; i < length; i++) {
            if (!tokenMatching(buffer1[i], buffer2[i])) {
                return false;
            }
        }

        // 最後まで O.K. なら成功
        //System.out.println(string1);
        //System.out.println(vars.toString());
        return true;
    }

    boolean tokenMatching(String token1, String token2) {
        if (token1.equals(token2))
            return true;
        if (var(token1) && !var(token2))
            return varMatching(token1, token2);
        if (!var(token1) && var(token2))
            return varMatching(token2, token1);
        if (var(token1) && var(token2))
            return varMatching(token1, token2);
        return false;
    }

    boolean varMatching(String vartoken, String token) {
        if (vars.containsKey(vartoken)) {
            if (token.equals(vars.get(vartoken))) {
                return true;
            } else {
                return false;
            }
        } else {
            replaceBuffer(vartoken, token);
            if (vars.containsValue(vartoken)) {
                replaceBindings(vartoken, token);
            }
            vars.put(vartoken, token);
        }
        return true;
    }

    void replaceBuffer(String preString, String postString) {
        for (int i = 0; i < buffer1.length; i++) {
            if (preString.equals(buffer1[i])) {
                buffer1[i] = postString;
            }
            if (preString.equals(buffer2[i])) {
                buffer2[i] = postString;
            }
        }
    }

    void replaceBindings(String preString, String postString) {
        Iterator<String> keys;
        for (keys = vars.keySet().iterator(); keys.hasNext();) {
            String key = (String) keys.next();
            if (preString.equals(vars.get(key))) {
                vars.put(key, postString);
            }
        }
    }

    boolean var(String str1) {
        // 先頭が ? なら変数
        return str1.startsWith("?");
    }

}
