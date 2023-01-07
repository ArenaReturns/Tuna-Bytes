## Angler
Angler is a framework to test mixins implementations.

It contains two projects :
 1) _fishing-ground_: Contains all the tests and test's data (target classes for the mixin)
 2) _mixins-tank_: Contains the mixins implementations

### Run
Run the complete test suite with the maven plugin jude:

```
> cd angler/fishingground
> mvn test-compile test:jute -f pom.xml
```
Or use the provided intellij configuration.

Run individual tests with intellij as you would run normal JUnit tests.

### Usage
Tests are based on JUnit framework but each of them is started on its own process ensure a 
clean classpath; thus preventing any classpath side effect from previous runs.

The tested mixins are compiled and processed on the fly on test execution.

You can load one or more mixin(s) by providing File(s) object pointing to its **source(s)** (.java !):
```java
ClassLoader mixinCl = MixinCompiler.compileAndLoad(<File>)
//or
ClassLoader mixinCl = MixinCompiler.compileAndLoad(<List<Files>>)
```
Or simply searching your mixin(s) by name in the tank :
```java
// For these methods to work, the mixin needs to be defined in the 'mixins-tank' module. 
// You are free to use the package path of your choice for your mixin.

ClassLoader mixinCl = MixinCompiler.compileAndLoad(MixinCompiler.getFilesFromMixinsTank("MyMixin.java"))
//or
ClassLoader mixinCl = MixinCompiler.compileAndLoad(MixinCompiler.getFilesFromMixinsTank("MyMixin.java", "MyMixin2.java"))
// Loading multiple mixins can be useful if you want to assert that the ordering of your mixins will not cause problems
```

This call will give you a ``ClassLoader`` which can load your mixin; you can now give it to the mixin launcher to apply your mixin(s) :
```java
Utils.launchMixins(mixinCl);
```

If you reach this state without error, your mixins will be applied !
You can now use assertions to validate the behaviour of your implementation or
validate the bytecode generated.



### Best practices:

The following method is provided to assert the number of files generated and compiled after the mixin appliance :

```java
Utils.assertNumberOfClassesMixified(<Number>)
```
It is recommended in most cases to assert it to 1 after each launch:

```java
// [...] methods were statically imported for readability
launchMixins(compileAndLoad(getFilesFromMixinsTank("MyMixin.java")); //we load one mixin
assertNumberOfClassesMixified(1); //we ensure that only one class was edited
// [...]
```
<br><br>

If you want the test to be atomic (**and you most likely want it !**), 
you must add the following lines to your tests classes to clean the framework between each run :
```java
@Before
public void cleanup() {
    Utils.cleanup();
}
```

<br><br>
It is recommended to implement a test, alongside your other test on a mixing, **without** applying the mixin to assert the change of behaviour of your target class:
```java
// [...] class definition, cleanup...

// Here the mixin change the 'State.success' variable to true when
//  the method 'noopMethod' of the 'SimplePOJO' class in invoked.

// We check in this test that the wanted behaviour is not observed
//  since we didn't load the mixin...
@Test
public void isolatedTest_noMixin() {
    Assert.assertFalse(State.success);
    new SimplePOJO().noopMethod();
    Assert.assertFalse(State.success);
}
        
// Then we load the mixin and assert the change of behaviour
@Test
public void isolatedTest() throws IOException {
    URLClassLoader cl = compileAndLoad(getFilesFromMixinsTank("TestMixin.java"));
    launchMixins(cl);
    assertNumberOfClassesMixified(1);
    Assert.assertFalse(State.success);
    new SimplePOJO().noopMethod();
    Assert.assertTrue(State.success);
}
```
**Keep in mind that since each test is run in its own process no mixins are kept loaded from the previous tests !**

### TroubleShooting
If your tests are failing you may want to look at the directory ``fishingground/tmp/`` which contains the compiled mixins 
(if the compilation succeeded !) under the ``CLASS_OUTPUT/`` directory; and your mixinified classes under the ``MIXINIFIED_OUTPUT/`` (if the mixins were applied successfully !).
These directories are removed by the cleanup method, remove the cleanup call if you want to inspect them.