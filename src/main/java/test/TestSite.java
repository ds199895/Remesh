package test;//package test;
//
//
//import evaluation.WindEv;
//import guo_cam.CameraController;
//import org.locationtech.jts.geom.GeometryFactory;
//import processing.core.PApplet;
//import util.render.HE_Render;
//import wblut.geom.WB_GeometryOp;
//import wblut.geom.WB_GeometryOp3D;
//import wblut.geom.WB_Point;
//import wblut.geom.WB_Vector;
//import wblut.hemesh.HEC_Cube;
//import wblut.hemesh.HET_Diagnosis;
//import wblut.hemesh.HE_Mesh;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * @ClassName: TestSite
// * @Description:
// * @author: zqy
// * @date: 2022/7/28/028 21:12
// */
//public class TestSite extends PApplet {
//    public static void main(String[] args) {PApplet.main("test.TestSite");}
//    HE_Render render;
//    CameraController cam;
//    List<HE_Mesh> buildings;
//    WindEv windEv;
//
//    @Override
//    public void settings(){
//        size(1300,900,P3D);
//    }
//
//    @Override
//    public void setup(){
//        render = new HE_Render(this);
//        cam = new CameraController(this,1000);
//        setBuildings();
//        windEv = new WindEv(new WB_Vector(1,2,0),buildings,buildings.get(1).getFaceCenter(2),1);
//    }
//
//    public void setBuildings(){
//        buildings = new ArrayList<>();
//        HEC_Cube creator=new HEC_Cube();
//        creator.setEdge(50);
//        creator.setWidthSegments(1).setHeightSegments(1).setDepthSegments(1);
//        creator.setCenter(new WB_Point(50,0,0));
//        HE_Mesh m1 = new HE_Mesh(creator);
//        HET_Diagnosis.validate(m1);
////        WB_GeometryOp.getDistance3D(new WB_Point(),m1.getAABB());
//
//        buildings.add(m1);
//
//        HEC_Cube creator2=new HEC_Cube();
//        creator2.setEdge(50);
//        creator2.setWidthSegments(1).setHeightSegments(1).setDepthSegments(1);
//        creator2.setCenter(new WB_Point(80,120,20));
//        HE_Mesh m2 = new HE_Mesh(creator2);
//        HET_Diagnosis.validate(m2);
//
//        buildings.add(m2);
//    }
//
//    @Override
//    public void draw(){
//        background(255);
//        cam.drawSystem(100);
//        windEv.display(render);
//    }
//
//}
