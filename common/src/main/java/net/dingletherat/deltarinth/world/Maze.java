package net.dingletherat.deltarinth.world;

import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;
import net.minecraft.util.RandomSource;

/**
 * Represents a maze as a 2D array of states, whether wall or not
 */
public class Maze {

    public static void main(String[] args) {
        System.out.println(new Maze(10, 4, 3, 3, null).toString());
    }

    // represents an integer ordered pair of x, z
    private class Pair {
        int x, z;

        Pair(int x, int z) {
            this.x = x;
            this.z = z;
        }

        Pair plus(int x, int z) {
            return new Pair(this.x + x, this.z + z);
        }

        @Override
        public String toString() {
            return x + ", " + z;
        }
    }

    private boolean[][] map;  // x, then z; true = no wall
    private RandomSource randomSource;

    private int cellsX;    // how many maze cells there are across the x-axis
    private int cellsZ;    // how many maze cells there are across the z-axis
    private int width;     // how many blocks there are across the x-axis in the map
    private int height;    // how many blocks there are across the z-axis in the map
    private int corridor;  // how many blocks wide is each corridor
    private int wall;      // how many blocks wide is each wall
    private int shift;     // how many blocks to move over every time; corridor + wall

    Stack<Pair> stack = new Stack<>();

    public Maze(int cellsX, int cellsZ, int corridor, int wall, RandomSource randomSource) {
        this.cellsX = cellsX;
        this.cellsZ = cellsZ;
        this.corridor = corridor;
        this.wall = wall;
        this.shift = corridor + wall;

        this.width = shift * cellsX + wall;   // +wall to include both outer walls
        this.height = shift * cellsZ + wall;  // +wall to include both outer walls

        this.map = new boolean[width][height];
        this.randomSource = randomSource;

        buildMaze();
    }

    private void buildMaze() {
        stack.push(new Pair(wall, wall));  // start at top left
        draw(stack.peek(), stack.peek());

        while (!stack.empty()) {
            forward();
        }
    }

    private void forward() {
        ArrayList<Pair> neighbours = new ArrayList<>(4);

        Pair prev = stack.peek();
        Pair check = prev.plus(shift, 0);
        if (available(check)) neighbours.add(check);
        check = prev.plus(-shift, 0);
        if (available(check)) neighbours.add(check);
        check = prev.plus(0, shift);
        if (available(check)) neighbours.add(check);
        check = prev.plus(0, -shift);
        if (available(check)) neighbours.add(check);

        if (neighbours.size() > 0) {
            int next = randomSource.nextInt(neighbours.size()); // 0 to size, exclusive

            stack.push(neighbours.get(next));
            draw(prev, stack.peek());
        } else {
            Pair curr = stack.pop();
            draw(curr, prev);
        }
    }

    // sets the corridor by corridor square with the corner at p to true
    private void draw(Pair curr, Pair next) {
        map[curr.x][curr.z] = true;
        if (curr.x != next.x) {
            // Horizontal corridor
            int x1 = Math.min(curr.x, next.x);
            int x2 = Math.max(curr.x, next.x) + corridor;
            int z = curr.z;

            for (int x = x1; x < x2; x++) {
                for (int dz = 0; dz < corridor; dz++) {
                    map[x][z + dz] = true;
                }
            }
        } else {
            // Vertical corridor
            int z1 = Math.min(curr.z, next.z);
            int z2 = Math.max(curr.z, next.z) + corridor;
            int x = curr.x;

            for (int z = z1; z < z2; z++) {
                for (int dx = 0; dx < corridor; dx++) {
                    map[x + dx][z] = true;
                }
            }
        }
    }

    // returns true if the coordinates are on the map and if the value on the map is false
    private boolean available(Pair p) {
        if (p.x < 0 ||
            p.x >= width ||
            p.z < 0 ||
            p.z >= height) return false;
        return !map[p.x][p.z];
    }

    public int getCellsX() { return cellsX; }
    public int getCellsZ() { return cellsZ; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public int getCorridor() { return corridor; }
    public int getWall() { return wall; }
    public boolean getAt(int x, int z) { return map[x][z]; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("MAZE: \n");

        for (int z = 0; z < height; z++) {
            for (int x = 0; x < width; x++) {
                sb.append(map[x][z] ? "  " : "X ");
            }
            sb.append("\n");
        }

        return sb.toString();
    }
}
