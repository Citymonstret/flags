import cloud.commandframework.flags.AbstractFlag
import cloud.commandframework.flags.FlagContainer
import cloud.commandframework.flags.GlobalFlagContainer
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
        assert(GlobalFlagContainer[TestFlag::class] != null)
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
