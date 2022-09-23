package evaluation;

import edu.duke.geo4.colormapper.ColorMap;
import edu.duke.geo4.colormapper.GradientMap;
import pro_fan.AgentPoint;
import pro_fan.ExtrudeBox;
import pro_fan.FanGeo;
import pro_fan.Site;
import processing.core.PApplet;
import util.render.HE_Render;
import wblut.geom.*;
import wblut.hemesh.HE_Face;
import wblut.hemesh.HE_Mesh;

import java.util.ArrayList;
import java.util.List;
/**
 * @ClassName: SightEv
 * @author: wb
 * @date: 2022/7/30 16:05
 */

/**设定trg build需要被看 terrain,old build,new build起到遮挡作用  */

public class SightEv implements Evaluation{
    List<FanGeo>geos;//所有几何体
    ArrayList<WB_Polygon> blocks;//遮挡物的集合

    ArrayList<ExtrudeBox> olds;//遮挡老建筑
    public ArrayList<ExtrudeBox> news;//新建筑
    ArrayList<ExtrudeBox> trgs;//需要被看的建筑

    HE_Mesh terrain;//地形
    WB_Coord sPt;//评价点位置
    double rate=0;//评价

    WB_Coord view;//集中观景点位置
    public boolean drawSight=false;

    public SightEv(List<FanGeo> geos){
        this.geos=geos;
        blocks=new ArrayList<>();
        olds=new ArrayList<>();
        news=new ArrayList<>();
        trgs=new ArrayList<>();
        terrain=new HE_Mesh();
        getAllBuildings();
        getCenterView();
    }

    //得到视线检测AABB
    public void setPreConditions(ExtrudeBox container){
        //遮挡物集合
        ArrayList<WB_Polygon>polys=new ArrayList<>();

        //加入地形mesh
        List<HE_Face> faces = terrain.getFaces();
        for(HE_Face face:faces){polys.add(face.getPolygon());}
        //加入遮挡房子mesh
        for(ExtrudeBox old:olds){
            faces = old.mesh.getFaces();
            for(HE_Face face:faces){polys.add(face.getPolygon());}
        }
        //除掉自身的新建筑加入遮挡
        for(ExtrudeBox news:news){
            if(news!=container){
            faces = news.mesh.getFaces();
            for(HE_Face face:faces){polys.add(face.getPolygon());}}
        }
        blocks=polys;
    }

    //给场地建筑分类
    protected void getAllBuildings(){
        for(FanGeo geo:geos){
            if(geo instanceof ExtrudeBox){
                if(((ExtrudeBox) geo).type.type=="trgBuild")trgs.add((ExtrudeBox) geo);
                if(((ExtrudeBox) geo).type.type=="newBuild")news.add((ExtrudeBox) geo);
                if(((ExtrudeBox) geo).type.type=="oldBuild")olds.add((ExtrudeBox) geo);
            }
            if(geo instanceof Site){
                terrain=((Site) geo).terrain.mesh;
            }
        }
    }

    //计算集中观景点位置
    protected void getCenterView(){
        double x=0;double y=0;double z=0;
        for(ExtrudeBox trg:trgs){
            x+=trg.loc.xf();
            y+=trg.loc.yf();
            z+=trg.loc.zf();
        }
        x=x/trgs.size();
        y=y/trgs.size();
        z=z/trgs.size();
        view=new WB_Point(x,y,z);
    }


    //设置计算点
    public void samplePt(WB_Coord sPt) {
        this.sPt=sPt;
    }

    //计算该点视线评分
    @Override
    public void setEvaluation() {
        double value=0;
        //检测与trg视线,若无遮挡则加1分
        for(ExtrudeBox trg:trgs){

            int score=0;
            boolean inte=false;
            WB_Segment sight=new WB_Segment(sPt,new WB_Point(trg.loc.xf(),trg.loc.yf(),trg.loc.zf()+trg.depth/2));
            for(WB_Polygon polygon:blocks){
                //相交则记录
                if(WB_GeometryOp3D.getIntersection3D(sight,polygon).intersection==true) {
                    inte=true;
                    break;
                }
            }
            score=inte==true?0:1;
            value+=score;
        }
        value=value/trgs.size();
        rate=value;

    }

    //仅计算该点与集中观景点的视线评分
    public void setEvaluation(boolean is) {
        double value=0;
        //检测与trg视线,若无遮挡则加1分
            boolean inte=false;
            WB_Segment sight=new WB_Segment(sPt,new WB_Point(view.xf(),view.yf(),view.zf()+trgs.get(0).depth/2));
            for(WB_Polygon polygon:blocks){
                //相交则记录
                if(WB_GeometryOp3D.getIntersection3D(sight,polygon).intersection==true) {
                    inte=true;
                    break;
                }
            }
        value=inte==true?0:1;
        rate=value;
    }

    @Override
    public void display(HE_Render render,AgentPoint ap,boolean is) {
        is=drawSight;
        PApplet app = render.getApp();
        app.pushMatrix();
        app.noStroke();
        app.fill(new GradientMap(app, ColorMap.JET).getColor((float)ap.getSight()), 170);
        app.translate(ap.loc.xf(), ap.loc.yf(), ap.loc.zf());
        app.box(2);
        app.popMatrix();
        // is==true时画视线
        if(is){
            app.pushMatrix();
            app.stroke(100,250);
            app.strokeWeight(0.2f);
        for(ExtrudeBox trg:trgs){
            WB_Segment sight=new WB_Segment(ap.loc,new WB_Point(trg.loc.xf(),trg.loc.yf(),trg.loc.zf()+trg.depth/2));
            render.drawSegment(sight);
        }
            app.popMatrix();
        }
    }


    @Override
    public float getValue() {
        return (float)rate;
    }
}
