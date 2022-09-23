import processing.core.PApplet;
import processing.core.PFont;
import guo_cam.CameraController;
import processing.core.PVector;
import wblut.geom.*;
import wblut.hemesh.*;
import wblut.processing.WB_Render;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("serial")
public class Ref_HEC_FromPolygons extends PApplet {
	public static void main(String[] args) {
		PApplet.main("Ref_HEC_FromPolygons");
	}
	HE_Mesh mesh;
	WB_Render render;
	WB_GeometryFactory wg=new WB_GeometryFactory();
    CameraController cam;
	HE_Halfedge edge;
	HE_Vertex vertex;
	WB_Vector v;
	List<HE_Halfedge>acuteEdge=new ArrayList<>();
	List<HE_Vertex>newVers=new ArrayList<>();
	PFont font = createFont(PFont.list()[274],10);
	public void setup() {
		size(600, 600, P3D);
		this.createMesh();
		cam=new CameraController(this,200);
		render = new WB_Render(this);
		textFont(font);
		for(int i=0;i<mesh.getVertices().size();i++){
			if(mesh.getVertices().get(i).getHalfedgeStar().size()>=3&&!mesh.getVertices().get(i).isBoundary()){
				this.ellipse(mesh.getVertices().get(i).xf(),mesh.getVertices().get(i).yf(),6,6);
				edge=mesh.getVertices().get(i).getHalfedge();
				vertex=mesh.getVertices().get(i);

			}
		}
		setLabel(mesh);
		println(edge,vertex);
		controlEdgesLength();
	}

