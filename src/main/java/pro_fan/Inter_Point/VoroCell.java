package pro_fan.Inter_Point;

import pro_fan.AgentPoint;
import pro_fan.Comman;
import pro_fan.Evaluation;
import pro_fan.Utils;
import processing.core.PApplet;
import util.render.HE_Render;
import wblut.geom.*;
import wblut.hemesh.*;
import wblut.math.WB_Epsilon;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * TODO
 *
 * @author libiao
 * @version 1.0
 * @date 2022/8/11 12:25
 * Inst. AAA, S-ARCH, Southeast University
 */
public class VoroCell {
    // voronoi 细胞的之间距离的阈值
    private double minSize, maxSize;
    // 干扰源
    private List<Interferon> interferons = null;
    // 生成细胞集合的 xy 平面范围
    private WB_Polygon range;

    // 生成 voronoi 细胞的中心点
    private ArrayList<AgentPoint> voro_centers;

    // 最终生成的立面
    private HE_Mesh cellMesh;

    /** 有干扰源的构造函数：
     * range-生成cells的范围（位于 xy 平面上）,
     * interferon- 干扰源,
     * extrudeMin- 平面突起的最小值,
     * extrudeMax- 平面突起的最大值
     * */
    public VoroCell(WB_Polygon range, List<Interferon> interferons, double extrudeMin, double extrudeMax,double minSize,double maxSize){
        this.range = range;

        this.interferons = interferons;

        this.voro_centers = new ArrayList<>();
        this.cellMesh = new HE_Mesh();
        this.minSize=minSize;
        this.maxSize=maxSize;

        this.initPoints(this.interferons);

        this.setVoronoiCells(extrudeMin, extrudeMax);
    }

    /** 无干扰源的构造函数：
     * face: HE_Mesh 实例的 面（HE_Face）,
     * minSize: 生成细胞的最小间距,
     * maxSize: 生成细胞的最大间距,
     * */
    public VoroCell(HE_Face face, double minSize, double maxSize){
        this.rotateToXY(face);

        this.minSize = minSize;
        this.maxSize = maxSize;

        this.voro_centers = new ArrayList<>();
        this.cellMesh = new HE_Mesh();

        this.initPoints();

    }


    // 空间旋转相关参数记录
    private double rotateAngle = 0;
    private WB_Vector rotateAxis;
    private WB_Point faceCenter = null;
    /** 将 face 面转至 xy 投影平面 */
    private void rotateToXY(HE_Face face) {
        faceCenter = new WB_Point(face.getFaceCenter());
        WB_Vector faceNormal = new WB_Vector(face.getFaceNormal());
        faceNormal.normalizeSelf();
        WB_Vector zAxis = new WB_Vector(0, 0, 1);
        rotateAxis = faceNormal.cross(zAxis);
        double dot = zAxis.dot(faceNormal);

        if (Comman.same(dot, 1, Comman.EPS)) {//range 与 z轴 垂直, 面朝上
            rotateAngle = 0;
            this.range = face.getPolygon();
        }else if(Comman.same(dot, -1, Comman.EPS)){//range 与 z轴 垂直, 面朝下
            rotateAngle = Math.PI;
            this.range = face.getPolygon();
        }else{
            this.rotateAngle = Math.acos(dot);

            WB_Transform3D tran = new WB_Transform3D();
            tran.addRotateAboutAxis(rotateAngle, faceCenter,rotateAxis);

            this.range = face.getPolygon();
            this.range.applySelf(tran);
        }

//        this.range = this.range.toPolygon2D();
    }

    /** 生成无干扰源实例的初始点 */
    private void initPoints(){
        WB_AABB aabb = this.range.getAABB();

        double signedArea = this.range.getSignedArea();
        signedArea = signedArea>0 ? signedArea : -signedArea;


        // 建构 tree 储存过载的 中心点
        int initLargeNumOfPoints = (int) (signedArea/(minSize*minSize));
        System.out.println("initLargeNumOfPoints   "+initLargeNumOfPoints);


        if(initLargeNumOfPoints<3)return;
        while(voro_centers.size()<initLargeNumOfPoints){
            double rx = aabb.getMinX()+Math.random()*aabb.getWidth();
            double ry = aabb.getMinY()+Math.random()*aabb.getHeight();
            WB_Point tmp_point = new WB_Point(rx,ry);

            if(WB_GeometryOp2D.contains2D(tmp_point, this.range)){
                AgentPoint agentPoint = new AgentPoint(Evaluation.distance, tmp_point);
                agentPoint.setValue(Evaluation.distance, 0.5);
                voro_centers.add(agentPoint);
            }

        }
        System.out.println("voro_centers   "+voro_centers.size());
    }

