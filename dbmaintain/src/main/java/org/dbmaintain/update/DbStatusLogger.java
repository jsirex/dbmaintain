package org.dbmaintain.update;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.dbmaintain.script.ExecutedScript;
import org.dbmaintain.script.InvalidObject;

/**
 *
 * @author Andrei_Akatsyeu
 */
public class DbStatusLogger {

    private static Logger logger = Logger.getLogger(DbStatusLogger.class.getName());

    public void log(List<InvalidObject> invalidObjects) {
        logger.info("\n\n============================================== DATABASE INVALID OBJECTS LIST ===========================================\n");
        Iterator<InvalidObject> it = invalidObjects.iterator();
        int scriptNumber = 0;
        InvalidObject object;
        while (it.hasNext()) {
            object = it.next();
            logger.info(++scriptNumber + ") Owner: " + object.getOwner()
                    + "; Object Type: " + object.getObjectType()
                    + "; Object Name: " + object.getObjectName());
        }
    }

    public void log(Set<ExecutedScript> executedScripts) {
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        logger.info("\n\n============================================== DATABASE UPDATE SCRIPTS LIST ============================================\n");
        Iterator<ExecutedScript> it = executedScripts.iterator();
        int scriptNumber = 0;
        ExecutedScript script;
        while (it.hasNext()) {
            script = it.next();
            logger.info(++scriptNumber + ") "
                    + (script.isSuccessful() ? "SUCCESS" : "FAILED")
                    + "; Time Stamp:" + df.format(script.getExecutedAt())
                    + "; Check Sum: " + script.getScript().getCheckSum()
                    + "; Script: " + script.getScript().getFileName());
        }
    }
}
