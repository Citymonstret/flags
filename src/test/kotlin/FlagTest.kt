import cloud.commandframework.flags.AbstractFlag
import cloud.commandframework.flags.FlagContainer
import cloud.commandframework.flags.GlobalFlagContainer
import cloud.commandframework.flags.types.IntegerFlag
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class FlagTest {

    @BeforeEach
    fun setupGlobalFlagContainer() {
        GlobalFlagContainer += TestFlag("default")
    }

    @Test
    fun `test get flag from string`() {
        assert(GlobalFlagContainer.flagClassFromString("test") != null)
    }

    @Test
    fun `test get flag from class`() {
        GlobalFlagContainer[TestFlag::class]
    }

    @Test
    fun `test get flag value`() {
        assert(GlobalFlagContainer[TestFlag::class]?.value == "default")
    }

    @Test
    fun `test get parent container`() {
        val container = FlagContainer(GlobalFlagContainer)
        assert(container.parentContainer === GlobalFlagContainer)
    }

    @Test
    fun `test get highest parent`() {
        val container = FlagContainer(GlobalFlagContainer)
        assert(container.highestFlagContainer === GlobalFlagContainer)
    }

    @Test
    fun `test get unknown flag throwing exception`() {
        val container = FlagContainer(GlobalFlagContainer)
        try {
            container[AnotherFlag::class]
        } catch (exception: IllegalStateException) {
            return
        }
        throw AssertionError("Flag is unknown and should have thrown...")
    }

    @Test
    fun `test flag extending standard flag type and then test removal`() {
        GlobalFlagContainer += NumericalFlag(10)
        val container = FlagContainer(GlobalFlagContainer)
        container += GlobalFlagContainer[NumericalFlag::class].parse("420")
        assert(container[NumericalFlag::class].value == 420)
        container -= container[NumericalFlag::class]
        assert(container[NumericalFlag::class].value == 10)
    }
}

class TestFlag(value: String) : AbstractFlag<String, TestFlag>(value) {

    override fun parse(input: String) = flagOf(input)

    override fun merge(input: String) = flagOf(this.value + input)

    override fun example() = ""

    override fun flagOf(value: String) = TestFlag(value)
}

class AnotherFlag(value: String) : AbstractFlag<String, TestFlag>(value) {

    override fun parse(input: String) = flagOf(input)

    override fun merge(input: String) = flagOf(this.value + input)

    override fun example() = ""

    override fun flagOf(value: String) = TestFlag(value)
}

class NumericalFlag(value: Int) : IntegerFlag<NumericalFlag>(value) {

    override fun flagOf(value: Int) = NumericalFlag(value)

}
