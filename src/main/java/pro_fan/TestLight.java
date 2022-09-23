package pro_fan;//package pro_fan;
//
//import guo_cam.CameraController;
//import processing.core.PApplet;
//import processing.core.PImage;
//import processing.event.KeyEvent;
//import util.render.HE_Render;
//import wblut.geom.WB_AABBTree;
//import wblut.geom.WB_Point;
//import wblut.geom.WB_Polygon;
//import wblut.geom.WB_Vector;
//
//import java.util.ArrayList;
//import java.util.List;
///**
// * @ClassName: TestLight
// * @author: wb
// * @date: 2022/7/31 11:14
// */
//public class TestLight extends PApplet {
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
//        site = new Site(cur_terr, 12, cur_terr.archiPoly,4);
//        geos.add(site);
//
//        /**———————————————————————— by wb————————————————————————————————————————————————*/
//
//        // 创建新建筑List
//        List<AgentPoint> agents = site.getAgents();
//        int rand = (int) (Math.random()*agents.size());
//        AgentPoint ap_random = agents.get(rand);
//        ExtrudeBox hall = new ExtrudeBox(TypeColor.newBuild, ap_random.loc, new WB_Vector(10,0), 20,20,8);
//
//        rand = (int) (Math.random()*agents.size());
//        ap_random = agents.get(rand);
//        ExtrudeBox out = new ExtrudeBox(TypeColor.newBuild, ap_random.loc, new WB_Vector(10,0), 15,30,4);
//
//        rand = (int) (Math.random()*agents.size());
//        ap_random = agents.get(rand);
//        ExtrudeBox office = new ExtrudeBox(TypeColor.newBuild, ap_random.loc, new WB_Vector(10,0), 15,15,4);
//
//        rand = (int) (Math.random()*agents.size());
//        ap_random = agents.get(rand);
//        ExtrudeBox equip = new ExtrudeBox(TypeColor.newBuild, ap_random.loc, new WB_Vector(10,0), 15,15,3);
//
//        site.addNewBuilding(hall);
//        site.addNewBuilding(out);
//        site.addNewBuilding(office);
//        site.addNewBuilding(equip);
//        for(ExtrudeBox box: site.newBuildings)geos.add(box);
//
//        // 模拟评价函数 ***************
//        site.setValues("testLight",geos,null);
//
//        /**———————————————————————— by wb————————————————————————————————————————————————*/
//    }
//
//
//
//    public void draw() {
//        background(255);
//
//
//        /**———————————————————————— by wb————————————————————————————————————————————————*/
//        if(opti){
//            site.testing_optimize("testLight",geos);
//        }
//        /**———————————————————————— by wb————————————————————————————————————————————————*/
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
//
//        if(key=='s'){
//            drawTerrain=!drawTerrain;
//        }
//        if(key=='h'){
//            cur_terr.heightview=!cur_terr.heightview;
//        }
//        if(key=='d'){
//            viewDetail=!viewDetail;
//            if(!viewDetail){
//                cur_terr.mesh=cur_terr.analysisMesh;
//                cur_terr.colors=cur_terr.analysisColors;
//            }else{
//                cur_terr.mesh=cur_terr.allMesh;
//                cur_terr.colors=cur_terr.allColors;
//            }
//            cur_terr.tree=new WB_AABBTree(cur_terr.mesh, 1);
//
//        }
//    }
//
//    public void settings() {
//        size(1000,1000,P3D);
//    }
//
//    public static void main(String[] args) {
//        PApplet.main("pro_fan.TestLight");
//    }
//
//
//}
