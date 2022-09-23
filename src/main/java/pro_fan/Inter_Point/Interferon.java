package pro_fan.Inter_Point;


import pro_fan.AgentPoint;
import util.render.HE_Render;
import wblut.geom.WB_Coord;

public abstract class Interferon {
    double influenceDistance;
    /**________________________*/
    double coefficient;


    public void setInfluenceValues( double influenceDistance,double coefficient){
        this.influenceDistance = influenceDistance;
        this.coefficient=coefficient;
    }

    public abstract double getRange(VoroCell v,WB_Coord ap);
    public abstract double getDistance(WB_Coord ap);
    public abstract void setValue(AgentPoint ap, double v);
    public abstract double getValue(AgentPoint ap);
    public abstract void draw(HE_Render render);
}
