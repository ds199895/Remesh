package pro_fan;

import igeo.IG;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import util.render.HE_Render;
import wblut.geom.*;
import wblut.hemesh.HEC_FromTriangulation;
import wblut.hemesh.HE_Face;
import wblut.hemesh.HE_Mesh;
import wblut.hemesh.HE_Vertex;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO：模拟地形的类 （真实的地形需贺思远完成 ***********************************）
 *
 * @author libiao
 * @version 1.0
 * @date 2022/7/28 10:38
 * Inst. AAA, S-ARCH, Southeast University
 */

/**——————————————————————————————————————全部by HSY————————————————————————————————————————————*/
public class Terrain implements FanGeo {
    // 模拟地形高程的灰度图
    private PImage image;
    // 在该 range 多边形内生成随机点, 用于生成 Delaunay 三角网
    protected WB_Polygon range = null;
    // range多边形 和 灰度图 的aabb
    WB_AABB2D aabb,_img_aabb;
    // 提高效率 获取
    public WB_AABBTree tree;
    // 该地形的mesh
    public HE_Mesh mesh,allMesh,analysisMesh;


    List<Color>allColors;
    List<Color>analysisColors;
    List<Color>colors;
    ArrayList<ArrayList<WB_Polygon>> barrierpolyss=new ArrayList<>();
    ArrayList<ArrayList<WB_Polygon>> targetpolyss=new ArrayList<>();
    ArrayList<ArrayList<WB_Polygon>> waterpolyss=new ArrayList<>();
    ArrayList<ArrayList<WB_Polygon>> roadpolyss=new ArrayList<>();
    ArrayList<ArrayList<WB_Polygon>> detailpolyss=new ArrayList<>();
    WB_Polygon rangepoly;

    WB_Polygon sitePoly;
    WB_Polygon archiPoly;
    ArrayList<HE_Mesh>barriermeshes=new ArrayList<>();
    ArrayList<HE_Mesh>targetmeshes=new ArrayList<>();
    WB_Point[]gcd;
    List<WB_Point>analysisGcd=new ArrayList<>();
    int[] triangles;
    String file;

    double minZ=Double.MAX_VALUE;
    double maxZ=0;
    public Terrain(String fileName){
        this.file=fileName;
        this.init();
    }

    private void init(){
        //this.set_aabb();
        this.createSite();

        // debug
        //this.setFaceGradients();
    }


