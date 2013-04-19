package org.dbmaintain.update;

import org.dbmaintain.DbMaintainer;
import org.dbmaintain.MainFactory;
import org.dbmaintain.script.executedscriptinfo.DatabaseStatusInfoSource;
import org.dbmaintain.script.executedscriptinfo.ExecutedScriptInfoSource;
import org.dbmaintain.util.DbMaintainException;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: Andrei_Akatsyeu
 * Date: 4/18/13
 * Time: 10:57 AM
 * To change this template use File | Settings | File Templates.
 */
public class DatabaseUpdater {

    public static final String DB_ALLOWED_AUTO_UPDATE_KEY = "database.allow.automatic.update";

    private static Logger logger = Logger.getLogger(DatabaseUpdater.class
            .getName());

    /**
     * Updates database using dbmaintainer
     *
     * @param dbprops
     * @return true if no errors
     */
    public Boolean update(Properties dbprops, boolean dryRun) {
        if (dbprops == null) {
            return true;
        }
        if (!Boolean.valueOf(dbprops.getProperty(DB_ALLOWED_AUTO_UPDATE_KEY))) {
            logger.info("Not updating database. NOT Allowed. Change "
                    + DB_ALLOWED_AUTO_UPDATE_KEY + " to true");
            return true;
        }


        MainFactory mainFactory = new MainFactory(dbprops);
        DbMaintainer dbMaintainer = mainFactory.createDbMaintainer();
        ExecutedScriptInfoSource executedScriptInfoSource = mainFactory.createExecutedScriptInfoSource();
        try {
            executedScriptInfoSource.removeErrorScripts();
            dbMaintainer.updateDatabase(dryRun);
        } catch (DbMaintainException ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
            logger.warning("Unsuccessfull scripts will been removed now!\n"
                    + "# Stop Application\n"
                    + "# Fix failed scripts or database errors\n"
                    + "# Start Application again");
            return false;
        } finally {
            DbStatusLogger dbStatusLogger = new DbStatusLogger();
            dbStatusLogger.log(executedScriptInfoSource.getExecutedScripts());
            if(executedScriptInfoSource instanceof DatabaseStatusInfoSource){
                dbStatusLogger.log( ((DatabaseStatusInfoSource) executedScriptInfoSource).getIvalidObjectsInfo());
            }
        }
        return true;
    }
}
