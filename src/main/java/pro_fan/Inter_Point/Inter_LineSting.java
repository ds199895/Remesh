package pro_fan.Inter_Point;

import pro_fan.AgentPoint;
import pro_fan.Evaluation;
import pro_fan.Utils;
import util.render.HE_Render;
import wblut.geom.WB_Coord;
import wblut.geom.WB_GeometryOp;
import wblut.geom.WB_PolyLine;

/**
 * TODO
 *
 * @author libiao
 * @version 1.0
 * @date 2022/8/11 12:07
 * Inst. AAA, S-ARCH, Southeast University
 */
public class Inter_LineSting extends Interferon{
    private WB_PolyLine polyline;

//    public Inter_LineSting(WB_PolyLine poly,double minSize, double maxSize, double influenceDistance){
//        this.polyline = poly;
//        this.setInfluenceValues(minSize,maxSize,influenceDistance);
//    }
/***/
    public Inter_LineSting(WB_PolyLine poly,double influenceDistance,double coefficient){
        this.polyline = poly;
        this.setInfluenceValues(influenceDistance,coefficient);
    }



    @Override
    public double getRange(VoroCell v, WB_Coord ap) {
        double distance2D = this.getDistance(ap);
        double range = distance2D>this.influenceDistance ? v.getMaxSize() : Utils.map((float)distance2D,0,(float)influenceDistance,(float)v.getMinSize(),(float)v.getMaxSize());
        return range;
    }

    @Override
    public double getDistance(WB_Coord ap) {
        double distance2D = WB_GeometryOp.getDistance2D(ap, this.polyline);
        return distance2D;
    }

    @Override
    public void setValue(AgentPoint ap, double v) {
        //double distance = this.getDistance(ap);

        ap.setValue(Evaluation.distance,v);
    }

    @Override
    public double getValue(AgentPoint ap) {
        //double size = Utils.map((float)ap.getValue(Evaluation.distance.name()), 0, 1, (float)minSize, (float)maxSize);
        return 0;
    }

    @Override
    public void draw(HE_Render render) {
        render.drawPolyLine(this.polyline);
    }
}
