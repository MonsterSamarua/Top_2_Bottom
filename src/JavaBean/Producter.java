package JavaBean;

public class Producter {

    private String producter;

    public Producter(String producter){
         this.producter = producter;
    }

    public String getProducter() {
        return producter;
    }

    public void setProducter(String producter) {
        this.producter = producter;
    }

    public Character getLeft(){
        String[] arr = producter.split("->");
        return arr[0].charAt(0);
    }

    public String getRight(){
        String[] arr = producter.split("->");
        return arr[1];
    }

    public void setLeft(Character c){
        String[] arr = producter.split("->");
        producter = c + "->" + arr[1];
    }

    public void setRight(String s){
        String[] arr = producter.split("->");
        producter = arr[0] + "->" + s;
    }

    @Override
    public String toString() {
        return producter;
    }
}
