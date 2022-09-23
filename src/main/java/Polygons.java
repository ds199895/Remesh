import guo_cam.CameraController;
import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PVector;
import wblut.geom.*;
import wblut.hemesh.*;
import wblut.processing.WB_Render;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import util.render.JtsRender;
@SuppressWarnings("serial")
public class Polygons extends PApplet {
    public static void main(String[] args) {
        PApplet.main("Polygons");
    }

    HE_Mesh mesh;
    WB_Render render;
    WB_GeometryFactory wg = new WB_GeometryFactory();
    CameraController cam;
    HE_Halfedge edge;
    HE_Vertex vertex;
    WB_Vector v;
    List<HE_Halfedge> acuteEdge = new ArrayList<>();
    List<HE_Vertex> newVers = new ArrayList<>();
    PFont font = createFont(PFont.list()[274], 10);

    public void setup() {
        size(1200, 1200, P3D);
//        cam=new CameraController(this,200);
//        cam.top();
//		this.createMesh();
//        try {
//            createVoronoi();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        render = new WB_Render(this);
        textFont(font);
        createMeshes();
        mesh=tri_mesh;
        controlEdgesLength();
//        setLabel(mesh);
    }

    public void draw() {
        background(255);

        controlEdgesLength();

        stroke(0);
        render.drawEdges(mesh);

        pushStyle();
        this.mesh.getVertices().forEach(v ->
                this.ellipse(v.xf(), v.yf(), 2, 2)
        );
        popStyle();

        pushStyle();
        fill(255, 0, 0);
        for (int i = 0; i < mesh.getVertices().size(); i++) {
            HE_Vertex ver = mesh.getVertices().get(i);
            if(ver.getUserLabel()!=-1) {
                WB_Vector dir = new WB_Vector(ver.xf() - ver.getHalfedge().getEndVertex().xf(), ver.yf() - ver.getHalfedge().getEndVertex().yf());
                WB_Vector norm = dir.getOrthoNormal2D();
                WB_Vector p = WB_Vector.add(ver, norm.scaleSelf(8));
                textAlign(CENTER);
                text(String.valueOf(ver.getUserLabel()), p.xf(), p.yf());
            }
        }
        popStyle();
        if (v_save.size() != 0) {
            pushStyle();
            fill(0, 255, 0);
            render.drawPoint2D(v_save, 10);
            popStyle();
        }
        pushStyle();
        if(fa!=null){
            fill(255,0, 0);
            for(HE_Face face:fa) {
                render.drawPolygonEdges(face.getPolygon());
            }

        }
        popStyle();
        pushStyle();
        if(thatFaces.size()!=0){
            fill(0,255, 0);
            for(HE_Face face:thatFaces) {
                render.drawPolygonEdges(face.getPolygon());
            }
//            render.drawFace(thatFaces.get(1));
        }
        popStyle();
        pushStyle();
        if(thatEdges!=null){
            strokeWeight(3);
            stroke(255,0, 0);
            for(HE_Halfedge edge:thatEdges) {
               line(edge.getVertex().xf(),edge.getVertex().yf(),edge.getEndVertex().xf(),edge.getEndVertex().yf());
            }

        }
        popStyle();
        pushStyle();
        if(acute.size()!=0){
            strokeWeight(3);
            stroke(0,255, 255);
            edge=acute.get(1).getPair().getNextInFace();
//            for(HE_Halfedge edge:acute) {
//                line(edge.getVertex().xf(),edge.getVertex().yf(),edge.getEndVertex().xf(),edge.getEndVertex().yf());
//            }
            line(edge.getVertex().xf(),edge.getVertex().yf(),edge.getEndVertex().xf(),edge.getEndVertex().yf());
        }
        popStyle();

        pushStyle();
        if(newH!=null){
            strokeWeight(3);
            stroke(0,255, 255);
            edge=newH;
//            for(HE_Halfedge edge:acute) {
//                line(edge.getVertex().xf(),edge.getVertex().yf(),edge.getEndVertex().xf(),edge.getEndVertex().yf());
//            }
            line(edge.getVertex().xf(),edge.getVertex().yf(),edge.getEndVertex().xf(),edge.getEndVertex().yf());
            render.drawPoint2D(edge.getVertex(),2);
            render.drawPoint2D(edge.getEndVertex(),1);
        }
        popStyle();
        pushStyle();
        if(v_o.size()!=0){
            fill(0,255, 255);
//            for(HE_Halfedge edge:acute) {
//                line(edge.getVertex().xf(),edge.getVertex().yf(),edge.getEndVertex().xf(),edge.getEndVertex().yf());
//            }
            for(HE_Vertex v:v_o){
                render.drawPoint2D(v,3);
            }

        }
        popStyle();
        pushStyle();
        if(v_n.size()!=0){
            fill(0,255, 0);
//            for(HE_Halfedge edge:acute) {
//                line(edge.getVertex().xf(),edge.getVertex().yf(),edge.getEndVertex().xf(),edge.getEndVertex().yf());
//            }
            for(HE_Vertex v:v_n){
                render.drawPoint2D(v,5);
            }

        }
        popStyle();
    }

