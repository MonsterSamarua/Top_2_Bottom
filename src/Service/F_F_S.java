package Service;

import JavaBean.Producter;
import Utils.GSBuilder;

import java.util.*;

public class F_F_S {

    /**
     * 求firstV集————非终结符VN和终结符VT的firstV集，为之后求推导式右侧字符串的first集作准备
     * @param gs    文法GS（推导式Producter的Set集合）
     * @return      非终结符VN和终结符VT的firstV集
     */
    public Map<Character, Set<Character>> firstV(Set<Producter> gs) {
        Map<Character, Set<Character>> firstV = new HashMap<>();
        // 将所有的VN和VT先初始化到HashMap当中
        for(Character vn : GSBuilder.getVN(gs)){
            firstV.put(vn, new HashSet<>());
        }
        for(Character vt : GSBuilder.getVT(gs)){
            firstV.put(vt, new HashSet<>());
            firstV.get(vt).add(vt);
            // 对于终结符VT，其firstV就是自身（e.g. FirstV(a) = {a}）
            // 而对于非终结符VN，求其firstV需要扫描（下面就是）
        }

        // 调用了can_2_empty()方法————返回emptyOK和emptyNO————如果当前字符属于emptyNO或者是终结符，就没有继续找firstV的必要了
        List<Set<Character>> res = can_2_empty(gs);
        Set<Character> emptyOK = res.get(0);
        Set<Character> emptyNO = res.get(1);

        // 不断尝试更新firstV，当其不再变化时停止循环
        while (true){
            int beforeCnt = countCell(firstV);
            for(Producter producter : gs){
                String str = producter.getRight();
                Set<Character> tempSet = new HashSet<>();
                // index不断后移，扫描生成式右侧的字符串
                // 一个小技巧：如果这个index能够移动到串的最后(str.length())，就可以把ε加到该VN的firstV中
                int index = 0;
                while(index < str.length()){
                    char c = str.charAt(index);
                    tempSet.addAll(firstV.get(c));
                    tempSet.remove('ε');
                    if(emptyNO.contains(c) || isVT(c)){
                        break;
                    }
                    index++;
                }
                if(index == str.length()){
                    tempSet.add('ε');
                }
                firstV.get(producter.getLeft()).addAll(tempSet);
            }
            if(countCell(firstV) == beforeCnt){
                break;
            }
        }
        return firstV;
    }

    /**
     * 求first集————推导式右侧字符串的first集
     * @param gs    文法GS（推导式Producter的Set集合）
     * @return      推导式右侧字符串的first集
     */
    public Map<String, Set<Character>> first(Set<Producter> gs){
        Map<String, Set<Character>> first = new HashMap<>();
        Map<Character, Set<Character>> firstV = firstV(gs);
        // 这里仍旧用到了emptyOK和emptyNO，因为需要扫描，我们需要判断index是否还可以后移
        List<Set<Character>> res = new F_F_S().can_2_empty(gs);
        Set<Character> emptyOK = res.get(0);
        Set<Character> emptyNO = res.get(1);

        // 我们已经求出每个文法符号(VN+VT)的firstVN了，在求字符串的first的时候是完全不需要while循环的
        for(Producter producter : gs){
            first.put(producter.getRight(), new HashSet<>());
            String str = producter.getRight();
            if(str.length() == 1){
                first.get(str).add(str.charAt(0));
            }
            int index = 0;
            while (index < str.length()){
                char c = str.charAt(index);
                first.get(str).addAll(firstV.get(c));
                first.get(str).remove('ε');
                if(emptyNO.contains(c) || isVT(c)){
                    break;
                }
                index++;
            }
            if(index == str.length()){
                first.get(str).add('ε');
            }
        }
        return first;
    }


