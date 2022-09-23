package pro_fan;

import igeo.*;
import wblut.geom.*;
import wblut.hemesh.HEC_Polygon;
import wblut.hemesh.HE_Face;
import wblut.hemesh.HE_Mesh;
import wblut.hemesh.HE_Vertex;

import java.util.ArrayList;
import java.util.List;

public class Lsave {
	public static final float flag = -1f;

	public static void savePolygonAsISurf(WB_Polygon poly, String layer) {
		int num = poly.getPoints().size();
		IVec[] MVecList = new IVec[num];
		for (int i = 0; i < num; i++) {
			WB_Point p = (WB_Point) poly.getPoints().get(i);

			MVecList[i] = new IVec(p.xf(), flag * p.yf(), p.zf());
		}
		ISurface surf = new ISurface(MVecList).layer("" + layer);
	}

	public static void savePlaneAsISurf(WB_Plane plane, float r, String layer) {
		// TODO Auto-generated method stub
		ArrayList<WB_Point> points = new ArrayList<>();
		WB_Vector vectorX = plane.getU().mul(r);
		WB_Vector vectorY = plane.getV().mul(r);
		WB_Point o = plane.getOrigin();
		points.add(o.sub(vectorX).sub(vectorY));
		points.add(o.add(vectorX).sub(vectorY));
		points.add(o.add(vectorX).add(vectorY));
		points.add(o.sub(vectorX).add(vectorY));
		savePolygonAsISurf(new WB_Polygon(points), layer);
	}

	public static void saveStringAsIText(String str, WB_Coord pos, float size, String layer) {
		// TODO Auto-generated method stub
		IVec vec = new IVec(pos.xf(), -pos.yf(), pos.zf());
		new IText(str, size, vec).layer(layer);
	}

	@Deprecated
	public static ISurface savePolygonwithHolesAsISurf(WB_Polygon poly, String layer) {
		int[] nums = poly.getNumberOfPointsPerContour();
		int num = nums.length;
		ICurve outcurve = null;
		ICurve[] innercurves = new ICurve[num - 1];
		IVec[] outcurvepts = null;
		IVec[][] innercurvepts = new IVec[num - 1][];
		int numcounter = 0;
		for (int i = 0; i < num; i++) {
			IVec[] MVecList = new IVec[nums[i] + 1];
			ArrayList<WB_Point> tempoly = new ArrayList<>();
			for (int j = 0; j < nums[i]; j++) {
				tempoly.add((WB_Point) poly.getPoints().get(numcounter));
				numcounter++;
			}
			// if (i == 0) {
			// ;
			// } else {
			// Collections.reverse(tempoly);
			// }
			for (int j = 0; j < nums[i]; j++) {
				WB_Point p = tempoly.get(j);
				MVecList[j] = new IVec(p.xf(), p.yf(), p.zf());
			}
			MVecList[nums[i]] = MVecList[0];
			if (i == 0) {
				outcurvepts = MVecList;
				outcurve = new ICurve(MVecList).layer("" + layer);
			} else {
				innercurvepts[i - 1] = MVecList;
			}
		}
		ISurface surf = new ISurface(outcurvepts/* , innercurvepts */).layer("" + layer);
		ITrimCurveI[] temp = surf.outerTrimLoop(0);

		for (int i = 1; i < surf.outerTrimLoop(0).length; i++) {
			System.out.println(outcurvepts[i].x + "_" + outcurvepts[i].y + "_" + outcurvepts[i].z);
		}
		for (int i = 1; i < num; i++) {
			System.out.println(num + "dsadaddddd");
			for (int j = 0; j < innercurvepts[i - 1].length; j++) {
				System.out.println(
						innercurvepts[i - 1][j].x + "_" + innercurvepts[i - 1][j].y + "_" + innercurvepts[i - 1][j].z);
			}

			// Collections.reverse(Arrays.asList(innercurvepts[i]));
			ICurve innercurve = new ICurve(innercurvepts[i - 1]);
			// ITrimCurve iner=new ITrimCurve(innercurve);
			surf.addInnerTrimLoop(innercurve);
		}
		// System.out.println(surf.innerTrimLoopNum()+"ssssssssssssssss");
		return surf;
		// IG.del(outcurve);
		// IG.del(innercurves);
	}

