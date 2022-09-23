package pro_fan.Inter_Point;

import pro_fan.AgentPoint;
import pro_fan.Evaluation;
import pro_fan.Utils;
import util.render.HE_Render;
import wblut.geom.WB_Coord;
import wblut.geom.WB_Point;

/**
 * TODO
 *
 * @author libiao
 * @version 1.0
 * @date 2022/8/11 13:15
 * Inst. AAA, S-ARCH, Southeast University
 */
public class Inter_Point extends Interferon{
    private WB_Point point;
    public Inter_Point(WB_Point point,double influenceDistance,double coefficient){
        this.point = point;
        this.setInfluenceValues(influenceDistance,coefficient);
    }

    @Override
    public double getRange(VoroCell v,WB_Coord ap) {

        double distance2D = this.getDistance(ap);
        double range = distance2D>this.influenceDistance ? v.getMaxSize() : Utils.map((float)distance2D,0,(float)influenceDistance,(float)v.getMinSize(),(float)v.getMaxSize());
        return range;
    }

    @Override
    public double getDistance(WB_Coord ap) {
        double distance2D = this.point.getDistance(ap);
        return distance2D;
    }

    @Override
    public void setValue(AgentPoint ap, double v) {
        ap.setValue(Evaluation.distance,v);

    }

    @Override
    public double getValue(AgentPoint ap) {
//        double size = Utils.map((float)ap.getValue(Evaluation.distance.name()), 0, 1, (float)minSize, (float)maxSize);
        return 0;
    }

    @Override
    public void draw(HE_Render render) {
    render.drawPoint(this.point,50);
    }
}
