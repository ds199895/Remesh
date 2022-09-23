package pro_fan;

import igeo.*;
import wblut.geom.*;
import wblut.hemesh.HEC_FromPolygons;
import wblut.hemesh.HE_Mesh;
import wblut.hemesh.HE_Vertex;
import wblut.nurbs.WB_BSpline;

import java.util.ArrayList;

public class Lt_Igeo {
	public static WB_Triangle ICurvetoTriangle(ICurve curve) {
		WB_Point p1 = IVecItoWB_Point(curve.cps()[0]);
		WB_Point p2 = IVecItoWB_Point(curve.cps()[1]);
		WB_Point p3 = IVecItoWB_Point(curve.cps()[2]);
		return new WB_Triangle(p1, p2, p3);

	}

	public static WB_Point IPointtoWB_Point(IPoint p) {
		return new WB_Point(-p.x(), -p.y(), p.z());
	}

	public static IVec WB_PointtoIVec(WB_Coord points) {
		return new IVec(-points.xf(), -points.yf(), points.zf());
	}

	public static IVec[] WB_PointstoIVecs(WB_Coord[] points) {
		IVec[] vecs = new IVec[points.length];
		for (int i = 0; i < points.length; i++) {
			vecs[i] = WB_PointtoIVec(points[i]);
		}
		return vecs;
	}

	public static IVecI VertextoIVecI(HE_Vertex p) {
		return new IVec(-p.xf(), -p.yf(), p.zf());
	}

	public static WB_Point IVectoWB_Point(IVec p) {
		return new WB_Point(-p.x(), -p.y(), p.z());
	}

	public static WB_Point IVecItoWB_Point(IVecI p) {
		return new WB_Point(-p.x(), -p.y(), p.z());
	}

	public static WB_Point[] IPointstoWB_Points(IPoint[] pts) {
		WB_Point[] points = new WB_Point[pts.length];
		for (int i = 0; i < pts.length; i++) {
			points[i] = IPointtoWB_Point(pts[i]);
		}
		return points;
	}

	public static ArrayList<WB_Point> IcurvetoPoints2D(ICurve curve) {
		ArrayList<WB_Point> points = new ArrayList<>();
		IVecI[] ps = curve.cps();
		for (int i = 0; i < ps.length; i++) {
			points.add(new WB_Point(-ps[i].x(), -ps[i].y()));
		}
		return points;
	}

	public static ArrayList<WB_Point> IcurvetoPoints3D(ICurve curve) {
		ArrayList<WB_Point> points = new ArrayList<>();
		IVecI[] ps = curve.cps();
		for (int i = 0; i < ps.length; i++) {
			points.add(new WB_Point(-ps[i].x(), -ps[i].y(), ps[i].z()));
		}
		return points;
	}

	public static WB_Polygon IcurvetoWB_Poly2D(ICurve curve) {
		ArrayList<WB_Point> points = new ArrayList<>();
		IVecI[] ps = curve.cps();
		for (int i = 0; i < ps.length; i++) {
			points.add(new WB_Point(-ps[i].x(), -ps[i].y()));
		}
		return new WB_Polygon(points);
	}

	public static WB_Polygon IcurvetoWB_Poly3D(ICurve curve) {
		ArrayList<WB_Point> points = new ArrayList<>();
		IVecI[] ps = curve.cps();
		for (int i = 0; i < ps.length - 1; i++) {
			points.add(new WB_Point(-ps[i].x(), -ps[i].y(), ps[i].z()));
		}
		return new WB_Polygon(points);
	}

	public static ArrayList<WB_Polygon> IcurvestoWB_Polys3D(ICurve[] curves) {
		ArrayList<WB_Polygon> polys = new ArrayList<>();
		for (int i = 0; i < curves.length; i++) {
			polys.add(IcurvetoWB_Poly3D(curves[i]));
		}
		return polys;
	}

	public static ArrayList<WB_PolyLine> IcurvestoWB_Polylines3D(ICurve[] curves) {
		ArrayList<WB_PolyLine> polys = new ArrayList<>();
		for (int i = 0; i < curves.length; i++) {
			polys.add(IcurvetoWB_Polyline(curves[i]));
		}
		return polys;
	}

	public static WB_PolyLine IcurvetoWB_Polyline(ICurve curve) {
		ArrayList<WB_Point> points = new ArrayList<>();
		IVecI[] ps = curve.cps();
		for (int i = 0; i < ps.length; i++) {
			points.add(new WB_Point(-ps[i].x(), -ps[i].y(), ps[i].z()));
		}
		return WB_GeometryFactory.instance().createPolyLine(points);
	}

	public static WB_BSpline IcurvetoWB_Spline(ICurve curve) {
		ArrayList<WB_Point> points = new ArrayList<>();
		IVecI[] ps = curve.cps();
		for (int i = 0; i < ps.length; i++) {
			points.add(new WB_Point(-ps[i].x(), -ps[i].y(), ps[i].z()));
		}
		return new WB_BSpline(points.toArray(new WB_Point[points.size()]), 3);
	}

