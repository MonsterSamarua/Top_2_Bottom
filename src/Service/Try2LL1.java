package Service;

import JavaBean.Producter;
import Utils.GSBuilder;

import java.util.*;


/**
 * Try2LL1主要提供了两个方法————「提取公因子extractCommonFactor」+「消除左递归removeRecursin」
 * 类如其名————一切都是为了"尝试将一个非LL1文法转化为等价的LL1文法"，即"Try to LL1"
 */
public class Try2LL1 {

    /**
     *【提取公因子】直接在文法gs的基础上进行等价的修改
     */
    public void extractCommonFactor(Set<Producter> gs){
        // 提取公因子之前，先消除隐式的情况
        transGs(gs);
        Map<Character, List<String>> gs2 = GSBuilder.gsHelper(gs);
        Set<Character> VN = GSBuilder.getVN(gs);
        // 维护了一个给新的VN命名的候选数组
        int k = 0;
        char[] unUsedCase = getUnUsedCase(gs);

        for(Character vn : VN){
            // 对于每个非终结符(产生式的左侧)，都尝试消除左递归
            List<String> list = gs2.get(vn);
            // 不是所有的文法，都能在有限步骤内提取出所有的左公因子。换句话说，明明算法正确，但仍有可能死循环————因此设置一个阀值
            int cnt = 10;
            boolean flag = true;
            while (flag && cnt > 0){
                cnt--;
                flag = false;
                // 循环实在是太深了(封装太少的恶果)，这里使用了一个flag+here标签实现跳出多层循环后，再次重新循环
                here: for(int i = 0; i < list.size(); i++){
                    String curI = list.get(i);
                    while (curI.length() > 0){
                        for(int j = i + 1; j < list.size(); j++){
                            String curJ = list.get(j);
                            if(curJ.indexOf(curI) != 0){
                                continue;
                            }else{
                                // 出现公共前缀
                                String s1 = list.get(i);
                                String s2 = list.get(j);
                                String commonFactor = curI;
                                char newVN = unUsedCase[k++];
                                String s3 = commonFactor + newVN;
                                list.remove(s1);
                                list.remove(s2);
                                list.add(s3);
                                removeFromGs(gs, vn, s1);
                                removeFromGs(gs, vn, s2);
                                gs.add(new Producter(vn + "->" + s3));
                                gs.add(new Producter(newVN + "->" + (s1.length() == commonFactor.length() ? "ε" : s1.replaceFirst(commonFactor, ""))));
                                gs.add(new Producter(newVN + "->" + (s2.length() == commonFactor.length() ? "ε" : s2.replaceFirst(commonFactor, ""))));
                                flag = true;
                                break here;
                            }
                        }
                        curI = curI.substring(0, curI.length() - 1);
                    }
                }
            }
        }
        // 提取公因子之后，删掉不可达的产生式
        removeUnReachable(gs);
    }


    /**
     *【消除左递归】直接在文法gs的基础上进行等价的修改
     */
    public void removeRecursin(Set<Producter> gs){
        Set<Character> VN = GSBuilder.getVN(gs);
        Map<Character, List<String>> gs2 = GSBuilder.gsHelper(gs);
        int k = 0;
        char[] unUsedCase = getUnUsedCase(gs);
        // 消除隐式
        transGs(gs);

        for(Character vn : VN){
            List<String> list = gs2.get(vn);
            List<String> alpha = new ArrayList<>();
            List<String> beta = new ArrayList<>();
            for(String str : list){
                if(str.charAt(0) == vn){
                    alpha.add(str.substring(1));
                }else{
                    if(str.equals("ε")){
                        str = "";
                    }
                    beta.add(str);
                }
            }
            if(alpha.size() > 0){
                removeFromGs(gs, vn);
                char newVN = unUsedCase[k++];
                for(String s : beta){
                    gs.add(new Producter(vn + "->" + s + newVN));
                }
                for(String s : alpha){
                    gs.add(new Producter(newVN + "->" + s + newVN));
                }
                gs.add(new Producter(newVN + "->" + "ε"));
            }
        }
        // 同样，删掉不可到达的生成式
        removeUnReachable(gs);
    }






    // 简单的功能性方法————比如目前已有的VN有SABC，这个方法会返回除了这几个之外的所有大写字母，作为候选
    private char[] getUnUsedCase(Set<Producter> gs){
        Set<Character> set = new HashSet<>();
        Set<Character> VN = GSBuilder.getVN(gs);
        for(int i = 0; i < 26; i++){
            set.add((char)('A' + i));
        }
        set.removeAll(VN);
        char[] res = new char[set.size()];
        int k = 0;
        for(char c : set){
            res[k++] = c;
        }
        return res;
    }


