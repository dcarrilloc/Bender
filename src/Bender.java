import java.util.*;

class Bender {
    private Mapping mapping;
    private Map<Character, Vector> movement = new HashMap<>();


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
        char[] collection = {'S', 'E', 'N', 'W'};
        int index = 0;
        char actualDirection = collection[index];

        // mentre que el robot no arribi a la meta
        while (!actualBender.equals(finish)) {
            // si la següent posició està buida avançarem el robot
            if (actionMap.getMap()[actualBender.add(movement.get(actualDirection)).getX()][actualBender.add(movement.get(actualDirection)).getY()] == ' ') {
                actionMap.setCoordinatesBender(actualBender.add(movement.get(actualDirection)));
                result.append(actualDirection);
            } else if (actionMap.getMap()[actualBender.add(movement.get(actualDirection)).getX()][actualBender.add(movement.get(actualDirection)).getY()] == '#') {
                index = 0;
                actualDirection = collection[index];
                while (actionMap.getMap()[actualBender.add(movement.get(actualDirection)).getX()][actualBender.add(movement.get(actualDirection)).getY()] == '#') {
                    index++;
                    actualDirection = collection[index];
                }
            } else if (actionMap.getMap()[actualBender.add(movement.get(actualDirection)).getX()][actualBender.add(movement.get(actualDirection)).getY()] == 'T') {
                actionMap.setCoordinatesBender(actionMap.getTeleporters().get(0));
                result.append(actualDirection);
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
        int height = 1;

        // calculam l'amplada màxima del mapa
        int counter = 0;
        for (int i = 0; i < mapLength; i++) {
            if (map.charAt(i) != '\n') {
                counter++;
            } else {
                if (counter > width) {
                    width = counter;
                }
                height++;
                counter = 0;
            }
        }

        this.map = new char[height][width];
        this.mapString = map;
        fillMap();
    }

    private void fillMap() {
        int counter = 0;
        for (int i = 0; i < this.map.length; i++) {
            for (int j = 0; j < this.map[0].length; j++) {
                if (this.mapString.charAt(counter) != '\n') {
                    // si el caràcter és diferent a un salt de linia

                    // mira si ha trobat un teleporter
                    if (this.mapString.charAt(counter) == 'T') {
                        teleporters.add(new Vector(i, j));
                    }
                    // mira si ha trobat un inverter
                    if (this.mapString.charAt(counter) == 'I') {
                        inverter.add(new Vector(i, j));
                    }
                    // mira si ha trobat la meta
                    if (this.mapString.charAt(counter) == '$') {
                        finish = new Vector(i, j);
                    }
                    // mira si ha trobat el bender
                    if (this.mapString.charAt(counter) == 'X') {
                        coordinatesBender = new Vector(i, j);
                    }
                    this.map[i][j] = this.mapString.charAt(counter);
                    counter++;
                    if (counter == this.mapString.length()) break;
                    if (j == this.map[0].length - 1 && this.mapString.charAt(counter) == '\n') counter++;
                } else {
                    // si el caràcter és un salt de linia afegim els espais necessaris
                    while (j < this.map[0].length) {
                        this.map[i][j] = ' ';
                        j++;
                    }
                    counter++;
                    break;
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

    public List<Vector> getTeleporters() {
        return teleporters;
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
