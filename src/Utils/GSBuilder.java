package Utils;

import JavaBean.Producter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class GSBuilder {

    // 接受输入，返回GS文法，即产生式的集合：Set<Producter>
    public static Set<Producter> build() throws IOException {
        Set<Producter> gs = new HashSet<>();
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String line = "";
        while (!(line = br.readLine().trim()).equals("end")){
            gs.add(new Producter(line));
        }
        return gs;
    }

    // 返回文法GS的另一种格式的副本
    public static Map<Character, List<String>> gsHelper(Set<Producter> gs){
        Map<Character, List<String>> map = new HashMap<>();
        for(Producter producter : gs){
            Character c = producter.getLeft();
            String s = producter.getRight();
            if(!map.containsKey(c)){
                List<String> list = new ArrayList<>();
                map.put(c, list);
            }
            map.get(c).add(s);
        }
        return map;
    }

    // 根据文法GS，获取所有的非终结符集
    public static Set<Character> getVN(Set<Producter> gs){
        Set<Character> vn = new HashSet<>();
        for(Producter producter : gs){
            vn.add(producter.getLeft());
        }
        return vn;
    }

    // 根据文法GS，获取所有的终结符集
    public static Set<Character> getVT(Set<Producter> gs){
        Set<Character> vt = new HashSet<>();
        String loli = "()+*";
        for(Producter producter : gs){
            for(Character c : producter.getRight().toCharArray()){
                if(Character.isLowerCase(c) || loli.indexOf(c) >= 0){
                    vt.add(c);
                }
            }
        }
        return vt;
    }
    // 这两个方法的返回值主要是为了给Map做key的，所以把'ε'算上了————其他地方可别把ε作为终结符了！！！
}
