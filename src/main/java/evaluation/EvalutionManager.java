package evaluation;

import edu.duke.geo4.colormapper.ColorMap;
import edu.duke.geo4.colormapper.GradientMap;
import pro_fan.AgentPoint;
import pro_fan.ExtrudeBox;
import pro_fan.FanGeo;
import processing.core.PApplet;
import util.geometry.Epsilon;
import util.render.HE_Render;
import wblut.geom.WB_Coord;
import wblut.geom.WB_Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: EvaluationManager
 * @author: wb
 * @date: 2022/7/31 13:29
 */
public class EvalutionManager {

    public EvalutionManager() {
    }
    public List<AgentPoint> evaluationPts = new ArrayList<>();

    /**
     * -----------------------------以下对extrudeBox采样------------------------------------------
     */
    //风
    public WindEv windEv;
    Map<ExtrudeBox, Float> windRate = new HashMap<>();


    public void setWindValues(List<FanGeo> geos, List<AgentPoint> fixedAgents) {
        windRate.clear();
        evaluationPts.clear();
        windEv = new WindEv(new WB_Vector(1, 2, 0), geos);
        ArrayList<ExtrudeBox> newB = windEv.news;

        for (int i = 0; i < newB.size(); i++) {
            List<AgentPoint> insidePt = new ArrayList<>();
            for (AgentPoint ap : fixedAgents) {
                if (newB.get(i).contains(ap)) {
                    insidePt.add(ap);
                    break;
                }
            }
            if (insidePt.size() == 1) {
                AgentPoint ref = insidePt.get(0);
                for (AgentPoint ap : fixedAgents) {
                    if (Math.abs(ap.loc.xf() - insidePt.get(0).loc.xf()) < Epsilon.epsilon &&
                            Math.abs(ap.loc.yf() - insidePt.get(0).loc.yf()) < Epsilon.epsilon) {
                        if (ap.loc.zf() >= newB.get(i).loc.zf() &&
                                ap.loc.zf() <= (newB.get(i).loc.zf() + newB.get(i).depth) &&
                                ap.loc.zf() != ref.loc.zf()) {
                            insidePt.add(ap);
                        }
                    }
                }
            } else {
                System.out.println("none agentPt inside sample building ");
            }
            System.out.println("采样点个数 " + insidePt.size());
            evaluationPts.addAll(insidePt);

            //rate 此处用以记录各个建筑单体的通风评价值
            float rate = 0;
            for (AgentPoint ap : insidePt) {
                //暂时以0为采样对象
                windEv.samplePt(ap.loc, i);
                windEv.setEvaluation();
                ap.setWind(windEv.getValue());
                rate += windEv.getValue();
                System.out.println("采样点通风测试 " + windEv.getValue());
                System.out.println("——————————————————");
            }
            windRate.put(newB.get(i), rate / (insidePt.size() * 1f));
            System.out.println("采样点测试 总值 " + rate);
            System.out.println("采样点测试 均值 " + rate / (insidePt.size() * 1f));
            System.out.println("采样建筑id " + i);
            System.out.println("————————————————————分隔线——————————————————————");
        }

    }
    //光
    public LightEv lightEv;
    Map<ExtrudeBox, Float> lightRate = new HashMap<>();

    public void setLightValues(List<FanGeo> geos, List<AgentPoint> fixedAgents) {
        evaluationPts.clear();
        lightRate.clear();
        lightEv = new LightEv(geos);
        //找到所有新建筑
        ArrayList<ExtrudeBox> newB = lightEv.news;
        //对每个点计算
        for (int i = 0; i < fixedAgents.size(); i++) {
            AgentPoint ap = fixedAgents.get(i);


            ExtrudeBox container = null;
            //检查点在哪个体形内,排除掉该形体的遮挡
            for (ExtrudeBox news : newB) {
                if (news.contains(ap)) {
                    container = news;
                    break;
                }
            }


                if (container != null) {
                    lightEv.setPreConditions(container.mesh.getFaces());
                } else {
                    lightEv.setPreConditions(null);
                }
            lightEv.samplePt(ap.loc);
            lightEv.setEvaluation();
            ap.setLight(lightEv.getValue());

            System.out.println("点" + i + "的采光值是 ： " + lightEv.getValue());

        }

        for(int i=0;i<newB.size();i++){
            float rate=0;
            List<AgentPoint> insidePt = new ArrayList<>();
            for(AgentPoint ap:fixedAgents){
                if(newB.get(i).contains(ap)){insidePt.add(ap);}
            }
            for(AgentPoint ap:insidePt){
                rate+=ap.getLight();
            }
           lightRate.put(newB.get(i), rate/ (insidePt.size() * 1f));
            System.out.println("建筑"+i+"的采光值是"+rate/ (insidePt.size()));
        }

    }

    /**todo:采光朝向？功能连接数?经济技术指标？*/


    /**
     * -----------------------------以下对agentPoint采样------------------------------------------
     */

    //视线
    public SightEv sightEv;

