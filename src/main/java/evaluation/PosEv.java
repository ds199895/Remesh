package evaluation;


import edu.duke.geo4.colormapper.ColorMap;
import edu.duke.geo4.colormapper.GradientMap;
import pro_fan.AgentPoint;
import processing.core.PApplet;
import util.render.HE_Render;
import wblut.geom.WB_Coord;
import wblut.geom.WB_Point;
import wblut.geom.WB_Segment;
/**
 * @ClassName: PosEv
 * @author: wb
 * @date: 2022/7/31 14:05
 */

/**
 * 可达性实验
 */
public class PosEv implements Evaluation {

    WB_Coord p;//设定场地出入口位置
    WB_Coord sPt;

    double rate = 0;//值
    public boolean drawPos=false;

    public PosEv() {
    }


    //设置要计算的新建筑
    public void samplePt(WB_Coord ap, WB_Coord p) {
        this.sPt = ap;
        this.p = p;
    }

    @Override
    public void setEvaluation() {
        double dis = getDis(p, sPt);
        this.rate = dis;
    }


    @Override
    public void display(HE_Render render, AgentPoint ap, boolean is) {
        is=drawPos;

        PApplet app = render.getApp();
        app.stroke(255, 0, 0);
        app.fill(255, 0, 0);
        render.drawPoint(p, 20);

        app.pushMatrix();
        app.noStroke();
        app.fill(new GradientMap(app, ColorMap.JET).getColor((float) ap.getPos()), 170);
        app.translate(ap.loc.xf(), ap.loc.yf(), ap.loc.zf());
        app.box(2);
        app.popMatrix();

        // is==true时画连线
        if (is) {
            app.pushMatrix();
            app.stroke(100, 250);
            app.strokeWeight(0.2f);
            WB_Segment dis = new WB_Segment(ap.loc, new WB_Point(p.xf(), p.yf(), p.zf()));
            render.drawSegment(dis);
            app.popMatrix();
        }

    }


    @Override
    public float getValue() {
        return (float) rate;
    }


    public static double getDis(WB_Coord a, WB_Coord b) {
        return Math.sqrt((a.xf() - b.xf()) * (a.xf() - b.xf()) + (a.yf() - b.yf()) * (a.yf() - b.yf()) + (a.zf() - b.zf()) * (a.zf() - b.zf()));

    }
}
