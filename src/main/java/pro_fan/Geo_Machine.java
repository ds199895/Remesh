package pro_fan;

/**
 * The class set a Map between String-Object (name-instance). Normally act as fields of other classes.
 *
 * @author Li Biao(李飚), Inst. AAA, S-ARCH, Southeast University (东南大学建筑学院 建筑运算与应用研究所)
 * @date 2022/4/19 16:02
 * Inst. AAA, S-ARCH, Southeast University
 */
public class Geo_Machine {
    private String name;
    protected Geo_ParameterSet parameters;

    public Geo_Machine() {
        this.setName(this.getClass().getSimpleName());
        parameters = new Geo_ParameterSet(name);
    }

    public String getName(){
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public Geo_ParameterSet getParameterSet() {
        return parameters;
    }

    public void setParameterSet(Geo_ParameterSet parameters) {
        this.parameters = parameters;
    }

    public void set(String name, Object value) {
        parameters.set(name, value);
    }

    public void set(String name, int value) {
        parameters.set(name, value);
    }

    public void set(String name, double value) {
        parameters.set(name, value);
    }

    public Object get(String name) {
        return parameters.get(name, null);
    }
}
