import processing.core.PApplet;
import wblut.geom.WB_Point;
import wblut.geom.WB_Triangle;
import wblut.hemesh.HEC_FromTriangles;
import wblut.hemesh.HE_Halfedge;
import wblut.hemesh.HE_Mesh;
import wblut.hemesh.HE_Vertex;
import wblut.processing.WB_Render;

import java.util.ArrayList;
import java.util.List;

public class collapseTest extends PApplet {
    public static void main(String[] args) {
        PApplet.main("collapseTest");
    }
    HE_Mesh mesh;
    List<WB_Triangle>tris=new ArrayList<WB_Triangle>();
    WB_Render render;
    double r=300;
    HE_Vertex ver=null;
    HE_Halfedge he=null;
    public void setup(){
        size(8000,8000,P3D);
        render=new WB_Render(this);
        for(int i=0;i<10;i++){
            int nxt=(i+1)%10;
            WB_Point p=new WB_Point(width/2+r*sin(i*2*PI/10),height/2+r*cos(i*2*PI/10),0);
            WB_Point p_nxt=new WB_Point(width/2+r*sin(nxt*2*PI/10),height/2+r*cos(nxt*2*PI/10),0);
            WB_Triangle tri=new WB_Triangle(new WB_Point(width/2,height/2,0),p,p_nxt);
            tris.add(tri);
        }
        HEC_FromTriangles hec_t=new HEC_FromTriangles().setTriangles(tris);
        mesh=new HE_Mesh(hec_t);
        for(HE_Vertex v:mesh.getVertices()){
            if(v.getVertexDegree()>4){
                ver=v;
            }
        }
    }

    public void draw(){
        background(255);
        stroke(0);
        render.drawFaces(mesh);
        render.drawPoint2D(ver,20);
        if(he!=null){
            stroke(0,255,0);
            strokeWeight(2);
            render.drawEdge(he);
        }
    }
    int index=0;
    public void keyReleased(){
        if(key=='s'){
            index=(index+1)%ver.getVertexDegree();
            he=ver.getHalfedgeStar().get(index);
        }
    }


}