	public void draw() {
		background(255);
		
		controlEdgesLength();

		stroke(0);
		render.drawEdges(mesh);

		pushStyle();
		this.mesh.getVertices().forEach(v->
		    this.ellipse(v.xf(), v.yf(), 2, 2)
				);
		popStyle();

		pushStyle();

		stroke(255,0,0);
		strokeWeight(3);
		for(HE_Halfedge edge:acuteEdge){
			line(edge.getVertex().xf(),edge.getVertex().yf(),edge.getEndVertex().xf(),edge.getEndVertex().yf());
		}
		popStyle();
		render.drawPoint2D(vertex,10);
		pushStyle();
		if(newVers.size()!=0){
			fill(255,0,0);
			render.drawPoint2D(newVers.get(0).getHalfedge().getEndVertex(),5);
			render.drawPoint2D(newVers.get(0),5);
		}
		popStyle();
		pushStyle();
		fill(255,0,0);
//		for(int i=0;i<mesh.getHalfedges().size();i++){
//			HE_Halfedge edge=mesh.getHalfedges().get(i);
//			WB_Vector dir=new WB_Vector(edge.getEndVertex().xf()-edge.getVertex().xf(),edge.getEndVertex().yf()-edge.getVertex().yf());
//			WB_Vector norm=dir.getOrthoNormal2D();
//			WB_Vector p=WB_Vector.add(edge.getCenter(),norm.scaleSelf(8));
//			textAlign(CENTER);
//			text(String.valueOf(edge.getKey()),p.xf(),p.yf());
//		}
		for(int i=0;i<mesh.getVertices().size();i++){
			HE_Vertex ver=mesh.getVertices().get(i);
			WB_Vector dir=new WB_Vector(ver.xf()-ver.getHalfedge().getEndVertex().xf(),ver.yf()-ver.getHalfedge().getEndVertex().yf());
			WB_Vector norm=dir.getOrthoNormal2D();
			WB_Vector p=WB_Vector.add(ver,norm.scaleSelf(8));
			textAlign(CENTER);
			text(String.valueOf(ver.getUserLabel()),p.xf(),p.yf());
		}
		popStyle();
		if(v_save.size()!=0){
			pushStyle();
			fill(0,255,0);
			render.drawPoint2D(v_save,10);
			popStyle();
		}

//		if(v_save!=null){
//			pushStyle();
//            render.drawEdges(v_save.geth);
//
//			popStyle();
//		}

	}
//	private void createMeshes(){
//		mesh = new HE_Mesh(new HEC_Cube().setEdge(4000));
//		//mesh.smooth();
//		HEC_Dual creator = new HEC_Dual();
//		creator.setSource(mesh);
//		dual = new HE_Mesh(creator);
//
//		WB_Transform3D tran = new WB_Transform3D();
//		tran.addRotateAboutAxis(PI/6,new WB_Point(0,0,0),new WB_Point(0,0,1));
//		tran.addTranslate(new WB_Vector(-4000,2000,4000));
//
//		mesh.applySelf(tran);
//		dual.applySelf(tran);
//
//		WB_Point[] points = new WB_Point[121];
//		int index = 0;
//		for (int j = 0; j < 11; j++) {
//			for (int i = 0; i < 11; i++) {
//				points[index] = new WB_Point(-200 + i * 40 + (((i != 0) && (i != 10)) ? random(-20, 20) : 0),
//						-200 + j * 40 + (((j != 0) && (j != 10)) ? random(-20, 20) : 0),
//						sin(TWO_PI / 20 * i) * 40 + cos(TWO_PI / 10 * j) * 40);
//				index++;
//			}
//		}
//
//		// create triangles from point grid
//		WB_Triangle[] tris = new WB_Triangle[200];
//
//		for (int i = 0; i < 10; i++) {
//			for (int j = 0; j < 10; j++) {
//				tris[2 * (i + 10 * j)] = new WB_Triangle(points[i + 11 * j], points[i + 1 + 11 * j],
//						points[i + 11 * j + 11]);
//				tris[2 * (i + 10 * j) + 1] = new WB_Triangle(points[i + 1 + 11 * j], points[i + 11 * j + 12],
//						points[i + 11 * j + 11]);
//			}
//		}
//
//		HEC_FromTriangles tri_creator = new HEC_FromTriangles();
//		tri_creator.setTriangles(tris);
//		// alternatively tris can be any Collection<WB_Triangle>
//		tri_mesh = new HE_Mesh(tri_creator);
//	}
	private void createMesh() {
		// create base points for a single hexagon

		WB_Polygon[] polygons=new WB_Polygon[4];
		WB_Point[] basepoints1 = new WB_Point[4];
		WB_Point[] basepoints2=new WB_Point[3];
		WB_Point[] basepoints3=new WB_Point[3];
		WB_Point[] basepoints4=new WB_Point[4];
		basepoints1=new WB_Point[]{
				new WB_Point(100,200),
				new WB_Point(300,200),
				new WB_Point(300,400),
				new WB_Point(100,400)
		};
		basepoints2=new WB_Point[]{
				new WB_Point(300,200),
				new WB_Point(400,100),
				new WB_Point(450,180)
		};
		basepoints3=new WB_Point[]{
				new WB_Point(100,200),
				new WB_Point(400,100),
				new WB_Point(300,200)
		};
		basepoints4=new WB_Point[]{
				new WB_Point(450,180),
				new WB_Point(300,400),
//				new WB_Point(100,200),
				new WB_Point(300,200),
		};

		polygons[0]=wg.createSimplePolygon(basepoints1);
		polygons[1]=wg.createSimplePolygon(basepoints2);
		polygons[2]=wg.createSimplePolygon(basepoints3);
		polygons[3]=wg.createSimplePolygon(basepoints4);
				HEC_FromPolygons creator = new HEC_FromPolygons();

				creator.setPolygons(polygons);
				// alternatively polygons can be any Collection<WB_Polygon>
				mesh = new HE_Mesh(creator);
				// Uncomment for a fun little combination with HEC_FromFrame
				// mesh=new HE_Mesh(new HEC_FromFrame().setFrame(mesh));
	}

	double gap = 80;
	private void controlEdgesLength(){ // gap 需要动态确定*******
        boolean done = false;
        while (!done) {
            done = true;
            for (HE_Halfedge he : this.mesh.getHalfedges()) {
                double length = he.getLength();
                if (length > gap) {
                    // edge 长度大于 gap 则在该 edge 中间加入新 HE_Vertex
                    done = false;
                    splitEdge(this.mesh, he);
                    break;
                }
                if (length < 0.2 * gap) { // gap 需要动态确定*******
                    // edge 长度大于 小于 gap 的 1/10 则端点合并 (删除该边)
                    done = false;
                    HE_MeshOp.collapseEdge(this.mesh, he);
                }

            }
        }
		setLabel(mesh);
    }
	
	HE_Vertex nearPnt = null;
//    public void mouseDragged() {
//    	HE_Vertex mouse = new HE_Vertex(mouseX, mouseY,0);
//        double min = Double.MAX_VALUE;
//        List<HE_Vertex>ps = mesh.getVertices();
//        for(HE_Vertex p:ps) {
//            float dist = (float) WB_GeometryOp.getDistance2D(mouse, p);
//            PVector nearV = null;
//            if (dist < min) {
//                min = dist;
//                nearPnt = p;
//            }
//        }
//        nearPnt.set(mouse);
//    }