    /** 设置无干扰源的点的 values 值从上往下褪韵变化*/
    public void setAPValuesByZ(float min, float max){
        // 转至三维空间并计算 AP 的 值（0-1之间）
        WB_Transform3D tran = new WB_Transform3D();
        tran.addRotateAboutAxis(-rotateAngle,faceCenter,rotateAxis);
        for(AgentPoint ap:voro_centers){
            ap.loc.applySelf(tran);

            float zMap = Utils.map(ap.loc.zf(), min, max, 0, 1);
            ap.setValue(Evaluation.distance, zMap);
        }
    }

    private void subInitPoints(){
        // 转至 xy 平面
        WB_Transform3D tran = new WB_Transform3D();
        tran.addRotateAboutAxis(rotateAngle,faceCenter,rotateAxis);
        for(AgentPoint ap:voro_centers){
            ap.loc.applySelf(tran);
        }

        WB_KDTreeInteger2D<AgentPoint> tree = new WB_KDTreeInteger2D<>(16);
        int c = 0;
        for(AgentPoint ap:this.voro_centers){
            tree.add(ap,c);
            c++;
        }

        // 移除距离太近的点
        boolean tooNear = true;
        while(tooNear) {
            tooNear = false;

            WB_KDTreeInteger2D.WB_KDEntryInteger[]pointsInRange = new WB_KDTreeInteger2D.WB_KDEntryInteger[1];
            AgentPoint cur_ap = null;
            for (int i = 0; i < voro_centers.size(); i++) {
                cur_ap = voro_centers.get(i);
                double value = cur_ap.getValue(Evaluation.distance.name());
                float realSize = Utils.map((float) value, 0, 1, (float) this.minSize, (float) this.maxSize);
                pointsInRange = tree.getRange(cur_ap, realSize);
                if(pointsInRange.length > 1){
                    tooNear = true;
                    break;
                }
            }

            if(pointsInRange.length>1) {
                for (int i = 0; i < pointsInRange.length; i++) {
                    AgentPoint ap = (AgentPoint) pointsInRange[i].coord;
                    if(ap != cur_ap) {
                        voro_centers.remove(ap);
                    }
                }
            }

            // 重构 tree
            tree = new WB_KDTreeInteger2D<>(16);
            for(int i=0;i<voro_centers.size();i++){
                AgentPoint agentPoint = voro_centers.get(i);
                tree.add(agentPoint,i);
            }
        }
//        HE_Mesh voronoi = this.getVoronoi();
    }

    /** 生成立面的 Voronoi_Cells */
    public HE_Mesh setVoronoiCells(double minExtrudeLength, double maxExtrudeLength) {
        if(this.interferons==null) { // 无干扰源
            this.subInitPoints();
        }
        HE_Mesh voro_base_mesh = this.getVoronoi();

        List<WB_Polygon> SecondPolys = new ArrayList<>();
        List<WB_Polygon> allPolys = new ArrayList<>();

        List<WB_Polygon> FirstPolys = voro_base_mesh.getPolygonList();
        for (WB_Polygon p : FirstPolys) {
            List<WB_Point> inner = new ArrayList<>();
            for (int i = 0; i < p.getNumberSegments() + 1; i++) {
                WB_Point mid = p.getSegment(i).getCenter();
                inner.add(mid);
            }
            inner.add(p.getSegment(0).getCenter());
            WB_Polygon innerp = WB_GeometryFactory.instance().createSimplePolygon(inner);
            SecondPolys.add(innerp);
        }

        List<WB_Polygon> triPolys = this.getTriangles(voro_base_mesh);
        allPolys.addAll(triPolys);
        allPolys.addAll(SecondPolys);
        HE_Mesh meshNew = new HE_Mesh(new HEC_FromPolygons().setPolygons(allPolys));

        this.extrude(meshNew, minExtrudeLength, maxExtrudeLength);

        if(interferons==null) { // 无干扰源
            WB_Transform3D tran = new WB_Transform3D();
            tran.addTranslate(new WB_Point(0, 0, faceCenter.zd()-8)); //????
            tran.addRotateAboutAxis(-rotateAngle, faceCenter, rotateAxis);
            meshNew.applySelf(tran);
        }

        this.cellMesh = meshNew;
        return this.cellMesh;
    }

