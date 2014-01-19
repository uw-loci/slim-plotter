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
 * Interface defining methods common to all curve renderers.
 *
 * @author Curtis Rueden
 */
public interface ICurveRenderer extends Runnable {

  CurveCollection getCurveCollection();

  void run();

  void stop();

  int getCurrentIterations();

  int getTotalIterations();

  int getCurrentX();

  int getCurrentY();

  int getSubsampleLevel();

  int getCurrentProgress();

  int getMaxProgress();

  void setMaxIterations(int mi);

  void setMaxRCSE(double mr);

  int getMaxIterations();

  double getMaxRCSE();

  double[][] getImage();

  void setComponentCount(int numExp);

  void setFixed(boolean[][] fixed);

  boolean[][] getFixed();

  void setMask(boolean[][] mask);

  boolean[][] getMask();

  int getImageX();

  int getImageY();

  double getTotalRCSE();

  double getWorstRCSE();

}
