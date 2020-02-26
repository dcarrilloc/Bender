import java.util.*;

class Bender {
    private Mapping actionMap;
    private Map<Character, Vector> movement = new HashMap<>();

    // Constructor: ens passen el mapa en forma d'String.
    public Bender(String map) {
        movement.put('S', new Vector(1, 0));
        movement.put('E', new Vector(0, 1));
        movement.put('N', new Vector(-1, 0));
        movement.put('W', new Vector(0, -1));

        this.actionMap = new Mapping(map);
    }

    // Navegar fins a l'objectiu («$»).
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
        int checkingCounter = 0;

        Map<Character, List<Vector>> positions = new HashMap<>();
        positions.put('S', new ArrayList<>());
        positions.put('E', new ArrayList<>());
        positions.put('N', new ArrayList<>());
        positions.put('W', new ArrayList<>());

        // Comprovam que el mapa és vàlid.
        if (!actionMap.validMap()) return null;

        // Mentre que el robot no arribi a la meta...
        while (!actualBender.equals(finish)) {
            // Si la següent posició està buida avançarem el robot.
            if (actionMap.getMap()[actualBender.add(movement.get(benderCourse)).getX()][actualBender.add(movement.get(benderCourse)).getY()] == ' ') {
                actionMap.setCoordinatesBender(actualBender.add(movement.get(benderCourse)));
                positions.get(benderCourse).add(actionMap.getCoordinatesBender());
                result.append(benderCourse);
            }
            // Si el robot es troba amb una paret canviarà la seva direcció.
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
                    // Comprovam que el robot no es quedi atrapat entre 4 parets.
                    if (benderCaught == 4) {
                        return null;
                    }
                }
                benderCaught = 0;
            }
            // Si el robot es troba amb un teletransportador s'anirà al teletransportador més proper.
            else if (actionMap.getMap()[actualBender.add(movement.get(benderCourse)).getX()][actualBender.add(movement.get(benderCourse)).getY()] == 'T') {
                actionMap.setCoordinatesBender(actualBender.add(movement.get(benderCourse)));
                result.append(benderCourse);
                actionMap.setCoordinatesBender(findTeleporter());
                positions.get(benderCourse).add(actionMap.getCoordinatesBender());
            }
            // Si el robot es troba amb un inversor canviaran les seves prioritats de direcció.
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
            else {
                // Arribarà quan passi per la seva posició inicial.
                actionMap.setCoordinatesBender(actualBender.add(movement.get(benderCourse)));
                positions.get(benderCourse).add(actionMap.getCoordinatesBender());
                result.append(benderCourse);
            }
            actualBender = actionMap.getCoordinatesBender();

            /* enseñar a samuel
            checkingCounter++;
            // Comprovam que no hagi entrat en un bucle infinit.
            if (checkingCounter > 99 && impossibleMap(positions)) {
                return null;
            } else if (checkingCounter > 99) {
                checkingCounter = 0;
            }

             */
            if (impossibleMap(positions)) return  null;
        }
        return result.toString();
    }

    // Aquesta funció retornarà les coordenades del teleporter més proper del robot.
    public Vector findTeleporter() {
        Vector bender = actionMap.getCoordinatesBender();
        List<Vector> vectorList = actionMap.getTeleporters();
        List<Vector> sameDistanceTeleporters = new ArrayList<>();
        Vector current;
        Vector sub;
        Vector result = new Vector(999, 999);
        double angle = 366;
        double currentAngle = 0;

        for (Vector vector : vectorList) {
            current = vector;
            sub = bender.sub(current);
            if ((sub.getX() + sub.getY() < bender.sub(result).getX() + bender.sub(result).getY() && sub.getX() + sub.getY() != 0)) {
                sameDistanceTeleporters.clear();
                sameDistanceTeleporters.add(current);
                result = current;
            } else if (sub.getX() + sub.getY() == bender.sub(result).getX() + bender.sub(result).getY()) {
                // Si dos teletransportadors estan a la mateixa
                // aplicam la regla del rellotje per retornar el teleporter adequat.
                sameDistanceTeleporters.add(current);
            }
        }

        // Un pic emmagatzemat els teleportadors que estan a la
        // mateixa distància, aplicarem la regla del rellotje.
        for (Vector actual : sameDistanceTeleporters) {
            // Miram a quina meitat es troba el teletransportador.
            if (actual.getY() >= bender.getY()) {
                // Si es troba a la meitat de l'esquerra del robot.
                currentAngle = actual.angle(bender, new Vector(0, 1));
            } else {
                // Si es troba a la meitat de la dreta del robot.
                currentAngle = actual.angle(bender, new Vector(0, -1)) + 180;
            }
            if (currentAngle < angle) {
                angle = currentAngle;
                result = actual;
            }
        }
        return result;
    }

    // Aquesta funcio retornarà TRUE o FALSE si troba que el robot
    // ha passat per la mateixa coordenada amb la mateixa direcció
    // més de dos pics anteriorment. En aquest cas, el mapa és impossible.
    public boolean impossibleMap(Map<Character, List<Vector>> positions) {
        int counter = 0;
        Iterator<Map.Entry<Character, List<Vector>>> mapIt = positions.entrySet().iterator();
        char[] index = new char[4];
        for (int i = 0; i < index.length; i++) {
            index[i] = mapIt.next().getKey();
            List<Vector> vectorList = positions.get(index[i]);
            for (int j = 0; j < vectorList.size() - 1; j++) {
                for (int k = j + 1; k < vectorList.size(); k++) {
                    if (vectorList.get(j).equals(vectorList.get(k))) {
                        counter++;
                    }
                }
                if (counter > 1) {
                    return true;
                }
                counter = 0;
            }
        }
        return false;
    }

    // Trobar l'objectiu ($) a través del cami més curt.
    public int bestRun() {
        boolean [][] visited = new boolean[this.actionMap.getMap().length][this.actionMap.getMap()[0].length];

        // Emplenam el nostre array visited amb false perquè de moment no hem visitat cap estat.
        for (int i = 0; i < visited.length; i++) {
            for (int j = 0; j < visited[0].length; j++) {
                visited[i][j] = false;
            }
        }


        // Declaram el nostre estat inicial (posició inicial del Bender).
        State initial = new State(actionMap.getCoordinatesBender(), 0);
        State actual;

        // Cua de tots els possibles estats per els que es passi fins arribar al destí.
        Queue<State> queue = new LinkedList<>();
        queue.offer(initial);

        // Per incrementar coordenades en x i y.
        int [] incrementX = {0, 0, 1, -1};
        int [] incrementY = {1, -1, 0, 0};

        while (!queue.isEmpty()) {
            actual = queue.poll();
            if (actionMap.getMap()[actual.getCoordenates().getX()][actual.getCoordenates().getY()] == '$') {
                System.out.println(actual.getDistance());
                return actual.getDistance();
            }
            visited[actual.getCoordenates().getX()][actual.getCoordenates().getY()] = true;
            for (int i = 0; i < 4; i++) {
                int x = incrementX[i] + actual.getCoordenates().getX();
                int y = incrementY[i] + actual.getCoordenates().getY();

                // Comprovam que la coordenada adjacent no sobrepassi els limits del mapa,
                // que no sigui paret (#) i que no estigui visitat
                if (x >= 0 && x < actionMap.getMap().length && y >= 0 && y < actionMap.getMap()[0].length && actionMap.getMap()[x][y] != '#' && !visited[x][y]) {
                    State neighbour = new State(new Vector(x, y), actual.getDistance() + 1);
                    queue.offer(neighbour);
                }
            }

        }
        return -1;
    }
}