    HE_Mesh dual;
    HE_Mesh tri_mesh;
    private void createMeshes(){
		mesh = new HE_Mesh(new HEC_Cube().setEdge(4000));
		//mesh.smooth();
		HEC_Dual creator = new HEC_Dual();
		creator.setSource(mesh);
		dual = new HE_Mesh(creator);

		WB_Transform3D tran = new WB_Transform3D();
		tran.addRotateAboutAxis(PI/6,new WB_Point(0,0,0),new WB_Point(0,0,1));
		tran.addTranslate(new WB_Vector(-4000,2000,4000));

		mesh.applySelf(tran);
		dual.applySelf(tran);

		WB_Point[] points = new WB_Point[121];
		int index = 0;
		for (int j = 0; j < 11; j++) {
			for (int i = 0; i < 11; i++) {
				points[index] = new WB_Point(0 + i * 120 + (((i != 0) && (i != 10)) ? random(-20, 20) : 0),
						120+ j * 40 + (((j != 0) && (j != 10)) ? random(-20, 20) : 0),
						sin(TWO_PI / 20 * i) * 40 + cos(TWO_PI / 10 * j) * 40);
				index++;
			}
		}

		// create triangles from point grid
		WB_Triangle[] tris = new WB_Triangle[200];

		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				tris[2 * (i + 10 * j)] = new WB_Triangle(points[i + 11 * j], points[i + 1 + 11 * j],
						points[i + 11 * j + 11]);
				tris[2 * (i + 10 * j) + 1] = new WB_Triangle(points[i + 1 + 11 * j], points[i + 11 * j + 12],
						points[i + 11 * j + 11]);
			}
		}

		HEC_FromTriangles tri_creator = new HEC_FromTriangles();
		tri_creator.setTriangles(tris);
		// alternatively tris can be any Collection<WB_Triangle>
		tri_mesh = new HE_Mesh(tri_creator);
	}
    public void createVoronoi() throws IOException {
        int num =500;
        WB_Polygon p = wg.createSimplePolygon(new WB_Point[]{
                new WB_Point(0, 0),
                new WB_Point(width, 0),
                new WB_Point(width, height),
                new WB_Point(0, height),
        });



        mesh=setVoronoi(p,num);
//        List<WB_Coord> points = createPoints(p, num);
//        StringBuilder sb=new StringBuilder();
//        for(WB_Coord ps:points){
//            sb.append(ps.xd()+","+ps.yd()+","+ps.zd()+"\n");
//        }
//        String requestData = sb.toString();
//        String saveFilePath = "E:\\1.txt";
////        saveToFile(requestData,saveFilePath);
//        List<WB_Coord> points=readFileContent(saveFilePath);
//        mesh = voronoiMesh(p, points);
    }
    public void saveToFile(String requestData, String saveFilePath) throws IOException {
        FileWriter fw = new FileWriter(saveFilePath);
        fw.write(requestData);
        fw.close();
        println("saved");
    }

    public static List<WB_Coord> readFileContent(String fileName) {
        List<WB_Coord>cs=new ArrayList<>();
        File file = new File(fileName);
        BufferedReader reader = null;
//        StringBuffer sbf = new StringBuffer();
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempStr;
            while ((tempStr = reader.readLine()) != null) {
//                sbf.append(tempStr);
                String[] s=tempStr.split(",");
                cs.add(new WB_Point(Double.valueOf(s[0]),Double.valueOf(s[1]),Double.valueOf(s[2])));
            }
            reader.close();
            return cs;
//            return sbf.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return cs;
//        return sbf.toString();
    }


    private void createMesh() {
        // create base points for a single hexagon

        WB_Polygon[] polygons = new WB_Polygon[4];
        WB_Point[] basepoints1 = new WB_Point[4];
        WB_Point[] basepoints2 = new WB_Point[3];
        WB_Point[] basepoints3 = new WB_Point[3];
        WB_Point[] basepoints4 = new WB_Point[4];
        basepoints1 = new WB_Point[]{
                new WB_Point(100, 200),
                new WB_Point(300, 200),
                new WB_Point(300, 400),
                new WB_Point(100, 400)
        };
        basepoints2 = new WB_Point[]{
                new WB_Point(300, 200),
                new WB_Point(400, 100),
                new WB_Point(450, 180)
        };
        basepoints3 = new WB_Point[]{
                new WB_Point(100, 200),
                new WB_Point(400, 100),
                new WB_Point(300, 200)
        };
        basepoints4 = new WB_Point[]{
                new WB_Point(450, 180),
                new WB_Point(300, 400),
//				new WB_Point(100,200),
                new WB_Point(300, 200),
        };

        polygons[0] = wg.createSimplePolygon(basepoints1);
        polygons[1] = wg.createSimplePolygon(basepoints2);
        polygons[2] = wg.createSimplePolygon(basepoints3);
        polygons[3] = wg.createSimplePolygon(basepoints4);
        HEC_FromPolygons creator = new HEC_FromPolygons();

        creator.setPolygons(polygons);
        // alternatively polygons can be any Collection<WB_Polygon>
        mesh = new HE_Mesh(creator);
        // Uncomment for a fun little combination with HEC_FromFrame
        // mesh=new HE_Mesh(new HEC_FromFrame().setFrame(mesh));
    }

    double gap = 80;

    private void controlEdgesLength() { // gap 需要动态确定*******
        boolean done = false;
        while (!done) {
            done = true;
            for (HE_Halfedge he : this.mesh.getHalfedges()) {
                double length = he.getLength();
                if (length > gap) {
                    // edge 长度大于 gap 则在该 edge 中间加入新 HE_Vertex
                    done = false;
                    HE_MeshOp.splitEdge(this.mesh, he);
                    HE_Vertex v1=new HE_Vertex(he.getCenter());
                    HE_Face f=he.getFace();
                    HE_Vertex v_opp=he.getNextInFace().getEndVertex();
//                    HE_MeshOp.splitFace(mesh, f, v1, v_opp);
                    break;
                }
                if (length < 0.2*gap) { // gap 需要动态确定*******
                    // edge 长度大于 小于 gap 的 1/10 则端点合并 (删除该边)
                    done = false;
                    HE_MeshOp.collapseEdge(this.mesh, he);
                }
            }
        }
    }

    HE_Vertex nearPnt = null;

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
    static List<HE_Face> fa=new ArrayList<>();
    public void keyReleased() {
        switch (key) {
            case 'a':
                v_save.clear();
                fa.clear();
                collapseSharpAngleEdge(mesh, 10, PI /2);
//                reduceVertexDegree(mesh, 10,  PI /6);

                println("end!");
        }
    }
    public List<HE_Face>getAroundFaces(HE_Face face){
        List<HE_Face>aroundFaces=new ArrayList<>();
        for(HE_Vertex v:face.getFaceVertices()) {
            for (HE_Face f : v.getFaceStar()) {
                if (!aroundFaces.contains(f)) {
                    aroundFaces.add(f);
                }
            }

        }
        aroundFaces.remove(face);

        return aroundFaces;
    }

    public static void setLabel(HE_Mesh mesh) {
        for (int i = 0; i < mesh.getFaces().size(); i++) {
            mesh.getFaces().get(i).setUserLabel(i);
            for (HE_Vertex v : mesh.getFaces().get(i).getFaceVertices()) {
                v.setUserLabel(i);
            }
        }
    }

    public List<HE_Halfedge> getacuteEdges(HE_Vertex vertex) {
        List<HE_Halfedge> edges = new ArrayList<>();
        List<HE_Halfedge> star = edge.getVertex().getHalfedgeStar();
        double minAngle = Double.MAX_VALUE;
        int id = -1;
        for (int i = 0; i < star.size(); i++) {
            int prev = (i - 1 + star.size()) % star.size();
            float angle = (float) (RAD_TO_DEG * HE_MeshOp.getAngle(star.get(i)));
            println(angle);
            if (angle < minAngle) {
                minAngle = angle;
                id = i;
            }
        }
        println(minAngle + "    " + id);
        if (minAngle < 90) {
            int prv = (id - 1 + star.size()) % star.size();
            edges.add(star.get(prv));
            edges.add(star.get(id));
        }


        return edges;
    }

    //1.0版本
    public void add(HE_Mesh mesh, HE_Halfedge edge, WB_Coord v) {
        List<HE_Halfedge> edges = edge.getVertex().getEdgeStar();
        HE_Halfedge he0 = edges.get(0);
        HE_Halfedge he1 = edges.get(1);
        HE_Halfedge he1_p = he0.getPair();

        HE_Halfedge he2 = edges.get(2);

        HE_Halfedge he3 = edges.get(3);
        HE_Halfedge he3_p = he0.getPair();

        HE_Vertex vNew = new HE_Vertex(v);
        HE_Halfedge henew1 = new HE_Halfedge();
        HE_Halfedge henew2 = new HE_Halfedge();


        mesh.setVertex(he3, vNew);
        mesh.setVertex(he2, vNew);

        mesh.setVertex(henew1, edge.getVertex());
        mesh.setNext(he1_p, henew1);
        mesh.setNext(henew1, he2);

        mesh.setVertex(henew2, vNew);
        mesh.setNext(he3_p, henew2);
        mesh.setNext(henew2, he0);


        mesh.setHalfedge(vNew, he2);
        mesh.setPair(henew1, henew2);

        if (he0.getFace() != null) {
            mesh.setFace(henew2, he0.getFace());
        }
        if (he1.getFace() != null) {
            mesh.setFace(henew1, he1.getFace());
        }
        mesh.addDerivedElement(vNew, new HE_Element[]{edge});
        mesh.addDerivedElement(henew1, new HE_Element[]{edge});
        mesh.addDerivedElement(henew2, new HE_Element[]{edge});
    }

    //2.0版本
    public void addVertex(HE_Mesh mesh, List<HE_Halfedge> acuteEdge, double d) {
        HE_Vertex v_ori = acuteEdge.get(0).getVertex();
        WB_Vector v1 = new WB_Vector(acuteEdge.get(0).getEndVertex().xf() - acuteEdge.get(0).getVertex().xf(), acuteEdge.get(0).getEndVertex().yf() - acuteEdge.get(0).getVertex().yf());
        WB_Vector v2 = new WB_Vector(acuteEdge.get(1).getEndVertex().xf() - acuteEdge.get(1).getVertex().xf(), acuteEdge.get(1).getEndVertex().yf() - acuteEdge.get(1).getVertex().yf());
        v = WB_Vector.add(v1, v2);
        v = WB_Vector.div(v, v.getLength());
        v = v.mul(20.0f);
        HE_Halfedge he0_p = acuteEdge.get(0).getPrevInFace();

        HE_Halfedge he1_p = acuteEdge.get(1).getNextInFace();
        if (he1_p.getVertex() == acuteEdge.get(1).getEndVertex()) {
            he1_p = acuteEdge.get(1).getPair().getNextInFace().getPair();
        }
        if (he0_p.getVertex() == acuteEdge.get(0).getEndVertex()) {
            he0_p = acuteEdge.get(0).getPair().getNextInFace().getPair();
        }
        HE_Halfedge he1 = he1_p.getPair();

        HE_Vertex vNew = new HE_Vertex((double) (acuteEdge.get(0).getVertex().xf() + v.xf()), (double) (acuteEdge.get(0).getVertex().yf() + v.yf()), 0);
        HE_Halfedge heac0_p = acuteEdge.get(0).getPair();
        HE_Halfedge heac1_p = acuteEdge.get(1).getPair();

        HE_Halfedge henew1 = new HE_Halfedge();
        HE_Halfedge henew2 = new HE_Halfedge();

        mesh.setVertex(acuteEdge.get(0), vNew);
        mesh.setVertex(acuteEdge.get(1), vNew);

        mesh.setVertex(henew1, v_ori);
        mesh.setVertex(henew2, vNew);

        mesh.setHalfedge(vNew, henew2);
        mesh.setHalfedge(v_ori, henew1);

        mesh.setNext(heac1_p, henew2);
        mesh.setNext(henew2, he1);

        mesh.setNext(he0_p, henew1);
        mesh.setNext(henew1, acuteEdge.get(0));

        mesh.setNext(heac0_p, acuteEdge.get(1));

        mesh.setPair(henew1, henew2);
        mesh.setPair(acuteEdge.get(0), heac0_p);
        mesh.setPair(acuteEdge.get(1), heac1_p);

        if (heac1_p.getFace() != null) {
            mesh.setFace(henew2, heac1_p.getFace());
        }
        if (acuteEdge.get(0).getFace() != null) {
            mesh.setFace(henew1, he1.getFace());
        }

        mesh.addDerivedElement(vNew, new HE_Element[]{edge});
        mesh.addDerivedElement(henew1, new HE_Element[]{edge});
        mesh.addDerivedElement(henew2, new HE_Element[]{edge});
    }
    public HE_Mesh voronoiMesh(WB_Polygon poly,List<WB_Coord>p_temp){
        List<WB_VoronoiCell2D> Vor = WB_VoronoiCreator.getClippedVoronoi2D(p_temp, poly).getCells();

        HE_Mesh meshp;
        List<WB_Polygon> lp = new ArrayList<WB_Polygon>();
        for (WB_VoronoiCell2D vor : Vor) {
            lp.add(vor.getPolygon());
        }

        HEC_FromPolygons hecp = new HEC_FromPolygons();
        hecp.setPolygons(lp);

        meshp = new HE_Mesh(hecp);

        return meshp;
    }

    public HE_Mesh setVoronoi(WB_Polygon poly, int pnts_num) {
        //�������һ���߽������ɵ�
//        WB_Coord[] coordinates = poly.getPoints().toArray();
//        ArrayList<Double> xList = new ArrayList<>();
//        ArrayList<Double> yList = new ArrayList<>();
//
//        for (int i = 0; i < coordinates.length; i++) {
//            xList.add(coordinates[i].xd());
//            yList.add(coordinates[i].yd());
//        }
//
//        int XindexMax = 0;
//        int XindexMin = 0;
//        int YindexMax = 0;
//        int YindexMin = 0;
//
//        for (int i = 0; i < xList.size(); i++) {
//            for (int j = 0; j < yList.size(); j++) {
//                if (xList.get(i) > xList.get(XindexMax)) {
//                    XindexMax = i;
//                }
//                if (xList.get(i) < xList.get(XindexMin)) {
//                    XindexMin = i;
//                }
//                if (yList.get(i) > yList.get(YindexMax)) {
//                    YindexMax = i;
//                }
//                if (yList.get(i) < yList.get(YindexMin)) {
//                    YindexMin = i;
//                }
//            }
//        }
//
//        Double xMax = xList.get(XindexMax);
//        Double xMin = xList.get(XindexMin);
//        Double yMax = yList.get(YindexMax);
//        Double yMin = yList.get(YindexMin); // xy �����Сֵ
//
//        // ��ǰ������
//        int pointCount = 0;
        List<WB_Coord> p_temp = new ArrayList<WB_Coord>();
//        while (pointCount < pnts_num) {
//            double rx = random(xMin, xMax);
//            double ry = random(yMin, yMax);
//            WB_Coord c = new WB_Point(rx, ry);
//
//            if (JtsRender.check_contain(poly, c)) {
//                p_temp.add(c);
//                pointCount++;
//            }
//        }
        for(int i=0;i<pnts_num;i++){
            p_temp.add(new WB_Point(random(0,width),random(0,height)));
        }

        List<WB_VoronoiCell2D> Vor = WB_VoronoiCreator.getClippedVoronoi2D(p_temp, poly).getCells();

        HE_Mesh meshp;
        List<WB_Polygon> lp = new ArrayList<WB_Polygon>();
        for (WB_VoronoiCell2D vor : Vor) {
            lp.add(vor.getPolygon());
        }

        HEC_FromPolygons hecp = new HEC_FromPolygons();
        hecp.setPolygons(lp);

        meshp = new HE_Mesh(hecp);

        return meshp;
    }

    public static double random(double d, double e) {
        double num = Math.random() * (e - d) + d;
        return num;
    }

    /**
     * 在 face 面上 临界角度为 thresholdAngle 值的顶角被塌陷掉 并生成该角的角平分线的新的半边, 重构半边引用关系
     * <p>
     * 贺思远
     */
    static List<HE_Vertex> v_save = new ArrayList<>();
    static List<HE_Face>thatFaces=new ArrayList<>();
    static List<HE_Halfedge>thatEdges=new ArrayList<>();
    static List<HE_Halfedge>acute=new ArrayList<>();
    static HE_Halfedge newH=null;
    static List<HE_Vertex>v_o=new ArrayList<>();
    static List<HE_Vertex>v_n=new ArrayList<>();
