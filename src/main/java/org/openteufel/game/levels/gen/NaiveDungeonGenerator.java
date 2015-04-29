package org.openteufel.game.levels.gen;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Random;
import org.boaz88.delaunay_triangulation.Delaunay_Triangulation;
import org.boaz88.delaunay_triangulation.Point_dt;
import org.boaz88.delaunay_triangulation.Triangle_dt;
import org.jgrapht.EdgeFactory;
import org.jgrapht.alg.KruskalMinimumSpanningTree;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

public class NaiveDungeonGenerator extends LevelBitmapGenerator {

    public int minRoomWidth = 4;
    public int maxRoomWidth = 8;
    public int maxRoomCount = 40;
    public double maxRoomWidthRatio = 0.2;
    public double preferredRoomWidth = 0.6;
    public double preferredRoomOdds = 0.8;
    public double targetDensity = 0.7;
    public double densityMargin = 0.1;
    public int maxRoomConnections = 6;
    public double minSideRatio = 0.2;

    private final Random rng = new Random(System.currentTimeMillis());
    private Random overrideRng = null;
    private final Rooms rooms = new Rooms();

    public void setRng(final Random rng) {
        overrideRng = rng;
    }

    public BufferedImage generate(final int width, final int height) {
        return generate(width, height, -1, -1);
    }

    public BufferedImage generate(final int width, final int height, int startX, int startY) {
        if (overrideRng == null) {
            Random rng = this.rng;
        } else {
            Random rng = overrideRng;
        }

        final int maxRoomWidthByRatio = new Double(width * maxRoomWidthRatio).intValue();
        if (maxRoomWidthByRatio < maxRoomWidth) {
            final int maxRoomWidth = maxRoomWidthByRatio;
        } else {
            final int maxRoomWidth = this.maxRoomWidth;
        }

        if (startX < 0) {
            startX = rng.nextInt(width + 1);
        }
        if (startY < 0) {
            startY = rng.nextInt(height + 1);
        }

        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics g = result.createGraphics();

        g.setColor(Color.BLACK);
        g.fillRect(0, 0, width, height);

        g.setColor(Color.YELLOW);

        int x = startX;
        int y = startY;
        int rw, rh, lx, ly;

        while (currentDensity(result) < targetDensity && rooms.size() < maxRoomCount) {

            boolean overlap = true;
            rw = 1;
            rh = new Double(1 / minSideRatio).intValue() + 1;
            while (overlap) {
                rw = 1;
                rh = new Double(1 / minSideRatio).intValue() + 1;
                while (uniRatio(rw, rh) < minSideRatio) {
                    if (rooms.size() == 0) {
                        x = startX;
                        y = startY;
                    } else {
                        x = rng.nextInt(width + 1);
                        y = rng.nextInt(width + 1);
                    }
                    rw = scewedRandomInt(minRoomWidth, maxRoomWidth, preferredRoomWidth, preferredRoomOdds, rng);
                    rh = scewedRandomInt(minRoomWidth, maxRoomWidth, preferredRoomWidth, preferredRoomOdds, rng);
                    if (x + rw > width) {
                        rw = width - x;
                    }
                    if (y + rh > height) {
                        rh = height - y;
                    }
                }
                overlap = false;
                for (Room room : rooms) {
                    if (room.overlaps(x, y, rw, rh)) {
                        overlap = true;
                        break;
                    }
                }
            }
            Room room = new Room(x, y, rw, rh);
            rooms.add(room);
            room.draw(g);
        }

        g.setColor(Color.red);
        for (Hallway hallway : rooms.getMST().getMinimumSpanningTreeEdgeSet()) {
            hallway.draw(g);

        }

        int[] pixels = result.getRGB(0, 0, width, height, null, 0, width);
        for (int i = 0; i < width * height; i++) {
            boolean issomething = ((pixels[i]) != Color.BLACK.getRGB());

            data[i] = issomething;
        }

        return result;
    }

    private int scewedRandomInt(final int min, final int max, final double scew, final double bias, final Random rng) {
        return min + rng.nextInt(max - min + 1);
    }

    private double uniRatio(final int w, final int h) {
        double a, b;
        if (w > h) {
            a = h;
            b = w;
        } else {
            a = w;
            b = h;
        }

        return a / b;
    }

    private double currentDensity(BufferedImage img) {
        int emptyPixels = 0;

        for (int pixel : img.getRGB(0, 0, img.getWidth(), img.getHeight(), null, 0, img.getWidth())) {
            if (new Color(pixel).equals(Color.BLACK)) {
                emptyPixels++;
            }
        }
        final double result = 1 - ((double) emptyPixels / (img.getHeight() * img.getHeight()));

        return result;
    }

    @Override
    public void generate() {
        generate(w, h, 2, 2);

    }

    private class Rooms extends ArrayList<Room> {


        private static final long serialVersionUID = 8230708060077029555L;
        private final RoomsGraph graph;
        private boolean dirty = true;

        Rooms() {
            super();
            graph = new RoomsGraph(new HallwayFactory());
        }

        @Override
        public boolean add(Room r) {
            super.add(r);
            graph.addVertex(r);
            dirty = true;
            return true;
        }

        @Override
        public boolean remove(Object r) {
            boolean result = super.remove(r);
            if (result) {
                graph.removeVertex((Room) r);
                dirty = true;
            }
            return result;
        }

