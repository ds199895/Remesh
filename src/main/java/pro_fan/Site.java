package pro_fan;

//
//import evaluation.*;
import evaluation.EvalutionManager;
import processing.core.PApplet;
import util.render.HE_Render;
import wblut.geom.*;

import java.util.ArrayList;
import java.util.List;


/**
 * TODO：可建设基地
 *
 * @author libiao
 * @version 1.0
 * @date 2022/7/28 16:35
 * Inst. AAA, S-ARCH, Southeast University
 */
public class Site implements FanGeo {
    // 基地红线（多边形）
    WB_Polygon range;
    // 基地上的建筑限高
    double rangeDepth;
    // 基地可建设空间的 aabb3d
    WB_AABB aabb;
    // 地形的引用
    public Terrain terrain;

    /**
     * terrain：地形,  rangeDepth:建筑限高, 构成基地多边形的点集:points
     */
    //形成点阵
    public Site(Terrain terrain, double rangeDepth, WB_Polygon poly,double gap) {
        this.terrain = terrain;
        this.rangeDepth = rangeDepth;
        this.range =poly;

        this.set_aabb();
        this.setFixedAgents(gap);
    }

    /**
     * 可建设范围内的智能点集, gap为预设点与点之间的距离
     */
    List<AgentPoint> fixedAgents = null;

    private void setFixedAgents(double gap) {
        fixedAgents = new ArrayList<>();

        double minX = this.aabb.getMinX();
        double minY = this.aabb.getMinY();
        double minZ = this.aabb.getMinZ();
        double wid = this.aabb.getWidth();
        double hei = this.aabb.getHeight();
        double dep = this.aabb.getDepth();

        int w_num = (int) (wid / gap);
        double w_step = wid / w_num;
        int h_num = (int) (hei / gap);
        double h_step = hei / h_num;
        int d_num = (int) (dep / gap);
        double d_step = dep / d_num;

        for (int i = 0; i <= w_num; i++) {
            for (int j = 0; j <= h_num; j++) {
                for (int k = 0; k <= d_num; k++) {
                    AgentPoint ap = new AgentPoint(minX + i * w_step, minY + j * h_step, minZ + k * d_step);
                    if (this.isOnSite(ap.loc)) {
                        fixedAgents.add(ap);
                    }
                }
            }
        }
    }

    public List<AgentPoint> getAgents() {
        return this.fixedAgents;
    }

    public static WB_Vector getRandomDirection() {
        WB_Vector v = new WB_Vector(Math.random() - 0.5, Math.random() - 0.5);
        while (v.getLength() < 0.001) {
            v = new WB_Vector(Math.random() - 0.5, Math.random() - 0.5);
        }
        return v;
    }

    /** 设置该基地可建设空间的 aabb3d */
    private void set_aabb(){
        int numberOfPoints = range.getNumberOfPoints();
        this.aabb = new WB_AABB();
        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double minZ = Double.MAX_VALUE;
        double maxZ=0;
        for(int i=0;i<numberOfPoints;i++){
            WB_Point p = range.getPoint(i);
            WB_Point p3d = this.terrain.getLocalLocation(p.xd(), p.yd());
            this.aabb.add(p3d);
            if(p3d.xd()<minX){
                minX = p3d.xd();
            }
            if(p3d.yd()<minY){
                minY = p3d.yd();
            }
            if(p3d.zd()<minZ){
                minZ = p3d.zd();
            }
            if(p3d.zd()>maxZ){
                maxZ=p3d.zd();
            }
        }
        this.aabb.add(new WB_Point(minX, minY, maxZ+this.rangeDepth));
    }

    /** 判断点 p 是否位于可建设范围之类 */
    private boolean isOnSite(WB_Point p){
        boolean inRange = WB_GeometryOp2D.contains2D(p,this.range);
        if(!inRange){
            return false;
        }

        WB_Point localLocation = terrain.getLocalLocation(p.xd(), p.yd());
        if(p.zd() > localLocation.zd()&&p.zd()<=localLocation.zd()+this.rangeDepth){
            return true;
        }else{
            return false;
        }
    }
    ExtrudeBox newBuilding = null;

