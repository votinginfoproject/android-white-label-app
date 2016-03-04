package com.votinginfoproject.VotingInformationProject;

import android.app.Application;
import android.test.ApplicationTestCase;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }
}

/**
 * Android Test Classes use JUnit3 code style
 *
 * Extend AndroidTestCase if:
 * you need access to Resources or other things that depend on Activity context
 *
 * Extend junit.framework.TestCase if:
 * You do not need any android classes.
 *
 */