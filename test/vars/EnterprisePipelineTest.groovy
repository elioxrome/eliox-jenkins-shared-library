import com.lesfurets.jenkins.unit.BasePipelineTest
import org.junit.Before
import org.junit.Test

import static org.junit.Assert.assertTrue

class EnterprisePipelineTest extends BasePipelineTest {

  private Object script
  private boolean failCheckout

  @Before
  void setUp() {
    super.setUp()
    failCheckout = false
    binding.setVariable('scm', [:])

    helper.registerAllowedMethod('node', [String, Closure], { String label, Closure c -> c.call() })
    helper.registerAllowedMethod('timestamps', [Closure], { Closure c -> c.call() })
    helper.registerAllowedMethod('ansiColor', [String, Closure], { String color, Closure c -> c.call() })
    helper.registerAllowedMethod('stage', [String, Closure], { String stageName, Closure c -> c.call() })
    helper.registerAllowedMethod('checkout', [Object], { Object scmConfig ->
      if (failCheckout) {
        throw new IllegalStateException(
          "'checkout scm' is only available when using " +
            '"Multibranch Pipeline" or "Pipeline script from SCM"'
        )
      }
    })
    helper.registerAllowedMethod('git', [Map], { Map args -> })
    helper.registerAllowedMethod('echo', [String], { String message -> })
    helper.registerAllowedMethod('sh', [String], { String command -> })
    helper.registerAllowedMethod('withCredentials', [List, Closure], { List bindings, Closure c -> c.call() })

    script = loadScript('vars/enterprisePipeline.groovy')
  }

  @Test
  void runsDefaultStages() {
    script.call([appName: 'sample-service'])

    assertTrue(helper.callStack.find { call ->
      call.methodName == 'stage' && call.argsToString().contains('Build')
    } != null)
  }

  @Test
  void skipsCheckoutWhenScmContextIsUnavailable() {
    failCheckout = true

    script.call([appName: 'sample-service'])

    assertTrue(helper.callStack.find { call ->
      call.methodName == 'stage' && call.argsToString().contains('Build')
    } != null)
  }

  @Test
  void usesGitCheckoutWhenRepoUrlIsProvided() {
    script.call([
      appName: 'sample-service',
      repoUrl: 'https://example.com/repo.git',
      repoBranch: 'develop'
    ])

    assertTrue(helper.callStack.find { call ->
      call.methodName == 'git'
    } != null)
  }

  @Test
  void wrapsDeployWithKubeconfigCredentialsWhenDeployEnabled() {
    script.call([
      appName: 'sample-service',
      deploy: true
    ])

    assertTrue(helper.callStack.find { call ->
      call.methodName == 'withCredentials'
    } != null)
  }
}