    public void setNewBuilding(ExtrudeBox newBuilding) {
        this.newBuilding = newBuilding;
    }


    /**
     * ———————————————————————— new 新建筑list————————————————————————————————————————————————
     */
    ArrayList<ExtrudeBox> newBuildings = new ArrayList<>();
    public void addNewBuilding(ExtrudeBox newBuilding) {
        this.newBuildings.add(newBuilding);
    }

    /**
     * ———————————————————————— new 新建筑list————————————————————————————————————————————————
     */

    // debug for testing values ..... the values need to be implemented by QianJie and etc. *******************
    double cur_value = 0;

    public void testing_optimize() {
        if (this.newBuilding != null) {

            //备份 建筑的原有 位置 和 方向
            WB_Point location = new WB_Point(this.newBuilding.loc.xd(), this.newBuilding.loc.yd());
            WB_Point direction = new WB_Point(this.newBuilding.dir.xd(), this.newBuilding.dir.yd());


//            List<AgentPoint> containedByNewBuilding = new ArrayList<>();
//            for (AgentPoint ap : fixedAgents) {
//                if (this.newBuilding.contains(ap)){
//                    containedByNewBuilding.add(ap);
//                }
//            }
//            cur_value = calculateValue(containedByNewBuilding);

            // 随机新 位置 和 方向
            int randIndex = (int) (Math.random() * this.fixedAgents.size());
            WB_Point new_loc = this.fixedAgents.get(randIndex).loc;
            WB_Vector new_dir = Site.getRandomDirection();
            this.newBuilding.set(new_loc, new_dir);
            List<AgentPoint> newContainedByNewBuilding = new ArrayList<>();
            for (AgentPoint ap : fixedAgents) {
                if (this.newBuilding.contains(ap)) {
                    newContainedByNewBuilding.add(ap);
                }
            }
            double new_value = calculateValue(newContainedByNewBuilding);

            if (new_value > cur_value) {
                cur_value = new_value;
                System.out.println("cur_value   " + cur_value);
            } else {
                this.newBuilding.set(location, direction);
            }
        }
    }

    /**
     * ———————————————————————— new test ————————————————————————————————————————————————
     */
    /**场地建筑单项 单独优化测试*/
    public void testing_optimize(String type,List<FanGeo> geos) {
        if (this.newBuildings.size() > 0) {
            for (ExtrudeBox newBuilding : newBuildings) {
                //备份 建筑的原有 位置 和 方向
                WB_Point location = new WB_Point(newBuilding.loc.xd(), newBuilding.loc.yd());
                WB_Point direction = new WB_Point(newBuilding.dir.xd(), newBuilding.dir.yd());

                //curvalue 计算旧值
                List<AgentPoint> containedByNewBuilding = new ArrayList<>();
                for (AgentPoint ap : fixedAgents) {
                    if (newBuilding.contains(ap)) {
                        containedByNewBuilding.add(ap);
                    }
                }

                cur_value = manager.calculateAgents(type,containedByNewBuilding);

                // 随机新 位置 和 方向
                int randIndex = (int) (Math.random() * this.fixedAgents.size());
                WB_Point new_loc = this.fixedAgents.get(randIndex).loc;
                WB_Vector new_dir = Site.getRandomDirection();
                newBuilding.set(new_loc, new_dir);
                List<AgentPoint> newContainedByNewBuilding = new ArrayList<>();
                //算新点值
                setValues(type, geos);
                for (AgentPoint ap : fixedAgents) {
                    if (newBuilding.contains(ap)) {
                        newContainedByNewBuilding.add(ap);
                    }
                }
                //给点赋新值
                double new_value = manager.calculateAgents(type,containedByNewBuilding);

                if (new_value > cur_value) {
                    System.out.println("新建筑" + newBuildings.indexOf(newBuilding) + "： 旧评价值"+type+" = " + cur_value);
                    System.out.println("新建筑" + newBuildings.indexOf(newBuilding) + "： 新评价值"+type+" = " + new_value);
                    System.out.println("新建筑" + newBuildings.indexOf(newBuilding) + "  的评价值"+type+" 增长了" + (new_value - cur_value));
                    cur_value = new_value;
                } else {
                    newBuilding.set(location, direction);
                    setValues(type, geos);
                }

            }
        }
    }

