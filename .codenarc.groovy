ruleset {
  description 'Enterprise baseline rules for Jenkins Shared Library'

  ruleset('rulesets/basic.xml')
  ruleset('rulesets/braces.xml')
  ruleset('rulesets/concurrency.xml')
  ruleset('rulesets/convention.xml') {
    CompileStatic(enabled: false)
  }
  ruleset('rulesets/design.xml')
  ruleset('rulesets/dry.xml')
  ruleset('rulesets/exceptions.xml')
  ruleset('rulesets/formatting.xml')
  ruleset('rulesets/imports.xml')
  ruleset('rulesets/junit.xml')
  ruleset('rulesets/naming.xml') {
    FactoryMethodName(enabled: false)
  }
  ruleset('rulesets/security.xml')
  ruleset('rulesets/unnecessary.xml')
  ruleset('rulesets/unused.xml')
}
