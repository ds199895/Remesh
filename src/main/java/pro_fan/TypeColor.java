package pro_fan;

public enum TypeColor {
    oldBuild("oldBuild", 0xff00ff00),
    trgBuild("trgBuild",0xffff0000),
    water("water", 0xffd7f4d3),
    newBuild("newBuild",0xff0000ff);

    public final String type;
    protected final int color;
    TypeColor(String type, int color) {
        this.type = type;
        this.color = color;
    }

    public int color() {
        return color;
    }

    public String type(){
        return type;
    }

}