    //混多种参数
    public void testValue(String... apTypes) {
        ArrayList<Float>values=new ArrayList<>();
        double min=2;
        double max=-1;
            for(int i=0;i<newBuildings.size();i++){
                ExtrudeBox extrudeBox = newBuildings.get(i);
                List<AgentPoint> containedByNewBuilding = new ArrayList<>();
            for (AgentPoint ap : fixedAgents) {
                if (extrudeBox.contains(ap)){
                    containedByNewBuilding.add(ap);
                }
            }
            float v=(float)manager.calculateValue(containedByNewBuilding,apTypes);
            values.add(v);
            if(v>max)max=v;
            if(v<min)min=v;
                //extrudeBox.value = manager.calculateValue(containedByNewBuilding,apTypes,extrudeBox,buTypes);
                //System.out.println("新建筑 "+i+" 的视线综合参数值 = "+extrudeBox.value);
        }
        //System.out.println(max);
        //System.out.println(min);
            for(int i=0;i<values.size();i++){
                float a = values.get(i);
                a=(float)(0.3+(a-min)*0.7/(max-min));
                newBuildings.get(i).value = a;
                if(i==0){ System.out.println("大厅组团的位置综合参数值 = "+a);}
                if(i==1){ System.out.println("公卫+零售组团的位置综合参数值 = "+a);}
                if(i==2){ System.out.println("办公+VIP组团的位置综合参数值 = "+a);}
                if(i==3){ System.out.println("设备辅助组团的位置综合参数值 = "+a);}
            }
    }


    // debug for testing values ..... the values need to be implemented by QianJie and etc. *******************

    EvalutionManager manager;

    public void setValues(String type, List<FanGeo> geo) {
        //由主程序传入两个新建筑的体量，暂时以0为采样对象
        switch (type) {
            case "testWind":
                manager = new EvalutionManager();
                manager.setWindValues(geo, fixedAgents);
                break;

            case "testLight":
                manager = new EvalutionManager();
                manager.setLightValues(geo, fixedAgents);
                break;

            case "testSight":
                manager = new EvalutionManager();
                manager.setSightValues(geo, fixedAgents);
                break;

            case "testPos":
                manager = new EvalutionManager();
                manager.setPosValues(new WB_Point(57,147,0), fixedAgents);
                break;

            default:
                System.out.println("no evaluation type");
        }

    }

    /**
     * ———————————————————————— new test ————————————————————————————————————————————————
     */


    private double calculateValue(List<AgentPoint> agents) {
        double v = 0;
        for (AgentPoint a : agents) {
            v += a.getSight();
        }
        return v / agents.size();
    }

    @Override
    public void draw(HE_Render render) {
        PApplet app = render.getApp();
        app.pushStyle();

        // 绘制基地多边形
        app.noFill();
        app.stroke(0xffff0000);
        render.drawPolygonEdges(this.range);

        /**———————————————————————— new ————————————————————————————————————————————————*/
        // 绘制基地上的"智能点"
        if(manager==null) {
            app.fill(255, 100, 0, 100);
            app.noStroke();
            for (AgentPoint ap : fixedAgents) {
                app.pushMatrix();
                app.translate(ap.loc.xf(), ap.loc.yf(), ap.loc.zf());
                app.box(2);
                app.popMatrix();
            }
        }else {
            manager.display(render, fixedAgents);
        }
        /**———————————————————————— new ————————————————————————————————————————————————*/

        app.popStyle();
    }
}
