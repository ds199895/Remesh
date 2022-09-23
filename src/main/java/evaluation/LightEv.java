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
import wblut.hemesh.HEC_Sphere;
import wblut.hemesh.HEM_Slice;
import wblut.hemesh.HE_Face;
import wblut.hemesh.HE_Mesh;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: LightEv
 * @author: wb
 * @date: 2022/7/31 10:05
 */

public class LightEv implements Evaluation{
    List<FanGeo>geos;//场地所有几何体
    public ArrayList<WB_Polygon> blocks;//遮挡物
    ArrayList<ExtrudeBox> olds;//老建筑
    public ArrayList<ExtrudeBox> news;//所有新建筑
    ArrayList<ExtrudeBox> trgs;//现存建筑
    HE_Mesh terrain;//地形
    WB_Coord sPt;
    HE_Mesh sunSphere;//太阳半球
    ArrayList<WB_Segment>sunLights;//太阳光
    double rate=0;//采光值

    public boolean drawLight=false;

    public LightEv(List<FanGeo> geos){
        this.geos=geos;
        blocks=new ArrayList<>();
        olds=new ArrayList<>();
        news=new ArrayList<>();
        trgs=new ArrayList<>();
        terrain=new HE_Mesh();
        getAllBuildings();//给场地建筑分类
        this.sunSphere=getSunSphere();//得到太阳光球


    }

    //得到所有遮挡物,除去自身
    public void setPreConditions(List<HE_Face> container){
        //遮挡物集合
        ArrayList<WB_Polygon>polys=new ArrayList<>();

        //加入地形mesh
        List<HE_Face> faces = terrain.getFaces();
        for(HE_Face face:faces){polys.add(face.getPolygon());}
        //加入遮挡房子mesh
        for(ExtrudeBox trg:trgs){
            faces = trg.mesh.getFaces();
            for(HE_Face face:faces){polys.add(face.getPolygon());}
        }
        //新建筑加入遮挡,除去自身
        for(ExtrudeBox news:news){
                faces = news.mesh.getFaces();
                for(HE_Face face:faces){
                    if(container!=null&&!container.contains(face))
                    polys.add(face.getPolygon());
                }
        }
        for(ExtrudeBox old:olds){

            faces = old.mesh.getFaces();
            for(HE_Face face:faces){polys.add(face.getPolygon());}
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
                System.out.println("视线检测加入了地形");
            }
        }
    }
    //太阳光球
    protected HE_Mesh getSunSphere(){
        int radius=80;
        HEC_Sphere creator=new HEC_Sphere();
        creator.setRadius(radius);
        creator.setUFacets(8);
        creator.setVFacets(8);
        HE_Mesh mesh=new HE_Mesh(creator);
        HEM_Slice modifier=new HEM_Slice();
        modifier.setPlane(new WB_Plane(new WB_Point(-radius*2,-radius*2),new WB_Point(radius*2,-radius*2),new WB_Point(0,radius*2)));
        mesh.modify(modifier);

        return mesh;
    }
    //太阳光线
    protected ArrayList<WB_Segment>getSunLights(WB_Coord p){
        ArrayList<WB_Segment>lights=new ArrayList<>();
        List<HE_Face> faces = this.sunSphere.getFaces();

        for(HE_Face face : faces){
            WB_Coord f = face.getFaceCenter();
            WB_Coord newF=new WB_Point(f.xf()+p.xf(),f.yf()+p.yf(),f.zf()+p.zf());
            WB_Segment newS=new WB_Segment(newF,p);
            lights.add(newS);
        }
        return lights;

    }

    //设置要计算的新建筑，得到对应太阳光线
    public void samplePt(WB_Coord sPt) {
        this.sPt=sPt;
        this.sunLights=getSunLights(sPt);}


    //计算评价值
    @Override
    public void setEvaluation() {
        double value=0;

            // TODO:取中点检测是否被遮挡，画一个半球
            //检测日光是否被遮挡
            for(WB_Segment sunLight:sunLights){
                boolean inte=false;
                for(WB_Polygon polygon:blocks){
                    if(WB_GeometryOp3D.getIntersection3D(sunLight,polygon).intersection==true){
                        inte=true;
                        break;
                    }
                }
                //若遮挡，分数低
                value+=inte==true?0:1;
            }


        value=value/sunSphere.getNumberOfFaces();
        rate=value;
    }

    @Override
    //画光线，采光值计算
    public void display(HE_Render render,AgentPoint ap, boolean is) {
        is=drawLight;

        PApplet app = render.getApp();
        app.pushMatrix();
        app.noStroke();
        app.fill(new GradientMap(app, ColorMap.JET).getColor((float)ap.getLight()), 170);
        app.translate(ap.loc.xf(), ap.loc.yf(), ap.loc.zf());
        app.box(1.5f);
        app.popMatrix();

        if(is) {
            app.pushMatrix();
            app.stroke(255, 0, 0, 240);
            app.strokeWeight(0.5f);
            ArrayList<WB_Segment> lights = getSunLights(sPt);
            for (WB_Segment light : lights) {
                render.drawSegment(light);
            }
            app.popMatrix();
            app.pushMatrix();
            app.translate(sPt.xf(), sPt.yf(), sPt.zf());
            app.stroke(200, 200, 0);
            render.drawEdges(sunSphere);
            app.popMatrix();

        }

    }



    @Override
    public float getValue() {
        return (float)rate;
    }
}