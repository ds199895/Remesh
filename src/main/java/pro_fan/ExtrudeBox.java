package pro_fan;

import processing.core.PApplet;
import util.render.HE_Render;
import wblut.geom.*;
import wblut.hemesh.HEC_FromPolygons;
import wblut.hemesh.HEM_Extrude;
import wblut.hemesh.HE_Face;
import wblut.hemesh.HE_Mesh;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO：带方向信息的立方体
 *
 * @author libiao
 * @version 1.0
 * @date 2022/7/28 15:50
 * Inst. AAA, S-ARCH, Southeast University
 */
public class ExtrudeBox implements FanGeo {
    /**———————————————————————— by wb ————————————————————————————————————————————————*/
//    protected Geo_Machine relativeGeo = new Geo_Machine();
//    public void setLight(double b1) {
//        relativeGeo.set("light", b1);
//    }
//    public double getLight() {
//        return relativeGeo.getParameterSet().get("light", 0.5d);
//    }
    /**———————————————————————— by wb ————————————————————————————————————————————————*/
    public WB_Point loc;
    WB_Vector dir;
    public float width,height,depth;

    WB_Polygon basePolygon;
    WB_Plane[]planes = null;
    public HE_Mesh mesh;
    ArrayList<WB_Polygon>polys;
    public TypeColor type;
    public double value;

    /** loc:底部中心点, dir: 矩形方向, width,height,depth: 长宽高 */
    public ExtrudeBox(TypeColor type, WB_Point loc, WB_Vector dir, float width, float height, float depth){
        this.type = type;

//        System.out.println("this.type   "+this.type);

        this.loc = loc;
        this.dir = dir.copy();
        this.dir.normalizeSelf();

        this.width = width;
        this.height = height;
        this.depth = depth;

//        this.planes = new WB_Plane[6]; //*******************************************************
//        this.updatePlanes();


        this.updateMesh();


    }
    public ExtrudeBox(TypeColor type, ArrayList<WB_Polygon> polys){
        this.type = type;

//        System.out.println("this.type   "+this.type);
        this.polys=polys;
        this.mesh=new HE_Mesh(new HEC_FromPolygons(polys));
        this.loc=mesh.getAABB().getCenter();
        this.depth=(float)mesh.getAABB().getDepth();


//        this.planes = new WB_Plane[6]; //*******************************************************
//        this.updatePlanes();

    }
    private WB_Point[] basepoints; // 基地多边形的点集
    private void updateMesh(){
        WB_Vector leftRotate = new WB_Vector(-dir.yd(),dir.xd());

        //create base points for a single rectangle
        basepoints = new WB_Point[4];
        basepoints[0] = this.loc.add(dir.scale(-width/2)).add(leftRotate.scale(-height/2));
        basepoints[1] = this.loc.add(dir.scale(width/2)).add(leftRotate.scale(-height/2));
        basepoints[2] = this.loc.add(dir.scale(width/2)).add(leftRotate.scale(height/2));
        basepoints[3] = this.loc.add(dir.scale(-width/2)).add(leftRotate.scale(height/2));

        WB_Polygon[] polygons=new WB_Polygon[1];
        polygons[0] = basePolygon = new WB_Polygon(basepoints);
        HEC_FromPolygons creator = new HEC_FromPolygons();
        creator.setPolygons(polygons);

        mesh=new HE_Mesh(creator);

        HEM_Extrude modifier = new HEM_Extrude();
        modifier.setDistance(this.depth); // extrusion distance, set to 0 for inset faces
        modifier.setFuse(true);
        modifier.setPeak(true);
        mesh.modify(modifier);
    }

    /** 与其它建筑的距离 */
    public double getDistance(ExtrudeBox box){
        if(this.contains(box) || box.contains(this)){
            return 0;
        }else{
            double minDist  = Double.MAX_VALUE;
            // distance of box_vertices to this
            for(WB_Coord c:box.basepoints){
                double dist_tmp = this.getDistance(c);
                if(dist_tmp<minDist){
                    minDist = dist_tmp;
                }
            }
            for(WB_Coord c:this.basepoints){
                double dist_tmp = box.getDistance(c);
                if(dist_tmp<minDist){
                    minDist = dist_tmp;
                }
            }
            return minDist;
        }
    }

    /** 与点的二维平面距离 */
    public double getDistance(WB_Coord coord){
        double minDist  = Double.MAX_VALUE;
        for(int i=0;i<this.basepoints.length;i++){ // segment iterate
            WB_Segment seg = new WB_Segment(this.basepoints[i], this.basepoints[(i+1)%this.basepoints.length]);
            double temp_dist = WB_GeometryOp2D.getDistance2D(coord,seg);
            if(temp_dist<minDist){
                minDist = temp_dist;
            }
        }
        return minDist;
    }

//    public boolean intersects(ExtrudeBox box){
//        return false; // implements by students!!!!
//    }

    public boolean contains(ExtrudeBox box){
        WB_Polygon poly = box.basePolygon;
        for(WB_Coord c:poly.getPoints().toArray()){
            if(WB_GeometryOp2D.contains2D(c, this.basePolygon)){
                return true;
            }
        }
        return false;
    }

    public boolean contains(AgentPoint ap){
        List<HE_Face> faces = this.mesh.getFaces();
        for(HE_Face f:faces){
            WB_Coord faceNormal = f.getFaceNormal();
            WB_Coord faceCenter = f.getFaceCenter();
            WB_Point sub = ap.loc.sub(faceCenter);
            double dot = sub.dot(faceNormal);
            if(dot>0){
                return false;
            }
        }
        return true;
    }

    public void setDirection(float x, float y){
        this.dir.set(x,y);
        this.dir.normalizeSelf();
        this.updateMesh();
    }

    public void setLocation(float x, float y,float z){
        this.loc.set(x,y,z);
        this.updateMesh();
    }

    public void set(WB_Point loc, WB_Vector dir){
        this.loc.set(loc);
        this.dir.set(dir);
        this.dir.normalizeSelf();

        this.updateMesh();
    }
    public boolean viewDetail=false;
    @Override
    public void draw(HE_Render render){
        PApplet app = render.getApp();

        app.pushStyle();
        app.stroke(this.type.color());

        if(this.type==TypeColor.newBuild){
            app.stroke(0,0,255);
            render.drawEdges(mesh);
//            app.noStroke();
//            app.fill(new GradientMap(app, ColorMap.JET).getColor((float)this.value), 170);
//            render.drawFaces(mesh);

        }else if(this.type==TypeColor.water){
            if(!viewDetail) {
                Color c = new Color(143, 205, 243);
                app.fill(c.getRGB());
                for (WB_Polygon p : polys) {
                    render.drawPolygonEdges(p);
                }
            }
        } else{
            if(!viewDetail) {
                app.fill(255);
                for (WB_Polygon p : polys) {
                    render.drawPolygonEdges(p);
                }
            }
        }
        app.popStyle();
//        app.noStroke();
//        render.drawFaces(mesh);
    }

}
