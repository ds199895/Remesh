package pro_fan;//package pro_fan;
//
//import guo_cam.CameraController;
//import processing.core.PApplet;
//import processing.core.PImage;
//import processing.event.KeyEvent;
//import util.render.HE_Render;
//import wblut.geom.WB_Point;
//import wblut.geom.WB_Polygon;
//import wblut.geom.WB_Vector;
//
//import java.util.ArrayList;
//import java.util.List;
///**
// * @ClassName: TestPos
// * @author: wb
// * @date: 2022/7/31 14:06
// */
//public class TestPos extends PApplet {
//    CameraController cam;
//    HE_Render render;
//
//    // 场地上所有的几何体
//    List<FanGeo> geos;
//    Terrain cur_terr;
//    Site site;
//    public void setup() {
//        this.cam = new CameraController(this, 1000);
//        this.render = new HE_Render(this);
//        geos = new ArrayList<>();
//
//        // 模拟地形
//        String file="E:\\infosim.3dm";
//
//        cur_terr = new Terrain(file);
//        geos.add(cur_terr);
//
//        // 模拟既有建筑(导入既有建筑)
//        for(ArrayList<WB_Polygon> polys:cur_terr.barrierpolyss){
//            ExtrudeBox builder;
//            builder = new ExtrudeBox(TypeColor.oldBuild, polys);
//
//            geos.add(builder);
//        }
//        for(ArrayList<WB_Polygon> polys:cur_terr.targetpolyss){
//            ExtrudeBox builder;
//            builder = new ExtrudeBox(TypeColor.trgBuild, polys);
//
//            geos.add(builder);
//        }
//
//        for(ArrayList<WB_Polygon> polys:cur_terr.waterpolyss){
//            ExtrudeBox builder;
//            builder = new ExtrudeBox(TypeColor.water, polys);
//
//            geos.add(builder);
//        }
//
//        // 模拟建设基地
//        site = new Site(cur_terr, 12, cur_terr.archiPoly,8);
//        geos.add(site);
//
//
//        /**———————————————————————— by wb————————————————————————————————————————————————*/
//
//        // 创建新建筑List
//        List<AgentPoint> agents = site.getAgents();
//        int rand = (int) (Math.random()*agents.size());
//        AgentPoint ap_random = agents.get(rand);
//        ExtrudeBox hall = new ExtrudeBox(TypeColor.newBuild, ap_random.loc, Site.getRandomDirection(), 20,20,8);
//
//        rand = (int) (Math.random()*agents.size());
//        ap_random = agents.get(rand);
//        ExtrudeBox out = new ExtrudeBox(TypeColor.newBuild, ap_random.loc, Site.getRandomDirection(), 15,30,4);
//
//        rand = (int) (Math.random()*agents.size());
//        ap_random = agents.get(rand);
//        ExtrudeBox office = new ExtrudeBox(TypeColor.newBuild, ap_random.loc, Site.getRandomDirection(), 15,15,4);
//
//        rand = (int) (Math.random()*agents.size());
//        ap_random = agents.get(rand);
//        ExtrudeBox equip = new ExtrudeBox(TypeColor.newBuild, ap_random.loc, Site.getRandomDirection(), 15,15,3);
//
//        site.addNewBuilding(hall);
//        site.addNewBuilding(out);
//        site.addNewBuilding(office);
//        site.addNewBuilding(equip);
//        for(ExtrudeBox box: site.newBuildings)geos.add(box);
//
//        site.setValues("testSight",geos,null);
//        // test debug
//
//        /**———————————————————————— by wb————————————————————————————————————————————————*/
//    }
//
//
//    public void draw() {
//        background(255);
//        directionalLight(255, 255, 255, 1, 1, -1);
//        directionalLight(127, 127, 127, 0, 0, 1);
//
//
//        if(opti){
//            site.testing_optimize("testPos",geos);
//
//        }
//
//        for(int i=0;i<geos.size();i++){
//            if(i==0){
//                if(drawTerrain){
//                    geos.get(i).draw(render);
//                }
//            }else{
//                if(viewDetail){
//                    for(ArrayList<WB_Polygon >ps:cur_terr.detailpolyss){
//                        for(WB_Polygon p:ps) {
//                            fill(200);
//                            render.drawPolygonEdges(p);
//                        }
//                    }
//                    //site.newBuilding.draw(render);
//                }else{
//                    geos.get(i).draw(render);
//                    for(ArrayList<WB_Polygon >ps:cur_terr.roadpolyss){
//                        for(WB_Polygon p:ps) {
//                            fill(200);
//                            render.drawPolygonEdges(p);
//                        }
//                    }
//                }
//
//            }
//        }
//
//        cam.drawSystem(100);
//    }
//
//    boolean opti = false;
//    boolean drawTerrain=true;
//    boolean viewDetail=false;
//    @Override
//    public void keyReleased(KeyEvent event) {
//        if(key=='1'){
//            opti = !opti;
//        }
//    }
//
//    public void settings() {
//        size(1000,1000,P3D);
//    }
//
//    public static void main(String[] args) {
//        PApplet.main("pro_fan.TestPos");
//    }
//
//}
