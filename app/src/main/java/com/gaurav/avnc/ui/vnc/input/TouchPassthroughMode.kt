/*
 * Copyright (c) 2026  Gaurav Ujjwal.
 *
 * SPDX-License-Identifier:  GPL-3.0-or-later
 *
 * See COPYING.txt for more details.
 */

package com.gaurav.avnc.ui.vnc.input

import android.graphics.PointF
import android.view.MotionEvent
import com.gaurav.avnc.viewmodel.VncViewModel
import com.gaurav.avnc.vnc.TouchSlot

/**
 * Receives raw [MotionEvent]s and converts them to [TouchSlot] lists,
 * transforming viewport coordinates to normalized 0-65535 range relative
 * to the remote framebuffer.
 */
class TouchPassthroughMode(private val viewModel: VncViewModel) {

    /**
     * Processes a touch event and sends corresponding touch slots to the server.
     * Returns true if the event was handled.
     */
    fun onTouchEvent(event: MotionEvent): Boolean {
        val fbWidth = viewModel.frameState.fbWidth
        val fbHeight = viewModel.frameState.fbHeight
        if (fbWidth < 1 || fbHeight < 1)
            return false

        val actionIndex = event.actionIndex
        val action = event.actionMasked
        val slots = mutableListOf<TouchSlot>()

        when (action) {
            MotionEvent.ACTION_DOWN,
            MotionEvent.ACTION_POINTER_DOWN -> {
                // Only the actionIndex finger changes state on POINTER_DOWN
                for (i in 0 until event.pointerCount) {
                    val type = if (i == actionIndex) TYPE_DOWN else TYPE_MOVE
                    addSlot(slots, event, i, type, fbWidth, fbHeight)
                }
            }

            MotionEvent.ACTION_MOVE -> {
                for (i in 0 until event.pointerCount) {
                    addSlot(slots, event, i, TYPE_MOVE, fbWidth, fbHeight)
                }
            }

            MotionEvent.ACTION_UP,
            MotionEvent.ACTION_POINTER_UP -> {
                // Only the actionIndex finger changes state on POINTER_UP
                for (i in 0 until event.pointerCount) {
                    val type = if (i == actionIndex) TYPE_UP else TYPE_MOVE
                    addSlot(slots, event, i, type, fbWidth, fbHeight)
                }
            }

            MotionEvent.ACTION_CANCEL -> {
                // All fingers up
                for (i in 0 until event.pointerCount) {
                    addSlot(slots, event, i, TYPE_UP, fbWidth, fbHeight)
                }
            }

            else -> return false
        }

        if (slots.isNotEmpty()) {
            viewModel.messenger?.sendTouchEvent(slots)
        }
        return true
    }

    private fun addSlot(
        slots: MutableList<TouchSlot>,
        event: MotionEvent,
        pointerIndex: Int,
        type: Int,
        fbWidth: Float,
        fbHeight: Float
    ) {
        val pointerId = event.getPointerId(pointerIndex)
        if (pointerId > 9) return // Clamp slot IDs to 0-9

        val vpPoint = PointF(event.getX(pointerIndex), event.getY(pointerIndex))
        val fbPoint = viewModel.frameState.toFb(vpPoint) ?: return

        val normalizedX = ((fbPoint.x / fbWidth) * 65535).toInt().coerceIn(0, 65535)
        val normalizedY = ((fbPoint.y / fbHeight) * 65535).toInt().coerceIn(0, 65535)
        val pressure = (event.getPressure(pointerIndex) * 65535).toInt().coerceIn(0, 65535)

        slots.add(TouchSlot(pointerId, type, normalizedX, normalizedY, pressure))
    }

    companion object {
        private const val TYPE_DOWN = 0
        private const val TYPE_MOVE = 1
        private const val TYPE_UP = 2
    }
}
