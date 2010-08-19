//
// CurveCollection.java
//

/*
SLIM Plotter application and curve fitting library for
combined spectral lifetime visualization and analysis.
Copyright (C) 2006-@year@ Curtis Rueden and Eric Kjellman.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package loci.slim.fit;

import java.util.Vector;

/**
 * Data structure for managing a collection of curves. The main purpose of this
 * structure is to create subsampled curve sets at a resolution for each order
 * of magnitude&mdash;e.g., for a 256 x 256 image, the collection constructs
 * 128 x 128, 64 x 64, 32 x 32, 16 x 16, 8 x 8, 4 x 4, 2 x 2 and 1 x 1
 * subsampled images&mdash;by summing neighboring curves.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/slim-plotter/src/loci/slim/fit/CurveCollection.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/slim-plotter/src/loci/slim/fit/CurveCollection.java">SVN</a></dd></dl>
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class CurveCollection implements CurveReporter {

  // -- Constants --

  private static final double LOG2 = Math.log(2);

  // -- Fields --

  /** Full image resolution. */
  protected int numRows, numCols;

  /** Total number of samplings, counting full-resolution image. */
  protected int depth;

  /** Curve fit data, dimensioned [maxDepth][numRows][numCols]. */
  protected ICurveFitter[][][] curves;

  // -- Constructors --

  /**
   * Creates an object to manage a collection of curves for the given data.
   *
   * @param data Data array dimensioned [numRows][numCols][timeBins].
   * @param curveFitterClass Class representing the type of curve fitters to
   *   use (e.g., loci.slim.fit.GACurveFitter or loci.slim.fit.LMCurveFitter).
   * @param binRadius Radius of neighboring pixels to bin,
   *   to improve signal-to-noise ratio.
   */
  public CurveCollection(int[][][] data, Class curveFitterClass,
    int binRadius, int firstIndex, int lastIndex)
  {
    this(makeCurveFitters(data, curveFitterClass,
      binRadius, firstIndex, lastIndex));
  }

  /**
   * Creates an object to manage the given collection of curves.
   *
   * @param curveFitters Array of curve fitters dimensioned [numRows][numCols].
   */
  public CurveCollection(ICurveFitter[][] curveFitters) {
    numRows = curveFitters.length;
    numCols = curveFitters[0].length;
    int max = numRows > numCols ? numRows : numCols;
    double log = Math.log(max) / LOG2;
    depth = (int) log;
    curves = new ICurveFitter[depth + 1][][];
    curves[0] = curveFitters;
  }

  // -- CurveCollection methods --

  /** Computes subsamplings. */
  public void computeCurves() {
    Class curveFitterClass = curves[0][0][0].getClass();
    int numExp = curves[0][0][0].getComponentCount();
    int xRes = numCols, yRes = numRows;
    int value = 0, max = numCols * numRows / 3;
    for (int d=1; d<=depth; d++) {
      xRes /= 2;
      yRes /= 2;
      if (xRes < 1) xRes = 1;
      if (yRes < 1) yRes = 1;
      curves[d] = new ICurveFitter[yRes][xRes];
      ICurveFitter[][] lastCurve = curves[d - 1];
      for (int y=0; y<yRes; y++) {
        int yy0 = 2 * y;
        int yy1 = 2 * y + 1;
        if (yy0 >= lastCurve.length) yy0 = lastCurve.length - 1;
        if (yy1 >= lastCurve.length) yy1 = lastCurve.length - 1;
        ICurveFitter[] lastCurve0 = lastCurve[yy0];
        ICurveFitter[] lastCurve1 = lastCurve[yy1];
        for (int x=0; x<xRes; x++) {
          int xx0y0 = 2 * x;
          int xx1y0 = 2 * x + 1;
          int xx0y1 = 2 * x;
          int xx1y1 = 2 * x + 1;
          if (xx0y0 >= lastCurve0.length) xx0y0 = lastCurve0.length - 1;
          if (xx0y1 >= lastCurve1.length) xx0y1 = lastCurve1.length - 1;
          if (xx1y0 >= lastCurve0.length) xx1y0 = lastCurve0.length - 1;
          if (xx1y1 >= lastCurve1.length) xx1y1 = lastCurve1.length - 1;
          ICurveFitter cf0 = lastCurve0[xx0y0];
          ICurveFitter cf1 = lastCurve0[xx1y0];
          ICurveFitter cf2 = lastCurve1[xx0y1];
          ICurveFitter cf3 = lastCurve1[xx1y1];
          int[] data0 = cf0.getData();
          int[] data1 = cf1.getData();
          int[] data2 = cf2.getData();
          int[] data3 = cf3.getData();
          int first0 = cf0.getFirst();
          int first1 = cf1.getFirst();
          int first2 = cf2.getFirst();
          int first3 = cf3.getFirst();
          int last0 = cf0.getLast();
          int last1 = cf1.getLast();
          int last2 = cf2.getLast();
          int last3 = cf3.getLast();
          int[] data = new int[data0.length];
          for (int i=0; i<data.length; i++) {
            data[i] = data0[i] + data1[i] + data2[i] + data3[i];
          }
          int first = (first0 + first1 + first2 + first3) / 4;
          int last = (last0 + last1 + last2 + last3) / 4;
          ICurveFitter cf = newCurveFitter(curveFitterClass);
          cf.setData(data, first, last);
          cf.setComponentCount(numExp);
          curves[d][y][x] = cf;
          fireCurveEvent(new CurveEvent(this, value++, max, null));
        }
      }
    }
  }

  /** Gets the full Y resolution. */
  public int getNumRows() { return numRows; }

  /** Gets the full X resolution. */
  public int getNumCols() { return numCols; }

  /** Gets the collection of curves at full resolution. */
  public ICurveFitter[][] getCurves() { return getCurves(0); }

  /**
   * Gets the collection of curves subsampled at the given depth.
   * For example, for a 256 x 256 image, getCurves(2) returns a 64 x 64
   * subsampling.
   *
   * @throws IllegalArgumentException
   *   if the subsampling depth is greater than {@link #getSubsamplingDepth}
   */
  public ICurveFitter[][] getCurves(int depth) {
    if (depth < 0 || depth > getSubsamplingDepth()) {
      throw new IllegalArgumentException("Invalid subsampling depth " +
        "(expected 0 <= depth <= " + getSubsamplingDepth());
    }
    return curves[depth];
  }

  /**
   * Gets the maximum subsampling depth of the curve collection. For example,
   * for a 250 x 200 image, the depth is 8 because there exist samplings at
   * 250 x 200, 125 x 100, 62 x 50, 31 x 25, 15 x 12, 7 x 6, 3 x 3 and 1 x 1.
   */
  public int getSubsamplingDepth() { return curves.length - 1; }

  /**
   * Sets how many exponentials are expected to be fitted.
   * Currently, more than 2 is not supported.
   */
  public void setComponentCount(int numExp) {
    for (int d=0; d<curves.length; d++) {
      if (curves[d] == null) continue;
      for (int y=0; y<curves[d].length; y++) {
        if (curves[d][y] == null) continue;
        for (int x=0; x<curves[d][y].length; x++) {
          curves[d][y][x].setComponentCount(numExp);
        }
      }
    }
  }

  /** Sets which parameters should be fixed, versus allowed to converge. */
  public void setFixed(boolean[][] fixed) {
    for (int d=0; d<curves.length; d++) {
      for (int y=0; y<curves[d].length; y++) {
        for (int x=0; x<curves[d][y].length; x++) {
          curves[d][y][x].setFixed(fixed);
        }
      }
    }
  }

  /** Gets which parameters should be fixed, versus allowed to converge. */
  public boolean[][] getFixed() { return curves[0][0][0].getFixed(); }

  // -- CurveReporter methods --

  protected Vector curveListeners = new Vector();

  public void addCurveListener(CurveListener l) {
    synchronized (curveListeners) {
      curveListeners.add(l);
    }
  }

  public void removeCurveListener(CurveListener l) {
    synchronized (curveListeners) {
      curveListeners.remove(l);
    }
  }

  public void fireCurveEvent(CurveEvent e) {
    synchronized (curveListeners) {
      for (int i=0; i<curveListeners.size(); i++) {
        CurveListener l = (CurveListener) curveListeners.get(i);
        l.curveChanged(e);
      }
    }
  }

  // -- Utility methods --

  /** Creates a list of curve fitters using the given data as a source. */
  public static ICurveFitter[][] makeCurveFitters(int[][][] data,
    Class curveFitterClass, int binRadius, int firstIndex, int lastIndex)
  {
    int numRows = data.length;
    int numCols = data[0].length;
    int timeBins = data[0][0].length;

    if (binRadius > 0) {
      // we need to bin neighboring pixels; make a copy of the data (*sigh*)
      int[][][] binnedData = new int[numRows][numCols][timeBins];
      for (int y=0; y<numRows; y++) {
        for (int x=0; x<numCols; x++) {
          for (int t=0; t<timeBins; t++) {
            int sum = 0;
            int yLo = y - binRadius, yHi = y + binRadius;
            if (yLo < 0) yLo = 0;
            if (yHi > numRows - 1) yHi = numRows - 1;
            for (int dy=yLo; dy<=yHi; dy++) {
              int xLo = x - binRadius, xHi = x + binRadius;
              if (xLo < 0) xLo = 0;
              if (xHi > numCols - 1) xHi = numCols - 1;
              for (int dx=xLo; dx<=xHi; dx++) sum += data[dy][dx][t];
            }
            binnedData[y][x][t] = sum;
          }
        }
      }
      data = binnedData;
    }

    ICurveFitter[][] curveFitters = new ICurveFitter[numRows][numCols];
    for (int y=0; y<numRows; y++) {
      for (int x=0; x<numCols; x++) {
        curveFitters[y][x] = newCurveFitter(curveFitterClass);
        curveFitters[y][x].setData(data[y][x], firstIndex, lastIndex);
      }
    }
    return curveFitters;
  }

  public static ICurveFitter newCurveFitter(Class c) {
    try {
      return (ICurveFitter) c.newInstance();
    }
    catch (InstantiationException exc) { exc.printStackTrace(System.out); }
    catch (IllegalAccessException exc) { exc.printStackTrace(System.out); }
    return null;
  }

}
