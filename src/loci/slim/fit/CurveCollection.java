//
// CurveCollection.java
//

/*
SLIM Plotter application and curve fitting library for
combined spectral lifetime visualization and analysis.
Copyright (C) 2006-@year@ Curtis Rueden and Eric Kjellman.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 3 of the License, or
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
public class CurveCollection {

  // -- Constants --

  private static final double LOG2 = Math.log(2);

  // -- Fields --

  /** Full image resolution. */
  protected int numRows;

  /** Curve fit data, dimensioned [maxDepth][numRows][numCols]. */
  protected CurveFitter[][][] curves;

  // -- Constructors --

  /**
   * Creates an object to manage a collection of curves for the given data.
   *
   * @param data Data array dimensioned [numRows][numCols][timeBins].
   * @param curveFitterClass Class representing the type of curve fitters to
   *   use (e.g., loci.slim.fit.GACurveFitter or loci.slim.fit.LMCurveFitter).
   * @param binRadius Radius of neighboring pixels to bin,
   *   to improve signal-to-noise ratio.
   *
   * @throws IllegalArgumentException
   *  if numRows or numCols is not a power of two or numRows != numCols
   */
  public CurveCollection(int[][][] data,
    Class curveFitterClass, int binRadius)
  {
    this(makeCurveFitters(data, curveFitterClass, binRadius));
  }

  /**
   * Creates an object to manage the given collection of curves.
   *
   * @param curveFitters Array of curve fitters dimensioned [numRows][numCols].
   *
   * @throws IllegalArgumentException
   *  if numRows or numCols is not a power of two or numRows != numCols
   */
  public CurveCollection(CurveFitter[][] curveFitters) {
    numRows = curveFitters.length;
    int numCols = curveFitters[0].length;
    if (numRows != numCols) {
      throw new IllegalArgumentException("Row and column counts do not match");
    }
    double log = Math.log(numRows) / LOG2;
    int depth = (int) log;
    if (log != depth) {
      throw new IllegalArgumentException("Row count is not a power of two");
    }
    curves = new CurveFitter[depth + 1][][];
    curves[0] = curveFitters;
  }

  // -- CurveCollection API methods --

  /** Computes subsamplings. */
  public void computeCurves() {
    Class curveFitterClass = curves[0][0][0].getClass();
    int depth = (int) (Math.log(numRows) / LOG2);
    int res = numRows;
    for (int d=1; d<=depth; d++) {
      res /= 2;
      curves[d] = new CurveFitter[res][res];
      for (int y=0; y<res; y++) {
        for (int x=0; x<res; x++) {
          CurveFitter cf = newCurveFitter(curveFitterClass);
          int[] data0 = curves[d-1][2*y][2*x].getData();
          int[] data1 = curves[d-1][2*y][2*x+1].getData();
          int[] data2 = curves[d-1][2*y+1][2*x].getData();
          int[] data3 = curves[d-1][2*y+1][2*x+1].getData();
          int[] data = new int[data0.length];
          for (int i=0; i<data.length; i++) {
            data[i] = data0[i] + data1[i] + data2[i] + data3[i];
          }
          cf.setData(data);
          curves[d][y][x] = cf;
        }
      }
    }
  }

  /** Gets the collection of curves at full resolution. */
  public CurveFitter[][] getCurves() { return getCurves(0); }

  /**
   * Gets the collection of curves subsampled at the given depth.
   * For example, for a 256 x 256 image, getCurves(2) returns a 64 x 64
   * subsampling.
   *
   * @throws IllegalArgumentException
   *   if the subsampling depth is greater than {@link #getSubsamplingDepth}
   */
  public CurveFitter[][] getCurves(int depth) {
    if (depth < 0 || depth > getSubsamplingDepth()) {
      throw new IllegalArgumentException("Invalid subsampling depth " +
        "(expected 0 <= depth <= " + getSubsamplingDepth());
    }
    return curves[depth];
  }

  /**
   * Gets the maximum subsampling depth of the curve collection. For example,
   * for a 256 x 256 image, the depth is 8 because there exist subsamplings at
   * 128 x 128, 64 x 64, 32 x 32, 16 x 16, 8 x 8, 4 x 4, 2 x 2 and 1 x 1.
   */
  public int getSubsamplingDepth() { return curves.length - 1; }

  /**
   * Sets how many exponentials are expected to be fitted.
   * Currently, more than 2 is not supported.
   */
  public void setComponentCount(int numExp) {
    for (int d=0; d<curves.length; d++) {
      for (int y=0; y<curves[d].length; y++) {
        for (int x=0; x<curves[d][y].length; x++) {
          curves[d][y][x].setComponentCount(numExp);
        }
      }
    }
  }

  // -- Utility methods --

  /** Creates a list of curve fitters using the given data as a source. */
  public static CurveFitter[][] makeCurveFitters(int[][][] data,
    Class curveFitterClass, int binRadius)
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
            for (int dy=y-binRadius; dy<=y+binRadius; dy++) {
              if (dy < 0) continue;
              if (dy >= numRows) break;
              for (int dx=x-binRadius; dx<=x+binRadius; dx++) {
                if (dx < 0) continue;
                if (dx >= numCols) break;
                sum += data[dy][dx][t];
              }
            }
            binnedData[y][x][t] = sum;
          }
        }
      }
      data = binnedData;
    }

    CurveFitter[][] curveFitters = new CurveFitter[numRows][numCols];
    for (int y=0; y<numRows; y++) {
      for (int x=0; x<numCols; x++) {
        curveFitters[y][x] = newCurveFitter(curveFitterClass);
        curveFitters[y][x].setData(data[y][x]);
      }
    }
    return curveFitters;
  }

  public static CurveFitter newCurveFitter(Class c) {
    try {
      return (CurveFitter) c.newInstance();
    }
    catch (InstantiationException exc) { exc.printStackTrace(System.out); }
    catch (IllegalAccessException exc) { exc.printStackTrace(System.out); }
    return null;
  }

}