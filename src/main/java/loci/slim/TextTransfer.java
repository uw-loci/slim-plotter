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

package loci.slim;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

/**
 * Helper class for clipboard interaction, stolen from
 * <a href="http://www.javapractices.com/Topic82.cjp">Java Practices</a>.
 */
public final class TextTransfer implements ClipboardOwner {

  public void setClipboardContents(String value) {
    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    if (clipboard != null) {
      clipboard.setContents(new StringSelection(value), this);
    }
  }

  public String getClipboardContents() {
    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    if (clipboard == null) return null;
    String result = null;
    Transferable contents = clipboard.getContents(null);
    boolean hasTransferableText = contents != null &&
      contents.isDataFlavorSupported(DataFlavor.stringFlavor);
    if (hasTransferableText) {
      try {
        result = (String) contents.getTransferData(DataFlavor.stringFlavor);
      }
      catch (UnsupportedFlavorException ex) { ex.printStackTrace(); }
      catch (IOException ex) { ex.printStackTrace(); }
    }
    return result;
  }

  public void lostOwnership(Clipboard clipboard, Transferable contents) { }

}
