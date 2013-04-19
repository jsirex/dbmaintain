package org.dbmaintain.script.executedscriptinfo;

import org.dbmaintain.script.InvalidObject;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Andrei_Akatsyeu
 * Date: 4/18/13
 * Time: 11:14 AM
 * To change this template use File | Settings | File Templates.
 */
public interface DatabaseStatusInfoSource extends  ExecutedScriptInfoSource {

    /**
     * Get Information about invalid objects in database
     *
     * @return list of invalid objects
     */
    List<InvalidObject> getIvalidObjectsInfo();

}
