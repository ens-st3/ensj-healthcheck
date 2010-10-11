/*
 Copyright (C) 2004 EBI, GRL
 
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.
 
 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.
 
 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package org.ensembl.healthcheck;

import org.ensembl.healthcheck.DatabaseRegistryEntry.DatabaseInfo;

/**
 * A single line of a report. Each ReportLine stores the names of the test case
 * and database (as Strings) a message, and a level. See the constants defined
 * by this class for the different levels. Levels are represented as ints to
 * allow easy comparison and setting of thresholds. EG: For improved report
 * tracing, include DatabaseInfo and Species in ReportLine
 */
public class ReportLine {

	/** The output level of this report */
	protected int level;

	/** The test case that this report refers to */
	protected String testCaseName;

	/** The database name that this report refers to */
	protected String databaseName;
	protected String speciesName;
	protected DatabaseType type;

	/** The message that this report contains */
	protected String message;

	/** The team responsible for this report */
	protected String teamResponsible;

	/** Output level that is higher than all the others */
	public static final int NONE = 2000;

	/** Output level representing a problem with a test */
	public static final int PROBLEM = 1000;

	/** Output level representing a test that has passed */
	public static final int WARNING = 750;

	/**
	 * Output level representing something that should be included in the test
	 * summary
	 */
	public static final int INFO = 500;

	/** Output level representing something that is for information only */
	public static final int CORRECT = 100;

	/** Output level that is lower than all others */
	public static final int ALL = 0;

	/**
	 * Creates a new instance of ReportLine
	 * 
	 * @param testCaseName
	 *            The test case to refer to.
	 * @param databaseName
	 *            The database name involved.
	 * @param level
	 *            The level of this report.
	 * @param message
	 *            The message to report.
	 */
	public ReportLine(String testCaseName, String name, int level,
			String message, String teamResponsible) {

		this.testCaseName = testCaseName;
		DatabaseInfo info = DatabaseRegistryEntry.getInfoFromName(name);
		this.databaseName = info.getName();
		if (info.getSpecies() != Species.UNKNOWN) {
			speciesName = info.getSpecies().toString();
		} else {
			speciesName = info.getAlias();
		}
		this.type = info.getType();
		this.level = level;
		this.message = message;
		this.teamResponsible = teamResponsible;

	} // constructor

	// -------------------------------------------------------------------------
	/**
	 * Get the level of this ReportLine.
	 * 
	 * @return level The level.
	 */
	public int getLevel() {

		return level;

	}

	/**
	 * Set the level of this report.
	 * 
	 * @param l
	 *            The new level.
	 */
	public void setLevel(int l) {

		level = l;

	}

	/**
	 * Get the report message.
	 * 
	 * @return The report message.
	 */
	public String getMessage() {

		return message;

	}

	/**
	 * Set the report message.
	 * 
	 * @param s
	 *            The new message.
	 */
	public void setMessage(String s) {

		message = s;

	}

	/**
	 * Get the name of the test case that this report line is associated with.
	 * 
	 * @return The name of the test case.
	 */
	public String getTestCaseName() {

		return testCaseName;

	}

	/**
	 * Set the name of the test case that this report is associated with.
	 * 
	 * @param s
	 *            The new name.
	 */
	public void setTestCaseName(String s) {

		testCaseName = s;

	}

	/**
	 * Get the name of the database that this report line is associated with.
	 * 
	 * @return The database name
	 */
	public String getDatabaseName() {

		return databaseName;

	}

	/**
	 * Set the name of the database that this report line is associated with.
	 * 
	 * @param s
	 *            The new name.
	 */
	public void setDatabaseName(String s) {

		databaseName = s;

	}

	// -------------------------------------------------------------------------
	/**
	 * Get the short test name (without the package name) of the test associated
	 * with this report.
	 * 
	 * @return The short test name.
	 */
	public String getShortTestCaseName() {

		return testCaseName.substring(testCaseName.lastIndexOf(".") + 1);

	} // setShortTestCaseName

	// -------------------------------------------------------------------------
	/**
	 * Get the level of this report as a String ("PROBLEM", "CORRECT" etc).
	 * 
	 * @return The level as a string.
	 */
	public String getLevelAsString() {

		String result = "";

		switch (level) {
		case CORRECT:
			result = "CORRECT";
			break;
		case INFO:
			result = "INFO";
			break;
		case WARNING:
			result = "WARNING";
			break;
		case PROBLEM:
			result = "PROBLEM";
			break;
		case NONE:
			result = "NONE";
			break;
		default:
			System.err.println("Can't get text equivalent for report level "
					+ level);
			break;
		}

		return result;

	} // getLevelAsString

	public String getTeamResponsible() {
		return teamResponsible;
	}

	public void setTeamResponsible(String teamResponsible) {
		this.teamResponsible = teamResponsible;
	}

	public String getSpeciesName() {
		return speciesName;
	}

	public void setSpecies(String species) {
		this.speciesName = species;
	}

	public DatabaseType getType() {
		return type;
	}

	public void setType(DatabaseType type) {
		this.type = type;
	}

	// -------------------------------------------------------------------------

} // ReportLine
