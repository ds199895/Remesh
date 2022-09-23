package pro_fan;


import wblut.geom.WB_Coord;
import wblut.geom.WB_Point;

/**
 * TODO：参数控制点
 *
 * @author libiao
 * @version 1.0
 * @date 2022/7/28 16:56
 * Inst. AAA, S-ARCH, Southeast University
 */
public class AgentPoint implements WB_Coord {
    public WB_Point loc;
    public Evaluation value;

    // 设置相关检测参数, 参数b1需要归一化到[0,1]区间
    public void setValue(Evaluation val, double v){
        relativeGeo.set(val.name(), v);
    }
    // 获取相关检测参数
    public double getValue(String name){
        return relativeGeo.getParameterSet().get(name, 0.5d);
    }
    protected Geo_Machine relativeGeo = new Geo_Machine();

    public AgentPoint(Evaluation value, WB_Point location){
        this.value = value;
        this.loc = location;
    }
    public AgentPoint(double x, double y, double z) {
        loc = new WB_Point(x, y, z);

    }

    // 由钱姐 蓓蓓等小朋友完成*********************************
    // 设置相关检测参数, 参数b1需要归一化到[0,1]区间
    public void setBlaBla01(double b1) {
        relativeGeo.set("wind", b1);
    }
    /**———————————————————————— by wb ————————————————————————————————————————————————*/
    public void setWind(double b1) {
        relativeGeo.set("wind", b1);
    }

    public double getWind() {
        return relativeGeo.getParameterSet().get("wind", 0.5d);
    }

    public void setSight(double b2) {
        relativeGeo.set("sight", b2);
    }

    public double getSight() {
        return relativeGeo.getParameterSet().get("sight", 0.5d);
    }

    public void setPos(double b3) {
        relativeGeo.set("pos", b3);
    }

    public double getPos() {
        return relativeGeo.getParameterSet().get("pos", 0.5d);
    }

    public void setLight(double b4) {
        relativeGeo.set("light", b4);
    }

    public double getLight() {
        return relativeGeo.getParameterSet().get("light", 0.5d);
    }
    /**———————————————————————— by wb ————————————————————————————————————————————————*/




    // 获取相关检测参数
    public double getBlaBla02() {
        return relativeGeo.getParameterSet().get("testBlaBla02", 0.5d);
    }



    @Override
    public double xd() {
        return loc.xd();
    }

    @Override
    public double yd() {
        return loc.yd();
    }

    @Override
    public double zd() {
        return loc.zd();
    }

    @Override
    public double wd() {
        return loc.wd();
    }

    @Override
    public double getd(int i) {
        if (i == 0) {
            return this.loc.xd();
        } else if (i == 1) {
            return this.loc.yd();
        } else if (i == 2) {
            return this.loc.zd();
        } else {
            return i == 3 ? 1.0D : 0.0D / 0.0;
        }
    }

    @Override
    public float xf() {
        return loc.xf();
    }

    @Override
    public float yf() {
        return loc.yf();
    }

    @Override
    public float zf() {
        return loc.zf();
    }

    @Override
    public float wf() {
        return loc.wf();
    }

    @Override
    public float getf(int i) {
        if (i == 0) {
            return this.loc.xf();
        } else if (i == 1) {
            return this.loc.yf();
        } else if (i == 2) {
            return this.loc.zf();
        } else {
            return i == 3f ? 1.0f : 0.0f / 0.0f;
        }
    }

    @Override
    public int compareTo(WB_Coord p) {
        int cmp = Double.compare(this.xd(), p.xd());
        if (cmp != 0) {
            return cmp;
        } else {
            cmp = Double.compare(this.yd(), p.yd());
            if (cmp != 0) {
                return cmp;
            } else {
                cmp = Double.compare(this.zd(), p.zd());
                return cmp != 0 ? cmp : Double.compare(this.wd(), p.wd());
            }
        }
    }

}
