/*
 * Copyright 2009-2012 Michael Tamm
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.googlecode.fightinglayoutbugs;

import org.junit.Test;

import javax.annotation.Nonnull;

import static com.googlecode.fightinglayoutbugs.ScreenshotCache.Condition.*;
import static org.junit.Assert.fail;

public class ScreenshotCacheTest {

    private int largeScreenshotSize;

    /**
     * Test for <a href="http://code.google.com/p/fighting-layout-bugs/issues/detail?id=7">issue 7</a>.
     */
    @Test
    public void testThatCacheHoldsWeakReferences() {
        assertThatTwoLargeScreenshotsDoNotFitIntoMemory();
        ScreenshotCache cache = new ScreenshotCache(null) {
            @Override
            void colorAllText(@Nonnull String color) {}

            @Override
            void restoreTextColors() {}

            @Override
            protected Screenshot takeScreenshot() {
                return newLargeScreenshot();
            }
        };
        cache.getScreenshot(UNMODIFIED);
        cache.getScreenshot(WITH_ALL_TEXT_BLACK);
        cache.getScreenshot(WITH_ALL_TEXT_WHITE);
        cache.getScreenshot(WITH_ALL_TEXT_TRANSPARENT);
    }

    private void assertThatTwoLargeScreenshotsDoNotFitIntoMemory() {
        Screenshot screenshot1 = newLargeScreenshot();
        try {
            Screenshot screenshot2 = newLargeScreenshot();
            fail("OutOfMemoryError expected.");
        } catch (OutOfMemoryError expected) {}
    }

    private Screenshot newLargeScreenshot() {
        if (largeScreenshotSize == 0) {
            largeScreenshotSize = determineLargeScreenshotSize();
        }
        int[][] pixels = new int[1][];
        pixels[0] = new int[largeScreenshotSize];
        return new Screenshot(pixels);
    }

    private int determineLargeScreenshotSize() {
        int n = 1000000;
        for (;;) {
            int[] a1 = new int[n];
            try {
                int[] a2 = new int[n];
            } catch (OutOfMemoryError e) {
                return n;
            }
            n = (n * 4) / 3;
        }
    }
}
