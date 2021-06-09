package test;

import JavaBean.Producter;
import Service.Analysis;
import Service.F_F_S;
import Service.LL1;
import Service.Try2LL1;
import Utils.GSBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Set;

public class testFinally {
    public static void main(String[] args) throws IOException {
        System.out.println("==========================================");

        System.out.println("请输入文法GS（一行一行的输入生成式，end表示结束）：");
        Set<Producter> gs = GSBuilder.build();

        System.out.println("------------------------------------------");
        System.out.println("首符集First是：");
        Map<String, Set<Character>> first = new F_F_S().first(gs);
        for(String str : first.keySet()){
            System.out.print(str);
            System.out.print(" : ");
            System.out.println(first.get(str));
        }

        System.out.println("------------------------------------------");
        System.out.println("随符集Follow是：");
        Map<Character, Set<Character>> follow = new F_F_S().follow(gs);
        for(Character key : follow.keySet()){
            System.out.print(key);
            System.out.print(" : ");
            System.out.println(follow.get(key));
        }

        System.out.println("------------------------------------------");
        System.out.println("Select集是：");
        Map<Producter, Set<Character>> select = new F_F_S().select(gs);
        for(Producter producter : select.keySet()){
            System.out.print(producter);
            System.out.print(" : ");
            System.out.println(select.get(producter));
        }


        System.out.println("------------------------------------------");
        System.out.println("是否是LL(1)文法呢？：");
        System.out.println(new LL1().isLL1(gs));

        System.out.println("------------------------------------------");
        System.out.println("提取公因子...");
        new Try2LL1().extractCommonFactor(gs);

        System.out.println("------------------------------------------");
        System.out.println("消除左递归...");
        new Try2LL1().removeRecursin(gs);

        System.out.println("------------------------------------------");
        System.out.println("处理后，首符集First是：");
        first = new F_F_S().first(gs);
        for(String str : first.keySet()){
            System.out.print(str);
            System.out.print(" : ");
            System.out.println(first.get(str));
        }

        System.out.println("------------------------------------------");
        System.out.println("处理后，随符集Follow是：");
        follow = new F_F_S().follow(gs);
        for(Character key : follow.keySet()){
            System.out.print(key);
            System.out.print(" : ");
            System.out.println(follow.get(key));
        }

        System.out.println("------------------------------------------");
        System.out.println("处理后，Select集是：");
        select = new F_F_S().select(gs);
        for(Producter producter : select.keySet()){
            System.out.print(producter);
            System.out.print(" : ");
            System.out.println(select.get(producter));
        }


        System.out.println("------------------------------------------");
        System.out.println("处理后，是否是LL(1)文法呢？：");
        System.out.println(new LL1().isLL1(gs));


        System.out.println("==========================================");
        System.out.println("请输入要检查的串：");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String str = br.readLine();

        System.out.println("------------------------------------------");
        new Analysis().analysis(new F_F_S().select(gs), str + "#");
    }
}


// 测试用例：finalTest
// 来源：清华大学出版社《编译原理》第三版，P94 典例
/*
S->S+T
S->T
T->T*F
T->F
F->i
F->(S)
end
 */
/*
"i+i*i"
 */

// 该测试用例，需要先做处理
/*
S->a
S->v
S->(T)
T->TiS
T->S
end
 */