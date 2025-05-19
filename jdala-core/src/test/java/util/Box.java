package util;

public class Box {
    public Box(Object value) {
        this.value = value;
    }

    public Object value = null;

    public Object getValue() {
        return value;
    }

    public void setValue(Object s) {
        this.value = s;
    }

    public String toString(){
        return "box: {" + value.toString() + "}";
    }
}