    /**
     * 消除隐式
     * 在「提取公因子」「消除左递归」之前进行隐式的消除————如果产生式的右部以非终结符(ABC)开头，那么公因子有可能是隐式的；递归同理
     */
    private void transGs(Set<Producter> gs){
        Set<Character> VN = GSBuilder.getVN(gs);
        boolean flag = true;
        while (flag){
            flag = false;
            Set<Producter> trash = new HashSet<>();
            Set<Producter> wait = new HashSet<>();
            for(Producter producter : gs){
                Character c = producter.getRight().charAt(0);
                // 这里多讨论了一下：比如 A->Aa|b，这就属于左递归的范畴。
                // 如果执迷于消除全部产生式的右部第一个VN，会死循环的————这就是「左递归」的陷阱，也是之后的工作
                if(VN.contains(c) && c != producter.getLeft()){
                    flag = true;
                    Map<Character, List<String>> gs2 = GSBuilder.gsHelper(gs);
                    List<String> arr = gs2.get(c);
                    trash.add(producter);
                    for(String s : arr){
                        // 你不能用一个递归的式子去进行首字母的替换
                        if(s.charAt(0) == c){
                            continue;
                        }
                        Character newLeft = producter.getLeft();
                        String newRight = producter.getRight().replaceFirst(String.valueOf(c), s);
                        wait.add(new Producter(newLeft + "->" + newRight));
                    }
                }
            }
            gs.removeAll(trash);
            gs.addAll(wait);
        }
    }

//    private void transGs2(Set<Producter> gs){
//        Set<Character> VN = GSBuilder.getVN(gs);
//        List<Character> listVN = new ArrayList<>(VN);
//        int k = 0;
//        Map<Character, List<String>> gs2 = GSBuilder.gsHelper(gs);
//        for(Character vn : listVN){
//            Set<Character> availableVN = new HashSet<>();
//            for(int i = 0 ; i < k; i++){
//                availableVN.add(listVN.get(i));
//            }
//            k++;
//            List<String> list = gs2.get(vn);
//            for(String str : list){
//                // 左边为vn
//                // 右边为str
//                // 右边第一个字母c
//                char c = str.charAt(0);
//                if (availableVN.contains(c)){
//                    removeFromGs(gs, vn, str);
//                    Map<Character, List<String>> gs3 = GSBuilder.gsHelper(gs);
//                    List<String> arr = gs3.get(c);
//                    for(String s : arr){
//                        // 你不能用一个递归的式子去进行首字母的替换
//                        if(s.charAt(0) == c){
//                            continue;
//                        }
//                        gs.add(new Producter(vn + "->" + str.replaceFirst(String.valueOf(c), s)));
//                    }
//                }
//            }
//        }
//    }


    /**
     * 删除不可达
     * 在「提取公因子」「消除左递归」之后进行不可达生成式的删除————有些生成式可能会变得不可达，完全可以删掉
     */
    private void removeUnReachable(Set<Producter> gs){
        Set<Character> reachableVN = new HashSet<>();
        Set<Character> unreachableVN = GSBuilder.getVN(gs);
        for(Producter producter : gs){
            for(char vn : unreachableVN){
                if(producter.getRight().indexOf(vn) >= 0){
                    reachableVN.add(vn);
                }
            }
        }
        // 排除"可达到"的非终结符VN，以及S（S作为起始符号，必然是"可到达"的）
        unreachableVN.removeAll(reachableVN);
        unreachableVN.remove('S');
        Set<Producter> trash = new HashSet<>();
        for(Producter producter : gs){
            if(unreachableVN.contains(producter.getLeft())){
                trash.add(producter);
            }
        }
        gs.removeAll(trash);
    }



    // 封装了一个简单的功能：从文法中删除某些产生式
    // 的是使上面的代码美观清晰
    private void removeFromGs(Set<Producter> gs, Character left, String right){
        Set<Producter> trash = new HashSet<>();
        for(Producter producter : gs){
            if(producter.getLeft() == left && producter.getRight().equals(right)){
                trash.add(producter);
            }
        }
        gs.removeAll(trash);
    }
    private void removeFromGs(Set<Producter> gs, Character left){
        Set<Producter> trash = new HashSet<>();
        for(Producter producter : gs){
            if(producter.getLeft() == left){
                trash.add(producter);
            }
        }
        gs.removeAll(trash);
    }

    // 封装了一个简单的功能：向文法中添加某些产生式
    // 的是使上面的代码美观清晰
    private void addToGs(Set<Producter> gs, Character newVN, String s, String commonFactor){
        String newRight = s.replaceFirst(commonFactor, "");
        if(newRight.equals("")){
            newRight = "ε";
        }
        gs.add(new Producter(newVN + "->" + newRight));
    }



}
