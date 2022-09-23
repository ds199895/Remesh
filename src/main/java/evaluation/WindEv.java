package evaluation;

import pro_fan.AgentPoint;
import pro_fan.ExtrudeBox;
import pro_fan.FanGeo;
import pro_fan.Site;
import processing.core.PApplet;
import util.render.HE_Render;
import wblut.geom.*;
import wblut.hemesh.HE_Face;
import wblut.hemesh.HE_Mesh;
import wblut.processing.WB_Render3D;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: WindEv
 * @Description:
 * @author: zqy
 * @date: 2022/7/28/028 21:15
 */
public class WindEv implements Evaluation {
    List<HE_Mesh> buildings;
    public ArrayList<ExtrudeBox> news;//新建筑

    //暂时以一个随机点进行测试
    //有一步骤：判断采样点是否在建筑内部？ 找出是哪个建筑？index？

    WB_Vector mainDir;
    WB_Coord[] drawMainDir;
    WB_Coord sPt;
    int sample_bldIndex;
    List<WB_Ray> rays;
    int divNum_ray=30;//必须是偶数
    Map<WB_Ray,Float> weight;
    List<Integer> interIndex;

    public boolean drawWind=false;

    float rate = 0;

    public WindEv(WB_Vector windDir,List<FanGeo> geos,WB_Coord sPt,int sample_bldIndex){
        getAllBuildings(geos);
        this.sPt=sPt;
        this.sample_bldIndex = sample_bldIndex;
        setPreConditions(buildings,windDir);
        rays = new ArrayList<>();
        weight = new HashMap<>();
        interIndex = new ArrayList<>();
        setEvaluation();
    }
    public WindEv(WB_Vector windDir,List<FanGeo> geos){
        rays = new ArrayList<>();
        weight = new HashMap<>();
        interIndex = new ArrayList<>();
        buildings=new ArrayList<>();
        news=new ArrayList<>();
        getAllBuildings(geos);
        setPreConditions(buildings,windDir);


    }


    public void setPreConditions(List<HE_Mesh> buildings, WB_Vector... factors) {
        //暂时只测试一个主导风向
        if (factors.length!=1)
            return;
        this.mainDir = factors[0];
        drawMainDir = new WB_Point[2];
        // TODO: 2022/7/28/028 定义主导风向画出来在哪个位置
        drawMainDir[0] = new WB_Point(0,0,0);
        drawMainDir[1] = WB_Point.add(drawMainDir[0],mainDir);

    }


    public void samplePt(WB_Coord sPt,int sample_bldIndex) {
        this.sPt = sPt;
        this.sample_bldIndex = sample_bldIndex;
    }


    @Override
    public void setEvaluation() {
        rays.clear();
        weight.clear();
        interIndex.clear();
        setRays();
        int time = 0;
        rate = 0;
        for (WB_Ray r:rays){
            boolean intersection = false;
            for (HE_Mesh m: buildings){
                if (buildings.indexOf(m)!=sample_bldIndex){
                    for (HE_Face f: m.getFaces()){
                        if(WB_GeometryOp.getIntersection3D(r,f.getPolygon()).intersection) {
                            intersection = true;
                            interIndex.add(rays.indexOf(r));
                        }
                        if (intersection==true)
                            continue;
                    }
                }
                if (intersection==true)
                    continue;
            }
            if (intersection==false){
                rate+=weight.get(r);
                time++;
            }
        }
        //计算一个总值 // 怎么样能不用每次迭代都算一次总值呢
        float sumRate=0;
        for (WB_Ray r:rays){
            sumRate+=weight.get(r);
        }
//        sumRate = sumRate; ///(rays.size()*1f)
        // rate = rate/(time*1f) 基数改成了射线的总数 + 除以权重总值
//        rate = rate/sumRate;
        System.out.println("权重总值 "+sumRate);
        System.out.println("评价值  "+rate);
        rate = rate/sumRate;
        System.out.println("评价值 归一化  "+rate);
        System.out.println("未相交数目 "+time);
    }


    protected void setRays(){
        WB_Point axis0 = new WB_Point(0,0,0);  WB_Point axis1 = new WB_Point(0,0,1);
//        rays.add(new WB_Ray(sPt,mainDir.rotateAboutAxis2P(Math.PI,axis0,axis1)));
        for (int i = 0; i < divNum_ray+1; i++) {
            double angle = Math.PI+(-0.5f*Math.PI*(divNum_ray*0.5f-i)/(divNum_ray*0.5f));
            WB_Ray temptRay = new WB_Ray(sPt,mainDir.rotateAboutAxis2P(angle,axis0,axis1));
            rays.add(i,temptRay);
            weight.put(temptRay, (float) Math.cos(Math.PI-angle));
        }
    }


    @Override
    public void display(HE_Render render, AgentPoint ap, boolean is) {
        is=drawWind;
        WB_Render3D wb_render3D = render;
        PApplet app = render.getApp();

        if (is){
            // draw mainWind
            app.pushStyle();
            app.stroke(0);
            wb_render3D.drawVector(drawMainDir[0],drawMainDir[1],10f );
            app.fill(0);
            app.pushMatrix();
            app.translate(drawMainDir[0].xf(),drawMainDir[0].yf(),drawMainDir[0].zf());
            app.sphere(1);
            app.popMatrix();
            app.popStyle();

            // draw rays
            app.pushStyle();
            for (WB_Ray r:rays){
                if (interIndex.contains(rays.indexOf(r))){
                    app.stroke(0,50);
                }else {
                    app.strokeWeight(3);
                    app.stroke(212, 82, 41);
                }
                /**——————————————————————————————————————*/

                wb_render3D.drawRay(r,(30*weight.get(r)+20));
                app.textSize(2);
                app.fill(212, 82, 41);
                app.text(weight.get(r),r.getPoint(30).xf(),r.getPoint(30).yf(),60);
            }
            app.popStyle();
        }



    }

    @Override
    public float getValue() {
        return rate;
    }

    //给场地建筑分类
    protected void getAllBuildings(List<FanGeo>geos){
        for(FanGeo geo:geos){
            if(geo instanceof ExtrudeBox){
                if(((ExtrudeBox) geo).type.type=="trgBuild")buildings.add(((ExtrudeBox) geo).mesh);
                if(((ExtrudeBox) geo).type.type=="newBuild")news.add((ExtrudeBox) geo);
                if(((ExtrudeBox) geo).type.type=="oldBuild")buildings.add(((ExtrudeBox) geo).mesh);
            }
            if(geo instanceof Site){
                this.buildings.add(((Site) geo).terrain.mesh);
            }
        }
    }
}
