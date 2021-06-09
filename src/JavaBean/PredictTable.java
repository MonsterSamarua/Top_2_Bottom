package JavaBean;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class PredictTable {

    // 成员变量为一个Table，下面的方法都是对该表的操作
    private Map<Character, Map<Character, Producter>> table;

    // makePredictTable建表
    public Map<Character, Map<Character, Producter>> makePredictTable(Map<Producter, Set<Character>> select){
        Map<Character, Map<Character, Producter>> table = new HashMap<>();
        for(Producter producter : select.keySet()){
            Character vn = producter.getLeft();
            Set<Character> set = select.get(producter);
            Map<Character, Producter> map = table.containsKey(vn) ? table.get(vn) : new HashMap<>();
            for(Character c : set){
                map.put(c, producter);
            }
            table.put(vn, map);
        }
        return table;
    }

    // findTable查表
    public String findTable(Map<Character, Map<Character, Producter>> table, Character vn, Character vt){
        if(table.get(vn) != null && table.get(vn).get(vt) != null){
            return table.get(vn).get(vt).getRight().equals("ε") ? "" : table.get(vn).get(vt).getRight();
        }else{
            return "ERROR";
        }
    }

}
