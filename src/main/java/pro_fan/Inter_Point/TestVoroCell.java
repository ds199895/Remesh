package pro_fan.Inter_Point;

import guo_cam.CameraController;
import processing.core.PApplet;
import util.render.HE_Render;
import wblut.geom.*;
import wblut.hemesh.HE_Mesh;
import wblut.nurbs.WB_BSpline;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO
 *
 * @author libiao
 * @version 1.0
 * @date 2022/8/11 15:15
 * Inst. AAA, S-ARCH, Southeast University
 */
public class TestVoroCell extends PApplet {
    CameraController cam;
    HE_Render render;

    HE_Mesh mesh;
    HE_Mesh dual;

    List<VoroCell>vcs;

    VoroCell vc;
    public void setup() {
        this.cam = new CameraController(this, 3000);
        this.render = new HE_Render(this);

        // test 01 二维平面上生成 Voronoi_Cells >>>>>>>>>>>>>>>>>>
        WB_Polygon range = setBoundary();
        WB_Transform3D tran = new WB_Transform3D();
        tran.addTranslate(new WB_Point(1000,1000,0));
        range.applySelf(tran);
        WB_PolyLine curve = createDisturbCurve(range);
        // key set >>>>>>
        Interferon  interferon = new Inter_Point(range.getCenter(),  600,2);

        List<Interferon>interferons=new ArrayList<>();
//        interferons.add(interferon);
        interferon = new Inter_LineSting(curve, 500, 4);
        interferons.add(interferon);

        vc = new VoroCell(range, interferons, 10, 40,10,80);
        // key set <<<<<<
        // <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<


        // test hemesh: 三维空间生成 Voronoi_Cells >>>>>>>>>>>>>>>>>>>>>>>>>>>
//        mesh = new HE_Mesh(new HEC_Cube().setEdge(1500));
//        HEC_Dual creator = new HEC_Dual();
//        creator.setSource(mesh);
//        dual = new HE_Mesh(creator);
//        WB_AABB aabb = dual.getAABB();
//
//        vcs = new ArrayList<>();
//        for(HE_Face he_face: dual.getFaces()){
//
//            // key set >>>>>>
//            VoroCell vc = new VoroCell(he_face, 20,400);
//            vc.setAPValuesByZ((float)aabb.getMinZ(), (float)aabb.getMaxZ());
//            vc.setVoronoiCells(10,30);
//            // key set <<<<<<
//
//            vcs.add(vc);
//        }
        // <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    }

    @Override
    public void draw() {
        background(255);
        this.directionalLight(255, 255, 255, 1, 1, 2);
        this.ambientLight(100, 100, 100, -1, -1, 0.5f);


        vc.draw(render);


//        for(VoroCell v:vcs) {
//            v.draw(render);
//        }

        stroke(0);
        noFill();
        render.drawFaces(dual);

        cam.drawSystem(3000);
    }
    public void keyReleased() {
        if (key == 't') {
            cam.top();
        }
        if (key == 'p') {
            cam.perspective();
        }
    }
    int w = 1800;
    public void settings() {
        size(w,(int)(w*9./16.),P3D);
    }

    public static void main(String[] args) {
        PApplet.main("pro_fan.Inter_Point.TestVoroCell");
    }

    private WB_Point[]controlpts=new WB_Point[3];
    public WB_PolyLine createDisturbCurve(WB_Polygon boundary) {
        List<WB_Point>mids=new ArrayList<>();
        for(int i=0;i<boundary.getNumberSegments()+1;i++){
            mids.add(boundary.getSegment(i).getCenter());
        }
        List<Integer>index=new ArrayList<>();
        for(int i=0;i<3;i++){
            int a= (int) random(boundary.getNumberSegments()+1);
            while(index.contains(a)){
                a= (int) random(boundary.getNumberSegments()+1);
            }
            index.add(a);
        }

        for(int i=0;i<index.size();i++){
            controlpts[i]=mids.get(index.get(i));
        }
//        controlpts[3]=boundary.getCenter();
        println(controlpts);
//        WB_Point[] controlpts = new WB_Point[]{
//                new WB_Point(50, 500),
//                new WB_Point(200, 200),
//                new WB_Point(700, 520),
//                new WB_Point(900, 100)
//        };


        return createBspline(controlpts, 2, 5);
    }

    public WB_PolyLine createBspline(WB_Coord[] controlpts, int order, int steps) {
        WB_BSpline spline = new WB_BSpline(controlpts, order);
        int n = Math.max(1, steps);
        WB_Point p0 = spline.getPointOnCurve(0.0D);
        double du = 1.0D / (double) n;
        List<WB_Point> divPts = new ArrayList<>();
        divPts.add(p0);
        for (int i = 0; i < n; ++i) {
            WB_Point p1 = spline.getPointOnCurve((double) (i + 1) * du);
            divPts.add(p1);
        }
        return WB_GeometryFactory.instance().createPolyLine(divPts);
    }

    public WB_Polygon  setBoundary() {
        List<WB_Point> points = new ArrayList<>();
        WB_Polygon bound;
        points.add(new WB_Point(100, 50));
        points.add(new WB_Point(2000, 100));
        points.add(new WB_Point(2500, 2000));
        points.add(new WB_Point(200, 1600));
        points.add(new WB_Point(100, 50));
//        List<WB_Point> holepts = new ArrayList<>();
//        holepts.add(new WB_Point(200, 150));
//        holepts.add(new WB_Point(400, 200));
//        holepts.add(new WB_Point(300, 600));
//        bound=gf.createPolygonWithHole(points,holepts);
        bound = WB_GeometryFactory.instance().createSimplePolygon(points);
        return bound;
    }
}
