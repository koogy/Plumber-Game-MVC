package com.company.View;

import com.company.Model.Pipe;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class PipeGUI {

    Pipe pipe;
    BufferedImage image;
    Point coordinate;

    public PipeGUI(Pipe pipe) {
        this.pipe = pipe;
    }

    public PipeGUI(Pipe pipe, BufferedImage image) {
        this.pipe = pipe;
        this.image = rotate(image, pipe.getRotation() * 90);
    }

    public BufferedImage rotate(BufferedImage image, double angle) {
        double rads = Math.toRadians(angle);
        double sin = Math.abs(Math.sin(rads)), cos = Math.abs(Math.cos(rads));
        int w = image.getWidth();
        int h = image.getHeight();
        int newWidth = (int) Math.floor(w * cos + h * sin);
        int newHeight = (int) Math.floor(h * cos + w * sin);

        BufferedImage rotated = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = rotated.createGraphics();
        AffineTransform at = new AffineTransform();
        at.translate((newWidth - w) / 2, (newHeight - h) / 2);

        int x = w / 2;
        int y = h / 2;

        at.rotate(rads, x, y);
        g2d.setTransform(at);
        g2d.drawImage(image, 0, 0, null);
        g2d.dispose();

        return rotated;
    }

    public void displayCoordinate() {
        System.out.println(coordinate.getX() + " : " + coordinate.getY());
    }
    /* GETTERS AND SETTERS */


    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }

    public Point getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(Point coordinate) {
        this.coordinate = coordinate;
    }

    public Pipe getPipe() {
        return pipe;
    }

    public void setPipe(Pipe pipe) {
        this.pipe = pipe;
    }

    @Override
    public PipeGUI clone() throws CloneNotSupportedException {
        return (PipeGUI) super.clone();
    }
}
