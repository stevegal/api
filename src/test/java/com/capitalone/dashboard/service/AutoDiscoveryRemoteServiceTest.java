package com.capitalone.dashboard.service;

import com.capitalone.dashboard.config.ApiTestConfig;
import com.capitalone.dashboard.config.FongoConfig;
import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.AutoDiscoveredEntry;
import com.capitalone.dashboard.model.AutoDiscovery;
import com.capitalone.dashboard.model.AutoDiscoveryMetaData;
import com.capitalone.dashboard.model.AutoDiscoveryStatusType;
import com.capitalone.dashboard.model.AutoDiscoveryRemoteRequest;
import com.capitalone.dashboard.repository.AutoDiscoveryRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ApiTestConfig.class, FongoConfig.class})
@DirtiesContext
public class AutoDiscoveryRemoteServiceTest {

    private static AutoDiscoveryRemoteRequest ad0, ad1, ad2;
    private static AutoDiscoveryMetaData adMeta0, adMeta1, adMeta2;
    private static List<AutoDiscoveredEntry> codeRepoEntries = null;
    private static List<AutoDiscoveredEntry> buildEntries = null;
    private static List<AutoDiscoveredEntry> securityScanEntries = null;
    private static List<AutoDiscoveredEntry> deploymentEntries = null;
    private static List<AutoDiscoveredEntry> libraryScanEntries = null;
    private static List<AutoDiscoveredEntry> functionalTestEntries = null;
    private static List<AutoDiscoveredEntry> artifactEntries = null;
    private static List<AutoDiscoveredEntry> staticCodeEntries = null;
    private static List<AutoDiscoveredEntry> featureEntries = null;

    @Autowired
    private AutoDiscoveryService autoSvc;

    @Autowired
    private AutoDiscoveryRepository autoRepo;

    @Before
    public void setUp() {

        codeRepoEntries = new ArrayList<>();
        buildEntries = new ArrayList<>();
        securityScanEntries = new ArrayList<>();
        deploymentEntries = new ArrayList<>();
        libraryScanEntries = new ArrayList<>();
        functionalTestEntries = new ArrayList<>();
        artifactEntries = new ArrayList<>();
        staticCodeEntries = new ArrayList<>();
        featureEntries = new ArrayList<>();

        adMeta0 = new AutoDiscoveryMetaData();
        adMeta0.setApplicationName("DummyApp");
        adMeta0.setBusinessApplication("BizApp11222333");
        adMeta0.setBusinessService("BizApp334440");
        adMeta0.setTemplate("template");
        adMeta0.setTitle("testAUTO_DISCOVERY");
        adMeta0.setType("Product");

        ad0 = new AutoDiscoveryRemoteRequest(adMeta0, codeRepoEntries, buildEntries, securityScanEntries, deploymentEntries,
                libraryScanEntries, functionalTestEntries, artifactEntries, staticCodeEntries, featureEntries, "17458071acd72450923475bb");

        AutoDiscoveredEntry codeRepoEntry = new AutoDiscoveredEntry();
        codeRepoEntry.setDescription("Hygieia GitHub");
        codeRepoEntry.setToolName("GitHub");
        codeRepoEntry.setStatus(AutoDiscoveryStatusType.USER_REJECTED);
        codeRepoEntry.getOptions().put("branch", "master");
        codeRepoEntry.getOptions().put("url", "https://github.com/Hygieia");
        codeRepoEntries.add(codeRepoEntry);

        adMeta1 = new AutoDiscoveryMetaData();
        adMeta1.setApplicationName("HygieiaApp");
        adMeta1.setBusinessApplication("BizApp12345678");
        adMeta1.setBusinessService("BizApp123456");
        adMeta1.setTemplate("template");
        adMeta1.setTitle("testAUTO_DISCOVERY");
        adMeta1.setType("Team");

        ad1 = new AutoDiscoveryRemoteRequest(adMeta1, codeRepoEntries, buildEntries, securityScanEntries, deploymentEntries,
                libraryScanEntries, functionalTestEntries, artifactEntries, staticCodeEntries, featureEntries, "5d67f7b5066a8b0fe6cbfb61");

        AutoDiscoveredEntry artifactEntry = new AutoDiscoveredEntry();
        artifactEntry.setDescription("Hygieia Artifactory");
        artifactEntry.setToolName("Artifactory");
        artifactEntry.setStatus(AutoDiscoveryStatusType.USER_REJECTED);
        artifactEntry.getOptions().put("path", "some/path");
        artifactEntry.getOptions().put("artifactName", "HygieiaArtifactory");
        artifactEntry.getOptions().put("instanceUrl", "http://www.artifact.com");
        artifactEntries.add(artifactEntry);

        adMeta2 = new AutoDiscoveryMetaData();
        adMeta2.setApplicationName("ApplicationNameStr");
        adMeta2.setBusinessApplication("BusinessApplicationStr");
        adMeta2.setBusinessService("BusinessServiceStr");
        adMeta2.setTemplate("template");
        adMeta2.setTitle("testTitle");
        adMeta2.setType("Team");

        ad2 = new AutoDiscoveryRemoteRequest(adMeta2, codeRepoEntries, buildEntries, securityScanEntries, deploymentEntries,
                libraryScanEntries, functionalTestEntries, artifactEntries, staticCodeEntries, featureEntries, "5d67f7b5066a8b0fe6cbfb99");
    }