    /**立面凸起
     * @return*/
    private void extrude(HE_Mesh mesh, double minExtrudeLength, double maxExtrudeLength) {
        Set<HE_Vertex> extrudePt = new HashSet<>();

        WB_CoordCollection points = range.getPoints();
        WB_PolyLine pl=new WB_PolyLine(points.toArray());
        for (HE_Face f : mesh.getFaces()) {
            if (f.getFaceVertices().size() > 3) {
                for (HE_Vertex v : f.getFaceVertices()) {
                    if(!(v.getPosition().getDistance(WB_GeometryOp.getClosestPoint2D(v,pl))< WB_Epsilon.EPSILON))
                        extrudePt.add(v);
                }
            }

        }

        if(this.interferons!=null) {
            for (HE_Vertex V : extrudePt) {
                V.setZ(V.zd() + getExtrudeLength(this.interferons, V, minExtrudeLength, maxExtrudeLength)); // + - 决定法向量
            }
        }else{
            for (HE_Vertex V : extrudePt) {
                V.setZ(V.zd() + maxExtrudeLength); // + - 决定法向量
            }
        }

        WB_Polygon basePoly = toPoly2D(this.range);
        HE_Mesh boundaryMesh=new HE_Mesh(new HEC_Polygon(basePoly,2));
        for(HE_Face f:boundaryMesh.getFaces()){
            if(WB_GeometryOp.contains2D(basePoly.getCenter(), f.getPolygon())){
                boundaryMesh.remove(f);
                break;
            }
        }
        mesh.add(boundaryMesh);

    }

    private WB_Polygon toPoly2D(WB_Polygon ori_poly){
         List<WB_Point>newPoints = new ArrayList<>();
        int numberOfPoints = ori_poly.getNumberOfPoints();
        for(int i=0;i<numberOfPoints;i++){
            WB_Point point = ori_poly.getPoint(i);
            newPoints.add(new WB_Point(point.xd(),point.yd(),0));
        }

        return Comman.gf_hemesh.createSimplePolygon(newPoints);

    }

    /**立面凸起*/
    private void extrude_(HE_Mesh mesh, double minExtrudeLength, double maxExtrudeLength) {
        Set<HE_Vertex> polyVs = new HashSet<>();

        for (HE_Face f : mesh.getFaces()) {
            if (f.getFaceVertices().size() > 3) {
                for (HE_Vertex v : f.getFaceVertices()) {
                    polyVs.add(v);
                }
            }
        }

        List<HE_Vertex> extrudePt = mesh.getVertices();
        extrudePt.removeAll(polyVs);

        if(this.interferons!=null) {
            for (HE_Vertex V : extrudePt) {
                V.setZ(V.zd() + getExtrudeLength(this.interferons, V, minExtrudeLength, maxExtrudeLength)); // + - 决定法向量
            }
        }else{
            for (HE_Vertex V : extrudePt) {
                V.setZ(V.zd() + maxExtrudeLength); // + - 决定法向量
            }
        }

    }

    private double getExtrudeLength(List<Interferon> interferons, HE_Vertex v, double minExtrudeLength, double maxExtrudeLength) {
        WB_Point cen = v.getPosition();
        double r=0;
        double co=0;
        for(Interferon interferon:interferons){
            double d = interferon.getDistance(cen);//cen.getDistance(WB_GeometryOp.getClosestPoint2D(cen, line));
            double r1 = 0;
            if (d <= interferon.influenceDistance){
                r1 = d / interferon.influenceDistance * (maxExtrudeLength - minExtrudeLength) + minExtrudeLength;  //计算映射后的值
            } else {
                r1 = maxExtrudeLength;  //按最大值来算
            }
            r1*=interferon.coefficient;
            r+=r1;
            co+=interferon.coefficient;
        }
        r/=co;

        return r;
    }

    /**立面剖分三角面*/
    private List<WB_Polygon> getTriangles(HE_Mesh mesh) {
        List<WB_Polygon> newTris = new ArrayList<>();
        for (HE_Vertex v : mesh.getVertices()) {
            List<WB_Point> triP = new ArrayList<>();

            if (v.getHalfedgeStar().size() >= 3) {

                for (HE_Halfedge e : v.getHalfedgeStar()) {
                    triP.add(new WB_Point(e.getCenter()));
                }

            } else {
                for (HE_Halfedge e : v.getHalfedgeStar()) {
                    triP.add(new WB_Point(e.getCenter()));
                }
                triP.add(new WB_Point(v));

            }
            WB_Polygon tripoly = Comman.gf_hemesh.createSimplePolygon(triP);
            WB_Point center = tripoly.getCenter();

            for (int i = 0; i < tripoly.getNumberSegments() + 1; i++) {
                List<WB_Point> newp = new ArrayList<>();
                newp.add(new WB_Point(tripoly.getSegment(i).getOrigin()));
                newp.add(new WB_Point(tripoly.getSegment(i).getEndpoint()));
                newp.add(center);
                newTris.add(Comman.gf_hemesh.createSimplePolygon(newp));
            }

        }
        return newTris;
    }

