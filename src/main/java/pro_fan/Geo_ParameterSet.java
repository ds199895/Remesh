package pro_fan;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * The class set a Map between String-Object (name-instance). Normally act as fields of other classes.
 *
 * @author Li Biao(李飚), Inst. AAA, S-ARCH, Southeast University (东南大学建筑学院 建筑运算与应用研究所)
 * Inst. AAA, S-ARCH, Southeast University
 */
public class Geo_ParameterSet {
    /**
     * instance of Map for storing String-Object
     */
    Map<String, Object> values;

    /**
     * name of the instance.
     */
    String name;

    /**
     * 构造函数
     *
     * @param name
     */
    public Geo_ParameterSet(String name) {
        this.name = name;
        values = new HashMap<String, Object>();
    }

    /**
     * 设置Map键值对
     *
     * @param name
     * @param value
     * @return 该实例
     */
    public Geo_ParameterSet set(String name, Object value) {
        values.put(name.toLowerCase(), value);
        return this;
    }

    /**
     * 获取key-value键值对：name-Object, 如果值为空,则返回ifEmpty
     *
     * @param name
     * @param ifEmpty
     * @return value(Object)
     */
    public Object get(String name, Object ifEmpty) {
        Object value = values.get(name.toLowerCase());
        if (value == null)
            return ifEmpty;
        return value;
    }

    /**
     * 设置name-int键值对
     *
     * @param name
     * @param value int
     * @return 该实例 this
     */
    public Geo_ParameterSet set(String name, int value) {
        values.put(name.toLowerCase(), value);
        return this;
    }

    /**
     * 获取key-value键值对：name-int, 如果值为空,则返回ifEmpty
     *
     * @param name
     * @param ifEmpty
     * @return value(int)
     */
    public int get(String name, int ifEmpty) {
        Object value = values.get(name.toLowerCase());
        if (value == null)
            return ifEmpty;
        return ((Integer) value).intValue();
    }

    /**
     * 设置name-double键值对
     *
     * @param name
     * @param value double
     * @return 该实例 this
     */
    public Geo_ParameterSet set(String name, double value) {
        values.put(name.toLowerCase(), (double) value);
        return this;
    }

    /**
     * 获取key-value键值对：name-double, 如果值为空,则返回ifEmpty
     *
     * @param name
     * @param ifEmpty
     * @return value(double)
     */
    public double get(String name, double ifEmpty) {
        Object value = values.get(name.toLowerCase());
        if (value == null)
            return ifEmpty;
        return ((Double) value).doubleValue();
    }

    /**
     * 设置name-boolean键值对
     *
     * @param name
     * @param value boolean
     * @return 该实例 this
     */
    public Geo_ParameterSet set(String name, boolean value) {
        values.put(name.toLowerCase(), value);
        return this;
    }

    /**
     * 获取key-boolean键值对：name-boolean, 如果值为空,则返回ifEmpty
     *
     * @param name
     * @param ifEmpty
     * @return value(boolean)
     */
    public boolean get(String name, boolean ifEmpty) {
        Object value = values.get(name.toLowerCase());
        if (value == null)
            return ifEmpty;
        return ((Boolean) value).booleanValue();
    }

    /**
     * 获取所有的键
     *
     * @return 所有的键数组
     */
    public String[] getNames() {
        Set<String> set = values.keySet();
        String[] result = new String[set.size()];
        int n = 0;
        for (String name : set) {
            result[n++] = name;
        }
        return result;

    }

    /**
     * 删除名为name的键值对
     *
     * @param name 需要删除的键
     * @return 该实例 this
     */
    public Geo_ParameterSet remove(String name) {
        values.remove(name.toLowerCase());
        return this;
    }
}
