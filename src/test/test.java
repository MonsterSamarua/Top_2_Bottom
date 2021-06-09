package test;

import JavaBean.Producter;
import Service.F_F_S;
import Service.LL1;
import Service.Try2LL1;
import Utils.GSBuilder;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class test {
    public static void main(String[] args) throws IOException {
        // 接受输入
        System.out.println("==========================================");

        System.out.println("请输入文法GS（一行一行的输入生成式，end表示结束）：");
        Set<Producter> gs = GSBuilder.build();
//
//        // 打印生成的文法GS（格式1）
//        for(Producter producter : gs){
//            System.out.print(producter.getLeft());
//            System.out.print(" -> ");
//            System.out.println(producter.getRight());
//        }
//
//        // 打印生成的文法GS（格式2）
//        Map<Character, List<String>> gs2 = GSBuilder.gsHelper(gs);
//        for (Character c : gs2.keySet()){
//            System.out.print(c);
//            System.out.print(" -> ");
//            System.out.println(gs2.get(c).toString());
//        }
//
//        // 将非终结符号VN分为了两类：最终可以推出空串的 + 最终也无法推导出空串的
//        List<Set<Character>> res = new F_F_S().can_2_empty(gs);
//        Set<Character> emptyOK = res.get(0);
//        Set<Character> emptyNO = res.get(1);
//        System.out.println(emptyOK);
//        System.out.println(emptyNO);
//
//
//        Map<Character, Set<Character>> firstV = new F_F_S().firstV(gs);
//        for(Character c : firstV.keySet()){
//            System.out.print(c);
//            System.out.print(" : ");
//            System.out.println(firstV.get(c));
//        }
//
//        System.out.println("-----------------");
//
//        Map<String, Set<Character>> first = new F_F_S().first(gs);
//        for(String str : first.keySet()){
//            System.out.print(str);
//            System.out.print(" : ");
//            System.out.println(first.get(str));
//        }
//
//        System.out.println("-----------------");
//
//        Map<Character, Set<Character>> follow = new F_F_S().follow(gs);
//        for(Character key : follow.keySet()){
//            System.out.print(key);
//            System.out.print(" : ");
//            System.out.println(follow.get(key));
//        }
//
//        System.out.println("-----------------");
//
//        Map<Producter, Set<Character>> select = new F_F_S().select(gs);
//        for(Producter producter : select.keySet()){
//            System.out.print(producter);
//            System.out.print(" : ");
//            System.out.println(select.get(producter));
//        }
//
//
//        System.out.println("-----------------");
//        // 是否为LL1型文法
//        System.out.println(new LL1().isLL1(gs));
//
//
//        // 提取公因子
//        System.out.println("----------------------------------");
//        System.out.println("提取公因子...");
//        new Try2LL1().extractCommonFactor(gs);
//        for(Producter producter : gs){
//            System.out.println(producter);
//        }

        // 消除左递归
        System.out.println("----------------------------------");
        System.out.println("消除左递归...");
        new Try2LL1().removeRecursin(gs);
        for(Producter producter : gs){
            System.out.println(producter);
        }

    }
}

// 测试用例：求firstV集，first集，follow集，select集，判断是否为LL1文法
// 来源：清华大学出版社《编译原理》第三版，P72
/*
S->AB
S->bC
A->ε
A->b
B->ε
B->aD
C->AD
C->b
D->aS
D->c
end
 */

// 测试用例：求firstV集，first集，follow集，select集，判断是否为LL1文法
// 来源：清华大学出版社《编译原理》第三版，P100 T3
/*
S->MH
S->a
H->LSo
H->ε
K->dML
K->ε
L->eHf
M->K
M->bLM
end
 */


// 测试用例：提取公因式（本例未涉及到隐式公因式，消除不可达生成式）（仅需一次提取）
// 来源：自编题目
/*
S->aSb
S->aSd
 */

// 测试用例：提取公因式（本例未涉及到隐式公因式，消除不可达生成式）(需要多次提取)
// 来源：自编题目
/*
S->aSd
S->aSb
S->aS
end
 */

// 测试用例：提取公因式（考虑隐式公因式）
// 来源：清华大学出版社《编译原理》第三版，P78，例4.7
/*
S->ad
S->Bc
B->aS
B->bB
end
 */

// 测试用例：提取公因式（考虑隐式公因式 + 消除不可达生成式）
// 来源：清华大学出版社《编译原理》第三版，P78，例4.8
/*
S->aSd
S->Ac
A->aS
A->b
end
 */


// 测试用例：消除左递归
// 来源：自编题目
/*
S->Sa
S->Sb
S->Scd
S->e
S->f
end
 */

// 测试用例：消除左递归（一种比较特殊的情况）
// 来源：清华大学出版社《编译原理》第三版，P83
/*
S->Qc
S->c
Q->Rb
Q->b
R->Sa
R->a
end
 */



