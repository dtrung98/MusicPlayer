package com.ldt.musicr.util;

import java.util.Random;

/**
 * Created by trung on 10/6/2017.
 */

public class GradientResourses {

public final static GradientColor[] gradientColors = new GradientColor[] {
       // new GradientColor( // this might be used for shader.
            //    new int[] {0xfffa709a,0xfffee140}
              //  ),
        new GradientColor( // this may be background shader.
                new int[] {
                        0xffeea2a2,
                        0xffbbc1bf,
                        0xff57c6e1,
                        0xffb49fda,
                        0xff7ac5d8},
                new float[] {
                        0f,0.19f,0.42f,0.79f,1f
                }
        ),
        new GradientColor(
                new int[]{
                        0xff4facfe,
                        0xff00f2fe
                }
        ),
        new GradientColor(
                new int[]
                        {
                                0xff00dbde,
                                0xfffc00ff
                        }
        ),
        new GradientColor(
                new int[]{
                        0xffff057c,
                        0xff7c64d5,
                        0xff4cc3ff,
                },
        new float[] {0f, 0.48f, 1f})
        //Add more GradientColor here.
    };
private static final int gradientNumbers=2;
private static Random rnd= new Random();
public static class GradientColor {
    public final int[] colors;
    public final float[] pos;
    GradientColor(final int[] c,final float[] p)
     {
         colors = c;
         pos= p;
     }
     GradientColor(final  int[] c)
     {
         colors = c;
       pos = null;
     }
    }

    /**
     *
     * @param id
     * @return
     */
    public static GradientColor getGradientColor(int id)
    {
    return gradientColors[id];
    }
    public static GradientColor getRandomGradientColor()
    {
        return getGradientColor(rnd.nextInt(gradientColors.length));
    }


}
