scala -cp ./target/test-classes;./target/classes;%M2_REPO%/junit/junit/4.4./junit-4.4.jar;%M2_REPO%/org/scalacheck/scalacheck/1.1.1/scalacheck-1.1.1.jar;%M2_REPO%/org/scala-lang/scala-library/2.6.1/scala-library-2.6.1.jar;%M2_REPO%/cglib/cglib/2.1_3/cglib-2.1_3.jar;%M2_REPO%/org/hamcrest/hamcrest-all/1.0/hamcrest-all-1.0.jar;%M2_REPO%/org/jmock/jmock/2.4.0/jmock-2.4.0.jar;%M2_REPO%/org/objenesis/objenesis/1.0/objenesis-1.0.jar;%M2_REPO%/org/scalatest/scalatest/0.9.1/scalatest-0.9.1.jar;%M2_REPO%/asm/asm/1.5.3/asm-1.5.3.jar -e "(new %1).reportSpecs"