//    public static void collapseSharpAngle(HE_Mesh mesh, HE_Face face, double dist, double thresholdAngle) {
//        List<HE_Vertex> vs = face.getFaceVertices();
//        List<HE_Vertex> fvs = new ArrayList<>();
//        vs.forEach(v -> fvs.add(v));
//        for (int i = 0; i < fvs.size(); i++) {
//            HE_Vertex v_ori = fvs.get(i);
//
//            HE_Halfedge nxt_out = v_ori.getHalfedge(face);
//            if (nxt_out == null) {
//                println("here!   " + i);
//                v_save.add(v_ori);
//            }
//            if (nxt_out == null) continue;
//            HE_Halfedge prv_out = nxt_out.getPrevInFace().getPair();
//            WB_Vector nxt_v = new WB_Vector(nxt_out.getEndVertex().xd() - nxt_out.getStartVertex().xd(), nxt_out.getEndVertex().yd() - nxt_out.getStartVertex().yd());
//            WB_Vector prv_v = new WB_Vector(prv_out.getEndVertex().xd() - prv_out.getStartVertex().xd(), prv_out.getEndVertex().yd() - prv_out.getStartVertex().yd());
//
//            double angle = nxt_v.getAngle(prv_v);
//
//            if (angle < thresholdAngle) {
//                if (v_ori.isBoundary()) {
//                    continue;
//                }
//                System.out.println("isNotBoundary");
//
//                WB_Vector v = WB_Vector.add(nxt_v, prv_v);
//                v.normalizeSelf();
//                v.scaleSelf(dist);
//
//
//                List<HE_Halfedge> acuteEdge = new ArrayList<>();
//                acuteEdge.add(prv_out);
//                acuteEdge.add(nxt_out);
//                HashMap<HE_Face, HE_Halfedge> face_edges = new HashMap<>();
//
//                for (int j = 0; j < mesh.getFaces().size(); j++) {
//                    HE_Face f = mesh.getFaces().get(j);
//                    if (f != face && f != prv_out.getFace() && f != nxt_out.getFace()) {
//                        face_edges.put(mesh.getFaces().get(j), v_ori.getHalfedge(f));
//                    }
//                }
//
//                //###############
//
//                HE_Halfedge he0_p = acuteEdge.get(0).getPrevInFace();
//
//                HE_Halfedge he1_p = acuteEdge.get(1).getNextInFace();
//                if (he1_p.getVertex() == acuteEdge.get(1).getEndVertex()) {
//                    he1_p = acuteEdge.get(1).getPair().getNextInFace().getPair();
//                }
//                if (he0_p.getVertex() == acuteEdge.get(0).getEndVertex()) {
//                    he0_p = acuteEdge.get(0).getPair().getNextInFace().getPair();
//                }
//                HE_Halfedge he1 = he1_p.getPair();
//                HE_Halfedge he0 = he0_p.getPair();
//                HE_Face f_op = he0.getFace();
//                HE_Face f_nex = he1.getFace();
//                HE_Face f_pre = acuteEdge.get(0).getFace();
//
//                HE_Vertex vNew = new HE_Vertex((double) (acuteEdge.get(0).getVertex().xf() + v.xf()), (double) (acuteEdge.get(0).getVertex().yf() + v.yf()), 0);
//
//                boolean isContain = WB_GeometryOp.contains2D(vNew, face.getPolygon());
//                if (!isContain) {
//                    continue;
//                }
//
//                HE_Halfedge heac0_p = acuteEdge.get(0).getPair();
//                HE_Halfedge heac1_p = acuteEdge.get(1).getPair();
//
//                HE_Halfedge henew1 = new HE_Halfedge();
//                HE_Halfedge henew2 = new HE_Halfedge();
//
//                mesh.setVertex(acuteEdge.get(0), vNew);
//                mesh.setVertex(acuteEdge.get(1), vNew);
//
//                mesh.setVertex(henew1, v_ori);
//                mesh.setVertex(henew2, vNew);
//
//                mesh.setHalfedge(vNew, henew2);
//                mesh.setHalfedge(v_ori, henew1);
//
//                mesh.setVertex(he0, v_ori);
//                mesh.setNext(he1_p, he0);
//                mesh.setNext(heac1_p, henew2);
//                mesh.setNext(henew2, he1);
//
//                mesh.setNext(he0_p, henew1);
//                mesh.setNext(henew1, acuteEdge.get(0));
//
//                mesh.setNext(heac0_p, acuteEdge.get(1));
//
//                mesh.setPair(henew1, henew2);
//                mesh.setPair(acuteEdge.get(0), heac0_p);
//                mesh.setPair(acuteEdge.get(1), heac1_p);
//
//                if (f_nex != null) {
//                    mesh.setFace(henew2, f_nex);
//                }
//
//                if (f_pre != null) {
//                    mesh.setFace(henew1, f_pre);
//                }
//                if (f_op != null) {
//                    mesh.setFace(he0, f_op);
//                }
//
//                mesh.addDerivedElement(vNew, new HE_Element[]{v_ori.getHalfedge(face)});
//                mesh.addDerivedElement(henew1, new HE_Element[]{v_ori.getHalfedge(face)});
//                mesh.addDerivedElement(henew2, new HE_Element[]{v_ori.getHalfedge(face)});
//
//                mesh.update();
//
//            }
//
//        }
//    }
    public void collapse(HE_Mesh mesh, HE_Vertex v_ori,HE_Face face,double dist, double thresholdAngle) {
        List<HE_Vertex> nei = v_ori.getNeighborVertices();

        HE_Halfedge nxt_out = v_ori.getHalfedge(face);

        if (nxt_out == null) {
            println("here!   " + vertex   + v_ori + "in face " + face);
            fa.add(face);
            v_save.add(v_ori);
//            thatFaces.addAll(v_ori.getFaceStar());
//            thatEdges.addAll(v_ori.getHalfedgeStar());
        }
        HE_Halfedge prv_out = nxt_out.getPrevInFace().getPair();
        WB_Vector nxt_v = new WB_Vector(nxt_out.getEndVertex().xd() - nxt_out.getStartVertex().xd(), nxt_out.getEndVertex().yd() - nxt_out.getStartVertex().yd());
        WB_Vector prv_v = new WB_Vector(prv_out.getEndVertex().xd() - prv_out.getStartVertex().xd(), prv_out.getEndVertex().yd() - prv_out.getStartVertex().yd());

        double angle = nxt_v.getAngle(prv_v);
        if (angle < thresholdAngle) {
//                    println("next Step!");
        } else {
//                    println("next Vertex!");
        }
        if (angle < thresholdAngle || v_ori.isBoundary()) {
            List<HE_Halfedge> acuteEdge = new ArrayList<>();
//                    println("this vertex is  "+v_ori);
//                    println("angle is  "+angle);
            if (nei.size() < 3) {
                System.out.println("isNotBoundary");

                WB_Vector v = WB_Vector.add(nxt_v, prv_v);
                v.normalizeSelf();
                v.scaleSelf(dist);

                acuteEdge.add(prv_out);
                acuteEdge.add(nxt_out);
                HE_Vertex vNew = new HE_Vertex((double) (acuteEdge.get(0).getVertex().xf() + v.xf()), (double) (acuteEdge.get(0).getVertex().yf() + v.yf()), 0);
                v_ori.set(vNew);
            } else {

                WB_Vector v = WB_Vector.add(nxt_v, prv_v);
                v.normalizeSelf();
                v.scaleSelf(dist);

                acuteEdge.add(prv_out);
                acuteEdge.add(nxt_out);
                HashMap<HE_Face, HE_Halfedge> face_edges = new HashMap<>();
                acute.addAll(acuteEdge);
                if (v_ori.isBoundary()) {
                    System.out.println("isBoundary");
                } else {
                    System.out.println("isNotBoundary");
                }
//                        v_save.add(v_ori);
                //###############
                HE_Halfedge heac0_p = acuteEdge.get(0).getPair();
                HE_Halfedge heac1_p = acuteEdge.get(1).getPair();

                HE_Halfedge he0 = acuteEdge.get(0).getPrevInFace().getPair();
                HE_Halfedge he1 =heac1_p.getNextInFace();
                HE_Halfedge he0_p = he0.getPair();
                HE_Halfedge he1_p = he1.getPair();


                HE_Halfedge henew1 = new HE_Halfedge();
                HE_Halfedge henew2 = new HE_Halfedge();
                newH=henew2;
//                        HE_Halfedge he0_p = acuteEdge.get(0).getPrevInFace();
//
//                        HE_Halfedge he1_p = acuteEdge.get(1).getNextInFace();
//                        if (he1_p.getVertex() == acuteEdge.get(1).getEndVertex()) {
//                            he1_p = acuteEdge.get(1).getPair().getNextInFace().getPair();
//                        }
//                        if (he0_p.getVertex() == acuteEdge.get(0).getEndVertex()) {
//                            he0_p = acuteEdge.get(0).getPair().getNextInFace().getPair();
//                        }
//                        HE_Halfedge he1 = he1_p.getPair();
//                        HE_Halfedge he0 = he0_p.getPair();

//                        HE_Face f_op = he0.getFace();
                HE_Face f_nex = heac1_p.getFace();
                HE_Face f_pre = acuteEdge.get(0).getFace();

                List<HE_Halfedge> v_edges = v_ori.getHalfedgeStar();
                List<HE_Face> v_faces = new ArrayList<>();
                List<HE_Halfedge>prv_edges=new ArrayList<>();
                v_edges.removeAll(acuteEdge);
                for (HE_Halfedge v_edge : v_edges) {
                    HE_Face v_face = v_edge.getFace();
                    prv_edges.add(v_edge.getPrevInFace());
                    v_faces.add(v_face);
                }


                HE_Vertex vNew = new HE_Vertex((double) (acuteEdge.get(0).getVertex().xf() + v.xf()), (double) (acuteEdge.get(0).getVertex().yf() + v.yf()), 0);

                boolean isContain = WB_GeometryOp.contains2D(vNew, face.getPolygon());

                //夹锐角的两半边起点更新
                mesh.setVertex(acuteEdge.get(0), vNew);
                mesh.setVertex(acuteEdge.get(1), vNew);

                //建立新加点与原先点相互引用的半边
                mesh.setVertex(henew1, v_ori);
                mesh.setVertex(henew2, vNew);

                //将前述半边更新为新加点和原始点的根半边
                mesh.setHalfedge(vNew, henew2);
                mesh.setHalfedge(v_ori, henew1);

                //半边对应用关系更新
                mesh.setPair(henew1, henew2);
                mesh.setPair(acuteEdge.get(0), heac0_p);
                mesh.setPair(acuteEdge.get(1), heac1_p);

                //将新添加的边放进面里
                if (f_nex != null) {
                    mesh.setFace(henew2, f_nex);
                }

                if (f_pre != null) {
                    mesh.setFace(henew1, f_pre);
                }
                //更新原先面对原先点的半边的引用
                for (int l = 0; l < v_edges.size(); l++) {
                    mesh.setFace(v_edges.get(l), v_faces.get(l));
                }
                //更新原先点半边的上一半边引用，确保通路
                for(int l=0;l<v_edges.size();l++){
                    mesh.setVertex(v_edges.get(l),v_ori);
                    mesh.setNext(prv_edges.get(l),v_edges.get(l));
                }

                //更新所有需要更新的半边顺序
                mesh.setNext(he0_p, henew1);
                mesh.setNext(henew1, acuteEdge.get(0));

                mesh.setNext(acuteEdge.get(1).getPair(), henew2);
                mesh.setNext(henew2, he1);

                mesh.setNext(heac0_p, acuteEdge.get(1));

                mesh.addDerivedElement(vNew, new HE_Element[]{nxt_out});
                mesh.addDerivedElement(henew1, new HE_Element[]{nxt_out});
                mesh.addDerivedElement(henew2, new HE_Element[]{nxt_out});

                mesh.update();
            }
        }
    }
    public static void collapseSharpAngleEdge1(HE_Mesh mesh, double dist, double thresholdAngle) {
//        boolean noSharp = true;
        for (int k = 0; k < mesh.getFaces().size(); k++) {
//            println("it's the face of " + k);
            HE_Face face = mesh.getFaceWithIndex(k);
            if(face!=null) {
//                println("get the Face" );
            }else{
//                println("get the Face" +null);
            }

            List<HE_Vertex> vs = face.getFaceVertices();
//            println("get all points");
//			List<HE_Vertex> fvs = new ArrayList<>();
//			vs.forEach(v -> fvs.add(v));
//            println(face);
            for (int i = 0; i < vs.size(); i++) {
//                println("Number   "+i);
                HE_Vertex v_ori = vs.get(i);
//                if (v_ori.getKey()==85)continue;

//                println("this vertex is  "+v_ori);
                List<HE_Vertex> nei = v_ori.getNeighborVertices();

                HE_Halfedge nxt_out = v_ori.getHalfedge(face);

                if (nxt_out == null) {
                    println("here!   "+i +"vertex  "+v_ori+"in face "+face);
                        fa.add(face);
                        v_save.add(v_ori);
                        thatFaces.addAll(v_ori.getFaceStar());
                        thatEdges.addAll(v_ori.getHalfedgeStar());
                }
                if (nxt_out == null) continue;
                HE_Halfedge prv_out = nxt_out.getPrevInFace().getPair();
                WB_Vector nxt_v = new WB_Vector(nxt_out.getEndVertex().xd() - nxt_out.getStartVertex().xd(), nxt_out.getEndVertex().yd() - nxt_out.getStartVertex().yd());
                WB_Vector prv_v = new WB_Vector(prv_out.getEndVertex().xd() - prv_out.getStartVertex().xd(), prv_out.getEndVertex().yd() - prv_out.getStartVertex().yd());

                double angle = nxt_v.getAngle(prv_v);

                if (angle<thresholdAngle || v_ori.isBoundary()) {
                    List<HE_Halfedge> acuteEdge = new ArrayList<>();

                    if (nei.size() < 3) {
                        if (v_ori.isBoundary()) {
                            continue;
                        }
                        System.out.println("isNotBoundary");

                        WB_Vector v = WB_Vector.add(nxt_v, prv_v);
                        v.normalizeSelf();
                        v.scaleSelf(dist);

                        acuteEdge.add(prv_out);
                        acuteEdge.add(nxt_out);
                        HE_Vertex vNew = new HE_Vertex((double) (acuteEdge.get(0).getVertex().xf() + v.xf()), (double) (acuteEdge.get(0).getVertex().yf() + v.yf()), (double)(acuteEdge.get(0).getVertex().zf()+ v.yf()));
                        v_ori.set(vNew);
                    } else {

                        WB_Vector v = WB_Vector.add(nxt_v, prv_v);
                        v.normalizeSelf();
                        v.scaleSelf(dist);

                        acuteEdge.add(prv_out);
                        acuteEdge.add(nxt_out);
                        if (v_ori.isBoundary()) {
                            System.out.println("isBoundary");

                            if (acuteEdge.get(0).isInnerBoundary() || acuteEdge.get(0).isOuterBoundary() || acuteEdge.get(1).isInnerBoundary() || acuteEdge.get(1).isOuterBoundary()) {
                                if (nei.size() > 3) {
                                    continue;
                                }
                            }
                        } else {
                            System.out.println("isNotBoundary");
                        }
//                        v_save.add(v_ori);
                        //###############
                        HE_Halfedge heac0_p = acuteEdge.get(0).getPair();
                        HE_Halfedge heac1_p = acuteEdge.get(1).getPair();

                        HE_Halfedge he0 = acuteEdge.get(0).getPrevInFace().getPair();
                        HE_Halfedge he1 = heac1_p.getNextInFace();
                        HE_Halfedge he0_p = he0.getPair();
                        HE_Halfedge he1_p = he1.getPair();

                        HE_Halfedge henew1 = new HE_Halfedge();
                        HE_Halfedge henew2 = new HE_Halfedge();

                        HE_Face f_nex = heac1_p.getFace();
                        HE_Face f_pre = acuteEdge.get(0).getFace();

                        List<HE_Halfedge> v_edges = v_ori.getHalfedgeStar();
                        List<HE_Face> v_faces = new ArrayList<>();
                        List<HE_Halfedge> prv_edges = new ArrayList<>();
                        v_edges.removeAll(acuteEdge);
                        for (HE_Halfedge v_edge : v_edges) {
                            HE_Face v_face = v_edge.getFace();
                            prv_edges.add(v_edge.getPrevInFace());
                            v_faces.add(v_face);
                        }

                        HE_Vertex vNew = new HE_Vertex((double) (acuteEdge.get(0).getVertex().xf() + v.xf()), (double) (acuteEdge.get(0).getVertex().yf() + v.yf()), 0);

                        boolean isContain = WB_GeometryOp.contains2D(vNew, face.getPolygon());
                        if (!isContain) {
                            continue;
                        }

                        //夹锐角的两半边起点更新
                        if (acuteEdge.get(0).isInnerBoundary()) {
                            if (nei.size() == 3) {
                            mesh.setVertex(acuteEdge.get(1), vNew);

                            //建立新加点与原先点相互引用的半边
                            mesh.setVertex(henew1, v_ori);
                            mesh.setVertex(henew2, vNew);

                            //将前述半边更新为新加点和原始点的根半边
                            mesh.setHalfedge(vNew, henew2);
                            mesh.setHalfedge(v_ori, henew1);

                            //半边对引用关系更新
                            mesh.setPair(henew1, henew2);
                            mesh.setPair(acuteEdge.get(0), heac0_p);
                            mesh.setPair(acuteEdge.get(1), heac1_p);

                            //将新添加的边放进面里
                            mesh.setFace(henew2,acuteEdge.get(0).getFace());
                            mesh.setFace(henew1,acuteEdge.get(1).getFace());
                            //更新原先面对原先点的半边的引用
                            for (int l = 0; l < v_edges.size(); l++) {
                                mesh.setFace(v_edges.get(l), v_faces.get(l));
                            }
                            //更新原先点半边的上一半边引用，确保通路
                            for(int l=0;l<v_edges.size();l++){
                                mesh.setVertex(v_edges.get(l),v_ori);
                                mesh.setNext(prv_edges.get(l),v_edges.get(l));
                            }

                            //更新所有需要更新的半边顺序
                            mesh.setNext(he1_p, henew1);
                            mesh.setNext(henew1, acuteEdge.get(0));

                            mesh.setNext(acuteEdge.get(1).getPair(), henew2);
                            mesh.setNext(henew2, he1);

                            mesh.setNext(heac0_p, acuteEdge.get(1));
                            mesh.addDerivedElement(vNew, new HE_Element[]{nxt_out});
                            mesh.addDerivedElement(henew1, new HE_Element[]{nxt_out});
                            mesh.addDerivedElement(henew2, new HE_Element[]{nxt_out});
                            }
                        }
                        if (!v_ori.isBoundary()||nei.size()>3){
                            mesh.setVertex(acuteEdge.get(0), vNew);
                            mesh.setVertex(acuteEdge.get(1), vNew);

                            //建立新加点与原先点相互引用的半边
                            mesh.setVertex(henew1, v_ori);
                            mesh.setVertex(henew2, vNew);

                            //将前述半边更新为新加点和原始点的根半边
                            mesh.setHalfedge(vNew, henew2);
                            mesh.setHalfedge(v_ori, henew1);

                            //半边对应用关系更新
                            mesh.setPair(henew1, henew2);
                            mesh.setPair(acuteEdge.get(0), heac0_p);
                            mesh.setPair(acuteEdge.get(1), heac1_p);

                            //将新添加的边放进面里
                            if (f_nex != null) {
                                mesh.setFace(henew2, f_nex);
                            }

                            if (f_pre != null) {
                                mesh.setFace(henew1, f_pre);
                            }
                            //更新原先面对原先点的半边的引用
                            for (int l = 0; l < v_edges.size(); l++) {
                                mesh.setFace(v_edges.get(l), v_faces.get(l));
                            }
                            //更新原先点半边的上一半边引用，确保通路
                            for (int l = 0; l < v_edges.size(); l++) {
                                mesh.setVertex(v_edges.get(l), v_ori);
                                mesh.setNext(prv_edges.get(l), v_edges.get(l));
                            }

                            //更新所有需要更新的半边顺序
                            mesh.setNext(he0_p, henew1);
                            mesh.setNext(henew1, acuteEdge.get(0));

                            mesh.setNext(acuteEdge.get(1).getPair(), henew2);
                            mesh.setNext(henew2, he1);

                            mesh.setNext(heac0_p, acuteEdge.get(1));
                            mesh.addDerivedElement(vNew, new HE_Element[]{nxt_out});
                            mesh.addDerivedElement(henew1, new HE_Element[]{nxt_out});
                            mesh.addDerivedElement(henew2, new HE_Element[]{nxt_out});
                        }


                        mesh.update();

//                        noSharp = false;
                    }

                }
            }
//            println("face  "+k+"  finish!");
//            println("the Value of  "+noSharp);
//            if (!noSharp) {
//                break;
//            }
        }
    }
    public static void collapseSharpAngleEdge(HE_Mesh mesh, double dist, double thresholdAngle){
        boolean noSharp=true;
        for (int k= 0; k< mesh.getFaces().size();k++) {
            println("it's the face of " + k);
            HE_Face face=mesh.getFaces().get(k);
            List<HE_Vertex> vs = face.getFaceVertices();
//			List<HE_Vertex> fvs = new ArrayList<>();
//			vs.forEach(v -> fvs.add(v));
            for (int i = 0; i < vs.size(); i++) {
                HE_Vertex v_ori = vs.get(i);
                List<HE_Vertex> nei = v_ori.getNeighborVertices();

                HE_Halfedge nxt_out = v_ori.getHalfedge(face);
                if (nxt_out == null) continue;
                HE_Halfedge prv_out = nxt_out.getPrevInFace().getPair();
                WB_Vector nxt_v = new WB_Vector(nxt_out.getEndVertex().xd() - nxt_out.getStartVertex().xd(), nxt_out.getEndVertex().yd() - nxt_out.getStartVertex().yd());
                WB_Vector prv_v = new WB_Vector(prv_out.getEndVertex().xd() - prv_out.getStartVertex().xd(), prv_out.getEndVertex().yd() - prv_out.getStartVertex().yd());

                double angle = nxt_v.getAngle(prv_v);

                if (angle < thresholdAngle) {
                    List<HE_Halfedge> acuteEdge = new ArrayList<>();
                    if (nei.size() < 3){
                        if (v_ori.isBoundary()) {
                            continue;
                        }
                        System.out.println("isNotBoundary");

                        WB_Vector v = WB_Vector.add(nxt_v, prv_v);
                        v.normalizeSelf();
                        v.scaleSelf(dist);

                        acuteEdge.add(prv_out);
                        acuteEdge.add(nxt_out);
                        HE_Vertex vNew = new HE_Vertex((double) (acuteEdge.get(0).getVertex().xf() + v.xf()), (double) (acuteEdge.get(0).getVertex().yf() + v.yf()), 0);
                        v_ori.set(vNew);
                    } else{
                        if (v_ori.isBoundary()) {
                            println("isBoundary");
//								if(acuteEdge.get(0))
                        }else {
                            System.out.println("isNotBoundary");
                        }
                        WB_Vector v = WB_Vector.add(nxt_v, prv_v);
                        v.normalizeSelf();
                        v.scaleSelf(dist);

                        acuteEdge.add(prv_out);
                        acuteEdge.add(nxt_out);
                        HashMap<HE_Face, HE_Halfedge> face_edges = new HashMap<>();

                        for (int j = 0; j < mesh.getFaces().size(); j++) {
                            HE_Face f = mesh.getFaces().get(j);
                            if (f != face && f != prv_out.getFace() && f != nxt_out.getFace()) {
                                face_edges.put(mesh.getFaces().get(j), v_ori.getHalfedge(f));
                            }
                        }

                        //###############

                        HE_Halfedge he0_p = acuteEdge.get(0).getPrevInFace();

                        HE_Halfedge he1_p = acuteEdge.get(1).getNextInFace();
                        if (he1_p.getVertex() == acuteEdge.get(1).getEndVertex()) {
                            he1_p = acuteEdge.get(1).getPair().getNextInFace().getPair();
                        }
                        if (he0_p.getVertex() == acuteEdge.get(0).getEndVertex()) {
                            he0_p = acuteEdge.get(0).getPair().getNextInFace().getPair();
                        }


                        HE_Halfedge he1 = he1_p.getPair();
                        HE_Halfedge he0 = he0_p.getPair();
                        HE_Face f_op = he0.getFace();
                        HE_Face f_nex = he1.getFace();
                        HE_Face f_pre = acuteEdge.get(0).getFace();

                        HE_Vertex vNew = new HE_Vertex((double) (acuteEdge.get(0).getVertex().xf() + v.xf()), (double) (acuteEdge.get(0).getVertex().yf() + v.yf()), 0);

                        boolean isContain = WB_GeometryOp.contains2D(vNew, face.getPolygon());
                        if (!isContain) {
                            continue;
                        }

                        HE_Halfedge heac0_p = acuteEdge.get(0).getPair();
                        HE_Halfedge heac1_p = acuteEdge.get(1).getPair();

                        HE_Halfedge henew1 = new HE_Halfedge();
                        HE_Halfedge henew2 = new HE_Halfedge();

                        mesh.setVertex(acuteEdge.get(0), vNew);
                        mesh.setVertex(acuteEdge.get(1), vNew);

                        mesh.setVertex(henew1, v_ori);
                        mesh.setVertex(henew2, vNew);

                        mesh.setHalfedge(vNew, henew2);
                        mesh.setHalfedge(v_ori, henew1);

                        mesh.setVertex(he0, v_ori);
                        mesh.setNext(he1_p, he0);
                        mesh.setNext(heac1_p, henew2);
                        mesh.setNext(henew2, he1);

                        mesh.setNext(he0_p, henew1);
                        mesh.setNext(henew1, acuteEdge.get(0));

                        mesh.setNext(heac0_p, acuteEdge.get(1));

                        mesh.setPair(henew1, henew2);
                        mesh.setPair(acuteEdge.get(0), heac0_p);
                        mesh.setPair(acuteEdge.get(1), heac1_p);

                        if (f_nex != null) {
                            mesh.setFace(henew2, f_nex);
                        }

                        if (f_pre != null) {
                            mesh.setFace(henew1, f_pre);
                        }
                        if (f_op != null) {
                            mesh.setFace(he0, f_op);
                        }

                        mesh.addDerivedElement(vNew, new HE_Element[]{v_ori.getHalfedge(face)});
                        mesh.addDerivedElement(henew1, new HE_Element[]{v_ori.getHalfedge(face)});
                        mesh.addDerivedElement(henew2, new HE_Element[]{v_ori.getHalfedge(face)});

                        mesh.update();
                        noSharp = false;
                    }

                }
            }
            if(!noSharp){
                break;
            }
        }
    }
    public static void  reduceVertexDegree(HE_Mesh mesh, double dist, double thresholdAngle) {
//        boolean noSharp = true;
        for (int k = 0; k < mesh.getFaces().size(); k++) {
//            println("it's the face of " + k);
            HE_Face face = mesh.getFaceWithIndex(k);

            List<HE_Vertex> vs = face.getFaceVertices();
            for (int i = 0; i < vs.size(); i++) {
                HE_Vertex v_ori = vs.get(i);
                List<HE_Vertex> nei = v_ori.getNeighborVertices();

                HE_Halfedge nxt_out = v_ori.getHalfedge(face);

//                if (nxt_out == null) {
//                    println("here!   "+i +"vertex  "+v_ori+"in face "+face);
//                    fa.add(face);
//                    v_save.add(v_ori);
//                    thatFaces.addAll(v_ori.getFaceStar());
//                    thatEdges.addAll(v_ori.getHalfedgeStar());
//                }
//                if (nxt_out == null) continue;
//                HE_Halfedge prv_out = nxt_out.getPrevInFace().getPair();
//                WB_Vector nxt_v = new WB_Vector(nxt_out.getEndVertex().xd() - nxt_out.getStartVertex().xd(), nxt_out.getEndVertex().yd() - nxt_out.getStartVertex().yd());
//                WB_Vector prv_v = new WB_Vector(prv_out.getEndVertex().xd() - prv_out.getStartVertex().xd(), prv_out.getEndVertex().yd() - prv_out.getStartVertex().yd());

                if (v_ori.getVertexDegree()>4 || v_ori.isBoundary()) {
                    List<HE_Halfedge> acuteEdge = new ArrayList<>();
                    int prevCount=(int)(v_ori.getVertexDegree()/2);
//                    HE_Halfedge prv_out = nxt_out.getPrevInFace().getPair();
                    HE_Halfedge prv_out = nxt_out.getPrevInVertex(prevCount);
                    WB_Vector nxt_v = new WB_Vector(nxt_out.getEndVertex().xd() - nxt_out.getStartVertex().xd(), nxt_out.getEndVertex().yd() - nxt_out.getStartVertex().yd());
                    WB_Vector prv_v = new WB_Vector(prv_out.getEndVertex().xd() - prv_out.getStartVertex().xd(), prv_out.getEndVertex().yd() - prv_out.getStartVertex().yd());

                        WB_Vector v = WB_Vector.add(nxt_v, prv_v);
                        v.normalizeSelf();
                        v.scaleSelf(dist);

                        acuteEdge.add(prv_out);
                        acuteEdge.add(nxt_out);
                        if (v_ori.isBoundary()) {
                            System.out.println("isBoundary");

                            if (acuteEdge.get(0).isInnerBoundary() || acuteEdge.get(0).isOuterBoundary() || acuteEdge.get(1).isInnerBoundary() || acuteEdge.get(1).isOuterBoundary()) {
                                if (nei.size() > 3) {
                                    continue;
                                }
                            }
                        } else {
                            System.out.println("isNotBoundary");
                        }
//                        v_save.add(v_ori);
                        //###############
                        HE_Halfedge heac0_p = acuteEdge.get(0).getPair();
                        HE_Halfedge heac1_p = acuteEdge.get(1).getPair();

                        HE_Halfedge he0 = acuteEdge.get(0).getPrevInFace().getPair();
                        HE_Halfedge he1 = heac1_p.getNextInFace();
                        HE_Halfedge he0_p = he0.getPair();
                        HE_Halfedge he1_p = he1.getPair();

                        HE_Halfedge henew1 = new HE_Halfedge();
                        HE_Halfedge henew2 = new HE_Halfedge();
//                        HE_Halfedge he0_p = acuteEdge.get(0).getPrevInFace();
//
//                        HE_Halfedge he1_p = acuteEdge.get(1).getNextInFace();
//                        if (he1_p.getVertex() == acuteEdge.get(1).getEndVertex()) {
//                            he1_p = acuteEdge.get(1).getPair().getNextInFace().getPair();
//                        }
//                        if (he0_p.getVertex() == acuteEdge.get(0).getEndVertex()) {
//                            he0_p = acuteEdge.get(0).getPair().getNextInFace().getPair();
//                        }
//                        HE_Halfedge he1 = he1_p.getPair();
//                        HE_Halfedge he0 = he0_p.getPair();

//                        HE_Face f_op = he0.getFace();
                        HE_Face f_nex = heac1_p.getFace();
                        HE_Face f_pre = acuteEdge.get(0).getFace();

                        List<HE_Halfedge> v_edges = v_ori.getHalfedgeStar();
                        List<HE_Face> v_faces = new ArrayList<>();
                        List<HE_Halfedge> prv_edges = new ArrayList<>();
                        v_edges.removeAll(acuteEdge);
                        for (HE_Halfedge v_edge : v_edges) {
                            HE_Face v_face = v_edge.getFace();
                            prv_edges.add(v_edge.getPrevInFace());
                            v_faces.add(v_face);
                        }

                        HE_Vertex vNew = new HE_Vertex((double) (acuteEdge.get(0).getVertex().xf() + v.xf()), (double) (acuteEdge.get(0).getVertex().yf() + v.yf()), 0);
//                        v_o.add(v_ori);
//                        v_n.add(vNew);
                        boolean isContain = WB_GeometryOp.contains2D(vNew, face.getPolygon());
                        if (!isContain) {
                            continue;
                        }

                        //夹锐角的两半边起点更新
                        if (acuteEdge.get(0).isInnerBoundary()) {
                            if (nei.size() == 3) {
                                mesh.setVertex(acuteEdge.get(1), vNew);

                                //建立新加点与原先点相互引用的半边
                                mesh.setVertex(henew1, v_ori);
                                mesh.setVertex(henew2, vNew);

                                //将前述半边更新为新加点和原始点的根半边
                                mesh.setHalfedge(vNew, henew2);
                                mesh.setHalfedge(v_ori, henew1);

                                //半边对引用关系更新
                                mesh.setPair(henew1, henew2);
                                mesh.setPair(acuteEdge.get(0), heac0_p);
                                mesh.setPair(acuteEdge.get(1), heac1_p);

                                //将新添加的边放进面里
                                mesh.setFace(henew2,acuteEdge.get(0).getFace());
                                mesh.setFace(henew1,acuteEdge.get(1).getFace());
                                //更新原先面对原先点的半边的引用
                                for (int l = 0; l < v_edges.size(); l++) {
                                    mesh.setFace(v_edges.get(l), v_faces.get(l));
                                }
                                //更新原先点半边的上一半边引用，确保通路
                                for(int l=0;l<v_edges.size();l++){
                                    mesh.setVertex(v_edges.get(l),v_ori);
                                    mesh.setNext(prv_edges.get(l),v_edges.get(l));
                                }

                                //更新所有需要更新的半边顺序
                                mesh.setNext(he1_p, henew1);
                                mesh.setNext(henew1, acuteEdge.get(0));

                                mesh.setNext(acuteEdge.get(1).getPair(), henew2);
                                mesh.setNext(henew2, he1);

                                mesh.setNext(heac0_p, acuteEdge.get(1));
                                mesh.addDerivedElement(vNew, new HE_Element[]{nxt_out});
                                mesh.addDerivedElement(henew1, new HE_Element[]{nxt_out});
                                mesh.addDerivedElement(henew2, new HE_Element[]{nxt_out});
                            }
                        }
                        if (!v_ori.isBoundary()||nei.size()>3){
                            mesh.setVertex(acuteEdge.get(0), vNew);
                            mesh.setVertex(acuteEdge.get(1), vNew);

                            //建立新加点与原先点相互引用的半边
                            mesh.setVertex(henew1, v_ori);
                            mesh.setVertex(henew2, vNew);

                            //将前述半边更新为新加点和原始点的根半边
                            mesh.setHalfedge(vNew, henew2);
                            mesh.setHalfedge(v_ori, henew1);

                            //半边对应用关系更新
                            mesh.setPair(henew1, henew2);
                            mesh.setPair(acuteEdge.get(0), heac0_p);
                            mesh.setPair(acuteEdge.get(1), heac1_p);

                            //将新添加的边放进面里
                            if (f_nex != null) {
                                mesh.setFace(henew2, f_nex);
                            }

                            if (f_pre != null) {
                                mesh.setFace(henew1, f_pre);
                            }
                            //更新原先面对原先点的半边的引用
                            for (int l = 0; l < v_edges.size(); l++) {
                                mesh.setFace(v_edges.get(l), v_faces.get(l));
                            }
                            //更新原先点半边的上一半边引用，确保通路
                            for (int l = 0; l < v_edges.size(); l++) {
                                mesh.setVertex(v_edges.get(l), v_ori);
                                mesh.setNext(prv_edges.get(l), v_edges.get(l));
                            }

                            //更新所有需要更新的半边顺序
                            mesh.setNext(he0_p, henew1);
                            mesh.setNext(henew1, acuteEdge.get(0));

                            mesh.setNext(acuteEdge.get(1).getPair(), henew2);
                            mesh.setNext(henew2, he1);

                            mesh.setNext(heac0_p, acuteEdge.get(1));
                            mesh.addDerivedElement(vNew, new HE_Element[]{nxt_out});
                            mesh.addDerivedElement(henew1, new HE_Element[]{nxt_out});
                            mesh.addDerivedElement(henew2, new HE_Element[]{nxt_out});
                        }
                        mesh.update();
//                        noSharp = false;
                    }
            }
        }
    }
}