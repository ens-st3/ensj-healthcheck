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

package org.ensembl.healthcheck.testcase.generic;

import java.sql.Connection;

import org.ensembl.healthcheck.DatabaseRegistryEntry;
import org.ensembl.healthcheck.ReportManager;
import org.ensembl.healthcheck.testcase.SingleDatabaseTestCase;

/**
 * Check if protein_coding genes have a canonical transcript that has a valid translation. See also canonical_transcript checks in
 * CoreForeignKeys.
 */
public class CanonicalTranscriptCoding extends SingleDatabaseTestCase {

	/**
	 * Create a new instance of CanonicalTranscriptCoding.
	 */
	public CanonicalTranscriptCoding() {

		addToGroup("release");
		addToGroup("post_genebuild");
		setDescription("Check if protein_coding genes have a canonical transcript that has a valid translation. Also check than number of canonical transcripts is correct. See also canonical_transcript checks in CoreForeignKeys.");
		setTeamResponsible("compara");

	}

	/**
	 * Run the test.
	 * 
	 * @param dbre
	 *          The database to use.
	 * @return true if the test passed.
	 * 
	 */
	public boolean run(DatabaseRegistryEntry dbre) {

		boolean result = true;

		Connection con = dbre.getConnection();

		// --------------------------------
		// A gene that has at least one transcript.biotype='protein_coding' should have gene.biotype='protein_coding'
		int rows = getRowCount(con, "SELECT COUNT(*) FROM gene g WHERE g.gene_id IN (SELECT tr.gene_id FROM transcript tr WHERE tr.biotype='protein_coding') AND g.biotype!='protein_coding'");

		if (rows > 0) {

			result = false;
			ReportManager.problem(this, con, rows + " genes with at least one protein_coding transcript do not have biotype protein_coding");

		} else {

			ReportManager.correct(this, con, "All genes with protein_coding transcripts have protein_coding biotype");

		}

		// --------------------------------
		// Protein_coding transcripts should all have translations
		rows = getRowCount(con, "SELECT COUNT(*) FROM transcript tr LEFT JOIN translation tl ON tl.transcript_id=tr.transcript_id WHERE tr.biotype='protein_coding' AND tl.transcript_id is NULL");

		if (rows > 0) {

			result = false;
			ReportManager.problem(this, con, rows + " protein_coding transcripts do not have translations");

		} else {

			ReportManager.correct(this, con, "All protein_coding transcripts have translations");
		}

		// --------------------------------
		// All genes should have a canonical transcript
		rows = getRowCount(con, "SELECT COUNT(*) FROM gene g WHERE g.canonical_transcript_id is NULL");

		if (rows > 0) {

			result = false;
			ReportManager.problem(this, con, rows + " genes do not have a canonical transcript");

		} else {

			ReportManager.correct(this, con, "All genes have a canonical transcript");

		}

		// --------------------------------
		// All canonical transcripts with a translation should belong to a gene with a biotype of 'protein_coding',
		// 'IG_C_gene','IG_D_gene','IG_J_gene' or'IG_V_gene'
		rows = getRowCount(
				con,
				"SELECT COUNT(*) FROM gene g WHERE g.canonical_transcript_id IN (SELECT tr.transcript_id FROM transcript tr, translation tl WHERE tr.transcript_id=tl.transcript_id) AND g.biotype NOT IN ('rRNA','retrotransposed','protein_coding','IG_C_gene','IG_D_gene','IG_J_gene','IG_V_gene')");

		if (rows > 0) {

			result = false;
			ReportManager.problem(this, con, rows + " genes with canonical transcripts have the wrong biotype");

		} else {

			ReportManager.correct(this, con, "All genes with canonical transcripts have the correct biotype");

		}

		// --------------------------------
		// None of the transcripts that have a translation and have a biotype different to
		// ('protein_coding','IG_C_gene','IG_D_gene','IG_J_gene','IG_V_gene')) should be canonical transcripts to any gene.
		rows = getRowCount(
				con,
				"SELECT COUNT(*) FROM gene g WHERE g.canonical_transcript_id IN (select tr.transcript_id FROM transcript tr, translation tl WHERE tr.transcript_id=tl.transcript_id AND tr.biotype NOT IN ('rRNA','retrotransposed','protein_coding','IG_C_gene','IG_D_gene','IG_J_gene','IG_V_gene'))");

		if (rows > 0) {

			result = false;
			ReportManager.problem(this, con, rows + " genes have canonical transripts with mismatched biotypes");

		} else {

			ReportManager.correct(this, con, "All genes have canonical transcripts with matching biotypes");

		}

		// --------------------------------
		// A gene that has gene.biotype='protein_coding' and has at least one transcript.biotype='protein_coding' should have a
		// canonical transcript.biotype='protein_coding'.
		rows = getRowCount(con, "SELECT count(*) FROM gene g JOIN transcript t USING (gene_id) WHERE g.gene_id IN (SELECT g.gene_id FROM gene g JOIN transcript t ON (g.canonical_transcript_id = t.transcript_id) WHERE g.biotype = 'protein_coding' AND t.biotype != 'protein_coding') AND t.biotype = 'protein_coding'");

		if (rows > 0) {

			result = false;
			ReportManager.problem(this, con, rows + " genes with at least one protein_coding transcript do not have a protein_coding canonical transcript");

		} else {

			ReportManager.correct(this, con, "All genes with at least one protein_coding transcript have a protein_coding canonical transcript");

		}
		
	// --------------------------------
		// If a gene is gene.biotype='protein_coding' but has no transcripts that are transcript.biotype='protein_coding', at least one of the transcripts has to have a translation.
		rows = getRowCount(con, "SELECT count(*) FROM gene g JOIN transcript t USING (gene_id) JOIN translation p ON (t.canonical_translation_id = p.translation_id) WHERE g.biotype = 'protein_coding' AND g.gene_id NOT IN (SELECT gene_id FROM transcript WHERE biotype = 'protein_coding')");

		if (rows > 0) {

			result = false;
			ReportManager.problem(this, con, rows + " protein_coding genes with no protein_coding transcripts have no transcripts with translations");

		} else {

			ReportManager.correct(this, con, "All protein_coding genes with no protein_coding transcripts have at least one transcripts which translates");

		}

		// --------------------------------
		// check if protein_coding genes have a canonical transcript that has a valid translation
		rows = getRowCount(con, "SELECT COUNT(*) FROM gene g LEFT JOIN translation tr ON g.canonical_transcript_id=tr.transcript_id WHERE g.biotype='protein_coding' AND tr.transcript_id IS NULL");

		if (rows > 0) {

			result = false;
			ReportManager.problem(this, con, rows + " protein_coding genes have canonical transcripts that do not have valid translations");

		} else {

			ReportManager.correct(this, con, "All protein_coding genes have canonical_transcripts that translate");
		}

		// --------------------------------
		// check that the number of canonical translations is correct
		int numCanonical = getRowCount(con, "SELECT COUNT(*) FROM transcript t1, translation p, transcript t2 WHERE t1.canonical_translation_id = p.translation_id AND p.transcript_id = t2.transcript_id");

		int numTotal = getRowCount(con, "SELECT COUNT(*) FROM translation p, transcript t WHERE t.transcript_id = p.transcript_id");

		if (numCanonical != numTotal) {

			result = false;
			ReportManager.problem(this, con, "Number of canonical translations (" + numCanonical + ") is different from the total number of translations (" + numTotal + ")");

		} else {

			ReportManager.correct(this, con, "Number of canonical translations is correct.");
		}

		return result;

	} // run

} // CanonicalTranscriptCoding
