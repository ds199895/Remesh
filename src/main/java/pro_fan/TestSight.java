package pro_fan;

import guo_cam.CameraController;
import processing.core.PApplet;
import processing.event.KeyEvent;
import util.render.HE_Render;
import wblut.geom.WB_Point;
import wblut.geom.WB_Polygon;
import wblut.geom.WB_Vector;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: TestSight
 * @author: wb
 * @date: 2022/7/30 16:14
 */

//todo:1.site的polygon改成顺应地形 2.输出点到rhino

public class TestSight extends PApplet {
    CameraController cam;
    HE_Render render;

    // 场地上所有的几何体
    List<FanGeo>geos;
    Terrain cur_terr;
    Site site;


    public void setup() {
        this.cam = new CameraController(this, 1000);
        this.render = new HE_Render(this);

        geos = new ArrayList<>();

        // 模拟地形
        String file="E:\\infosim.3dm";

        cur_terr = new Terrain(file);
        geos.add(cur_terr);

        // 模拟既有建筑(导入既有建筑)
        for(ArrayList<WB_Polygon> polys:cur_terr.barrierpolyss){
            ExtrudeBox builder;
            builder = new ExtrudeBox(TypeColor.oldBuild, polys);

            geos.add(builder);
        }
        for(ArrayList<WB_Polygon> polys:cur_terr.targetpolyss){
            ExtrudeBox builder;
            builder = new ExtrudeBox(TypeColor.trgBuild, polys);

            geos.add(builder);
        }

        for(ArrayList<WB_Polygon> polys:cur_terr.waterpolyss){
            ExtrudeBox builder;
            builder = new ExtrudeBox(TypeColor.water, polys);

            geos.add(builder);
        }

        // 模拟建设基地
        site = new Site(cur_terr, 20, cur_terr.archiPoly,9);
        geos.add(site);

        WB_Point locHall = new WB_Point(-15f,-100f);
        locHall.setZ(site.terrain.getLocalLocation(locHall.xd(),locHall.yd()).zd());
        ExtrudeBox hall = new ExtrudeBox(TypeColor.newBuild, locHall, new WB_Vector(1,-0.08f,0), 20,17,15);
        site.addNewBuilding(hall);
        geos.add(hall);

        //site.setValues("testSight",geos);
        site.setValues("testPos",geos);


   }



    public void draw() {
        background(0);
        directionalLight(255, 255, 255, 1, 1, -1);
        directionalLight(127, 127, 127, 0, 0, 1);
        for(int i=0;i<geos.size();i++){
            if(i==0){
                    geos.get(i).draw(render);
            }else{
                    for(ArrayList<WB_Polygon >ps:cur_terr.detailpolyss){
                        for(WB_Polygon p:ps) {
                            fill(200);
                            render.drawPolygonEdges(p);
                        }
                    }
                    }
                }

        for(FanGeo geo:geos){
            geo.draw(render);
        }

        cam.drawSystem(100);
    }





    @Override
    public void keyReleased(KeyEvent event) {

        if(key=='b'){
            site.manager.sightEv.drawSight=!site.manager.sightEv.drawSight;
        }
        if(key=='c'){
            site.manager.posEv.drawPos=!site.manager.posEv.drawPos;
        }
    }


    public void settings() {
        size(1000,1000,P3D);
    }

    public static void main(String[] args) {
        PApplet.main("pro_fan.TestSight");
    }



}
