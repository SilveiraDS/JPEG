/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jpeg;

/**
 *
 * @author silveira
 */
import java.awt.AWTException;
import java.awt.Frame;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.image.PixelGrabber;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Vector;
import javax.swing.ImageIcon;

 /*
 * DCT - A Java implementation of the Discreet Cosine Transform
 */

class DCT
{
    /**
     * DCT Block Size - default 8
     */
    
    public int N = 8;

    /**
     * Constructs a new DCT object. Initializes the cosine transform matrix
     * these are used when computing the DCT and it's inverse. This also
     * initializes the run length counters and the ZigZag sequence. Note that
     * the image quality can be worse than 25 however the image will be
     * extemely pixelated, usually to a block size of N.
     *
     * @param QUALITY The quality of the image (0 worst - 100 best)
     *
     */
    public DCT(int QUALITY) {

    }
    

    /*
     * This method preforms forward DCT on a block of image data using
     * the literal method specified for a 2-D Discrete Cosine Transform.
     * It is included as a curiosity and can give you an idea of the
     * difference in the compression result (the resulting image quality)
     * by comparing its output to the output of the AAN method below.
     * It is ridiculously inefficient.
     */

     // For now the final output is unusable.  The associated quantization step
     // needs some tweaking.  If you get this part working, please let me know.

     public double[][] forwardDCTExtreme(float input[][])
     {
        double output[][] = new double[N][N];
        //double tmp0, tmp1, tmp2, tmp3, tmp4, tmp5, tmp6, tmp7;
        //double tmp10, tmp11, tmp12, tmp13;
        //double z1, z2, z3, z4, z5, z11, z13;
        //int i;
        //int j;
        int v, u, x, y;
        for (v = 0; v < 8; v++) {
                for (u = 0; u < 8; u++) {
                        for (x = 0; x < 8; x++) {
                                for (y = 0; y < 8; y++) {
                                        output[v][u] += input[x][y]*Math.cos(((2*x + 1)*(double)u*Math.PI)/16d)*Math.cos(((2*y + 1)*(double)v*Math.PI)/16d);
                                }
                        }
                        output[v][u] *= 0.25d*((u == 0) ? (1d/Math.sqrt(2)) : 1d)*((v == 0) ? (1d/Math.sqrt(2)) : 1d);
                }
        }
        return output;
    }


    /*
     * This method preforms a DCT on a block of image data using the AAN
     * method as implemented in the IJG Jpeg-6a library.
     */
    public double[][] forwardDCT(float input[][])
    {
        double output[][] = new double[N][N];
        double tmp0, tmp1, tmp2, tmp3, tmp4, tmp5, tmp6, tmp7;
        double tmp10, tmp11, tmp12, tmp13;
        double z1, z2, z3, z4, z5, z11, z13;
        int i;
        int j;

// Subtracts 128 from the input values
        for (i = 0; i < 8; i++) {
                for(j = 0; j < 8; j++) {
                        output[i][j] = input[i][j] - 128d;
//                        input[i][j] -= 128;

                }
        }

        for (i = 0; i < 8; i++) {
                tmp0 = output[i][0] + output[i][7];
                tmp7 = output[i][0] - output[i][7];
                tmp1 = output[i][1] + output[i][6];
                tmp6 = output[i][1] - output[i][6];
                tmp2 = output[i][2] + output[i][5];
                tmp5 = output[i][2] - output[i][5];
                tmp3 = output[i][3] + output[i][4];
                tmp4 = output[i][3] - output[i][4];

                tmp10 = tmp0 + tmp3;
                tmp13 = tmp0 - tmp3;
                tmp11 = tmp1 + tmp2;
                tmp12 = tmp1 - tmp2;

                output[i][0] = tmp10 + tmp11;
                output[i][4] = tmp10 - tmp11;

                z1 = (tmp12 + tmp13) * 0.707106781d;
                output[i][2] = tmp13 + z1;
                output[i][6] = tmp13 - z1;

                tmp10 = tmp4 + tmp5;
                tmp11 = tmp5 + tmp6;
                tmp12 = tmp6 + tmp7;

                z5 = (tmp10 - tmp12) * 0.382683433d;
                z2 = 0.541196100d * tmp10 + z5;
                z4 = 1.306562965d * tmp12 + z5;
                z3 = tmp11 * 0.707106781d;

                z11 = tmp7 + z3;
                z13 = tmp7 - z3;

                output[i][5] = z13 + z2;
                output[i][3] = z13 - z2;
                output[i][1] = z11 + z4;
                output[i][7] = z11 - z4;
        }

        for (i = 0; i < 8; i++) {
                tmp0 = output[0][i] + output[7][i];
                tmp7 = output[0][i] - output[7][i];
                tmp1 = output[1][i] + output[6][i];
                tmp6 = output[1][i] - output[6][i];
                tmp2 = output[2][i] + output[5][i];
                tmp5 = output[2][i] - output[5][i];
                tmp3 = output[3][i] + output[4][i];
                tmp4 = output[3][i] - output[4][i];

                tmp10 = tmp0 + tmp3;
                tmp13 = tmp0 - tmp3;
                tmp11 = tmp1 + tmp2;
                tmp12 = tmp1 - tmp2;

                output[0][i] = tmp10 + tmp11;
                output[4][i] = tmp10 - tmp11;

                z1 = (tmp12 + tmp13) * 0.707106781d;
                output[2][i] = tmp13 + z1;
                output[6][i] = tmp13 - z1;

                tmp10 = tmp4 + tmp5;
                tmp11 = tmp5 + tmp6;
                tmp12 = tmp6 + tmp7;

                z5 = (tmp10 - tmp12) * 0.382683433d;
                z2 = 0.541196100d * tmp10 + z5;
                z4 = 1.306562965d * tmp12 + z5;
                z3 = tmp11 * 0.707106781d;

                z11 = tmp7 + z3;
                z13 = tmp7 - z3;

                output[5][i] = z13 + z2;
                output[3][i] = z13 - z2;
                output[1][i] = z11 + z4;
                output[7][i] = z11 - z4;
        }

        return output;
    }

    /*
    * This method quantitizes data and rounds it to the nearest integer.
    */
    public int[] quantizeBlock(double inputData[][], int code)
    {
        int outputData[] = new int[N*N];
        int i, j;
        int index;
        index = 0;
        for (i = 0; i < 8; i++) {
                for (j = 0; j < 8; j++) {
// The second line results in significantly better compression.
                        outputData[index] = (int)(Math.round(inputData[i][j] * Divisors[code][index]));
//                        outputData[index] = (int)(((inputData[i][j] * (((double[]) (Divisors[code]))[index])) + 16384.5) -16384);
                        index++;
                }
        }

        return outputData;
    }

    /*
    * This is the method for quantizing a block DCT'ed with forwardDCTExtreme
    * This method quantitizes data and rounds it to the nearest integer.
    */
    public int[] quantizeBlockExtreme(double inputData[][], int code)
    {
        int outputData[] = new int[N*N];
        int i, j;
        int index;
        index = 0;
        for (i = 0; i < 8; i++) {
                for (j = 0; j < 8; j++) {
                        outputData[index] = (int)(Math.round(inputData[i][j] / quantum[code][index]));
                        index++;
                }
        }

        return outputData;
    }
}
