/*******************************************************************************
 * Copyright © 2009-present Jean-François Lamy
 *
 * Licensed under the Non-Profit Open Software License version 3.0  ("NPOSL-3.0")
 * License text at https://opensource.org/licenses/NPOSL-3.0
 *******************************************************************************/
package app.owlcms.tests;

import static org.junit.Assert.assertEquals;

import java.io.InputStream;
import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import app.owlcms.data.records.FederationStructureReader;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

public class FederationStructureReaderTest {

    @BeforeClass
    public static void setupTests() {
    }

    @AfterClass
    public static void tearDownTests() {
    }

    final Logger logger = (Logger) LoggerFactory.getLogger(FederationStructureReaderTest.class);

    public FederationStructureReaderTest() {
        logger.setLevel(Level.TRACE);
    }

    @Test
    public void test() throws Exception {

        String streamURI = "/testData/records/Federations.xlsx";

        try (InputStream xmlInputStream = this.getClass().getResourceAsStream(streamURI)) {
            Workbook wb = null;
            try {
                wb = WorkbookFactory.create(xmlInputStream);
                Map<String, Set<String>> membership = new FederationStructureReader().buildStructure(wb, streamURI);

                // base case
                assertEquals(membership.get("IWF"), null);

                // continental = first induction step
                logger.info("{} {}", "PAWF", membership.get("PAWF"));
                assertEquals(membership.get("PAWF").toString(), "[PAWF, IWF]");

                // country directly under continental
                logger.info("{} {}", "VEN", membership.get("VEN"));
                assertEquals(membership.get("VEN").toString(), "[VEN, PAWF, IWF]");

                // state/provincial federation under country, multiple iwf-child federations
                logger.info("{} {}", "BC", membership.get("BC"));
                assertEquals(membership.get("BC").toString(), "[BC, CAN, CWF, IWF, PAWF]");

            } finally {
                if (wb != null) {
                    wb.close();
                }
            }
        }

    }

}
