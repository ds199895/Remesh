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
 * @ClassName: TestWind
 * @Description:
 * @author: zqy
 * @date: 2022/7/29/029 15:41
 */
public class TestWind extends PApplet {
    CameraController cam;
    HE_Render render;

    // 场地上所有的几何体
    List<FanGeo> geos;
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
        site = new Site(cur_terr, 8, cur_terr.archiPoly,3);
        geos.add(site);


        WB_Point locHall = new WB_Point(-15f,-100f);
        locHall.setZ(site.terrain.getLocalLocation(locHall.xd(),locHall.yd()).zd());
        ExtrudeBox hall = new ExtrudeBox(TypeColor.newBuild, locHall, new WB_Vector(1,-0.08f,0), 20,17,12);


        locHall.setZ(site.terrain.getLocalLocation(locHall.xd(),locHall.yd()).zd()+13);
        ExtrudeBox hall1 = new ExtrudeBox(TypeColor.newBuild, locHall, new WB_Vector(1,-0.08f,0), 20,17,12);


// 卫生间+零售 400 通风（视线x） 通风优先级更高
        WB_Point locOut = new WB_Point(-22f,-140f);
        locOut.setZ(site.terrain.getLocalLocation(locOut.xd(),locOut.yd()).zd());
        ExtrudeBox out = new ExtrudeBox(TypeColor.newBuild, locOut,  new WB_Vector(1,0.06f,0), 25,16,4);

//办公vip   250  通风（视线x）
        WB_Point locOffice = new WB_Point(-52f,-116f);
        locOffice.setZ(site.terrain.getLocalLocation(locOffice.xd(),locOffice.yd()).zd());
        ExtrudeBox office = new ExtrudeBox(TypeColor.newBuild,locOffice,  new WB_Vector(0.05f,1,0), 20,12,4);

 //设备辅助   250  都不需要
        WB_Point locEquip = new WB_Point(-42f,-95f);//-50f,-108f
        locEquip.setZ(site.terrain.getLocalLocation(locEquip.xd(),locEquip.yd()).zd());
        ExtrudeBox equip = new ExtrudeBox(TypeColor.newBuild, locEquip,  new WB_Vector(1,0,0), 20,12,4);

        site.addNewBuilding(hall);
        site.addNewBuilding(equip);
        site.addNewBuilding(office);
        site.addNewBuilding(out);
        site.addNewBuilding(hall1);
        for(ExtrudeBox box: site.newBuildings)geos.add(box);

        //site.setValues("testWind",geos);
        site.setValues("testLight",geos);

    }




    public void draw() {
        background(255);
        //lights();

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

    boolean opti = false;

    @Override
    public void keyReleased(KeyEvent event) {

        if(key=='1'){
            opti = !opti;
        }
        if(key=='r'){ 
            site.manager.windEv.drawWind=!site.manager.windEv.drawWind;
        }
        if(key=='a'){
            site.manager.lightEv.drawLight=!site.manager.lightEv.drawLight;
        }
    }


    public void settings() {
        size(1000,1000,P3D);
    }

    public static void main(String[] args) {
        PApplet.main("pro_fan.TestWind");
    }
}
