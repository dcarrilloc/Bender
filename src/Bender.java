import java.util.*;

class Bender {
    private Mapping actionMap;
    private Map<Character, Vector> movement = new HashMap<>();


    // constructor: ens passen el mapa en forma d'String
    public Bender(String map) {
        movement.put('S', new Vector(1, 0));
        movement.put('E', new Vector(0, 1));
        movement.put('N', new Vector(-1, 0));
        movement.put('W', new Vector(0, -1));

        this.actionMap = new Mapping(map);
    }

    // navegar fins a l'objectiu («$»).
    // El valor retornat pel mètode consisteix en una cadena de
    // caràcters on cada lletra pot tenir
    // els valors «S», «N», «W» o «E»,
    // segons la posició del robot a cada moment.
    public String run() {
        StringBuilder result = new StringBuilder();
        Vector actualBender = actionMap.getCoordinatesBender();
        Vector finish = actionMap.getFinish();
        char[] collection = {'S', 'E', 'N', 'W'};
        int index = 0;
        char benderCourse = collection[index];
        boolean inverterState = true;
        int benderCaught = 0;

        Map<Character, List<Vector>> positions = new HashMap<>();
        positions.put('S', new ArrayList<>());
        positions.put('E', new ArrayList<>());
        positions.put('N', new ArrayList<>());
        positions.put('W', new ArrayList<>());

        // mentre que el robot no arribi a la meta
        while (!actualBender.equals(finish)) {
            // si la següent posició està buida avançarem el robot
            if (actionMap.getMap()[actualBender.add(movement.get(benderCourse)).getX()][actualBender.add(movement.get(benderCourse)).getY()] == ' ') {
                actionMap.setCoordinatesBender(actualBender.add(movement.get(benderCourse)));
                positions.get(benderCourse).add(actionMap.getCoordinatesBender());
                result.append(benderCourse);
            }
            // si el robot es troba amb una paret
            else if (actionMap.getMap()[actualBender.add(movement.get(benderCourse)).getX()][actualBender.add(movement.get(benderCourse)).getY()] == '#') {
                if (inverterState) {
                    index = 0;
                } else {
                    index = 2;
                }
                benderCourse = collection[index];
                while (actionMap.getMap()[actualBender.add(movement.get(benderCourse)).getX()][actualBender.add(movement.get(benderCourse)).getY()] == '#') {
                    if (index == 3) index = -1;
                    index++;
                    benderCourse = collection[index];
                    benderCaught++;
                    // comprovam que el robot no es quedi atrapat entre 4 parets
                    if (benderCaught == 4) {
                        return null;
                    }
                }
                benderCaught = 0;
            }
            // si el robot es troba amb un teletransportador
            else if (actionMap.getMap()[actualBender.add(movement.get(benderCourse)).getX()][actualBender.add(movement.get(benderCourse)).getY()] == 'T') {
                actionMap.setCoordinatesBender(actualBender.add(movement.get(benderCourse)));
                result.append(benderCourse);
                actionMap.setCoordinatesBender(findTeleporter());
                positions.get(benderCourse).add(actionMap.getCoordinatesBender());
            }
            // si el robot es troba amb un inversor
            else if (actionMap.getMap()[actualBender.add(movement.get(benderCourse)).getX()][actualBender.add(movement.get(benderCourse)).getY()] == 'I') {
                actionMap.setCoordinatesBender(actualBender.add(movement.get(benderCourse)));
                positions.get(benderCourse).add(actionMap.getCoordinatesBender());
                result.append(benderCourse);
                if (inverterState) {
                    inverterState = false;
                } else {
                    inverterState = true;
                }
            }
            // arribarà quan passi per la seva posició inicial
            else {
                actionMap.setCoordinatesBender(actualBender.add(movement.get(benderCourse)));
                positions.get(benderCourse).add(actionMap.getCoordinatesBender());
                result.append(benderCourse);
            }
            actualBender = actionMap.getCoordinatesBender();
            // comprovam que no hagi entrat en un bucle infinit
            if (impossibleMap(positions)) {
                return null;
            }
        }
        return result.toString();
    }

    // Aquesta funció retornarà el teleporter més proper del robot
    public Vector findTeleporter() {
        Vector bender = actionMap.getCoordinatesBender();
        List<Vector> vectorList = actionMap.getTeleporters();
        Vector current;
        Vector sub;
        Vector result = new Vector(999, 999);
        Iterator<Vector> it = vectorList.iterator();

        while (it.hasNext()) {
            current = it.next();
            sub = bender.sub(current);
            if ((sub.getX() + sub.getY() < bender.sub(result).getX() + bender.sub(result).getY() && sub.getX() + sub.getY() != 0)) {
                result = current;
            }
        }
        return result;
    }

    // Aquesta funcio retornarà true o false si troba que el robot
    // ha passat per la mateixa coordenada amb la mateixa direcció
    // anteriorment. En aquest cas, el mapa és impossible.
    public boolean impossibleMap(Map<Character, List<Vector>> positions) {
        Iterator<Map.Entry<Character, List<Vector>>> mapIt = positions.entrySet().iterator();
        char[] index = new char[4];
        for (int i = 0; i < index.length; i++) {
            index[i] = mapIt.next().getKey();
            List<Vector> vectorList = positions.get(index[i]);
            for (int j = 0; j < vectorList.size() - 1; j++) {
                for (int k = j + 1; k < vectorList.size(); k++) {
                    if (vectorList.get(j).equals(vectorList.get(k))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}

class Mapping {
    private String mapString;
    private char[][] map;
    private Vector coordinatesBender;
    private Vector finish;
    private List<Vector> inverter = new ArrayList<>();
    private List<Vector> teleporters = new ArrayList<>();

    public Mapping(String map) {
        int mapLength = map.length();
        int width = 0;
        int height = 1;

        // calculam l'amplada i l'altura del mapa
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
        this.mapString = map;
        this.map = new char[height][width];
        fillMap();
    }

    private void fillMap() {
        int counter = 0;
        for (int i = 0; i < this.map.length; i++) {
            for (int j = 0; j < this.map[0].length; j++) {
                // si el caràcter és diferent a un salt de linia
                if (this.mapString.charAt(counter) != '\n') {
                    // mira si ha trobat un teleporter i guarda les seves coordenades
                    if (this.mapString.charAt(counter) == 'T') {
                        teleporters.add(new Vector(i, j));
                    }
                    // mira si ha trobat un inverter i guarda les seves coordenades
                    if (this.mapString.charAt(counter) == 'I') {
                        inverter.add(new Vector(i, j));
                    }
                    // mira si ha trobat la meta i guarda les seves coordenades
                    if (this.mapString.charAt(counter) == '$') {
                        finish = new Vector(i, j);
                    }
                    // mira si ha trobat el bender i guarda les seves coordenades
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

    public Vector add(Vector movement) {
        int x = movement.getX();
        int y = movement.getY();
        return new Vector(this.x + x, this.y + y);
    }

    public Vector sub(Vector v) {
        int x = v.getX();
        int y = v.getY();
        return new Vector(Math.abs(this.x - x), Math.abs(this.y - y));
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
    public String toString() {
        return "[" + this.x + ", " + this.y + "]";
    }
}
