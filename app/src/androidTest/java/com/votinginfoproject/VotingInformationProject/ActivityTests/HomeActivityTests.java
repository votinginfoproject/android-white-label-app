package com.votinginfoproject.VotingInformationProject.ActivityTests;


import android.app.Activity;
import android.app.Instrumentation;
import android.content.res.Resources;
import android.support.v4.app.FragmentManager;
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.test.ViewAsserts;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.votinginfoproject.VotingInformationProject.R;
import com.votinginfoproject.VotingInformationProject.activities.homeActivity.HomeActivity;
import com.votinginfoproject.VotingInformationProject.activities.VIPTabBarActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class HomeActivityTests extends ActivityInstrumentationTestCase2<HomeActivity> {

    HomeActivity homeActivity;
    FragmentManager fragmentManager;
    HomeFragment homeFragment;

    public HomeActivityTests() {
        super(HomeActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        // enable interaction with UI elements under AboutVIPActivity
        setActivityInitialTouchMode(true);

        refreshActivity();

        homeFragment.setAddress("");
    }

    @Override
    protected void tearDown() throws Exception {
        homeActivity.finish();
    }

    public void testHomeActivityExists() {
        assertNotNull(homeActivity);
    }

    public void testHomeFragmentExists() {
        assertNotNull(fragmentManager);
        assertNotNull(homeFragment);
    }

    public void testHomeActivityLoadsResponse() {

        Resources resources = homeActivity.getResources();
        Instrumentation instrumentation = getInstrumentation();

        // read AboutVIPActivity result from file
        InputStream is = resources.openRawResource(R.raw.test_response);
        BufferedReader ir = new BufferedReader(new InputStreamReader(is));
        StringBuilder builder = new StringBuilder();
        try {
            String line;
            while ((line = ir.readLine()) != null) {
                builder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
            assertFalse("IOException reading AboutVIPActivity file", true);
        } catch (Exception ex) {
            ex.printStackTrace();
            assertFalse("Failed to read AboutVIPActivity file", true);
        } finally {
            try {
                ir.close();
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            ir = null;
            is = null;
        }

        String responseStr = builder.toString();
        builder = null;

        // save AboutVIPActivity result to shared preferences
        final String testAddress = "123 Main St, Richmond, VA";

        homeFragment.setAddress(testAddress);

        responseStr = null;

        final EditText homeEditTextAddress = (EditText)homeActivity.findViewById(R.id.home_edit_text_address);

        instrumentation.runOnMainSync(new Runnable() {
            @Override
            public void run() {
                homeEditTextAddress.requestFocus();
            }
        });

        instrumentation.waitForIdleSync();
        instrumentation.sendStringSync(testAddress);
        instrumentation.waitForIdleSync();

        instrumentation.runOnMainSync(new Runnable() {
            @Override
            public void run() {
                // use AboutVIPActivity election
                homeFragment.doTestRun();
                // load result from shared preferences
                homeFragment.queryWithNewAddress(testAddress);
            }
        });


        instrumentation.waitForIdleSync();
        final TextView statusView = (TextView)homeActivity.findViewById(R.id.home_label_status);

        // wait for status message to show up
        instrumentation.runOnMainSync(new Runnable() {
            @Override
            public void run() {
                int counter = 0;
                while ((statusView.getVisibility() != View.VISIBLE) && counter < 20) {
                    try {
                        Thread.sleep(1000);
                        counter += 1;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        instrumentation.waitForIdleSync();

        // wait for 'GO' button to show up
        final Button go = (Button)homeActivity.findViewById(R.id.home_button_go);
        instrumentation.runOnMainSync(new Runnable() {
            @Override
            public void run() {
                int counter = 0;
                while ((go.getVisibility() != View.VISIBLE) && counter < 20) {
                    try {
                        Thread.sleep(2000);
                        counter += 1;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        instrumentation.waitForIdleSync();

        //should be seeing 'GO' button now
        assertEquals(View.VISIBLE, go.getVisibility());

        // 'GO' button should be on screen
        ViewAsserts.assertOnScreen(homeActivity.getWindow().getDecorView(), go);

        // click 'GO'
        instrumentation.runOnMainSync(new Runnable() {
            @Override
            public void run() {
                go.requestFocus();
            }
        });

        instrumentation.waitForIdleSync();
        Instrumentation.ActivityMonitor monitor = instrumentation.addMonitor(VIPTabBarActivity.class.getName(), null, false);
        TouchUtils.clickView(this, go);

        // wait up to 8 seconds for the tab bar activity to start before timing out
        Activity startedActivity = monitor.waitForActivityWithTimeout(8000);
        assertNotNull("Tab bar activity is null", startedActivity);
        assertEquals("Monitor for tab bar activity has not been called", 1, monitor.getHits());
        assertEquals("Tab bar activity is of wrong type", VIPTabBarActivity.class, startedActivity.getClass());

        instrumentation.removeMonitor(monitor);
        startedActivity.finish(); // close tab bar activity
    }

    public void refreshActivity() {
        homeActivity = getActivity();
        fragmentManager = homeActivity.getSupportFragmentManager();
        homeFragment = (HomeFragment)fragmentManager.findFragmentById(R.id.home_fragment);
    }
}
