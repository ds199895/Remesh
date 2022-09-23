package pro_fan;

import wblut.geom.WB_GeometryFactory;

public class Comman {
    public static double EPS = 1e-9;
    public static WB_GeometryFactory gf_hemesh=new WB_GeometryFactory();
    public static boolean same(double a, double b, double eps) {
        double delta = a - b;
        return (delta > 0 ? delta : -delta) < eps;
    }
}
