package gui;

import film.FrameBuffer;
import renderer.RenderEventInterface;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class RenderTab extends JPanel {

    /**
     * The frame buffer.
     */
    final RenderEventInterface renderer;

    /**
     * Progress bar indicating the percentage of completion of the render.
     */
    private final JProgressBar bar;

    /**
     * A panel which allows the user to manipulate the gamma and sensitivity.
     */
    private final ControlPanel control;

    /**
     * Panel which shows a preview of the render.
     */
    public final ImagePanel panel;

    public RenderTab(RenderEventInterface renderer, double sensitivity, double gamma) {
        super(new BorderLayout());
        this.renderer = renderer;

        // Create the image panel
        panel = new ImagePanel(new FrameBuffer(1,1), sensitivity, gamma);

        // Create the progress bar
        bar = new JProgressBar(0, 100);
        bar.setStringPainted(true);
        bar.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        // Create the control panel
        control = new ControlPanel(this);
        control.setPreferredSize(new Dimension(320, -1));

        // Add the components
        add(control, BorderLayout.EAST);
        add(panel, BorderLayout.CENTER);
        add(bar, BorderLayout.SOUTH);

        // Show the user interface.
        setVisible(true);

    }

    /**
     * Sets the value of the progress bar. The given value must be in the
     * interval [0,1] or are otherwise clamped to the interval.
     *
     * @param progress
     *            value for the progress bar (between 0 and 1).
     */
    protected void setProgress(double progress) {
        bar.setValue(Math.max(0, Math.min(100, (int) (100.0 * progress))));
    }

    /**
     * Opens a file dialog with the request to save the current image stored in
     * the frame buffer.
     */
    public void save() {
        JFileChooser chooser = new JFileChooser(".");
        chooser.setMultiSelectionEnabled(false);
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setFileFilter(new FileFilter() {
            /*
             * (non-Javadoc)
             *
             * @see javax.swing.filechooser.FileFilter#getDescription()
             */
            @Override
            public String getDescription() {
                return "Portable Network Graphics (*.png)";
            }

            /*
             * (non-Javadoc)
             *
             * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
             */
            @Override
            public boolean accept(File file) {
                return file.isDirectory() || file.getName().endsWith(".png");
            }
        });

        int result = chooser.showSaveDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            String filename = file.getName();

            if (!filename.endsWith(".png")) {
                JOptionPane.showMessageDialog(this,
                        "The chosen filename does not end with '.png'!",
                        "Invalid filename", JOptionPane.ERROR_MESSAGE);
            } else {
                try {
                    double sensitivity = panel.getSensitivity();
                    double gamma = panel.getGamma();
                    BufferedImage image = panel.getBuffer().toBufferedImage(sensitivity,
                            gamma);
                    ImageIO.write(image, "png", file);
                } catch (IOException e) {
                    StringBuilder builder = new StringBuilder(e.toString());
                    for (StackTraceElement element : e.getStackTrace())
                        builder.append("\n\tat ").append(element);

                    JOptionPane.showMessageDialog(this, builder.toString(),
                            "IOException occured", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    public void startRender() {
        this.renderer.startRender();
    }
}
