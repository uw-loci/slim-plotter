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
 * Renderer manager capable of switching between multiple renderers.
 * Used primarily to switch between lifetime computation in multiple channels.
 *
 * @author Curtis Rueden
 */
public class RendererSwitcher implements ICurveRenderer {

  // -- Fields --

  protected ICurveRenderer[] renderers;
  protected int c;
  protected boolean alive;

  // -- Constructor --

  public RendererSwitcher(ICurveRenderer[] renderers) {
    this.renderers = renderers;
  }

  // -- RendererSwitcher methods --

  public void setCurrent(int current) {
    if (current == c) return;
    int lastC = c;
    c = current;
    renderers[lastC].stop();
  }

  public ICurveRenderer[] getCurveRenderers() {
    return renderers;
  }

  public CurveCollection[] getCurveCollections() {
    CurveCollection[] cc = new CurveCollection[renderers.length];
    for (int i=0; i<cc.length; i++) cc[i] = renderers[i].getCurveCollection();
    return cc;
  }

  // -- ICurveRenderer methods --

  public CurveCollection getCurveCollection() {
    return renderers[c].getCurveCollection();
  }

  public void run() {
    alive = true;
    while (alive) renderers[c].run();
  }

  public void stop() {
    alive = false;
    renderers[c].stop();
  }

  public int getCurrentIterations() {
    return renderers[c].getCurrentIterations();
  }

  public int getTotalIterations() {
    return renderers[c].getTotalIterations();
  }

  public int getCurrentX() {
    return renderers[c].getCurrentX();
  }

  public int getCurrentY() {
    return renderers[c].getCurrentY();
  }

  public int getSubsampleLevel() {
    return renderers[c].getSubsampleLevel();
  }

  public int getCurrentProgress() {
    return renderers[c].getCurrentProgress();
  }

  public int getMaxProgress() {
    return renderers[c].getMaxProgress();
  }

  public void setMaxIterations(int mi) {
    for (int i=0; i<renderers.length; i++) {
      renderers[i].setMaxIterations(mi);
    }
  }

  public void setMaxRCSE(double mr) {
    for (int i=0; i<renderers.length; i++) {
      renderers[i].setMaxRCSE(mr);
    }
  }

  public int getMaxIterations() {
    return renderers[c].getMaxIterations();
  }

  public double getMaxRCSE() {
    return renderers[c].getMaxRCSE();
  }

  public double[][] getImage() {
    return renderers[c].getImage();
  }

  public void setComponentCount(int numExp) {
    for (int i=0; i<renderers.length; i++) {
      renderers[i].setComponentCount(numExp);
    }
  }

  public void setFixed(boolean[][] fixed) {
    for (int i=0; i<renderers.length; i++) {
      renderers[i].setFixed(fixed);
    }
  }

  public boolean[][] getFixed() {
    return renderers[c].getFixed();
  }

  public void setMask(boolean[][] mask) {
    for (int i=0; i<renderers.length; i++) {
      renderers[c].setMask(mask);
    }
  }

  public boolean[][] getMask() {
    return renderers[c].getMask();
  }

  public int getImageX() {
    return renderers[c].getImageX();
  }

  public int getImageY() {
    return renderers[c].getImageY();
  }

  public double getTotalRCSE() {
    return renderers[c].getTotalRCSE();
  }

  public double getWorstRCSE() {
    return renderers[c].getWorstRCSE();
  }

}