	public void keyReleased() {
		switch (key){
			case 'a':
				v_save.clear();
				for(int i=0;i<mesh.getFaces().size();i++){
					println("it's the face of "+i);
					collapseSharpAngle(mesh,mesh.getFaces().get(i),20,PI/3);
				}
				println("end!");
				break;
			case 't':
				cam.top();
				break;
			case 'p':
				cam.perspective();
				break;
		}
	}
	public void mouseDragged() {
		HE_Vertex mouse = new HE_Vertex(mouseX, mouseY, 0);
		double min = Double.MAX_VALUE;
		List<HE_Vertex> ps = mesh.getVertices();
		for (HE_Vertex p : ps) {
			float dist = (float) WB_GeometryOp.getDistance2D(mouse, p);
			PVector nearV = null;
			if (dist < min) {
				min = dist;
				nearPnt = p;
			}
		}
		nearPnt.set(mouse);
	}
	public static void setLabel(HE_Mesh mesh){
		for(int i=0;i<mesh.getFaces().size();i++){
			mesh.getFaces().get(i).setUserLabel(i);
			for(HE_Vertex v:mesh.getFaces().get(i).getFaceVertices()){
				v.setUserLabel(i);
			}
		}
	}
	public List<HE_Halfedge>getacuteEdges(HE_Vertex vertex){
		List<HE_Halfedge>edges=new ArrayList<>();
		List<HE_Halfedge>star=edge.getVertex().getHalfedgeStar();
		double minAngle=Double.MAX_VALUE;
		int id=-1;
		for(int i=0;i<star.size();i++){
			int prev=(i-1+star.size())%star.size();
			float angle= (float) (RAD_TO_DEG*HE_MeshOp.getAngle(star.get(i)));
			println(angle);
			if(angle<minAngle){
				minAngle=angle;
				id=i;
			}
		}
		println(minAngle+"    "+id);
		if(minAngle<90){
			int prv=(id-1+star.size())%star.size();
			edges.add(star.get(prv));
			edges.add(star.get(id));
		}


		return edges;
	}

    //1.0版本
	public void add(HE_Mesh mesh,HE_Halfedge edge,WB_Coord v){
		List<HE_Halfedge> edges=edge.getVertex().getEdgeStar();
		HE_Halfedge he0=edges.get(0);
		HE_Halfedge he1=edges.get(1);
		HE_Halfedge he1_p=he0.getPair();

		HE_Halfedge he2=edges.get(2);

		HE_Halfedge he3=edges.get(3);
		HE_Halfedge he3_p=he0.getPair();

		HE_Vertex vNew=new HE_Vertex(v);
		HE_Halfedge henew1=new HE_Halfedge();
		HE_Halfedge henew2=new HE_Halfedge();


		mesh.setVertex(he3,vNew);
		mesh.setVertex(he2,vNew);

		mesh.setVertex(henew1,edge.getVertex());
		mesh.setNext(he1_p,henew1);
		mesh.setNext(henew1,he2);

		mesh.setVertex(henew2,vNew);
		mesh.setNext(he3_p,henew2);
		mesh.setNext(henew2,he0);


		mesh.setHalfedge(vNew,he2);
		mesh.setPair(henew1,henew2);

		if(he0.getFace()!=null){
			mesh.setFace(henew2, he0.getFace());
		}
		if(he1.getFace()!=null){
			mesh.setFace(henew1, he1.getFace());
		}
		mesh.addDerivedElement(vNew, new HE_Element[]{edge});
		mesh.addDerivedElement(henew1, new HE_Element[]{edge});
		mesh.addDerivedElement(henew2, new HE_Element[]{edge});
	}
	//2.0版本
	public void addVertex(HE_Mesh mesh,List<HE_Halfedge>acuteEdge,double d){
    	HE_Vertex v_ori=acuteEdge.get(0).getVertex();
		WB_Vector v1=new WB_Vector(acuteEdge.get(0).getEndVertex().xf()-acuteEdge.get(0).getVertex().xf(),acuteEdge.get(0).getEndVertex().yf()-acuteEdge.get(0).getVertex().yf());
		WB_Vector v2=new WB_Vector(acuteEdge.get(1).getEndVertex().xf()-acuteEdge.get(1).getVertex().xf(),acuteEdge.get(1).getEndVertex().yf()-acuteEdge.get(1).getVertex().yf());
		v=WB_Vector.add(v1,v2);
		v=WB_Vector.div(v,v.getLength());
		v=v.mul(20.0f);
		HE_Halfedge he0_p=acuteEdge.get(0).getPrevInFace();

		HE_Halfedge he1_p=acuteEdge.get(1).getNextInFace();
		if(he1_p.getVertex()==acuteEdge.get(1).getEndVertex()){
			he1_p=acuteEdge.get(1).getPair().getNextInFace().getPair();
		}
		if(he0_p.getVertex()==acuteEdge.get(0).getEndVertex()){
			he0_p=acuteEdge.get(0).getPair().getNextInFace().getPair();
		}
		HE_Halfedge he1=he1_p.getPair();

		HE_Vertex vNew=new HE_Vertex((double)(acuteEdge.get(0).getVertex().xf()+v.xf()),(double)(acuteEdge.get(0).getVertex().yf()+v.yf()),0);
		HE_Halfedge heac0_p=acuteEdge.get(0).getPair();
		HE_Halfedge heac1_p=acuteEdge.get(1).getPair();

		HE_Halfedge henew1=new HE_Halfedge();
		HE_Halfedge henew2=new HE_Halfedge();

		mesh.setVertex(acuteEdge.get(0),vNew);
		mesh.setVertex(acuteEdge.get(1),vNew);

		mesh.setVertex(henew1,v_ori);
		mesh.setVertex(henew2,vNew);

		mesh.setHalfedge(vNew,henew2);
		mesh.setHalfedge(v_ori,henew1);

		mesh.setNext(heac1_p,henew2);
		mesh.setNext(henew2,he1);

		mesh.setNext(he0_p,henew1);
		mesh.setNext(henew1,acuteEdge.get(0));

		mesh.setNext(heac0_p,acuteEdge.get(1));

		mesh.setPair(henew1,henew2);
		mesh.setPair(acuteEdge.get(0),heac0_p);
		mesh.setPair(acuteEdge.get(1),heac1_p);

		if(heac1_p.getFace()!=null){
			mesh.setFace(henew2, heac1_p.getFace());
		}
		if(acuteEdge.get(0).getFace()!=null){
			mesh.setFace(henew1, he1.getFace());
		}

		mesh.addDerivedElement(vNew, new HE_Element[]{edge});
		mesh.addDerivedElement(henew1, new HE_Element[]{edge});
		mesh.addDerivedElement(henew2, new HE_Element[]{edge});
	}