// Objecte mapa que emprarem per crear el nostre mapa
// segons un String i moure el robot a través d'ell.
class Mapping {
    private String mapString;
    private char[][] map;
    private Vector coordinatesBender;
    private Vector finish;
    private List<Vector> inverter = new ArrayList<>();
    private List<Vector> teleporters = new ArrayList<>();

    // Constructor del mapa.
    public Mapping(String map) {
        int mapLength = map.length();
        int width = 0;
        int height = 1;

        // Calculam l'amplada i l'altura del mapa.
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

    // Emplenam el nostre mapa que emprarem per moure el robot.
    private void fillMap() {
        int counter = 0;
        for (int i = 0; i < this.map.length; i++) {
            for (int j = 0; j < this.map[0].length; j++) {
                // Si el caràcter és diferent a un salt de linia
                if (this.mapString.charAt(counter) != '\n') {
                    // Mira si ha trobat un teleporter i guarda les seves coordenades
                    if (this.mapString.charAt(counter) == 'T') {
                        teleporters.add(new Vector(i, j));
                    }
                    // Mira si ha trobat un inverter i guarda les seves coordenades
                    if (this.mapString.charAt(counter) == 'I') {
                        inverter.add(new Vector(i, j));
                    }
                    // Mira si ha trobat la meta i guarda les seves coordenades
                    if (this.mapString.charAt(counter) == '$') {
                        finish = new Vector(i, j);
                    }
                    // Mira si ha trobat el bender i guarda les seves coordenades
                    if (this.mapString.charAt(counter) == 'X') {
                        coordinatesBender = new Vector(i, j);
                    }
                    this.map[i][j] = this.mapString.charAt(counter);
                    counter++;
                    if (counter == this.mapString.length()) break;
                    if (j == this.map[0].length - 1 && this.mapString.charAt(counter) == '\n') counter++;
                } else {
                    // Si el caràcter és un salt de linia afegim els espais
                    // necessaris per completar la linia del nostre array.
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

    // Comprova si un mapa és vàlid i retorna un
    // booleà TRUE so és vàlid i FALSE si no.
    public boolean validMap() {
        for (int i = 0; i < this.map.length - 1; i++) {
            for (int j = 0; j < this.map[0].length; j++) {
                if (this.map[i][j] != ' ' && this.map[i][j] != '#' && this.map[i][j] != 'T' && this.map[i][j] != 'I' && this.map[i][j] != 'X' && this.map[i][j] != '$') {
                    return false;
                }
            }
        }
        return true;
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

// Objecte Vector per definir les coordenades dins un array bidimensional.
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

    // Retorna el vector resultant de la suma de dos vectors.
    public Vector add(Vector movement) {
        int x = movement.getX();
        int y = movement.getY();
        return new Vector(this.x + x, this.y + y);
    }

    // Retorna el vector resultant de la resta de dos vectors.
    public Vector sub(Vector v) {
        int x = v.getX();
        int y = v.getY();
        return new Vector(Math.abs(this.x - x), Math.abs(this.y - y));
    }

    // Retorna l'angle entre el teletransportador i un angle imaginari segons la posició del robot.
    public double angle(Vector bender, Vector v) {
        // Primer de tot corregirem el Vector actual a partir de les coordenades del robot.
        Vector current = new Vector(this.getY() - bender.getY(), bender.getX() - this.getX());

        // Calculam l'angle entre el teletransportador i el Vector v imaginari
        double result = (current.getX() * v.getX() + current.getY() * v.getY()) / ((Math.sqrt(Math.pow(current.getX(), 2) + Math.pow(current.getY(), 2))) * (Math.sqrt(Math.pow(v.getX(), 2) + Math.pow(v.getY(), 2))));
        result = Math.toDegrees(Math.acos(result));
        return result;
    }

    // Compara dos vectors i retorna TRUE si són iguals i FALSE si no ho son.
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

class State {
    private Vector coordenates;
    private int distance;

    public State(Vector coordenates, int distance) {
        this.coordenates = coordenates;
        this.distance = distance;
    }

    public Vector getCoordenates() {
        return coordenates;
    }

    public int getDistance() {
        return distance;
    }
}
