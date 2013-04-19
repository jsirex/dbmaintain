package org.dbmaintain.script.executedscriptinfo.impl;

import org.dbmaintain.database.Database;
import org.dbmaintain.database.SQLHandler;
import org.dbmaintain.script.ScriptFactory;

import java.text.DateFormat;

/**
 * Created with IntelliJ IDEA.
 * User: Andrei_Akatsyeu
 * Date: 4/18/13
 * Time: 11:55 AM
 * To change this template use File | Settings | File Templates.
 */
public class OracleDatabaseStatusInfoSource extends AbstractDatabaseStatusInfoSource {

    public static final String QUERY_INVALID_OBJECTS = "select owner, object_name, object_type from all_objects"
            + " where status = 'INVALID'"
            + " order by owner, object_type, object_name";

    public OracleDatabaseStatusInfoSource(boolean autoCreateExecutedScriptsTable,
                                          String executedScriptsTableName,
                                          String fileNameColumnName,
                                          int fileNameColumnSize,
                                          String fileLastModifiedAtColumnName,
                                          String checksumColumnName,
                                          int checksumColumnSize,
                                          String executedAtColumnName,
                                          int executedAtColumnSize,
                                          String succeededColumnName,
                                          DateFormat timestampFormat,
                                          Database defaultSupport,
                                          SQLHandler sqlHandler,
                                          ScriptFactory scriptFactory) {
        super(autoCreateExecutedScriptsTable,
                executedScriptsTableName,
                fileNameColumnName,
                fileNameColumnSize,
                fileLastModifiedAtColumnName,
                checksumColumnName,
                checksumColumnSize,
                executedAtColumnName,
                executedAtColumnSize,
                succeededColumnName,
                timestampFormat,
                defaultSupport,
                sqlHandler,
                scriptFactory);
    }

    @Override
    public String getQueryString() {
        return QUERY_INVALID_OBJECTS;
    }
}
