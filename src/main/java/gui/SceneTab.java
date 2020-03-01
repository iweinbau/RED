package gui;

import renderer.RenderEventInterface;

import javax.swing.*;
import java.awt.*;

public class SceneTab extends JPanel {

    final RenderEventInterface renderEventInterface;

    public SceneTab(RenderEventInterface renderer) {
        super(new BorderLayout());
        this.renderEventInterface = renderer;

        // Specify the layout
        JPanel panel = new JPanel();

        panel.add(new Label("Hello"));

        add(panel,BorderLayout.CENTER);

    }
}
