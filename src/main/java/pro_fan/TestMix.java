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
// * @ClassName: TestMix
// * @author: wb
// * @date: 2022/7/31 16:47
// */
////综合优化计算
//public class TestMix extends PApplet{
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
//
//        geos = new ArrayList<>();
//
//        // 模拟地形
//        PImage img = loadImage("E:\\terr.jpg");
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
//        /**———————————————————————— by wb————————————————————————————————————————————————*/
//
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
//        ArrayList<ArrayList<String>>lists=new ArrayList<>();
//
//        site.setValues("testPos",geos,null);
//        site.setValues("testSight",geos,null);
//        site.setValues("testLight",geos,null);
//        ArrayList<String >apTypes=new ArrayList<>();
//        apTypes.add("testPos");
//        apTypes.add("testSight");
//        ArrayList<String >buTypes=new ArrayList<>();
//        buTypes.add("testLight");
//        site.testValue(apTypes,buTypes);
//
//        /**———————————————————————— by wb————————————————————————————————————————————————*/
//    }
//
//    // 模拟建设基地
//    private WB_Polygon creat_site() {
//        WB_Point[]points = new WB_Point[]{
//                new WB_Point(-900,-900),new WB_Point(900,-900),new WB_Point(900,900),new WB_Point(-900,900)
//        };
//        return new WB_Polygon(points);
//    }
//
//    public void draw() {
//        background(255);
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
//        PApplet.main("pro_fan.TestMix");
//    }
//}