	public static void savePolygonWithHoleAsSurf(WB_Polygon poly, String layer) {
		int n = poly.getNumberOfHoles();
		if (n == 0) {
			savePolygonAsISurf(poly, layer);
		} else {
			int shellPtsNum = poly.getNumberOfShellPoints();
			IVec[] shellPts = new IVec[shellPtsNum];
			int count = 0;

			for (int i = 0; i < shellPtsNum; i++) {
				WB_Point pt = poly.getPoint(i);
				shellPts[i] = new IVec(pt.xf(), flag * pt.yf(), pt.zf());
				count++;
			}

			IVec[][] innerPts = new IVec[n][];

			int[] holesPtsNum = poly.getNumberOfPointsPerContour();

			for (int i = 1; i < n + 1; i++) {
				IVec[] holePts = new IVec[holesPtsNum[i]];
				for (int j = 0; j < holesPtsNum[i]; j++) {
					WB_Point pt = poly.getPoint(count);
					holePts[j] = new IVec(pt.xf(), flag * pt.yf(), pt.zf());
					count++;
				}

				innerPts[i - 1] = holePts;
			}

			new ISurface(shellPts, innerPts).layer(layer);
		}
	}

	@Deprecated
	public static void savePolygonWithHolesAsIMesh(WB_Polygon poly, String layer) {
		HEC_Polygon creator = new HEC_Polygon(poly, 0);
		HE_Mesh hemesh = new HE_Mesh(creator);
		int fnum = hemesh.getFaces().size();
		System.out.println(fnum);
		IFace[] ifaces = new IFace[fnum];
		for (int i = 0; i < fnum; i++) {
			List<HE_Vertex> vers = hemesh.getFaces().get(i).getFaceVertices();
			int vernum = vers.size();
			IVertex[] MVecList = new IVertex[vernum];
			for (int j = 0; j < vernum; j++) {
				HE_Vertex p = vers.get(j);
				MVecList[j] = new IVertex(p.xf(), p.yf(), p.zf());
			}
			ifaces[i] = new IFace(MVecList);
		}
		IMesh iMesh = new IMesh(ifaces);
	}

	public static void saveMeshAsIMesh(HE_Mesh hemesh, String layer) {
		int fnum = hemesh.getFaces().size();
		System.out.println(fnum);
		IFace[] ifaces = new IFace[fnum];
		for (int i = 0; i < fnum; i++) {
			List<HE_Vertex> vers = hemesh.getFaces().get(i).getFaceVertices();
			int vernum = vers.size();
			IVertex[] MVecList = new IVertex[vernum];
			for (int j = 0; j < vernum; j++) {
				HE_Vertex p = vers.get(j);
				MVecList[j] = new IVertex(p.xf(), flag * p.yf(), p.zf());
			}
			ifaces[i] = new IFace(MVecList);
		}
		IMesh iMesh = new IMesh(ifaces);
	}

	public static void saveNewRecPipe() {
	}

	public static void savePolygonWithHolesAsSurf(WB_Polygon poly, String layer) {
		int n = poly.getNumberOfHoles();

		int shellPtsNum = poly.getNumberOfShellPoints();
		IVec[] shellPts = new IVec[shellPtsNum];
		int count = 0;

		for (int i = 0; i < shellPtsNum; i++) {
			WB_Point pt = poly.getPoint(i);
			shellPts[i] = new IVec(pt.xf(), flag * pt.yf(), pt.zf());
			count++;
		}

		IVec[][] innerPts = new IVec[n][];

		int[] holesPtsNum = poly.getNumberOfPointsPerContour();

		for (int i = 1; i < n + 1; i++) {
			IVec[] holePts = new IVec[holesPtsNum[i]];
			for (int j = 0; j < holesPtsNum[i]; j++) {
				WB_Point pt = poly.getPoint(count);
				holePts[j] = new IVec(pt.xf(), flag * pt.yf(), pt.zf());
				count++;
			}

			innerPts[i - 1] = holePts;
		}

		new ISurface(shellPts, innerPts).layer(layer);

	}