    @After
    public void tearDown() {
        codeRepoEntries = null;
        buildEntries = null;
        securityScanEntries = null;
        deploymentEntries = null;
        libraryScanEntries = null;
        functionalTestEntries = null;
        artifactEntries = null;
        staticCodeEntries = null;
        featureEntries = null;
        autoRepo.deleteAll();
    }

    @Test
    public void testCreateNewAutoDiscovery() throws HygieiaException {
        autoSvc.save(ad0);
        autoSvc.save(ad1);
        autoSvc.save(ad2);
        assertTrue("Failed to create new AutoDiscovery records", autoRepo.findAll().iterator().hasNext());
        assertEquals(autoRepo.count(), 3);
    }

    @Test
    public void testUpdateAutoDiscovery() throws HygieiaException {
        autoSvc.save(ad0);
        assertEquals(autoRepo.count(), 1);

        AutoDiscovery ad = autoRepo.findAll().iterator().next();
        ad0.setAutoDiscoveryId(ad.getId().toHexString());
        ad0.getMetaData().setTitle("new title");  // this should not persist
        ad0.getMetaData().setTemplate("new template");  // this should not persist
        autoSvc.save(ad0);

        assertEquals(autoRepo.count(), 1);
        ad = autoRepo.findAll().iterator().next();
        // we should not update anything else other than the status for the AutoDiscoveredEntry
        assertEquals(ad.getMetaData().getTitle(), "testAUTO_DISCOVERY");
        assertEquals(ad.getMetaData().getTemplate(), "template");
    }

    @Test
    public void testUpdateStatusOnly() throws HygieiaException {
        autoSvc.save(ad2);
        assertEquals(autoRepo.count(), 1);

        AutoDiscovery ad = autoRepo.findAll().iterator().next();
        ObjectId id2 = ad.getId();

        autoSvc.save(ad1);
        assertEquals(autoRepo.count(), 2);
        ad = autoRepo.findOne(id2);
        assertNotNull(ad);
        assertNotNull(ad.getCodeRepoEntries());
        assertEquals(ad.getCodeRepoEntries().size(),1);
        assertFalse(ad.getArtifactEntries().isEmpty());

        // now try to update ad2
        ad2.setAutoDiscoveryId(id2.toHexString());
        ad2.setCodeRepoEntries(new ArrayList<>());
        AutoDiscoveredEntry artifactEntry = ad2.getArtifactEntries().iterator().next();
        artifactEntry.setStatus(AutoDiscoveryStatusType.AWAITING_USER_RESPONSE); // this should persist
        artifactEntry.setDescription("new artifactory descriptions blah blah"); // this should not persist
        autoSvc.save(ad2);
        assertEquals(autoRepo.count(), 2);
        ad = autoRepo.findOne(id2);
        assertFalse(ad.getCodeRepoEntries().isEmpty());
        assertFalse(ad.getArtifactEntries().isEmpty());
        assertNotNull(ad.getModifiedTimestamp());

        artifactEntry = ad.getArtifactEntries().iterator().next();
        assertEquals(artifactEntry.getStatus(), AutoDiscoveryStatusType.AWAITING_USER_RESPONSE);
        // we should not update anything else other than the status for the AutoDiscoveredEntry, so description should not change
        assertEquals(artifactEntry.getDescription(), "Hygieia Artifactory");

        AutoDiscoveredEntry codeRepoEntry = ad.getCodeRepoEntries().iterator().next();
        assertEquals(codeRepoEntry.getStatus(), AutoDiscoveryStatusType.USER_REJECTED);
        assertEquals(codeRepoEntry.getDescription(), "Hygieia GitHub");
    }

    @Test
    public void testException_badObjId() {
        ad0.setAutoDiscoveryId("this is a bad object id");
        try {
            autoSvc.save(ad0);
            fail("Expected an HygieiaException to be thrown");
        } catch (HygieiaException hex) {
            assertEquals(hex.getMessage(), "Invalid Auto Discovery Object ID: [this is a bad object id] received.");
        }
    }

}