        private Room[] getDtPoints() {
            final int s = this.size();
            Room[] result = new Room[s];
            for (int i = 0; i < s; i++) {
                result[i] = this.get(i);
            }
            return result;
        }

        public Collection<Hallway> getHallways() {
            ArrayList<Hallway> allEdges = new ArrayList<NaiveDungeonGenerator.Hallway>();
            for (Object edge : graph.edgeSet().toArray()) {
                allEdges.add((Hallway) edge);
            }
            return allEdges;
        }

        public RoomsGraph getFullGraph() {
            graph.removeAllEdges(getHallways());
            Iterator<Triangle_dt> dttit = new Delaunay_Triangulation(getDtPoints()).trianglesIterator();
            while (dttit.hasNext()) {
                Triangle_dt t = dttit.next();

                if (t.p1() != null && t.p2() != null) {
                    graph.addEdge((Room) t.p1(), (Room) t.p2());
                }

                if (t.p2() != null && t.p3() != null) {
                    graph.addEdge((Room) t.p2(), (Room) t.p3());
                }

                if (t.p3() != null && t.p1() != null) {
                    graph.addEdge((Room) t.p3(), (Room) t.p1());
                }
            }
            return graph;
        }

        public KruskalMinimumSpanningTree<Room, Hallway> getMST() {
            return new KruskalMinimumSpanningTree<Room, Hallway>(getFullGraph());
        }

        private class RoomsGraph extends SimpleWeightedGraph<Room, Hallway> {

            private static final long serialVersionUID = -7653852555686827033L;

            RoomsGraph(EdgeFactory<Room, Hallway> ef) {
                super(ef);
            }

            @Override
            public double getEdgeWeight(Hallway e) {
                double x1 = e.source.x - (((e.source.x + e.source.w) - e.source.x) / 2);
                double y1 = e.source.y - (((e.source.y + e.source.h) - e.source.y) / 2);
                double x2 = e.target.x - (((e.target.x + e.target.w) - e.target.x) / 2);
                double y2 = e.target.y - (((e.target.y + e.target.h) - e.target.y) / 2);
                double result = Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
                return 0.0;
            }
        }
    }

    private class HallwayFactory implements EdgeFactory<Room, Hallway> {

        @Override
        public Hallway createEdge(Room sRoom,
                Room tRoom) {
            // TODO Auto-generated method stub
            return new Hallway(sRoom, tRoom);
        }
    }

    private class Hallway extends DefaultEdge {

        private static final long serialVersionUID = 7732696710128827092L;
        private Room source;
        private Room target;

        public Hallway(final Room s, final Room t) {
            source = s;
            target = t;
        }

        @Override
        public Room getSource() {
            return source;
        }

        public void draw(Graphics g) {
            //g.drawRect(getSource().X(), getSource().Y(), 2, getTarget().Y() - getSource().Y());
            myfillRect(g, getSource().X(), getSource().Y(), 2, getTarget().Y() - getSource().Y());
            //g.drawRect(getSource().X(), getTarget().Y(), getTarget().X() - getSource().X(), 2);
            myfillRect(g, getSource().X(), getTarget().Y(), getTarget().X() - getSource().X(), 2);
        }

        @Override
        public Room getTarget() {
            return target;
        }

        private void myfillRect(Graphics g, int x, int y, int width, int height) {
            if (width < 0) {
                x -= Math.abs(width);
            }
            if (height < 0) {
                y -= Math.abs(height);
            }

            g.fillRect(x, y, Math.abs(width), Math.abs(height));
        }
    }

    private class Room extends Point_dt {

        private final int x, y, w, h;

        Room(final int nx, final int ny, final int nw, final int nh) {
            super((double) nx + (nw / 2), (double) ny + (nh / 2));
            x = nx;
            y = ny;
            w = nw;
            h = nh;
        }

        public int X() {
            return new Double(x()).intValue();
        }

        public int Y() {
            return new Double(y()).intValue();
        }

        public boolean overlaps(final int cx, final int cy, final int cw, final int ch) {
            // top-left corner
            if (cx >= x && cx <= x + w && cy >= y && cy <= y + h) {
                return true;
            }
            // top-right corner
            if (cx + cw >= x && cx + cw <= x + w && cy >= y && cy <= y + h) {
                return true;
            }
            // bottom-left corner
            if (cx >= x && cx <= x + w && cy + ch >= y && cy + ch <= y + h) {
                return true;
            }
            // bottom-right corner
            if (cx + cw >= x && cx + cw <= x + w && cy + ch >= y && cy + ch <= y + h) {
                return true;
            }
            // left side
            if (cx >= x && cx <= x + w && cy <= y && cy + ch >= y + h) {
                return true;
            }
            // right side
            if (cx + cw >= x && cx + cw <= x + w && cy <= y && cy + ch >= y + h) {
                return true;
            }
            // top side
            if (cy >= y && cy <= y + h && cx <= x && cx + cw >= x + w) {
                return true;
            }
            // bottom side
            if (cy + ch >= y && cy + ch <= y + h && cx <= x && cx + cw >= x + w) {
                return true;
            }

            return false;
        }

        public void draw(Graphics g) {
            g.fillRect(x, y, w, h);
        }

    }

}
