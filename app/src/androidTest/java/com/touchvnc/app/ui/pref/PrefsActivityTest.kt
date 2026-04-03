/*
 * Copyright (c) 2022  Gaurav Ujjwal.
 *
 * SPDX-License-Identifier:  GPL-3.0-or-later
 *
 * See COPYING.txt for more details.
 */

package com.touchvnc.app.ui.pref

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.touchvnc.app.R.string.*
import com.touchvnc.app.checkIsDisplayed
import com.touchvnc.app.checkWillBeDisplayed
import com.touchvnc.app.doClick
import com.touchvnc.app.targetContext
import com.touchvnc.app.ui.prefs.PrefsActivity
import com.touchvnc.app.util.KnownHostsTest.Companion.getTestCert
import com.touchvnc.app.util.isCertificateTrusted
import com.touchvnc.app.util.trustCertificate
import org.junit.Assert
import org.junit.Rule
import org.junit.Test

/**
 * Simple tests to make sure [PrefsActivity] is not completely broken.
 */
class PrefsActivityTest {

    @Rule
    @JvmField
    val prefActivity = ActivityScenarioRule(PrefsActivity::class.java)

    @Test
    fun uiTest() {
        openSection(pref_ui, pref_ui_summary, pref_theme)
    }

    @Test
    fun viewerTest() {
        openSection(pref_viewer, pref_viewer_summary, pref_fullscreen)
    }

    @Test
    fun inputTest() {
        openSection(pref_input, pref_input_summary, pref_gesture_style)
    }

    @Test
    fun serverTest() {
        openSection(pref_servers, pref_server_summary, pref_discovery)
    }

    @Test
    fun toolsTest() {
        openSection(pref_tools, pref_tools_summary, pref_import_export)
    }

    @Test
    fun forgetKnownHosts() {
        trustCertificate(targetContext, getTestCert())
        Assert.assertTrue(isCertificateTrusted(targetContext, getTestCert()))

        openSection(pref_servers, pref_server_summary, pref_forget_known_hosts)
        onView(withText(pref_forget_known_hosts)).doClick()
        onView(withText(pref_forget_known_hosts_question)).checkWillBeDisplayed()
        onView(withText(title_forget)).doClick()
        onView(withText(msg_done)).checkWillBeDisplayed()

        Assert.assertFalse(isCertificateTrusted(targetContext, getTestCert()))
    }

    /**
     * Validates section can be opened.
     */
    private fun openSection(sectionNameId: Int, sectionSummaryId: Int, childPrefNameId: Int) {
        onView(withText(sectionNameId)).checkIsDisplayed()
        onView(withText(sectionSummaryId)).checkIsDisplayed()

        onView(withText(sectionNameId)).doClick()

        onView(withText(childPrefNameId)).checkWillBeDisplayed()
        onView(withText(sectionNameId)).checkIsDisplayed()
        onView(withText(sectionSummaryId)).check(doesNotExist())
    }
}