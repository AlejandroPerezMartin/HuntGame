
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Hunter extends Thread implements FieldItem {

    private int hunted;
    private boolean alive;
    private Position position;
    private final HuntField huntField;

    public Hunter(HuntField huntField) {
        this.hunted = 0;
        this.huntField = huntField;
        this.alive = true;
        // Do-while solves error of while loop accessing an invalid position
        do {
            this.position = getRandomPosition();
        } while (huntField.setItem(this, position) == false);
    }

    @Override
    public char getType() {
        return 'H';
    }

    @Override
    public boolean fired() {
        if (alive()) {
            alive = false;
            return true;
        }
        return false;
    }

    public boolean alive() {
        return alive;
    }

    public int hunted() {
        return hunted;
    }

    // http://lineadecodigo.com/java/generar-un-numero-aleatorio/
    private Position getRandomPosition() {
        Random random = new Random();
        return new Position(random.nextInt(huntField.getXLength()), random.nextInt(huntField.getYLength()));
    }

    @Override
    public void run() {
        int shotDirection = 0; // by default, first shot is up
        Random random = new Random();
        Position newShotPosition = null;

        while (alive() && huntField.getNumberOfItems('D') > 0) {

            try {
                Thread.sleep(random.nextInt(101));
            } catch (InterruptedException e) {
                return;
            }

            switch (shotDirection) {
                case 0:
                    // Shoot up
                    newShotPosition = new Position(position.getX(), position.getY() - 1);
                    break;
                case 1:
                    // Shoot right
                    newShotPosition = new Position(position.getX() + 1, position.getY());
                    break;
                case 2:
                    // Shoot down
                    newShotPosition = new Position(position.getX(), position.getY() + 1);
                    break;
                case 3:
                    // Shoot left
                    newShotPosition = new Position(position.getX() - 1, position.getY());
                    break;
            }
            
            // Next shot direction
            shotDirection++;

            // If counter is greater than 3, shot direction is changed to default value 0 (shoot up)
            if (shotDirection > 3) {
                shotDirection = 0;
            }
            
            huntField.shot(newShotPosition);

            // New shot position is a duck
            if (huntField.getItemType(newShotPosition) == 'D') {
                hunted++;
                // Move hunter to duck position to catch the prey
                if (huntField.moveItem(this, position, newShotPosition)) {
                    position = newShotPosition;
                }
            }
        }
        // Remove hunter if shot or there are no more ducks
        huntField.removeItem(this, position);
    }
}
