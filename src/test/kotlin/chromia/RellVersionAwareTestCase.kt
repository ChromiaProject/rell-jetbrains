package net.postchain.rellide.jetbrains.chromia

import com.intellij.testFramework.fixtures.BasePlatformTestCase

/**
 * Base for suites that resolve Rell versions. The light fixture reuses one project across test
 * classes, so [RellVersionResolver]'s path-keyed cache outlives a suite while different suites
 * reuse fixture directory names (`newest/chromia.yml` declares one version in [RellLspRoutingTest]
 * and another in [RellVersionEditorNotificationProviderTest]).
 */
abstract class RellVersionAwareTestCase : BasePlatformTestCase() {
    override fun setUp() {
        super.setUp()
        RellVersionResolver.getInstance(project).dropCaches()
    }
}
