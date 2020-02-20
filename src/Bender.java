import java.util.*;

class Bender {
    private Mapping mapping;
    private Map<Character,Vector> movement = new HashMap<>();


    // Constructor: ens passen el mapa en forma d'String
    public Bender(String map) {
        movement.put('S', new Vector(1, 0));
        movement.put('E', new Vector(0, 1));
        movement.put('N', new Vector(-1, 0));
        movement.put('W', new Vector(0, -1));

        this.mapping = new Mapping(map);
    }

    // Navegar fins a l'objectiu («$»).
    // El valor retornat pel mètode consisteix en una cadena de
    // caràcters on cada lletra pot tenir
    // els valors «S», «N», «W» o «E»,
    // segons la posició del robot a cada moment.
    public String run() {
        Mapping actionMap = new Mapping(this.mapping.toString());
        StringBuilder result = new StringBuilder();
        Vector actualBender = actionMap.getCoordinatesBender();
        Vector finish = actionMap.getFinish();
        char actualDirection = 'S';
        int index = 0;

        // mentre que el robot no arribi a la meta
        while (!actualBender.equals(finish)) {
            // si la següent posició està buida avançarem el robot
            if (actionMap.getMap()[actualBender.add(movement.get(actualDirection)).getX()][actualBender.add(movement.get(actualDirection)).getY()] == ' ') {
                actionMap.setCoordinatesBender(actualBender.add(movement.get(actualDirection)));
                result.append(actualDirection);
            } else if(actionMap.getMap()[actualBender.add(movement.get(actualDirection)).getX()][actualBender.add(movement.get(actualDirection)).getY()] == '#') {
                //TODO
            } else if (actionMap.getMap()[actualBender.add(movement.get(actualDirection)).getX()][actualBender.add(movement.get(actualDirection)).getY()] == 'T') {
                //TODO
            } else if (actionMap.getMap()[actualBender.add(movement.get(actualDirection)).getX()][actualBender.add(movement.get(actualDirection)).getY()] == 'I') {
                //TODO
            } else {
                // ha arribat al final
                actionMap.setCoordinatesBender(actualBender.add(movement.get(actualDirection)));
                result.append(actualDirection);
            }
            actualBender = actionMap.getCoordinatesBender();
        }

        return result.toString();
    }
}

class Mapping {
    private String mapString;
    private char[][] map;
    private Vector coordinatesBender; //actual
    private Vector finish;
    private List<Vector> inverter = new ArrayList<>();
    private List<Vector> teleporters = new ArrayList<>();

    public Mapping(String map) {
        int mapLength = map.length();
        int width = 0;

        // calculam l'amplada del mapa
        while(map.charAt(width) == '#') {
            width++;
        }

        // calculam la altura del mapa
        int height = 1;
        while (width * height < mapLength) {
            height++;
        }
        height--;

        this.map = new char[height][width];
        this.mapString = map;
        fillMap();
    }

    private void fillMap() {
        int counter = 0;
        for (int i = 0; i < this.map.length; i++) {
            for (int j = 0; j < this.map[0].length; j++, counter++) {
                if (this.mapString.charAt(counter) != '\n') {
                    this.map[i][j] = this.mapString.charAt(counter);

                    // mira si troba el robot
                    if (this.mapString.charAt(counter) == 'X') {
                        this.coordinatesBender = new Vector(i, j);
                    }

                    // mira si troba inverters
                    if (this.mapString.charAt(counter) == 'I') {
                        this.inverter.add(new Vector(i, j));
                    }

                    // mira si troba teleporters
                    if (this.mapString.charAt(counter) == 'T') {
                        this.teleporters.add(new Vector(i, j));
                    }

                    // mira si troba la meta
                    if (this.mapString.charAt(counter) == '$') {
                        this.finish = new Vector(i, j);
                    }
                } else {
                    j--;
                }
            }
        }
    }

    public Vector getCoordinatesBender() {
        return coordinatesBender;
    }

    public Vector getFinish() {
        return finish;
    }

    public char[][] getMap() {
        return map;
    }

    public void setCoordinatesBender(Vector coordinatesBender) {
        this.coordinatesBender = coordinatesBender;
    }

    @Override
    public String toString() {
        return this.mapString;
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

    public Vector add(Vector movement) {
        int x = movement.getX();
        int y = movement.getY();
        return new Vector(this.x + x, this.y + y);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Vector) {
            Vector v = (Vector) o;
            return this.x == v.x && this.y == v.y;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "[" + this.x + ", " + this.y + "]";
    }
}