# SLIM Plotter

> Development of SLIM Plotter has ceased in favor of
> [FLIMJ](https://imagej.net/FLIMJ).

The SLIM Plotter is a tool for interactive visualization and inspection
of combined spectral lifetime (SLIM) data, written by Curtis Rueden and
Eric Kjellman. It uses the
[VisAD](http://visad.ssec.wisc.edu/) Java visualization
toolkit to display data. It was originally developed for internal use
within LOCI and the White and Keely labs, but has grown into an
application that may be useful to others as well.

SLIM Plotter works with data in Becker & Hickl's SDT format (but could
be adapted to work with data in other formats without too much extra
effort). The main purpose of the program is to allow exploration of
regions of data collected with a combined spectral lifetime detector.

The right-hand plot shows an intensity image for each channel
(controlled with the slider directly beneath the image). The left-hand
plot shows the lifetime histograms for each channel, rendered as a
surface. Individual channels can be toggled using the checkbox next to
the slider beneath the right-hand plot.

Optionally, SLIM Plotter can fit single-exponential curves to the
lifetime data, to determine an approximation of the aggregate lifetime
value per channel, using the [Levenberg-Marquardt least
squares](https://en.wikipedia.org/wiki/Levenberg%E2%80%93Marquardt_algorithm)
curve fitting algorithm (LMA). To use this feature, the "Align peaks"
option must be checked when the data is first read—so that SLIM Plotter
can adjust for slight discrepancies in the system response time between
channels. The log window on the bottom right shows the results of this
alignment, as well as the exact parameter values of the curve fits.

SLIM Plotter functions similarly for single-channel lifetime data, but
uses a 2D line plot for the lifetime histogram, rather than a surface in
3D.

The lifetime histograms displayed in the left-hand plot are a summation
of the pixels selected in the right-hand intensity view. By default, all
pixels are selected, but the user can draw a region of interest (ROI)
using the mouse to focus on a particular area. Individual pixels can be
selected with a mouse click. The text above the left-hand plot details
the portion of pixels currently selected, as well as the minimum and
maximum aggregate lifetime values across all channels (if known).

Lastly, the lifetime histograms currently being viewed can be exported
to a simple text file for further processing in another program, such as
a spreadsheet.

## Download

- [slim.zip](http://www.loci.wisc.edu/files/software/slim.zip)

## Instructions

SLIM Plotter requires the Java Runtime Environment and Java 3D.

### Windows

1.  [Verify whether you have
    Java](https://java.com/en/download/installed.jsp) installed.
    -   If you do not, or if your version is less than 1.4.2, [download
        and install](https://java.com/en/) it.
2.  Download and install
    [Java3D](https://java3d.dev.java.net/binary-builds.html).
3.  Unzip **slim.zip** into your **"Program Files"** folder. A subfolder
    called **"SlimPlotter"** will be created.
4.  Double-click on **SlimPlotter.exe** to launch the program.

### Mac OS X

1.  SLIM Plotter requires Mac OS X 10.3 "Panther" or greater:
    -   If you are running 10.4 "Tiger" or later, everything you need is
        already installed.
    -   If you are running 10.3 "Panther," install the [Java3D and JAI
        update](http://docs.info.apple.com/article.html?artnum=120289)
        from Apple.
2.  Expand **slim.zip**, if your browser did not do so already. Drag the
    resulting **"SlimPlotter"** folder to your **Applications** folder.
3.  Double-click on **SlimPlotter** to launch the program.

### Linux

1.  [Verify whether you have
    Java](https://java.com/en/download/installed.jsp)installed.
    -   If you do not, or if your version is less than 1.4.2, [download
        and install](http://java.com/en/) it.
2.  Download and install
    [Java3D](https://java3d.dev.java.net/binary-builds.html).
3.  Unzip **slim.zip** wherever you like (your home directory works
    well). A subfolder called **"SlimPlotter"** will be created.
4.  You can launch SLIM Plotter from the command line with the included
    **slim** script.
