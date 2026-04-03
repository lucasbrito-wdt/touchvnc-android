/*
 * Copyright (c) 2026  Gaurav Ujjwal.
 *
 * SPDX-License-Identifier:  GPL-3.0-or-later
 *
 * See COPYING.txt for more details.
 */

package com.gaurav.avnc.vnc

data class TouchSlot(
    val id: Int,       // 0-9 finger slot
    val type: Int,     // 0=down, 1=move, 2=up
    val x: Int,        // normalized 0-65535
    val y: Int,        // normalized 0-65535
    val pressure: Int  // 0-65535
)