	public void splitEdge(HE_Mesh mesh, HE_Halfedge edge) {
		WB_Point v = wg.createMidpoint(edge.getVertex(), edge.getEndVertex());
		splitEdge(mesh, (HE_Halfedge)edge, (WB_Coord)v);
	}

	public static void splitEdge(HE_Mesh mesh, HE_Halfedge edge, WB_Coord v) {
		HE_Halfedge he0 = edge.isEdge() ? edge : edge.getPair();
		HE_Halfedge he1 = he0.getPair();
		HE_Vertex vNew = new HE_Vertex(v);
		HE_Halfedge he0new = new HE_Halfedge();
		HE_Halfedge he1new = new HE_Halfedge();
		HE_Halfedge he0n = he0.getNextInFace();
		HE_Halfedge he1n = he1.getNextInFace();
		double d0 = he0.getVertex().getPosition().getDistance(v);
		double d1 = he1.getVertex().getPosition().getDistance(v);
		double f0 = d1 / (d0 + d1);
		double f1 = d0 / (d0 + d1);
		mesh.setVertex(he0new, vNew);
		mesh.setVertex(he1new, vNew);
		mesh.setHalfedge(vNew, he0new);
		mesh.setNext(he0new, he0n);
		he0new.copyProperties(he0);
		mesh.setNext(he1new, he1n);
		he1new.copyProperties(he1);
		if (he0.hasUVW() && he0n.hasUVW()) {
			he0new.setUVW(new HE_TextureCoordinate(f0, he0.getUVW(), he0n.getUVW()));
		}

		if (he1.hasUVW() && he1n.hasUVW()) {
			he1new.setUVW(new HE_TextureCoordinate(f1, he1.getUVW(), he1n.getUVW()));
		}

		mesh.setNext(he0, he0new);
		mesh.setNext(he1, he1new);
		mesh.setPair(he0, he1new);
		mesh.setPair(he0new, he1);
		if (he0.getFace() != null) {
			mesh.setFace(he0new, he0.getFace());
		}

		if (he1.getFace() != null) {
			mesh.setFace(he1new, he1.getFace());
		}

		mesh.addDerivedElement(vNew, new HE_Element[]{edge});
		mesh.addDerivedElement(he0new, new HE_Element[]{edge});
		mesh.addDerivedElement(he1new, new HE_Element[]{edge});

	}
	/**
	 * 在 face 面上 临界角度为 thresholdAngle 值的顶角被塌陷掉 并生成该角的角平分线的新的半边, 重构半边引用关系
	 *
	 * 贺思远
	 * */
	static List<HE_Vertex> v_save=new ArrayList<>();
	public static boolean collapseSharpAngle(HE_Mesh mesh, HE_Face face, double dist, double thresholdAngle){
		List<HE_Vertex> vs = face.getFaceVertices();
		List<HE_Vertex> fvs = new ArrayList<>();
		vs.forEach(v->fvs.add(v));
		for(int i=0;i<fvs.size();i++){
			HE_Vertex v_ori=fvs.get(i);
			HE_Halfedge nxt_out = v_ori.getHalfedge(face);
			if(nxt_out==null){
				println("here!   "+i);
				v_save.add(v_ori);
			}
			if(nxt_out==null) continue;//?????

			double angle = HE_MeshOp.getAngle(nxt_out);

			if (angle<thresholdAngle){
				if(v_ori.isBoundary()){
					continue;
				}
				HashMap<HE_Face,HE_Halfedge> face_edges=new HashMap<>();

				for(int j=0;j<mesh.getFaces().size();j++){
					if(mesh.getFaces().get(j)!=face) {
						face_edges.put(mesh.getFaces().get(j),v_ori.getHalfedge(mesh.getFaces().get(j)));
					}
				}

				System.out.println("isNotBoundary");


//                nxt_out = v_ori.getHalfedge(face);

				HE_Halfedge prv_out = nxt_out.getPrevInFace().getPair();

				WB_Coord nxt_v = nxt_out.getEdgeDirection();
				WB_Coord prv_v = prv_out.getEdgeDirection();

				WB_Vector v = WB_Vector.add(nxt_v, prv_v);
				v.normalizeSelf();
				v.scaleSelf(dist);

				List<HE_Halfedge>acuteEdge = new ArrayList<>();
				acuteEdge.add(prv_out); acuteEdge.add(nxt_out);

				//###############

				HE_Halfedge he0_p=acuteEdge.get(0).getPrevInFace();

				HE_Halfedge he1_p=acuteEdge.get(1).getNextInFace();
				if(he1_p.getVertex()==acuteEdge.get(1).getEndVertex()){
					he1_p=acuteEdge.get(1).getPair().getNextInFace().getPair();
				}
				if(he0_p.getVertex()==acuteEdge.get(0).getEndVertex()){
					he0_p=acuteEdge.get(0).getPair().getNextInFace().getPair();
				}
				HE_Halfedge he1=he1_p.getPair();

				HE_Vertex vNew=new HE_Vertex((double)(acuteEdge.get(0).getVertex().xf()+v.xf()),(double)(acuteEdge.get(0).getVertex().yf()+v.yf()),0);
				HE_Halfedge heac0_p=acuteEdge.get(0).getPair();
				HE_Halfedge heac1_p=acuteEdge.get(1).getPair();

				HE_Halfedge henew1=new HE_Halfedge();
				HE_Halfedge henew2=new HE_Halfedge();

				mesh.setVertex(acuteEdge.get(0),vNew);
				mesh.setVertex(acuteEdge.get(1),vNew);

				mesh.setVertex(henew1,v_ori);
				mesh.setVertex(henew2,vNew);

				mesh.setHalfedge(vNew,henew2);

//				mesh.setHalfedge(v_ori,henew1);

				mesh.setNext(heac1_p,henew2);
				mesh.setNext(henew2,he1);

				mesh.setNext(he0_p,henew1);
				mesh.setNext(henew1,acuteEdge.get(0));

				mesh.setNext(heac0_p,acuteEdge.get(1));

				mesh.setPair(henew1,henew2);
				mesh.setPair(acuteEdge.get(0),heac0_p);
				mesh.setPair(acuteEdge.get(1),heac1_p);

				if(heac1_p.getFace()!=null){
					mesh.setFace(henew2, heac1_p.getFace());
				}
				if(acuteEdge.get(0).getFace()!=null){
					mesh.setFace(henew1, he1.getFace());
				}

				mesh.addDerivedElement(vNew, new HE_Element[]{v_ori.getHalfedge(face)});
				mesh.addDerivedElement(henew1, new HE_Element[]{v_ori.getHalfedge(face)});
				mesh.addDerivedElement(henew2, new HE_Element[]{v_ori.getHalfedge(face)});

				mesh.update();
				return true;

			}

		}

		return false;
	}
}