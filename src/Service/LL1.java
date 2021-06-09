package Service;

import JavaBean.Producter;
import Utils.GSBuilder;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class LL1 {

    // 判断是否为LL(1)文法————根据First集，Follow集->Select集
    public boolean isLL1(Set<Producter> gs){
        Map<Producter, Set<Character>> select = new F_F_S().select(gs);
        Set<Character> VN = GSBuilder.getVN(gs);

        for(Character vn : VN){
            Set<Producter> producterSet = producterWithVN(vn, gs);
            Set<Character> tmp = new HashSet<>();
            int cnt = 0;
            for(Producter producter : producterSet){
                cnt += select.get(producter).size();
                tmp.addAll(select.get(producter));
            }
            if(cnt != tmp.size()){
                return false;
            }
        }
        return true;
    }

    private Set<Producter> producterWithVN(Character vn, Set<Producter> gs){
        Set<Producter> producterSet = new HashSet<>();
        for (Producter producter : gs){
            if(producter.getLeft() == vn){
                producterSet.add(producter);
            }
        }
        return producterSet;
    }

}