    public void setSightValues(List<FanGeo> geos, List<AgentPoint> fixedAgents) {
        evaluationPts.clear();
        evaluationPts.addAll(fixedAgents);

        sightEv = new SightEv(geos);
        //找到所有新建筑
        ArrayList<ExtrudeBox> newB = sightEv.news;

        //对每个点计算
        for (int i = 0; i < evaluationPts.size(); i++) {
            AgentPoint ap = evaluationPts.get(i);
            ExtrudeBox container = null;
            //检查点在哪个体形内,排除掉该形体的遮挡
            for (ExtrudeBox news : newB) {
                if (news.contains(ap)) {
                    container = news;
                    break;
                }
            }
            sightEv.setPreConditions(container);
            sightEv.samplePt(ap.loc);
            sightEv.setEvaluation();
            ap.setSight(sightEv.getValue());

            System.out.println("点" + i + "的视线值是 ： " + sightEv.getValue());

        }
    }


    //可达性(位置)
    public PosEv posEv;

    public void setPosValues(WB_Coord p, List<AgentPoint> fixedAgents) {
        evaluationPts.clear();
        evaluationPts.addAll(fixedAgents);

        posEv = new PosEv();
        ArrayList<Float>values=new ArrayList<>();

        //对每个点计算
        for (int i = 0; i < evaluationPts.size(); i++) {
            AgentPoint ap = evaluationPts.get(i);
            posEv.samplePt(ap.loc, p);
            posEv.setEvaluation();
            values.add(posEv.getValue());

        }
        //map一下
        ArrayList<Float> mapValues = mapValues(values);
        for (int i = 0; i < evaluationPts.size(); i++) {
            AgentPoint ap = evaluationPts.get(i);

            ap.setPos(mapValues.get(i));

            System.out.println("点" + i + "的距离因子是 ： " + mapValues.get(i));
        }
    }

    /**todo:噪音?*/


    /**
     * -----------------------------显示------------------------------------------
     */
    public void display(HE_Render render, List<AgentPoint> fixedAgents) {
        //

        PApplet app = render.getApp();
        if (windEv != null) {
            for(AgentPoint ap:evaluationPts){
            windEv.display(render,ap,windEv.drawWind);}
            for (ExtrudeBox b : windEv.news) {
                app.pushStyle();
                app.stroke(0);
                render.drawEdges(b.mesh);
                app.noStroke();
                app.fill(new GradientMap(app, ColorMap.JET).getColor(windRate.get(b)), 170);
                render.drawFaces(b.mesh);
                app.popStyle();
            }

        }

        if (lightEv != null) {
            for (AgentPoint ap : fixedAgents) {
                lightEv.display(render,ap, lightEv.drawLight);
            }
            for (ExtrudeBox b : lightEv.news) {
                app.pushStyle();
                app.stroke(0);
                render.drawEdges(b.mesh);
                app.noStroke();
                app.fill(new GradientMap(app, ColorMap.JET).getColor(lightRate.get(b)), 170);
                render.drawFaces(b.mesh);
                app.popStyle();
            }
        }

        if (sightEv != null) {
            for (AgentPoint ap : fixedAgents) {
                sightEv.display(render,ap, sightEv.drawSight);
            }
        }


        if (posEv != null) {
            for (AgentPoint ap : fixedAgents) {
                posEv.display(render,ap, posEv.drawPos);
            }
        }
    }

    /**
     * 计算参数(根据种类们)
     */
    public double calculateValue(List<AgentPoint> fixedAgents, String... agTypes) {
        double v = 0;
        if (agTypes != null && agTypes.length > 0) {
            for (String type : agTypes) {
                v += calculateAgents(type, fixedAgents);
            }
            v = v / agTypes.length;
        }

        return v;
    }


    /**
     * 计算参数(根据种类)
     */
    public double calculateAgents(String type, List<AgentPoint> agents) {
        double v = 0;
        switch (type) {
            case "testSight":
                for (AgentPoint a : agents) {
                    v += a.getSight();
                }
                break;

            case "testWind":
                for (AgentPoint a : agents) {
                    v += a.getWind();
                }
                break;

            case "testPos":
                for (AgentPoint a : agents) {
                    v += a.getPos();
                }
                break;

            case "tesLight":
                for (AgentPoint a : agents) {
                    v += a.getLight();
                }
                break;


            default:
                System.out.println("no calculate type");

        }
        return v / agents.size();
    }





    //Sigmoid
    public static double sigmoid(double value) {
        //Math.E=e;Math.Pow(a,b)=a^b
        double ey = Math.pow(Math.E, -value);
        double result = 1 / (1 + ey);
        return result;
    }

    public static float map(double value, double min, double max, double a, double b) {
        return (float)(a + (value - min) * (b - a) / (max - min));
    }

    public static ArrayList<Float>mapValues(ArrayList<Float>values){
        ArrayList<Float>mapValues=new ArrayList<>();
        float min=5000;
        float max=-5000;
        for(float v:values){
            if(v<min)min=v;
        if(v>max)max=v;}
        for(int i=0;i<values.size();i++){
            mapValues.add(map(values.get(i),min,max,0,1));

        }
       return mapValues;
    }
}