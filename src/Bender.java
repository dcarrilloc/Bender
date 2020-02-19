import java.util.Arrays;
import java.util.List;

class Bender {
    private Map map;

    // Constructor: ens passen el mapa en forma d'String
    public Bender(String map) {
        this.map = new Map(map);
    }

    // Navegar fins a l'objectiu («$»).
    // El valor retornat pel mètode consisteix en una cadena de
    // caràcters on cada lletra pot tenir
    // els valors «S», «N», «W» o «E»,
    // segons la posició del robot a cada moment.
    public String run() {
        return "";
    }

}

class Map {
    private String mapString;
    private char[][] map;
    private Vector coordinatesBender; //actual
    private Vector finish;
    private List<Vector> inverter;
    private List<Vector> teleporters;

    public Map(String map) {
        int mapLength = map.length();
        int width = 0;

        // calculam l'amplada del mapa
        while(map.charAt(width) == '#') {
            width++;
        }

        // calculam la altura del mapa
        int height = 1;
        while (width * height <= mapLength) {
            height++;
        }
        height--;

        this.map = new char[height][width];
        this.mapString = map;
        fillMap();
        System.out.println(Arrays.deepToString(this.map));
    }

    private void fillMap() {
        int counter = 0;
        for (int i = 0; i < this.map.length; i++) {
            for (int j = 0; j < this.map[0].length; j++) {
                this.map[i][j] = mapString.charAt(counter);
                counter ++;
            }
            counter++;
        }
    }
}

class Vector {
    private int x;
    private int y;

    public Vector(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }
}