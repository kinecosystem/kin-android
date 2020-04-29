package org.kin.stellarfork

import org.junit.Assert
import org.junit.Test
import org.kin.stellarfork.Price.Companion.fromString

class PriceTest {
    @Test
    fun testFromDouble() {
        val tests = arrayOf(
            PriceTestCase("0", Price(0, 1)),
            PriceTestCase("0.1", Price(1, 10)),
            PriceTestCase("0.01", Price(1, 100)),
            PriceTestCase("0.001", Price(1, 1000)),
            PriceTestCase("543.01793", Price(54301793, 100000)),
            PriceTestCase("319.69983", Price(31969983, 100000)),
            PriceTestCase("0.93", Price(93, 100)),
            PriceTestCase("0.5", Price(1, 2)),
            PriceTestCase("1.730", Price(173, 100)),
            PriceTestCase("0.85334384", Price(5333399, 6250000)),
            PriceTestCase("5.5", Price(11, 2)),
            PriceTestCase("2.72783", Price(272783, 100000)),
            PriceTestCase("638082.0", Price(638082, 1)),
            PriceTestCase("2.93850088", Price(36731261, 12500000)),
            PriceTestCase("58.04", Price(1451, 25)),
            PriceTestCase("41.265", Price(8253, 200)),
            PriceTestCase("5.1476", Price(12869, 2500)),
            PriceTestCase("95.14", Price(4757, 50)),
            PriceTestCase("0.74580", Price(3729, 5000)),
            PriceTestCase("4119.0", Price(4119, 1)),
            PriceTestCase("1073742464.5", Price(1073742464, 1)),
            PriceTestCase("1635962526.2", Price(1635962526, 1)),
            PriceTestCase("2147483647", Price(2147483647, 1))
        )
        for (test in tests) {
            val price = fromString(test.input)
            Assert.assertEquals(
                test.expectedPrice.numerator.toString() + "/" + test.expectedPrice.denominator,
                price.numerator.toString() + "/" + price.denominator
            )
        }
    }

    private inner class PriceTestCase(
        var input: String,
        var expectedPrice: Price
    )
}
