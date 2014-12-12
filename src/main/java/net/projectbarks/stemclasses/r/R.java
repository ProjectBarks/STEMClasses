package net.projectbarks.stemclasses.r;

/**
 * Created by brandon on 12/12/14.
 */
public class R {

    public static final ClockRenderer draw;
    public static final Config config;
    public static final Text text;

    static {
        draw = new ClockRenderer();
        config = new Config();
        text = new Text();
    }


}
