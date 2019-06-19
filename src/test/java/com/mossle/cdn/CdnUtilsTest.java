package com.mossle.cdn;

import java.io.File;
import java.io.IOException;
import java.net.*;
import java.util.Date;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({"javax.net.ssl.*"})
public class CdnUtilsTest {

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void testCopyUrlToFileThrowsUnknownHostException1()
            throws Exception {
        thrown.expect(UnknownHostException.class);
        CdnUtils.copyUrlToFile(
                "mossle.store/cdn", "https://www.foo.bar/", "public");
        // Method is not expected to return due to exception thrown
    }

    @Test
    public void testCopyUrlToFileThrowsUnknownHostException2()
            throws Exception {
        thrown.expect(UnknownHostException.class);
        CdnUtils.copyUrlToFile(
                "mossle.store/cdn", "https://www.foo.bar/", "public", null);
        // Method is not expected to return due to exception thrown
    }

    @Test
    public void testCopyUrlToFileThrowsIllegalStateException()
            throws Exception {
        thrown.expect(IllegalStateException.class);
        CdnUtils.copyUrlToFile("foo", "https://www.foo.bar/", "baz", "../");
        // Method is not expected to return due to exception thrown
    }

    @PrepareForTest({CdnUtils.class})
    @Test
    public void testCopyMultipartFileToFile() throws Exception {
        PowerMockito.mockStatic(UUID.class);
        Date date = new Date(1560600794000L);
        PowerMockito.whenNew(Date.class).withNoArguments().thenReturn(date);
        UUID uuid = PowerMockito.mock(UUID.class);
        PowerMockito.when(uuid.randomUUID().toString()).thenReturn("baz");

        MultipartFile mFile = new MockMultipartFile("foo", new byte[0]);
        Assert.assertEquals("/2019/06/15/baz",
                CdnUtils.copyMultipartFileToFile(
                        "foo", mFile, "bar", null));
    }

    @Test
    public void testCopyMultipartFileToFileIllegalStateException()
            throws Exception {
        MultipartFile mFile = new MockMultipartFile("foo", new byte[0]);

        thrown.expect(IllegalStateException.class);
        CdnUtils.copyMultipartFileToFile("foo", mFile, "bar", "../");
        // Method is not expected to return due to exception thrown
    }

    @PrepareForTest({CdnUtils.class})
    @Test
    public void testGenerateTargetFileName() throws Exception {
        PowerMockito.mockStatic(UUID.class);
        Date date = new Date(1560600794000L);
        PowerMockito.whenNew(Date.class).withNoArguments().thenReturn(date);
        UUID uUID = PowerMockito.mock(UUID.class);
        PowerMockito.when(uUID.randomUUID().toString()).thenReturn("baz");

        Assert.assertEquals("/2019/06/15/baz",
                CdnUtils.generateTargetFileName("foo"));
    }

    @Test
    public void testFindTargetFile() throws IOException {
        File file = folder.newFile("foo.txt");

        Assert.assertEquals(file,
                CdnUtils.findTargetFile(file.getPath(), "", ""));
        Assert.assertEquals(new File("foo/bar"),
                CdnUtils.findTargetFile(
                        "foo", "bar", file.getName()).getParentFile());
    }

    @Test
    public void testFindSuffix() {
        Assert.assertEquals("", CdnUtils.findSuffix("1a 2b 3c"));
        Assert.assertEquals(".bar/",
                CdnUtils.findSuffix("https://www.foo.bar/?baz"));
    }
}
