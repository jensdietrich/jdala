package util;

public class Box {
    public Box(Object value) {
        this.value = value;
    }

    public Object value = null;

    public String toString(){
        return "box: {" + value.toString() + "}";
    }
}
