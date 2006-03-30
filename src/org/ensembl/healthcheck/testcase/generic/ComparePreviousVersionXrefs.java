/*
 * Copyright (C) 2003 EBI, GRL
 * 
 * This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation,
 * Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.ensembl.healthcheck.testcase.generic;

import java.util.Map;

import org.ensembl.healthcheck.DatabaseRegistryEntry;

/**
 * Compare the xrefs in the current database with those from the equivalent
 * database on the secondary server.
 */

public class ComparePreviousVersionXrefs extends ComparePreviousVersionBase {

	/**
	 * Create a new XrefTypes testcase.
	 */
	public ComparePreviousVersionXrefs() {

		addToGroup("release");
		addToGroup("core_xrefs");
		setDescription("Compare the xrefs in the current database with those from the equivalent database on the secondary server");

	}

	// ----------------------------------------------------------------------

	protected Map getCounts(DatabaseRegistryEntry dbre) {

		String sql = "SELECT DISTINCT(e.db_name) AS db_name, COUNT(*) AS count" + " FROM external_db e, xref x, object_xref ox"
				+ " WHERE e.external_db_id=x.external_db_id AND x.xref_id=ox.xref_id " + getExcludeProjectedSQL(dbre)
				+ " GROUP BY e.db_name";

		return getCountsBySQL(dbre, sql);

	} // ------------------------------------------------------------------------

	protected String description() {

		return "xrefs";

	}

	// ------------------------------------------------------------------------

	protected double threshold() {

		return 0.78;

	}

	// ----------------------------------------------------------------------

	private String getExcludeProjectedSQL(DatabaseRegistryEntry dbre) {

		return Integer.parseInt(dbre.getSchemaVersion()) <= 37 ? " AND x.display_label NOT LIKE '%[from%'" : " AND x.info_type IS NULL"; // should really be != 'PROJECTION'

	}

	// ----------------------------------------------------------------------

} // ComparePreviousVersionXrefs

