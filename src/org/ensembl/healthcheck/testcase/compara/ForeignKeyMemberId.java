/*
 * Copyright [1999-2015] Wellcome Trust Sanger Institute and the EMBL-European Bioinformatics Institute
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.ensembl.healthcheck.testcase.compara;

import java.sql.Connection;

import org.ensembl.healthcheck.DatabaseRegistryEntry;
import org.ensembl.healthcheck.ReportManager;
import org.ensembl.healthcheck.Team;
import org.ensembl.healthcheck.testcase.SingleDatabaseTestCase;

/**
 * An EnsEMBL Healthcheck test case that looks for broken foreign-key
 * relationships.
 */

public class ForeignKeyMemberId extends SingleDatabaseTestCase {

    /**
     * Create an ForeignKeyMemberId that applies to a specific set of databases.
     */
    public ForeignKeyMemberId() {

        addToGroup("compara_homology");
        setDescription("Check for broken foreign-key relationships in ensembl_compara databases.");
        setTeamResponsible(Team.COMPARA);

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

        if (tableHasRows(con, "seq_member")) {

            result &= checkForOrphans(con, "family_member", "seq_member_id", "seq_member", "seq_member_id");
            result &= checkForOrphans(con, "homology_member", "seq_member_id", "seq_member", "seq_member_id");
            result &= checkForOrphans(con, "gene_align_member", "seq_member_id", "seq_member", "seq_member_id");
            result &= checkOptionalRelation(con, "gene_tree_node", "seq_member_id", "seq_member", "seq_member_id");
            result &= checkForOrphans(con, "other_member_sequence", "seq_member_id", "seq_member", "seq_member_id");

        } else {
            ReportManager.correct(this, con, "NO ENTRIES in seq_member table, so nothing to test IGNORED");
        }

        if (tableHasRows(con, "gene_member")) {
            result &= checkForOrphans(con, "homology_member", "gene_member_id", "gene_member", "gene_member_id");
            result &= checkForOrphans(con, "member_xref", "gene_member_id", "gene_member", "gene_member_id");
            result &= checkOptionalRelation(con, "seq_member", "gene_member_id", "gene_member", "gene_member_id");
        } else {
            ReportManager.correct(this, con, "NO ENTRIES in gene_member table, so nothing to test IGNORED");
        }


        return result;

    }

} // ForeignKeyMemberId
