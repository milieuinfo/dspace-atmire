package org.dspace.app.itemexport;

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

/**
 * Created by: Antoine Snyers (antoine at atmire dot com)
 * Date: 08 Jul 2016
 */
public class SubdirTest {

    @Test
    public void getSubDir_EventLength() throws Exception {
        String handle = "123456/987654";
        String expected = "98" + File.separator + "76" + File.separator + "54";

        Subdir subdir = new Subdir();
        String result = subdir.getSubDir(handle);
        assertEquals(expected, result);
    }

    @Test
    public void getSubDir_OddLength() throws Exception {
        String handle = "123456/21212";
        String expected = "21" + File.separator + "21" + File.separator + "2";

        Subdir subdir = new Subdir();
        String result = subdir.getSubDir(handle);
        assertEquals(expected, result);
    }

    @Test
    public void getSubDir_Null() throws Exception {
        String handle = null;
        String expected = "00";

        Subdir subdir = new Subdir();
        String result = subdir.getSubDir(handle);
        assertEquals(expected, result);
    }

}