    private void createSite(){
        IG.init();
        IG.open(this.file);

        if (IG.layer("buildings").breps().length > 0)
        barriermeshes.addAll(Lt_Igeo.IBrepstoHemeshs(IG.layer("buildings").breps()));
        barrierpolyss= Lt_Igeo.IBrepstoWB_Polyss(IG.layer("buildings").breps());
        System.out.println("barrier num: "+IG.layer("buildings").breps().length);

        if(IG.layer("target").breps().length > 0)
            targetmeshes.addAll(Lt_Igeo.IBrepstoHemeshs(IG.layer("target").breps()));
        targetpolyss.addAll(Lt_Igeo.IBrepstoWB_Polyss(IG.layer("target").breps()));
        System.out.println("target num: "+IG.layer("target").breps().length);

        if(IG.layer("water").breps().length > 0)
            //inputmeshes.addAll(Lt_Igeo.IBrepstoHemeshs(IG.layer("water").breps()));
            targetmeshes.addAll(Lt_Igeo.IBrepstoHemeshs(IG.layer("water").breps()));
        waterpolyss.addAll(Lt_Igeo.IBrepstoWB_Polyss(IG.layer("water").breps()));
        System.out.println("water num: "+IG.layer("water").breps().length);

        if(IG.layer("road").breps().length > 0)
            roadpolyss.addAll(Lt_Igeo.IBrepstoWB_Polyss(IG.layer("road").breps()));
        System.out.println("road num: "+IG.layer("road").breps().length);

        if(IG.layer("detail").breps().length > 0)
            detailpolyss.addAll(Lt_Igeo.IBrepstoWB_Polyss(IG.layer("detail").breps()));
        System.out.println("detail num: "+IG.layer("detail").breps().length);

        if(IG.layer("range").crvs().length > 0)
            rangepoly= Lt_Igeo.IcurvetoWB_Poly2D(IG.layer("range").crv(0));
        System.out.println("range num: "+IG.layer("range").crvs().length);

        if(IG.layer("newBuilding").crvs().length > 0)
            archiPoly= Lt_Igeo.IcurvetoWB_Poly2D(IG.layer("newBuilding").crv(0));
        System.out.println("archiSite num: "+IG.layer("newBuilding").crvs().length);

        if(IG.layer("site").crvs().length > 0)
            System.out.println("sites num: "+IG.layer("site").srfs().length);
        sitePoly= Lt_Igeo.IcurvetoWB_Poly3D(IG.layer("site").crv(0));

        if(IG.layer("gcd").points().length > 0)
            gcd= Lt_Igeo.IPointstoWB_Points(IG.layer("gcd").points());
        System.out.println("gcd num: "+IG.layer("gcd").points().length);

        for(WB_Point p:gcd){
            if(WB_GeometryOp2D.contains2D(p,rangepoly)){
                analysisGcd.add(p);
            }
        }

        WB_Triangulation2D triangulation=WB_Triangulate.triangulate2D(analysisGcd);
        triangles=triangulation.getTriangles();// 1D array of indices of triangles, 3 indices per triangle
        analysisMesh=new HE_Mesh(new HEC_FromTriangulation().setTriangulation(triangulation).setPoints(analysisGcd));
        mesh=analysisMesh;
        this.tree = new WB_AABBTree(mesh, 1);


        WB_Triangulation2D triangulation1=WB_Triangulate.triangulate2D(gcd);
        triangles=triangulation1.getTriangles();// 1D array of indices of triangles, 3 indices per triangle
        allMesh=new HE_Mesh(new HEC_FromTriangulation().setTriangulation(triangulation1).setPoints(gcd));

        for(WB_Coord c:allMesh.getPoints().toList()){
            if(c.zd()>maxZ){
                maxZ=c.zd();
            }
            if(c.zd()<minZ){
                minZ=c.zd();
            }
        }
        this.analysisColors=this.calculateColor(analysisMesh);
        this.allColors=this.calculateColor(allMesh);
        this.colors=this.analysisColors;
    }

    /** 获取平面 2D (x,y) 处的点在该地形的3D(x,y,z) 坐标*/
    public WB_Point getLocalLocation(double x, double y){
        WB_Point p=new WB_Point();
        WB_Ray ray=new WB_Ray(new WB_Point(x,y,0), new WB_Point(0,0,1));

        for(HE_Face f :allMesh.getFaces()){
            WB_IntersectionResult result=WB_GeometryOp.getIntersection3D(ray,f.getPolygon());
            if(result.intersection){
                p=(WB_Point) result.object;
            }
        }
        return p;
    }

    /** copy from processing... */
    public float map(float value, float start1, float stop1, float start2, float stop2) {
        float outgoing = start2 + (stop2 - start2) * ((value - start1) / (stop1 - start1));
        String badness = null;
        if (outgoing != outgoing) {
            badness = "NaN (not a number)";
        } else if (outgoing == -1.0F / 0.0 || outgoing == 1.0F / 0.0) {
            badness = "infinity";
        }

        if (badness != null) {
            String msg = String.format("map(%s, %s, %s, %s, %s) called, which returns %s", nf(value), nf(start1), nf(stop1), nf(start2), nf(stop2), badness);
            PGraphics.showWarning(msg);
        }

        return outgoing;
    }
    private String nf(float num) {
        int inum = (int)num;
        return num == (float)inum ? String.valueOf(inum) : String.valueOf(num);
    }

    private double offset = 100;
    /** 获取三维 xyz 空间的梯度向量 */
    public WB_Vector getGradient3D(double x, double y){
        HE_Face f = getLocalFace(x, y);
        if (f != null) {

            float x_pos = this.getLocalLocation(x+offset, y).zf();
            float x_neg = this.getLocalLocation(x-offset, y).zf();
            float y_pos = this.getLocalLocation(x, y+offset).zf();
            float y_neg = this.getLocalLocation(x, y-offset).zf();

            WB_Vector g  = new WB_Vector(x_pos-x_neg, y_pos-y_neg);
            if(g.getLength() != 0){
                WB_Coord faceNormal = f.getFaceNormal();
                WB_Vector cross = g.cross(faceNormal);
                WB_Vector gradient = cross.cross(faceNormal);
                gradient.normalizeSelf();
                return gradient;
            }else{
                return null;
            }
        }
        return null;
    }

