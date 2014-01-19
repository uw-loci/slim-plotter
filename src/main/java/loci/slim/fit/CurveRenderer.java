/*
 * #%L
 * SLIM Plotter application and curve fitting library for
 * combined spectral lifetime visualization and analysis.
 * %%
 * Copyright (C) 2006 - 2014 Board of Regents of the University of
 * Wisconsin-Madison.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

package loci.slim.fit;

/**
 * Base class for curve renderer implementations.
 *
 * @author Eric Kjellman
 */
public abstract class CurveRenderer implements ICurveRenderer {

  // -- Fields --

  protected boolean alive;
  protected int maxIterations;
  protected double maxRCSE;
  protected int totalIterations;
  protected int currentIterations;
  protected int currentX;
  protected int currentY;
  protected int subsampleLevel;
  protected int currProgress;
  protected int maxProgress;
  protected CurveCollection curveData;
  protected int numRows, numCols;
  protected int numExponentials;

  // -- Constructor --

  public CurveRenderer(CurveCollection cc) {
    curveData = cc;
    numRows = curveData.getNumRows();
    numCols = curveData.getNumCols();
  }

  // -- ICurveRenderer methods --

  public CurveCollection getCurveCollection() {
    return curveData;
  }

  public abstract void run();

  public void stop() {
    alive = false;
  }

  public int getCurrentIterations() {
    return currentIterations;
  }

  public int getTotalIterations() {
    return totalIterations;
  }

  public int getCurrentX() {
    return currentX;
  }

  public int getCurrentY() {
    return currentY;
  }

  public int getSubsampleLevel() {
    return subsampleLevel;
  }

  public int getCurrentProgress() {
    return currProgress;
  }

  public int getMaxProgress() {
    return maxProgress;
  }

  public void setMaxIterations(int mi) {
    maxIterations = mi;
  }

  public void setMaxRCSE(double mr) {
    maxRCSE = mr;
  }

  public int getMaxIterations() {
    return maxIterations;
  }

  public double getMaxRCSE() {
    return maxRCSE;
  }

  public abstract double[][] getImage();

  public void setComponentCount(int numExp) {
    numExponentials = numExp;
  }

  public void setFixed(boolean[][] fixed) {
    curveData.setFixed(fixed);
  }

  public boolean[][] getFixed() {
    return curveData.getFixed();
  }

  public abstract void setMask(boolean[][] mask);

  public abstract boolean[][] getMask();

  public abstract int getImageX();

  public abstract int getImageY();

  public abstract double getTotalRCSE();

  public abstract double getWorstRCSE();

}
