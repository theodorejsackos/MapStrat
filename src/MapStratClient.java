import model.DrawModel;
import model.MapModel;
import model.SessionModel;
import view.MapStratFrame;

import java.awt.*;

/** The MapStratClient initializes and displays the MapStratFrame on the main thread, then exits.
 * The execution of the program will be managed from the Event Dispatch Thread as managed by
 * Swing.
 *
 * Invocation:
 * java MapStratClient
 *  -- There are no expected or used command line parameters.
 *
 * @author Theodore Sackos (theodorejsackos@email.arizona.edu)
 */
public class MapStratClient {

    private static class TimedEventQueue extends EventQueue {
        @Override
        protected void dispatchEvent(AWTEvent event) {
            long startNano = System.nanoTime();
            super.dispatchEvent(event);
            long endNano = System.nanoTime();

            if (endNano - startNano > 5000000)
                System.out.println(((endNano - startNano) / 1000000)+"ms: "+event);
        }
    }

    private static final boolean DEBUG_GUI_BOTTLENECKS = false;
    public static void main(String[] args){
        if(DEBUG_GUI_BOTTLENECKS)
            /* Registers a new event queue that tracks how long each action in the queue takes to execute */
            Toolkit.getDefaultToolkit().getSystemEventQueue().push(new TimedEventQueue());

        MapModel  mapModel     = new MapModel(MapModel.MAP_DEFAULT);
        DrawModel drawModel    = new DrawModel();

        MapStratFrame window = new MapStratFrame(mapModel, drawModel);
        window.setVisible(true);
    }
}
