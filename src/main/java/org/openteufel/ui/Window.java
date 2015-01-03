package org.openteufel.ui;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JFrame;

public class Window extends JFrame implements MouseListener, MouseWheelListener
{
    public Window()
    {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.addMouseListener(this);
        this.addMouseWheelListener(this);
    }

    public void run()
    {

    }

    @Override
    public void mouseWheelMoved(final MouseWheelEvent arg0)
    {
    }

    @Override
    public void mouseClicked(final MouseEvent arg0)
    {
    }

    @Override
    public void mouseEntered(final MouseEvent arg0)
    {
    }

    @Override
    public void mouseExited(final MouseEvent arg0)
    {
    }

    @Override
    public void mousePressed(final MouseEvent arg0)
    {
    }

    @Override
    public void mouseReleased(final MouseEvent arg0)
    {
    }
}
