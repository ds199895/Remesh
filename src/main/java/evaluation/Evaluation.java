package evaluation;
import pro_fan.AgentPoint;
import util.render.HE_Render;


/**
 * @ClassName: Evaluation
 * @Description:
 * @author: zqy
 * @date: 2022/7/28/028 21:15
 */
public interface Evaluation {
    public abstract void setEvaluation();

    public abstract void display(HE_Render render, AgentPoint ap, boolean is);

    public abstract float getValue();


}