    /** 获取二维 xy 平面的梯度向量 */
    public WB_Vector getGradient2D(double x, double y){
        HE_Face f = getLocalFace(x, y);
        if (f != null) {
            float x_pos = this.getBrightness(x+offset, y);
            float x_neg = this.getBrightness(x-offset,y);
            float y_pos = this.getBrightness(x, y+offset);
            float y_neg = this.getBrightness(x,y-offset);

            WB_Vector g  = new WB_Vector(x_pos-x_neg, y_pos-y_neg);
            if(g.getLength() != 0){
                g.normalizeSelf();
                return g;
            }else{
                return null;
            }
        }
        return null;
    }

    // debug...测试
    List<WB_Vector>gradients = new ArrayList<>();
    private void setFaceGradients(){
        List<HE_Face> faces = mesh.getFaces();
        for(HE_Face f:faces){
            WB_Coord faceCenter = f.getFaceCenter();
            WB_Vector gradient = getGradient3D(faceCenter.xd(), faceCenter.yd());
            gradients.add(gradient);
        }
    }

    /**
     * 获取基地上点(x,y)位置的亮度（可被映射到高程）
     */
    public float getBrightness(double x, double y) {
        if (this.aabb.contains(new WB_Point(x, y))) {// should be true...
//            System.out.println("in");
            WB_Point min = this.aabb.getMin();
            double min_x = min.xd(), min_y = min.yd();
            double w = this.aabb.getWidth(), h = this.aabb.getHeight();

            double x_ratio = (x - min_x) / w;
            double y_ratio = (y - min_y) / h;

            //prevent ArrayIndexOutOfBoundsException...
            x_ratio = x_ratio <= 0 ? 0.00001 : x_ratio;
            x_ratio = x_ratio >= 1 ? 0.99999 : x_ratio;
            y_ratio = y_ratio <= 0 ? 0.00001 : y_ratio;
            y_ratio = y_ratio >= 1 ? 0.99999 : y_ratio;

            int pixel_x = (int) (this.image.width * x_ratio);
            int pixel_y = (int) (this.image.height * y_ratio);

            //translate from RGB to brightness(Gray)
            BufferedImage scr = (BufferedImage) this.image.getNative();
            Color color = new Color(scr.getRGB(pixel_x, pixel_y));
            int r = color.getRed();
            int g = color.getGreen();
            int b = color.getBlue();
            //简化版，效果貌似差不多
            return r * 0.299f + g * 0.587f + b * 0.114f;
        } else { // should never happen
            System.out.println("Terrain.getBrightness -> should never happen!");
            return 0;
        }
    }

    /** 返回离点(x,y)最近的点 */
    public HE_Face getLocalFace(double x, double y) {
        WB_Coord xy = new WB_Point(x, y);
        return tree.getClosestFace(xy);
    }

    private void set_aabb(){
        int numberOfPoints = range.getNumberOfPoints();
        this.aabb = new WB_AABB2D();
        for(int i=0;i<numberOfPoints;i++){
            this.aabb.add(range.getPoint(i));
        }
    }

    /** 在 range 范围内产生 num 个随机点 */
    private List<WB_Point> randomInRange(int num){
        List<WB_Point>rand_points = new ArrayList<>();
        while(rand_points.size() < num){
            float rx = (float) (this.aabb.getMinX()+Math.random()*this.aabb.getWidth());
            float ry = (float) (this.aabb.getMinY()+Math.random()*this.aabb.getHeight());
            WB_Point cur_p = new WB_Point(rx,ry);
            if(WB_GeometryOp2D.contains2D(cur_p,this.range)){
                rand_points.add(cur_p);
            }
        }
        return rand_points;
    }

