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
                actionMap.setCoordinatesBender(findTeleporter(actionMap.getCoordinatesBender()));
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
            checkingCounter++;
            // Comprovam que no hagi entrat en un bucle infinit.
            if (checkingCounter > 99 && impossibleMap(positions)) {
                return null;
            } else if (checkingCounter > 99) {
                checkingCounter = 0;
            }
        }
        return result.toString();
    }

    // Aquesta funció retornarà les coordenades del teleporter més proper del robot.
    public Vector findTeleporter(Vector bender) {
        List<Vector> vectorList = actionMap.getTeleporters();
        List<Vector> sameDistanceTeleporters = new ArrayList<>();
        Vector current;
        Vector sub;
        Vector result = new Vector(999, 999);
        double angle = 366;
        double currentAngle = 0;

        // Emmagatzemam els teleportadors que estan a la minima distància dins sameDistanceTeleporters
        for (Vector vector : vectorList) {
            current = vector;
            sub = bender.sub(current);
            if ((sub.getX() + sub.getY() < bender.sub(result).getX() + bender.sub(result).getY() && sub.getX() + sub.getY() != 0)) {
                sameDistanceTeleporters.clear();
                sameDistanceTeleporters.add(current);
                result = current;
            } else if (sub.getX() + sub.getY() == bender.sub(result).getX() + bender.sub(result).getY()) {
                // Si dos teletransportadors estan a la mateixa
                // aplicam la regla del rellotge per retornar el teleporter adequat.
                sameDistanceTeleporters.add(current);
            }
        }

        // Un pic emmagatzemat els teleportadors que estan a la
        // mateixa distància, aplicarem la regla del rellotge.
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

    // Trobar la distància més curta per arribar a l'objectiu ($).
    public int bestRun() {
        List<Cell> openList = new ArrayList<>();
        List<Cell> closedList = new ArrayList<>();
        // Inicialitzam la cel·la on es troba el robot.
        Cell actual = new Cell(actionMap.getCoordinatesBender(), actionMap.getFinish());
        Cell neighbour;

        closedList.add(actual);

        // Per incrementar coordenades en x i y.
        int [] incrementX = {0, 0, 1, -1};
        int [] incrementY = {1, -1, 0, 0};

        while (true) {
            // Ficam en openList tots els veïns de actual
            for (int i = 0; i < 4; i++) {
                int x = incrementX[i] + actual.getPosition().getX();
                int y = incrementY[i] + actual.getPosition().getY();

                // Si no es surt dels limits del mapa, ni és una paret, ni està ja a openList ni a closeList...
                if (x >= 0 && x < actionMap.getMap().length && y >= 0 && y < actionMap.getMap()[0].length && actionMap.getMap()[x][y] != '#' && !containsCell(openList, new Vector(x, y)) && !containsCell(closedList, new Vector(x, y))) {
                    if (actionMap.getMap()[x][y] == 'T') {
                        neighbour = new Cell(findTeleporter(new Vector(x, y)), actual, actionMap.getFinish());
                    } else {
                        neighbour = new Cell(new Vector(x, y), actual, actionMap.getFinish());
                    }
                    openList.add(neighbour);
                }
            }

            // Comprovam que la meta no estigui dins openList
            for (Cell onGoing : openList) {
                if (onGoing.getPosition().equals(actionMap.getFinish())) {
                    return onGoing.getG();
                }
            }

            // Un pic tenim en openList tots els veïns de la cel·la actual elegim la cel·la amb menys F(n).
            double maxF = 999999999;
            int index = 0;
            int counter = 0;
            for (Cell onGoing : openList) {
                if (onGoing.getF() < maxF) {
                    maxF = onGoing.getF();
                    index = counter;
                }
                counter++;
            }
            // L'element amb menys F(n) és l'element amb posició 'index' dins openList
            actual = openList.get(index);
            closedList.add(actual);
            openList.remove(index);

            // Miram si millora el pas.
            for (int i = 0; i < 4; i++) {
                int x = incrementX[i] + actual.getPosition().getX();
                int y = incrementY[i] + actual.getPosition().getY();

                Cell onGoing = new Cell(new Vector(x, y), actual, new Vector(actionMap.getFinish().getX(), actionMap.getFinish().getY()));
                if (x >= 0 && x < actionMap.getMap().length && y >= 0 && y < actionMap.getMap()[0].length && actionMap.getMap()[x][y] != '#' && !containsCell(closedList, new Vector(x, y)) && onGoing.getG() + 1 < actual.getG()) {
                    onGoing.setBackCell(actual);
                    onGoing.setG(1 + actual.getG());
                    onGoing.setF(onGoing.getG() + onGoing.getH());
                }
            }
        }
    }

    // Retorna TRUE si cell hi existeix dins la llista i FALSE si no.
    public boolean containsCell(List<Cell> openList, Vector cell) {
        for (Cell actual : openList) {
            if (actual.getPosition().equals(cell)) {
                return true;
            }
        }
        return false;
    }
}

// Objecte mapa que emprarem per crear el nostre mapa
// segons un String i moure el robot a través d'ell.
class Mapping {
    private String mapString;
    private char[][] map;
    private Vector coordinatesBender;
    private Vector finish;
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
        // Comprova que no hi hagi un altre caràcter dels permesos al mapa
        for (int i = 0; i < this.map.length - 1; i++) {
            for (int j = 0; j < this.map[0].length; j++) {
                if (this.map[i][j] != ' ' && this.map[i][j] != '#' && this.map[i][j] != 'T' && this.map[i][j] != 'I' && this.map[i][j] != 'X' && this.map[i][j] != '$') {
                    return false;
                }
            }
        }

        // Comprova que el mapa sigui un mapa tancat
        int counter = 0;
        for (int i = 0; i < this.map.length; i++) {
            for (int j = 0; j < this.map[0].length; j++) {
                if (this.map[i][j] == '#') counter++;
            }
            if (counter < 2) return false;
            counter = 0;
        }

        // Comprova que el mapa tengui objectiu i robot
        if (this.coordinatesBender == null || this.finish == null) return false;
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

// Objecte Cell per definir l'estat de cada cel·la dins un mapa.
class Cell {
    private Vector position;
    private Cell backCell;
    private Vector finish;
    private double f;
    private int g;
    private double h;

    // Constructor general.
    public Cell(Vector position, Cell backCell, Vector finish) {
        this.position = position;
        this.finish = finish;
        this.backCell = backCell;
        this.h = Math.sqrt(Math.pow(this.finish.getX() - position.getY(), 2) + Math.pow(this.finish.getY() - position.getY(), 2));
        this.g = 1 + this.getBackCell().getG();
        this.f = this.g + this.h;
    }

    // Constructor per la primera Cel·la.
    public Cell(Vector position, Vector finish) {
        this.position = position;
        this.finish = finish;
        this.g = 0;
        this.h = 0;
        this.f = 0;
    }

    public Vector getPosition() {
        return position;
    }

    public double getF() {
        return f;
    }

    public int getG() {
        return g;
    }

    public Cell getBackCell() {
        return backCell;
    }

    public void setBackCell(Cell backCell) {
        this.backCell = backCell;
    }

    public double getH() {
        return h;
    }

    public void setF(double f) {
        this.f = f;
    }

    public void setG(int g) {
        this.g = g;
    }
}