    /**
     * 返回follow集
     * 这里有必要说一下实现的思路。以「S -> ABCD」为例
     * 用一个index扫描生成式右侧字符串，当index指向一个非终结符VN时，开启以下算法(假设当前指向B)：
     *      1. 将其后方的firstV加到当前的follow当中；如果后方可推出ε，则用一个jndex继续向后扫直到扫到不可推出ε的字符为止
     *      2. 如果这个时候jndex == str.length()，说明这一路(包括结束)都可以推出ε，因此这时把左侧的VN的follow加到当前的follow当中
     * 另外两点，要有这种意识：
     *      1. 第一步啥都别干，赶紧直接把把{#}加到开始符号S的follow里
     *      2. 随符集follow中是不可能出现ε的，因此每次向follow加first时，都要把ε去掉，即加入"非空串元素"
     *      3. while循环结束的条件依旧是follow集不再变化
     */
    public Map<Character, Set<Character>> follow(Set<Producter> gs){
        Map<Character, Set<Character>> follow = new HashMap<>();
        for(Character c : GSBuilder.getVN(gs)){
            follow.put(c, new HashSet<>());
        }
        // 第一步就是把{#}加到开始符号S的follow里
        follow.get('S').add('#');
        // 这里仍旧用到了emptyOK和emptyNO
        List<Set<Character>> res = new F_F_S().can_2_empty(gs);
        Set<Character> emptyOK = res.get(0);
        Set<Character> emptyNO = res.get(1);

        Map<Character, Set<Character>> firstV = firstV(gs);

        while (true){
            int beforeCnt = countCell(follow);
            for(Producter producter : gs){
                String str = producter.getRight();
                int index = 0;
                while (index < str.length()){
                    char c = str.charAt(index);
                    if(isVN(c)){
                        int jndex = index + 1;  // 幽默一下
                        while (jndex < str.length()){
                            char nextC = str.charAt(jndex);
                            follow.get(c).addAll(firstV.get(nextC));
                            follow.get(c).remove('ε');
                            if(emptyNO.contains(nextC) || isVT(nextC)){
                                break;
                            }
                            jndex++;
                        }
                        if(jndex == str.length()){
                            follow.get(c).addAll(follow.get(producter.getLeft()));
                        }
                    }
                    index++;
                }
            }
            if(countCell(follow) == beforeCnt){
                break;
            }
        }
        return follow;
    }


    /**
     * 返回select集————这可能是整个文法分析中最简单的一步
     * 以「S->ABC」为例：
     *      1.如果first(ABC)中含有ε，Select(S->ABC) = First(ABC) - {ε} + Follow(S)
     *      2.如果first(ABC)中不含有ε，Select(S->ABC) = First(ABC)
     */
    public Map<Producter, Set<Character>> select(Set<Producter> gs){
        // 之前求的first集和follow集，终于全都派上了用场！
        Map<Producter, Set<Character>> select = new HashMap<>();
        Map<String, Set<Character>> first = first(gs);
        Map<Character, Set<Character>> follow = follow(gs);

        for(Producter producter : gs){
            select.put(producter, new HashSet<>());
            if(!first.get(producter.getRight()).contains('ε')){
                select.get(producter).addAll(first.get(producter.getRight()));
            }else{
                select.get(producter).addAll(first.get(producter.getRight()));
                select.get(producter).remove('ε');
                select.get(producter).addAll(follow.get(producter.getLeft()));
            }
        }

        return select;
    }






