package pro_fan;

import processing.core.PGraphics;

public class Utils {
    public static final float map(float value, float start1, float stop1, float start2, float stop2) {
        float outgoing = start2 + (stop2 - start2) * ((value - start1) / (stop1 - start1));
        String badness = null;
        if (outgoing != outgoing) {
            badness = "NaN (not a number)";
        } else if (outgoing == -1.0F / 0.0 || outgoing == 1.0F / 0.0) {
            badness = "infinity";
        }

        if (badness != null) {
            String msg = String.format("map(%s, %s, %s, %s, %s) called, which returns %s", value, start1, stop1, start2, stop2, badness);
            PGraphics.showWarning(msg);
        }

        return outgoing;
    }
}
