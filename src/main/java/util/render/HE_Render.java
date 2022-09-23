package util.render;

import processing.core.PApplet;
import wblut.processing.WB_Render3D;

/**
 * TODO
 *
 * @author libiao
 * @version 1.0
 * @date 2022/4/20 10:33
 * Inst. AAA, S-ARCH, Southeast University
 */
public class HE_Render extends WB_Render3D {
    PApplet home;
    JtsRender jst;

    public HE_Render(PApplet home) {
        super(home);
        this.jst = new JtsRender(home);
        this.home = home;
    }

    public PApplet getApp() {
        return this.home;
    }

    public JtsRender getJtsRender() {
        return this.jst;
    }

}