	public static WB_BSpline IcurvetoWB_Spline_Closed(ICurve curve) {
		ArrayList<WB_Point> points = new ArrayList<>();
		IVecI[] ps = curve.cps();
		for (int i = 0; i < ps.length - 2; i++) {
			points.add(new WB_Point(-ps[i].x(), -ps[i].y(), ps[i].z()));
		}
		return new WB_BSpline(points.toArray(new WB_Point[points.size()]), 3);
	}

	public static WB_BSpline IcurvetoWB_Spline_abs(ICurve curve) {
		ArrayList<WB_Point> points = new ArrayList<>();
		IVecI[] ps = curve.cps();
		for (int i = 0; i < ps.length; i++) {
			points.add(new WB_Point(-ps[i].x(), ps[i].y(), ps[i].z()));
		}
		return new WB_BSpline(points.toArray(new WB_Point[points.size()]), 3);
	}

	public static ArrayList<WB_BSpline> IcurvestoWB_Splines(ICurve[] curves) {
		ArrayList<WB_BSpline> slines = new ArrayList<>();
		for (int i = 0; i < curves.length; i++) {
			slines.add(IcurvetoWB_Spline(curves[i]));
		}
		return slines;
	}

	public static WB_Segment IcurvetoWB_Segment(ICurve curve) {
		IVecI[] ps = curve.cps();
		WB_Point p1 = new WB_Point(-ps[0].x(), -ps[0].y(), ps[0].z());
		WB_Point p2 = new WB_Point(-ps[1].x(), -ps[1].y(), ps[1].z());
		return new WB_Segment(p1, p2);
	}

	public static ArrayList<WB_Segment> IcurvestoWB_Segments(ICurve curves[]) {
		ArrayList<WB_Segment> segs = new ArrayList<>();
		for (int i = 0; i < curves.length; i++) {
			segs.add(IcurvetoWB_Segment(curves[i]));
		}
		return segs;
	}

	public static String getItextInfo(IText text) {
		return text.text();
	}

	public static String[] getItextInfos(IText[] texts) {
		String[] infos = new String[texts.length];
		for (int i = 0; i < texts.length; i++) {
			infos[i] = texts[i].text();
		}
		return infos;
	}

	public static WB_Point[] getItextPostions(IText[] texts) {
		WB_Point[] poss = new WB_Point[texts.length];
		for (int i = 0; i < texts.length; i++) {
			poss[i] = IVectoWB_Point(texts[i].pos());
		}
		return poss;
	}

	public static ArrayList<WB_Polygon> IBreptoWB_Polys(IBrep brep) {
		ArrayList<WB_Polygon> polys = new ArrayList<>();
		for (int i = 0; i < brep.surfaceNum(); i++) {
			polys.add(ISurftoWB_PolywithHoles(brep.surface(i)));
		}
		return polys;
	}

	public static ArrayList<ArrayList<WB_Polygon>> IBrepstoWB_Polyss(IBrep[] breps) {
		ArrayList<ArrayList<WB_Polygon>> polyss = new ArrayList<>();
		for (int i = 0; i < breps.length; i++) {
			polyss.add(IBreptoWB_Polys(breps[i]));
		}
		return polyss;
	}
	public static ArrayList<HE_Mesh> IBrepstoHemeshs(IBrep[] breps) {
		ArrayList<HE_Mesh>meshes=new ArrayList<>();
		ArrayList<ArrayList<WB_Polygon>> polyss = new ArrayList<>();
		for (int i = 0; i < breps.length; i++) {
			HEC_FromPolygons hecp=new HEC_FromPolygons(IBreptoWB_Polys(breps[i]));
			HE_Mesh mesh=new HE_Mesh(hecp);
			meshes.add(mesh);
		}
		return meshes;
	}
	public static ArrayList<WB_Polygon> ISurfstoWB_Polys(ISurface[] surfs) {
		ArrayList<WB_Polygon> polys = new ArrayList<>();
		for (int i = 0; i < surfs.length; i++) {
			polys.add(ISurftoWB_PolywithHoles(surfs[i]));
		}
		return polys;
	}

	public static WB_Polygon ISurftoWB_PolywithHoles(ISurface surf) {
		return ISurftoWB_PolywithHoles(surf.surface);
	}

	public static WB_Polygon ISurftoWB_PolywithHoles(ISurfaceGeo surf) {
		ArrayList<WB_Point> outpts = new ArrayList<>();
		for (ITrimCurveI outer : surf.outerTrimLoop(0)) {
			outpts.add(Lt_Igeo.IVecItoWB_Point(outer.start()));
		}
		WB_Point[][] innerptss = new WB_Point[surf.innerTrimLoopNum()][];
		for (int i = 0; i < surf.innerTrimLoopNum(); i++) {
			ArrayList<WB_Point> inners = new ArrayList<>();
			for (ITrimCurveI inner : surf.innerTrimLoop(i)) {
				inners.add(Lt_Igeo.IVecItoWB_Point(inner.start()));
			}
			innerptss[i] = (inners.toArray(new WB_Point[inners.size()]));
		}
		return new WB_Polygon(outpts.toArray(new WB_Point[outpts.size()]), innerptss);
	}
}
