package com.cam2cook

import org.junit.Assert.assertEquals
import org.junit.Test

class DeadlineFormattingTest {
    @Test fun labelsUseReviewStateForPastEstimate() {
        assertEquals("Review date", deadlineLabel(-1))
    }

    @Test fun labelsUseTodayForDeadlineNow() {
        assertEquals("Today", deadlineLabel(0))
    }

    @Test fun labelsFormatFutureEstimate() {
        assertEquals("1 day left", deadlineLabel(1))
        assertEquals("4 days", deadlineLabel(4))
    }
}