	public static void savePolygonAsICurve(WB_Polygon poly, String layer) {
		int num = poly.getPoints().size();
		IVec[] MVecList = new IVec[num];
		for (int i = 0; i < num; i++) {
			WB_Point p = (WB_Point) poly.getPoints().get(i);

			MVecList[i] = new IVec(p.xf(), flag * p.yf(), p.zf());
		}
		ICurve cur = new ICurve(MVecList, true).layer("" + layer);
	}

	/*
	 * 版本适应  WB_Coord和WB_Point的相互转化
	 */
	public static void savePolylineAsICurve(WB_PolyLine poly, String layer) {
		ArrayList<WB_Point> ps = new ArrayList<>();
		for(WB_Coord c:poly.getPoints().toList()) {
			ps.add((WB_Point) c);
		}
		savePolylineAsICurve(ps, layer);
	}

	public static void savePolylineAsICurve(List<WB_Point> points, String layer) {
		int num = points.size();
		IVec[] MVecList = new IVec[num];
		for (int i = 0; i < num; i++) {
			WB_Point p = points.get(i);

			MVecList[i] = new IVec(p.xf(), flag * p.yf(), p.zf());
		}
		ICurve cur = new ICurve(MVecList, false).layer("" + layer);
	}

	public static void saveSegmentAsIGCurve(WB_Segment segment, String layer) {
		saveSegmentAsIGCurve(segment.getOrigin(), segment.getEndpoint(), layer);
	}

	public static void saveSegmentAsIGCurve(WB_Coord p1, WB_Coord p2, String layer) {
		IVec[] MVecList = new IVec[2];
		MVecList[0] = new IVec(p1.xf(), flag * p1.yf(), p1.zf());
		MVecList[1] = new IVec(p2.xf(), flag * p2.yf(), p2.zf());
		new ICurve(MVecList).layer("" + layer);
	}

	public static void saveMeshAsIBrep(HE_Mesh mesh, String layer) {
		ISurface[] surfaces = new ISurface[mesh.getNumberOfFaces()];
		int count = 0;
		for (HE_Face f : mesh.getFaces()) {
			WB_Polygon poly = f.getPolygon();
			int num = poly.getPoints().size();
			IVec[] MVecList = new IVec[num];
			for (int i = 0; i < num; i++) {
				WB_Point p = (WB_Point) poly.getPoints().get(i);

				MVecList[i] = new IVec(p.xf(), flag * p.yf(), p.zf());
			}
			surfaces[count] = new ISurface(MVecList).layer("" + layer);
			count++;
		}
		new IBrep(surfaces).layer("" + layer);
		IG.del(surfaces);

	}

	public static void saveWB_PointAsIPoint(WB_Coord p, String layer) {
		new IPoint(flag*p.xf(), flag * p.yf(), p.zf()).layer(layer).clr(0XFF0000);
	}

//	public static void saveBeamAsPipe(WB_Coord[] points, float r, String layer) {
//
//		IG.pipe(Ltrans.WB_PointstoIVecs(points), r).layer(layer);
//	}
//
//	public static void saveSegmentAsPipe(WB_Segment s, float r, String layer) {
//		WB_Point[] points = { new WB_Point(s.getOrigin()), new WB_Point(s.getEndpoint()) };
//		IG.pipe(Ltrans.WB_PointstoIVecs(points), r).layer(layer);
//	}

	public static void saveMeshAsISurfs(HE_Mesh mesh, String layer) {
		ISurface[] surfaces = new ISurface[mesh.getNumberOfFaces()];
		int count = 0;
		for (HE_Face f : mesh.getFaces()) {
			WB_Polygon poly = f.getPolygon();
			int num = poly.getPoints().size();
			IVec[] MVecList = new IVec[num];
			for (int i = 0; i < num; i++) {
				WB_Point p = (WB_Point) poly.getPoints().get(i);

				MVecList[i] = new IVec(p.xf(), flag * p.yf(), p.zf());
			}
			surfaces[count] = new ISurface(MVecList).layer("" + layer);
			count++;
		}

	}
}
