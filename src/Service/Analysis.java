package Service;

import JavaBean.PredictTable;
import JavaBean.Producter;
import Utils.GSBuilder;

import java.util.*;

public class Analysis {

    public void analysis(Map<Producter, Set<Character>> select, String str){
        Map<Character, Map<Character, Producter>> table = new PredictTable().makePredictTable(select);

        Stack<Character> stack = new Stack<>();
        stack.push('#');
        stack.push('S');
        int index = 0;

        while (true){
            Character X = stack.pop();
            Character Y = str.charAt(index);
            if(X == '#' && Y == '#'){
                System.out.println("接受！！！自顶向下分析成功 >_<!!!");
                break;
            }
            if(isVN(X)){
                String tmp = new PredictTable().findTable(table, X, Y);
                if(tmp.equals("ERROR")){
                    System.out.println("推导失败！自顶向下分析失败 >_<");
                    break;
                }else{
                    System.out.println("【推导】" + X + "->" + (tmp.equals("") ? "ε" : tmp));
                    for(int i = tmp.length() - 1; i >= 0; i--){
                        stack.push(tmp.charAt(i));
                    }
                }
            }else{
                if(X == Y){
                    System.out.println("【匹配】" + Y);
                    index++;
                }else{
                    System.out.println("匹配失败！自顶向下分析失败 >_<");
                    break;
                }
            }
        }

    }


    // 老生常谈了，判断VN/VT
    private boolean isVN(Character c){
        return Character.isUpperCase(c);
    }
    private boolean isVT(Character c){
        String loli = "()+*";
        return (Character.isLowerCase(c) || loli.indexOf(c) >= 0) && !c.equals('ε');
    }

}
