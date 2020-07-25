import javax.swing.Timer;
import java.awt.event.*;

public abstract class Lib {

    public static int square(int a) {
        return a * a;
    }
    public static void removeAllListeners(Timer t) {
        t.stop();
        for (ActionListener l : t.getActionListeners()) t.removeActionListener(l);
    }
    public static String convertToHMS(long time) {
        long millis = time % 1000;
        time /= 1000;
        long sec = time % 60;
        time /= 60;
        long min = time % 60;
        time /= 60;
        long hrs = time;
        return leftPad(hrs + "", 2, '0') + ":" + leftPad(min + "", 2, '0') + ":" + leftPad(sec + "", 2, '0') + "." + leftPad(millis + "", 3, '0');
    }
    public static String leftPad(String s, int len, char pad) {
        if (s.length() >= len) return s;
        int iter = len - s.length();
        for (int i = 0; i < iter; i++) {
            s = pad + s;
        }
        return s;
    }
}
