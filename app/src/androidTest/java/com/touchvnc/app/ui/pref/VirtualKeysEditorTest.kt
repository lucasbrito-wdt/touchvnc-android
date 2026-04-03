/*
 * Copyright (c) 2024  Gaurav Ujjwal.
 *
 * SPDX-License-Identifier:  GPL-3.0-or-later
 *
 * See COPYING.txt for more details.
 */

package com.touchvnc.app.ui.pref

import androidx.core.content.edit
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.touchvnc.app.R
import com.touchvnc.app.checkIsDisplayed
import com.touchvnc.app.checkWillBeDisplayed
import com.touchvnc.app.doClick
import com.touchvnc.app.targetConfigContext
import com.touchvnc.app.targetPrefs
import com.touchvnc.app.ui.prefs.PrefsActivity
import org.junit.Assert
import org.junit.Rule
import org.junit.Test

class VirtualKeysEditorTest {
    @Rule
    @JvmField
    val activityRule = ActivityScenarioRule(PrefsActivity::class.java)

    private fun openEditor() {
        onView(withText(R.string.pref_input)).doClick()
        onView(withId(androidx.preference.R.id.recycler_view)).perform(
                RecyclerViewActions.actionOnItem<RecyclerView.ViewHolder>(
                        hasDescendant(withText(R.string.pref_customize_virtual_keys)), ViewActions.click()
                )
        )
        onView(withText(R.string.pref_customize_virtual_keys)).checkIsDisplayed()
        onView(withText(R.string.title_save)).checkIsDisplayed()
        onView(withText(R.string.title_cancel)).checkIsDisplayed()
    }

    @Test // User should be able to restore default config
    fun restoreDefaultConfig() {
        targetPrefs.edit { putString("vk_keys_layout", "Up,Down,Left,Right") }
        openEditor()
        openActionBarOverflowOrOptionsMenu(targetConfigContext)

        onView(withText(R.string.title_load_defaults)).doClick()
        onView(withText(R.string.title_save)).doClick()
        onView(withText(R.string.msg_saved)).checkWillBeDisplayed()

        // Saving default will clear the pref
        Assert.assertNull(targetPrefs.getString("vk_keys_layout", null))
    }
}