    private List<Color> calculateColor(HE_Mesh mesh){

        List<Color>colors=new ArrayList<>();
        for(HE_Face f:mesh.getFaces()){
            float r_average=0;
            float g_average=0;
            for(HE_Vertex v:f.getFaceVertices()){
                float r=map(v.getPosition().zf(),(float) minZ,(float) maxZ,0,255);
                float g=map(v.getPosition().zf(),(float) minZ,(float) maxZ,255,0);
                r_average+=r;
                g_average+=g;
            }
            r_average/=f.getFaceVertices().size();
            g_average/=f.getFaceVertices().size();
            //System.out.println(r_average+", "+g_average);

            Color c=new Color((int)r_average,(int)g_average,0);
            colors.add(c);
        }

        return colors;
    }


    /**
     * 地形（site的aabb）是否与 image的长宽匹配 (只用于检测, 不做任何工作)
     */
    private void checkRatio() {
        double aabbRatio = (double) aabb.getWidth() / (double) aabb.getHeight();
        double imagRatio = (double) image.width / (double) image.height;

        //根据image的长宽比例，将image的aabb覆盖到site_aabb
        WB_Point min = aabb.getMin();
        if (imagRatio > aabbRatio) {
            double h = aabb.getHeight();
            double w = h * imagRatio;
            double minx = min.xd() - (w - aabb.getWidth()) / 2.;
            double miny = min.yd();
            double maxx = minx + w;
            double maxy = miny + h;
            this._img_aabb = new WB_AABB2D(minx, miny, maxx, maxy);
        } else {
            double w = aabb.getWidth();
            double h = w / imagRatio;
            double minx = min.xd();
            double miny = min.yd() - (h - aabb.getHeight()) / 2.;
            double maxx = minx + w;
            double maxy = miny + h;
            this._img_aabb = new WB_AABB2D(minx, miny, maxx, maxy);
        }
//        //给前台发布比例信息
//        if (Comman.same(aabbRatio, imagRatio, 0.01)) {
////            isValid = true;
//            System.out.println("the site is matched to the image! " + aabbRatio + " -> " + imagRatio);
//        } else {
////            isValid = false;
//            System.err.println("the site IS NOT MATCH to the image!!! " + aabbRatio + " -> " + imagRatio);
//        }
    }

    public boolean heightview=false;

    @Override
    public void draw(HE_Render render){
        PApplet app = render.getApp();
        app.pushStyle();

//        app.image(this.image, (float)this._img_aabb.getMinX(), (float)this._img_aabb.getMinY());
//        app.line(0,0,1000,1000);

//        this.drawAABB(app);

//        app.stroke(0,100);
//        app.noFill();
//        render.drawEdges(mesh);
        app.pushStyle();
        if(heightview) {
            for (int i = 0; i < mesh.getFaces().size(); i++) {
                app.fill(this.colors.get(i).getRGB());
                app.noStroke();
                render.drawFace(mesh.getFaceWithIndex(i));
            }
        }else {
            app.fill(240);
            app.noStroke();
            render.drawFaces(mesh);
        }
        ;
        app.popStyle();
//        app.fill(204);
//        app.noStroke();
//        render.drawFaces(mesh);


        // 绘制梯度方向
//        List<HE_Face> faces = this.mesh.getFaces();
//        for(int i=0;i<this.gradients.size();i++){
//            HE_Face f = faces.get(i);
//            WB_Coord fc = f.getFaceCenter();
//
//
//            WB_Vector gradient = this.gradients.get(i);
//            if (gradient==null) continue;
//            WB_Vector g = gradient.scale(20).add(fc);
//
//            app.stroke(255,0,0);
//            app.line(fc.xf(),fc.yf(),fc.zf(),g.xf(),g.yf(),g.zf());
//
//            app.pushMatrix();
//            app.translate(fc.xf(),fc.yf(),fc.zf());
//            app.box(2);
//            app.popMatrix();
//        }




        app.popStyle();
    }


//    private void drawAABB(PApplet app){
//        float num = 30;
//        float bx = (float) this.aabb.getMinX();
//        float by = (float) this.aabb.getMinY();
//        float ux = (float) (this.aabb.getWidth()/num);
//        float uy = (float) (this.aabb.getHeight()/num);
//
//        app.noStroke();
//        for(int j=0;j<num;j++){
//            for(int i=0;i<num;i++){
//                float px = bx + ux * i;
//                float py = by + uy * j;
//                app.fill(this.getBrightness(px,py));
//                app.rect(px,py,ux,uy);
//            }
//        }
//
//    }
}
