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
//
///**
// * TODO: 测试程序
// *
// * @author libiao
// * @version 1.0
// * @date 2022/7/28 10:29
// * Inst. AAA, S-ARCH, Southeast University
// */
//public class Test01 extends PApplet {
//    CameraController cam;
//    HE_Render render;
//
//    // 场地上所有的几何体
//    List<FanGeo>geos;
//    Terrain cur_terr;
//    Site site;
//    public void setup() {
//        this.cam = new CameraController(this, 1000);
//        this.render = new HE_Render(this);
//
//        geos = new ArrayList<>();
//
//        // 模拟地形
//        PImage img = loadImage("E://terr.jpg");
//        WB_Polygon poly = creat_site();
//        //cur_terr = new Terrain(img,poly);
//        geos.add(cur_terr);
//
//        // 模拟既有建筑(生成随机的既有建筑)
//        for(int i=0;i<10;i++){
//            WB_Point loc = cur_terr.getLocalLocation(random(0,800),random(0,800));
//            WB_Vector dir = cur_terr.getGradient2D(loc.xd(),loc.yd());
//            ExtrudeBox builder;
//            if(random(0,1)>0.5) {// 模拟不同类型的基地既有建筑
//                builder = new ExtrudeBox(TypeColor.oldBuild, loc, dir, random(20, 30), random(40, 60), random(80, 150));
//            }else{
//                builder = new ExtrudeBox(TypeColor.trgBuild, loc, dir, random(20, 30), random(40, 60), random(80, 150));
//            }
//
//            geos.add(builder);
//        }
//
//        // 模拟建设基地
//        //site = new Site(cur_terr, 300, new WB_Point(-800,-800,0),new WB_Point(-200,-700,0),new WB_Point(-200,-200,0), new WB_Point(-700,-300,0), new WB_Point(-600,-500,0));
//        geos.add(site);
//
//        // 模拟评价函数 ***************
//        site.setValues("TestSight",geos,null);
//
//
//        // 创建新建筑
//        List<AgentPoint> agents = site.getAgents();
//        int rand = (int) (Math.random()*agents.size());
//        AgentPoint ap_random = agents.get(rand);
//        ExtrudeBox newBuilder = new ExtrudeBox(TypeColor.newBuild, ap_random.loc, Site.getRandomDirection(), 50,100,150);
//        site.setNewBuilding(newBuilder);
//        geos.add(newBuilder);
//
//        // test debug
//        site.testing_optimize();
//    }
//
//    // 模拟建设基地
//    private WB_Polygon creat_site() {
//        WB_Point[]points = new WB_Point[]{
//          new WB_Point(-900,-900),new WB_Point(900,-900),new WB_Point(900,900),new WB_Point(-900,900)
//        };
//        return new WB_Polygon(points);
//    }
//
//    public void draw() {
//        background(255);
//
//        if(opti){
//            site.testing_optimize();
//        }
//
//        for(FanGeo geo:geos){
//            geo.draw(render);
//        }
//
//        cam.drawSystem(100);
//    }
//
//    boolean opti = false;
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
//        PApplet.main("pro_fan.Test01");
//    }
//
//
//}