    /** 生成基准voronoi
     * @return*/
    private HE_Mesh getVoronoi() {
//        public HE_Mesh setVoronoi(List<WB_Point> centers, WB_Polygon poly) {
//        HE_Mesh meshp;
        List<WB_Point> p_temp = new ArrayList<>();
        for (AgentPoint pv : this.voro_centers) {
            p_temp.add(pv.loc);
        }
        List<WB_VoronoiCell2D> Vor = WB_VoronoiCreator.getClippedVoronoi2D(p_temp, this.range).getCells();

        List<WB_Polygon> lp = new ArrayList<WB_Polygon>();
        for (WB_VoronoiCell2D vor : Vor) {
            lp.add(vor.getPolygon());
        }

        HEC_FromPolygons hecp = new HEC_FromPolygons();
        hecp.setPolygons(lp);

        return new HE_Mesh(hecp);
    }


    /** 生成有干扰源实例的初始点 */
    private void initPoints(List<Interferon> interferons){
        WB_AABB aabb = this.range.getAABB();

        double signedArea = this.range.getSignedArea();
        signedArea = signedArea>0 ? signedArea : -signedArea;

        // 建构 tree 储存过载的 中心点
        int initLargeNumOfPoints = (int) (signedArea*2/(minSize*minSize));
        System.out.println("initLargeNumOfPoints   "+initLargeNumOfPoints);

        WB_KDTreeInteger2D<AgentPoint> tree = new WB_KDTreeInteger2D<>(16);
        int c=0;
        while(voro_centers.size()<initLargeNumOfPoints){
            double rx = aabb.getMinX()+Math.random()*aabb.getWidth();
            double ry = aabb.getMinY()+Math.random()*aabb.getHeight();
            WB_Point tmp_point = new WB_Point(rx,ry);
            if(WB_GeometryOp2D.contains2D(tmp_point,this.range)){
                AgentPoint agentPoint = new AgentPoint(Evaluation.distance, tmp_point);
                voro_centers.add(agentPoint);
                tree.add(agentPoint,c);
                c++;
            }
        }
        System.out.println("first voro_centers  "+voro_centers.size());
        // 设置 AP 的值
        for(int i=0;i<voro_centers.size();i++){
            AgentPoint agentPoint = voro_centers.get(i);
            double r=0;
            double co=0;
            double inf=0;
            for(Interferon interferon:interferons){
                double d = interferon.getDistance(agentPoint);//cen.getDistance(WB_GeometryOp.getClosestPoint2D(cen, line));
                d*=interferon.coefficient;
                r+=d;
                co+=interferon.coefficient;
                inf+=interferon.influenceDistance;
            }
            r/=co;
            inf/=interferons.size();
            r = r>inf ? maxSize : Utils.map((float)r,0,(float)inf,(float)minSize,(float)maxSize);

            agentPoint.setValue(Evaluation.distance, r);

        }

        // 移除距离太近的点
        boolean tooNear = true;
        while(tooNear) {
            tooNear = false;

            WB_KDTreeInteger2D.WB_KDEntryInteger[]pointsInRange = null;
            AgentPoint cur_ap = null;
            for (int i = 0; i < voro_centers.size(); i++) {
                cur_ap = voro_centers.get(i);
                pointsInRange = tree.getRange(cur_ap, cur_ap.getValue(Evaluation.distance.name()));
                if(pointsInRange.length > 1){
                    tooNear = true;
                    break;
                }
            }

            if(pointsInRange.length>1) {
                for (int i = 0; i < pointsInRange.length; i++) {
                    AgentPoint ap = (AgentPoint) pointsInRange[i].coord;
                    if(ap != cur_ap) {
                        voro_centers.remove(ap);
                    }
                }
            }

            // 重构 tree
            tree = new WB_KDTreeInteger2D<>(16);
            for(int i=0;i<voro_centers.size();i++){
                AgentPoint agentPoint = voro_centers.get(i);
                tree.add(agentPoint,i);
            }
        }
        System.out.println("voro_centers  "+voro_centers.size());
    }

    public double getMinSize(){return this.minSize;}
    public double getMaxSize(){return this.maxSize;}
    public void draw(HE_Render render){
        PApplet app = render.getApp();

        // 绘制干扰源
        if(this.interferons!=null) {
            for(Interferon interferon:interferons)
            interferon.draw(render);
        }

        // 绘制 Voronoi 的中心
//        for(AgentPoint ap:voro_centers){
//            render.drawPoint(ap.loc,2);
//        }

        // 绘制 Voronoi_Cells
        if(this.cellMesh!=null){
            app.fill(200);
            app.noStroke();
            render.drawFaces(cellMesh);
        }

//        render.drawPolygonEdges(this.range);
    }




}