    /**
     * 返回emptyOK + emptyNO
     * 即对非终结符号VN分为了两类：最终可以推出空串的 + 最终也无法推导出空串的（这尤为关键，是整个自上而下分析的基础，一步错步步错）
     */
    public List<Set<Character>> can_2_empty(Set<Producter> gs){
        Set<Character> emptyOK = new HashSet<>();                       // 最终可以推导出空串
        Set<Character> emptyNO = new HashSet<>();                       // 最终不可推导出空串
        Set<Producter> gs1 = gs;                                        // GS文法格式1
        Map<Character, List<String>> gs2 = GSBuilder.gsHelper(gs);      // GS文法格式2

        // 第一次扫描
        // 对于某个终结符A，如果生成式右侧为ε，即直接可以推导出空串，将该生成式左侧加入emptyOK集合
        for(Producter producter : gs){
            if(producter.getRight().equals("ε")) {
                emptyOK.add(producter.getLeft());
            }
        }

        // 第二次扫描
        // 对于某个终结符A，如果以此为左侧的生成式，右侧全都包括至少一个终结符a，则将其加入emptyNO集合
        for(Character c : gs2.keySet()){
            if(emptyOK.contains(c)){
                continue;
            }
            boolean flag = true;
            for(String s : gs2.get(c)){
                if(!hasVT(s)){
                    flag = false;
                    break;
                }
            }
            if (flag){
                emptyNO.add(c);
            }
        }

        // 循环扫描（1次扫描尝试加入emptyOK + 1次扫描尝试加入emptyNO）
        // 当这两个集合都不再变化时，STOP
        while (true){
            // 记录一下两个集合每次循环之前的大小，循环结束后比较的时候要用————决定是否还需要下一次循环
            int emptyOK_num = emptyOK.size();
            int emptyNO_num = emptyNO.size();

            // 循环中的第一次扫描，当右侧全为非终结符(ABC)并且它们都在emptyOK之中时，将生成式左侧加入emptyOK集合
            for (Producter producter : gs){
                if(emptyOK.contains(producter.getLeft()) || emptyNO.contains(producter.getLeft())){
                    continue;
                }
                if(!hasVT(producter.getRight())){
                    boolean flag = true;
                    for(char i : producter.getRight().toCharArray()){
                        if(!emptyOK.contains(i)){
                            flag = false;
                            break;
                        }
                    }
                    if (flag){
                        emptyOK.add(producter.getLeft());
                    }
                }
            }

            // 循环中的第二次扫描，对于某个非终结符A，以其为左侧的所有生成式的右侧，都至少包括一个终结符b 或 一个在emptyNO中的非终结符B，则将A加入emptyNO集合
            for(Character c : gs2.keySet()){
                if(emptyOK.contains(c) || emptyNO.contains(c)){
                    continue;
                }
                boolean flag = true;
                for(String s : gs2.get(c)){
                    if(!hasVT(s)){
                        flag = false;
                        for(char i : s.toCharArray()){
                            if(emptyNO.contains(i)){
                                flag = true;
                            }
                        }
                    }
                }
                if (flag){
                    emptyNO.add(c);
                }
            }

            // 当这两个集合都不再变化时，跳出循环
            if(emptyOK_num == emptyOK.size() && emptyNO_num == emptyNO.size()){
                break;
            }
        }
        // 将千辛万苦得到的两个集合（emptyOK + emptyNO）作为返回值返回
        List<Set<Character>> res = new ArrayList<>();
        res.add(emptyOK);
        res.add(emptyNO);
        return res;
    }






    // 是否是非终结符VN？
    // 我们认为，大写字母即为非终结符(S, A, B, C, D)
    // 跟严谨的写法是，遍历文法GS的所有生成式Producter，所有生成式左边的字母即为VN，其余为VT
    private boolean isVN(Character c){
        return Character.isUpperCase(c);
    }

    // 是否为终结符VT？
    // 我们认为，小写字母即为终结符(a, b, c, d, e)
    // 注意两点：一，这不严谨；二，「ε」被Java认为是小写字母，但它不是终结符————如果忽略这个细节，后面你将会付出惨痛的代价
    private boolean isVT(Character c){
        String loli = "()+*";
        return (Character.isLowerCase(c) || loli.indexOf(c) >= 0) && !c.equals('ε');
    }

    // 一个字符串是否包含非终结符？
    // 技巧：取反————这个字符串不包含非终结符————这个字符串是纯终结符串
    private boolean hasVN(String s){
        for(char c : s.toCharArray()){
            if(isVN(c)){
                return true;
            }
        }
        return false;
    }

    // 同理
    private boolean hasVT(String s){
        for(char c : s.toCharArray()){
            if(isVT(c)){
                return true;
            }
        }
        return false;
    }

    // 将HashMap的value中字符(Character)的个数作为了该数据结构的标志————如果该计数值不变，则认为该数据结构没变
    private int countCell(Map<Character, Set<Character>> map){
        int cnt = 0;
        for(Set<Character> value : map.values()){
            cnt += value.size();
        }
        return cnt;
    }


}
