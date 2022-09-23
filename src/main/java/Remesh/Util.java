package Remesh;

import org.eclipse.collections.impl.list.mutable.FastList;
import wblut.geom.WB_Vector;
import wblut.hemesh.*;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class Util {
    public void Util(){

    }

    public static void applyFlip(HE_Mesh mesh) {
        int devpre = 0;
        int devpost = 0;

        for(int r = 0; r < 2; ++r) {
            List<HE_Halfedge> edges = new FastList();
            edges.addAll(mesh.getEdges());
            Collections.shuffle(edges);
            Iterator var11 = edges.iterator();

            while(var11.hasNext()) {
                HE_Halfedge e = (HE_Halfedge)var11.next();
                if (!e.isInnerBoundary()) {
                    HE_Vertex a = e.getVertex();
                    HE_Vertex b = e.getEndVertex();
                    HE_Vertex c = e.getNextInFace().getEndVertex();
                    HE_Vertex d = e.getPair().getNextInFace().getEndVertex();
                    devpre = Math.abs((a.isBoundary() ? 4 : 6) - a.getVertexDegree());
                    devpre += Math.abs((b.isBoundary() ? 4 : 6) - b.getVertexDegree());
                    devpre += Math.abs((c.isBoundary() ? 4 : 6) - c.getVertexDegree());
                    devpre += Math.abs((d.isBoundary() ? 4 : 6) - d.getVertexDegree());
                    if (devpre > 0) {
                        flipEdge(mesh, e);
                        devpost = Math.abs((a.isBoundary() ? 4 : 6) - a.getVertexDegree());
                        devpost += Math.abs((b.isBoundary() ? 4 : 6) - b.getVertexDegree());
                        devpost += Math.abs((b.isBoundary() ? 4 : 6) - c.getVertexDegree());
                        devpost += Math.abs((b.isBoundary() ? 4 : 6) - d.getVertexDegree());
                        if (devpre <= devpost) {
                            flipEdge(mesh, e);
                        }
                    }
                }
            }
        }
    }
    public static void collapse(HE_Mesh mesh,double gap){
        boolean done = false;
        while (!done) {
            done = true;
            for (HE_Halfedge he : mesh.getHalfedges()) {
                double length = he.getLength();
                if (length >4/3.* gap) {
                    // edge 长度大于 gap 则在该 edge 中间加入新 HE_Vertex
                    done = false;
                    HE_MeshOp.splitEdge(mesh, he);
                    break;
                }
                if (length <0.2*gap) { // gap 需要动态确定*******
                    // edge 长度大于 小于 gap 的 1/10 则端点合并 (删除该边)
                    done = false;
                    HE_MeshOp.collapseEdge(mesh, he);
                }
            }
        }
    }
    public static double getSplitDistance(HE_Mesh mesh){
        double target_Length=0;
        for(HE_Halfedge he:mesh.getHalfedges()){
            target_Length+=he.getLength();
        }
        target_Length/=mesh.getNumberOfHalfedges();
        return target_Length;
    }
    public static void SplitAllLongEdges(HE_Mesh mesh,double target) {
        double low = 4/5.*target;
        double high = 4/3.*target;
        boolean done=false;
        while (!done) {
            done=true;
            for (HE_Halfedge he : mesh.getHalfedges()) {
                if (he.getLength() > high) {

                    if (!he.isOuterBoundary() && !he.isInnerBoundary()) {      //判断半边是否是边界
                        done=false;
                        if (he.getLength() > he.getNextInFace().getLength()
                                && he.getLength() > he.getPrevInFace().getLength()
                                && he.getPair().getLength() > he.getPair().getNextInFace().getLength()
                                && he.getPair().getLength() > he.getPair().getPrevInFace().getLength()) {

                            HE_Face f1 = he.getFace();             //f1：半边的相邻面
                            HE_Face f2 = he.getPair().getFace();             //f2：相邻半边的相邻面

                            HE_Vertex v1_opp = he.getPrevInFace().getVertex();              // v1_opp：半边相邻三角面分裂边的另一个顶点
                            HE_Vertex v2_opp = he.getPair().getPrevInFace().getVertex();      // v2_opp：半边相邻三角面分裂边的另一个顶点


                            HE_MeshOp.splitEdge(mesh, he);        //长边从中点分割一半
                            HE_Vertex v1 = he.getEndVertex();           //新的端点（半边的中点）位置更新,注意不能写成简单 HE_Vertex v1=new HE_Vertex(mid);得把网格中的vertex赋予v1

                            HE_MeshOp.splitFace(mesh, f1, v1, v1_opp);
                            HE_MeshOp.splitFace(mesh, f2, v1, v2_opp);
                        }
//                        break;
                    } else if (he.isInnerBoundary()) {
                        done=false;
                        if (he.getLength() > he.getNextInFace().getLength() && he.getLength() > he.getPrevInFace().getLength()) {
                            HE_Face f1 = he.getFace();             //f1：半边的相邻面

                            HE_Vertex v1_opp = he.getPrevInFace().getVertex();              // v1_opp：半边相邻三角面分裂边的另一个顶点

                            HE_MeshOp.splitEdge(mesh, he);        //长边从中点分割一半
                            HE_Vertex v1 = he.getEndVertex();           //新的端点（半边的中点）位置更新,注意不能写成简单 HE_Vertex v1=new HE_Vertex(mid);得把网格中的vertex赋予v1
                            HE_MeshOp.splitFace(mesh, f1, v1, v1_opp);
                        }
//                        break;
                    }
                    else if (he.isOuterBoundary()) {
                        continue;
                    }

                }
            }
        }
    }

    public static void flipEdgeControl(HE_Mesh mesh,int Max) {
//        for(HE_Vertex v:mesh.getVertices()){
//            if(!v.isBoundary()) {
//                List<HE_Halfedge> edges = v.getHalfedgeStar();
//                double MaxAngle = 0;
//                HE_Halfedge edge_to_flip = null;
//                for (HE_Halfedge he : edges) {
//                    if (MaxAngle < getHalfEdgeAngle(he)) {
//                        MaxAngle = getHalfEdgeAngle(he);
//                        edge_to_flip = he;
//                    }
//                }
//                HE_Vertex op_ver1 = edge_to_flip.getNextInFace().getEndVertex();
//                HE_Vertex op_ver2 = edge_to_flip.getPair().getNextInFace().getEndVertex();
//                if (op_ver1.getHalfedgeStar().size() < 5 && op_ver2.getHalfedgeStar().size() < 5) {
//                    flipEdge(mesh, edge_to_flip);
//                }
//            }
//        }
        for(HE_Vertex v:mesh.getVertices()){
            if(v.getHalfedgeStar().size()>5) {
                if (!v.isBoundary()) {
//                System.out.println(v);
                    List<HE_Halfedge> edges = v.getHalfedgeStar();
//                    double MaxAngle = 0;
//                    HE_Halfedge edge_to_flip = null;
//                    for (HE_Halfedge he : edges) {
//                        if (MaxAngle < getHalfEdgeAngle(he)) {
//                            MaxAngle = getHalfEdgeAngle(he);
//                            edge_to_flip = he;
//                        }
//                    }
                    for(HE_Halfedge he:edges) {
                        HE_Vertex op_ver1 = he.getNextInFace().getEndVertex();
                        HE_Vertex op_ver2 = he.getPair().getNextInFace().getEndVertex();
                        double angle1 = getHalfEdgeAngle(he);    //半边对应角度
                        double angle2 = getHalfEdgeAngle(he.getPair());     //半边对边对应角度
                        double angle3 = getHalfEdgeAngle(he.getPair().getNextInFace());
                        double angle4 = getHalfEdgeAngle(he.getPair().getNextInFace().getNextInFace());
                        if(angle1>0.5*Math.PI&&angle2>0.5*Math.PI) {
                            if (op_ver1.getHalfedgeStar().size() < Max - 1 && op_ver2.getHalfedgeStar().size() < Max - 1) {
                                flipEdge(mesh, he);
                            }
                        }
                    }
                }
            }
        }

//        for (HE_Halfedge he : mesh.getHalfedges()) {
//            double angle1 = getHalfEdgeAngle(he);    //半边对应角度
//            double angle2 = getHalfEdgeAngle(he.getPair());     //半边对边对应角度
//            double angle3 = getHalfEdgeAngle(he.getPair().getNextInFace());
//            double angle4 = getHalfEdgeAngle(he.getPair().getNextInFace().getNextInFace());
//            if (!he.isOuterBoundary() && !he.isInnerBoundary()) {      //判断半边是否是边界
//                if (angle1>threshold&&angle2>threshold){
//                    flipEdge(mesh,he);
//                }else if(angle1>threshold && angle2<threshold && angle3<threshold&& angle4<threshold){
//                    flipEdge(mesh,he);
//                }
//            }
//        }
    }

    public static double getHalfEdgeAngle(HE_Halfedge _he) {
        WB_Vector v1 = _he.getNextInFace().getPosition();  //下一条半边顶点
        WB_Vector v2 = _he.getNextInFace().getEndPosition();  //下一条半边终点

        WB_Vector v3 = _he.getNextInFace().getNextInFace().getPosition();  //下下一条半边顶点
        WB_Vector v4 = _he.getNextInFace().getNextInFace().getEndPosition();  //下下一条半边终点

        WB_Vector vP = WB_Vector.sub(v1, v2);
        WB_Vector vQ = WB_Vector.sub(v4, v3);
        double angle = WB_Vector.getAngle(vP, vQ);    //半边对应角度

        return angle;
    }
    public static void flipEdge(HE_Mesh mesh, HE_Halfedge h0) {
        HE_Halfedge h1 = h0.getNextInFace();
        HE_Halfedge h2 = h1.getNextInFace();
        HE_Halfedge h3 = h0.getPair();
        HE_Halfedge h4 = h3.getNextInFace();
        HE_Halfedge h5 = h4.getNextInFace();
        HE_Halfedge h6 = h1.getPair();
        HE_Halfedge h7 = h2.getPair();
        HE_Halfedge h8 = h4.getPair();
        HE_Halfedge h9 = h5.getPair();

        HE_Vertex v0=h0.getVertex();
        HE_Vertex v1=h3.getVertex();
        HE_Vertex v2 = h8.getVertex();
        HE_Vertex v3 = h6.getVertex();

        HE_Face f0 = h0.getFace();
        HE_Face f1 = h3.getFace();


        //重新设置翻转半边起点
        mesh.setVertex(h0, v2);
        mesh.setVertex(h3, v3);

        mesh.setVertex(h5,v2);
        mesh.setVertex(h4,v0);
        mesh.setVertex(h2,v3);
        mesh.setVertex(h1,v1);

        mesh.setVertex(h6,v3);
        mesh.setVertex(h9,v1);
        mesh.setVertex(h8,v2);
        mesh.setVertex(h7,v0);


        //重设半边连接顺序关系
        mesh.setNext(h0, h2);
        mesh.setNext(h2, h4);
        mesh.setNext(h4, h0);

        mesh.setNext(h1, h3);
        mesh.setNext(h3, h5);
        mesh.setNext(h5, h1);
        //重新设置半边之间pair关系
        mesh.setPair(h0, h3);
        mesh.setPair(h1, h6);
        mesh.setPair(h5, h9);
        mesh.setPair(h2, h7);
        mesh.setPair(h4, h8);
        //重设半边与面的关系
        mesh.setFace(h4, f0);
        mesh.setFace(h0, f0);
        mesh.setFace(h2, f0);

        mesh.setFace(h1, f1);
        mesh.setFace(h5, f1);
        mesh.setFace(h3, f1);
        //重设半边索引到的面
        mesh.setHalfedge(f0, h0);
        mesh.setHalfedge(f1, h3);


        mesh.setHalfedge(v0,h7);
        mesh.setHalfedge(v1,h9);
        mesh.setHalfedge(v2,h8);
        mesh.setHalfedge(v3,h6);
    }
}
